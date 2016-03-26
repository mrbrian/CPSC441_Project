package game_space;

import java.util.ArrayList;

import game_space.ReadyRoom.State;
import players.Player;
import server.SelectServer;

public class LobbyLogic_NotReady{
		
	private static final float COUNT_INTERVAL = 2;
	private int count;
	private float timer;
	private ReadyRoom room;
	
	public LobbyLogic_NotReady(ReadyRoom r)
	{
		room = r;
		count = 5;
	}
	
	public void update(float elapsedTime)
	{
		timer -= elapsedTime;

		while (timer <= 0 && count >= 0)
		{			
			// send to all people in the room
			if (count > 0)
				room.sendMessageRoom(String.format("Starting in %d", count));
			timer += COUNT_INTERVAL;
			count--; 
		}
		
		if (count == -1)
		{
			room.sendMessageRoom("Game starting now!");
			room.changeState(State.GameInProgress);
		}	
	}
}