package oshai.prefx.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import oshai.prefx.R;
import oshai.prefx.RegistrationActivity;
import oshai.prefx.RegistrationActivity.CountryCode;
import android.content.Context;
import android.content.res.XmlResourceParser;

public class MyCoutriesXmlParser {
public List<CountryCode> parse(Context context) throws XmlPullParserException, IOException {
	ArrayList<CountryCode> $ = new ArrayList<RegistrationActivity.CountryCode>();
	XmlResourceParser myxml = context.getResources().getXml(R.xml.countries);//MyXml.xml is name of our xml in newly created xml folder, mContext is the current context
	// Alternatively use: XmlResourceParser myxml = getContext().getResources().getXml(R.xml.MyXml);

	myxml.next();//Get next parse event
	int eventType = myxml.getEventType(); //Get current xml event i.e., START_DOCUMENT etc.
	String NodeValue;
	while (eventType != XmlPullParser.END_DOCUMENT)  //Keep going until end of xml document
	{  
	    if(eventType == XmlPullParser.START_DOCUMENT)   
	    {     
	        //Start of XML, can check this with myxml.getName() in Log, see if your xml has read successfully
	    }    
	    else if(eventType == XmlPullParser.START_TAG)   
	    {     
	        NodeValue = myxml.getName();//Start of a Node
	        //<country code="AF" title="@string/AF" idd="93" local="0"/>
	        if (NodeValue.equalsIgnoreCase("countries"))
	        {
	                // use myxml.getAttributeValue(x); where x is the number
	                // of the attribute whose data you want to use for this node
	        }
	        if (NodeValue.equalsIgnoreCase("country"))
	        {
	        	try {
					CountryCode c = new CountryCode();
					c.country = myxml.getAttributeValue(0);
					String res = myxml.getAttributeValue(1);
					c.countryView = context.getString(context.getResources().getIdentifier(res, "id", context.getPackageName()));
					c.code = "+"+myxml.getAttributeValue(2);
					c.local = myxml.getAttributeValue(3);
					$.add(c);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	        if (NodeValue.equalsIgnoreCase("SecondNodeNameType"))
	        {
	                // use myxml.getAttributeValue(x); where x is the number
	                // of the attribute whose data you want to use for this node

	        } 
	       //etc for each node name
	   }   
	    else if(eventType == XmlPullParser.END_TAG)   
	    {     
	        //End of document
	    }    
	    else if(eventType == XmlPullParser.TEXT)   
	    {    
	        //Any XML text
	    }

	    eventType = myxml.next(); //Get next event from xml parser
	}
	return $;
}
}
