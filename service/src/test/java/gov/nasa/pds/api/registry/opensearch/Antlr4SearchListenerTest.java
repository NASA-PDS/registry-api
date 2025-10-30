package gov.nasa.pds.api.registry.opensearch;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;


import gov.nasa.pds.api.registry.lexer.SearchLexer;
import gov.nasa.pds.api.registry.lexer.SearchParser;
import gov.nasa.pds.api.registry.model.Antlr4SearchListener;

class Antlr4SearchListenerTest {
  private class NegativeTester implements Executable {
    final private Antlr4SearchListenerTest parent;
    final private String qs;

    NegativeTester(Antlr4SearchListenerTest parent, String qs) {
      this.parent = parent;
      this.qs = qs;
    }

    public void execute() {
      this.parent.run(this.qs);
    }
  }


  private Antlr4SearchListener listener;

  @BeforeEach
  void setUp() {
    listener = new Antlr4SearchListener(null);
  }


  private BoolQuery run(String query) {
    CodePointCharStream input = CharStreams.fromString(query);
    SearchLexer lex = new SearchLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lex);
    SearchParser par = new SearchParser(tokens);
    par.setErrorHandler(new BailErrorStrategy());
    ParseTree tree = par.query();
    // Walk it and attach our listener
    ParseTreeWalker walker = new ParseTreeWalker();
    Antlr4SearchListener listener = new Antlr4SearchListener(null);
    walker.walk(listener, tree);

    // System.out.println ("query string: " + query);
    // System.out.println("query tree: " + tree.toStringTree(par));
    // System.out.println("boolean query: " + listener.getBoolQuery().toString());
    return listener.getBoolQuery();
  }


  @Test
  void testSimpleCompEq() {
    String qs = "pds:Time_Coordinates.pds:stop_date_time eq \"2021-05-21T15:47:08Z\"";
    BoolQuery query = this.run(qs);
    // TODO: add asserts
    Assertions.assertEquals(1, query.must().size());
    Query matchQuery = (Query) query.must().get(0);
    Assertions.assertEquals(Query.Kind.Match, matchQuery._kind());
    // Assertions.assertEquals((matchQuery).field(), "pds:Time_Coordinates/pds:stop_date_time");


  }


  @Test
  void testLikeWildcard() {
    String qs = "lid like \"*pdart14_meap\"";
    BoolQuery query = this.run(qs);
    // TODO: add asserts


  }

  @Test
  void testEscape() {
    String qs = "lid eq \"*pdart14_meap?\"";
    BoolQuery query = this.run(qs);

    // TODO: add asserts
  }

  @Test
  void testGroupedStatementAndExclusiveInequality() {
    String qs = "( timestamp gt 12 and timestamp lt 27 )";
    BoolQuery query = this.run(qs);

    // TODO: add asserts
  }

  @Test
  void testGroupedStatementAndInclusiveInequality() {
    String qs = "( timestamp_A ge 12 and timestamp_B le 27 )";
    BoolQuery query = this.run(qs);

    // TODO: add asserts
  }

  @Test
  void testNot() {
    String qs = "not ( timestamp ge 12 and timestamp le 27 )";
    BoolQuery query = this.run(qs);

    // TODO: add asserts
  }


  @Test
  void testNestedGrouping() {

    String qs =
        "( ( timestamp ge 12 and timestamp le 27 ) or ( timestamp gt 13 and timestamp lt 37 ) )";
    BoolQuery query = this.run(qs);

    // TODO: add asserts

  }



  @Test
  void testNoWildcardQuoted() {
    String qs = "ref_lid_target eq \"urn:nasa:pds:context:target:planet.mercury\"";
    BoolQuery query = this.run(qs);

    // TODO: add asserts
  }

  @Test
  void testExceptionsInParsing() {
    NegativeTester actor;
    String fails[] = {"( a eq b", "a eq b )", "not( a eq b )", "a eq b and c eq d and",
        "( a eq b and c eq d and )", "( a eq b and c eq d or e eq f )"};

    for (int i = 0; i < fails.length; i++) {
      actor = new NegativeTester(this, fails[i]);
      Assertions.assertThrows(ParseCancellationException.class, actor);
    }
  }



  @Test
  void testEnterGroup() {
    listener.enterGroup(Mockito.mock(SearchParser.GroupContext.class));
    assertEquals(0, listener.getBoolQuery().should().size(), "Initial should size should be 0");
  }

  @Test
  void testExitGroup() {
    SearchParser.GroupContext mockCtx = Mockito.mock(SearchParser.GroupContext.class);
    listener.enterGroup(mockCtx);
    listener.exitGroup(mockCtx);
    assertEquals(0, listener.getBoolQuery().should().size(),
        "Should size after exiting group should be 0");
  }

  @Test
  void testEnterAndStatement() {
    listener.enterAndStatement(Mockito.mock(SearchParser.AndStatementContext.class));
    // assuming the conjunctions are properly set
  }

  @Test
  void testEnterOrStatement() {
    listener.enterOrStatement(Mockito.mock(SearchParser.OrStatementContext.class));
    // assuming the conjunctions are properly set
  }

  @Test
  void testExitOrStatement() {
    listener.enterOrStatement(Mockito.mock(SearchParser.OrStatementContext.class));
    listener.exitOrStatement(Mockito.mock(SearchParser.OrStatementContext.class));
    assertSame("1", listener.getBoolQuery().minimumShouldMatch(),
        "Minimum should match should be set");
  }


  @Test
  void testGetBoolQuery() {
    BoolQuery query = listener.getBoolQuery();
    assertNotNull(query, "BoolQuery should not be null");
  }


}
