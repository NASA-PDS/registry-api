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

import gov.nasa.pds.api.registry.lexer.SearchLexer;
import gov.nasa.pds.api.registry.lexer.SearchListener;
import gov.nasa.pds.api.registry.lexer.SearchParser;
import gov.nasa.pds.api.registry.lexer.SearchParser.AndStatementContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.ComparisonContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.ExistenceContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.ExpressionContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.GroupContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.LikeComparisonContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.OperatorContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.OrStatementContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.QueryContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.QueryTermContext;

import org.junit.jupiter.api.Assertions;


public class TestParsing implements ParseTreeListener, SearchListener {
  TerminalNode field = null, number = null, strval = null;
  boolean isNot = false;

  @Test
  public void testNumber() {
    String queryString = "lid eq 1234";
    CodePointCharStream input = CharStreams.fromString(queryString);
    SearchLexer lex = new SearchLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lex);
    SearchParser par = new SearchParser(tokens);
    ParseTree tree = par.query();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(this, tree);

    Assertions.assertNotNull(this.field);
    Assertions.assertEquals("lid", this.field.getSymbol().getText());

    Assertions.assertNotNull(this.number);
    Assertions.assertEquals("1234", this.number.getSymbol().getText());

  }

  @Test
  public void testStringVal() {
    String queryString = "lid eq \"*text*\"";
    CodePointCharStream input = CharStreams.fromString(queryString);
    SearchLexer lex = new SearchLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lex);
    SearchParser par = new SearchParser(tokens);
    ParseTree tree = par.query();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(this, tree);

    Assertions.assertNotNull(this.field);
    Assertions.assertEquals("lid", this.field.getSymbol().getText());

    Assertions.assertNotNull(this.strval);
    Assertions.assertEquals("\"*text*\"", this.strval.getSymbol().getText());
  }


  @Test
  public void testLike() {
    String queryString = "lid like \"*text*\"";
    CodePointCharStream input = CharStreams.fromString(queryString);
    SearchLexer lex = new SearchLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lex);
    SearchParser par = new SearchParser(tokens);
    ParseTree tree = par.query();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(this, tree);

    Assertions.assertNotNull(this.field);
    Assertions.assertEquals("lid", this.field.getText());

    Assertions.assertNotNull(this.strval);
    Assertions.assertEquals("\"*text*\"", this.strval.getText());
  }


  @Test
  void testTemporalRange() {
    String queryString = "(AAA gt \"2016-09-09\" and BBB lt \"2020-09-11\")";
    CodePointCharStream input = CharStreams.fromString(queryString);
    SearchLexer lex = new SearchLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lex);
    SearchParser par = new SearchParser(tokens);
    ParseTree tree = par.query();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(this, tree);

    // TODO: Parse

  }

  @Test
  void testFieldExistence() {
    String queryString  = "apple exists";
    CodePointCharStream input = CharStreams.fromString(queryString);
    SearchLexer lex = new SearchLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lex);
    SearchParser par = new SearchParser(tokens);
    ParseTree tree = par.query();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(this, tree);
    
    Assertions.assertNotNull(this.field);
    Assertions.assertNull(this.strval);
    Assertions.assertEquals("apple", this.field.getSymbol().getText());
  }
  @Test
  void testParenFieldExistence() {
    String queryString  = "(apple exists)";
    CodePointCharStream input = CharStreams.fromString(queryString);
    SearchLexer lex = new SearchLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lex);
    SearchParser par = new SearchParser(tokens);
    ParseTree tree = par.query();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(this, tree);
    
    Assertions.assertNotNull(this.field);
    Assertions.assertNull(this.strval);
    Assertions.assertEquals("apple", this.field.getSymbol().getText());
  }
  @Test
  void testStrvalExistence() {
    String queryString  = "\".*apple\" exists";
    CodePointCharStream input = CharStreams.fromString(queryString);
    SearchLexer lex = new SearchLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lex);
    SearchParser par = new SearchParser(tokens);
    ParseTree tree = par.query();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(this, tree);
    
    Assertions.assertNull(this.field);
    Assertions.assertNotNull(this.strval);
    Assertions.assertEquals("\".*apple\"", this.strval.getSymbol().getText());
  }
  @Test
  void testParenStrvalExistence() {
    String queryString  = "(\".*apple\" exists)";
    CodePointCharStream input = CharStreams.fromString(queryString);
    SearchLexer lex = new SearchLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lex);
    SearchParser par = new SearchParser(tokens);
    ParseTree tree = par.query();
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(this, tree);
    
    Assertions.assertNull(this.field);
    Assertions.assertNotNull(this.strval);
    Assertions.assertEquals("\".*apple\"", this.strval.getSymbol().getText());
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
    this.field = ctx.FIELD();
    this.number = ctx.NUMBER();
    this.strval = ctx.STRINGVAL();
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

  @Override
  public void enterEveryRule(ParserRuleContext arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void exitEveryRule(ParserRuleContext arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visitErrorNode(ErrorNode arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visitTerminal(TerminalNode arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void enterLikeComparison(LikeComparisonContext ctx) {
    this.field = ctx.FIELD();
    this.strval = ctx.STRINGVAL();

    String op = ctx.getChild(1).getText();
    if ("not".equals(op))
      this.isNot = true;
  }

  @Override
  public void exitLikeComparison(LikeComparisonContext ctx) {
    // TODO Auto-generated method stub

  }

  @Override
  public void enterExistence(ExistenceContext ctx) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void exitExistence(ExistenceContext ctx) {
    this.field = ctx.FIELD();
    this.strval = ctx.STRINGVAL();
  }
}
