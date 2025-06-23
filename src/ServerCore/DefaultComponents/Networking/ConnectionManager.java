package ServerCore.DefaultComponents.Networking;

import java.util.ArrayList;

import ServerCore.ServerCore;
import ServerCore.TestingEnviromentCore;
import ServerCore.DefaultComponents.ComponentType;
import ServerCore.DefaultComponents.CoreComponent;

public class ConnectionManager extends CoreComponent{

	public ConnectionManager(Boolean active, ServerCore core) {
		super("CoreComponent", active, core, ComponentType.CONNECTION_MANAGER);
		// TODO Auto-generated constructor stub
	}
	
	protected ArrayList<RConnection> conQueue = new ArrayList<>();
	
	public void receiveNewConnection(RConnection c) {
		synchronized (this.conQueue) {
			this.conQueue.add(c);
		}
	}

	@Override
	protected void step(ServerCore core) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void update(ServerCore core) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int test(TestingEnviromentCore core) {
		// TODO Auto-generated method stub
		return 0;
	}

}
