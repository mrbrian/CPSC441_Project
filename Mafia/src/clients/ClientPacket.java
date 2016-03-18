package clients;

public class ClientPacket {
	
	private byte type;
	private int dataSize;
	private byte[] data;
	
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
	
	public ClientPacket(byte type, int dataSize, byte[] data){
		this.type = type;
		this.dataSize = dataSize;
		this.data = data;
	}
}
