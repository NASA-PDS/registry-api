package api.pds.nasa.gov.api_search_query_lexer;


import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import gov.nasa.pds.api.engineering.lexer.SearchLexer;
import gov.nasa.pds.api.engineering.lexer.SearchParser;

public class TestParsing {

	public static void main(String[] args)
	{
		String queryString = "lid eq *text*";
		CodePointCharStream input = CharStreams.fromString(queryString);
        SearchLexer lex = new SearchLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        System.out.println("Token count: " + Integer.toString(tokens.getNumberOfOnChannelTokens()));
        SearchParser par = new SearchParser(tokens);
        ParseTree tree = par.query();
                
        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
	}

}
