package oshai.prefx.common;

public enum SmsType
{
	DeviceBuiltIn("sms","com.android.mms","com.android.mms.MmsApp"),
	WhatsApp("whatsapp", "com.whatsapp", "com.whatsapp.Conversation"), 
//	Viber("viber", "com.viber.voip", "com.viber.voip.messages.ui.ConversationActivity"),
	Voxer("voxer", "com.rebelvox.voxer", "com.rebelvox.voxer.Intents.Splash"),
	Skype("skype", "com.skype.raider", "com.skype.raider.Main"),
	DummyNotExistsType("not_exists","should_not_exists","should_not_exists"),
	;
	private String viewString;
	private String packageName;
	private String className;

private SmsType(String viewString, String packageName, String className) {
	this.viewString = viewString;
	this.packageName = packageName;
	this.className = className;
}
	
public String asString() {return viewString;}
public String packageName() {return packageName;}
public String className() {return className;}
	
}