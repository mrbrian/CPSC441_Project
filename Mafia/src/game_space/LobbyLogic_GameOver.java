package game_space;

import game_space.ReadyRoom.State;
import players.Player;
import server.Outbox;
import server.ServerPacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import client.ClientPacket;

public class LobbyLogic_GameOver extends LobbyLogic{
	
	public LobbyLogic_GameOver(ReadyRoom r)
	{
		super(r);
	}
	
	@Override
	public void processPacket(ClientPacket p, Player player) {
		switch (p.type) {
		case Leave:
		{
			String showStr = String.format("%s left the room.", player.getUsername());
			room.sendMessageRoom(showStr);
			player.leaveRoom();				
		}
		break;	
		}
	}
	
	public void clearRoom() {
		
	}

}
