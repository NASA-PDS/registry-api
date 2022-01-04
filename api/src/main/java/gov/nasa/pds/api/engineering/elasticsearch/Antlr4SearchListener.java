package gov.nasa.pds.api.engineering.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import gov.nasa.pds.api.engineering.lexer.SearchBaseListener;
import gov.nasa.pds.api.engineering.lexer.SearchParser;

public class Antlr4SearchListener extends SearchBaseListener {
	
	private static final Logger log = LoggerFactory.getLogger(Antlr4SearchListener.class);
	
	private BoolQueryBuilder queryQB;
	private LinkedList<BoolQueryBuilder> queryTermQBs  = new LinkedList<BoolQueryBuilder>();
	private LinkedList<BoolQueryBuilder> groupQBs = new LinkedList<BoolQueryBuilder>();
	private LinkedList<BoolQueryBuilder> expressionQBs = new LinkedList<BoolQueryBuilder>();
	private LinkedList<BoolQueryBuilder> andStatementQBs = new LinkedList<BoolQueryBuilder>();
	private LinkedList<BoolQueryBuilder> orStatementQBs = new LinkedList<BoolQueryBuilder>();
	private LinkedList<BoolQueryBuilder> comparisonQBs = new LinkedList<BoolQueryBuilder>();
	
	
	private String elasticQuery;
	
	private Boolean comparisonBoolean;
	
	private String rangeOperator;
	private Boolean includeBoundary;
	
	public Antlr4SearchListener(BoolQueryBuilder boolQuery) {
		
		super();
		this.queryQB = boolQuery;
		
	}
	
	
    public Antlr4SearchListener() {
		
		super();
		this.queryQB = QueryBuilders.boolQuery();
		
	}
	
	
	
	
	 @Override
	 public void enterQuery(SearchParser.QueryContext ctx) {
		 Antlr4SearchListener.log.debug("enterQuery: " + ctx.getText()); 
		 this.queryQB = QueryBuilders.boolQuery(); 	
     }
	 
	 @Override
	 public void exitQuery(SearchParser.QueryContext ctx) {
		Antlr4SearchListener.log.debug("exitQuery: " + ctx.getText());	 
		this.queryQB.must(this.queryTermQBs.pollLast());
	 }
	 
	 @Override
	 public void enterQueryTerm(SearchParser.QueryTermContext ctx) {
		 Antlr4SearchListener.log.debug("enter queryterm: " + ctx.getText());
		 this.queryTermQBs.add(QueryBuilders.boolQuery());	 
		  
     }
	 
	 @Override
	 public void exitQueryTerm(SearchParser.QueryTermContext ctx) {
		 Antlr4SearchListener.log.debug("exit queryterm: " + ctx.getText());
		 if (ctx.comparison() != null) {
			 this.queryTermQBs.getLast().must(this.comparisonQBs.pollLast());
		}
		 else if (ctx.group() != null) {
			 this.queryTermQBs.getLast().must(this.groupQBs.pollLast());
		}
     }
	 
	 
	 @Override
	 public void enterGroup(SearchParser.GroupContext ctx) {
		 Antlr4SearchListener.log.debug("enter group: " + ctx.getText());
		 this.groupQBs.add(QueryBuilders.boolQuery());
     }
	 
	 @Override
	 public void exitGroup(SearchParser.GroupContext ctx) {
		 Antlr4SearchListener.log.debug("exit group: " + ctx.getText());
		 if (ctx.NOT() != null) {
			 this.groupQBs.getLast().mustNot(this.expressionQBs.pollLast());
		 }
		 else {
			 this.groupQBs.getLast().must(this.expressionQBs.pollLast());
		 }
	 }
	 
	 @Override
	 public void enterExpression(SearchParser.ExpressionContext ctx) {
		 Antlr4SearchListener.log.debug("enter expression: " + ctx.getText());
		 this.expressionQBs.add(QueryBuilders.boolQuery());
		  
	 }
	 
	 
	 @Override
	 public void exitExpression(SearchParser.ExpressionContext ctx) {
		 Antlr4SearchListener.log.debug("exit expression: " + ctx.getText());
		 if (ctx.queryTerm() != null) {
			 this.expressionQBs.getLast().must(this.queryTermQBs.pollLast());
		 }
		 else if (ctx.andStatement() != null) {
			 this.expressionQBs.getLast().must(this.andStatementQBs.pollLast());
		 }
		 else if (ctx.orStatement() != null) {
			 this.expressionQBs.getLast().must(this.orStatementQBs.pollLast());
		 }
	 }
	 
	 
	 @Override
	 public void enterAndStatement(SearchParser.AndStatementContext ctx) {
		 Antlr4SearchListener.log.debug("enter andStatement: " + ctx.getText());
		 this.andStatementQBs.add(QueryBuilders.boolQuery());
		  
	 }
	 
	 @Override
	 public void enterOrStatement(SearchParser.OrStatementContext ctx) {
		 Antlr4SearchListener.log.debug("enter orStatement: " + ctx.getText());
		 this.orStatementQBs.add(QueryBuilders.boolQuery());
		 	 
	 }
	 
	 @Override
	 public void exitOrStatement(SearchParser.OrStatementContext ctx)  {
		 
		 BoolQueryBuilder currentOrStatementQB = this.orStatementQBs.getLast();
		 
		 for (SearchParser.QueryTermContext QTCtx : ctx.queryTerm()) {
			 currentOrStatementQB.should(this.queryTermQBs.pollLast());
		 }
		 currentOrStatementQB.minimumShouldMatch(1);
	 }
	 
	 
	 @Override
	 public void exitAndStatement(SearchParser.AndStatementContext ctx) {
		 Antlr4SearchListener.log.debug("exit andStatement: " + ctx.getText());
		 
		 for (SearchParser.QueryTermContext QTCtx : ctx.queryTerm()) {
			 this.andStatementQBs.getLast().must(this.queryTermQBs.pollLast());
		 }
		 
	 }

	 @Override
	 public void enterComparison(SearchParser.ComparisonContext ctx) {
	
		this.comparisonQBs.add(QueryBuilders.boolQuery());
		 
		 // termQuery or rangeQuery
		 this.elasticQuery = null;
		
		 // march eq, ne
		 this.comparisonBoolean = true;
		
		 // range (gt, ge, lt, le)
		 this.rangeOperator = null;
		 this.includeBoundary = false;
		 
 	 }
	 
	 
	 
	@Override
	public void exitComparison(SearchParser.ComparisonContext ctx) {
		Antlr4SearchListener.log.debug("exit comparison: " + ctx.getText());

		try {
			
			String comparisonLeftArg = 
					ElasticSearchUtil.jsonPropertyToElasticProperty(ctx.FIELD().getSymbol().getText());
			Object comparisonRightArg = null; 
			
			Method method;
			QueryBuilder comparisonQB = null;
			
			// parse right arg of the comparison
			if (ctx.STRINGVAL() != null) {
				this.log.debug("parse string");
				String rightArg = ctx.STRINGVAL().getSymbol().getText();
				comparisonRightArg = rightArg.substring(1, rightArg.length() - 1);
			
			} else if (ctx.NUMBER() != null){
				this.log.debug("parse number");
				comparisonRightArg = Float.parseFloat(ctx.NUMBER().getSymbol().getText());
					
			}
			
		
			// apply comparison (match or range)
			if (this.elasticQuery == "termQuery") {					
				comparisonQB = (QueryBuilder) QueryBuilders.termQuery(comparisonLeftArg, comparisonRightArg);
			}
			else if (this.elasticQuery == "rangeQuery") {
				comparisonQB = (RangeQueryBuilder) QueryBuilders.rangeQuery(comparisonLeftArg);
				Method methodRangeOperator = RangeQueryBuilder.class.getDeclaredMethod(this.rangeOperator, Object.class, boolean.class);
				comparisonQB = (QueryBuilder) methodRangeOperator.invoke(comparisonQB, 
						comparisonRightArg,
						this.includeBoundary);
			}
			
			// apply boolean operator on comparison (only for NE operator)
			if (comparisonQB != null) {
				if (this.comparisonBoolean) {
					this.comparisonQBs.getLast().must(comparisonQB);
				}
				else {
					this.comparisonQBs.getLast().mustNot(comparisonQB);
				}
			}
			
		
		} catch (SecurityException e) { 
			Antlr4SearchListener.log.error("SecurityException " + e.getMessage());
		} catch (NoSuchMethodException e) {	  
			Antlr4SearchListener.log.error("NoSuchMethodException " + e.getMessage());
		} catch (IllegalArgumentException e) {
			Antlr4SearchListener.log.error("IllegalArgumentException " + e.getMessage());
		} catch (IllegalAccessException e) {
			Antlr4SearchListener.log.error("IllegalAccessException " + e.getMessage());
		} catch (InvocationTargetException e) {
		    Antlr4SearchListener.log.error("InvocationTargetException " + e.getMessage());
		}
		
	}

	@Override
	public void enterOperator(SearchParser.OperatorContext ctx) {
		Antlr4SearchListener.log.debug("enter operator: " + ctx.getText());
		
		if (ctx.EQ() != null) {
				Antlr4SearchListener.log.info("Operator is EQ " + ctx.getText());
				this.elasticQuery = "termQuery";
		}
		else if (ctx.NE() != null)  {
			Antlr4SearchListener.log.info("Operator is NE ");
			this.elasticQuery = "termQuery";
			this.comparisonBoolean = false;
			
		}
		else if (ctx.GT() != null) {
			Antlr4SearchListener.log.info("Operator is GT ");
			this.elasticQuery = "rangeQuery";
			this.rangeOperator = "from";
			this.includeBoundary = false;
		}
		else if (ctx.GE() != null) {
			Antlr4SearchListener.log.info("Operator is GE ");
			this.elasticQuery = "rangeQuery";
			this.rangeOperator = "from";
			this.includeBoundary = true;
		}
		else if (ctx.LT() != null) {
			Antlr4SearchListener.log.info("Operator is LT ");
			this.elasticQuery = "rangeQuery";
			this.rangeOperator = "to";
			this.includeBoundary = false;
		}
		else if (ctx.LE() != null) {
			Antlr4SearchListener.log.info("Operator is LE ");
			this.elasticQuery = "rangeQuery";
			this.rangeOperator = "to";
			this.includeBoundary = true;
		}
		
	}
	 
	 
	 public BoolQueryBuilder getBoolQuery() {
		 return this.queryQB;
	 }
	 
	 
	 
	 

}
