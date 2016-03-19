import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.nio.ByteBuffer;
import org.junit.Test;

import clients.ClientPacket;
import networks.ServerPacket;
import networks.ServerPacket.PacketType;

public class UnitTests {
/*
	@Test
	public void dataInputStream_test1() 
	{
		DataInputStream dis = new DataInputStream(InputStream);		
		
		int e = 1;
		int a = dis.available();
		
		assertEquals(e, a);
	}
*/
	@Test
	public void serverPacket_readWrite_Test1_Pass() 
	{
		ServerPacket p = new ServerPacket(
				ServerPacket.PacketType.BanUser,
				"Hello",
				new byte[]{6,6,6}
			);		
		
		int BUFFERSIZE = 256;
		
		ByteBuffer bb = ByteBuffer.allocateDirect(BUFFERSIZE);
		ServerPacket expected = p;
		p.write(bb);
		
		bb.flip();
		
		ServerPacket actual = ServerPacket.read(bb);
		
		assertEquals(expected.pType, actual.pType);
		assertEquals(expected.msgLength, actual.msgLength);
		assertEquals(expected.dataSize, actual.dataSize);
		assertEquals(expected.msg, actual.msg);
		assertArrayEquals(expected.data, actual.data);
	}

	@Test
	public void serverPacket_readWrite_Test1_Fail() 
	{
		ServerPacket expected = new ServerPacket(
				ServerPacket.PacketType.BanUser,
				"Hello",
				new byte[]{6,6,6,6}
			);		
		
		ServerPacket p = new ServerPacket(
				ServerPacket.PacketType.BanUser,
				"Hello",
				new byte[]{6,6,6}
			);		
		
		int BUFFERSIZE = 256;
		
		ByteBuffer bb = ByteBuffer.allocateDirect(BUFFERSIZE);
		p.write(bb);
		
		bb.flip();

		ServerPacket actual = ServerPacket.read(bb);
		
		if (expected.equals(actual))
			fail("non match");
	}
	
	@Test
	public void clientPacket_readWrite_Test1_Pass(){
		ClientPacket clientPacket = new ClientPacket(
				ClientPacket.PacketType.Login, new byte[]{6,6,6});
		
		int BUFFERSIZE = 256;
		ByteBuffer bb = ByteBuffer.allocateDirect(BUFFERSIZE);
		ClientPacket expected = clientPacket;
		clientPacket.write(bb);
		
		
		bb.flip();
		
		ClientPacket actual = ClientPacket.read(bb);
		
		if(!expected.equals(actual)){
			fail("non match");
		}
	}
}
