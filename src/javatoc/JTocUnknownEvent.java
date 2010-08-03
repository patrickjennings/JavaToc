package javatoc;

public class JTocUnknownEvent implements JTocEvent {

	private static final String type = UNKNOWN;
	
	private String message;
	
	public JTocUnknownEvent(String m) {
		message = m;
	}

	public String getType() {
		return type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String toString() {
		return "Unknown event:\n"
			+ "\t" + message;
	}
}
