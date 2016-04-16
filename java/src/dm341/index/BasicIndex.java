package dm341.index;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
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
		ByteBuffer buf = ByteBuffer.allocate(16);
		
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
		if (termId == 20) {
			System.out.println("posting: " + postings);
		}
		if (termId < 100) {
			System.out.println("In readposting termId: " + termId);
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
		
		bufStream.write(BaseIndex.intToByteArray(p.getTermId()));
		bufStream.write(BaseIndex.intToByteArray(4*p.getList().size()));
		
		for (int i = 0; i < p.getList().size(); i++) {
			bufStream.write(BaseIndex.intToByteArray(p.getList().get(i)));
		}
		
		ByteBuffer buf = ByteBuffer.wrap(bufStream.toByteArray());
		
		while(buf.hasRemaining()) {
		    fc.write(buf);
		}
	}
	

	
	public static void main(String[] args) {
		
		//BitSet GammaCode = new BitSet();
		
		try {
			RandomAccessFile testFile = new RandomAccessFile("testFile.txt", "rw");
			FileChannel fc = testFile.getChannel();
			ArrayList<Integer> posting = new ArrayList<Integer>();
			for (int i = 1; i < 6553; i+= 1) {
				posting.add(i);
			}
			/*
			posting.add(1);
			posting.add(5);
			posting.add(7);
			posting.add(8);*/
			
			PostingList pl = new PostingList(4, posting);
			System.out.println("encoded:" + pl);
			BaseIndex bi = new BasicIndex();
			bi.writePosting(fc, pl);
			fc.position(0);
			
			PostingList rpl = bi.readPosting(fc);
			System.out.println("decoded:" + rpl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
