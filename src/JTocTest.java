
import java.io.IOException;
import java.util.Scanner;

import javatoc.JTocActionListener;
import javatoc.JTocConnection;
import javatoc.JTocEvent;
import javatoc.JTocNewMessageEvent;

/**
 * @author Patrick Jennings
 * 
 * The JTocTest class holds an example of how to use the
 * javatoc package.
 *
 */
public class JTocTest implements JTocActionListener {
	
	private JTocConnection c;
	
	public static void main(String[] args) {
		new JTocTest();
	}
	
	public JTocTest() {
		c = new JTocConnection();
		c.addEventListener(this);
		try {
			c.connect("username","password");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		c.setAway("Send me a message and I will echo!");
		c.setInfo("Using the javatoc package!");
		c.setIdle(90);
		
		Scanner sc = new Scanner(System.in);
		String command;
		StringBuffer sb;
		command = sc.next();
		while(!command.equals("exit")) {
			sb = new StringBuffer(command);
			if(command.startsWith("send")) {
				nextElement(sb);
				c.sendMessage(nextElement(sb), sb.toString());
			}
			command = sc.next();
		}
		c.disconnect();
	}

	public void receiveEvent(JTocEvent ev) {
		System.out.println(ev);
		
		if(ev.getType().equals(JTocEvent.NEWMESSAGE)) {
			c.setIdle(0);
			c.sendMessage(((JTocNewMessageEvent) ev).getUser(), ((JTocNewMessageEvent) ev).getMessage());
		}
	}
	
	public static String nextElement(StringBuffer str) {
		String result = "";
	    int i = str.indexOf(":", 0);
	    if(i == -1) {
	    	result = str.toString();
	    	str.setLength(0);
	    }
	    else {
	    	result = str.substring(0,i).toString();
	    	String a = str.substring(i+1);
	    	str.setLength(0);
	    	str.append(a);
	    }
	    return result;
	}
}
