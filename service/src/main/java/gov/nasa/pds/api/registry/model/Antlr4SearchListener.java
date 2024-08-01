package gov.nasa.pds.api.registry.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.pds.api.registry.lexer.SearchBaseListener;
import gov.nasa.pds.api.registry.lexer.SearchParser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch._types.query_dsl.SimpleQueryStringQuery;
import org.opensearch.client.opensearch._types.query_dsl.TermsQuery;
import org.opensearch.client.opensearch._types.query_dsl.TermsQueryField;
import org.opensearch.client.opensearch._types.query_dsl.TermsSetQuery;
import org.opensearch.client.opensearch._types.query_dsl.WildcardQuery;
import org.opensearch.client.opensearch._types.query_dsl.Operator;
import org.opensearch.index.query.RangeQueryBuilder;
import org.opensearch.index.query.SimpleQueryStringBuilder;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.query.QueryBuilder;

public class Antlr4SearchListener extends SearchBaseListener {
  enum conjunctions {
    AND, OR
  };

  enum operation {
    eq, ge, gt, le, lt, ne
  };

  private static final Logger log = LoggerFactory.getLogger(Antlr4SearchListener.class);

  private BoolQuery.Builder queryBuilder = new BoolQuery.Builder();
  private conjunctions conjunction = conjunctions.AND; // DEFAULT

  final private Deque<BoolQuery.Builder> stackQueryBuilders = new ArrayDeque<BoolQuery.Builder>();
  final private Deque<conjunctions> stack_conjunction = new ArrayDeque<conjunctions>();

  private operation operator = null;

  public Antlr4SearchListener() {
    super();
  }


  @Override
  public void exitQuery(SearchParser.QueryContext ctx) {}

  @Override
  public void enterGroup(SearchParser.GroupContext ctx) {
    log.debug("Enter Group");

    this.stack_conjunction.push(this.conjunction);
    this.conjunction = conjunctions.AND; // DEFAULT

    this.stackQueryBuilders.push(this.queryBuilder);
    this.queryBuilder = new BoolQuery.Builder();

  }

  @Override
  public void exitGroup(SearchParser.GroupContext ctx) {

    log.debug("Exit Group");

    BoolQuery.Builder upperBoolQueryBuilder = this.stackQueryBuilders.pop();
    this.conjunction = this.stack_conjunction.pop();

    Query innerQuery = this.queryBuilder.build().toQuery();
    if (ctx.NOT() != null) {
      upperBoolQueryBuilder.mustNot(innerQuery);
    } else {
      if (this.conjunction == conjunctions.AND) {
        upperBoolQueryBuilder.must(innerQuery);
      } else {
        upperBoolQueryBuilder.should(innerQuery);
      }
    }

    this.queryBuilder = upperBoolQueryBuilder;

  }

  @Override
  public void enterAndStatement(SearchParser.AndStatementContext ctx) {
    log.debug("Enter AndStatement");
    this.conjunction = conjunctions.AND;
  }

  @Override
  public void enterOrStatement(SearchParser.OrStatementContext ctx) {
    log.debug("Enter OrStatement");
    this.conjunction = conjunctions.OR;
  }

  @Override
  public void exitOrStatement(SearchParser.OrStatementContext ctx) {
    log.debug("Exit OrStatement");
    this.queryBuilder.minimumShouldMatch("1");
  }


  @Override
  public void enterComparison(SearchParser.ComparisonContext ctx) {}

  @Override
  public void exitComparison(SearchParser.ComparisonContext ctx) {
    log.debug("Exit comparison");
    final String left = SearchUtil.jsonPropertyToOpenProperty(ctx.FIELD().getSymbol().getText());

    String right;
    Query comparatorQuery = null;


    if (ctx.NUMBER() != null) {
      right = ctx.NUMBER().getSymbol().getText();
    } else if (ctx.STRINGVAL() != null) {
      right = ctx.STRINGVAL().getSymbol().getText();
      right = right.replaceAll("^\"|\"$", "");
    } else {
      throw new ParseCancellationException(
          "A right component (literal) of a comparison is neither a number or a string. Number and String are the only types supported for literals.");
    }

    if (this.operator == operation.eq || this.operator == operation.ne) {


      FieldValue fieldValue = new FieldValue.Builder().stringValue(right).build();

      MatchQuery matchQueryBuilder = new MatchQuery.Builder().field(left).query(fieldValue).build();

      comparatorQuery = matchQueryBuilder.toQuery();

      if (this.operator == operation.ne) {
        comparatorQuery = new BoolQuery.Builder().mustNot(comparatorQuery).build().toQuery();
      }



    } else {
      RangeQuery.Builder rangeQueryBuilder = new RangeQuery.Builder();

      rangeQueryBuilder = rangeQueryBuilder.field(left);

      if (this.operator == operation.ge)
        rangeQueryBuilder.gte(JsonData.of(right));
      else if (this.operator == operation.gt)
        rangeQueryBuilder.gt(JsonData.of(right));
      else if (this.operator == operation.le)
        rangeQueryBuilder.lte(JsonData.of(right));
      else if (this.operator == operation.lt)
        rangeQueryBuilder.lt(JsonData.of(right));
      else {
        throw new ParseCancellationException("Operator " + this.operator.name()
            + " is not supported. Supported comparison operators are eq, ne, gt, gte, lt, lte.");
      }

      comparatorQuery = rangeQueryBuilder.build().toQuery();

    }

    if (this.conjunction == conjunctions.AND) {
      this.queryBuilder.must(comparatorQuery);
    } else {
      this.queryBuilder.should(comparatorQuery);
    }

  }

  @Override
  public void enterLikeComparison(SearchParser.LikeComparisonContext ctx) {}

  @Override
  public void exitLikeComparison(SearchParser.LikeComparisonContext ctx) {
    log.debug("Exit likeComparison");
    final String left = SearchUtil.jsonPropertyToOpenProperty(ctx.FIELD().getSymbol().getText());

    String right = ctx.STRINGVAL().getText();
    // remove quotes
    right = right.replaceAll("^\"|\"$", "");

    SimpleQueryStringQuery simpleQueryString = new SimpleQueryStringQuery.Builder().fields(left)
        .query(right).fuzzyMaxExpansions(0).build();

    Query query = simpleQueryString.toQuery();
    log.debug("Exit Like comparison: left member is " + left + " right member is " + right);

    if (this.conjunction == conjunctions.AND) {
      this.queryBuilder.must(query);
    } else {
      this.queryBuilder.should(query);
    }

  }

  @Override
  public void enterOperator(SearchParser.OperatorContext ctx) {
    if (ctx.EQ() != null)
      this.operator = operation.eq;
    else if (ctx.GE() != null)
      this.operator = operation.ge;
    else if (ctx.GT() != null)
      this.operator = operation.gt;
    else if (ctx.LE() != null)
      this.operator = operation.le;
    else if (ctx.LT() != null)
      this.operator = operation.lt;
    else if (ctx.NE() != null)
      this.operator = operation.ne;
    else {
      log.error("Panic, there are more operators than this versionof the lexer knows about");
      throw new ParseCancellationException(); // PANIC: listener out of sync with the grammar
    }
  }

  public BoolQuery getBoolQuery() {
    log.debug("Get boolQuery");
    return this.queryBuilder.build();
  }
}
