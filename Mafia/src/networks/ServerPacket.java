package networks;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

 
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
}
