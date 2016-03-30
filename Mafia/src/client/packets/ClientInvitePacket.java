package client.packets;

import client.ClientPacket;
import client.ClientPacket.PacketType;

public class ClientInvitePacket extends ClientPacket {

	public String invited; // the user's name whom they have invited
	
	public ClientInvitePacket(ClientPacket src) {
		super(src.type, src.data);
		invited = new String(src.data, 0, src.dataSize);		
	}
}

