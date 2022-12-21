package gov.nasa.pds.api.registry.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import gov.nasa.pds.api.registry.GroupConstraint;
import gov.nasa.pds.api.registry.lexer.SearchLexer;
import gov.nasa.pds.api.registry.lexer.SearchParser;


@Component
public class ProductQueryBuilderUtil
{
    private static final Logger log = LoggerFactory.getLogger(ProductQueryBuilderUtil.class);
    
    @Value("${filter.archiveStatus}")
    private String propArchiveStatusFilter;
    private static List<String> archiveStatusFilter;

    /**
     * Init archive status filter
     */
    @PostConstruct
    public void init() 
    {
        if(propArchiveStatusFilter == null) return;
        
        List<String> list = new ArrayList<>();
        
        StringTokenizer tkz = new StringTokenizer(propArchiveStatusFilter, ",; ");
        while(tkz.hasMoreTokens())
        {
            String token = tkz.nextToken();
            list.add(token);
        }
        
        if(!list.isEmpty())
        {
            archiveStatusFilter = list;
        }
    }
    
    public static void addArchiveStatusFilter(BoolQueryBuilder boolQuery)
    {
        log.debug("addArchiveStatusFilter: " + archiveStatusFilter);
        
        if(archiveStatusFilter == null || archiveStatusFilter.isEmpty()) return;
        
        boolQuery.must(QueryBuilders.termsQuery("ops:Tracking_Meta/ops:archive_status", archiveStatusFilter));
    }

    public static void addHistoryStopband (BoolQueryBuilder boolQuery)
    {
    	boolQuery.mustNot(QueryBuilders.existsQuery("ops:Provenance/ops:superseded_by"));
    }

    public static void addPresetCriteria(BoolQueryBuilder boolQuery, GroupConstraint presetCriteria)
    {
        if(presetCriteria != null)
        {
            presetCriteria.all().forEach((key, list) -> 
            {
                list.forEach(value -> { boolQuery.must(QueryBuilders.termQuery(key, value)); });
            });
            presetCriteria.any().forEach((key, list) -> 
            {
            	list.forEach(value -> { boolQuery.filter(QueryBuilders.termQuery(key, value)); });
            });
            presetCriteria.not().forEach((key, list) -> 
            {
            	list.forEach(value -> { boolQuery.mustNot(QueryBuilders.termQuery(key, value)); });
            });
        }
    }
    
    public static BoolQueryBuilder parseQueryString(String queryString)
    {
        CodePointCharStream input = CharStreams.fromString(queryString);
        SearchLexer lex = new SearchLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);

        SearchParser par = new SearchParser(tokens);
        par.setErrorHandler(new BailErrorStrategy());
        ParseTree tree = par.query();

        log.debug(tree.toStringTree(par));

        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
        Antlr4SearchListener listener = new Antlr4SearchListener();
        walker.walk(listener, tree);

        return listener.getBoolQuery();
    }

}
