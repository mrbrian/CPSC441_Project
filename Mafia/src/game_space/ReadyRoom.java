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
		GameOver,
		Paused
	}
	
	public final int NUM_PLAYERS_REQ;
	private SelectServer server;
	private LobbyLogic logic;
	private State state;
	
	private static final int NUM_PLAYERS_REQ_DEFAULT = 4;
	//playerList is 2-tuple string of (IP,pseudonym)
	private ArrayList<Player> playerList;
	private ArrayList<Player> observerList;
	private GameSpace game;
	private int id;
	private boolean allReady;
	private ArrayList<String> banList;
	private double pauseOffset = 0.0;

	public ReadyRoom(SelectServer s, int id, int roomsize){
		NUM_PLAYERS_REQ = roomsize;
		this.id = id;
		server = s;
		playerList = new ArrayList<Player>();
		observerList = new ArrayList<Player>();
		banList = new ArrayList<String>();
		changeState(State.NotReady);
	}
	
	public double getPauseOffset() {
		return pauseOffset;
	}
	
	public void setPauseOffset(double t) {
		pauseOffset = t;
	}
	
	public void updatePauseOffset(double t) {
		pauseOffset += t;
	}
	

	public ReadyRoom(SelectServer s, int id){
		this(s, id, NUM_PLAYERS_REQ_DEFAULT);		
	}

	public int getId(){
		return id;
	}
	
	// returns true if not a duplicate
	public boolean checkDupe(Player player)
	{
		String name = player.getUsername();
		String ip = player.getIPAddress().toString();
		
		boolean result = true;
		
		for (int i = 0; i < playerList.size(); i++) {
			Player p = playerList.get(i);
			
			if(name == null){
				System.out.println("playerInfo[0] is null");
			}
			
			if(ip == null){
				System.out.println("playerinfo[1] is null");
			}
			
			System.out.println("Player's state: " + player.getState());
			
			if (name.equals(p.getIPAddress()) || ip.equals(p.getPseudonym()))
				result = false;
		}
		
		return result;
	}
	
	//return false if in banList
	public boolean checkBanList(Player player)
	{
		String name = player.getUsername();
		String ip = player.getIPAddress().toString();
		
		boolean result = true;
		
		for (int i = 0; i < banList.size(); i++) {
			String banName = banList.get(i);
			
			if (name.equals(banName))
			{
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	public boolean joinRoom(Player player) {
		String[] playerInfo = {player.getIPAddress(), player.getPseudonym()};
		boolean canAdd = checkDupe(player);
		if (canAdd)
			canAdd = checkBanList(player);
		
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
	
	public ArrayList<Player> getObserverList() {
		return observerList;
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
				logic = new LobbyLogic_GameInProgress(this, game);
				break;
			case Beginning:
				beginGame();
				logic = new LobbyLogic_Beginning(this, game);
				break;
			case GameOver:
				logic = new LobbyLogic_GameOver(this);
				break;
			case Paused:
				logic = new LobbyLogic_Paused(this, game);
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
			Outbox.sendMessage(msg, p.getChannel());
		}

		for (Player p : observerList)	// send to observers too
		{		
			if (!p.isConnected())
				continue;
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
		
		for(Player p : observerList)
			result.add(p.getChannel());
		
		return result;
	}

	public void observeRoom(Player player) {
		player.setState(PlayerState.In_Room);
		player.setRoomIndex(this.id);
		observerList.add(player);
	}

	public ArrayList<Player> getObservers() {
		return observerList;
	}

	public void banUser(Player banPlayer) {		
		banList.add(banPlayer.getUsername());
		banPlayer.leaveRoom();
	}

	public Player findPlayer(String username) 
	{
		for (Player p : playerList)
		{
			if (p.getUsername().equals(username))
				return p;		
		}
		return null;
	}
}