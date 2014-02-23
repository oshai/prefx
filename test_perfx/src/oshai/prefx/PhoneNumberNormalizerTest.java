package oshai.prefx;

import static org.junit.Assert.assertEquals;

import javax.inject.Provider;

import org.junit.Test;

import oshai.prefx.phone_numbers.PhoneNumberNormalizer;

public class PhoneNumberNormalizerTest {

@Test
public void test1() {
	String number = new PhoneNumberNormalizer(new ForTestingProvider()).removeSmsto("smsto:054");
	assertEquals("054", number);
}
@Test
public void test11() {
	String number = new PhoneNumberNormalizer(new ForTestingProvider()).convertSpecialChars("054-054");
	assertEquals("054054", number);
}
@Test
public void test2() {
	String number = new PhoneNumberNormalizer(new ForTestingProvider()).convertSpecialChars("%2B9");
	assertEquals("+9", number);
}
@Test
public void test3() {
	String number = new PhoneNumberNormalizer(new ForTestingProvider()).convertSpecialChars("%209");
	assertEquals("9", number);
}
@Test
public void test4() {
	String number = new PhoneNumberNormalizer(new ForTestingProvider()).getNormalizedNumberWithCountryPrefix("5");
	assertEquals("+972", number);
}
public static class ForTestingProvider implements Provider<String> {

	@Override
	public String get() {
		return "+972";
	}

}
}
