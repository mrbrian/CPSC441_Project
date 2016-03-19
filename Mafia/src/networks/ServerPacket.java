package networks;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import java.nio.ByteBuffer;
 
public class ServerPacket
{
	public enum PacketType
	{
		ServerMessage,
		InviteNotify,
		ListUsers,
		ListRooms,
		Acknowledge,
		StatusChange,
		BanUser
	}
	
	public PacketType pType;
	public int msgLength;
	public int dataSize;
	public String msg;
	public byte[] data;		
	
	public ServerPacket (
			PacketType t,
			String m,
			byte[] dataArr)
	{
		pType = t;
		dataSize = dataArr.length;
		msg = m;
		
		msgLength = msg.length();
		
		data = dataArr;
	}
	
	public void write(ByteBuffer buf)
	{
		buf.putInt(pType.ordinal());
		buf.putInt(msgLength);
		buf.putInt(dataSize);
		
		String s = msg;
		buf.put(s.getBytes());
		
		buf.put(data);
	}

	public static ServerPacket read(ByteBuffer buf)
	{
		
		PacketType pt = PacketType.values()[buf.getInt()];
		int ml = buf.getInt();
		int ds = buf.getInt();

		byte[] msgBytes = new byte[ml];
		byte[] dataBytes = new byte[ds];

		buf.get(msgBytes);
		String msg = new String(msgBytes, StandardCharsets.UTF_8);
				
		buf.get(dataBytes);
		
		ServerPacket result = new ServerPacket(pt, msg, dataBytes);		
				
		return result;	
	}

	public static ServerPacket read(DataInputStream buf) throws IOException
	{		
		PacketType pt = PacketType.values()[buf.readInt()];
		int ml = buf.readInt();
		int ds = buf.readInt();

		byte[] msgBytes = new byte[ml];
		byte[] dataBytes = new byte[ds];

		buf.readFully(msgBytes);
		String msg = new String(msgBytes, StandardCharsets.UTF_8);
				
		buf.readFully(dataBytes);
		
		ServerPacket result = new ServerPacket(pt, msg, dataBytes);		
				
		return result;	
	}

	@Override
	public boolean equals(Object o) 
	{		
		ServerPacket other = (ServerPacket)o;
		
		if (other != null)
		{
			if (pType != other.pType)
				return false;
			if (msgLength != other.msgLength)
				return false;
			if (dataSize != other.dataSize)
				return false;
			if (!msg.equals(other.msg))
				return false;
			if (!Arrays.equals(data, other.data))
				return false;
			return true;
		}
		
		return super.equals(o);
	}
	
	public static ServerPacket buildServerMessagePacket(String msg) {
		
		byte[] data = null;
		ServerPacket packet = new ServerPacket(PacketType.ServerMessage, msg, data);
		return packet;
	}
	
	// for data array, first two bytes are size of username string next bytes are username and last byte is roomid
	public static ServerPacket buildInviteNotifyPacket(String msg, String username, byte roomid) {
		byte[] user = username.getBytes();
		byte b2 = (byte) (username.length() & 0xFF);
		byte b1 = (byte) ((username.length() >> 8) & 0xFF);
		
		byte[] data = new byte[2+username.length()+1];
		data[0] = b1;
		data[1] = b2;
		for (int i = 0; i < username.length(); i++) {
			data[i+2] = user[i];
		}
		data[2+username.length()] = roomid;
		
		ServerPacket packet = new ServerPacket(PacketType.InviteNotify, msg, data);
		return packet;
	}
	
	//builds a packet for listing all users
	public static ServerPacket buildListUsersPacket(String msg, int numUsers, String usernames[]) {
		
		
		int totalUserNamesLen = 0;
		
		for (int i = 0; i < usernames.length; i++) {
			totalUserNamesLen += usernames[i].length();
		}
		
		//4 is numUsers, 2*usernames.length is username lengths, totalUserNamesLen is total space for usernames
		int totalSize = 4 + 2*usernames.length + totalUserNamesLen;
		ByteBuffer byteBuffer = ByteBuffer.allocate(totalSize);
		byte data[] = new byte[totalSize];
		
		byteBuffer.putInt(numUsers);
		
		String currentUser;
		
		for (int i = 0; i < usernames.length; i++) {
			currentUser = usernames[i];
			byteBuffer.putShort((short)currentUser.length());
			byteBuffer.put(currentUser.getBytes());
		}
		
		byteBuffer.flip();
		
		byteBuffer.get(data);
		
		ServerPacket packet = new ServerPacket(PacketType.ListUsers, msg, data);
		return packet;
		
	}
	
	public static ServerPacket buildListRoomsPacket(String msg, byte roomId, byte numPlayers, byte maxPlayers, byte active) {
		byte data[] = {roomId, numPlayers, maxPlayers, active};
		
		ServerPacket packet = new ServerPacket(PacketType.ListRooms, msg, data);
		return packet;
	}
	
	public static ServerPacket buildAcknowledgePacket(String msg, boolean success, String message) {
		
		//1 is boolean, 2 is message size, rest is message
		int totalSize = 1 + 2 + message.length();
		ByteBuffer byteBuffer = ByteBuffer.allocate(totalSize);
		byte data[] = new byte[totalSize];
		
		byte succ = (byte)(success?1:0);
		
		byteBuffer.put(succ);
		byteBuffer.putShort((short)message.length());
		byteBuffer.put(message.getBytes());
		
		byteBuffer.flip();
		
		byteBuffer.get(data);
		
		ServerPacket packet = new ServerPacket(PacketType.Acknowledge, msg, data);
		return packet;
	}
	
	public static ServerPacket buildStatusChangePacket(String msg, String message) {
		byte[] data = message.getBytes();
		
		ServerPacket packet = new ServerPacket(PacketType.StatusChange, msg, data);
		return packet;
	}
	
	public static ServerPacket buildBanUserPacket(String msg, String message) {
		byte[] data = message.getBytes();
		
		ServerPacket packet = new ServerPacket(PacketType.BanUser, msg, data);
		return packet;
	}
}
