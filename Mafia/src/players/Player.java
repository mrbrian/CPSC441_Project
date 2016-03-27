package players;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class Player {

	public enum PlayerState
	{		
		Not_Logged_In,
		Logged_In,
		In_Room,
		Reconnect_Waiting
	}
	
	private String username;
	private String pseudonym;
	
	private String IPAddress;
	private String portNumber;
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
		isAlive = false;
		new ArrayList<>();
		playerType = null;
		username = "NOT LOGGED IN";
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
	
	public void setPortNumber(String portNumber){
		this.portNumber = portNumber;
	}
	
	public String getPortNumber(){
		return portNumber;
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

	public Object getUsername() {
		return username;
	}

	public SocketChannel getChannel() {
		return channel;
	}
	
	public Player getPlayer(){
		return this;
	}

}
