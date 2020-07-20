import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class TeamBuild implements Runnable {
	
	 static Map<Integer, Integer> players = new ConcurrentHashMap<>(); //<playerId, rank>
	//	static Set<CopyOnWriteArrayList<Integer>> teamsBuilt = Collections.newSetFromMap(new ConcurrentHashMap<CopyOnWriteArrayList<Integer>, Boolean>());
	 
	 static CopyOnWriteArrayList<Team> teams = new CopyOnWriteArrayList<Team>();
	static int teamId=0;
	
	
	public void run() {

	 
		while (true) {build(players);}
		
	}
	
	
	public static void build (Map<Integer, Integer> players) {

		CopyOnWriteArrayList<Integer> team = new CopyOnWriteArrayList<>(); //team that is being built
  		
		for (  Integer i : players.keySet()) {

			team.add(i);
			
			for ( Integer   j : players.keySet() ) {	
 
				if (j != i ) {			
					if (players.get(j)== players.get(i)) {
						team.add(j);
						if (team.size() == 3) break;
					}
					else if ( (players.get(j)== (players.get(i)+1)) || 
							(players.get(j)== (players.get(i)-1)) ) {
						team.add(j);
						if (team.size() == 3) break;
					}
					else if (team.size()==2) {
						if ( ( (team.get(1) == (team.get(0)+1)) && (players.get(j) == (team.get(0)+2)) )
								|| ((team.get(1) == (team.get(0)-1))&& (players.get(j) == (team.get(0)-2) ))) {
							team.add(j);
							if (team.size() == 3) break;
						}
						
					}
				}
			}
			
			if (team.size() == 3) {
				CopyOnWriteArrayList<Integer> teamBuilt = new CopyOnWriteArrayList<>(team) ;
				
				//setTeam(teamBuilt);
				Team teamObject =new Team(teamBuilt,teamId);
 				teams.add(teamObject);
 				double teamAvRank =0;
				for (Integer k :teamBuilt) {teamAvRank+= Double.valueOf(Server.serverThreads.get((k-1)).getRank())/3;}
				teamObject.setTeamAverageRank(teamAvRank);

				for (Integer k :teamBuilt) {
					Server.serverThreads.get((k-1)).setTeam(teamBuilt);
					Server.serverThreads.get((k-1)).setTeamId(teamId);
					players.remove(k);
				}
 				System.out.println("team " +teamBuilt+ " was built");

				team.clear();
 				teamId++;
 				//Server.pool.execute(choose);
				
				break;
			}
			team.clear();
		}

	}// end of build method

	
	
	public static void addPlayer (Integer playerId, Integer rank) {
		players.put(playerId, rank);
	}
	

}
