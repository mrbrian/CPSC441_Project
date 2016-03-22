package server;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import client.Player;

public class PlayerManager 
{
	private static PlayerManager instance;
	ArrayList<Player> players;
	
	PlayerManager()
	{
		players = new ArrayList<Player>();	
	}
	
	public void addPlayer(Player p)
	{
		players.add(p);
	}

    public Player findPlayer(SocketAddress socketAddress)
    {
    	String findIp = socketAddress.toString();
    	for (Player p :players) {
			if (p.getIPAddress().equals(findIp))
				return p;
		}
		return null;    	
    }
}
