package com.downrighttech.dmxdip;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.view.LayoutInflater;

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
		Drawable button_on;
		Drawable button_off;
		
		button_on = mContext.getResources().getDrawable(R.drawable.buttons_half_on_30x30);
		button_off = mContext.getResources().getDrawable(R.drawable.buttons_half_off_30x30);

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.dmx_adapter_layout, parent, false);
		TextView tv = (TextView) rowView.findViewById(R.id.textView1);
		ToggleButton tva[];
		tva = new ToggleButton[9];
		tva[0] = (ToggleButton) rowView.findViewById(R.id.ToggleButton01);		
		tva[1] = (ToggleButton) rowView.findViewById(R.id.ToggleButton02);		
		tva[2] = (ToggleButton) rowView.findViewById(R.id.ToggleButton03);		
		tva[3] = (ToggleButton) rowView.findViewById(R.id.ToggleButton04);		
		tva[4] = (ToggleButton) rowView.findViewById(R.id.ToggleButton05);		
		tva[5] = (ToggleButton) rowView.findViewById(R.id.ToggleButton06);		
		tva[6] = (ToggleButton) rowView.findViewById(R.id.ToggleButton07);		
		tva[7] = (ToggleButton) rowView.findViewById(R.id.ToggleButton08);		
		tva[8] = (ToggleButton) rowView.findViewById(R.id.ToggleButton09);		
		
		//String bin = Integer.toBinaryString(mStart.get(index));
		String bin = swapBin(mStart.get(index), 9);
		
		
		tv.setWidth(130);
		
		tv.setGravity(Gravity.RIGHT);
//		tva[0].setText("0");
//		int tva_width = 40;
		
		tv.setText(String.format("%3s", mStart.get(index)) + ": ");
		
		char test;
		for (int i = 0 ; i < bin.length() ; i++){
			test = bin.charAt(i);
			if (test =='1')
				tva[i].setBackgroundDrawable(button_on);
			else
				tva[i].setBackgroundDrawable(button_off);
		}

		tv.setTypeface(tf);
		tv.setTextSize(30);
		
		return rowView;
	}
	public void clear (){
		mStart.clear();
	}

	private String swapBin (int input, int length) {
		String bin = Integer.toBinaryString(input);
		String output = new StringBuffer(bin).reverse().toString();
			for (;output.length() < length; )
				output += "0";
		return output;
	}

}
