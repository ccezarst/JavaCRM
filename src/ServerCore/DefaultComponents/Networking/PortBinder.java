package ServerCore.DefaultComponents.Networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ServerCore.ServerCore;
import ServerCore.TestingEnviromentCore;
import ServerCore.DefaultComponents.ComponentType;
import ServerCore.DefaultComponents.CoreComponent;

public class PortBinder extends CoreComponent{
	
	private ServerSocket soc;
	private PortManager man;
	public int port;
	
	public PortBinder(String name, Boolean active, ServerCore core, int port) {
		super(name, active, core, ComponentType.PORT_BINDER);
		this.port = port;
	}
	
	@Override
	protected void step(ServerCore core) {
		synchronized(this.soc) {
			synchronized(this.man) {
				try {
					Socket s = soc.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void update(ServerCore core) {
		try {
			if(this.soc != null) {
				this.soc = new ServerSocket(port);
				this.man = this.core.getComponentFromName("PortManager", PortManager.class);	
			}else {
				synchronized(this.soc) {
					synchronized(this.man) {
						this.soc = new ServerSocket(port);
						this.man = this.core.getComponentFromName("PortManager", PortManager.class);		
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected int test(TestingEnviromentCore core) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
