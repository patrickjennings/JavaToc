package javatoc;

public class JTocBuddyUpdateEvent implements JTocEvent {

	private static final String type = BUDDYUPDATE;
	
	private String user;
	private String warn;
	private String signOnTime;
	private String idle;
	
	public JTocBuddyUpdateEvent(String u, String w, String s, String i) {
		user = u;
		warn = w;
		signOnTime = s;
		idle = i;
	}

	public String getType() {
		return type;
	}

	public String getUser() {
		return user;
	}
	
	public String getWarn() {
		return warn;
	}
	
	public String getSignOnTime() {
		return signOnTime;
	}
	
	public String getIdle() {
		return idle;
	}
	
	public String toString() {
		return "Status update:\n"
			+ "\tUser:\t" + user + "\n"
			+ "\tWarn:\t" + warn + "\n"
			+ "\tUptime:\t" + signOnTime + "\n"
			+ "\tIdle:\t" + idle;
	}
}
