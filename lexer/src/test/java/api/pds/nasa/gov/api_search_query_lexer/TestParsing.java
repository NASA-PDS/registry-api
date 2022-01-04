package api.pds.nasa.gov.api_search_query_lexer;


import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import gov.nasa.pds.api.engineering.lexer.SearchLexer;
import gov.nasa.pds.api.engineering.lexer.SearchListener;
import gov.nasa.pds.api.engineering.lexer.SearchParser;
import gov.nasa.pds.api.engineering.lexer.SearchParser.AndStatementContext;
import gov.nasa.pds.api.engineering.lexer.SearchParser.ComparisonContext;
import gov.nasa.pds.api.engineering.lexer.SearchParser.ExpressionContext;
import gov.nasa.pds.api.engineering.lexer.SearchParser.GroupContext;
import gov.nasa.pds.api.engineering.lexer.SearchParser.OperatorContext;
import gov.nasa.pds.api.engineering.lexer.SearchParser.OrStatementContext;
import gov.nasa.pds.api.engineering.lexer.SearchParser.QueryContext;
import gov.nasa.pds.api.engineering.lexer.SearchParser.QueryTermContext;



public class TestParsing implements ParseTreeListener,SearchListener
{
	TerminalNode field=null, number=null, strval=null, value=null;

	@Test
	public void isNumber()
	{
		String queryString = "lid eq 1234";
		CodePointCharStream input = CharStreams.fromString(queryString);
        SearchLexer lex = new SearchLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        SearchParser par = new SearchParser(tokens);
        ParseTree tree = par.query();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, tree);
        
        Assertions.assertNotEquals(this.number, null);
        Assertions.assertEquals(this.number.getSymbol().getText(), "1234");
       
	}
	
	@Test
	public void isStringVal()
	{
		String queryString = "lid eq \"*text*\"";
		CodePointCharStream input = CharStreams.fromString(queryString);
        SearchLexer lex = new SearchLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        SearchParser par = new SearchParser(tokens);
        ParseTree tree = par.query();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, tree);
        
        Assertions.assertNotEquals(this.strval, null);
        Assertions.assertEquals(this.strval.getSymbol().getText(), "\"*text*\"");
	}
	
	@Test
	public void isValue()
	{
		String queryString = "lid eq *text*";
		CodePointCharStream input = CharStreams.fromString(queryString);
        SearchLexer lex = new SearchLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        SearchParser par = new SearchParser(tokens);
        ParseTree tree = par.query();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, tree);
        
        Assertions.assertNotEquals(this.value, null);
        Assertions.assertEquals(this.value.getSymbol().getText(), "*text*");
	}
	
	@Test void isParsable()
	{
		String queryString = "lid eq text";
		CodePointCharStream input = CharStreams.fromString(queryString);
        SearchLexer lex = new SearchLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        SearchParser par = new SearchParser(tokens);
        ParseTree tree = par.query();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(this, tree);
 
        Assertions.assertNotEquals(this.field, null);
        Assertions.assertEquals(this.field.getSymbol().getText(), "text");
	}

	@Override
	public void visitTerminal(TerminalNode node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visitErrorNode(ErrorNode node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void enterEveryRule(ParserRuleContext ctx) {
		// TODO Auto-generated method stub
	}
	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterQuery(QueryContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitQuery(QueryContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterQueryTerm(QueryTermContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitQueryTerm(QueryTermContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterGroup(GroupContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitGroup(GroupContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterExpression(ExpressionContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitExpression(ExpressionContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAndStatement(AndStatementContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAndStatement(AndStatementContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterOrStatement(OrStatementContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitOrStatement(OrStatementContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterComparison(ComparisonContext ctx) {
		// TODO Auto-generated method stub
		this.field = ctx.FIELD(1);
		this.number = ctx.NUMBER();
		this.strval = ctx.STRINGVAL();
		this.value = ctx.VALUE();
	}

	@Override
	public void exitComparison(ComparisonContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterOperator(OperatorContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitOperator(OperatorContext ctx) {
		// TODO Auto-generated method stub
		
	}
}