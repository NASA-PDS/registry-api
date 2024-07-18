package gov.nasa.pds.api.registry.controllers;


import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import gov.nasa.pds.api.registry.model.exceptions.AcceptFormatNotSupportedException;
import gov.nasa.pds.api.registry.model.exceptions.SortSearchAfterMismatchException;
import gov.nasa.pds.api.registry.model.exceptions.NotFoundException;
import gov.nasa.pds.api.registry.model.exceptions.RegistryApiException;
import gov.nasa.pds.api.registry.model.exceptions.UnhandledException;
import gov.nasa.pds.api.registry.model.exceptions.UnparsableQParamException;



@ControllerAdvice
public class RegistryApiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private String errorDisclaimerHeader = "An error occured.\n";
  private String errorDisclaimerFooter =
      "For assistance, forward this error message to pds-operator@jpl.nasa.org";

  // TODO refactor code to avoid repeating oneself.

  private ResponseEntity<Object> genericExceptionHandler(RegistryApiException ex,
      WebRequest request, String errorDescrpitionSuffix, HttpStatus status) {

    String requestDescription = request.getDescription(false);
    String errorDescription = ex.getMessage() + errorDescrpitionSuffix;
    String errorIdentifier = ex.getUuid();


    String bodyOfResponse =
        status.toString() + "\n Request " + requestDescription + " failed with message:\n"
            + errorDescription + "(ref:" + errorIdentifier + ")\n" + errorDisclaimerFooter;
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), status, request);

  }


  @ExceptionHandler(value = {NotFoundException.class})
  protected ResponseEntity<Object> notFound(NotFoundException ex, WebRequest request) {
    return genericExceptionHandler(ex, request, "", HttpStatus.NOT_FOUND);

  }

  @ExceptionHandler(value = {UnhandledException.class})
  protected ResponseEntity<Object> unhandled(UnhandledException ex, WebRequest request) {
    return genericExceptionHandler(ex, request, "", HttpStatus.INTERNAL_SERVER_ERROR);
  }


  @ExceptionHandler(value = {AcceptFormatNotSupportedException.class})
  protected ResponseEntity<Object> notAcceptable(AcceptFormatNotSupportedException ex,
      WebRequest request) {
    Set<String> supportedFormats = ProductsController.getFormatters().keySet();
    String errorDescriptionSuffix =
        " Supported formats (in Accept header) are: " + String.join(", ", supportedFormats);

    return genericExceptionHandler(ex, request, errorDescriptionSuffix, HttpStatus.NOT_ACCEPTABLE);

  }

  @ExceptionHandler(value = {SortSearchAfterMismatchException.class})
  protected ResponseEntity<Object> missSort(SortSearchAfterMismatchException ex,
                                            WebRequest request) {
    return genericExceptionHandler(ex, request, "", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {UnparsableQParamException.class})
  protected ResponseEntity<Object> unparsableQParam(UnparsableQParamException ex,
      WebRequest request) {
    return genericExceptionHandler(ex, request, "", HttpStatus.BAD_REQUEST);
  }


}
