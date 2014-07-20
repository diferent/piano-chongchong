import static org.junit.Assert.*;
import ivy.basic.AppException;

import org.junit.Test;

import chong.ReadList;


/**
 */
public class ReadTest {

	@Test
	public void test() {
		try {
			ReadList.read();
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

}
