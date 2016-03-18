package networks;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
	public String[] msgs;
	public byte[] data;	
	
	public ServerPacket (
			PacketType t,
			int msgL,
			int dataS,
			String[] msgArr,
			byte[] dataArr)
	{
		pType = t;
		msgL = msgLength;
		msgLength = dataS;
		msgs = msgArr;
		data = dataArr;
	}
	
	public void write(ByteBuffer buf)
	{
		buf.putInt(pType.ordinal());
		buf.putInt(msgLength);
		buf.putInt(dataSize);
		for(int i = 0 ; i < msgs.length; i++)
		{
			String s = msgs[i];
			buf.put(s.getBytes());
		}
		buf.put(data);
	}
	
	public void write(ObjectOutputStream out)
	{
		try
		{
			out.writeObject(pType);
			out.writeInt(msgLength);
			out.writeInt(dataSize);
			out.writeObject(msgs);
			out.write(data);
		}
		catch(IOException e)
		{
            System.out.println(e.getMessage());
		}
	}
}
