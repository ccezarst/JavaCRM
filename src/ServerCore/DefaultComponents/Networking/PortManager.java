package ServerCore.DefaultComponents.Networking;

import java.util.HashMap;
import java.util.Map;

import ServerCore.ServerCore;
import ServerCore.TestingEnviromentCore;
import ServerCore.DefaultComponents.ComponentType;
import ServerCore.DefaultComponents.CoreComponent;

public class PortManager extends CoreComponent{

	public PortManager(Boolean active, ServerCore core) {
		super("PortManager", active, core, ComponentType.PORT_MANAGER);
		// TODO Auto-generated constructor stub
	}
	
	protected Map<Integer, PortBinder> portBindings = new HashMap<>();
	
	protected ConnectionManager man;
	
	@Override
	protected void step(ServerCore core) {
		// TODO Auto-generated method stub
		
	}
	
	public void receiveNewConnection(RConnection c) {
		synchronized(this.man) {
			this.man.receiveNewConnection(c);
		}
	}
	
	public void markNewBinding(PortBinder binder, int port) {
		synchronized(this.portBindings) {
			this.portBindings.put(port, binder);	
		}
	}
	
	@Override
	protected void update(ServerCore core) {
		// TODO Auto-generated method stub
		synchronized(this.portBindings) {
			this.portBindings = new HashMap<>();	
		}
		if(man != null) {
			synchronized(this.man) {
				this.man = this.core.getComponentFromName("ConnectionManager", ConnectionManager.class);
			}
		}else {
			this.man = this.core.getComponentFromName("ConnectionManager", ConnectionManager.class);
		}
	}

	@Override
	protected int test(TestingEnviromentCore core) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
