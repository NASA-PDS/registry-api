package gov.nasa.pds.api.registry.exceptions;

public class MembershipException extends Exception
{
	private static final long serialVersionUID = 4619792009605522154L;

	public MembershipException (String lidvid, String membership, String type)
	{
		super("The lidvid '" + lidvid + "' of type " + type + " does not support " + membership + " membership");
	}
}
