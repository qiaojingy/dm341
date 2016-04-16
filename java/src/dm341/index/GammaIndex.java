package dm341.index;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class GammaIndex implements BaseIndex {

	@Override
	public PostingList readPosting(FileChannel fc) throws IOException {
		/*
		 * TODO: Your code here
		 */
		long oldPos = fc.position();
		ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
		ByteBuffer buf = ByteBuffer.allocate(48);
		
		long readSize = 0;
		while (readSize < 8) {
			int s = fc.read(buf);
			if (s < 0) return null;
			readSize += s;
		}
		
		buf.flip();
		int termId = buf.getInt();
		int numBits = buf.getInt();
		
		fc.position(oldPos + 8);
		buf.rewind();
		readSize = 0;
		
		while (readSize < (numBits + 1 + 7)/8) {
			int s = fc.read(buf);
			if (s == -1) break;
			readSize += s;
			bufStream.write(buf.array(), 0, s);
			buf.rewind();
		}
		
		fc.position(oldPos + 8 + (numBits + 1 + 7)/8);
		//gammaDecodeInteger(BitSet inputGammaCode, int startIndex, int[] numberEndIndex) 
		//@param numberEndIndex Return values: index 0 holds the decoded number; index 1 holds the index
		// *                       position in inputGammaCode immediately following the gamma code.
		
		
		int[] numberEndIndex = new int[2];
		List<Integer> postings = new ArrayList<Integer>();
		BitSet inputGammaCode = BitSet.valueOf(bufStream.toByteArray());
		int startIndex = 0;
		while (startIndex < numBits) {
			gammaDecodeInteger(inputGammaCode, startIndex, numberEndIndex);
			postings.add(numberEndIndex[0] + (postings.size() == 0 ? -1 : postings.get(postings.size()-1)));
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

		bufStream1.write(BaseIndex.intToByteArray(p.getTermId()));
		
		BitSet outputGammaCode = new BitSet();
		int numBits = 0;
		for (int i = 0; i < p.getList().size(); i++) {
			int gap = 0;
			if (i == 0) gap = p.getList().get(0) + 1;
			else gap = p.getList().get(i) - p.getList().get(i - 1);
			numBits = gammaEncodeInteger(gap, outputGammaCode, numBits);
			//bufStream2.write(outputGammaCode.toByteArray());
		}
		outputGammaCode.set(numBits);

		byte[] gammaCodes = outputGammaCode.toByteArray();
		
		bufStream2.write(gammaCodes);
		bufStream1.write(BaseIndex.intToByteArray(numBits));
		
		
		ByteBuffer buf1 = ByteBuffer.wrap(bufStream1.toByteArray());
		ByteBuffer buf2 = ByteBuffer.wrap(bufStream2.toByteArray());

		
		while(buf1.hasRemaining()) {
		    fc.write(buf1);
		}
		while(buf2.hasRemaining()) {
		    fc.write(buf2);
		}
	}

	/**
	 * Encodes a number using unary code.  The unary code for the number is placed in the BitSet
	 * outputUnaryCode starting at index startIndex.  The method returns the BitSet index that
	 * immediately follows the end of the unary encoding.  Use startIndex = 0 to place the unary
	 * encoding at the beginning of the outputUnaryCode.
	 *
	 * Examples:
	 * If number = 5, startIndex = 3, then unary code 111110 is placed in outputUnaryCode starting
	 * at the 4th bit position and the return value 9.
	 *
	 * @param number          The number to be unary encoded
	 * @param outputUnaryCode The unary code for number is placed into this BitSet
	 * @param startIndex      The unary code for number starts at this index position in outputUnaryCode
	 * @return The next index position in outputUnaryCode immediately following the unary code for number
	 */
	public static int unaryEncodeInteger(int number, BitSet outputUnaryCode, int startIndex) {
		int nextIndex = startIndex + number + 1;
		// TODO: Fill in your code here
		for (int i = startIndex; i < startIndex + number; i++) {
			outputUnaryCode.set(i);
		}
		outputUnaryCode.clear(startIndex + number);
		return nextIndex;
	}


	/**
	 * Decodes the unary coded number in BitSet inputUnaryCode starting at (0-based) index startIndex.
	 * The decoded number is returned in numberEndIndex[0] and the index position immediately following
	 * the encoded value in inputUnaryCode is returned in numberEndIndex[1].
	 *
	 * @param inputUnaryCode BitSet containing the unary code
	 * @param startIndex     Unary code starts at this index position
	 * @param numberEndIndex Return values: index 0 holds the decoded number; index 1 holds the index
	 *                       position in inputUnaryCode immediately following the unary code.
	 */
	public static void unaryDecodeInteger(BitSet inputUnaryCode, int startIndex, int[] numberEndIndex) {
		// TODO: Fill in your code here
		int num = 0;
		while (inputUnaryCode.get(startIndex)) {
			startIndex++;
			num++;
		}
		numberEndIndex[0] = num;
		numberEndIndex[1] = startIndex + 1;
	}


	/**
	 * Gamma encodes number.  The encoded bits are placed in BitSet outputGammaCode starting at
	 * (0-based) index position startIndex.  Returns the index position immediately following the
	 * encoded bits.  If you try to gamma encode 0, then the return value should be startIndex (i.e.,
	 * it does nothing).
	 *
	 * @param number          Number to be gamma encoded
	 * @param outputGammaCode Gamma encoded bits are placed in this BitSet starting at startIndex
	 * @param startIndex      Encoded bits start at this index position in outputGammaCode
	 * @return Index position in outputGammaCode immediately following the encoded bits
	 */
	public static int gammaEncodeInteger(int number, BitSet outputGammaCode, int startIndex) {
		int nextIndex = startIndex;
		// TODO: Fill in your code here
		if (number == 0) return startIndex;
		int digits = 0;
		int num = number;
		while (num > 0) {
			digits++;
			num /= 2;
		}
		nextIndex = unaryEncodeInteger(digits - 1, outputGammaCode, startIndex);
		int mask = 1 << (digits - 2);
		for (int i = 0; i < digits - 1; i++) {
			if ((number & mask) != 0) outputGammaCode.set(nextIndex);
			else outputGammaCode.clear(nextIndex);
			nextIndex++;
			mask >>= 1;
		}
		return nextIndex;
	}


	/**
	 * Decodes the Gamma encoded number in BitSet inputGammaCode starting at (0-based) index startIndex.
	 * The decoded number is returned in numberEndIndex[0] and the index position immediately following
	 * the encoded value in inputGammaCode is returned in numberEndIndex[1].
	 *
	 * @param inputGammaCode BitSet containing the gamma code
	 * @param startIndex     Gamma code starts at this index position
	 * @param numberEndIndex Return values: index 0 holds the decoded number; index 1 holds the index
	 *                       position in inputGammaCode immediately following the gamma code.
	 */
	public static void gammaDecodeInteger(BitSet inputGammaCode, int startIndex, int[] numberEndIndex) {
		// TODO: Fill in your code here
		unaryDecodeInteger(inputGammaCode, startIndex, numberEndIndex);
		int digits = numberEndIndex[0] + 1;
		int nextIndex = numberEndIndex[1];
		int num = 1;
		for (int i = 0; i < digits - 1; i++) {
			int b = inputGammaCode.get(nextIndex) ? 1:0;
			num = num * 2 + b;
			nextIndex++;
		}
		numberEndIndex[0] = num;
		numberEndIndex[1] = nextIndex;
	}
	
	public static void main(String[] args) {
		
		BitSet GammaCode = new BitSet();/*
		int end = gammaEncodeInteger(0, GammaCode, 0);
		System.out.println(GammaCode.toByteArray()[0]);
		System.out.println(end);*/
		
		int end = gammaEncodeInteger(6, GammaCode, 0);
		System.out.println(GammaCode.toByteArray()[0]);
		System.out.println(end);
		
		
		int[] numberEndIndex = new int[2];
		gammaDecodeInteger(GammaCode, 0, numberEndIndex);
		System.out.println(numberEndIndex[0]);
		System.out.println(numberEndIndex[1]);
	}
	

}
