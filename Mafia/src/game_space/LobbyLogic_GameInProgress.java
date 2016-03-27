package game_space;

import game_space.ReadyRoom.State;
import java.util.Date;

public class LobbyLogic_GameInProgress implements LobbyLogic{
	
	private static final float TURN_INTERVAL = 60;  
	private GameSpace game;
	private ReadyRoom room;
	private float timer;
	private int turn_num;
	private Date currTime;
	private int oldState;
	private int currState;
	
//State
	public LobbyLogic_GameInProgress(ReadyRoom r, GameSpace g)
	{
		game = g;
		room = r;
	}
	
	public void update(float elapsedTime)
	{
		timer += elapsedTime;
		currTime = new Date();
		currState = game.updateState(currTime.getTime());
		if (currState == 1) {
			room.sendMessageRoom(String.format("******A new day begins...******"));
		}
		else if (currState == 0) {
			room.sendMessageRoom(String.format("~~~~~~Night descends...~~~~~~~"));	
		}
	
	}
}
