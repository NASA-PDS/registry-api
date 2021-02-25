package gov.nasa.pds.api.engineering.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.engineering.elasticsearch.ElasticSearchUtil;
import gov.nasa.pds.api.engineering.elasticsearch.entities.EntityProduct;
import gov.nasa.pds.api.model.ProductWithXmlLabel;
import gov.nasa.pds.model.Product;
import gov.nasa.pds.model.Products;
import io.swagger.annotations.ApiParam;



@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-10-29T11:01:11.991-07:00[America/Los_Angeles]")
@Controller
public class MyProductsApiController extends MyProductsApiBareController implements ProductsApi {

    private static final Logger log = LoggerFactory.getLogger(MyProductsApiController.class);
    
    @org.springframework.beans.factory.annotation.Autowired
    public MyProductsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
    	
    	super(objectMapper, request);
    }
    
   
    public ResponseEntity<Products> products(@ApiParam(value = "offset in matching result list, for pagination", defaultValue = "0") @Valid @RequestParam(value = "start", required = false, defaultValue="0") Integer start
,@ApiParam(value = "maximum number of matching results returned, for pagination", defaultValue = "100") @Valid @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit
,@ApiParam(value = "search query") @Valid @RequestParam(value = "q", required = false) String q
,@ApiParam(value = "returned fields, syntax field0,field1") @Valid @RequestParam(value = "fields", required = false) List<String> fields
,@ApiParam(value = "sort results, syntax asc(field0),desc(field1)") @Valid @RequestParam(value = "sort", required = false) List<String> sort
,@ApiParam(value = "only return the summary, useful to get the list of available properties", defaultValue = "false") @Valid @RequestParam(value = "only-summary", required = false, defaultValue="false") Boolean onlySummary
)  {
    	return this.getProductsResponseEntity(q, start, limit, fields, sort, onlySummary);

    }

    
     
    public ResponseEntity<Product> productsByLidvid(@ApiParam(value = "lidvid (urn)",required=true) @PathVariable("lidvid") String lidvid) {
    	return this.getProductResponseEntity(lidvid);
    }

    
 
    
}
