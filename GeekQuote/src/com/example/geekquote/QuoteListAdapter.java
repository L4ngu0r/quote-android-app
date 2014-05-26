package com.example.geekquote;

import java.util.List;

import com.example.geekquote.model.Quote;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Redéfinition de notre Adapter
 */
public class QuoteListAdapter<T> extends BaseAdapter {

	private List<T> mObjects;
	private int mResource;
	private int mDropDownResource;
	private int mFieldId = 0;
	private Context mContext;
	private LayoutInflater mInflater;
	
	/**
	 * Constructor to get
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public QuoteListAdapter(Context context, int resource, List<T> objects) {
		 init(context, resource, 0, objects);
	}


	private void init(Context context, int resource, int textViewResourceId, List<T> objects) {
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = mDropDownResource = resource;
		mObjects = objects;
		mFieldId = textViewResourceId;
	}
	
	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mObjects.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	/**
	 * Méthod to override
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
  
		return createViewFromResource(position, convertView, parent,mResource);
	}
	
	/**
	 * Method of displaying ArrayList
	 * @param position
	 * @param convertView
	 * @param parent
	 * @param resource
	 * @return
	 */
	private View createViewFromResource(int position, View convertView, ViewGroup parent,int resource){
		View view; 
		TextView text;
		
		//if our View exist
		if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

		
		//if our View is already somewhere
		if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (TextView) view;
        } else {
                //  Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(mFieldId);
        }
		
		//get item of ArrayList
		T item = (T) getItem(position);
		if(item instanceof Quote){	//if it's a Quote object
			Quote q = (Quote) item;
			text.setText(q.toString());	//getting the text to displaying it
		}
		
		//Testing even and odd
		if (position % 2 == 1) {
		    view.setBackgroundColor(Color.TRANSPARENT); 
		} else {
		    view.setBackgroundColor(Color.LTGRAY);  
		}

		return view;
	}

}
