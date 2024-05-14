package gov.nasa.pds.api.registry.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.lexer.SearchBaseListener;
import gov.nasa.pds.api.registry.lexer.SearchParser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.index.query.RangeQueryBuilder;
import org.opensearch.index.query.SimpleQueryStringBuilder;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.query.QueryBuilder;

public class Antlr4SearchListener extends SearchBaseListener {
  enum conjunctions {
    AND, OR
  };

  enum operation {
    eq, ge, gt, le, lt, ne
  };

  private static final Logger log = LoggerFactory.getLogger(Antlr4SearchListener.class);

  private BoolQuery.Builder queryBuilder = new BoolQuery.Builder();

  private conjunctions conjunction = conjunctions.AND;
  final private Deque<conjunctions> stack_conjunction = new ArrayDeque<conjunctions>();
  final private Deque<BoolQuery> stack_queries = new ArrayDeque<BoolQuery>();
  final private Deque<List<Query>> stack_musts = new ArrayDeque<List<Query>>();
  final private Deque<List<Query>> stack_nots = new ArrayDeque<List<Query>>();
  final private Deque<List<Query>> stack_shoulds = new ArrayDeque<List<Query>>();

  int depth = 0;
  private List<Query> musts = new ArrayList<Query>();
  private List<Query> nots = new ArrayList<Query>();
  private List<Query> shoulds = new ArrayList<Query>();
  private operation operator = null;

  public Antlr4SearchListener() {
    super();
  }

  @Override
  public void exitQuery(SearchParser.QueryContext ctx) {
    for (Query qb : musts)
      this.queryBuilder.must(qb);
    for (Query qb : nots)
      this.queryBuilder.mustNot(qb);
    for (Query qb : shoulds)
      this.queryBuilder.filter(qb);
  }

  @Override
  public void enterGroup(SearchParser.GroupContext ctx) {
    /*
     * this.stack_conjunction.push(this.conjunction); this.stack_musts.push(this.musts);
     * this.stack_nots.push(this.nots); this.stack_queries.push(this.queryBuilder);
     * this.stack_shoulds.push(this.shoulds); this.conjunction = conjunctions.AND; this.musts = new
     * ArrayList<QueryBuilder>(); this.nots = new ArrayList<QueryBuilder>(); this.shoulds = new
     * ArrayList<QueryBuilder>();
     * 
     * if (0 < this.depth) { this.queryBuilder = new BoolQueryBuilder(); }
     * 
     * this.depth++;
     */
  }

  @Override
  public void exitGroup(SearchParser.GroupContext ctx) {
    /*
     * BoolQueryBuilder group = this.queryBuilder; List<QueryBuilder> musts = this.musts;
     * List<QueryBuilder> nots = this.nots; List<QueryBuilder> shoulds = this.shoulds;
     * 
     * this.conjunction = this.stack_conjunction.pop(); this.depth--; this.musts =
     * this.stack_musts.pop(); this.nots = this.stack_nots.pop(); this.queryBuilder =
     * this.stack_queries.pop(); this.shoulds = this.stack_shoulds.pop();
     * 
     * for (QueryBuilder qb : musts) group.must(qb); for (QueryBuilder qb : nots) group.mustNot(qb);
     * for (QueryBuilder qb : shoulds) group.filter(qb);
     * 
     * if (0 < depth) { if (ctx.NOT() != null) this.nots.add(group); else if (this.conjunction ==
     * conjunctions.AND) this.musts.add(group); else this.shoulds.add(group); } else if (ctx.NOT()
     * != null) { this.queryBuilder = new BoolQueryBuilder(); this.nots.add(group); }
     */
  }

  @Override
  public void enterAndStatement(SearchParser.AndStatementContext ctx) {
    this.conjunction = conjunctions.AND;
  }

  @Override
  public void enterOrStatement(SearchParser.OrStatementContext ctx) {
    this.conjunction = conjunctions.OR;
  }

  @Override
  public void enterComparison(SearchParser.ComparisonContext ctx) {}

  @Override
  public void exitComparison(SearchParser.ComparisonContext ctx) {
    final String left = SearchUtil.jsonPropertyToOpenProperty(ctx.FIELD().getSymbol().getText());

    String right;
    Query comparatorQuery = null;


    if (ctx.NUMBER() != null) {
      right = ctx.NUMBER().getSymbol().getText();
    } else if (ctx.STRINGVAL() != null) {
      right = ctx.STRINGVAL().getSymbol().getText();
      right = right.substring(1, right.length() - 1);
    } else {
      throw new ParseCancellationException(
          "A right component (literal) of a comparison is neither a number or a string. Number and String are the only types supported for literals.");
    }

    if (this.operator == operation.eq || this.operator == operation.ne) {


      FieldValue fieldValue = new FieldValue.Builder().stringValue(right).build();

      MatchQuery matchQueryBuilder = new MatchQuery.Builder().field(left).query(fieldValue).build();

      comparatorQuery = matchQueryBuilder.toQuery();


    } else {
      RangeQuery.Builder rangeQueryBuilder = new RangeQuery.Builder();

      rangeQueryBuilder = rangeQueryBuilder.field(left);

      if (this.operator == operation.ge)
        rangeQueryBuilder.gte(JsonData.of(right));
      else if (this.operator == operation.gt)
        rangeQueryBuilder.gt(JsonData.of(right));
      else if (this.operator == operation.le)
        rangeQueryBuilder.lte(JsonData.of(right));
      else if (this.operator == operation.lt)
        rangeQueryBuilder.lt(JsonData.of(right));
      else {
        throw new ParseCancellationException("Operator " + this.operator.name()
            + " is not supported. Supported comparison operators are eq, ne, gt, gte, lt, lte.");
      }

      comparatorQuery = rangeQueryBuilder.build().toQuery();

    }


    if (this.operator == operation.ne) {
      this.nots.add(comparatorQuery);
    } else if (this.conjunction == conjunctions.AND) {
      this.musts.add(comparatorQuery);
    } else {
      this.shoulds.add(comparatorQuery);
    }
  }

  @Override
  public void enterLikeComparison(SearchParser.LikeComparisonContext ctx) {}

  @Override
  public void exitLikeComparison(SearchParser.LikeComparisonContext ctx) {
    /*
     * final String left = SearchUtil.jsonPropertyToOpenProperty(ctx.FIELD().getText());
     * 
     * String right = ctx.STRINGVAL().getText(); right = right.substring(1, right.length() - 1);
     * QueryBuilder comparator = new
     * SimpleQueryStringBuilder(right).field(left).fuzzyMaxExpansions(0);
     * 
     * if ("not".equalsIgnoreCase(ctx.getChild(1).getText())) { this.nots.add(comparator); } else if
     * (this.conjunction == conjunctions.AND) { this.musts.add(comparator); } else {
     * this.shoulds.add(comparator); }
     */
  }

  @Override
  public void enterOperator(SearchParser.OperatorContext ctx) {
    if (ctx.EQ() != null)
      this.operator = operation.eq;
    else if (ctx.GE() != null)
      this.operator = operation.ge;
    else if (ctx.GT() != null)
      this.operator = operation.gt;
    else if (ctx.LE() != null)
      this.operator = operation.le;
    else if (ctx.LT() != null)
      this.operator = operation.lt;
    else if (ctx.NE() != null)
      this.operator = operation.ne;
    else {
      log.error("Panic, there are more operators than this versionof the lexer knows about");
      throw new ParseCancellationException(); // PANIC: listener out of sync with the grammar
    }
  }

  public BoolQuery getBoolQuery() {
    return this.queryBuilder.build();
  }
}
