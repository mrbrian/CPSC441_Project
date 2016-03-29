package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import game_space.GameSpace;
import game_space.ReadyRoom;
import game_space.ReadyRoom.State;
import players.Player;

public class Outbox {
	
	public static void sendMessage(String msg, ArrayList<SocketChannel> channels)
	{
		for (SocketChannel ch : channels){				
			ServerPacket p = new ServerPacket(ServerPacket.PacketType.ServerMessage, msg, new byte[] {});
			sendPacket(p, ch);
		}
	}
	
	public static void sendMessage(String msg, SocketChannel ch)
	{				
		ServerPacket p = new ServerPacket(ServerPacket.PacketType.ServerMessage, msg, new byte[] {});
		sendPacket(p, ch);
	}

    public static void sendPacket(ServerPacket p, SocketChannel ch) 
    {
    	if (ch == null)
    	{
    		System.out.println(String.format("sendPacket warning (ch == null): %s ", p.msg));
    		return;
    	}
    	try
    	{
	    	ByteBuffer inBuffer = ByteBuffer.allocateDirect(p.getSize());
	    	p.write(inBuffer);
	    	inBuffer.rewind();
	    	ch.write(inBuffer);
    	}
    	catch (IOException e)
    	{
    		System.out.println(String.format("sendPacket error: %s", e.getMessage()));
    	}
    }    
}
