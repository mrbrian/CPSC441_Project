package game_space;

import java.util.ArrayList;

import client.ClientPacket;
import game_space.ReadyRoom.State;
import players.Player;
import server.SelectServer;

public class LobbyLogic_Beginning extends LobbyLogic{
		
	private static final float COUNT_INTERVAL = 2;
	private int count;
	private float timer;
	
	public LobbyLogic_Beginning(ReadyRoom r)
	{
		super(r);
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

	@Override
	public void processPacket(ClientPacket p, Player player) {
		switch (p.type)
		{
			default:
				super.processPacket(p, player);
				break;
		}
		
	}
}