package gov.nasa.pds.api.engineering.elasticsearch.business;


public class LidVidNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4675409702552965562L;

	public LidVidNotFoundException(String lidvid) {
		super("The lidvid " + lidvid + " was not found");
	}
}
