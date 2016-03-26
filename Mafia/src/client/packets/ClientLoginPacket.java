package client.packets;

import java.nio.ByteBuffer;
import client.ClientPacket;


public class ClientLoginPacket extends ClientPacket {

	public String username;
	public String password;
	
	public ClientLoginPacket(ClientPacket src) {
		super(src.type, src.data);
		
		ByteBuffer bb = ByteBuffer.allocate(src.dataSize);
		bb.put(data);
		bb.flip();
		
		int name_size = bb.getShort();
		int pwdOffset = 4 + name_size;
		username = new String(src.data, 2, name_size);
		password = new String(src.data, pwdOffset, src.dataSize - (pwdOffset));
	}
}

