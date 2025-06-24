package ServerCore.DefaultComponents.Networking;

import java.util.ArrayList;

import ServerCore.ServerCore;
import ServerCore.TestingEnviromentCore;
import ServerCore.DefaultComponents.ComponentType;
import ServerCore.DefaultComponents.CoreComponent;

public abstract class ProtocolHandler extends CoreComponent{

	public ProtocolHandler(String name, Boolean active, ServerCore core, ArrayList<ComponentType> dependencies) {
		super(name, active, core, dependencies, ComponentType.PROTOCOL_HANDLER);
	}
	
	public abstract boolean accept(RConnection c);
	public abstract ConnectionHandler getHandler(RConnection c);

}
