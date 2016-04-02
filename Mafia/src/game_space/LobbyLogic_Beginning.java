package game_space;

import java.util.ArrayList;

import client.ClientPacket;
import client.packets.ClientBanPacket;
import game_space.ReadyRoom.State;
import players.Player;
import server.Outbox;
import server.SelectServer;

public class LobbyLogic_Beginning extends LobbyLogic{
		
	private static final float COUNT_INTERVAL = 2;
	private int count;
	private float timer;
	private ArrayList<Player> mafioso;
	private ArrayList<Player> innocent;
	private GameSpace game;
	
	public LobbyLogic_Beginning(ReadyRoom r, GameSpace g)
	{
		super(r);
		count = 5;
		game = g;
	}
	
	public void setTeams() {
		mafioso = game.assignMafia();
		for (int i = 0; i < mafioso.size(); i++){
			Outbox.sendMessage("||||||You are a member of the Mafia||||||", mafioso.get(i).getChannel());
		}
		innocent = game.assignInnocent();
		for (int i = 0; i < innocent.size(); i++){
			Outbox.sendMessage("------You are an innocent townsperson------", innocent.get(i).getChannel());
		}
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
			setTeams();
			room.changeState(State.GameInProgress);
		}	
	}

	@Override
	public void processPacket(ClientPacket p, Player player) {
		switch (p.type)
		{
		case Ban:
		{
			String showStr = String.format("Cannot ban a player once the game has begun");
			Outbox.sendMessage(showStr, player.getChannel());
			break;
		}
		
			default:
				super.processPacket(p, player);
				break;
		}
		
	}
}