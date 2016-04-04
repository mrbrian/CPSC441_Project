package client.packets;

import java.nio.ByteBuffer;
import client.ClientPacket;
import client.ClientPacket.PacketType;


public class ClientJoinPacket extends ClientPacket {

	public int roomId;
	
	public ClientJoinPacket(ClientPacket src) {
		super(src.type, src.data);
		
		ByteBuffer bb = ByteBuffer.allocate(src.dataSize);
		bb.put(data);
		bb.flip();
		
		roomId = bb.getInt();		
	}

	public ClientJoinPacket(PacketType join, byte[] data) {
		super(join, data);
	}

	public byte[] toBytes() {
		int size = 4 + 4 + data.length;
		ByteBuffer bb = ByteBuffer.allocate(size);   // type + datasize + data length
		bb.putInt(this.type.ordinal());
		bb.putInt(this.dataSize);
		bb.put(data);
		bb.flip();
		
		byte[] result = new byte[size];
		bb.get(result);
		return result; 
	}
}

