package game_space;

import java.util.Random;

import players.Player;
import players.PlayerTypes;

import java.util.ArrayList;

public class GameSpace{
	private Random randGen = new Random();
	private Player lynchVictim = null;
	private Player murderVictim = null;
	private boolean lynchOngoing;
	private boolean murderOngoing;
	private boolean canLynch;
	private boolean canMurder;
	private int lynchCount = 0;
	private int murderCount = 0;
	private long dayTime = 300000;		//300000ms = 5min
	private long nightTime = 150000;	//150000ms = 2.5min
	private long voteTime = 60000; // = 1min
	private long switchTime = 0;
	private long voteBegin = 0;
	
	private ArrayList<Player> players = null;
	private ArrayList<Player> innocent = null;
	private ArrayList<Player> mafioso = null;
	private ArrayList<Player> graveyard = null;
	private int mafiaFraction = 3;			//fraction = 1/mafiaFraction
	private enum gameState {DAY, NIGHT};
	private gameState currentState = gameState.DAY;
	
	public GameSpace(ArrayList<Player> connected) {
		players = connected;
		graveyard = new ArrayList<Player>(connected.size());
	}

	public ArrayList<Player> assignMafia() {
		int numOfMafia = (players.size() / mafiaFraction);
		if (numOfMafia == 0)
			numOfMafia = 1;
			
		mafioso = new ArrayList <Player>(numOfMafia);
		innocent = new ArrayList <Player>(players.size() - numOfMafia);
			
		for (int i = 0; i < numOfMafia; i++) {
			int newMafia = randGen.nextInt(players.size());
			players.get(newMafia).setPlayerType(PlayerTypes.PlayerType.MAFIA);
			mafioso.add(players.get(newMafia));
		}
		return mafioso;
	}
	
	public ArrayList<Player> assignInnocent() {
		for (int i = 0; i < players.size(); i++) {			//cycle through players and add to innocent if not part of mafioso
			if (mafioso.contains(players.get(i)) == false) {
				players.get(i).setPlayerType(PlayerTypes.PlayerType.INNO);
				innocent.add(players.get(i));
			}
		}
		return innocent;
	}
	
	public int updateState(long callTime) {
		if (switchTime == 0) {
			switchTime = callTime;
			nextDay();
			return 1;
		}
		if (currentState == gameState.DAY) {
			if (switchTime + dayTime <= callTime) {
				switchTime = callTime;
				nextNight();
				return 0;
			}
		}
		if (currentState == gameState.NIGHT) {
			if (switchTime + nightTime <= callTime) {
				switchTime = callTime;
				nextDay();
				return 1;
			}
		}
		return -1;
	}
	
	public void nextDay() {
		voteReset();
		currentState = gameState.DAY;
		canLynch = true;
	}
	
	public void nextNight() {
		voteReset();
		currentState = gameState.NIGHT;
		canMurder = true;
	}
	
	public void voteReset() {
		lynchVictim = null;
		murderVictim = null;
		lynchOngoing = false;
		murderOngoing = false;
		//canLynch = false;
		//canMurder = false;
		lynchCount = 0;
		murderCount = 0;
		voteBegin = 0;
	}
	
	public boolean checkVote(long votePing) {
		if (voteBegin == 0 && (lynchOngoing || murderOngoing == true)) {
			voteBegin = voteTime;
		}
		else {
			if (votePing >= voteBegin + voteTime) {
				voteReset();
				return true;
			}
		}
		return false;
	}
	public Player checkVictim(String victimPseudonym) {
		Player victim = null;
		for(Player current : players){
			if(current.getPseudonym().toString().equals(victimPseudonym))
				victim = current;
		}
		return victim;
	}
	
	public Player lynchVote(Player lyncher, Player victim) {		
		if (canLynch == true) {
			if (lynchVictim == null && lynchOngoing == false) {
				lynchVictim = victim;
				lynchCount++;
				lynchOngoing = true;
				
				//System.out.println("lyncher is: " + lyncher.getPseudonym().toString());
				//System.out.println("victim is: " + lynchVictim.getPseudonym().toString());
				
				//System.out.println("lynch count: " + lynchCount);
				
				//System.out.println(lyncher + " has begun a vote to lynch " + victim + " ["lynchCount "/" players.size() + "]");
			}
			else if (victim != lynchVictim && lynchOngoing == true) {
				//System.out.println("Only one lynch vote may be ongoing at a time");
			}
			else {
				lynchCount++;
				//System.out.println(lyncher + " has voted to lynch " + victim + " ["lynchCount "/" players.size() + "]");
			}
		}
		System.out.println("Victim is: " + victim.getPseudonym().toString());
		return victim;
	}
	
	public Player lynchCheck() {
		if (lynchCount > ((innocent.size() + mafioso.size())/2)) {
			kill(lynchVictim);
			return lynchVictim;
		}
		else
			return null;
	}

	public Player murderVote(Player murderer, Player victim) {		
		if (canMurder == true && mafioso.contains(murderer) == true && victim != null) {
			if (murderVictim == null && murderOngoing == false) {
				murderVictim = victim;
				murderCount++;
				murderOngoing = true;
				//System.out.println(murderer + " has begun a vote to murder " + victim + " ["murderCount "/" mafioso.size() + "]");
			}
			else if (victim != murderVictim && murderOngoing == true) {
				//System.out.println("Only one murder vote may be ongoing at a time");
			}
			else {
				murderCount++;
				//System.out.println(murderer + " has voted to murder " + victim + " ["murderCount "/" players.size() + "]");
			}
		}
		
		return victim;
	}
	
	public Player murderCheck() {
		if (murderCount > (mafioso.size()/2)) {
			kill(murderVictim);
			return murderVictim;
		}
		else
			return null;
	}	
	
	public void kill(Player condemned) {
		if (innocent.contains(condemned) == true){
			innocent.remove(condemned);
		}
		if (mafioso.contains(condemned) == true){
			mafioso.remove(condemned);
		}
		condemned.setIsAlive(false);
		graveyard.add(condemned);
	}
	
	public gameState getState() {
		return currentState;
	}
	
	public int checkWin() {
		if(mafioso.isEmpty() == true)
			return 0;
		else if (mafioso.size() > innocent.size())
			return 1;
		else
			return -1;
	}
	
	//returns an ArrayList of players with whom the player can speak based on current state, less the player
	public ArrayList <Player> whoCanChatWith(Player speaker) {	
		ArrayList<Player> listeners = new ArrayList<Player>();
		
		if (speaker.getIsAlive() == false)
			listeners.addAll(graveyard);
		
		else if (currentState == gameState.DAY) {
			listeners.addAll(innocent);
			listeners.addAll(mafioso);
			listeners.addAll(graveyard);
		}
		
		else if (mafioso.contains(speaker) && currentState == gameState.NIGHT) {
			listeners.addAll(mafioso);
			listeners.addAll(graveyard);
		}
		
		return listeners;
	}

	public void removePlayer(Player player) {
		players.remove(player);
		graveyard.remove(player);
		mafioso.remove(player);
		innocent.remove(player);		
	}
	
	public int getNumOfPlayers(){
		return players.size();
	}	
	
	public int getLynchCount(){
		return lynchCount;
	}
	
	public int getMurderCount(){
		return murderCount;
	}
	
	public void resetVoteCounter(){
		lynchCount = 0;
		murderCount = 0;
	}
	public long getSwitchTime() {
		return switchTime;
	}
	
	public void setSwitchTime(long t) {
		switchTime = t;
	}
	
	public boolean isDay(){
		if(currentState.equals(gameState.DAY)){
			return true;
		}else{
			return false;
		}
	}
	
	public Player getCurrentVictim(){
		if(currentState.equals(gameState.DAY)){
			return lynchVictim; 
		}else{
			return murderVictim;
		}
	}


	public int switchTurn(long callTime) {
		switchTime = callTime - dayTime - nightTime;
		return updateState(callTime);		
	}

	public String getChatString(Player player, String msg) {
		String result = "Chat";
		
		if (currentState == gameState.NIGHT && mafioso.contains(player))
			result = String.format("(Mafia Only) [%s]: %s", player.getPseudonym(), msg);
		else if (graveyard.contains(player))
			result = String.format("(Dead Chat) [%s]: %s", player.getPseudonym(), msg);
		else 
			result = String.format("(Game Chat) [%s]: %s", player.getPseudonym(), msg);
		
		return result;
	}
}

