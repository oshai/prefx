package oshai.prefx.contacts;

import java.util.List;

import oshai.prefx.common.ContactData;

public interface IContactReader {

	List<ContactData> readContacts(String selection);

}
