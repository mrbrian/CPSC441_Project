package game_space;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import game_space.ReadyRoom.State;
import players.Player;
import players.Player.PlayerState;
import server.PlayerManager;
import server.SelectServer;

public class ReadyRoom{

	public enum State
	{
		NotReady,
		Beginning,
		GameInProgress,
		GameOver
	}
	
	private SelectServer server;
	private LobbyLogic_NotReady logic;
	private State state;
	private static final int NUM_PLAYERS_REQ = 2;
	private String socket;
	
	//playerList is 2-tuple string of (IP,pseudonym)
	private ArrayList<Player> playerList;
	private GameSpace game;
	private int id;
	private boolean allReady;
	
	public ReadyRoom(SelectServer s, int id){
		this.id = id;
		server = s;
		playerList = new ArrayList<Player>();
		state = State.NotReady;
	}

	public int getId(){
		return id;
	}
	
	public boolean joinRoom(Player player) {
		String[] playerInfo = {player.getIPAddress(), player.getPseudonym()};
		boolean canAdd = true;
		
		//check if name and IP are unique
		for (int i = 0; i < playerList.size(); i++) {
			Player p = playerList.get(i);
			
			if(playerInfo[0] == null){
				System.out.println("playerInfo[0] is null");
			}
			
			if(playerInfo[1] == null){
				System.out.println("playerinfo[1] is null");
			}
			
			System.out.println("Player's state: " + player.getState());
			
			if (playerInfo[0].equals(p.getIPAddress()) || playerInfo[1].equals(p.getPseudonym()))
				canAdd = false;
		}
		//add player to list
		if (canAdd) {
			playerList.add(player);
			player.setRoomIndex(id);
			player.setState(PlayerState.In_Room);
			return true;
		} else {
			return false;
		}		 
	}	
	
	public ArrayList<Player> getPlayerList() {
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
		//make game space
		game = new GameSpace(playerList);
		return game;
	}	
	
	public void changeState(State ns)
	{
		logic = null;
		
		switch (ns)
		{
			case GameInProgress:
				break;
			case Beginning:
				logic = new LobbyLogic_NotReady(this);
				break;
		}
		
		state = ns;		
	}
	
	public void update(float elapsedTime) {
		
		if (state == State.NotReady && playerList.size() == NUM_PLAYERS_REQ)
			changeState(State.Beginning);
		
		if (logic != null)
			logic.update(elapsedTime);
	}

	public void sendMessageRoom(String msg) {
		for (Player p : playerList)
		{		
			if (server == null)
				System.out.println("sendMessageRoom warning: server == null");
			else
				server.sendMessage(msg, p.getChannel());
		}
	}

	public State getState() {
		return state;
	}
}