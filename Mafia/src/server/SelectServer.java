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
    int port;
    
	SelectServer(int port) throws IOException
	{
		this.port = port;
		
		room_mgr = new RoomManager();
		plyr_mgr = new PlayerManager();
		        
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
	}

	void sendMessage(String msg, SocketChannel ch) throws IOException
	{
		ServerPacket p = new ServerPacket(ServerPacket.PacketType.ServerMessage, msg, new byte[] {});
		sendPacket(p, ch);
	}
	
    void sendPacket(ServerPacket p, SocketChannel ch) throws IOException
    {
    	ByteBuffer inBuffer = ByteBuffer.allocateDirect(p.getSize());
    	p.write(inBuffer);
    	inBuffer.rewind();
    	ch.write(inBuffer);    	
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
			case CreateAccount:
	    		bb = ByteBuffer.allocate(2);
	    		bb.put(p.data[0]);
	    		bb.put(p.data[1]);
	    		short usernameLength = bb.getShort(0);
	    		
	    		username = new String(p.data, 2, usernameLength);
    			password = new String(p.data, 2 + usernameLength + 2, p.dataSize - (2 + usernameLength + 2));
    			
    			FileIO saveInfo = new FileIO();
    			
    			if(!saveInfo.doesUsrExist(username)){
    				saveInfo.saveUserData(username, password);
    			}else{
    				System.out.println("Username already exists !");   
    	    		sendMessage("Username already exists!", ch); 				
    			}
    			
	    		break;
	    	case Login:	    
	    		ClientLoginPacket clp = new ClientLoginPacket(p);	    		
	    		// update player info		
    			player.setUsername(clp.username);
    			player.setState(PlayerState.Logged_In);
    			
	    		plyr_mgr.addPlayer(player);	// if valid auth details given
	    		System.out.println(String.format("Login [%s]", player.getUsername()));
	    		
	    		sendMessage(String.format("Logged in successfully as: %s", clp.username), ch);
	    		break;	
			default:
	    		sendMessage("Log in first!", ch);
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
	    		// will join or if not exist, create room 
	    		ClientJoinPacket cjp = new ClientJoinPacket(p);
	    		
	    		int rmIdx = room_mgr.open(cjp.roomId);
	    		player.setRoomIndex(rmIdx);
	    		System.out.println(String.format("Join [%s]: %d", player.getUsername(), rmIdx));

	    		sendMessage(String.format("Joined room #: %d", rmIdx), ch);
	    		break;
	    	case Chat:
	    		String msg = new String(p.data, 0, p.dataSize);
	    		System.out.println(String.format("Chat [%s]: %s", player.getUsername(), msg));	    			
	    		break;
    		default:
    			System.out.println(String.format("%s [%s]", p.type.toString(), socketAddress.toString()));
    			sendMessage(String.format("Could not process command: %s",  p.type.toString()), ch);
    			break;
    	}
    }

    void processRoomCommands(ClientPacket p, SocketChannel ch) throws IOException
    {
    	SocketAddress socketAddress = ch.getRemoteAddress();
    	
    	Player player = plyr_mgr.findPlayer(socketAddress);
    	
    	switch (p.type)
    	{	    	
    		default:
    			System.out.println(String.format("%s [%s]", p.type.toString(), socketAddress.toString()));
    			break;
    	}
    }
    
    void processPacket(ClientPacket p, SocketChannel ch) throws IOException
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
    
    public void run() throws Exception 
    {
    	System.out.println("Server running on port: " + port );
        // Wait for something happen among all registered sockets
        try {
            boolean terminated = false;
            while (!terminated) 
            {
                if (selector.select(500) < 0)
                {
                    System.out.println("select() failed");
                    System.exit(1);
                }
                
                // Get set of ready sockets
                Set readyKeys = selector.selectedKeys();
                Iterator readyItor = readyKeys.iterator();

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
                        
                        Player plyr = new Player();
                        plyr.setIPAddress(cchannel.getRemoteAddress().toString());
        	    		plyr_mgr.addPlayer(plyr);	// if valid auth details given
        	    		
                        sendMessage("Welcome", cchannel);                    
                        //sendPacket(p, cchannel); 
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

                            // Read from socket
                            int bytesRecv = cchannel.read(inBuffer);
                            if (bytesRecv <= 0)
                            {
                                System.out.println("read() error, or connection closed");
                                key.cancel();  // deregister the socket
                                continue;
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
        }
        catch (IOException e) {
            System.out.println(e);
        }
 
        // close all connections
        Set keys = selector.keys();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) 
        {
            SelectionKey key = (SelectionKey)itr.next();
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
