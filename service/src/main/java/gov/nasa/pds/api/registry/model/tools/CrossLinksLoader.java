package gov.nasa.pds.api.registry.model.tools;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This is a wrapper class for ./CrossLinks.java. Use this loader to do a
 * one-time pull of CrossLink's cross-links.json configuration file.
 * 
 * Use like:
 *   this.crossLinksLoader = new CrossLinksLoader();
 *   this.crossLinks = this.crossLinksLoader.loadConfiguration();
 * 
 * @see gov.nasa.pds.api.registry.controllers.ProductsController#Constructor(ConnectionContext connectionContext, ErrorMessageFactory errorMessageFactory, ObjectMapper objectMapper)
 * @see gov.nasa.pds.api.registry.controllers.ProductsController#productCrossLinks(String identifier)
 * @see gov.nasa.pds.api.registry.model.tools.CrossLinks
 * @author tariqksoliman
 */
public class CrossLinksLoader {
    private CrossLinks config;

    public CrossLinks loadConfiguration() {
        if (config == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                config = mapper.readValue(new File("src/main/resources/cross-links.json"), CrossLinks.class);
                System.out.println(config);
            } catch (IOException e) {
                // Handle exception
                System.out.println(e);
            }
        }
        return config;
    }
}