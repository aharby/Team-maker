import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServerThread implements Runnable {
        
        Socket client=null;
        String userName;
        BufferedReader cin;
        PrintStream cout;
        int id;
        int rank;
        Integer teamId= null;        
        CopyOnWriteArrayList<Integer> team = new CopyOnWriteArrayList<>();
        String script;
        String character = null;
        boolean chChoosingTO = false; //check if choosing timed out
        
        ServerThread(Socket client, int count  )  {
            
            this.client=client;
            this.id=count;
            System.out.println("Connection "+id+"established with client "+client);
            
            try {
				cin=new BufferedReader(new InputStreamReader(client.getInputStream()));
	            cout=new PrintStream(client.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}

        }
        
        void setTeamId (int teamId) {
        	this.teamId = teamId;
        }
        
        void setTeam(CopyOnWriteArrayList<Integer> team) {
			 this.team =team;
		}
        
        void setUserName (String userName) {
        	this.userName = userName;
        }
        
        String getUserName () {
        	return this.userName;
        }
        
        int getRank () {
        	return this.rank;
        }
        
        public void run() {
        	//registration
        	rank = LoginSystem.Login(this);

        	try {
        		String startOver = null;
	        	do { 
		            cout.println("your current rank is ("+ rank+ ") Please wait for other player");
		            
		            //team building
					TeamBuild.addPlayer(id, rank);

					// wait for setting a team
					while (true) {
						if(teamId!= null) break;
						Thread.sleep(500);} 
					
					CopyOnWriteArrayList<String> teamMates = getTeamMates(team);
					
					cout.println("Team formed!");
					cout.println("Now you are in a team with " +teamMates.get(0)+ " and "+ teamMates.get(1)+ "\n");

					//displaying script to be performed
					script = TeamBuild.teams.get(teamId).getScript();
					cout.println("Script"+ script+ " is your script to be performed according to your team average rank \n");
					cout.println("performance will start after every team member choses a character to play");
					cout.println("you have to choose a character that is not chosen by anyone else");
					cout.println("if you didn't choose in 1 minute, you will be assigned a randomly \n");

					//character choose
					 class ChooseChTask extends Thread {
				        	
				        	public void run() {
				        		 character = chooseCh();
								  cout.println("you have chosen character"+ character);

				        	}
				        }
				        
					 ChooseChTask task = new ChooseChTask();
					FutureTask<String> futureTask = new FutureTask<>(task, "Task completed");
						ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
					    executorService.execute(futureTask);
					    
					    try {
							futureTask.get(60, TimeUnit.SECONDS);

						} catch (TimeoutException e ) {
							futureTask.cancel(true);
							chChoosingTO= true;
							cout.println("time out");
							cout.println("your character is character"+ assignAvailableCh());

						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					    executorService.shutdownNow();

					//check if other players in the team made their choice 
					while (true) {
						if(checkIfOthersChose()) break;
						Thread.sleep(100);
					}
		
					//performance (set a random new rank)
					cout.println("Performance started!");
					Random rand = new Random();
					int newRank = rand.nextInt(5)+1;
					LoginSystem.setNewRank(userName, newRank);
					cout.println("your new rank is "+ newRank+ "\n");
					rank = newRank;
					
					//resetting teamId
					teamId = null;
					
					//game over, start a new game?
					cout.println("Game Over! \n");

					if (chChoosingTO== true) {
						cout.println("to PROCEED enter 1"); //to cancel inputStream block since ch choosing timed out
						cout.println("please note that because of chosing ch time out if you didn't press 1"); 
						cout.println("program will not work as expected. After that,"); 
					}
					
					cout.println("to start a new game press 1, to exit press any key");
					startOver = cin.readLine();

	        	 }while (startOver.equals("1"));
	        	
	        	//session end
				 cout.println("bye!");
				 System.out.println("connection"+id+" ended by client.");
				 cout.close();
				 cin.close();
				 client.close();
				 
        	}catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			
        	
        }//end of run method
        
        //get team mates user names for a player
        CopyOnWriteArrayList<String> getTeamMates ( CopyOnWriteArrayList<Integer> team){
        	CopyOnWriteArrayList<String> teamMates = new CopyOnWriteArrayList<>();
        	for(int i :team) {
				if (i!=id) 	teamMates.add(Server.serverThreads.get(i-1).getUserName());
			}
        	return teamMates;
        }
        

       
        String chooseCh ()  {
        	   	
			Set<String> characters = Collections.emptySet();
			Collections.addAll(characters = new HashSet<String>(), "1", "2", "3");
			        			
			String chosenCh= "";
				
			boolean chosen = false;
			
			try {
				cout.println("choose a character:"+ characters);
				do{
				    String ch = cin.readLine();
				    if(characters.contains(ch)) {
				    	if(!TeamBuild.teams.get(teamId).checkIfChosen(ch)) {
				    		chosenCh = ch;
				    		TeamBuild.teams.get(teamId).addChosenCh(ch);
				    		chosen = true;
				    		for(Integer i : team) 
				    			if(i!=id) Server.serverThreads.get(i-1).cout.println(userName+" chose character"+ch);}
				    	
				    	else cout.println("already chosen Ch, choose another one");
				    }
				    else cout.println("try again");		
				    
				} while (chosen != true);
				
			}catch (IOException e) { e.printStackTrace();}
			
			return chosenCh;
        }//end of chooseCh method
        
        
        
        String assignAvailableCh() {
        	String ch=null;
        	Set<String> characters = Collections.emptySet();
			Collections.addAll(characters = new HashSet<String>(), "1", "2", "3");
			
			for (String s : characters)
		    	if(!TeamBuild.teams.get(teamId).checkIfChosen(s)) {
		    		ch = s;
		    		TeamBuild.teams.get(teamId).addChosenCh(ch);
		    		for(Integer i : team) 
		    			if(i!=id) 
		    				Server.serverThreads.get(i-1).cout.println(userName+" chose character"+ch);
		    		break;
		    	}
			return ch;
        }
        
        
        boolean checkIfOthersChose () {
        	if (TeamBuild.teams.get(teamId).chosenCh.size() ==3) return true;
        	return false;
        }
        
        

    }//end of ServerThread