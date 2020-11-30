package gov.nasa.pds.api.engineering.elasticsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import gov.nasa.pds.api.engineering.lexer.SearchBaseListener;
import gov.nasa.pds.api.engineering.lexer.SearchParser;

public class Antlr4SearchListener extends SearchBaseListener {
	
	private static final Logger log = LoggerFactory.getLogger(Antlr4SearchListener.class);
	
	private BoolQueryBuilder boolQuery;
	private QueryBuilder queryBuilder;
	
	
	private String elasticQuery;
	private String termQueryLeftArgument;
	private String termQueryRightArgument;
	
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
		 this.boolQuery.must(this.queryBuilder);
     }
	 
	 
	 @Override
	 public void enterGroup(SearchParser.GroupContext ctx) {
		 Antlr4SearchListener.log.debug("enter group: " + ctx.getText());
     }

	 
	 @Override
	 public void enterComparison(SearchParser.ComparisonContext ctx) {
		Antlr4SearchListener.log.debug("enter comparison: " + ctx.getText());
		
		this.termQueryLeftArgument = ctx.FIELD().getSymbol().getText();
		
		String rightArg = ctx.STRINGVAL().getSymbol().getText();
		this.termQueryRightArgument = rightArg.substring(1, rightArg.length() - 1);
			
	 }

	@Override
	public void exitComparison(SearchParser.ComparisonContext ctx) {
		Antlr4SearchListener.log.debug("exit comparison: " + ctx.getText());

		try {
			
			//Class<?> QueryBuildersClass = Class.forName("QueryBuilders");
			Method method = QueryBuilders.class.getDeclaredMethod(this.elasticQuery, String.class, String.class);
			
			this.queryBuilder = (QueryBuilder) method.invoke(null, this.termQueryLeftArgument, this.termQueryRightArgument);
		
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
		else {
			Antlr4SearchListener.log.info("Operator is NOT EQ ");
			
		}
		
	}
	 
	 
	 public BoolQueryBuilder getBoolQuery() {
		 return this.boolQuery;
	 }
	 
	 
	 
	 

}
