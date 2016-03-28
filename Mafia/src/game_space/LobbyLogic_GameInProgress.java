package game_space;

import game_space.ReadyRoom.State;
import players.Player;
import server.Outbox;
import server.ServerPacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import client.ClientPacket;

public class LobbyLogic_GameInProgress extends LobbyLogic{
	
	private static final float TURN_INTERVAL = 60;  
	private GameSpace game;
	private float timer;
	private int turn_num;
	private Date currTime;
	private int oldState;
	private int currState;
	
//State
	public LobbyLogic_GameInProgress(ReadyRoom r, GameSpace g)
	{
		super(r);
		game = g;
	}
	
	public void update(float elapsedTime)
	{
		timer += elapsedTime;
		currTime = new Date();
		currState = game.updateState(currTime.getTime());
		if (currState == 1) {
			Outbox.sendMessage("******A new day begins...******", room.getSocketChannelList());
			room.sendMessageRoom(String.format("******A new day begins...******"));
		}
		else if (currState == 0) {
			Outbox.sendMessage("~~~~~~Night descends...~~~~~~~", room.getSocketChannelList());
			room.sendMessageRoom(String.format("~~~~~~Night descends...~~~~~~~"));	
		}
	
	}

	public void sendMessageToGroup(String msg, Player speaker) {
				
		ArrayList<Player> listeners = game.whoCanChatWith(speaker);
		
		for (int i = 0; i < listeners.size(); i++) {
			Player player = listeners.get(i);
			
			ServerPacket p = new ServerPacket(ServerPacket.PacketType.ServerMessage, msg, new byte[] {});
			Outbox.sendPacket(p, player.getChannel());
		}
	}
	
	@Override
	public void processPacket(ClientPacket p, Player player) {

		switch(p.type)
		{		
			case Chat:
				String msg = new String(p.data, 0, p.dataSize);
				String showStr = String.format("Chat [%s]: %s", player.getUsername(), msg); 
				sendMessageToGroup(showStr, player);
				System.out.println(showStr);
				break;
				
			case Vote:
	    		String victim = new String(p.data, 0, p.dataSize);
    			game.lynchVote(player, victim);
				break;
				
			default:
				break;
		}		
	}
}