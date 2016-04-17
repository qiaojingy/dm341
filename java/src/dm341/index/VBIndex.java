package cs276.assignments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class VBIndex implements BaseIndex {

	@Override
	public PostingList readPosting(FileChannel fc) throws IOException {
		/*
		 * TODO: Your code here
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
		
		int[] numberEndIndex = new int[2];
		List<Integer> postings = new ArrayList<Integer>();
		byte[] inputVBCode = bufStream.toByteArray();
		int startIndex = 0;
		while (startIndex < numBytes) {
			VBDecodeInteger(inputVBCode, startIndex, numberEndIndex);
			postings.add(numberEndIndex[0] + (postings.size() == 0 ? 0: postings.get(postings.size()-1)));
			startIndex = numberEndIndex[1];
		}
		return new PostingList(termId, postings);
	}
	
	@Override
	public void writePosting(FileChannel fc, PostingList p) throws IOException {
		/*
		 * TODO: Your code here
		 */
		ByteArrayOutputStream bufStream1 = new ByteArrayOutputStream();
		ByteArrayOutputStream bufStream2 = new ByteArrayOutputStream();
		
		bufStream1.write(intToByteArray(p.getTermId()));
		
		byte[] outputVBCode = new byte[20];
		int size = 0;
		for (int i = 0; i < p.getList().size(); i++) {
			int gap = 0;
			if (i == 0) gap = p.getList().get(0);
			else gap = p.getList().get(i) - p.getList().get(i - 1);
			int curSize = VBEncodeInteger(gap, outputVBCode);
			size += curSize;
			bufStream2.write(outputVBCode, 0, curSize);
		}

		bufStream1.write(intToByteArray(size));
		
		
		ByteBuffer buf1 = ByteBuffer.wrap(bufStream1.toByteArray());
		ByteBuffer buf2 = ByteBuffer.wrap(bufStream2.toByteArray());
		
		while(buf1.hasRemaining()) {
		    fc.write(buf1);
		}
		while(buf2.hasRemaining()) {
		    fc.write(buf2);
		}
	}

	public static byte[] intToByteArray(int n) {
		return ByteBuffer.allocate(4).putInt(n).array();
	}

	/**
	 * Gap encodes a postings list.  The DocIds in the postings list are provided
	 * in the array inputDocIdsOutputGaps.  The output gaps are placed right back
	 * into this array, replacing each docId with the corresponding gap.
	 *
	 * Example:
	 * If inputDocIdsOutputGaps is initially {5, 1000, 1005, 1100}
	 * then at the end inputDocIdsOutputGaps is set to {5, 995, 5, 95}
	 *
	 * @param inputDocIdsOutputGaps The array of input docIds.
	 *                              The output gaps are placed back into this array!
	 */
	public static void gapEncode(int[] inputDocIdsOutputGaps) {
		// TODO: Fill in your code here
		for (int i = inputDocIdsOutputGaps.length - 1; i > 0; i--) {
			inputDocIdsOutputGaps[i] -= inputDocIdsOutputGaps[i - 1];
		}
	}


	/**
	 * Decodes a gap encoded postings list into the corresponding docIds.  The input
	 * gaps are provided in inputGapsOutputDocIds.  The output docIds are placed
	 * right back into this array, replacing each gap with the corresponding docId.
	 *
	 * Example:
	 * If inputGapsOutputDocIds is initially {5, 905, 5, 95}
	 * then at the end inputGapsOutputDocIds is set to {5, 1000, 1005, 1100}
	 *
	 * @param inputGapsOutputDocIds The array of input gaps.
	 *                              The output docIds are placed back into this array.
	 */
	public static void gapDecode(int[] inputGapsOutputDocIds) {
		// TODO: Fill in your code here
		for (int i = 1; i < inputGapsOutputDocIds.length; i++) {
			inputGapsOutputDocIds[i] += inputGapsOutputDocIds[i - 1];
		}
	}


	/**
	 * Encodes gap using a VB code.  The encoded bytes are placed in outputVBCode.
	 * Returns the number bytes placed in outputVBCode.
	 *
	 * @param gap          gap to be encoded.  Assumed to be greater than or equal to 0.
	 * @param outputVBCode VB encoded bytes are placed here.  This byte array is assumed to be large
	 *                     enough to hold the VB code for gap (e.g., Integer.SIZE/7 + 1).
	 * @return Number of bytes placed in outputVBCode.
	 */
	public static int VBEncodeInteger(int gap, byte[] outputVBCode) {
		int numBytes = 0;
		// TODO: Fill in your code here
		while (true) {
			if (numBytes == 0) {
				outputVBCode[numBytes] = (byte) (gap % 128 + 128);
			} else {
				outputVBCode[numBytes] = (byte) (gap % 128);
			}
			numBytes++;
			if (gap < 128) {
				break;
			}
			gap /= 128;
		}
		for (int i = 0; i < numBytes/2; i++) {
			byte temp = outputVBCode[i];
			outputVBCode[i] = outputVBCode[numBytes - 1 - i];
			outputVBCode[numBytes - 1 - i] = temp;
		}
		return numBytes;
	}


	/**
	 * Decodes the first integer encoded in inputVBCode starting at index startIndex.  The decoded
	 * number is placed in the element zero of the numberEndIndex array and the index position
	 * immediately after the encoded value is placed in element one of the numberEndIndex array.
	 *
	 * @param inputVBCode    Byte array containing the VB encoded number starting at index startIndex.
	 * @param startIndex     Index in inputVBCode where the VB encoded number starts
	 * @param numberEndIndex Outputs are placed in this array.  The first element is set to the
	 *                       decoded number and the second element is set to the index of inputVBCode
	 *                       immediately after the end of the VB encoded number.
	 * @throws IllegalArgumentException If not a valid variable byte code
	 */
	public static void VBDecodeInteger(byte[] inputVBCode, int startIndex, int[] numberEndIndex) {
		// TODO: Fill in your code here
		int num = 0;
		while (true) {
			if (startIndex == inputVBCode.length) throw new IllegalArgumentException();
			int cur = (int) inputVBCode[startIndex];
			if (num == 0 && cur == 0) throw new IllegalArgumentException();
			startIndex++;
			if (cur < 0) cur += 256;
			if (cur >= 128) {
				num = num * 128 + cur - 128;
				break;
			} else {
				num = num * 128 + cur;
			}
		}
		numberEndIndex[0] = num;
		numberEndIndex[1] = startIndex;
	}
}
