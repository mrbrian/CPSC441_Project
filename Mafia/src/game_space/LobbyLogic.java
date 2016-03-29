package game_space;

import client.ClientPacket;
import client.packets.ClientInvitePacket;
import client.packets.ClientJoinPacket;
import players.Player;
import server.Outbox;
import server.PlayerManager;
import server.ServerPacket;
import server.ServerPacket.PacketType;

public abstract class LobbyLogic {

	ReadyRoom room;
	
	public LobbyLogic(ReadyRoom r){
		room = r;
	}
	
	public void update(float elapsedTime){
		
	}
	
	public void processPacket(ClientPacket p, Player player){
		switch(p.type)
		{
			case Chat:
			{
				String msg = new String(p.data, 0, p.dataSize);
				String showStr = String.format("Chat [%s]: %s", player.getUsername(), msg); 
				room.sendMessageRoom(showStr);
				System.out.println(showStr);
			}
			break;
			case Leave:
			{
				String showStr = String.format("%s left the room.", player.getUsername());
				room.sendMessageRoom(showStr);
				player.leaveRoom();				
			}
			break;
			case Invite:
			{
				String inviteStr = String.format("%s has sent you an invite!  Enter \"/accept\" to join them.", player.getUsername());
				ClientInvitePacket cip = new ClientInvitePacket(p);
				
				ClientJoinPacket sendJoin = ClientPacket.join(room.getId());
				
				ServerPacket out_pkt = new ServerPacket(PacketType.InviteNotify, inviteStr, sendJoin.toBytes()); 

				Player target = PlayerManager.findPlayerByName(cip.invited);				
				Outbox.sendPacket(out_pkt, target.getChannel());
				player.leaveRoom();				
			}
			break;
			default:
				Outbox.sendMessage(String.format("Bad room command: %s", p.type.toString()), player.getChannel());
				break;
		}
	}
}
