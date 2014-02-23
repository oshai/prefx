package oshai.prefx.common;

public class DataObject {
	public String key;
	public String userId;
	public String name;
	public String phone;
	public String email;
	public String smsType;
	public String smsTypeFromServer;
	
	@Override
	public String toString() {
	    return "DataObject [key=" + key + ", userId=" + userId + ", name="
		    + name + ", phone=" + phone + ", email=" + email
		    + ", smsType=" + smsType + ", smsTypeFromServer="
		    + smsTypeFromServer + "]";
	}
	
	
	
}