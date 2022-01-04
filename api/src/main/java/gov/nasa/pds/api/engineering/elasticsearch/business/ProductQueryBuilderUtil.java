package gov.nasa.pds.api.engineering.elasticsearch.business;

import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.engineering.elasticsearch.Antlr4SearchListener;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.lexer.SearchLexer;
import gov.nasa.pds.api.engineering.lexer.SearchParser;


public class ProductQueryBuilderUtil
{
    private static final Logger log = LoggerFactory.getLogger(ProductQueryBuilderUtil.class);
    
    /**
     * Create PDS query language query
     * @param req request parameters
     * @param presetCriteria preset criteria
     * @return a query
     */
    public static QueryBuilder createPqlQuery(String queryString, List<String> fields, Map<String, String> presetCriteria)
    {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (queryString != null)
        {
            boolQuery = parseQueryString(queryString);
        }

        for (Map.Entry<String, String> e : presetCriteria.entrySet())
        {
            // example "product_class", "Product_Collection"
            boolQuery.must(QueryBuilders.termQuery(e.getKey(), e.getValue()));
        }

        if (fields != null)
        {
            boolQuery.must(parseFields(fields));
        }
        
        return boolQuery;
    }


    /**
     * Create full-text / keyword query (Uses Lucene query language for now)
     * @param req request parameters
     * @param presetCriteria preset criteria
     * @return a query
     */
    public static QueryBuilder createKeywordQuery(String keyword, Map<String, String> presetCriteria)
    {
        // Lucene query
        QueryStringQueryBuilder luceneQuery = QueryBuilders.queryStringQuery(keyword);
        // Search in following fields only
        luceneQuery.field("title");
        luceneQuery.field("description");
        
        // Boolean (root) query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(luceneQuery);
        
        // Preset criteria filter
        if(presetCriteria != null)
        {
            presetCriteria.forEach((key, value) -> 
            {
                boolQuery.filter(QueryBuilders.termQuery(key, value));
            });
        }
        
        return boolQuery;
    }

    
    private static BoolQueryBuilder parseFields(List<String> fields)
    {
        BoolQueryBuilder fieldsBoolQuery = QueryBuilders.boolQuery();
        String esField;
        ExistsQueryBuilder existsQueryBuilder;
        for (String field : fields)
        {
            esField = ElasticSearchUtil.jsonPropertyToElasticProperty(field);
            existsQueryBuilder = QueryBuilders.existsQuery(esField);
            fieldsBoolQuery.should(existsQueryBuilder);
        }
        fieldsBoolQuery.minimumShouldMatch(1);

        return fieldsBoolQuery;
    }

    
    private static BoolQueryBuilder parseQueryString(String queryString)
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
