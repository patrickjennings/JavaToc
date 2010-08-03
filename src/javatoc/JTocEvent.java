package javatoc;

/**
 * @author Patrick Jennings
 * 
 * The JTocEvent class provides an interface for
 * generic events.
 *
 */
public interface JTocEvent {
	
	/*
	 * Some generic events that may be sent from the toc
	 * servers.
	 */
	public static final String NEWMESSAGE = "IM_IN_ENC2";
	public static final String BUDDYUPDATE = "UPDATE_BUDDY2";
	public static final String BUDDYADDED = "NEW_BUDDY_REPLY2";
	public static final String CONFIG = "CONFIG2";
	public static final String NAMEFORMAT = "NICK";
	public static final String UNKNOWN = "Unknown Event";

	public String getType();
}
