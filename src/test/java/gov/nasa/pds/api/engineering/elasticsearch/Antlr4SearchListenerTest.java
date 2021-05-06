package gov.nasa.pds.api.engineering.elasticsearch;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import gov.nasa.pds.api.engineering.lexer.SearchLexer;
import gov.nasa.pds.api.engineering.lexer.SearchParser;

public class Antlr4SearchListenerTest
{
	private class NegativeTester implements Executable
	{
		final private Antlr4SearchListenerTest parent;
		final private String qs;
		
		NegativeTester (Antlr4SearchListenerTest parent, String qs)
		{
			this.parent = parent;
			this.qs = qs;
		}
		public void execute() { this.parent.run (this.qs); } 
	}

	private BoolQueryBuilder run (String query)
	{
		CodePointCharStream input = CharStreams.fromString(query);
        SearchLexer lex = new SearchLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        SearchParser par = new SearchParser(tokens);
        par.setErrorHandler(new BailErrorStrategy());
        ParseTree tree = par.query();
        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
        Antlr4SearchListener listener = new Antlr4SearchListener();
        walker.walk(listener, tree);
     
		//System.out.println ("query string: " + query);
        //System.out.println("query tree: " + tree.toStringTree(par));
        //System.out.println("boolean query: " + listener.getBoolQuery().toString());
        return listener.getBoolQuery();
	}

	@Test
	public void testEqualWildcard()
	{
		String qs = "lid eq *pdart14_meap";
		BoolQueryBuilder query = this.run(qs);
		
		Assertions.assertEquals (query.must().size(), 1);
		Assertions.assertEquals (query.mustNot().size(), 0);
		Assertions.assertEquals (query.should().size(), 0);
		Assertions.assertTrue (query.must().get(0) instanceof WildcardQueryBuilder);
		Assertions.assertEquals (((WildcardQueryBuilder)query.must().get(0)).fieldName(), "lid");
		Assertions.assertEquals (((WildcardQueryBuilder)query.must().get(0)).value(), "*pdart14_meap");
	}

	@Test
	public void testNotEqualWildchar()
	{
		String qs = "lid ne pdart14_meap?";
		BoolQueryBuilder query = this.run(qs);
		
		Assertions.assertEquals (query.must().size(), 0);
		Assertions.assertEquals (query.mustNot().size(), 1);
		Assertions.assertEquals (query.should().size(), 0);
		Assertions.assertTrue (query.mustNot().get(0) instanceof WildcardQueryBuilder);
		Assertions.assertEquals (((WildcardQueryBuilder)query.mustNot().get(0)).fieldName(), "lid");
		Assertions.assertEquals (((WildcardQueryBuilder)query.mustNot().get(0)).value(), "pdart14_meap?");
	}

	@Test
	public void testEscape()
	{
		String qs = "lid eq \"*pdart14_meap?\"";
		BoolQueryBuilder query = this.run(qs);
		
		Assertions.assertEquals (query.must().size(), 1);
		Assertions.assertEquals (query.mustNot().size(), 0);
		Assertions.assertEquals (query.should().size(), 0);
		Assertions.assertTrue (query.must().get(0) instanceof MatchQueryBuilder);
		Assertions.assertEquals (((MatchQueryBuilder)query.must().get(0)).fieldName(), "lid");
		Assertions.assertEquals (((MatchQueryBuilder)query.must().get(0)).value(), "*pdart14_meap?");
	}

	@Test
	public void testGroup()
	{
		String qs = "( lid eq *pdart14_meap* )";
		BoolQueryBuilder query = this.run(qs);
		
		Assertions.assertEquals (query.must().size(), 1);
		Assertions.assertEquals (query.mustNot().size(), 0);
		Assertions.assertEquals (query.should().size(), 0);
		Assertions.assertTrue (query.must().get(0) instanceof WildcardQueryBuilder);
		Assertions.assertEquals (((WildcardQueryBuilder)query.must().get(0)).fieldName(), "lid");
		Assertions.assertEquals (((WildcardQueryBuilder)query.must().get(0)).value(), "*pdart14_meap*");

	}

	@Test
	public void testGroupedStatementAndExclusiveInequality()
	{
		String qs = "( timestamp gt 12 and timestamp lt 27 )";
		BoolQueryBuilder query = this.run(qs);

		Assertions.assertEquals (query.must().size(), 2);
		Assertions.assertEquals (query.mustNot().size(), 0);
		Assertions.assertEquals (query.should().size(), 0);
		Assertions.assertTrue (query.must().get(0) instanceof RangeQueryBuilder);
		Assertions.assertTrue (query.must().get(1) instanceof RangeQueryBuilder);
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(0)).fieldName(), "timestamp");
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(0)).from(), "12");
		Assertions.assertNull (((RangeQueryBuilder)query.must().get(0)).to());
		Assertions.assertFalse (((RangeQueryBuilder)query.must().get(0)).includeLower());
		Assertions.assertTrue (((RangeQueryBuilder)query.must().get(0)).includeUpper());
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(1)).fieldName(), "timestamp");
		Assertions.assertNull (((RangeQueryBuilder)query.must().get(1)).from());
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(1)).to(), "27");
		Assertions.assertTrue (((RangeQueryBuilder)query.must().get(1)).includeLower());
		Assertions.assertFalse (((RangeQueryBuilder)query.must().get(1)).includeUpper());
	}

	@Test
	public void testGroupedStatementAndInclusiveInequality()
	{
		boolean result = true;
		String qs = "( timestamp_A ge 12 and timestamp_B le 27 )";
		BoolQueryBuilder query = this.run(qs);

		Assertions.assertEquals (query.must().size(), 2);
		Assertions.assertEquals (query.mustNot().size(), 0);
		Assertions.assertEquals (query.should().size(), 0);
		Assertions.assertTrue (query.must().get(0) instanceof RangeQueryBuilder);
		Assertions.assertTrue (query.must().get(1) instanceof RangeQueryBuilder);
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(0)).fieldName(), "timestamp_A");
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(0)).from(), "12");
		Assertions.assertNull (((RangeQueryBuilder)query.must().get(0)).to());
		Assertions.assertTrue (((RangeQueryBuilder)query.must().get(0)).includeLower());
		Assertions.assertTrue (((RangeQueryBuilder)query.must().get(0)).includeUpper());
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(1)).fieldName(), "timestamp_B");
		Assertions.assertNull (((RangeQueryBuilder)query.must().get(1)).from());
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(1)).to(), "27");
		Assertions.assertTrue (((RangeQueryBuilder)query.must().get(1)).includeLower());
		Assertions.assertTrue (((RangeQueryBuilder)query.must().get(1)).includeUpper());
	}

	@Test
	public void testNot()
	{
		String qs = "not ( timestamp ge 12 and timestamp le 27 )";
		BoolQueryBuilder query = this.run(qs);

		Assertions.assertEquals (query.must().size(), 0);
		Assertions.assertEquals (query.mustNot().size(), 1);
		Assertions.assertEquals (query.should().size(), 0);
		Assertions.assertTrue (query.mustNot().get(0) instanceof BoolQueryBuilder);
		query = (BoolQueryBuilder)query.mustNot().get(0);
		Assertions.assertEquals (query.must().size(), 2);
		Assertions.assertEquals (query.mustNot().size(), 0);
		Assertions.assertEquals (query.should().size(), 0);
		Assertions.assertTrue (query.must().get(0) instanceof RangeQueryBuilder);
		Assertions.assertTrue (query.must().get(1) instanceof RangeQueryBuilder);
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(0)).fieldName(), "timestamp");
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(0)).from(), "12");
		Assertions.assertNull (((RangeQueryBuilder)query.must().get(0)).to());
		Assertions.assertTrue (((RangeQueryBuilder)query.must().get(0)).includeLower());
		Assertions.assertTrue (((RangeQueryBuilder)query.must().get(0)).includeUpper());
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(1)).fieldName(), "timestamp");
		Assertions.assertNull (((RangeQueryBuilder)query.must().get(1)).from());
		Assertions.assertEquals (((RangeQueryBuilder)query.must().get(1)).to(), "27");
		Assertions.assertTrue (((RangeQueryBuilder)query.must().get(1)).includeLower());
		Assertions.assertTrue (((RangeQueryBuilder)query.must().get(1)).includeUpper());
	}

	@Test
	public void testNestedGrouping()
	{
		String qs = "( ( timestamp ge 12 and timestamp le 27 ) or ( timestamp gt 13 and timestamp lt 37 ) )";
		BoolQueryBuilder nest, query = this.run(qs);

		Assertions.assertEquals (query.must().size(), 0);
		Assertions.assertEquals (query.mustNot().size(), 0);
		Assertions.assertEquals (query.should().size(), 2);
		Assertions.assertTrue (query.should().get(0) instanceof BoolQueryBuilder);
		nest = (BoolQueryBuilder)query.should().get(0);
		Assertions.assertEquals (nest.must().size(), 2);
		Assertions.assertEquals (nest.mustNot().size(), 0);
		Assertions.assertEquals (nest.should().size(), 0);
		Assertions.assertTrue (nest.must().get(0) instanceof RangeQueryBuilder);
		Assertions.assertTrue (nest.must().get(1) instanceof RangeQueryBuilder);
		Assertions.assertEquals (((RangeQueryBuilder)nest.must().get(0)).fieldName(), "timestamp");
		Assertions.assertEquals (((RangeQueryBuilder)nest.must().get(0)).from(), "12");
		Assertions.assertNull (((RangeQueryBuilder)nest.must().get(0)).to());
		Assertions.assertTrue (((RangeQueryBuilder)nest.must().get(0)).includeLower());
		Assertions.assertTrue (((RangeQueryBuilder)nest.must().get(0)).includeUpper());
		Assertions.assertEquals (((RangeQueryBuilder)nest.must().get(1)).fieldName(), "timestamp");
		Assertions.assertNull (((RangeQueryBuilder)nest.must().get(1)).from());
		Assertions.assertEquals (((RangeQueryBuilder)nest.must().get(1)).to(), "27");
		Assertions.assertTrue (((RangeQueryBuilder)nest.must().get(1)).includeLower());
		Assertions.assertTrue (((RangeQueryBuilder)nest.must().get(1)).includeUpper());
		nest = (BoolQueryBuilder)query.should().get(1);
		Assertions.assertEquals (nest.must().size(), 2);
		Assertions.assertEquals (nest.mustNot().size(), 0);
		Assertions.assertEquals (nest.should().size(), 0);
		Assertions.assertTrue (nest.must().get(0) instanceof RangeQueryBuilder);
		Assertions.assertTrue (nest.must().get(1) instanceof RangeQueryBuilder);
		Assertions.assertEquals (((RangeQueryBuilder)nest.must().get(0)).fieldName(), "timestamp");
		Assertions.assertEquals (((RangeQueryBuilder)nest.must().get(0)).from(), "13");
		Assertions.assertNull (((RangeQueryBuilder)nest.must().get(0)).to());
		Assertions.assertFalse (((RangeQueryBuilder)nest.must().get(0)).includeLower());
		Assertions.assertTrue (((RangeQueryBuilder)nest.must().get(0)).includeUpper());
		Assertions.assertEquals (((RangeQueryBuilder)nest.must().get(1)).fieldName(), "timestamp");
		Assertions.assertNull (((RangeQueryBuilder)nest.must().get(1)).from());
		Assertions.assertEquals (((RangeQueryBuilder)nest.must().get(1)).to(), "37");
		Assertions.assertTrue (((RangeQueryBuilder)nest.must().get(1)).includeLower());
		Assertions.assertFalse (((RangeQueryBuilder)nest.must().get(1)).includeUpper());
	}

	@Test
	public void testNoWildcard()
	{
		String qs = "ref_lid_target eq urn:nasa:pds:context:target:planet.mercury";
		BoolQueryBuilder nest, query = this.run(qs);

		Assertions.assertEquals (query.must().size(), 1);
		Assertions.assertEquals (query.mustNot().size(), 0);
		Assertions.assertEquals (query.should().size(), 0);
		Assertions.assertTrue (query.must().get(0) instanceof MatchQueryBuilder);
		Assertions.assertEquals (((MatchQueryBuilder)query.must().get(0)).fieldName(), "ref_lid_target");
		Assertions.assertEquals (((MatchQueryBuilder)query.must().get(0)).value(), "urn:nasa:pds:context:target:planet.mercury");
	}
	
	@Test
	public void testNoWildcardQuoted()
	{
		String qs = "ref_lid_target eq \"urn:nasa:pds:context:target:planet.mercury\"";
		BoolQueryBuilder nest, query = this.run(qs);

		Assertions.assertEquals (query.must().size(), 1);
		Assertions.assertEquals (query.mustNot().size(), 0);
		Assertions.assertEquals (query.should().size(), 0);
		Assertions.assertTrue (query.must().get(0) instanceof MatchQueryBuilder);
		Assertions.assertEquals (((MatchQueryBuilder)query.must().get(0)).fieldName(), "ref_lid_target");
		Assertions.assertEquals (((MatchQueryBuilder)query.must().get(0)).value(), "urn:nasa:pds:context:target:planet.mercury");
	}
	
	@Test
	public void testExceptionsInParsing()
	{
		NegativeTester actor;
		String fails[] = {"( a eq b", "a eq b )", "not( a eq b )",
				          "a eq b and c eq d and", "( a eq b and c eq d and )",
				          "( a eq b and c eq d or e eq f )"};

		for (int i = 0 ; i < fails.length ; i++)
		{
			actor = new NegativeTester(this, fails[i]);
			Assertions.assertThrows(ParseCancellationException.class, actor);
		}
	}
}
