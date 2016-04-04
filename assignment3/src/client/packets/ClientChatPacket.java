package client.packets;

import client.ClientPacket;
import client.ClientPacket.PacketType;

public class ClientChatPacket extends ClientPacket {

	public String message;
	
	public ClientChatPacket(ClientPacket src) {
		super(src.type, src.data);
		message = new String(src.data, 0, src.dataSize);
	}
}

