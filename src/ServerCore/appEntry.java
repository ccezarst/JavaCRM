package ServerCore;

import ServerCore.DefaultComponents.Networking.ConnectionManager;
import ServerCore.DefaultComponents.Networking.PortBinder;
import ServerCore.DefaultComponents.Networking.PortManager;
import ServerCore.DefaultComponents.Networking.HTTP.HTTPHandler;
import ServerCore.DefaultComponents.Networking.HTTP.HTTPRouter;

public class appEntry {

	public static void main(String[] args) {
		ServerCore core = new ServerCore();
		core.addComponent(new ConnectionManager(true, core));
		core.addComponent(new PortManager(true, core));
		core.addComponent(new HTTPHandler(true, core));
		core.addComponent(new HTTPRouter(true, core));
		core.addComponent(new PortBinder("TestingPort", true, core, 42076));
		System.console().printf("Starting server\n");
		core.init();
		System.console().printf("Server inited\n");
		core.start();
		System.console().printf("Server started\n");
	}

}
