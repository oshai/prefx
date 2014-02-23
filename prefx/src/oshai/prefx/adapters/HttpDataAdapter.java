package oshai.prefx.adapters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import oshai.prefx.common.DataObject;
import oshai.prefx.common.Utils;
import android.net.Uri;

public class HttpDataAdapter implements IHttpDataAdapter {

private static final String get_url = "http://ohadshai.servehttp.com/get.php?phone_number=";
private static final String put_url = "http://ohadshai.servehttp.com/put.php?";
private static final String log_url = "http://ohadshai.servehttp.com/log.php?";

private String httpGetNoException(String url){
	try {
		return httpGet(url);
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
}
private String httpGet(String url) throws IOException {
	HttpClient httpclient = new DefaultHttpClient();
    HttpResponse response = httpclient.execute(new HttpGet(url));
    StatusLine statusLine = response.getStatusLine();
    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.getEntity().writeTo(out);
        out.close();
        String responseString = out.toString();
        return responseString;
    } else{
        //Closes the connection.
        response.getEntity().getContent().close();
        throw new IOException(statusLine.getReasonPhrase());
    }
}
@Override
public void put(String phone, String smsType, String updater) {
	String url = put_url + 
			"phone_number=" + Uri.encode(phone) + "&" + 
			"message_type=" + Uri.encode(smsType) + "&" +
			"updater=" + Uri.encode(updater);
	httpGetNoException(url);
}
@Override
public DataObject getData(String key, String updater) {
	String url = get_url + Uri.encode(key) + "&updater=" + Uri.encode(updater);
	String result = httpGetNoException(url);
	if (Utils.isEmpty(result)) {
		return null;
	}
	DataObject d = new DataObject();
	d.phone = key;
	d.smsTypeFromServer = result.split("<br />")[0].split("=")[1];
	return d;
}
@Override
public void log(String mail, String phone, String message) {
    String url = log_url + "mail=" + Uri.encode(mail) + "&phone=" + Uri.encode(phone)  + "&message=" + Uri.encode(message);
    httpGetNoException(url);
}

}
