package clients;

import java.nio.ByteBuffer;

public class ClientPacket {
	
	private enum PacketType{
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
		GetGameStatus
	}
	
	public PacketType type;
	public int dataSize;
	public byte[] data;
	
	// Class constructor initializing the fields
	public ClientPacket(PacketType type, int dataSize, byte[] data){
		this.type = type;
		this.dataSize = dataSize;
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
	
	/* Method to read from the ByteBuffer
	 * Return is of type ClientPacket because
	 * we need to return multiple things
	 * */
	public ClientPacket read(ByteBuffer buffer){
		PacketType typeLocal = PacketType.values()[buffer.getInt()];
		int sizeLocal = buffer.getInt();
		byte[] dataBytes = new byte[sizeLocal];
		buffer.get(dataBytes);
		
		return new ClientPacket(typeLocal, sizeLocal, dataBytes);
	}	
}
