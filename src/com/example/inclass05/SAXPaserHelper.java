/**
 * SAXPaserHelper.java
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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import android.util.Xml;

public class SAXPaserHelper extends DefaultHandler {

	private List<String> urlList = new ArrayList<String>();

	public SAXPaserHelper() {
		super();
	}

	public List<String> fetchUrlList(InputStream inputStream) {
		try {
			Xml.parse(inputStream, Xml.Encoding.UTF_8, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlList;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals("photo")) {
			String photoUrl = attributes.getValue("url_m");
			if (photoUrl != null && !photoUrl.isEmpty()) {
				urlList.add(attributes.getValue("url_m").trim());
			}else{
				Log.d(MainActivity.LOGGING_KEY, "Url is empty");
			}
		}
		super.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
	}

}
