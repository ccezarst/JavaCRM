package ServerCore.DefaultComponents.Networking.HTTP;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {
	public static enum Methods{
		GET,
		POST,
		PUT,
		HEAD,
		DELETE,
		CONNECT,
		OPTIONS,
		TRACE,
		PATCH
	}
	
	public String path;
	public String protocolVersion;
	public Methods method;
	public InetAddress orgIp;
	public Map<String, String> headers = new HashMap<>();
}
