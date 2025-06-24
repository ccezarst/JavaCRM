package ServerCore.DefaultComponents.Networking;

import ServerCore.ServerCore;

public abstract class ConnectionHandler implements Runnable{
	public RConnection con;
	public ServerCore core;
	
	
	public ConnectionHandler(RConnection con, ServerCore core) {
		this.con = con;
		this.core = core;
	}
	
}
