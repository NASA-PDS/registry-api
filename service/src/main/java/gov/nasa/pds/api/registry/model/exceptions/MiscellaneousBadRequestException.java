package gov.nasa.pds.api.registry.model.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

/**
 * Use as a catch-all for one-off errors where the request is bad and specific handling is not required
 */
public class MiscellaneousBadRequestException extends RegistryApiException {
    private static final Logger log = LoggerFactory.getLogger(MiscellaneousBadRequestException.class);
    @Serial
    private static final long serialVersionUID = 2026697251322082840L;

    public MiscellaneousBadRequestException(String msg) {
        super("MiscellaneousBadRequestException: " + msg);
    }

}
