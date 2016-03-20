package clients;

import java.util.ArrayList;

public class Players {
	
	private String pseudonym;
	
	private String IPAddress;
	private String portNumber;
	
	private boolean isAlive;
	private ArrayList<String> votedWho;
	
	
	
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
	
	
	
	public Players(){
		isAlive = false;
		votedWho = new ArrayList<>();
	}
	
	public void setIPAddress(String IPAddress){
		this.IPAddress = IPAddress;
	}
	
	public String getIPAddress(){
		return IPAddress;
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
	
	public boolean getIsAlive(){
		return isAlive;
	}
}
