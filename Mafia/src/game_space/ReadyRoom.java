package game_space;

import java.nio.channels.SocketChannel;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import client.ClientPacket;
import game_space.ReadyRoom.State;
import players.Player;
import players.Player.PlayerState;
import server.Outbox;
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
	
	public final int NUM_PLAYERS_REQ;
	private SelectServer server;
	private LobbyLogic logic;
	private State state;
	private static final int NUM_PLAYERS_REQ_DEFAULT = 3;
	//playerList is 2-tuple string of (IP,pseudonym)
	private ArrayList<Player> playerList;
	private GameSpace game;
	private int id;
	private boolean allReady;

	public ReadyRoom(SelectServer s, int id, int roomsize){
		NUM_PLAYERS_REQ = roomsize;
		this.id = id;
		server = s;
		playerList = new ArrayList<Player>();
		changeState(State.NotReady);
	}

	public ReadyRoom(SelectServer s, int id){
		this(s, id, NUM_PLAYERS_REQ_DEFAULT);
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
	public GameSpace beginGame(){	
		//make game space
		game = new GameSpace(playerList);
		return game;
	}		
	
	public void processPackets(ClientPacket p, Player player)
	{
		logic.processPacket(p, player);
	}
	
	public void changeState(State ns)
	{
		logic = null;
		
		switch (ns)
		{
			case NotReady:
				logic = new LobbyLogic_NotReady(this, playerList);
				break;
			case GameInProgress:
				beginGame();
				logic = new LobbyLogic_GameInProgress(this, game);
				break;
			case Beginning:
				logic = new LobbyLogic_Beginning(this);
				break;
		}
		
		state = ns;		
	}
	
	public void update(float elapsedTime) {
		
		if (logic != null)
			logic.update(elapsedTime);
	}

	public void sendMessageRoom(String msg) {
		for (Player p : playerList)
		{		
			if (!p.isConnected())
				continue;
			if (server == null)
				System.out.println("sendMessageRoom warning: server == null");
			else
				Outbox.sendMessage(msg, p.getChannel());
		}
	}

	public State getState() {
		return state;
	}

	public ArrayList<SocketChannel> getSocketChannelList() {
		ArrayList<SocketChannel> result = new ArrayList<SocketChannel>();
		
		for(Player p : playerList)
			result.add(p.getChannel());
		
		return result;
	}
}