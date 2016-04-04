package client.packets;

import client.ClientPacket;
import client.ClientPacket.PacketType;

public class ClientBanPacket extends ClientPacket {

	public String username;
	
	public ClientBanPacket(ClientPacket src) {
		super(src.type, src.data);
		username = new String(src.data, 0, src.dataSize);
	}
}

