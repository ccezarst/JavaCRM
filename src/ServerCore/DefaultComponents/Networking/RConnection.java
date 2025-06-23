package ServerCore.DefaultComponents.Networking;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

public class RConnection {
	public final InetAddress senderIP;
	public final Socket connectionSocket;
	
	public RConnection(Socket s) {
		this.connectionSocket = s;
		this.senderIP = s.getInetAddress();
	}
}
