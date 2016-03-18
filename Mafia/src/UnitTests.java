import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import org.junit.Test;

import networks.ServerPacket;

public class UnitTests {

	@Test
	public void stream_Test1() {
		ServerPacket p = new ServerPacket(
				ServerPacket.PacketType.ServerMessage,
				5,
				0,
				new String[]{"Hello"},
					new byte[]{}
				);
		
		int BUFFERSIZE = 256;
		
		ByteBuffer bb = ByteBuffer.allocateDirect(BUFFERSIZE);
		byte[] expected = new byte[]{0};
		p.write(bb);
		byte[] actual = new byte[BUFFERSIZE];
		bb.get(actual);
		
		assertEquals(expected, actual);
		//fail("Not yet implemented");
	}

}
