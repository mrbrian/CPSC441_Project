package server;

import java.util.ArrayList;
import java.util.Date;

import client.ClientPacket;
import game_space.ReadyRoom;
import players.Player;

public class RoomManager implements Runnable{

	private Thread thread;
	private ArrayList<ReadyRoom> rooms;
	private SelectServer server;
	private double last_update;
	private boolean quit;	
	private static RoomManager instance;
	
	public static RoomManager getInstance()
	{ 
		return instance;
	}
	
	public RoomManager(SelectServer s)
	{
		instance = this;
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
			Date now = new Date();
			double currTime = ((double)now.getTime()) / 1000;
			float elapsedTime = (float)(currTime - last_update);
			
			for (ReadyRoom r : rooms)
			{				
				r.update(elapsedTime);
			}
			last_update = currTime;
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
