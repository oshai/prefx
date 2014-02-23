package oshai.prefx.contacts;

import com.google.inject.AbstractModule;

public class ContactReaderModule extends AbstractModule {

@Override
protected void configure() {
	bind(IContactReader.class).toProvider(ContactReaderProvider.class);
}

}
