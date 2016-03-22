package game_space;

import java.util.ArrayList;

import clients.Player;

public class LobbyLogic{
	
	//private ReadyRoom[] roomList;
	private ArrayList<ReadyRoom> roomList;
	private String IP;

	
	
	public LobbyLogic(ArrayList<ReadyRoom> roomList){
		this.roomList = roomList;
	}
	
	public void createAccount(){
		
	}
	
	public boolean connectUser(String user, String password){
		return false;
	}
	
	public void login(String username, String password){
		
	}
	
	public void disconnect(){
		
	}	
}