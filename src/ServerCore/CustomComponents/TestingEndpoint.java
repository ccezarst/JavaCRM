package ServerCore.CustomComponents;

import ServerCore.ServerCore;
import ServerCore.TestingEnviromentCore;
import ServerCore.DefaultComponents.Networking.HTTP.HTTPEndpoint;
import ServerCore.DefaultComponents.Networking.HTTP.HTTPRequest;
import ServerCore.DefaultComponents.Networking.HTTP.HTTPResponse;

public class TestingEndpoint extends HTTPEndpoint {

	public TestingEndpoint(Boolean active, ServerCore core) {
		super("TestingHTTPendpoint", active, core, "testing");
		this.ownsAllChildRoutes = true;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(HTTPRequest req, HTTPResponse res) {
		res.bodyPrintln("Worked :)");
		res.bodyPrintln(req.path);
		res.bodyPrintln(req.pathParameters.toString());
		res.bodyPrintln(req.queryParameters.toString());
		//res.sendResponse();
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
