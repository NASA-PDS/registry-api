package gov.nasa.pds.api.engineering.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import gov.nasa.pds.api.engineering.lexer.SearchBaseListener;
import gov.nasa.pds.api.engineering.lexer.SearchParser;

public class Antlr4SearchListener extends SearchBaseListener
{
	enum conjunctions { AND, OR };
	enum operation { eq, ge, gt, le, lt, ne };

	private static final Logger log = LoggerFactory.getLogger(Antlr4SearchListener.class);
	
	private BoolQueryBuilder query = new BoolQueryBuilder();
	private boolean wildcard = false;
	private conjunctions conjunction = conjunctions.AND;
	final private Deque<conjunctions> stack_conjunction = new ArrayDeque<conjunctions>(); 
	final private Deque<BoolQueryBuilder> stack_queries = new ArrayDeque<BoolQueryBuilder>();
	final private Deque<List<QueryBuilder>> stack_musts = new ArrayDeque<List<QueryBuilder>>();
	final private Deque<List<QueryBuilder>> stack_nots = new ArrayDeque<List<QueryBuilder>>();
	final private Deque<List<QueryBuilder>> stack_shoulds = new ArrayDeque<List<QueryBuilder>>(); 
	int depth = 0;
	private List<QueryBuilder> musts = new ArrayList<QueryBuilder>();
	private List<QueryBuilder> nots = new ArrayList<QueryBuilder>();
	private List<QueryBuilder> shoulds = new ArrayList<QueryBuilder>();
	private operation operator = null;
	
    public Antlr4SearchListener()
    {
		super();		
	}

	 @Override
	 public void exitQuery(SearchParser.QueryContext ctx)
	 {
		 for (QueryBuilder qb : musts) this.query.must(qb);
		 for (QueryBuilder qb : nots) this.query.mustNot(qb);
		 for (QueryBuilder qb : shoulds) this.query.should(qb);
	 }
	 
	 @Override
	 public void enterGroup(SearchParser.GroupContext ctx)
	 {
		 this.stack_conjunction.push(this.conjunction);
		 this.stack_musts.push(this.musts);
		 this.stack_nots.push(this.nots);
		 this.stack_queries.push(this.query);
		 this.stack_shoulds.push(this.shoulds);
		 this.conjunction = conjunctions.AND;
		 this.musts = new ArrayList<QueryBuilder>();
		 this.nots = new ArrayList<QueryBuilder>();
		 this.shoulds = new ArrayList<QueryBuilder>();

		 if (0 < this.depth) { this.query = new BoolQueryBuilder(); }
		 
		 this.depth++;
     }
	 
	 @Override
	 public void exitGroup(SearchParser.GroupContext ctx)
	 {
		 BoolQueryBuilder group = this.query;
		 List<QueryBuilder> musts = this.musts;
		 List<QueryBuilder> nots = this.nots;
		 List<QueryBuilder> shoulds = this.shoulds;

		 this.conjunction = this.stack_conjunction.pop();
		 this.depth--;
		 this.musts = this.stack_musts.pop();
		 this.nots = this.stack_nots.pop();
		 this.query = this.stack_queries.pop();
		 this.shoulds = this.stack_shoulds.pop();

		 for (QueryBuilder qb : musts) group.must(qb);
		 for (QueryBuilder qb : nots) group.mustNot(qb);
		 for (QueryBuilder qb : shoulds) group.should(qb);

		 if (0 < depth)
		 {
			 if (ctx.NOT() != null) this.nots.add(group);
			 else if (this.conjunction == conjunctions.AND) this.musts.add(group);
			 else this.shoulds.add(group);
		 }
		 else if (ctx.NOT() != null)
		 {
			 this.query = new BoolQueryBuilder();
			 this.nots.add(group);
		 }
	 }
	 
	 @Override
	 public void enterAndStatement(SearchParser.AndStatementContext ctx)
	 {
		 this.conjunction = conjunctions.AND;
	 }

	 @Override
	 public void enterOrStatement(SearchParser.OrStatementContext ctx)
	 {
		 this.conjunction = conjunctions.OR;
	 }

	 @Override
	 public void enterComparison(SearchParser.ComparisonContext ctx)
	 {
		 this.wildcard = ctx.VALUE() != null && (ctx.VALUE().getSymbol().getText().contains("*") || ctx.VALUE().getSymbol().getText().contains("?"));
 	 }

	@Override
	public void exitComparison(SearchParser.ComparisonContext ctx)
	{
		final String left = ElasticSearchUtil.jsonPropertyToElasticProperty (ctx.FIELD(0).getSymbol().getText());
		String right;
		QueryBuilder comparator = null;

		// the second term of the comparison can be first tokenized as FIELD (string without quote) since FIELD is before VALUE (string with optional wildcard, no quote) in the ANTLR4 grammar.
		if (ctx.FIELD(1) != null) right = ctx.FIELD(1).getSymbol().getText();
		else if (ctx.NUMBER() != null) right = ctx.NUMBER().getSymbol().getText();
		else if (ctx.STRINGVAL() != null)
		{
			right = ctx.STRINGVAL().getSymbol().getText();
			right = right.substring(1, right.length()-1);
		}
		else if (ctx.VALUE() != null) right = ctx.VALUE().getSymbol().getText();
		else
		{
			log.error("Panic, there are more data types than this version of the lexer knows about.");
			throw new ParseCancellationException(); // PANIC: listener out of sync with the grammar
		}

		if (this.operator == operation.eq || this.operator == operation.ne)
		{
			if (this.wildcard) comparator = new WildcardQueryBuilder(left, right);
			else comparator = new MatchQueryBuilder(left, right);
		}
		else
		{
			comparator = new RangeQueryBuilder(left);
			
			if (this.operator == operation.ge) ((RangeQueryBuilder)comparator).gte(right);
			else if (this.operator == operation.gt) ((RangeQueryBuilder)comparator).gt(right);
			else if (this.operator == operation.le) ((RangeQueryBuilder)comparator).lte(right);
			else if (this.operator == operation.lt) ((RangeQueryBuilder)comparator).lt(right);
			else
			{
				log.error("Panic, there are more range operators than this version of the lexer knows about");
				throw new ParseCancellationException(); // PANIC: listener out of sync with the grammar
			}
		}

		if (this.operator == operation.ne) this.nots.add(comparator);
		else if (this.conjunction == conjunctions.AND) this.musts.add(comparator);	
		else this.shoulds.add(comparator);
	}

	@Override
	public void enterOperator(SearchParser.OperatorContext ctx)
	{
		if (this.wildcard && ctx.EQ() == null && ctx.NE() == null)
		{
			log.warn("Cannot use wildcards with <, <=, >=, or >");
			throw new ParseCancellationException();
		}
		
		if (ctx.EQ() != null) this.operator = operation.eq;
		else if (ctx.GE() != null) this.operator = operation.ge;
		else if (ctx.GT() != null) this.operator = operation.gt;
		else if (ctx.LE() != null) this.operator = operation.le;
		else if (ctx.LT() != null) this.operator = operation.lt;
		else if (ctx.NE() != null) this.operator = operation.ne;
		else
		{
			log.error("Panic, there are more operators than this versionof the lexer knows about");
			throw new ParseCancellationException(); // PANIC: listener out of sync with the grammar
		}
	}
	 
	 
	 public BoolQueryBuilder getBoolQuery()
	 {
		 return this.query;
	 }
}
