package game_space;

import java.util.ArrayList;

import players.Player;
import server.PlayerManager;

public class ReadyRoom{
	
	private String socket;
	
	//playerList is 2-tuple string of (IP,pseudonym)
	private ArrayList<String[]> playerList;
	private boolean allReady;
	private GameSpace game;
	private int id;
	
	public ReadyRoom(int id){
		this.id = id;
		playerList = new ArrayList<String[]>();		
	}

	public int getId(){
		return id;
	}
	
	public boolean joinRoom(String IP, String pseudonym) {
		String[] playerInfo = {IP, pseudonym};
		boolean canAdd = true;
		
		//check if name and IP are unique
		for (int i = 0; i < playerList.size(); i++) {
			if (IP.equals(playerList.get(i)[0]) || pseudonym.equals(playerList.get(i)[1]));
				canAdd = false;
		}
		//add player to list
		if (canAdd) {
			playerList.add(playerInfo);
			return true;
		} else {
			return false;
		}		
	}
	
	
	public ArrayList<String[]> getPlayerList() {
		return playerList;
	}
	
	public GameSpace getGameSpace() {
		return game;
	}
	
	public boolean gameIsReady() {
		return allReady;
	}
	
	//done in the server?
	public GameSpace beginGame(PlayerManager plyr_mgr){
		//create player objects
		ArrayList<Player> players = new ArrayList<Player>();
		
		//create players for game
		
		for (int i = 0; i < playerList.size(); i++) {
			Player p = plyr_mgr.findPlayer(playerList.get(i)[0]);
			players.add(p);
		}
		
		//make game space
		game = new GameSpace(players);
		return game;
	}
}