package clients;

public class Players {
	private String username;
	private String pseudonym;
	private enum PlayerType {INNOCENT, MAFIA}
	private PlayerType type;
	private boolean alive;


	public boolean getAlive() {
		return alive;
	}
	
	public void setAlive(boolean life) {
		alive = life;
	}
}