package gov.nasa.pds.api.registry.model.exceptions;

public class SortSearchAfterMismatchException extends RegistryApiException {

  public SortSearchAfterMismatchException(String detail) {
    super("Invalid combination of sort and searchAfter values: " + detail);
  }
}
