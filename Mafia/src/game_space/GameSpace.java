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
	private long dayTime = 5000; //300000;		//300000ms = 5min
	private long nightTime = 2000;//150000;	//150000ms = 2.5min
	private long voteTime = 60000; //60000 = 1min
	private long switchTime = 0;
	private long voteBegin = 0;
	
	private ArrayList<Player> players;
	private ArrayList<Player> innocent;
	private ArrayList<Player> mafioso;
	private ArrayList<Player> graveyard;
	private int mafiaFraction = 3;			//fraction = 1/mafiaFraction
	private enum gameState {DAY, NIGHT};
	private gameState currentState = gameState.DAY;
	
	public GameSpace(ArrayList<Player> connected) {
		players = connected;
		assignTeams();
	}

	public void assignTeams() {
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
		
		for (int i = 0; i < players.size(); i++) {			//cycle through players and add to innocent if not part of mafioso
			if (mafioso.contains(players.get(i)) == false)
				players.get(i).setPlayerType(PlayerTypes.PlayerType.MAFIA);
				innocent.add(players.get(i));
		}
	}
	
	public int updateState(long callTime) {
		if (switchTime == 0) {
			switchTime = callTime;
			nextDay();
			return 1;
		}
		if (currentState == gameState.DAY) {
			if (switchTime + dayTime <= callTime)
				switchTime = callTime;
				nextNight();
				return 0;
		}
		if (currentState == gameState.NIGHT) {
			if (switchTime + nightTime <= callTime)
				switchTime = callTime;
				nextDay();
				return 1;
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
		canLynch = false;
		canMurder = false;
		lynchCount = 0;
		murderCount = 0;
		voteBegin = 0;
	}
	
	public boolean checkVote(long voteTime) {
		if (voteBegin == 0 && (lynchOngoing || murderOngoing == true)) {
			voteBegin = voteTime;
		}
		else {
			if (voteTime >= voteBegin + voteTime) {
				voteReset();
				return true;
			}
		}
		return false;
	}
	
	public void lynchVote(Player lyncher, Player victim) {
		if (canLynch == true) {
			if (lynchVictim == null && lynchOngoing == false) {
				lynchVictim = victim;
				lynchCount++;
				lynchOngoing = true;
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
	}
	
	public Player lynchCheck() {
		if (lynchCount > (players.size()/2)) {
			kill(lynchVictim);
			return lynchVictim;
		}
		else
			return null;
	}

	public void murderVote(Player murderer, Player victim) {
		if (canMurder == true && mafioso.contains(murderer) == true) {
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
		players.remove(condemned);
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
	
	public PlayerTypes.PlayerType checkWin() {
		if(mafioso.isEmpty() == true)
			return PlayerTypes.PlayerType.INNO;
		else if (mafioso.size() > innocent.size())
			return PlayerTypes.PlayerType.MAFIA;
		else
			return null;
	}
	
	//returns an ArrayList of players with whom the player can speak based on current state, less the player
	public ArrayList <Player> whoCanChatWith(Player speaker) {	
		ArrayList<Player> listeners = null;
		
		if(speaker == null){
			System.out.println("speaker is NULL");
		}else{
			System.out.println("speaker is NOT NULL");
			
		}
		
		if(graveyard == null){
			System.out.println("graveyard is NULL");
		}else{
			System.out.println("graveyard is NOT NULL");
			
		}
		
		if (speaker.getIsAlive() == false && graveyard.size() > 1)
			listeners = graveyard;
		
		if (currentState == gameState.DAY)
			listeners = players;
		
		if (mafioso.contains(speaker) && currentState == gameState.NIGHT)
			listeners = mafioso;
		
		listeners.remove(speaker);
		return listeners;
	}

	public void removePlayer(Player player) {
		players.remove(player);
		graveyard.remove(player);
		mafioso.remove(player);
		innocent.remove(player);		
	}
	
}
