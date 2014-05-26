package com.example.geekquote.parser;

import java.util.ArrayList;

import android.util.Log;

import com.example.geekquote.model.Quote;

public class XMLGettersSetters {
	private ArrayList<String> strQuotes = new ArrayList<String>();
	private ArrayList<String> date = new ArrayList<String>();
	private ArrayList<Integer> rating = new ArrayList<Integer>();
	
	
    public ArrayList<String> getQuotes() {
        return strQuotes;
    }
    public void setQuote(String s) {
        this.strQuotes.add(s);
        Log.i("This is the quote:", s);
    }
    
    public ArrayList<String> getDate(){
    	return date;
    }
    
    public void setDate(String s){
    	this.date.add(s);
    	Log.i("This is the date:",s);
    }
    
    public ArrayList<Integer> getRating(){
    	return rating;
    }
    
    public void setRating(int i){
    	this.rating.add(i);
    	Log.i("This is the rating:",Integer.toString(i));
    }
}
