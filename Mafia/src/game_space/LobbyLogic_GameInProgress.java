package game_space;

import game_space.ReadyRoom.State;

public class LobbyLogic_GameInProgress implements LobbyLogic{
	
	private static final float TURN_INTERVAL = 60;  
	private GameSpace game;
	private ReadyRoom room;
	private float timer;
	private int turn_num;
	
//State
	public LobbyLogic_GameInProgress(ReadyRoom r, GameSpace g)
	{
		game = g;
		room = r;
	}
	
	public void update(float elapsedTime)
	{
		timer += elapsedTime;

		while (timer <= 0 && turn_num >= 0)
		{			
			// send to all people in the room
			if (turn_num > 0)
				room.sendMessageRoom(String.format("Beginning turn #%d..", turn_num));
			timer += TURN_INTERVAL;
			turn_num--; 
		}
		
		//if (count == -1)
		//{
		//	room.sendMessageRoom("Game is finished!");
		//	room.changeState(State.GameOver);
		//}	
	}
}
