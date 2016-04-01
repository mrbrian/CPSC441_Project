package players;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import game_space.ReadyRoom;
import server.RoomManager;

public class Player {

	public enum PlayerState
	{		
		Not_Logged_In,
		Logged_In,
		In_Room,
		Disconnecting
	}
	
	private String username;
	private String pseudonym;
	
	private String IPAddress;
	private SocketChannel channel;
	private PlayerTypes.PlayerType playerType;
	
	private PlayerState state;
	private int roomIndex;  
	private boolean isAlive;
	/*
	 *	Client initiates	 
	 * 		- Reply from server
	 * 		
	 * 	Server asks for
	 * 		- Authentication details
	 * 		- Display to player
	 *  
	 *  Connection related things
	 *  
	 *  Debug info 
	 *  	- 
	 *  
	 * 
	 * 
	 * */
		
	public Player(SocketChannel sc){
		
		channel = sc;
		
		try
		{
			this.IPAddress = sc.getRemoteAddress().toString();
		}
		catch (Exception e)
		{
			this.IPAddress = String.format("invalid player ipaddress: %s", e.getMessage());
		}
		
		state = PlayerState.Not_Logged_In;
		roomIndex = -1;
		isAlive = true;
		new ArrayList<>();
		playerType = null;
		username = "NOT_LOGGED_IN";
	}
	
	public void setPlayerType(PlayerTypes.PlayerType playerType){
		this.playerType = playerType;
	}
	
	public PlayerTypes.PlayerType getPlayerType(){
		return playerType;
	}

	public String getIPAddress(){
		return IPAddress;
	}

	public void setIPAddress(String s){
		IPAddress = s;
	}
	
	// Set the players pseudonym 
	public void setPseudonym(String pseudonym){
		this.pseudonym = pseudonym;
	}
	
	// Get the players pseudonym
	public String getPseudonym(){
		return pseudonym;
	}
	
	public void setIsAlive(boolean isAlive){
		this.isAlive = isAlive;
	}
	
	public void setRoomIndex(int v){
		this.roomIndex = v;
	}
	
	public int getRoomIndex(){
		return this.roomIndex;
	}

	public boolean getIsAlive(){
		return isAlive;
	}

	public void setUsername(String u) {
		username = u;
	}
	
	public void setState(PlayerState s)
	{
		state = s;
	}

	public PlayerState getState()
	{
		return state;
	}

	public String getUsername() {
		return username;
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public Player getPlayer(){
		return this;
	}
	
	public boolean isConnected(){
		return (channel != null);
	}

	public void setSocketChannel(SocketChannel ch) {
		channel = ch;
		if (ch == null)
			return;
		try{
			SocketAddress addr = ch.getRemoteAddress();
			setIPAddress(addr.toString());
		}
		catch(IOException e)
		{
			setIPAddress("NOT VALID: " + e.getMessage());
		}
	}	
	
	public String stateString(){
		String result = String.format("Username: %s\nPseudonym: %s\nIP addr: %s\nChannel: %s\nPlayerType: %s\nState: %s\nRoomIndex: %d\nisAlive: %s\n",
				username,
				pseudonym,
				IPAddress, 
				channel,
				playerType,
				state,
				roomIndex,
				isAlive);	
		return result;
	}

	public void leaveRoom() {
		ReadyRoom room = RoomManager.getInstance().findRoom(roomIndex);
		if (room != null)
		{
			room.getPlayerList().remove(this);
			roomIndex = -1;		
			state = PlayerState.Logged_In;
		}
	}
}
