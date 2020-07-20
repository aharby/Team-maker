
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class Client {
    
    public static void main(String args[]) throws Exception
	{
		Socket connection =new Socket("127.0.0.1",4000);
		System.out.println("connection established, to quit at any time press 'q' ");
		BufferedReader cin=new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		String serverMsg;
		
		/*writer thread that is able to handle to read a msg from client and write 
		 * it to server
		 * since io operations are blocking, it is better to have them independent*/
		
        ClientWriteThread write = new ClientWriteThread(connection);
        Executor executor = Executors.newSingleThreadScheduledExecutor();
        executor.execute(write);
		
		while (true )
		{
			serverMsg =cin.readLine(); //read
			 if ( serverMsg.equals("q") )
             {
                System.out.println("Session ended");
                break;
             }
			System.out.println(serverMsg);
			
		}
		 connection.close();
		 cin.close();
	}//end of main
    
}//end of Client class 