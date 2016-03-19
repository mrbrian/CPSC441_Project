package game_space;


import java.util.Random;

import java.util.ArrayList;

import clients.Players;

public class GameSpace{
	
	private Random randGen;
	private Players lynchVictim = null;
	private Players murderVictim = null;
	private boolean lynchOngoing;
	private boolean murderOngoing;
	private int lynchCount = 0;
	private int murderCount = 0;
	
	private ArrayList<Players> players;
	private ArrayList<Players> innocent;
	private ArrayList<Players> mafioso;
	private ArrayList<Players> graveyard;
	private int mafiaFraction = 3;			//fraction = 1/mafiaFraction
	private enum gameState {DAY, NIGHT};
	private gameState currentState;
	
	public GameSpace(ArrayList<Players> connected) {
		players = connected;
		currentState = gameState.DAY;
		assignTeams();
	}

	public void assignTeams() {
		int numOfMafia = (players.size() / mafiaFraction);
		if (numOfMafia == 0)
			numOfMafia = 1;
			
		mafioso = new ArrayList <Players>(numOfMafia);
		innocent = new ArrayList <Players>(players.size() - numOfMafia);
			
		for (int i = 0; i < numOfMafia; i++) {
			int newMafia = randGen.nextInt(players.size());
			mafioso.add(players.get(newMafia));
		}
		
		for (int i = 0; i < players.size(); i++) {			//cycle through players and add to innocent if not part of mafioso
			if (mafioso.contains(players.get(i)) == false)
				innocent.add(players.get(i));
		}

	}
	
	public void lynchVote(Players lyncher, Players victim)
	{
		if (currentState == gameState.DAY) {
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
			lynchCheck();
		}
	}
	
	public void lynchCheck() {
		if (lynchCount > (players.size()/2)) {
			kill(lynchVictim);
			//System.out.println(lynchVictim + " has been lynched!");
			lynchVictim = null;
			lynchOngoing = false;
			lynchCount = 0;
			//end the day
		}
	}

	public void murderVote(Players murderer, Players victim)
	{
		if (currentState == gameState.NIGHT) {
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
			murderCheck();
		}
	}
	
	public void murderCheck() {
		if (murderCount > (mafioso.size()/2)) {
			kill(murderVictim);
			//System.out.println(murderVictim + " has been murdered!");
			murderVictim = null;
			murderOngoing = false;
			murderCount = 0;
			//end the night
		}
	}	
	
	public void kill(Players condemned) {
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
	
	
	//returns an ArrayList of players with whom the player can speak based on current state, less the player
	public ArrayList <Players> whoCanChatWith(Players speaker) {	
		ArrayList<Players> listeners = null;
		
		if (speaker.getIsAlive() == false && graveyard.size() > 1)
			listeners = graveyard;
		
		if (currentState == gameState.DAY)
			listeners = players;
		
		if (mafioso.contains(speaker) && currentState == gameState.NIGHT)
			listeners = mafioso;
		
		listeners.remove(speaker);
		return listeners;
	}
	
}
