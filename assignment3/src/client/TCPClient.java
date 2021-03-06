package client;

/*
 * A simple TCP client that sends messages to a server and display the message
   from the server. 
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 */


import java.io.*; 
import java.net.*;
import java.nio.ByteBuffer;

import client.ClientPacket.PacketType;
import client.packets.ClientJoinPacket;
import server.ServerPacket; 

class TCPClient implements Runnable {
	
	Thread thread;
	boolean terminate;
    Socket clientSocket; 
    byte[] lastInviteData;
    
    public TCPClient (String args[]){

        // Initialize a client socket connection to the server
        try
        {
        	clientSocket = new Socket(args[0], Integer.parseInt(args[1]));        
        }
        catch (IOException e)
        {
        	System.out.println(String.format("Could not initialize socket: %s", e.getMessage()));
        	System.exit(1);
        }       	                 
    }
    
	public void processPacket(ServerPacket p)
	{		
		switch (p.pType)
		{
			case Disconnect:				
				System.out.println(p.msg);	
				System.out.println("Hit enter to continue...");	
				terminate = true;
				break;
			case InviteNotify:				
				lastInviteData = p.data;
				System.out.println(String.format("%s", p.msg));				
				break;
			default:
				System.out.println(String.format("%s", p.msg));	            
				break;
		}	
	}
	
	public void parseCommand(String input, DataOutputStream outBuffer) throws IOException
	{
		String delims = "[ ]+";
		String[] tokens = input.split(delims);
		ClientPacket packet = null;
		
		if (input.isEmpty())
			return;
		
		if (tokens.length > 0 && tokens[0].length() > 0 && tokens[0].charAt(0) == '/') { //is a command
			String command = tokens[0];
			
			switch (command) {
				case "/createaccount":
					if (tokens.length >= 3) {
						packet = ClientPacket.createAccountPacket(tokens[1],tokens[2]);
					} else {
						System.out.println("error with createaccount: must provide a username and password");
					}
					break;
				case "/login":	
					if (tokens.length >= 3) {
						packet = ClientPacket.loginPacket(tokens[1],tokens[2]);
					} else {
						System.out.println("error with login: must provide a username and password");
					}
					break;
				case "/logout":
					packet = ClientPacket.logout();
					break;
				case "/setalias":
					if (tokens.length >= 2) {
						packet = ClientPacket.setAlias(tokens[1]);
					} else {
						System.out.println("error with setalias: must provide an alias");
					}
					break;
				case "/observe":
					if (tokens.length >= 2) {						
						
						packet = ClientPacket.observe(Integer.parseInt(tokens[1]));
					} else {
						System.out.println("error with observe: must provide a room id");
					}				
					break;
				case "/join":
					if (tokens.length >= 2) {
						
						//System.out.println(tokens[1]);
						
						packet = ClientPacket.join(Integer.parseInt(tokens[1]));
					} else {
						System.out.println("error with join: must provide a room id");
					}
					break;
				case "/invite":
					if (tokens.length >= 2) {
						packet = ClientPacket.invite(tokens[1]);
					} else {
						System.out.println("error with invite: must provide a username");
					}
					break;
				case "/listusers":
					packet = ClientPacket.listUser();
					break;
				case "/listrooms":
					packet = ClientPacket.listRoom();
					break;
				case "/vote":
					if (tokens.length >= 2) {
						packet = ClientPacket.vote(tokens[1]);
					} else {
						System.out.println("error with vote: must provide a username");
					}
					break;
				case "/getgamestatus":
					packet = ClientPacket.getGameStatus();
					break;
				case "/createroom":
					if (tokens.length >= 2) {
						
						//System.out.println(tokens[1]);
						
						packet = ClientPacket.createRoom(Integer.parseInt(tokens[1]));
					} else {
						System.out.println("error with join: must provide a room id");
					}
					break;
				case "/showstate":
					packet = new ClientPacket(PacketType.ShowState, new byte[]{});
					break;
				case "/startgame":
					packet = new ClientPacket(PacketType.StartGame, new byte[]{});
					break;
				case "/leave":
					packet = new ClientPacket(PacketType.Leave, new byte[]{});
					break;
				case "/switchturn":
					packet = new ClientPacket(PacketType.SwitchTurn, new byte[]{});
					break;
				case "/ban":
					packet = ClientPacket.ban(tokens[1]);
					break;
				case "/accept":
					
					if (lastInviteData != null)
					{
						System.out.println("Accepting invite...");
						sendData(lastInviteData, outBuffer);
					}
					else
						System.out.println("error: there was no invite to accept");
					
					lastInviteData = null;
					break;
				default:
					System.out.println("Not a vaild command");
					break;
			}
			if (packet != null)
				sendPacket(packet, outBuffer);
			
		} else { //not a command, just text so use chat packet
			packet = ClientPacket.chat(input);
            sendPacket(packet, outBuffer);
		}		
	}
	
    private void sendData(byte[] data, DataOutputStream outBuffer) {
    	try
    	{
    		outBuffer.write(data);
    	}
    	catch (IOException e)
    	{
    		System.out.println("sendData err: " + e.getMessage());
    	}
	}

	static void sendPacket(ClientPacket p, DataOutputStream outBuffer) throws IOException
    {
    	int size = p.getPacketSize();
    	ByteBuffer buf = ByteBuffer.allocateDirect(size);
    	p.write(buf);
    	buf.rewind();
    	byte[] bytes = new byte[size];
    	buf.get(bytes);
    	outBuffer.write(bytes);  		
	}
    
    public void startInputThread ()
    {
        if (thread == null)
        {
      	  thread = new Thread(this, "TCPClientInput");
      	  thread.start();
        }
    }
    
    public void update() throws Exception 
    { 
        try
        {
        	DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream()); 
        	DataInputStream inData = new DataInputStream(clientSocket.getInputStream()); 
	        
        	// expect a welcome packet from server.	       
            int isDataAvailable = 0;
            
	        while (!terminate)
	        {   
		        // check for data  
	        	isDataAvailable = inData.available();	
		        
	        	// read data
	        	while (isDataAvailable > 0)
		        {
		        	// read a packet
		        	
		        	ServerPacket p = ServerPacket.read(inData);
		        	processPacket(p); // process data	       
		        	isDataAvailable = inData.available();		        	
		        }		        
	        }
	        thread.interrupt();//join();
	        // Close the socket
	        clientSocket.close();    
        }
        catch(SocketException e)		// nicely catch error if connection is closed		
        {
        	System.out.println(e);
        }       
    }

	@Override
	public void run() {
		String line = "";

		try
		{	        
			// Initialize input and an output stream for the connection(s)
	        DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream()); 
	        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
	
	        while (!terminate)
	        {
		        //System.out.print("Please enter a message to be sent to the server ('logout' to terminate): \n");
		        line = inFromUser.readLine();
		         
		        if (!line.isEmpty()) 
		        	parseCommand(line, outBuffer);
	        }
        }
		catch (IOException e)
		{
			System.out.println(String.format("error run: %s", e.getMessage()));
		}
	} 
	
    public static void main(String args[]) throws Exception 
    { 
        if (args.length != 2)
        {
            System.out.println("Usage: TCPClient <Server IP> <Server Port>");
            System.exit(1);
        }
        
        TCPClient client = new TCPClient(args);
        client.startInputThread();
        client.update();
    }
} 
