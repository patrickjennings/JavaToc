package javatoc;

public class JTocNewMessageEvent implements JTocEvent {

	private static final String type = NEWMESSAGE;
	
	private String from;
	private String message;
	
	public JTocNewMessageEvent(String f, String m) {
		from = f;
		message = m;
	}

	public String getType() {
		return type;
	}

	public String getUser() {
		return from;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String toString() {
		return "New Message:\n"
			+ "\tUser:\t" + from + "\n"
			+ "\tMSG:\t" + message;
	}
}
