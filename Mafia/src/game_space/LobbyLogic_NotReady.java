package game_space;

import java.util.ArrayList;

import game_space.ReadyRoom.State;
import players.Player;
import server.SelectServer;

public class LobbyLogic_NotReady implements LobbyLogic{
		
	private ArrayList<Player> players;
	private ReadyRoom room;
	
	public LobbyLogic_NotReady(ReadyRoom r, ArrayList<Player> ps)
	{
		room = r;
		players = ps;
	}
	
	public void update(float elapsedTime)
	{
		if (players.size() == room.NUM_PLAYERS_REQ)
			room.changeState(State.Beginning);			
	}
}