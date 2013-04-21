package com.downrighttech.dmxdip;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.drm.DrmStore.RightsStatus;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DMXAdapter extends BaseAdapter {
	private ArrayList<Integer> mStart;
	private final Context mContext;
	private Typeface tf;
	
	public DMXAdapter(Context context, ArrayList<Integer> addressArray){
		mContext=context;
		mStart = addressArray;
		tf = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Regular.ttf");
	}

	@Override
	public int getCount() {
		return mStart.size();
	}

	@Override
	public Object getItem(int position) {
		return mStart.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		String bin = Integer.toBinaryString(mStart.get(index));
		
		
		LinearLayout ly1 = new LinearLayout(mContext);
		LinearLayout ly2 = new LinearLayout(mContext);
		
		TextView tv = new TextView(mContext);
		TextView tva[];
		tva = new TextView[9];
		tva[0] = new TextView(mContext);
		tva[1] = new TextView(mContext);
		tva[2] = new TextView(mContext);
		tva[3] = new TextView(mContext);
		tva[4] = new TextView(mContext);
		tva[5] = new TextView(mContext);
		tva[6] = new TextView(mContext);
		tva[7] = new TextView(mContext);
		tva[8] = new TextView(mContext);

		ly1.addView(tv);
		ly1.addView(ly2);
		ly2.addView(tva[0]);
		ly2.addView(tva[1]);
		ly2.addView(tva[2]);
		ly2.addView(tva[3]);
		ly2.addView(tva[4]);
		ly2.addView(tva[5]);
		ly2.addView(tva[6]);
		ly2.addView(tva[7]);
		ly2.addView(tva[8]);

		tv.setText("555: ");
		int tv_width = tv.getMeasuredWidth();
		tv.setWidth(130);
		
		tv.setGravity(Gravity.RIGHT);
		tva[0].setText("0");
		int tva_width = 40;
		
		tv.setText(String.format("%3s", mStart.get(index)) + ": ");
		
		int i = 0;
		int j = bin.length()-1;
		for ( ; i < bin.length() ; i++){
			tva[i].setText(String.valueOf(bin.charAt(j)));
			tva[i].setTypeface(tf);
			tva[i].setTextSize(30);
			tva[i].setWidth(tva_width);
			//TODO: Set Weight for each Bit field
			
			j--;
		}
		
		// fill the other bits with 0s
		for ( ; i < 9 ; i++){
			tva[i].setText("0");
			tva[i].setTypeface(tf);
			tva[i].setTextSize(30);
			tva[i].setEms(1);
			tva[i].setWidth(tva_width);
		}


		tv.setTypeface(tf);
		tv.setTextSize(30);
		
		return ly1;
	}
	public void clear (){
		mStart.clear();
	}
}
