package server;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;

import client.ClientPacket;
import game_space.ReadyRoom;
import players.Player;
import players.Player.PlayerState;

public class RoomManager implements Runnable{

	Thread thread;
	int roomCounter;
	ArrayList<ReadyRoom> rooms;
	SelectServer server;
	double last_update;
	boolean quit;
	
	public RoomManager(SelectServer s)
	{
		server = s;
		rooms = new ArrayList<ReadyRoom>();
		startThread();
	}
	
	void startThread()
	{
		if (thread == null)
		{
			System.out.println("Starting room thread..");
			thread = new Thread(this, "RoomManager");
			thread.start();
		}
	}
	
	public void shutdown()
	{
		quit = true;
	}
	
	public ReadyRoom create()
	{
		ReadyRoom room = new ReadyRoom(server, roomCounter);
		Date d = new Date();
		last_update = (double)d.getTime() / 1000;
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
	public ReadyRoom open(int rmIdx) 
	{
		ReadyRoom room = findRoom(rmIdx);
		
		if (room == null)
			room = create();
		
		if (room != null)
		{		
			return room;
		}
		
		return null;
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
		while (!quit)
		{
			for (ReadyRoom r : rooms)
			{
				Date now = new Date();
				double currTime = ((double)now.getTime()) / 1000;
				float elapsedTime = (float)(currTime - last_update);
				
				r.update(elapsedTime);
				last_update = currTime;
			}
			try {
				thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
}
