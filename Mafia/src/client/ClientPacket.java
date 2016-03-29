package client;

import java.nio.ByteBuffer;
import java.util.Arrays;

import client.packets.ClientJoinPacket;

public class ClientPacket {
	private static final int USR_PASS_LENGTHS = 4;
	
	public enum PacketType{
		CreateAccount,
		Login,
		Logout,
		SetAlias,
		Join,
		Invite,
		ListUsers,
		ListRooms,
		Chat,
		Vote,
		GetGameStatus,
		CreateRoom,
		ShowState,
		StartGame,
		SwitchTurn,
		Leave
	}
	
	public PacketType type;
	public int dataSize;
	public byte[] data;
	
	// Class constructor initializing the fields
	public ClientPacket(PacketType type, byte[] data){
		this.type = type;
		this.dataSize = data.length;
		this.data = data;
	}
	
	/* Method that takes a ByteBuffer as argument and
	 * writes to it the fields of this class
	 * */
	public void write(ByteBuffer buffer){
		buffer.putInt(type.ordinal());
		buffer.putInt(dataSize);
		buffer.put(data);
	}
	

	//login to account packet
	public static ClientPacket loginPacket(String username, String password){
		int totalSize = username.length() + password.length() + 4;
		ByteBuffer buffer = ByteBuffer.allocate(totalSize);
		
		buffer.putShort((short)username.length());
		buffer.put(username.getBytes());
		
		buffer.putShort((short)password.length());
		buffer.put(password.getBytes());
		
		buffer.flip();
		
		byte[] credentials = new byte[totalSize];
		buffer.get(credentials);
		
		return new ClientPacket(PacketType.Login, credentials);
	}
	
	
	public static ClientPacket createAccountPacket(String username, String password){
		int totalSize = username.length() + password.length() + 4;
		ByteBuffer buffer = ByteBuffer.allocate(totalSize);
		
		buffer.putShort((short)username.length());
		buffer.put(username.getBytes());
		
		buffer.putShort((short)password.length());
		buffer.put(password.getBytes());
		
		buffer.flip();
		
		byte[] credentials = new byte[totalSize];
		buffer.get(credentials);
		
		return new ClientPacket(PacketType.CreateAccount, credentials);
	}
	
	/* Could possibly merge all these "mini methods" into a single method
	 * and add an extra parameter to indicate what type of event it is
	 * */
	public static ClientPacket logout(){
		return new ClientPacket(PacketType.Logout, new byte[0]);
	}
	
	public static ClientPacket setAlias(String alias){
		return new ClientPacket(PacketType.SetAlias, alias.getBytes());
	}
	
	public static ClientJoinPacket join(int roomId){
		int totalSize = 4;
		ByteBuffer buffer = ByteBuffer.allocate(totalSize);
		
		buffer.putInt(roomId);
		buffer.flip();
		
		byte[] data = new byte[totalSize];
		buffer.get(data);
		
		return new ClientJoinPacket(PacketType.Join, data);
	}
	
	public static ClientPacket invite(String username){
		return new ClientPacket(PacketType.Invite, username.getBytes());
	}
	
	public static ClientPacket listUser(){
		return new ClientPacket(PacketType.ListUsers, new byte[0]);
	}
	
	public static ClientPacket listRoom(){
		return new ClientPacket(PacketType.ListRooms, new byte[0]);
	}
	
	public static ClientPacket chat(String msg){
		return new ClientPacket(PacketType.Chat, msg.getBytes());
	}
	
	public static ClientPacket vote(String username){
		return new ClientPacket(PacketType.Vote, username.getBytes());
	}
	
	public static ClientPacket getGameStatus(){
		return new ClientPacket(PacketType.GetGameStatus, new byte[0]);
	}
	
	public static ClientPacket createRoom(int roomId) {
		int totalSize = 4;
		ByteBuffer buffer = ByteBuffer.allocate(totalSize);
		
		buffer.putInt(roomId);
		buffer.flip();
		
		byte[] data = new byte[totalSize];
		buffer.get(data);
		
		return new ClientPacket(PacketType.CreateRoom, data);
	}

	
	/* Method to read from the ByteBuffer
	 * Return is of type ClientPacket because
	 * we need to return multiple things
	 * */
	public static ClientPacket read(ByteBuffer buffer){
		PacketType typeLocal = PacketType.values()[buffer.getInt()];
		int sizeLocal = buffer.getInt();
		byte[] dataBytes = new byte[sizeLocal];
		buffer.get(dataBytes);
		
		return new ClientPacket(typeLocal, dataBytes);
	}
	
	// Used for JUnit testing
	@Override
	public boolean equals(Object obj){
		ClientPacket otherPacket = (ClientPacket)obj;
		
		if (otherPacket != null){
			if(type != otherPacket.type){
				return false;
			}
			if(dataSize != otherPacket.dataSize){
				return false;
			}
			
			if(!Arrays.equals(data, otherPacket.data)){
				return false;
			}
			
			return true;
		}
		
		return super.equals(obj);
	}
	
	/* Convenience method that returns the size of the packet
	 * 4 = 2 bytes (for the enum type) + 2 bytes (for the data size)
	 * */
	public int getPacketSize(){
		return 8 + data.length;
	}
}
