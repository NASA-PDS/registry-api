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

// So that we don't have to load the json multiple times
public class CrossLinksLoader {
    private CrossLinks config;

    public CrossLinks loadConfiguration() {
        if (config == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                config = mapper.readValue(new File("cross-links.json"), CrossLinks.class);
                System.out.println(config);
            } catch (IOException e) {
                // Handle exception
                System.out.println(e);
            }
        }
        return config;
    }
}