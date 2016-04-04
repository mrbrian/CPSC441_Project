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

	private int win = -1;
	
	
//State
	public LobbyLogic_GameInProgress(ReadyRoom r, GameSpace g)
	{
		super(r);
		game = g;
		oldState = 2;
	}
	
	public void update(float elapsedTime)
	{
		//System.out.println(room.getPlayerList().size());
		for (Player p : room.getPlayerList()) {
			if (p.getChannel() == null) {
				room.sendMessageRoom(String.format("Player %s has disconnected, waiting for them to reconnect...", p.getPseudonym()));
				game.voteReset();
				room.changeState(State.Paused);
			}
		}
		
		win = game.checkWin();
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
		}
		else {	
			timer += elapsedTime;
			currTime = new Date();
			currState = game.updateState(currTime.getTime());
			if (oldState != currState) {
				oldState = currState;
				if (currState == 1) {
					Outbox.sendMessage("******A new day begins...******", room.getSocketChannelList());
				}
				else if (currState == 0) {
					Outbox.sendMessage("~~~~~~Night descends...~~~~~~~", room.getSocketChannelList());
				}
			
			}
		}

	}

	public void sendMessageToGroup(String msg, Player speaker) {
				
		ArrayList<Player> observers = room.getObservers();
		if (observers.contains(speaker))
		{
			for (int i = 0; i < observers.size(); i++) {
				Player player = observers.get(i);
				
				ServerPacket p = new ServerPacket(ServerPacket.PacketType.ServerMessage, msg, new byte[] {});
				Outbox.sendPacket(p, player.getChannel());
			}
			return;	
		}
		
		ArrayList<Player> listeners = game.whoCanChatWith(speaker);
		
		if(listeners != null){
			for (int i = 0; i < listeners.size(); i++) {
				Player player = listeners.get(i);
				
				ServerPacket p = new ServerPacket(ServerPacket.PacketType.ServerMessage, msg, new byte[] {});
				Outbox.sendPacket(p, player.getChannel());
			}
		}
	}

	@Override
	public void processPacket(ClientPacket p, Player player) {
		String msgToDisplay;
		
		System.out.println("player is: " + player.getPseudonym().toString());

		switch(p.type)
			{	
			case Chat:
			{
				String msg = new String(p.data, 0, p.dataSize);				
				String showStr = game.getChatString(player, msg);
				sendMessageToGroup(showStr, player);
				System.out.println(showStr);
			}
			break;
			case Ban:
			{
				String showStr = String.format("Cannot ban a player once the game has begun");
				Outbox.sendMessage(showStr, player.getChannel());
				break;
			}
			case Vote:
	    		String victim = new String(p.data, 0, p.dataSize);	    		
	    		Player victimPlayer = game.checkVictim(victim);
	    		if(victimPlayer == null){

    				msgToDisplay = String.format("User \"" + victim + "\" does not exist");
	    			Outbox.sendMessage(msgToDisplay, player.getChannel());
    				//sendMessageToGroup(msgToDisplay, player);
    			}
	    		else{
	    			int voteCount = 0;
	    			String voteDescriptor = "";
	    			boolean killSuccess = false;
	    			
	    			if(game.isDay() == true){
	    				game.lynchVote(player, victimPlayer);
	    				if(game.lynchCheck() != null){
	    					msgToDisplay = "\"" + victim + "\" has been lynched successfully";
	    					sendMessageToGroup(msgToDisplay, player);
	    					killSuccess = true;
	    				}	    				
	    				if(!killSuccess){
		    				voteCount = game.getLynchCount();
		    				voteDescriptor = " Lynch count on " + victim + " : ";
	    				}
	    			}
	    			else{
	    				game.murderVote(player, victimPlayer);
	    				if(game.murderCheck() != null){
	    					msgToDisplay = "\"" + victim + "\" has been murdered successfully";
	    					sendMessageToGroup(msgToDisplay, player);
	    					killSuccess = true;
	    				}	    				
	    				if(!killSuccess){
		    				voteCount = game.getMurderCount();
		    				voteDescriptor = " Murder count: " + victim + " : ";	
	    				}
	    			}
    				
	    			if(!killSuccess){
		    			msgToDisplay = "\"" + player.getPseudonym().toString() + 
		    					"\" has voted for \"" + victim + "\" ---- " + 
		    					voteDescriptor + voteCount;
	    			}
	    			else{
	    				
	    				// ***** ISSUE MIGHT BE HERE ******
	    				msgToDisplay = "";
	    				if(game.isDay()) {
	    					Outbox.sendMessage("+++The day ends in bloodshed... +++", room.getSocketChannelList());
	    					Outbox.sendMessage("~~~~~~Night descends...~~~~~~~", room.getSocketChannelList());
	    					game.nextNight();
	    				}else{
	    					Outbox.sendMessage("+++A bump in the night...+++ ", room.getSocketChannelList());
	    					Outbox.sendMessage("******A new day begins...******", room.getSocketChannelList());
	    					game.nextDay();
	    				}
	    				
	    				System.out.println("isDay: " + game.isDay());
	    				//game.resetVoteCounter();
	    			}
	    			
    				sendMessageToGroup(msgToDisplay, player);
    			}	    		
				break;
			case Join:
			case Leave:
			{
				String showStr = "You can't leave while a game is in progress!"; 
				Outbox.sendMessage(showStr, player.getChannel());
			}
			break;
			case SwitchTurn:
			{
				Date currTime = new Date();
				int currState = game.switchTurn(currTime.getTime());
				if (currState == 1) {
					Outbox.sendMessage("******A new day begins...******", room.getSocketChannelList());					
				}
				else if (currState == 0) {
					Outbox.sendMessage("~~~~~~Night descends...~~~~~~~", room.getSocketChannelList());					
				}				
			}
			break;
		}		

	}
}
