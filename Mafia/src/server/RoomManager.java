package server;

import java.util.ArrayList;

import client.Player;
import game_space.ReadyRoom;

public class RoomManager {

	int roomCounter;
	ArrayList<ReadyRoom> rooms;
	
	public RoomManager()
	{
		rooms = new ArrayList<ReadyRoom>();
	}
	
	public ReadyRoom create()
	{
		ReadyRoom room = new ReadyRoom(roomCounter);
		
		rooms.add(room);
		roomCounter++;
		return room;
	}
	
	public ReadyRoom findRoom(int idx)
	{
		for (ReadyRoom rm : rooms) {
			if (rm.getId() == idx)
				return rm;
		}
		return null;
	}
	
	public int open(int rmIdx) 
	{
		ReadyRoom room = findRoom(rmIdx);
		
		if (room == null)
			room = create();
		
		if (room != null)
		{
			//room.joinRoom(IP, playerName);			
			return rmIdx;
		}
		
		return -1;
	}
}
