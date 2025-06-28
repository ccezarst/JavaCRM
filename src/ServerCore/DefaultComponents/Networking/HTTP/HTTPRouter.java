package ServerCore.DefaultComponents.Networking.HTTP;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	protected ExecutorService es;
	public void processRequest(HTTPRequest req) {
		synchronized(this.queue) {
			this.queue.add(new HTTPResponse(req));// the response contains a reference to the request but also internal info needed to respond
			// so it's easier to just transmit the response object
		}
	}
	
    public static Map<String, String> parseQuery(String query) {
        Map<String, String> result = new LinkedHashMap<>();
        if (query == null || query.isEmpty()) {
            return result;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String key;
            String value;
            int idx = pair.indexOf('=');
            if (idx >= 0) {
                key = pair.substring(0, idx);
                value = pair.substring(idx + 1);
            } else {
                // No '=' means key only, value is empty string
                key = pair;
                value = "";
            }

            try {
                key = URLDecoder.decode(key, "UTF-8");
                value = URLDecoder.decode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // UTF-8 should always be supported; this is just a safeguard
                throw new RuntimeException("UTF-8 encoding not supported", e);
            }

            result.put(key, value);
        }

        return result;
    }
	
	protected ArrayList<HTTPEndpoint> endpoints;
	@Override
	protected void step(ServerCore core) {
		// not so complicated code but might faster if we use ExecutorService to schedule multiple workers that route requests in parallel
		HTTPResponse res = null;
		synchronized(this.queue) {
			if(!this.queue.isEmpty()) {
				res = this.queue.remove(0);
				// grab a response of the queue(if available) then give up ownership of the queue
				// to allow a much greater inflow of requests even though we maybe can't process them fast enough
			}
		}
		if(res != null) {
			if(res.relatedRequest.headers.containsKey("Connection") && res.relatedRequest.headers.get("Connection").get(0) == "close") {
				res.closeAfterResponse = true;
			}
			String[] route = res.relatedRequest.path.split("/");
			String query = "";
			if(route[route.length-1].contains("?")) {
				query = route[route.length-1].split("\\?")[1]; // seperate the query from the last entry of the routes, so we can match them properly
				route[route.length-1] = route[route.length-1].split("\\?")[0];
			}
			for(HTTPEndpoint endpoint: this.endpoints) {
				boolean matching = true;
				String[] endpointRoute = endpoint.route.split("/");
				if(route.length >= endpointRoute.length) { // we dont need to check between /home/pageA and /home/pageB/signup etc..
					for(int i = 0; i < route.length; i++) {
						if(i < endpointRoute.length) {
							// routes assumed to be the same until proven otherwise
							if(!Objects.equals(route[i], endpointRoute[i])) {
								matching = false;
								break;
							}
						}else {
							// means that the endpoint might be a general one(if not matching so far it would have breaked, ex: route: home/login/testing, endpoint: home/login)
							if(!endpoint.ownsAllChildRoutes) {
								matching = false;
								// means even though it's included, the route has that disabled
							}
							break;
						}
					}
				}else {
					matching = false;
				}
				if(matching) {
					List<String> params = new ArrayList<>(
						    Arrays.asList(route).subList(endpointRoute.length, route.length)
					);
					res.relatedRequest.pathParameters = (ArrayList<String>) params;
					res.relatedRequest.queryParameters = parseQuery(query);
					es.execute(endpoint.getRunnable(res.relatedRequest, res));
				}else {
					res.statusCode = 404;
					res.statusMessage = "Endpoint not found";
					res.body = "Router has failed to find an endpoint for this path";
					res.contentType = HTTPResponse.ContentType.TEXT_PLAIN;
					res.sendResponse();
				}
			}
		}
	}

	@Override
	protected void update(ServerCore core) {
		queue.clear();
		endpoints = this.core.getComponentsOfType(ComponentType.HTTP_ENDPOINT, HTTPEndpoint.class);
		es = Executors.newCachedThreadPool(); // for actually running the endpoints
		
		// TODO: CHECK THE ENDPOINTS FROM CONFLICTING ROUTES!!!
	}

	@Override
	protected int test(TestingEnviromentCore core) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
