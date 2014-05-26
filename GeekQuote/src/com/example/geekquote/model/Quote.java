package com.example.geekquote.model;

import java.io.Serializable;

/**
 * JavaBean Quote
 *
 */
public class Quote implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String strQuote;
	private int rating;
	private String creationDate;
	
	public String getStrQuote() {
		return strQuote;
	}
	public void setStrQuote(String strQuote) {
		this.strQuote = strQuote;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		if(rating <= 5){
			this.rating = rating;
		}
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
	/**
	 * Méthode appelée par défaut par l'ArrayAdapter
	 */
	@Override
	public String toString() {
		return this.strQuote;
	}
}
