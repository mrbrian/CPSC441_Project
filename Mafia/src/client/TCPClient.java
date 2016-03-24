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

import server.ServerPacket; 

class TCPClient { 

	public static void processPacket(ServerPacket p)
	{		
		switch (p.pType)
		{			
			default:
				System.out.println(String.format("%s: %s", p.pType.toString(), p.msg));	            
				break;
		}	
	}
	
	public static void parseCommand(String input, DataOutputStream outBuffer) throws IOException
	{
		String delims = "[ ]+";
		String[] tokens = input.split(delims);
		ClientPacket packet = null;
		
		if (tokens.length > 0 && tokens[0].length() > 0 && tokens[0].charAt(0) == '/') { //is a command
			String command = tokens[0];
			
			switch (command) {
				case "/createaccount":
					if (tokens[1] != null && tokens[2] != null) {
						packet = ClientPacket.createAccountPacket(tokens[1],tokens[2]);
					} else {
						System.out.println("error with createaccount: must provide a username and password");
					}
					break;
				case "/login":	
					if (tokens[1] != null && tokens[2] != null) {
						packet = ClientPacket.loginPacket(tokens[1],tokens[2]);
					} else {
						System.out.println("error with login: must provide a username and password");
					}
					break;
				case "/logout":
					packet = ClientPacket.logout();
					break;
				case "/setalias":
					if (tokens[1] != null) {
						packet = ClientPacket.setAlias(tokens[1]);
					} else {
						System.out.println("error with setalias: must provide an alias");
					}
					break;
				case "/join":
					if (tokens[1] != null) {
						packet = ClientPacket.join(Integer.parseInt(tokens[1]));
					} else {
						System.out.println("error with join: must provide a room id");
					}
					break;
				case "/invite":
					if (tokens[1] != null) {
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
					if (tokens[1] != null) {
						packet = ClientPacket.vote(tokens[1]);
					} else {
						System.out.println("error with vote: must provide a username");
					}
					break;
				case "/getgamestatus":
					packet = ClientPacket.getGameStatus();
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
	
    public static void main(String args[]) throws Exception 
    { 
        if (args.length != 2)
        {
            System.out.println("Usage: TCPClient <Server IP> <Server Port>");
            System.exit(1);
        }
        
        try
        {
            // Initialize a client socket connection to the server
            Socket clientSocket = new Socket(args[0], Integer.parseInt(args[1])); 
            
	        // Initialize input and an output stream for the connection(s)
	        DataOutputStream outBuffer = new DataOutputStream(clientSocket.getOutputStream()); 
	        //BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));         
	        DataInputStream inData = new DataInputStream(clientSocket.getInputStream());
	 
	        // Initialize user input stream
	        String line = ""; 
	        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

	        // expect a welcome packet from server.
	        
	        int isDataAvailable = 0;
	        	        
	        while (!line.equals("logout"))
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
		        
	            System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");

	            // Get user input and parse command
	            line = inFromUser.readLine(); 
	            parseCommand(line, outBuffer);
	        }
	        
	        // Close the socket
	        clientSocket.close();    
        }
        catch(SocketException e)		// nicely catch error if connection is closed		
        {
        	System.out.println(e);
        }       
    } 
} 
