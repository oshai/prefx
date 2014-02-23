package oshai.prefx.common;

public class Utils {

	public static boolean isEmpty(String value) {
		return null == value || "".equals(value.trim());
	}

	public static void sleep(long milli) {
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
