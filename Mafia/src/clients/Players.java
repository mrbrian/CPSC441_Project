package clients;

public class Players {
	
	private String pseudonym;
	private String playerType;
	private int lynchCount;
	
	public Players(){
		lynchCount = 0;
	}
	
	// Set the players pseudonym 
	public void setPseudonum(String pseudonym){
		this.pseudonym = pseudonym;
	}
	
	// Set the players type (for now, just Innocent or Mafioso)
	public void setPlayerType(String playerType){
		this.playerType = playerType;
	}
	
	// Increase the lynch vote for the player
	public void increaseLynchCount(){
		lynchCount++;
	}
}
