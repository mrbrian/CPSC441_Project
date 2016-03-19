import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import org.junit.Test;

import clients.ClientPacket;
import networks.ServerPacket;

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
		if (!expected.equals(actual))
			fail("non match");
		//assertArrayEquals(expected, actual);
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
			fail("shouldn't match");
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
