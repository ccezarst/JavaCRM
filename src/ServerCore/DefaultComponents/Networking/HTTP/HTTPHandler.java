
package ServerCore.DefaultComponents.Networking.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ServerCore.ServerCore;
import ServerCore.TestingEnviromentCore;
import ServerCore.DefaultComponents.ComponentType;
import ServerCore.DefaultComponents.Networking.ConnectionHandler;
import ServerCore.DefaultComponents.Networking.ProtocolHandler;
import ServerCore.DefaultComponents.Networking.RConnection;

public class HTTPHandler extends ProtocolHandler{
	
	public HTTPHandler(Boolean active, ServerCore core) {
		super("HTTPHandler", active, core, new ArrayList<>());
		// TODO Auto-generated constructor stub
	}

	protected HTTPRouter router;
	
	protected class HTTPConnectionHandler extends ConnectionHandler{
		protected HTTPRequest req;
		public HTTPConnectionHandler(HTTPRequest req, ServerCore core) {
			super(req.con, core);
			this.req = req;
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			// start listening on input stream, after confirming a request reset the parser and send the request to the router.
			try {
				if(req.isConnectionHTTP()) {
					req.parse();
					/*System.out.println(req.method + " " + req.path + " " + req.protocolVersion);
//					/.console().printf(req.orgIp + "\n");
					//System.console().printf(req.headers.toString() + "\n");
					for(Map.Entry<String, ArrayList<String>> e: req.headers.entrySet()){
						System.out.println(e.getKey() + ": " + e.getValue());	
					}
					System.console().printf(req.body + "\n");
					System.out.println();*/
					router.processRequest(req);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}}
	
	@Override
	public ConnectionHandler getHandler(RConnection c) {
		HTTPRequest req = new HTTPRequest(c);
		try {
			if(req.isConnectionHTTP()){
				return new HTTPConnectionHandler(req, this.core);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void step(ServerCore core) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void update(ServerCore core) {
		// TODO Auto-generated method stub
		this.router = this.core.getComponentFromName("HTTPRouter", HTTPRouter.class);
	}

	@Override
	protected int test(TestingEnviromentCore core) {
		// TODO Auto-generated method stub
		return 0;
	}
}
