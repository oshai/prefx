package oshai.prefx.phone_numbers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import android.util.Log;

public class PhoneNumberNormalizer {
private static final String TAG = PhoneNumberNormalizer.class.getSimpleName();
private Provider<String> countryPrefixProvider;


@Inject
public PhoneNumberNormalizer(@Named("countryPrefix") Provider<String> countryPrefixProvider) {
	super();
	this.countryPrefixProvider = countryPrefixProvider;
}

public String convertSpecialChars(String number) {
	int i = 0;
	StringBuilder $ = new StringBuilder();
	while (i < number.length()) {
		char current = number.charAt(i);
		if (current == '%') {
			String hexa = "" + number.charAt(i + 1) + number.charAt(i + 2);
			int fromHexa = Integer.valueOf(hexa, 16);
			current = (char) fromHexa;
			i += 2;
		}
		if (Character.isDigit(current) || '+' == current) {
			$.append(current);
		}
		else {
			Log.v(TAG, "charecter not appended " + current);
		}
		i++;
	}
	return $.toString();
}

public String addCountryCode(String number) {
	if (number.startsWith("+")) {
		return number;
	}
	return countryPrefixProvider.get() + number.substring(1);
}

public String removeSmsto(String data) {
	String number = data;
	int indexOfSeperator = number.indexOf(":");
	if (indexOfSeperator != -1) {
		number = number.substring(indexOfSeperator + 1);
	}
	return number;
}

public String getNormalizedNumberWithCountryPrefix(String dataString) {
	return convertSpecialChars(addCountryCode(convertSpecialChars(removeSmsto(dataString))));
}
public String getNumberAsKey(String dataString) {
	return convertSpecialChars(removeSmsto(dataString));
}
public String getNormalizedNumberWithCountryPrefixAndSmsto(String dataString) {
	return addSmsto(getNormalizedNumberWithCountryPrefix(dataString));
}
public String addSmsto(String number){
	return "smsto:" + number;
}
public String getNormalizedNumberForSkype(String dataString) {
	return normalizedNumberForSkype(convertSpecialCharsForSkype(removeSmsto(dataString)));
}
public String convertSpecialCharsForSkype(String number) {
	int i = 0;
	StringBuilder $ = new StringBuilder();
	while (i < number.length()) {
		char current = number.charAt(i);
		if (current == '%') {
			String hexa = "" + number.charAt(i + 1) + number.charAt(i + 2);
			int fromHexa = Integer.valueOf(hexa, 16);
			current = (char) fromHexa;
			i += 2;
		}
		$.append(current);
		i++;
	}
	return $.toString();
}
private String normalizedNumberForSkype(String phone) {
	boolean hasCountryCode = phone.startsWith("+");
	if (hasCountryCode) { 
		return phone;
	}
	else {
		return countryPrefixProvider.get() + " " + phone.substring(1);
	}
		
}

public String decode(String phone) {
	return phone.replace(" ", "%20").replace("+", "%2B");
}

}