package javatoc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Patrick Jennings
 * 
 * The JacConnention object holds all of the important
 * information in order to connect and send commands
 * to the AIM servers. 
 * The user should add a JacActionListener before connection
 * so that events may be processed.
 *
 */
public class JTocConnection {
	
	/*
	 * Some static variables.
	 */
    private static final int SIGNON = 1;
    private static final int DATA = 2;
	private static String tocHost = "toc.oscar.aol.com";
	private static int tocPort = 9898;
	private static String authHost = "login.oscar.aol.com";
	private static int authPort = 5190;
	private static String language = "english";
	private static String version = "TIC:JToc";
	
	/*
	 * Class variables.
	 */
	private Socket connection;
	private InputStream is;
    private OutputStream os;
    private String screenname;
    private short sequence;
    private JTocActionListener al;
    private JTocParserThread it;

	public JTocConnection() {
	}
	
	/**
	 * Creates a TCP connection with the oscar server.
	 * @param s The screen name fo the user
	 * @param p The users password (Is not saved)
	 * @throws IOException Throws an exception if a connection error occurs
	 */
	public void connect(String s, String p) throws IOException {
		screenname = s;
		connection = new Socket(tocHost, tocPort);		// Create a socket to the toc server.
		is = connection.getInputStream();				// Get input and output streams so that we may write bytes to the toc server.
	    os = connection.getOutputStream();
	    os.write(("FLAPON\r\n\r\n").getBytes());		// Must sent these bytes to the toc server before authentication.
	    getFlap();										// Server responds with garbage we don't care about.
	    sendFlapSignon();								// Send the signon FLAP
	    
	    String command = "toc2_login " + authHost + " " + authPort + " "
	    	+ screenname + " " + Util.roastPassword(p) + " " + language +
	    	" \"" + version + "\" 160 US \"\" \"\" 3 0 30303 -kentucky -utf8 "
	    	+ Util.signOnCode(screenname, p);
	    
	    sendFlap(DATA,command);							// Send authentication FLAP.
	    String str = getFlap();							// Get return message from the toc server.
	    
	    if(str.toUpperCase().startsWith("ERROR:"))		// If an error occurred, throw an exception and leave immediately.
	    	throw new IOException("Could not connect! " + str);

	    sendFlap(DATA,"toc_add_buddy " + screenname);	// Send the remaining FLAPs needed to signon.
	    sendFlap(DATA,"toc_init_done");
	    sendFlap(DATA,"toc_set_caps 09461343-4C7F-11D1-8222-444553540000 09461348-4C7F-11D1-8222-444553540000");
	    sendFlap(DATA,"toc_add_permit ");
	    sendFlap(DATA,"toc_add_deny ");
	    
	    it = new JTocParserThread(this);				// Create and start a toc parser thread.
	    it.start();
	}
	
	/**
	 * Kills the connection to the toc server.
	 */
	public void disconnect() {
		try {
			it.end();
			connection.close();
			is.close();
			os.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Returns whether there is an open socket to the
	 * toc server.
	 */
	public boolean isConnected() {
		return connection.isConnected();
	}
	
	public void sendMessage(String to, String msg) {
		try {
			this.sendFlap(DATA,"toc_send_im " + Util.normalize(to) + " \"" +
					Util.encode(msg) + "\"");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addBuddy(String group, String buddy) {
		try {
			sendFlap(DATA, "toc2_new_buddies {g:" + group + "\nb:" + buddy + "\n}");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteBuddy(String group, String buddy) {
		try {
			sendFlap(DATA, "toc2_remove_buddy {g:" + group + "\nb:" + buddy + "\n}");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void newGroup(String group) {
		try {
			System.out.println("toc2_new_group \"" + group + "\"");
			sendFlap(DATA, "toc2_new_group \"" + group + "\"");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteGroup(String group) {
		try {
			sendFlap(DATA, "toc2_del_group \"" + group + "\"");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setInfo(String info) {
		try {
			sendFlap(DATA, "toc_set_info \"" + info + "\"");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setAway(String message) {
		try {
			sendFlap(DATA, "toc_set_away \"" + message + "\"");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setIdle(int secs) {
		try {
			sendFlap(DATA, "toc_set_idle \"" + secs + "\"");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getFlap() throws IOException {
		if(is.read() != '*')
			throw new IOException();
		is.read();
		is.read();
		is.read();
		int length = (is.read()*0x100) + is.read();
		byte b[] = new byte[length];
		is.read(b);
		return new String(b);
	}
	
	public void sendFlap(int type,String str) throws IOException {
		int length = str.length()+1;
	    sequence++;
	    os.write((byte)'*');
	    os.write((byte)type);
	    writeWord(sequence);
	    writeWord((short)length);
	    os.write(str.getBytes());
	    os.write(0);
	    os.flush();
	}
	
	private void sendFlapSignon() throws IOException {
		int length = 8 + screenname.length();
		sequence++;
		os.write((byte)'*');
		os.write((byte)SIGNON);
		writeWord(sequence);
		writeWord((short)length);

		os.write(0);
		os.write(0);
		os.write(0);
	    os.write(1);

	    os.write(0);
	    os.write(1);

	    writeWord((short)screenname.length());
	    os.write(screenname.getBytes());
	    os.flush();
	}
	
	private void writeWord(short word) throws IOException {
		os.write((byte) ((word >> 8) & 0xff) );
	    os.write( (byte) (word & 0xff) );
	}
	
	public void sendEvent(JTocEvent event) {
		al.receiveEvent(event);
    }
	
	public void addEventListener(JTocActionListener l) {
        al = l;
    }
	
	public void removeEventListener(JTocActionListener l) {
        al = null;
    }
}
