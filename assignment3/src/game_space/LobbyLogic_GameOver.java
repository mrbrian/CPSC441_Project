package game_space;

import java.util.Date;
import client.ClientPacket;
import players.Player;
import server.Outbox;
import game_space.ReadyRoom.State;

public class LobbyLogic_GameOver extends LobbyLogic{
	
	private Date currTime = new Date();
	private float timer = 0;
	
	public LobbyLogic_GameOver(ReadyRoom r)
	{
		super(r);
	}
	
	public void update(float elapsedTime)
	{
		if (room.getPlayerList().isEmpty() && room.getObserverList().isEmpty() == true)
			room.changeState(State.NotReady);	
	}
	
	@Override
	public void processPacket(ClientPacket p, Player player) {
		switch (p.type) {
		case Ban:
		{
			String showStr = String.format("Cannot ban a player once the game has begun");
			Outbox.sendMessage(showStr, player.getChannel());
		}
		break;
		case Leave:
		{
			String showStr = String.format("%s left the room.", player.getUsername());
			room.sendMessageRoom(showStr);
			player.leaveRoom();
		}
		break;	
		}
	}
}
