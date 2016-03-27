package server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

import players.Player;
import players.Player.PlayerState;

public class PlayerManager implements Iterable<Player>
{
	private static PlayerManager instance;
	private ArrayList<Player> players;
	private SelectServer server;
	
	public PlayerManager(SelectServer s)
	{
		server = s;
		players = new ArrayList<Player>();	
	}
	
	public void addPlayer(Player p)
	{
		if (!players.contains(p))
			players.add(p);
	}

    public Player findPlayer(SocketAddress socketAddress)
    {
		return findPlayer(socketAddress.toString());    	
    }

    public Player findPlayer(String findIp)
    {
    	for (Player p :players) {
			if (p.getIPAddress().equals(findIp))
				return p;
		}
		return null;    	
    }
    
    public Iterator<Player> iterator()
    {
    	return players.iterator();
    }

    public void removePlayer(Player p) {
    	players.remove(p);
    }

    public void disconnect(Player p) {
    	if (p != null)
    		p.setSocketChannel(null);	// set to null to show they disconnected
    	
    	// to do: have a time limit they can be disconnected before they are removed from the player list?
    }

	public void login(Player player) 
	{
		Player prevLogin = null;
		boolean reconnecting = false;
		// check if its a reconnect
		for (Player p : players)
		{
			if (player == p)
				continue;
			if (player.getUsername().equals(p.getUsername()) && !p.isConnected())
			{
				reconnecting = true;				
				p.setSocketChannel(player.getChannel());	// update the old player with new socket address	
				prevLogin = p;
				break;
			}
		}
		
		if (reconnecting)
		{	
			server.sendMessage(String.format("Welcome back %s!", prevLogin.getUsername()), prevLogin.getChannel());
			server.sendMessage(prevLogin.stateString(), prevLogin.getChannel());
			removePlayer(player);	// remove the new (duplicate) player
			return;
		}
		
		player.setState(PlayerState.Logged_In);
						
	}
}
