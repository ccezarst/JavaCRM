package ServerCore.DefaultComponents.Networking.HTTP;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ServerCore.DefaultComponents.Networking.RConnection;

public class HTTPResponse {
	
	public int statusCode = 200;
	public String statusMessage = "mi l sugi :)";
	public Map<String, ArrayList<String>> headers = new HashMap<>();
	public String body = "";
	public String HTTPVersion = "HTTP/1.1";
	public CacheControl cacheControl = CacheControl.NO_CACHE;
	public ContentType contentType = ContentType.TEXT_PLAIN;
	public boolean closeAfterResponse = false;
	public HTTPRequest relatedRequest;
	
	protected HTTPWriter writer;
	
	public HTTPResponse(HTTPRequest req) {
		this.relatedRequest = req;
		this.writer = new HTTPWriter(this);
	}
	
	public static enum ContentType {
	    APPLICATION_JSON("application/json"),
	    TEXT_HTML("text/html"),
	    TEXT_PLAIN("text/plain");

	    public final String headerValue;

	    ContentType(String headerValue) {
	        this.headerValue = headerValue;
	    }
	}

	
	public static  enum CacheControl {
	    NO_CACHE("no-cache");

	    public final String headerValue;

	    CacheControl(String headerValue) {
	        this.headerValue = headerValue;
	    }
	}
	
	public void sendResponse() {
		this.writer.sendResponse();
	}
	
	public class HTTPWriter{
		public RConnection con;
		public HTTPResponse res;
		
		public HTTPWriter(HTTPResponse res) {
			synchronized(res) {
				this.res = res;	
				this.con = res.relatedRequest.con;
			}
		}
		
		protected void writeHeaders(BufferedWriter writer) throws IOException {
			for(Map.Entry<String, ArrayList<String>> header: this.res.headers.entrySet()) {
				String key = header.getKey();
				ArrayList<String> values = header.getValue();
				writer.write(key.toString() + ": ");
				for(int i = 0; i < values.size(); i++) {
					if(i == 0) {
						writer.write(values.get(i));
					}else {
						writer.write(", " + values.get(i));
					}
				}
				writer.newLine();
			}
		}
		
		public void sendResponse() {
			OutputStream out;
			try {
				out = con.connectionSocket.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
				writer.write(HTTPVersion + " " + this.res.statusCode + " " + this.res.statusMessage);
				writer.newLine();
				headers.put("Server", new ArrayList<>(List.of("ccezarst JServerFramework")));
				headers.put("Date", new ArrayList<>(List.of(java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)))));
				headers.put("Cache-Control", new ArrayList<>(List.of(this.res.cacheControl.headerValue)));
				headers.put("Content-Type", new ArrayList<>(List.of(this.res.contentType.headerValue)));
				headers.put("Content-Length", new ArrayList<>(List.of(String.valueOf(body.getBytes(StandardCharsets.UTF_8).length))));
				this.writeHeaders(writer);
				writer.newLine();
				writer.write(body);
				// do not close the connection
				writer.flush();
				if(this.res.closeAfterResponse) {
					this.con.connectionSocket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
