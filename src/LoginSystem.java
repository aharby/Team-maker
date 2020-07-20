import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

 

public class LoginSystem {
	
	static Map<String, Integer> usersRanks =  new ConcurrentHashMap<>();
	static Map<String, String> credentials =  new ConcurrentHashMap<>();
	
	public static Integer Login (ServerThread client) {
		
		BufferedReader cin = client.cin;
		PrintStream cout = client.cout;
		
		String userName,  password;
		Integer rank =0;
		
		String signUpOrIn;
		
		boolean loggedInSuccessfully  ;
		
		try{
			do { 
	

			cout.println("to create new account press 1 , to login press any key");
			signUpOrIn = cin.readLine();

			cout.println("enter user name");
			userName = cin.readLine();
			
			cout.println("enter password");
			password = cin.readLine();
			
	
			if  (signUpOrIn.contentEquals("1") ) { 
					if (!credentials.keySet().contains(userName)) {
					client.setUserName(userName);
					int newUserRank = 1;
					credentials.put(userName, password);
					usersRanks.put(userName, newUserRank);
					cout.println("you have been registered!");
					rank = newUserRank;
					loggedInSuccessfully = true;
					//rank = RegisterNewUser(loginCredentials,cout);
						}
					else {cout.println("user name already exists please try again");
						loggedInSuccessfully = false;}
					}
					
			else {
				
				
					if (password.contentEquals(credentials.get(userName))) {
 						loggedInSuccessfully = true;
 					    cout.println("Logged in!");
 					    rank = usersRanks.get(userName);

					}
					else {
						loggedInSuccessfully = false;
						cout.println("user name or password is incorrect!");
						}
			}
					
			}while (loggedInSuccessfully != true);
		}catch (IOException e) { e.printStackTrace();}

		return rank;
		
}// end of login method

		
	public static void setNewRank (String userName, int newRank) {
		usersRanks.put(userName, newRank);
	}
	
	
} //End of Register class

