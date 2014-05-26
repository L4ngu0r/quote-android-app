package com.example.geekquote.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {

    String elementValue = null;
    Boolean elementOn = false;
	
	public static XMLGettersSetters data = null;
	
	public static XMLGettersSetters getXMLData() {
        return data;
    }
    public static void setXMLData(XMLGettersSetters data) {
        XMLHandler.data = data;
    }
    
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		 if (elementOn) {
	            elementValue = new String(ch, start, length);
	            elementOn = false;
		 }
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		elementOn = false;
		if(localName.equalsIgnoreCase("date"))data.setDate(elementValue);
		else if(localName.equalsIgnoreCase("rating"))data.setRating(Integer.parseInt(elementValue));
		else if(localName.equalsIgnoreCase("strQuote"))data.setQuote(elementValue);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		elementOn = true;
		if(localName.equals("quotes")){
			data = new XMLGettersSetters();
		}
	}
    
    
}
