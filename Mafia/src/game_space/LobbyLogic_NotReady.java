package game_space;

import java.util.ArrayList;

import client.ClientPacket;
import game_space.ReadyRoom.State;
import players.Player;

public class LobbyLogic_NotReady extends LobbyLogic{
		
	private ArrayList<Player> players;
	
	public LobbyLogic_NotReady(ReadyRoom r, ArrayList<Player> ps)
	{
		super(r);
		room = r;
		players = ps;
	}
	
	public void update(float elapsedTime)
	{
		if (players.size() == room.NUM_PLAYERS_REQ)
			room.changeState(State.Beginning);			
	}

	@Override
	public void processPacket(ClientPacket p, Player player) {
		switch (p.type)
		{
			case StartGame:
				if (!room.getObservers().contains(player))
					room.changeState(State.Beginning);
				break;
			default:
				super.processPacket(p, player);
				break;
		}		
	}
}