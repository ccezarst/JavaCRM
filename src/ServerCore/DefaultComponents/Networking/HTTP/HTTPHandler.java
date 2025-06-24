
package ServerCore.DefaultComponents.Networking.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import ServerCore.DefaultComponents.Networking.RConnection;

public class HTTPHandler {
	
	public class HTTP_Parser{
		
		public InputStream in;
		protected RConnection con;
		private String tempBuffer = "";
		public String buffer = "";
		int currentLine = 0;
		
		public HTTP_Parser(RConnection c) {
			try {
				this.in = c.connectionSocket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.con = c;
		}
		
		protected String readLineFromInput() throws IOException {
			String temp = tempBuffer; // in the case that tempBuffer isn't empty
			tempBuffer = ""; // reset tempBuffer
			while(true) {
				if(in.available() != 0) {
					temp += (char) in.read();
					if(temp == "\n" || temp == "\r") {
						char tempB = (char)in.read();
						if(tempB == '\n' || tempB == '\r') { // in case of \n\r or \r\n
							temp += tempB;
							buffer += temp;
							currentLine += 1;
							return temp;
						}else {
							tempBuffer += tempB; // if the following is a normal character, 
							buffer += temp;
							currentLine += 1;
							return temp;
						}
					}
				}else {
					buffer += temp;
					currentLine += 1;
					return temp;
				}
			}
		}
		
		public String readLineFromBuffer(int lineNumber) {
			if (buffer == null || buffer.isEmpty()) {
	            throw new IndexOutOfBoundsException("Buffer is empty");
	        }

	        int currentLine = 0;
	        int pos = 0;
	        int len = buffer.length();

	        while (pos < len) {
	            int lineEnd = pos;

	            // Find end of current line (position of first \r or \n)
	            while (lineEnd < len && buffer.charAt(lineEnd) != '\r' && buffer.charAt(lineEnd) != '\n') {
	                lineEnd++;
	            }

	            if (currentLine == lineNumber) {
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

	            currentLine++;
	        }

	        throw new IndexOutOfBoundsException("Line number " + lineNumber + " out of range");
		}
		
		public boolean isConnectionHttp() throws IOException {
			if(this.currentLine == 0) {
				String read_ = this.readLineFromInput();
				if(read_.split(" ").length == 3 || read_.split(" ")[2].contains("HTTP")) {
					return true;				
				}
			}
			return false;
		}
		
		public HTTPRequest proccessRequest() throws IOException {
			String firstLine = "";
			HTTPRequest toReturn = new HTTPRequest();
			try {
				firstLine = this.readLineFromBuffer(0);
			}catch(Exception e) {
				firstLine = this.readLineFromInput();
				System.console().printf("You should really be checking if the connection is HTTP before trying to parse it.");
			}
			String[] elems = firstLine.split(" ");
			switch(elems[0]) {
				case "GET":
					toReturn.method = HTTPRequest.Methods.GET;
					break;
				case "PUT":
					toReturn.method = HTTPRequest.Methods.PUT;
					break;
				case "HEAD":
					toReturn.method = HTTPRequest.Methods.HEAD;
					break;
				case "POST":
					toReturn.method = HTTPRequest.Methods.POST;
					break;
				case "DELETE":
					toReturn.method = HTTPRequest.Methods.DELETE;
					break;
				case "CONNECT":
					toReturn.method = HTTPRequest.Methods.CONNECT;
					break;
				case "OPTIONS":
					toReturn.method = HTTPRequest.Methods.OPTIONS;
					break;
				case "TRACE":
					toReturn.method = HTTPRequest.Methods.TRACE;
					break;
				case "PATCH":
					toReturn.method = HTTPRequest.Methods.PATCH;
					break;
			}
			toReturn.path = elems[1];
			toReturn.protocolVersion = elems[2];
			toReturn.orgIp = this.con.senderIP;
			return toReturn;
		}
	}
}
