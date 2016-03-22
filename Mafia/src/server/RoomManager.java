package networks;

import java.util.ArrayList;

import clients.Player;
import game_space.ReadyRoom;

public class RoomManager {

	int roomCounter;
	ArrayList<ReadyRoom> rooms;
	
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
	
	public ReadyRoom open(Player p, int rmIdx) 
	{
		ReadyRoom room = findRoom(rmIdx);
		
		//room.joinRoom(IP, playerName);
		
		if (room != null)
			return room;
		else		
			return create(); 		
	}
}
