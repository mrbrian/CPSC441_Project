package game_space;

public class Players {
	
	private String pseudonym;	
	private PlayerTypes.PlayerType playerType;	
	private boolean isAlive;

	public Players(){
		isAlive = false;
	}
	
	public void setPlayerType(PlayerTypes.PlayerType playerType){
		this.playerType = playerType;
	}
	
	public PlayerTypes.PlayerType getPlayerType(){
		return playerType;
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
