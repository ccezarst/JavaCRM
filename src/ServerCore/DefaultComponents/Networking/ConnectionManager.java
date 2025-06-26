package ServerCore.DefaultComponents.Networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import ServerCore.ServerCore;
import ServerCore.TestingEnviromentCore;
import ServerCore.DefaultComponents.ComponentType;
import ServerCore.DefaultComponents.CoreComponent;

public class ConnectionManager extends CoreComponent{

	public ConnectionManager(Boolean active, ServerCore core) {
		super("ConnectionManager", active, core, ComponentType.CONNECTION_MANAGER);
		// TODO Auto-generated constructor stub
	}
	
	protected ExecutorService es;
	
	protected class ConnectionHandlerFinder implements Runnable{ // is a task executed in parallel that finds the protocol
		public RConnection c;
		public ServerCore core;
		public ConnectionHandlerFinder(RConnection c, ServerCore s) {
			this.c = c;
			this.core = s;
		}
		@Override
		public void run() {
			synchronized(this.core){
				synchronized(this.c) {
					ArrayList<CoreComponent> comps = this.core.getComponentsOfType(ComponentType.PROTOCOL_HANDLER);
					System.console().printf("Received connection from " + c.senderIP.toString() + "\n");
					boolean found = false;
					for(CoreComponent cp: comps) {
						ProtocolHandler ph = (ProtocolHandler) cp;
						ConnectionHandler h = ph.getHandler(c);
						if(h != null) {
							es.execute(h);
							found = true;
							break;
						}
					}
					try {
						if(!found) {
							c.connectionSocket.close();	
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	
	public void receiveNewConnection(RConnection c) {
		this.es.execute(new ConnectionHandlerFinder(c, this.core));
	}

	@Override
	protected void step(ServerCore core) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void update(ServerCore core) {
		if(this.es != null) {
			synchronized(this.es) {
				this.es = Executors.newCachedThreadPool();
			}
		}else {
			this.es = Executors.newCachedThreadPool();
		}
		// will create a pool which automatically adds threads when needed and keeps old ones for 60 seconds(to be possibly reused)
	}

	@Override
	protected int test(TestingEnviromentCore core) {
		// TODO Auto-generated method stub
		return 0;
	}

}
