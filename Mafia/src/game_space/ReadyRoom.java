package game_space;

import java.util.ArrayList;

import clients.Player;

public class ReadyRoom{
	
	private String socket;
	
	//playerList is 2-tuple string of (IP,playerName)
	private ArrayList<String[]> playerList;
	private boolean allReady;
	private GameSpace game;
	
	public ReadyRoom(){
		playerList = new ArrayList<String[]>();
	}
	
	public boolean joinRoom(String IP, String playerName) {
		String[] playerInfo = {IP, playerName};
		boolean canAdd = true;
		
		//check if name and IP are unique
		for (int i = 0; i < playerList.size(); i++) {
			if (IP.equals(playerList.get(i)[0]) || playerName.equals(playerList.get(i)[1]));
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
	public GameSpace beginGame(){
		//create player objects
		ArrayList<Player> players = new ArrayList<Player>();
		
		Player p;
		
		//create players for game
		for (int i = 0; i < playerList.size(); i++) {
			p = new Player();
			p.setIPAddress(playerList.get(i)[0]);
			players.add(p);
		}
		
		//make game space
		game = new GameSpace(players);
		return game;
	}
}