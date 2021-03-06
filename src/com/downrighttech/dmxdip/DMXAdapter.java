package com.downrighttech.dmxdip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


//TODO: Suppressing NewApi for a call to setBackground >= 16 need to make a default
// perhaps set color?
@SuppressLint("NewApi")
public class DMXAdapter extends BaseAdapter {
    private ArrayList<Integer> mStart;
    private final Context mContext;
    private Typeface tf;
    private SharedPreferences mSharedPreferences;
    private ViewHolder mHolder;
    private String pref_addr;
    private String pref_offset2;
    private boolean mOffset;

    public DMXAdapter(Context context, ArrayList<Integer> addressArray) {
        mContext = context;
        mStart = addressArray;
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Regular.ttf");
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        pref_addr = mSharedPreferences.getString("pref_addr", "0");
        pref_offset2 = mSharedPreferences.getString("pref_offset2", "0");
        mOffset = false;

    }

    @Override
    public int getCount() {
        return mStart.size();
    }

    public void setOffset(boolean offset) {
        mOffset = offset;
        Log.v("setOffset", Boolean.toString(offset));
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

        mHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (pref_addr.equals("0"))
                convertView = inflater.inflate(R.layout.dmx_adapter_layout, null);
            else
                convertView = inflater.inflate(R.layout.dmx_adapter_layout_bit, null);

            mHolder.tva = new TextView[9];
            mHolder.address = (TextView) convertView.findViewById(R.id.textView1);
            mHolder.tva[0] = (TextView) convertView.findViewById(R.id.ToggleButton01);
            mHolder.tva[1] = (TextView) convertView.findViewById(R.id.ToggleButton02);
            mHolder.tva[2] = (TextView) convertView.findViewById(R.id.ToggleButton03);
            mHolder.tva[3] = (TextView) convertView.findViewById(R.id.ToggleButton04);
            mHolder.tva[4] = (TextView) convertView.findViewById(R.id.ToggleButton05);
            mHolder.tva[5] = (TextView) convertView.findViewById(R.id.ToggleButton06);
            mHolder.tva[6] = (TextView) convertView.findViewById(R.id.ToggleButton07);
            mHolder.tva[7] = (TextView) convertView.findViewById(R.id.ToggleButton08);
            mHolder.tva[8] = (TextView) convertView.findViewById(R.id.ToggleButton09);

            mHolder.button_on = mContext.getResources().getDrawable(R.drawable.buttons_half_on_30x30);
            mHolder.button_off = mContext.getResources().getDrawable(R.drawable.buttons_half_off_30x30);

            convertView.setTag(mHolder);
        } else
            mHolder = (ViewHolder) convertView.getTag();

        int start;
        if (mOffset) {
            start = mStart.get(index) - 1;      //DMX 1 = Switch 1 ON
        } else {
            start = mStart.get(index);   //DMX 1 = All Switches OFF
        }
        String bin = swapBin(start, 9);

        mHolder.address.setTypeface(tf);
        //
        mHolder.address.setGravity(Gravity.RIGHT);
        mHolder.address.setText(String.format("%3s", mStart.get(index)) + ":");

        char test;
        for (int i = 0; i < 9; i++) {
            test = bin.charAt(i);
            if (test == '1') {
                if (VERSION.SDK_INT >= 16)
                    mHolder.tva[i].setBackground(mHolder.button_on);
                else
                    mHolder.tva[i].setBackgroundDrawable(mHolder.button_on);
            } else {
                if (VERSION.SDK_INT >= 16)
                    mHolder.tva[i].setBackground(mHolder.button_off);
                else
                    mHolder.tva[i].setBackgroundDrawable(mHolder.button_off);
            }
        }
        return convertView;
    }

    public void clear() {
        mStart.clear();
    }

    private String swapBin(int input, int length) {
        String bin = Integer.toBinaryString(input);
        String output = new StringBuffer(bin).reverse().toString();
        for (; output.length() < length; )
            output += "0";
        return output;
    }

    static class ViewHolder {
        TextView address;
        TextView tva[];
        Drawable button_on;
        Drawable button_off;
    }
}
