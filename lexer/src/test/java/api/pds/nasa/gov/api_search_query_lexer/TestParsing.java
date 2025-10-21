package api.pds.nasa.gov.api_search_query_lexer;


import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;
import gov.nasa.pds.api.registry.lexer.SearchLexer;
import gov.nasa.pds.api.registry.lexer.SearchParser;

import org.junit.jupiter.api.Assertions;



public class TestParsing {


  @Test
  public void testMaliciousQuery() {

    ParseCancellationException ex =
        Assertions.assertThrows(ParseCancellationException.class, () -> {
          String queryString = "select * from table where lid like '%'";

          CodePointCharStream input = CharStreams.fromString(queryString);
          SearchLexer lex = new SearchLexer(input);
          CommonTokenStream tokens = new CommonTokenStream(lex);

          SearchParser par = new SearchParser(tokens);
          par.setErrorHandler(new BailErrorStrategy());
          ParseTree tree = par.query();
        }, "Expected code to throw, but it didn't");


  }



  @Test
  public void testNumber() {
    String queryString = "lid eq 1234";
    CodePointCharStream input = CharStreams.fromString(queryString);
    SearchLexer lex = new SearchLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lex);
    SearchParser par = new SearchParser(tokens);
    ParseTree tree = par.query();
    ParseTreeWalker walker = new ParseTreeWalker();
    MockedListener listener = new MockedListener();
    walker.walk(listener, tree);

    Assertions.assertNotNull(listener.field);
    Assertions.assertEquals(listener.field.getSymbol().getText(), "lid");

    Assertions.assertNotEquals(listener.number, null);
    Assertions.assertEquals(listener.number.getSymbol().getText(), "1234");

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
    MockedListener listener = new MockedListener();
    walker.walk(listener, tree);

    Assertions.assertNotNull(listener.field);
    Assertions.assertEquals(listener.field.getSymbol().getText(), "lid");

    Assertions.assertNotNull(listener.strval);
    Assertions.assertEquals(listener.strval.getSymbol().getText(), "\"*text*\"");
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
    MockedListener listener = new MockedListener();
    walker.walk(listener, tree);

    Assertions.assertNotNull(listener.field);
    Assertions.assertEquals(listener.field.getText(), "lid");

    Assertions.assertNotNull(listener.strval);
    Assertions.assertEquals(listener.strval.getText(), "\"*text*\"");
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
    MockedListener listener = new MockedListener();
    walker.walk(listener, tree);

    // TODO: Parse

  }
}


