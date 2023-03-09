package gov.nasa.pds.api.registry.exceptions;


public class LidVidMismatchException extends Exception {
  /**
   * 
   */
  private static final long serialVersionUID = -4675409702552965562L;

  public LidVidMismatchException(String lidvid, String expected, String found) {
    super("The lidvid " + lidvid + " was of type " + found + " not of type " + expected);
  }
}
