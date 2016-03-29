package game_space;

import client.ClientPacket;
import players.Player;

public abstract class LobbyLogic {

	ReadyRoom room;
	
	public LobbyLogic(ReadyRoom r){
		room = r;
	}
	
	public void update(float elapsedTime){
		
	}
	
	public void processPacket(ClientPacket p, Player player){
		switch(p.type)
		{
			case Chat:
			{
				String msg = new String(p.data, 0, p.dataSize);
				String showStr = String.format("Chat [%s]: %s", player.getUsername(), msg); 
				room.sendMessageRoom(showStr);
				System.out.println(showStr);
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
