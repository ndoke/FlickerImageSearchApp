/**
 * PullParserHelper.java
 * A Yang
 * Ajay Vijayakumaran Nair
 * Nachiket Doke
 * 
 */
package com.example.inclass05;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class PullParserHelper {
	public List<String> getUrlList(InputStream inStream) throws XmlPullParserException, IOException{
		XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
		parser.setInput(inStream, "UTF-8");
		int eventType = parser.getEventType();
		List<String> urlList = new ArrayList<String>();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("photo")) {
					urlList.add(parser.getAttributeValue(null, "url_m").trim());
				}
				break;

			default:
				break;
			}
			eventType = parser.next();
		}
		return urlList;
	}
}
