package game_space;

import java.util.ArrayList;

import clients.Players;

public class GameSpace{

	private ArrayList<Players> innocentList;
	private ArrayList<Players> mafioList;
	private ArrayList<Players> toLynch;
	
	private boolean daytime;
	private int roundTimer;
		
	public GameSpace(){
		innocentList = new ArrayList<>();
		mafioList = new ArrayList<>();
		
		daytime = false;
		roundTimer = 0;
	}
	
	public void lynchVote(){
		
	}
	
	public void murderVote(){
		
	}
}