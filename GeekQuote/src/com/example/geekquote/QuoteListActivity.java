package com.example.geekquote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.example.geekquote.model.Quote;
import com.example.geekquote.parser.XMLGettersSetters;
import com.example.geekquote.parser.XMLHandler;
import com.example.geekquote.sqlite.OpenHelper;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Activité principale : lancée lors du clic sur l'icone de l'application
 *
 */
public class QuoteListActivity extends Activity {

	private static String URL_SERVER = "192.168.2.100:8080";		//url to change with your web server
	private ArrayList<Quote> quotes = null;
	private final int MY_CODE = 1;
	private ProgressBar mProgressBar;
	QuoteListAdapter<Quote> adapter = null;			//declaring our adapter
	OpenHelper oh;
	XMLGettersSetters data;
	
	@SuppressWarnings("unchecked")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_list);			//set the ui (xml resource)
        
        //si des données ont été sauvées dans le bundle
        if(savedInstanceState != null){
        	quotes = (ArrayList<Quote>) savedInstanceState.getSerializable("quotes"); //reload of ArrayList<Quote>
        }
        
        final ListView listView =  (ListView) findViewById(R.id.listView1);	//get of resource ListView
        //final ArrayAdapter<Quote> adapter;
        
        
        if(quotes == null){						//if ArrayList is null
        	quotes = new ArrayList<Quote>();
//            Resources resources = getResources();	//get all the resources
//            
//            String[] str = resources.getStringArray(R.array.quotes);	//get string array
//            
//            for(String s : str){	//browse array
//            	addQuote(s);		//add a Quote to ArrayList
//            }
        	if(isOnline()){
        		mProgressBar = new ProgressBar(this);

        		//background request to the REST webservice
	            try {
	    			new BddRequest().execute(new URL("http://"+URL_SERVER+"/QuoteWeb/resources/quotes"));
	    		} catch (MalformedURLException e) {
	    			e.printStackTrace();
	    		}
        	}else{
        		Toast.makeText(this, "Aucune connexion trouvée", Toast.LENGTH_SHORT).show();
        	}
        }else{	//if ArrayList is not null
        	Log.d("DEBUG GEEK QUOTE", "Size : "+quotes.size());

        }
        

//      adapter = new ArrayAdapter<Quote>(this,android.R.layout.simple_list_item_1, quotes);
        adapter = new QuoteListAdapter<Quote>(this, android.R.layout.simple_list_item_1, quotes);	//creating our Adapter
        listView.setAdapter(adapter);			//ListView is going tu use our adapter
        
        //tap on item list
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt,
					long myLng) {
				//Quote q = (Quote)listView.getItemAtPosition(myItemInt);
				Intent intent = new Intent(getApplicationContext(),QuoteActivity.class);	//create a new Intent
				intent.putExtra("quote", quotes);											//ArrayList in Extra
				intent.putExtra("quoteIndex", myItemInt);									//Position of the selected element
				startActivityForResult(intent, MY_CODE);									//Launch activity for result
				//startActivity(intent);
			}
        	
		});
        
        //Long tap on item
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
					final Quote q = (Quote) listView.getItemAtPosition(arg2);		//get element tapped
					final EditText edit = new EditText(getApplicationContext());	//create an EditText	

					AlertDialog.Builder builder = new AlertDialog.Builder(arg1.getContext());	//create a Dialog
								
					//Button OK of Dialog
					builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							q.setStrQuote(edit.getText().toString());	//change the quote's text
							adapter.notifyDataSetChanged();				//notify the adapter
						}
					});
					
					//Button CANCEL
					builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//nothing
						}
					});
					
					//creating dialog
					AlertDialog alertDial = builder.create();
					//Title
					alertDial.setTitle("Edit Quote");
					//text color of EditText
					edit.setTextColor(Color.BLACK);
					//Texte of EditText
					edit.setText(q.getStrQuote());
					//Add the view of dialog (EditText)
					alertDial.setView(edit,10,0,10,0);
					//Showing
					alertDial.show();
				return true;
			}
        	
		});

        
        final Button button = (Button) findViewById(R.id.button1);
        //clic on add button
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText text = (EditText) findViewById(R.id.editText1);	//get EditText
				String value = text.getText().toString();					//texte inside
				if(!value.equals("")){										//if value is not empty
					addQuote(value);										//add new Quote
					adapter.notifyDataSetChanged();							//notify adapter
				}else{
					Toast.makeText(getApplicationContext(), "Nothing to add", Toast.LENGTH_SHORT).show();
				}
				text.setText("");		//reset of EditText
			}
		});
    }
	
	/**
	 * Method to implement when we launch an activity with startActivityOnResult
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//First SWITCH : code passed in parameter of startActivityOnResult
    	switch(requestCode){
    		case MY_CODE :
    			//second SWITCH : result code
    			switch(resultCode){
    			case RESULT_CANCELED :
    				break;
    			case RESULT_OK :
    				Bundle extras = data.getExtras();	//get the bundle passed
    				if(extras != null){
    					@SuppressWarnings("unchecked")
						ArrayList<Quote> q = (ArrayList<Quote>) extras.getSerializable("quoteModif");	//reload the ArrayList
    					quotes = q;
    				}else{
    					Log.d("DEBUG GEEK QUOTE", "Extras null");
    				}
    				break;
    			}
    	}
	}

	/**
	 * Method to add a Quote
	 * @param strQuote Quote's text
	 */
	public void addQuote(String strQuote){
		Quote q = new Quote();
		SimpleDateFormat simpDate = new SimpleDateFormat("dd/MM/yyyy",Locale.FRANCE);	//format the date
		String date = simpDate.format(new Date());
		q.setCreationDate(date);
		q.setStrQuote(strQuote);
		
		quotes.add(q);		//add to l'ArrayList
		
		Toast.makeText(getApplicationContext(), "Quote ajouté : "+strQuote, Toast.LENGTH_SHORT).show();
		Log.d("ADDED QUOTE", "Quote ajouté : "+strQuote);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quote_list, menu);
        return true;
    }
	
	@Override
	protected void onPause() {
//		oh = new OpenHelper(this);						//our class for connection
//		SQLiteDatabase db = oh.getWritableDatabase();	//Database writable
//		
//		ContentValues values = new ContentValues();		//prepare value to insert
//		
//		for(Quote q : quotes){
//			values.put("textQuote",q.getStrQuote());
//			values.put("date", q.getCreationDate());
//			values.put("rating", q.getRating());
//			
//			db.insert(OpenHelper.TABLE_NAME, null, values);	//insert in database
//		}
//		Log.d("DEBUG GEEK QUOTE", "OnCreateBase");
//		db.close();
		super.onPause();
	}

	@Override
	protected void onResume() {
		oh = new OpenHelper(this);
		SQLiteDatabase db = oh.getReadableDatabase();		//Database readable
		
		String[] columns = {OpenHelper.COL_ID,OpenHelper.NAME_COL_1,OpenHelper.NAME_COL_2,OpenHelper.NAME_COL_3};	//columns of table
		
		Cursor c = db.query(OpenHelper.TABLE_NAME,	//SELECT * FROM "TABLE_NAME"
				columns,null,null,null,null,null);
		
		int nbRow = c.getCount();					//number of row
		Log.d("DEBUG GEEK QUOTE",nbRow+" rows!");
		if(nbRow > 0){
			ArrayList<Quote> temp = new ArrayList<Quote>(20);
			c.moveToFirst();
			while(!c.isAfterLast()){	//browsing all rows for add them in a temp ArrayList
				Quote q = new Quote();
				q.setStrQuote(c.getString(1));
				q.setCreationDate(c.getString(2));
				q.setRating(c.getInt(3));
				
				temp.add(q);
				
				c.moveToNext();
			}
			
			quotes = temp;
		}else{
			Log.d("DEBUG GEEK QUOTE","0 row returned!");
		}
		db.close();
		
		adapter.notifyDataSetChanged();
		
		super.onResume();
	}

	/**
	 * Method to implement for saving in the bundle
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		quotes = (ArrayList<Quote>) savedInstanceState.getSerializable("quotes");
	}

	/**
	 * Method to implement for saving in the bundle
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("quotes", quotes);
		super.onSaveInstanceState(outState);
	}
	
	/**
	 * Security for WebService
	 */
//	private void disableConnectionReuseIfNecessary(){
//		if(Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO){
//			System.setProperty("http.keepAlive", "false");
//		}
//	}

//	@Override
//	public Object onRetainNonConfigurationInstance() {
//		ArrayList<Quote> liste = quotes;
//		return liste;
//	}  
	
	/**
	 * Method to load content
	 *
	 */
	private class BddRequest extends AsyncTask<URL, Integer, Long> {

		/**
		 * Méthode with Ellipse on Urls
		 */
		@Override
		protected Long doInBackground(URL... arg0) {

			int count = arg0.length;
			Long totalSize = 0L;
	        URL url;
	        HttpURLConnection urlConnection = null;
			
			for(int i = 0; i < count; i++){
				try {
					url = arg0[i];
					urlConnection = (HttpURLConnection) url.openConnection();	//Open connection
					totalSize = (long) urlConnection.getContentLength();				//total size
					publishProgress(totalSize.intValue());
//					InputStream in = new BufferedInputStream(urlConnection.getInputStream());	//stocking reslut in InputStream
//					File xml = readStream(in);
					parseXmlFile();
				} catch (MalformedURLException e) {
					e.printStackTrace(); 
		        } catch (IOException e) {
					e.printStackTrace();
				} finally {
		          urlConnection.disconnect();
		        }
				if(isCancelled()) break;
			}
			
			return totalSize;
		}
		
		/**
		 * Method called at the end
		 */
		@Override
		protected void onPostExecute(Long result) {
	        System.out.println("Downloaded " + result + " bytes");
	     }
		
		protected void onProgressUpdate(String... progress){
			mProgressBar.setProgress(Integer.parseInt(progress[0]));
		}
		
	}
	
	/**
	 * Method to test the existence of internet connection
	 * Use permission
	 * @return boolean
	 */
	public boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if(netInfo != null && netInfo.isConnectedOrConnecting()){
			return true;
		}
		return false;
	}
	
	/**
	 * Method to transform an InputStream into a XML File
	 * @param in InputStream
	 * @return File XML file
	 * @throws IOException
	 */
    private File readStream(InputStream in) throws IOException {
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A"); //Scanner to simplify the reading
        String r = s.hasNext() ? s.next() : "";								//result's string (opérateur ternaire)
        
        File f = new File(this.getFilesDir(),"quotes.xml");					//new file (if SD Card: add permission)
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buffer = new byte[1024];
        int bufferLength = 0;
        while ( (bufferLength = in.read(buffer)) > 0 ) {
            fos.write(buffer, 0, bufferLength);								//writing file
        }
        System.out.println(f.getPath());									//displaying file's PATH
        fos.close();

        return f;
	}
    
    /**
     * Method to parse XML and load data into ArrayList
     */
    private void parseXmlFile(){
    	
    	try {
    		SAXParserFactory saxPf = SAXParserFactory.newInstance();
			SAXParser saxP = saxPf.newSAXParser();
			XMLReader xmlR = saxP.getXMLReader();
			//TODO change URL
			URL url = new URL("http://"+URL_SERVER+"/QuoteWeb/resources/quotes");
			
			XMLHandler myXMLHandler = new XMLHandler();
			xmlR.setContentHandler(myXMLHandler);
			xmlR.parse(new InputSource(url.openStream()));
			
			data = XMLHandler.data;
			
			for(String s : data.getQuotes()){
				Quote q = new Quote();
				q.setStrQuote(s);
				quotes.add(q);
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
