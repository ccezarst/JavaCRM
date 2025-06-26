package ServerCore.DefaultComponents.Networking.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ServerCore.DefaultComponents.Networking.RConnection;

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
	public String body;
	public InetAddress orgIp;
	public Map<String, ArrayList<String>> headers = new HashMap<>();
	public RConnection con;
	protected HTTPParser parser;
	
	public HTTPRequest(RConnection con) {
		this.parser = new HTTPParser(con);
		this.con = con;
	}
	
	public boolean isConnectionHTTP() throws IOException {
		return this.parser.isConnectionHttp();
	}
	
	public void parse() throws IOException {
		this.parser.processRequest(this);
	}
	
	public class HTTPParser{	
		public InputStream in;
		protected RConnection con;
		private String tempBuffer = "";
		public String buffer = "";
		int currentLine = 0;
		
		public HTTPParser(RConnection c) {
			try {
				this.in = c.connectionSocket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.con = c;
		}
		
		protected String readLineFromInput() throws IOException {
		    StringBuilder line = new StringBuilder();
		
		    int prev = -1;
		    while (true) {
		        int curr = in.read();
		        if (curr == -1) {
		            // End of stream
		            break;
		        }
		
		        line.append((char) curr);
		
		        // Check for CRLF or LF or CR
		        if ((prev == '\r' && curr == '\n') || curr == '\n' ) {
		            break;
		        }
		
		        prev = curr;
		    }
		
		    String result = line.toString();
		    buffer += result;
		    currentLine += 1;
		    return result;
		}
		
		public String readLineFromBuffer(int lineNumber) {
			if (buffer == null || buffer.isEmpty()) {
	            throw new IndexOutOfBoundsException("Buffer is empty");
	        }

	        int cLine = 0;
	        int pos = 0;
	        int len = buffer.length();

	        while (pos < len) {
	            int lineEnd = pos;

	            // Find end of current line (position of first \r or \n)
	            while (lineEnd < len && buffer.charAt(lineEnd) != '\r' && buffer.charAt(lineEnd) != '\n') {
	                lineEnd++;
	            }

	            if (cLine == lineNumber) {
	                return buffer.substring(pos, lineEnd);
	            }

	            // Skip line terminator(s)
	            if (lineEnd < len) {
	                char firstTerm = buffer.charAt(lineEnd);
	                int skip = 1;
	                if (lineEnd + 1 < len) {
	                    char secondTerm = buffer.charAt(lineEnd + 1);
	                    if ((firstTerm == '\r' && secondTerm == '\n') ||
	                        (firstTerm == '\n' && secondTerm == '\r')) {
	                        skip = 2;
	                    }
	                }
	                pos = lineEnd + skip;
	            } else {
	                pos = len;
	            }

	            cLine++;
	        }

	        throw new IndexOutOfBoundsException("Line number " + lineNumber + " out of range");
		}
		
		public boolean isConnectionHttp() throws IOException {
			if(this.con == null) {
				return false;
			}
		    if (this.currentLine == 0) {
		        String read_ = this.readLineFromInput();
		
		        String[] parts = read_.split(" ");
		        if (parts.length == 3 && parts[2].startsWith("HTTP")) {
		            return true;
		        }
		    }else{
		    	String read_ = this.readLineFromBuffer(0);
		        String[] parts = read_.split(" ");
		        if (parts.length == 3 && parts[2].startsWith("HTTP")) {
		            return true;
		        }
		    }
		    return false;
		}
		protected Map<String, ArrayList<String>> getRequestHeaders() throws IOException{
			Map<String, ArrayList<String>> toReturn = new HashMap<>();
			int line = 1;
			while(true) {
				String tempLine = this.readLineFromInput().strip();
				/* try{
					tempLine = this.readLineFromBuffer(line);
				}catch(Exception e){
					tempLine = this.readLineFromInput();
				} */
				
				if(tempLine != "") {
					//System.out.println("Header:  " + tempLine);
					String[] keyValue = tempLine.split(":", 2);
					String key = keyValue[0];
					String values = keyValue[1].strip();
					if(toReturn.containsKey(key)) { // a header can contain multiple values, or be present multiple times
						ArrayList<String> temp = toReturn.get(key);
						temp.addAll((ArrayList<String>) new ArrayList<>(Arrays.asList(values.split(","))));
						toReturn.put(key, temp);
					}else {
						toReturn.put(key, (ArrayList<String>) new ArrayList<>(Arrays.asList(values.split(","))));
					}
					line += 1;
				}else {
					break;
				}
			}
			return toReturn;
		}
		
		protected String getRequestBody(Map<String, ArrayList<String>> headers) throws  IOException { // headers needed for content-length/transfer-encoding
			String toReturn = "FAILED";

			// Case 1: Both Content-Length and Transfer-Encoding (invalid combo, prefer Transfer-Encoding)
			if (headers.containsKey("Content-Length") && headers.containsKey("Transfer-Encoding")) {
				System.console().printf("Ignoring Content-Length due to Transfer-Encoding (chunked).");
				// Fall through to chunked handling
			}

			// Case 2: Content-Length
			if (headers.containsKey("Content-Length") && !headers.containsKey("Transfer-Encoding")) {
				toReturn = "";
				byte[] temp = this.con.connectionSocket.getInputStream()
						.readNBytes(Integer.parseInt(headers.get("Content-Length").get(0)));
				toReturn = new String(temp); // assuming UTF-8 or ASCII body; for binary, you’d return the raw byte[]
			}

			// Case 3: Transfer-Encoding: chunked
			else if (headers.containsKey("Transfer-Encoding")) {
				ArrayList<String> encodings = headers.get("Transfer-Encoding");
				if (encodings.stream().anyMatch(e -> e.strip().equalsIgnoreCase("chunked"))) {
					toReturn = "";
					InputStream in = this.con.connectionSocket.getInputStream();

					while (true) {
						// Read the chunk size line
						StringBuilder chunkSizeLine = new StringBuilder();
						while (true) {
							int c = in.read();
							if (c == -1) throw new IOException("Unexpected EOF while reading chunk size");
							if (c == '\r') {
								if (in.read() != '\n') throw new IOException("Invalid chunk size line ending");
								break;
							}
							chunkSizeLine.append((char) c);
						}

						// Parse the chunk size (hex)
						int chunkSize = Integer.parseInt(chunkSizeLine.toString().trim(), 16);
						if (chunkSize == 0) {
							// Final chunk, consume trailing \r\n
							if (in.read() != '\r' || in.read() != '\n') {
								throw new IOException("Invalid end of chunked body");
							}
							break;
						}

						// Read the chunk data
						byte[] chunkData = in.readNBytes(chunkSize);
						toReturn += new String(chunkData); // again, assumes text content
						// Consume \r\n
						if (in.read() != '\r' || in.read() != '\n') {
							throw new IOException("Missing CRLF after chunk data");
						}
					}
				} else {
					throw new IOException("Unsupported Transfer-Encoding: " + encodings);
				}
			}
			if(!headers.containsKey("Transfer-Encoding") && !headers.containsKey("Content-Length")){
				toReturn = "";
			}
			return toReturn;
		}
		public void processRequest(HTTPRequest req) throws IOException {
			String firstLine = "";
			try {
				firstLine = this.readLineFromBuffer(0);
			}catch(Exception e) {
				firstLine = this.readLineFromInput();
				System.console().printf("You should really be checking if the connection is HTTP before trying to parse it.");
			}
			String[] elems = firstLine.split(" ");
			switch(elems[0]) {
				case "GET":
					req.method = HTTPRequest.Methods.GET;
					break;
				case "PUT":
					req.method = HTTPRequest.Methods.PUT;
					break;
				case "HEAD":
					req.method = HTTPRequest.Methods.HEAD;
					break;
				case "POST":
					req.method = HTTPRequest.Methods.POST;
					break;
				case "DELETE":
					req.method = HTTPRequest.Methods.DELETE;
					break;
				case "CONNECT":
					req.method = HTTPRequest.Methods.CONNECT;
					break;
				case "OPTIONS":
					req.method = HTTPRequest.Methods.OPTIONS;
					break;
				case "TRACE":
					req.method = HTTPRequest.Methods.TRACE;
					break;
				case "PATCH":
					req.method = HTTPRequest.Methods.PATCH;
					break;
			}
			req.path = elems[1];
			req.protocolVersion = elems[2];
			req.orgIp = this.con.senderIP;
			req.headers = this.getRequestHeaders();
			req.body = this.getRequestBody(req.headers);
		}
	}

}
