package ServerCore.DefaultComponents.Networking.HTTP;

import java.util.ArrayList;
import java.util.List;

import ServerCore.ServerCore;
import ServerCore.TestingEnviromentCore;
import ServerCore.DefaultComponents.ComponentType;
import ServerCore.DefaultComponents.CoreComponent;

public class HTTPRouter extends CoreComponent{

	public HTTPRouter(Boolean active, ServerCore core) {
		super("HTTPRouter", active, core, ComponentType.HTTP_ROUTER);
		// TODO Auto-generated constructor stub
	}
	
	protected ArrayList<HTTPResponse> queue = new ArrayList<>();
	
	public void processRequest(HTTPRequest req) {
		synchronized(this.queue) {
			this.queue.add(new HTTPResponse(req));// the response contains a reference to the request but also internal info needed to respond
			// so it's easier to just transmit the response object
		}
	}
	
	@Override
	protected void step(ServerCore core) {
		HTTPResponse res = null;
		synchronized(this.queue) {
			if(!this.queue.isEmpty()) {
				res = this.queue.remove(0);
				// grab a response of the queue(if available) then give up ownership of the queue
				// to allow a much greater inflow of requests even though we maybe can't process them fast enough
			}
		}
		if(res != null) {
			// COD DE TESTARE
			res.body = "Merge :)\n" + java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)) + " \n" + res.relatedRequest.path;
			res.headers.put("Salut", new ArrayList<>(List.of("Ma", "Cheama", "Mama", "Acasa")));
			if(res.relatedRequest.headers.containsKey("Connection") && res.relatedRequest.headers.get("Connection").get(0) == "close") {
				res.closeAfterResponse = true;
			}
			//closeAfterResponse = true;
			res.sendResponse();
		}
	}

	@Override
	protected void update(ServerCore core) {
		queue.clear();
		
	}

	@Override
	protected int test(TestingEnviromentCore core) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
