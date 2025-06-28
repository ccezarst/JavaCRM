package ServerCore.DefaultComponents.Networking.HTTP;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ServerCore.ServerCore;
import ServerCore.DefaultComponents.ComponentType;
import ServerCore.DefaultComponents.CoreComponent;

public abstract class HTTPEndpoint extends CoreComponent {
	public HTTPEndpoint(String name, Boolean active, ServerCore core, String route) {
		super(name, active, core, ComponentType.HTTP_ENDPOINT);
		if(route.charAt(0) != '/') {
			this.route = "/" + route;
		}else {
			this.route = route;
		}
		if(this.route.charAt(this.route.length() - 1) == '/') {
			this.route = this.route.substring(0, this.route.length() - 1);
		}
	}

	public String route = "";
	public boolean ownsAllChildRoutes = false;
	
	protected class HandleRunnable implements Runnable {
		
		public HTTPRequest req;
		public HTTPResponse res;
		public BiConsumer<HTTPRequest, HTTPResponse> callback;
		public HandleRunnable(HTTPRequest req, HTTPResponse res, BiConsumer<HTTPRequest, HTTPResponse>  callback) {
			this.req = req;
			this.res = res;
			this.callback = callback;
		}
		
		@Override
		public void run() {
			this.callback.accept(req, res);
			if(!res.wasSent) {
				res.statusCode = 500;
				res.statusMessage = "Endpoint failed";
				res.body = "Endpoint failed send a response, router has sent default response";
				res.contentType = HTTPResponse.ContentType.TEXT_PLAIN;
				res.sendResponse();
			}
		}
		
	}
	
	public final Runnable getRunnable(HTTPRequest req, HTTPResponse res) {
		return new HandleRunnable(req, res, (a, b) ->{handle(a, b);});
	}
	
	public abstract void handle(HTTPRequest req, HTTPResponse res);
}
