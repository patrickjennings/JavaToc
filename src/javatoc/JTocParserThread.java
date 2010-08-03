package javatoc;

import java.io.IOException;

/**
 * @author Patrick Jennings
 * 
 * The JTocParserThread class parses the messages received 
 * from the toc server and sends new events to the
 * JTocActionListener.
 *
 */
public class JTocParserThread extends Thread {
	
	private boolean running;
	private JTocConnection connection;
	
	public JTocParserThread(JTocConnection c) {
		connection = c;
		running = false;
	}
	
	public void end() {
		running = false;
	}

	/*
	 * Run the parser thread on the connection. The thread
	 * will parse new messages from the toc server.
	 * If the connection is disconnected, the parser
	 * will stop running.
	 */
	public void run() {
		running = connection.isConnected();
		while(running) {
			String msg = null;
			try {
				msg = connection.getFlap();
			} catch (IOException e) {
				System.out.println("Connection broken!");
				break;
			}
			
			parseMessage(msg);
			
			running = connection.isConnected();
		}
	}
	
	/*
	 * Parses the toc server message into a new event
	 * then sends the event to the register action
	 * listener.
	 */
	private void parseMessage(String str) {
		if(str == null)
			return;
		
		StringBuffer sb = new StringBuffer(str);
		String event = Util.nextElement(sb);		// The event type
		String message = sb.toString();				// The rest of the message
		
		if(event.equals(JTocEvent.NEWMESSAGE)) {	// If the event is a new message, parse and send a NEWMESSAGE event.
			String from = Util.nextElement(sb);		// Get the user name who sent the message.
			Util.nextElement(sb);					// auto (dunno what it does)
			Util.nextElement(sb);					// garbage
			Util.nextElement(sb);					// garbage
			Util.nextElement(sb);					// buddy status (could be useful...)
			Util.nextElement(sb);					// garbage
			Util.nextElement(sb);					// garbage
			Util.nextElement(sb);					// language (could be useful...)
			message = sb.toString();				// Get the contents of the message (in HTML format)
			if(message == null)
				message = "";
			connection.sendEvent(new JTocNewMessageEvent(from, message));
		}
		else if(event.equals(JTocEvent.BUDDYUPDATE)) {	// If event is a buddy update, send appropriate event.
			String name = Util.nextElement(sb);
			Util.nextElement(sb);
			String warn = Util.nextElement(sb);
			String signOnTime = Util.nextElement(sb);
			String idle = Util.nextElement(sb);
			connection.sendEvent(new JTocBuddyUpdateEvent(name, warn, signOnTime, idle));
		}
		/*else if(event.equals("NEW_BUDDY_REPLY2")) {
			connection.sendEvent(new JTocEvent(JTocEvent.BUDDYADDED, message));
		}
		else if(event.equals("CONFIG2")) {
			connection.sendEvent(new JTocEvent(JTocEvent.CONFIG, message));
		}
		else if(event.equals("NICK")) {
			connection.sendEvent(new JTocEvent(JTocEvent.NAMEFORMAT, message));
		}*/
		else {
			connection.sendEvent(new JTocUnknownEvent(str));
		}
	}
}
