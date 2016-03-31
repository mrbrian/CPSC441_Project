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
			case Vote:
	    		String victim = new String(p.data, 0, p.dataSize);	    		
	    		
	    		if(game.lynchVote(player, victim) == null){

    				msgToDisplay = String.format("User \"" + victim + "\" does not exist");
	    			Outbox.sendMessage(msgToDisplay, player.getChannel());
    				//sendMessageToGroup(msgToDisplay, player);
    			}else{
	    			int voteCount = 0;
	    			String voteDescriptor = "";
	    			boolean killSuccess = false;
	    			
	    			if(game.isDay() == true){
	    				if(game.lynchCheck() != null){
	    					msgToDisplay = "\"" + victim + "\" has been lynched successfully";
	    					sendMessageToGroup(msgToDisplay, player);
	    					killSuccess = true;
	    				}	    				
	    				if(!killSuccess){
		    				voteCount = game.getLynchCount();
		    				voteDescriptor = " Lynch count on " + victim + " : ";
	    				}

	    			}else{
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
	    			}else{
	    				
	    				// ***** ISSUE MIGHT BE HERE ******
	    				msgToDisplay = "";
	    				if(game.isDay()){
	    					game.nextNight();
	    				}else{
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
