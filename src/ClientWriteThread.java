import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientWriteThread implements Runnable {
	Socket connection = new Socket();
	
	 ClientWriteThread(Socket connection){
			this.connection =connection;
		}
	
	
	
	Scanner scanner = new Scanner(System.in);
 	String clientRequest;
 	
 	
	 public void run() {
	        while(true) {
	        	
	        	try {
	        		PrintStream cout=new PrintStream(connection.getOutputStream());
	        		clientRequest =scanner.nextLine();
	        		cout.println(clientRequest);
	        	}catch (IOException ex) {
	                ex.printStackTrace();
	            }
                        if ( clientRequest.equals("q") )
                        {
                           System.out.println("Connection ended by client");
                           break;
                        }
               
	    }
	        try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            scanner.close();
	 }
	 

}
