package cs276.assignments;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public interface BaseIndex {
	
	public PostingList readPosting (FileChannel fc) throws IOException;
	
	public void writePosting (FileChannel fc, PostingList p) throws IOException;
	
	public static byte[] intToByteArray(int n) {
		return ByteBuffer.allocate(4).putInt(n).array();
	}
}
