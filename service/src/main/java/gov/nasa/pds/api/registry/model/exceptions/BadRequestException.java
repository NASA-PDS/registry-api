package gov.nasa.pds.api.registry.model.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

public class BadRequestException extends RegistryApiException {
    private static final Logger log = LoggerFactory.getLogger(BadRequestException.class);
    @Serial
    private static final long serialVersionUID = 2026697251322082840L;

    public BadRequestException(String msg) {
        super("BadRequestException: " + msg);
    }
}
