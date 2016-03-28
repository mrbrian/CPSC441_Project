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
	private int oldState = 2;
	private int currState;
	private ArrayList<Player> mafioso;
	private ArrayList<Player> innocent;
	private int win = -1;
	
	
//State
	public LobbyLogic_GameInProgress(ReadyRoom r, GameSpace g)
	{
		super(r);
		game = g;
		setTeams();
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
		timer += elapsedTime;
		currTime = new Date();
		currState = game.updateState(currTime.getTime());
		if (oldState == 2)
			Outbox.sendMessage("+++A sleepy town is beset by violent criminals...+++", room.getSocketChannelList());
		if (oldState != currState) {
			oldState = currState;
			if (currState == 1) {
				Outbox.sendMessage("******A new day begins...******", room.getSocketChannelList());
			}
			else if (currState == 0) {
				Outbox.sendMessage("~~~~~~Night descends...~~~~~~~", room.getSocketChannelList());
			}
		
		}
		
		/*win = game.checkWin();
		if (win != -1) {
			if (win == 0) {
				Outbox.sendMessage("---The innocent have killed the last Mafioso, returning peace to their town!---", room.getSocketChannelList());
				Outbox.sendMessage("+++GAME OVER+++", room.getSocketChannelList());
				room.changeState(State.GameOver);
			}
			else {
				Outbox.sendMessage("|||The Mafia has gained control of the town from the shadows...|||", room.getSocketChannelList());
				Outbox.sendMessage("+++GAME OVER+++", room.getSocketChannelList());
				room.changeState(State.GameOver);
			}
		}*/
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
