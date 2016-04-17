package dm341.index;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class BasicIndex implements BaseIndex {

	@Override
	public PostingList readPosting(FileChannel fc) throws IOException{
		/*
		 * TODO: Your code here
		 *       Read and return the postings list from the given file.
		 */
		long oldPos = fc.position();
		ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
		ByteBuffer buf = ByteBuffer.allocate(128);
		
		int readSize = 0;
		while (readSize < 8) {
			int s = fc.read(buf);
			if (s < 0) return null;
			readSize += s;
		}
		
		buf.flip();
		int termId = buf.getInt();
		int numBytes = buf.getInt();
		
		fc.position(oldPos + 8);
		buf.rewind();
		readSize = 0;
		
		while (readSize < numBytes) {
			int s = fc.read(buf);
			readSize += s;
			bufStream.write(buf.array(), 0, s);
			buf.rewind();
		}
		
		fc.position(oldPos + 8 + numBytes);
		
		int numPostings = numBytes/4;
		ByteBuffer postingBuf = ByteBuffer.wrap(bufStream.toByteArray());
		List<Integer> postings = new ArrayList<Integer>();
		for (int i = 0; i < numPostings; i++) {
			postings.add(postingBuf.getInt());
		}
		return new PostingList(termId, postings);
	}

	@Override
	public void writePosting(FileChannel fc, PostingList p) throws IOException {
		/*
		 * TODO: Your code here
		 *       Write the given postings list to the given file.
		 */
		/*
		String newData = p.toString();
		
		ByteBuffer buf = ByteBuffer.wrap(newData.getBytes());
		while(buf.hasRemaining()) {
		    fc.write(buf);
		}*/
		
		ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
		
		bufStream.write(intToByteArray(p.getTermId()));
		bufStream.write(intToByteArray(4*p.getList().size()));
		
		for (int i = 0; i < p.getList().size(); i++) {
			bufStream.write(intToByteArray(p.getList().get(i)));
		}
		
		ByteBuffer buf = ByteBuffer.wrap(bufStream.toByteArray());
		
		while(buf.hasRemaining()) {
		    fc.write(buf);
		}
	}

	public static byte[] intToByteArray(int n) {
		return ByteBuffer.allocate(4).putInt(n).array();
	}
}
