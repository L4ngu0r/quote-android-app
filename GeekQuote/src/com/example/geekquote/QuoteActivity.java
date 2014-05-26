package com.example.geekquote;

import java.util.ArrayList;

import com.example.geekquote.model.Quote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

/**
 * Activité lancée lors du clic sur un item de la liste
 *
 */
public class QuoteActivity extends Activity {

	ArrayList<Quote> quote = null;
	int index = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quote);
		
		Bundle extras = getIntent().getExtras();	//getting extras
		
		if(extras != null){
			quote = (ArrayList<Quote>) extras.getSerializable("quote");	//loading ArrayList
			index = extras.getInt("quoteIndex");						//position of tapped item
		}else{
			Log.d("DEBUG GEEK QUOTE", "Extras null");
		}
		
		TextView txt = (TextView) findViewById(R.id.textViewQuote);		//TextView for quote's text
		TextView txtDate = (TextView) findViewById(R.id.textViewDate);	//TextVieux for date
		RatingBar rate = (RatingBar) findViewById(R.id.ratingBar1);	
		rate.setNumStars(5);		//5 stars to the rating bar
		
		Button boutCancel = (Button) findViewById(R.id.buttonCancel);
		Button boutOk = (Button) findViewById(R.id.buttonOk);
		
		Quote q = quote.get(index);		//getting the quote
		if(q != null){
			txt.setText(q.getStrQuote());	//display
			txtDate.setText(q.getCreationDate());
			rate.setRating(q.getRating());
		}
		
		//Change on the rating
		rate.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,boolean fromUser) {
				quote.get(index).setRating((int) rating);	//change quote's rating
			}
		});
		
		//Tap on CANCEL
		boutCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);	//we send the code
				finish();					//we finish the activity
			}
		});
		
		//Tap on OK
		boutOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),QuoteListActivity.class);	//Intent for return
				myIntent.putExtra("quoteModif", quote);		//ArrayList
				setResult(RESULT_OK,myIntent);
				finish();									//end of activity
			}
		});
	}
}
