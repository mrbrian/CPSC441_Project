package server;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;

import client.ClientPacket;
import game_space.ReadyRoom;
import players.Player;

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
	
	public ReadyRoom create(int rmIdx)
	{
		ReadyRoom room = findRoom(rmIdx);
		
		if (room == null){
			room = new ReadyRoom(server, rmIdx);
			Date d = new Date();
			last_update = (double)d.getTime() / 1000;
			rooms.add(room);
			roomCounter++;
			return room;
		}
		
		//return null if room already exists to show no new room was created
		return null;
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
			room = create(rmIdx);
		
		if (room != null)
		{		
			return room;
		}
		
		return null;
	}

	public void processPacket(ClientPacket p, Player player) {
		// find which room it should go to
		ReadyRoom r = findRoom(player);
		r.processPackets(p, player);
	}

	private ReadyRoom findRoom(Player p) {
		int idx = p.getRoomIndex();
		return (findRoom(idx));
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
				thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String getRooms() {
		String msg = "\nRoomId\tStatus";
		
		for (ReadyRoom rm : rooms) {
			msg = msg + "\n" + rm.getId() + "\t" + rm.getState();
		}
		
		return msg;
	}
}	
