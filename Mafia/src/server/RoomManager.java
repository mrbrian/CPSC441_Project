package server;

import java.nio.channels.SocketChannel;
import java.security.Timestamp;
import java.sql.Time;
import java.util.ArrayList;

import client.ClientPacket;
import game_space.ReadyRoom;

public class RoomManager implements Runnable{

	Thread thread;
	int roomCounter;
	ArrayList<ReadyRoom> rooms;
	
	public RoomManager()
	{
		rooms = new ArrayList<ReadyRoom>();
		startThread();
	}
	
	void startThread()
	{
		if (thread == null)
		{
			thread = new Thread(this, "RoomManager");
			thread.start();
		}
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
	
	// tries to access the room, if not found, creates a new room (with roomindex = counter++)
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

	public ServerPacket processPacket(ClientPacket p, SocketChannel ch) {
		switch (p.type)
		{
			case Vote:
				break;
			case Invite:
				break;
		}
		return null;
	}

	@Override
	public void run() {
		for (ReadyRoom r : rooms)
		{
			r.update(new Date());
		}
	}
	
}
