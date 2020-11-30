package gov.nasa.pds.api.engineering.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.elasticsearch.index.query.QueryBuilders;

import java.lang.reflect.InvocationTargetException;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import gov.nasa.pds.api.engineering.lexer.SearchBaseListener;
import gov.nasa.pds.api.engineering.lexer.SearchParser;

public class Antlr4SearchListener extends SearchBaseListener {
	
	private static final Logger log = LoggerFactory.getLogger(Antlr4SearchListener.class);
	
	private BoolQueryBuilder boolQuery;
	
	private String elasticQuery;
	private String leftArgument;
	private String rightArgument;
	
	 @Override
	 public void enterQuery(SearchParser.QueryContext ctx) {
		 System.out.println("rule entered: " + ctx.getText());
		 Antlr4SearchListener.log.info(ctx.getText());
		 
		 this.boolQuery = QueryBuilders.boolQuery();
    	
	    	
		 
     }
	 
	 @Override
	 public void enterExpression(SearchParser.ExpressionContext ctx) {
		 System.out.println("rule entered: " + ctx.getText());
		 Antlr4SearchListener.log.info(ctx.getText());
     }
	 
	 @Override
	 public void enterQueryTerm(SearchParser.QueryTermContext ctx) {
		 Antlr4SearchListener.log.info(ctx.getText());
     }
	 
	 @Override
	 public void enterGroup(SearchParser.GroupContext ctx) {
		 Antlr4SearchListener.log.info(ctx.getText());
     }

	 
	 @Override
	 public void enterComparison(SearchParser.ComparisonContext ctx) {
		Antlr4SearchListener.log.info("Comparison rule: " + ctx.getText());
		
		this.leftArgument = ctx.FIELD();
		this.rightArgument = ctx.STRINGVAL();
			
		}else {
			// raise exception, error 400
		}
	 }

	@Override
	public void existComparison(SearchParser.ComparisonContext ctx) {
		java.lang.reflect.Method method;
		try {
		  method = QueryBuilders.getClass().getMethod(this.elasticQuery, String.class, String.class);
		  
		  method.invoke(QueryBuilders, this.leftArgument, this.rightArgument);
		
		} catch (SecurityException e) { 
			Antlr4SearchListener.log.error(e.getMessage());
		} catch (NoSuchMethodException e) {	  
			Antlr4SearchListener.log.error(e.getMessage());
		} catch (IllegalArgumentException e) {
			Antlr4SearchListener.log.error(e.getMessage());
		} catch (IllegalAccessException e) {
			Antlr4SearchListener.log.error(e.getMessage());
		} catch (InvocationTargetException e) {
		    Antlr4SearchListener.log.error(e.getMessage());
		}
		
	}

	@Override
	public void enterOperator(SearchParser.OperatorContext ctx) {
		if (ctx.EQ() == SearchParser.EQ) {
				Antlr4SearchListener.log.info("Operator is EQ " + ctx.getText());
				this.elasticQuery = "termQuery";
		}
		else {
			Antlr4SearchListener.log.info("Operator is NOT EQ ");
			
		}
		
	}
	 
	 
	 public getBoolQuery() {
		 return this.boolQuery;
	 }
	 
	 
	 
	 

}
