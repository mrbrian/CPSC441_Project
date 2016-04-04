package game_space;

import java.util.ArrayList;
import java.util.Date;

import client.ClientPacket;
import game_space.ReadyRoom.State;
import players.Player;
import server.Outbox;

public class LobbyLogic_Paused extends LobbyLogic {
	private Date currTime = new Date();
	private float timer = 0;
	private GameSpace game;
	
	public LobbyLogic_Paused(ReadyRoom r, GameSpace g)
	{
		super(r);
		game = g;
	}
	
	public void update(float elapsedTime)
	{
		boolean disconnected = false;
		for (Player p : room.getPlayerList()) {
			if (p.getChannel() == null)
				disconnected = true;
		}
		if (disconnected == false) {
			room.sendMessageRoom("All players have reconnected, continuing last turn (votes have been reset)...");
			game.setSwitchTime(game.getSwitchTime() - (long)elapsedTime); 
			room.changeState(State.GameInProgress);
		}
	}
	
	@Override
	public void processPacket(ClientPacket p, Player player) {
		switch (p.type) {
		
		}
	}

}
