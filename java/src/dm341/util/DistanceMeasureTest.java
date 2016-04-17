package dm341.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class DistanceMeasureTest {

	@Test
	public void test() {
		assertTrue(DistanceMeasure.editDistanceScore("", "") == 1);
		assertTrue(DistanceMeasure.editDistanceScore("a", "") == 0);
		assertTrue(DistanceMeasure.editDistanceScore("ab", "ba") == 0);
		assertTrue(DistanceMeasure.editDistanceScore("abc", "bca") - 1.0/3 < 1E-8);
		assertTrue(DistanceMeasure.editDistanceScore("abcd", "rut") == 0);
		assertTrue(DistanceMeasure.editDistanceScore("abcd", "abc") == 0.75);
	}

}
