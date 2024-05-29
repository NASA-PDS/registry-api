package gov.nasa.pds.api.registry.model.exceptions;

public class MissSortWithSearchAfterException extends RegistryApiException {

  public MissSortWithSearchAfterException() {
    super("sort parameter missing, sort is mandatory with search-after");
  }
}
