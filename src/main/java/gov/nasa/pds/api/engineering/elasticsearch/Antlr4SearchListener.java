package gov.nasa.pds.api.engineering.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import gov.nasa.pds.api.engineering.lexer.SearchBaseListener;
import gov.nasa.pds.api.engineering.lexer.SearchParser;

public class Antlr4SearchListener extends SearchBaseListener {
	
	private static final Logger log = LoggerFactory.getLogger(Antlr4SearchListener.class);
	
	private BoolQueryBuilder boolQuery;
	private BoolQueryBuilder comparisonBoolQB;
	
	
	private String elasticQuery;
	
	private String termQueryLeftArgument;
	private String termQueryRightStringArgument = null;
	private float termQueryRightFloatArgument;
	private Boolean comparisonBoolean = true;
	
	private String rangeOperator;
	private Boolean includeBoundary;
	
	public Antlr4SearchListener(BoolQueryBuilder boolQuery) {
		
		super();
		this.boolQuery = boolQuery;
		
	}
	
	
	 @Override
	 public void enterQuery(SearchParser.QueryContext ctx) {
		 Antlr4SearchListener.log.debug("enterQuery: " + ctx.getText());
		 
		 this.boolQuery = QueryBuilders.boolQuery();  	
		 
     }
	 
	 @Override
	 public void enterQueryTerm(SearchParser.QueryTermContext ctx) {
		 Antlr4SearchListener.log.debug("enter queryterm: " + ctx.getText());
     }
	 
	 @Override
	 public void exitQueryTerm(SearchParser.QueryTermContext ctx) {
		 Antlr4SearchListener.log.debug("exit query term: " + ctx.getText());
		 if (this.comparisonBoolQB != null) {
			 this.boolQuery.must(this.comparisonBoolQB);
		 }
     }
	 
	 
	 @Override
	 public void enterGroup(SearchParser.GroupContext ctx) {
		 Antlr4SearchListener.log.debug("enter group: " + ctx.getText());
     }

	 
	
	 
	@Override
	public void exitComparison(SearchParser.ComparisonContext ctx) {
		Antlr4SearchListener.log.debug("exit comparison: " + ctx.getText());

		try {
			
			String termQueryLeftArgument = ctx.FIELD().getSymbol().getText();
			
			Method method;
			QueryBuilder comparisonQB = null;
			
			if (ctx.STRINGVAL() != null) {
				this.log.debug("parse string");
				String rightArg = ctx.STRINGVAL().getSymbol().getText();
				String termQueryRightStringArgument = rightArg.substring(1, rightArg.length() - 1);
			
				if (this.elasticQuery == "termQuery") {					
					comparisonQB = (QueryBuilder) QueryBuilders.termQuery(termQueryLeftArgument, termQueryRightStringArgument);
				}
				else if (this.elasticQuery == "rangeQuery") {
					comparisonQB = (RangeQueryBuilder) QueryBuilders.rangeQuery(termQueryLeftArgument);
					Method methodRangeOperator = RangeQueryBuilder.class.getDeclaredMethod(this.rangeOperator, Object.class, boolean.class);
					comparisonQB = (QueryBuilder) methodRangeOperator.invoke(comparisonQB, 
							termQueryRightStringArgument,
							this.includeBoundary);
				}
			} else if (ctx.NUMBER() != null){
				this.log.debug("parse number");
				method = QueryBuilders.class.getDeclaredMethod(this.elasticQuery, String.class, float.class);
				float termQueryRightFloatArgument = Float.parseFloat(ctx.NUMBER().getSymbol().getText());
				comparisonQB = (QueryBuilder) method.invoke(null, termQueryLeftArgument, termQueryRightFloatArgument);
			}

			this.comparisonBoolQB = QueryBuilders.boolQuery();
			if (comparisonQB != null) {
				if (this.comparisonBoolean) {
					this.comparisonBoolQB.must(comparisonQB);
				}
				else {
					this.comparisonBoolQB.mustNot(comparisonQB);
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
		 return this.boolQuery;
	 }
	 
	 
	 
	 

}
