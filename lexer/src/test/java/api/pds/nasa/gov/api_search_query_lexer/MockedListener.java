package api.pds.nasa.gov.api_search_query_lexer;

import java.util.ArrayList;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import gov.nasa.pds.api.registry.lexer.SearchListener;
import gov.nasa.pds.api.registry.lexer.SearchParser.AndStatementContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.ComparisonContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.ExpressionContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.FieldsContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.GroupContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.LikeComparisonContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.ExistenceContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.OperatorContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.OrStatementContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.QueryContext;
import gov.nasa.pds.api.registry.lexer.SearchParser.QueryTermContext;

public class MockedListener implements ParseTreeListener, SearchListener {

  ArrayList<String> fields = new ArrayList<String>();
  TerminalNode number = null, strval = null;
  boolean isNot = false;

  @Override
  public void enterQuery(QueryContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void exitQuery(QueryContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void enterQueryTerm(QueryTermContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void exitQueryTerm(QueryTermContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void enterGroup(GroupContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void exitGroup(GroupContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void enterExpression(ExpressionContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void exitExpression(ExpressionContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void enterAndStatement(AndStatementContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void exitAndStatement(AndStatementContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void enterOrStatement(OrStatementContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void exitOrStatement(OrStatementContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void enterComparison(ComparisonContext ctx) {
    this.number = ctx.NUMBER();
    this.strval = ctx.STRINGVAL();
  }

  @Override
  public void exitComparison(ComparisonContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void enterLikeComparison(LikeComparisonContext ctx) {
    this.strval = ctx.STRINGVAL();

    String op = ctx.getChild(1).getText();
    if ("not".equals(op))
      isNot = true;
  }

  @Override
  public void exitLikeComparison(LikeComparisonContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void enterOperator(OperatorContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void exitOperator(OperatorContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void visitTerminal(TerminalNode node) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void visitErrorNode(ErrorNode node) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void enterEveryRule(ParserRuleContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void exitEveryRule(ParserRuleContext ctx) {
    // Nothing useful to do in this mocked version

  }


  @Override
  public void enterExistence(ExistenceContext ctx) {
    // Nothing useful to do in this mocked version

  }

  @Override
  public void exitExistence(ExistenceContext ctx) {
  }

  @Override
  public void enterFields(FieldsContext ctx) {
  }

  @Override
  public void exitFields(FieldsContext ctx) {
    boolean any = ctx.ALL() == null;
    String fieldname = "";
    if (ctx.FIELDNAME() != null) {
      fieldname = ctx.FIELDNAME().getText();
    }
    if (ctx.ALL() != null ) {
      fieldname = ctx.ALL().getText();
    }
    if (ctx.ANY() != null) {
      fieldname = ctx.ANY().getText();
    }
    if (fieldname.contains("*")) {
      fields.add(fieldname.replace(".", "\\.").replace("*", ".*"));
    } else {
      fields.add(fieldname);
    }
  }

}
