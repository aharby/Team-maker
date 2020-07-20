

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Server {
    int port;
    ServerSocket server=null;
    Socket client=null;
    static ExecutorService pool = null;
    
    static ArrayList<ServerThread> serverThreads = new ArrayList<>(); //to keep trace of threads
    int clientcount=0; //to have id for each client
     
    public static void main(String[] args) throws IOException {
        Server serverobj=new Server(4000);
        serverobj.startServer();

    }
    
    Server (int port){
        this.port=port;
        pool = Executors.newFixedThreadPool(100);
    }

    
    public void startServer() throws IOException {
        
        server=new ServerSocket(4000);
        System.out.println("Server Booted");
        System.out.println("waiting for connection");
        
        TeamBuild teamBuild = new TeamBuild();
        pool.execute(teamBuild);
       
        while(true)
        {
            client=server.accept();
            clientcount++;
            ServerThread serverThread = new ServerThread(client,clientcount);
            serverThreads.add(serverThread);
            pool.execute(serverThread);
        }
        
    }


}