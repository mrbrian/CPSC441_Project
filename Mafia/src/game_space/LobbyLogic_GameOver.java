package game_space;

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
