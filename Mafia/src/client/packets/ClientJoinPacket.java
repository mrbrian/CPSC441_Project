package client.packets;

import java.nio.ByteBuffer;
import client.ClientPacket;


public class ClientJoinPacket extends ClientPacket {

	public int roomId;
	
	public ClientJoinPacket(ClientPacket src) {
		super(src.type, src.data);
		
		ByteBuffer bb = ByteBuffer.allocate(src.dataSize);
		bb.put(data);
		bb.flip();
		
		roomId = bb.getInt();		
	}
}

