package server;

/*
 * A simple TCP select server that accepts multiple connections and echo message back to the clients
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 */

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import client.ClientPacket;
import client.packets.*;
import game_space.GameSpace;
import game_space.ReadyRoom;
import game_space.ReadyRoom.State;
import players.Player;
import players.Player.PlayerState;
import server.FileIO;

public class SelectServer 
{
	public static int BUFFERSIZE = 256;
	
	RoomManager room_mgr;
	PlayerManager plyr_mgr;
	
	ServerSocketChannel tcp_channel;
    ByteBuffer inBuffer;
    CharBuffer cBuffer;
    Selector selector;
    FileIO saveInfo;
    int port;
    
	public SelectServer(int port) 
	{
		this.port = port;
		
		room_mgr = new RoomManager(this);
		plyr_mgr = new PlayerManager(this);
		   
		try {
	        // Initialize the selector
	        selector = Selector.open();

	        // Create a server channel and make it non-blocking
	        tcp_channel = ServerSocketChannel.open();
	        tcp_channel.configureBlocking(false);
	       
	        // Get the port number and bind the socket
	        InetSocketAddress isa = new InetSocketAddress(port);
	        tcp_channel.socket().bind(isa);

	        // Register that the server selector is interested in connection requests
	        tcp_channel.register(selector, SelectionKey.OP_ACCEPT);
	        saveInfo = new FileIO();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	

	
    void processUnrestrictedCommands(ClientPacket p, SocketChannel ch) throws IOException
    {
    	SocketAddress socketAddress = ch.getRemoteAddress();

    	Player player = plyr_mgr.findPlayer(socketAddress);
    	
    	ByteBuffer bb;
    	String username;
    	String password;
    	
    	switch (p.type)
		{
    		// Gets new username and password and stores in "save_file"
			case CreateAccount:
	    		bb = ByteBuffer.allocate(2);
	    		bb.put(p.data[0]);
	    		bb.put(p.data[1]);
	    		short usernameLength = bb.getShort(0);
	    		
	    		username = new String(p.data, 2, usernameLength);
    			password = new String(p.data, 2 + usernameLength + 2, p.dataSize - (2 + usernameLength + 2));    			
    			
    			if(saveInfo.doesUsrExist(username) == false){
    				saveInfo.saveUserData(username, password); 
    				Outbox.sendMessage("Creating a new account..", ch);
    			}else{
    				System.out.println("Error Username Already Exists");
    			}
    			
	    		break;
	    	case Login:	    
	    		ClientLoginPacket clp = new ClientLoginPacket(p);	    		
	    		// update player info		
    			player.setUsername(clp.username);
    			
    			if(saveInfo.checkCredentials(clp.username, clp.password)){
    				plyr_mgr.login(player);
        			System.out.println(String.format("Login [%s]", player.getUsername()));
        			Outbox.sendMessage(String.format("Access Granted!\n Logged in successfully as: %s", clp.username), ch);    	    		
    			}else{
    				Outbox.sendMessage("Access Denied!", ch);
    			}
    			
	    		break;		    		

	    	case ShowState:
	    		Outbox.sendMessage(player.stateString(), ch);
	    		break;
			default:
				Outbox.sendMessage("Log in first!", ch);
				break;
		}
    }
    
    void processLoggedInCommands(ClientPacket p, SocketChannel ch) throws IOException
    {
    	SocketAddress socketAddress = ch.getRemoteAddress();
    	
    	Player player = plyr_mgr.findPlayer(socketAddress);
    	
    	switch (p.type)
    	{
	    	case SetAlias:
	    		String pseudo = new String(p.data, 0, p.dataSize);
    			player.setPseudonym(pseudo);
	    		break;
	    	
	    	case Join:
	    		{
	    			//make sure player has a pseudonym before they can use join
	    			if (player.getPseudonym() != null) {
	    			
	    				// try to join
	    				ClientJoinPacket cjp = new ClientJoinPacket(p);
		    		
	    				ReadyRoom room = room_mgr.findRoom(cjp.roomId);
	    				
	    				if (room != null) {
	    					room.joinRoom(player);	
	    					int rmIdx = room.getId();
	    					System.out.println(String.format("Join [%s]: %d", player.getUsername(), rmIdx));
	
	    					Outbox.sendMessage(String.format("You are now in room #%d", rmIdx), ch);
	    				} else {
	    					Outbox.sendMessage("No such room. Use '/createroom rooomid' or join a room that already exists",ch);
	    				}
	    			} else {
	    				String msg = "You must use '/setalias' to choose a pseudonym before you can join a room";
	    				Outbox.sendMessage(msg, ch);	
	    			}
	    		}
	    		break;
	    		
	    	case CreateRoom:
	    		
	    		//join packet has everything needed to create a room
	    		ClientJoinPacket crp = new ClientJoinPacket(p);
	    		
	    		ReadyRoom room = room_mgr.create(crp.roomId);
	    		if (room != null) { //room created 
	    			int rmIdx = room.getId();
	    			System.out.println(String.format("Created room [%s]: %d", player.getUsername(), rmIdx));
	    			Outbox.sendMessage(String.format("You created room #%d", rmIdx), ch);
	    		} else {
	    			Outbox.sendMessage("Could not create room",ch);
	    		}
				
	    		break;
	    		
	    	case ListRooms:
	    		String msg;
	    		
	    		msg = room_mgr.getRooms();
	    		Outbox.sendMessage(msg,ch);
	    		break;

	    	case Logout:
	    		int roomID = player.getRoomIndex();
	    		
	    		if (roomID != -1) {  //then in a game
	    			room = room_mgr.findRoom(roomID);
	    			GameSpace game = room.getGameSpace();
	    			game.removePlayer(player);	    			
	    		}
	    		
	    		//now remove player from the player manager
	    		plyr_mgr.removePlayer(player);
	    		player.getChannel().socket().close();
	    		break;

	    	case ListUsers:
	    		
	    		Iterator<Player> playerList = plyr_mgr.iterator();
	    		
	    		while(playerList.hasNext()){
	    			String element = playerList.next().getUsername().toString();
	    			System.out.println(element);
	    			Outbox.sendMessage(element, ch);
	    		}   		
	    		
	    		break;
	    	case ShowState:
	    		Outbox.sendMessage(player.stateString(), ch);
	    		break;
    		default:
	    		{
	    			String str = String.format("Could not process command: %s, %s",  p.type.toString(), ch.toString());
	    			System.out.println(str);
	    			Outbox.sendMessage(str, ch);
	    		}
    			break;
    	}
    }

    void processRoomCommands(ClientPacket p, SocketChannel ch) throws IOException
    {
    	SocketAddress socketAddress = ch.getRemoteAddress();
    	Player player = plyr_mgr.findPlayer(socketAddress);
    	
		room_mgr.processPacket(p, player);
    	switch (p.type)
    	{
	    	case ShowState:
	    		Outbox.sendMessage(player.stateString(), ch);
	    		break;
    		default:
    			System.out.println(String.format("%s [%s]", p.type.toString(), socketAddress.toString()));
    			break;
    	}
    }
    
    void processPacket(ClientPacket p, SocketChannel ch)
    {
    	try
    	{
	    	SocketAddress socketAddress = ch.getRemoteAddress();
	    	
	    	Player player = plyr_mgr.findPlayer(socketAddress);
	    	
	    	if (player == null)
	    		return;
	    	
	    	PlayerState pState = player.getState();
	    	
	    	if (pState == PlayerState.Not_Logged_In) 
	    	{
	    		processUnrestrictedCommands(p, ch);	
	    	}
	    	else if (pState == PlayerState.Logged_In) 
	    	{
		    	processLoggedInCommands(p, ch);
	    	}
	    	else if (pState == PlayerState.In_Room) 
	    	{
		    	processRoomCommands(p, ch);
	    	}
    	}
    	catch (IOException e)
    	{
    		System.out.println("processPacket error: " + e.getMessage());
    	}
    }
    
    public void run() throws Exception 
    {
    	System.out.println("Server running on port: " + port );
        // Wait for something happen among all registered sockets
            boolean terminated = false;
            while (!terminated) 
            {
            	try 
            	{
                	if (selector.select(500) < 0)
	                {
	                    System.out.println("select() failed");
	                    System.exit(1);
	                }
	                
	                // Get set of ready sockets
	                Set<SelectionKey> readyKeys = selector.selectedKeys();
	                Iterator<SelectionKey> readyItor = readyKeys.iterator();
	
	                // Walk through the ready set
	                while (readyItor.hasNext()) 
	                {
	                    // Get key from set
	                    SelectionKey key = (SelectionKey)readyItor.next();
	
	                    // Remove current entry
	                    readyItor.remove();
	
	                    // Accept new connections, if any
	                    if (key.isAcceptable())
	                    {
	                        SocketChannel cchannel = ((ServerSocketChannel)key.channel()).accept();
	                        cchannel.configureBlocking(false);
	                        System.out.println("Accept connection from " + cchannel.socket().toString());
	                        
	                        // Register the new connection for read operation
	                        cchannel.register(selector, SelectionKey.OP_READ);
	                        
	                        Player plyr = new Player(cchannel);
	        	    		plyr_mgr.addPlayer(plyr);	
	        	    		
	        	    		Outbox.sendMessage("Welcome!\nCommands: \"/login <user> <pwd>\" or \"/createaccount <user> <pwd>\"", cchannel);                    
	                    } 
	                    else 
	                    {
	                    	SelectableChannel sc = key.channel();
	                    	
	                        SocketChannel cchannel = (SocketChannel)sc;
	                        if (key.isReadable())
	                        {	                        
	                            // Open input and output streams
	                            inBuffer = ByteBuffer.allocateDirect(BUFFERSIZE);
	                            cBuffer = CharBuffer.allocate(BUFFERSIZE);
	                          
	                            Thread.sleep(100);		
	
	                        	Player p = plyr_mgr.findPlayer(cchannel.getRemoteAddress());	                            
	                            try 
	                            {
		                            // Read from socket
	                            	int bytesRecv = cchannel.read(inBuffer);
		                            if (bytesRecv <= 0)
		                            {
		                                System.out.println(String.format("[%s]: read() error, or connection closed", p.getUsername()));		                                
		                            	plyr_mgr.disconnect(p);
		                                key.cancel();  // deregister the socket
		                                continue;
		                            }
	                            }
	                            catch(Exception e)
	                            {
	                            	plyr_mgr.disconnect(p);
	                                System.out.println("Canceling..");
	                                
	                                key.cancel();  // deregister the socket	                            	
	                            }	                             
	                            inBuffer.flip();      // make buffer available  
	                            
	                            while (inBuffer.hasRemaining())
	                            {
		                            ClientPacket cp = ClientPacket.read(inBuffer);
		                            processPacket(cp, cchannel);
	                            }
	                    	}
	                    }
	                } // end of while (readyItor.hasNext()) 
	            } // end of while (!terminated)
                catch (IOException e) 
            	{
                    System.out.println("SelectServer run error: " + e.getMessage());
                }
	        }
 
        // close all connections
        Set<SelectionKey> keys = selector.keys();
        Iterator<SelectionKey> itr = keys.iterator();
        while (itr.hasNext()) 
        {
            SelectionKey key = itr.next();
            //itr.remove();
            if (key.isAcceptable())
            {
            	closeChannel(key.channel());
            }
            else if (key.isValid())
            {
            	closeChannel(key.channel());                
            }
        }
    }

    // closeChannel - closes the given SelectableChannel (either UDP or TCP)
    static void closeChannel(SelectableChannel channel)
    {
    	try
    	{
    		if (channel instanceof ServerSocketChannel)		// check is it TCP
    		{
    			ServerSocketChannel tcpChannel = (ServerSocketChannel)channel;
	        	tcpChannel.socket().close();
    		}
    		else if (channel instanceof DatagramChannel)	// check if its UDP
    		{
	        	DatagramChannel udpChannel = (DatagramChannel)channel;
            	udpChannel.socket().close();
	        }
    	}
        catch (IOException e) {
            System.out.println(e);
    	}
    }

    public static void main(String args[]) throws Exception 
	{
		 if (args.length != 1)
        {
            System.out.println("Usage: SelectServer <Listening Port>");
            System.exit(1);
        }
		int port = Integer.parseInt(args[0]);
		SelectServer server = new SelectServer(port);
		server.run();
	}
}
