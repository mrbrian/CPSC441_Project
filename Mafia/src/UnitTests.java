import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import org.junit.Test;

import networks.ServerPacket;
import networks.ServerPacket.PacketType;

public class UnitTests {

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

}
