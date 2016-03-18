package clients;

import java.util.ArrayList;

public class Players {
	
	private String pseudonym;
	private boolean isAlive;
	private PlayerTypes.PlayerType type;
	private ArrayList<String> votedWho;
	
	public Players(){
		isAlive = false;
		votedWho = new ArrayList<>();
		type = null;
	}
	
	public void setType(PlayerTypes.PlayerType type){
		this.type = type;
	}
	
	public PlayerTypes.PlayerType getType(){
		return type;
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
