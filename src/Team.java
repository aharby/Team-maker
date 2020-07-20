
import java.util.concurrent.CopyOnWriteArrayList;

public class Team {
	
	int teamId;
	CopyOnWriteArrayList<Integer> team = new CopyOnWriteArrayList<>();

	CopyOnWriteArrayList<String> chosenCh = new CopyOnWriteArrayList<>();
	
	double teamAverageRank;
	
	String teamScript;

	Team(CopyOnWriteArrayList<Integer> team, int teamId){
		this.teamId = teamId;
	}
	
	void setTeamAverageRank(double avRank) {
		this.teamAverageRank = avRank;
	}
	
	 void addChosenCh (String ch) {
		chosenCh.add(ch);
	}
	
	synchronized boolean checkIfChosen (String ch) {
		return chosenCh.contains(ch);
	}
	 

	synchronized String getScript () {
		 return String.valueOf(Math.round(this.teamAverageRank));
	 }
	 
}
