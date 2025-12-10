package api.pds.nasa.gov.api_search_query_lexer;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import gov.nasa.pds.api.registry.lexer.SearchListener;
import gov.nasa.pds.api.registry.lexer.SearchParser.AndStatementContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.ComparisonContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.ExpressionContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.GroupContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.LikeComparisonContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.OperatorContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.OrStatementContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.QueryContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.QueryTermContext;

public class MockedListener implements ParseTreeListener, SearchListener {


  TerminalNode field = null, number = null, strval = null;
  boolean isNot = false;

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
  public void enterLikeComparison(LikeComparisonContext ctx) {
    this.field = ctx.FIELD();
    this.strval = ctx.STRINGVAL();

    String op = ctx.getChild(1).getText();
    if ("not".equals(op))
      isNot = true;
  }

  @Override
  public void exitLikeComparison(LikeComparisonContext ctx) {
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

}
