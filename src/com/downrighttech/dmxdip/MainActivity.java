//  Copyright 2013 Down Right Technical
//
//        Licensed under the Apache License, Version 2.0 (the "License");
//        you may not use this file except in compliance with the License.
//        You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//        Unless required by applicable law or agreed to in writing, software
//        distributed under the License is distributed on an "AS IS" BASIS,
//        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//        See the License for the specific language governing permissions and
//        limitations under the License.


package com.downrighttech.dmxdip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.ArrayList;
//import android.preference.Preference;
//import java.io.FileOutputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import android.preference.PreferenceManager;
import android.content.Context;


public class MainActivity extends Activity implements OnClickListener, TextWatcher {
    private EditText editText_Start;
    private EditText editText_Span;
    private ImageButton clearButton;
    private ToggleButton[] toggleButton;
    private ListView listView;
    private ArrayList<Integer> addressList;
    private DMXAdapter arrayAdapter;
    private Drawable button_on;
    private Drawable button_off;
    private Vibrator vib;
    private ShareActionProvider mShareActionProvider;
    private Boolean mSkipProcess;
    private SharedPreferences sharedPreferences;
    private int mCurrentTheme;
    private int mLastAddress;
    private boolean mOffset;

    //private FileOutputStream fileOS;
    //private Preference preference;


    // Constants
    private final int ADDRESS_BUTTONS = 9;
    private final int mFirstAddress = 1;
    private int VIB_TIME = 10;
    private final String FILENAME = "AndroDip.html";

    //TODO: Delete share_text.txt if exists

    // TODO: Make this a fragment?
    private final int BUTTON_TEXT_ADDR[] = {
            R.string.dip_addr_1,
            R.string.dip_addr_2,
            R.string.dip_addr_3,
            R.string.dip_addr_4,
            R.string.dip_addr_5,
            R.string.dip_addr_6,
            R.string.dip_addr_7,
            R.string.dip_addr_8,
            R.string.dip_addr_9};
    private final int BUTTON_TEXT_SW[] = {
            R.string.dip_sw_1,
            R.string.dip_sw_2,
            R.string.dip_sw_3,
            R.string.dip_sw_4,
            R.string.dip_sw_5,
            R.string.dip_sw_6,
            R.string.dip_sw_7,
            R.string.dip_sw_8,
            R.string.dip_sw_9};

    public MainActivity() {
        mSkipProcess = true;
    }

    //@SuppressWarnings("unused")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Load Theme from preferences
        if (sharedPreferences.getString("pref_theme", "0").equals("0")) {
            mCurrentTheme = android.R.style.Theme_Holo_Light;
        } else
            mCurrentTheme = android.R.style.Theme_Holo;
        setTheme(mCurrentTheme);

        setContentView(R.layout.activity_main);

        //Load Resources
        button_on = getResources().getDrawable(R.drawable.buttons_on_30x30);
        button_off = getResources().getDrawable(R.drawable.buttons_off_30x30);

        // Load Interface Items
        editText_Start = (EditText) findViewById(R.id.editText_Start);
        editText_Span = (EditText) findViewById(R.id.EditText_Span);
        clearButton = (ImageButton) findViewById(R.id.imageButton);
        toggleButton = new ToggleButton[9];
        toggleButton[0] = (ToggleButton) findViewById(R.id.ToggleButton01);
        toggleButton[1] = (ToggleButton) findViewById(R.id.ToggleButton02);
        toggleButton[2] = (ToggleButton) findViewById(R.id.ToggleButton03);
        toggleButton[3] = (ToggleButton) findViewById(R.id.ToggleButton04);
        toggleButton[4] = (ToggleButton) findViewById(R.id.ToggleButton05);
        toggleButton[5] = (ToggleButton) findViewById(R.id.ToggleButton06);
        toggleButton[6] = (ToggleButton) findViewById(R.id.ToggleButton07);
        toggleButton[7] = (ToggleButton) findViewById(R.id.ToggleButton08);
        toggleButton[8] = (ToggleButton) findViewById(R.id.ToggleButton09);
        listView = (ListView) findViewById(R.id.listView1);

        // Load ArrayList
        addressList = new ArrayList<Integer>();

        // Load Fonts
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/RobotoSlab-Regular.ttf");

        // Setup Fonts
        editText_Start.setTypeface(tf);
        editText_Span.setTypeface(tf);
        for (int i = 0; i < ADDRESS_BUTTONS; i++)
            toggleButton[i].setTypeface(tf);

        //Setup setHapticFeedbackEnabled
        vib = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);

        // Events
        editText_Start.addTextChangedListener(this);
        editText_Span.addTextChangedListener(this);
        clearButton.setOnClickListener(this);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            }

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i != SCROLL_STATE_IDLE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("lifeCycle", "onStart");

        // Load ArrayAdapter
        arrayAdapter = new DMXAdapter(this, addressList);

        //Assign ArrayAdapter to listView
        listView.setAdapter(arrayAdapter);

        //TODO: Make these the correct type, and make a pref class to take care of all of this.
        String pref_vib = sharedPreferences.getString("pref_vib", "0");
        String pref_addr = sharedPreferences.getString("pref_addr", "0");
        String pref_offset2 = sharedPreferences.getString("pref_offset2", "0");

        if (pref_offset2.equals("0")) {
            mOffset = false;
            mLastAddress = 511;
            arrayAdapter.setOffset(mOffset);
        } else {
            mOffset = true;
            mLastAddress = 512;
            arrayAdapter.setOffset(mOffset);
        }

        int pref_start = sharedPreferences.getInt("pref_start", mFirstAddress);
        int pref_span = sharedPreferences.getInt("pref_span", 1);


        // Load Vibration from preferences
        if (pref_vib.equals("0"))
            VIB_TIME = 10;
        else if (pref_vib.equals("1"))
            VIB_TIME = 30;
        else if (pref_vib.equals("2"))
            VIB_TIME = 90;

        //Load Button Text
        if (pref_addr.equals("1")) {
            for (int i = 0; i < ADDRESS_BUTTONS; i++) {
                toggleButton[i].setText(getString(BUTTON_TEXT_ADDR[i]));
                toggleButton[i].setTextOn(getString(BUTTON_TEXT_ADDR[i]));
                toggleButton[i].setTextOff(getString(BUTTON_TEXT_ADDR[i]));
                toggleButton[i].setTextSize(18);
            }
        } else {
            for (int i = 0; i < ADDRESS_BUTTONS; i++) {
                toggleButton[i].setText(getString(BUTTON_TEXT_SW[i]));
                toggleButton[i].setTextOn(getString(BUTTON_TEXT_SW[i]));
                toggleButton[i].setTextOff(getString(BUTTON_TEXT_SW[i]));
                toggleButton[i].setTextSize(36);
            }
        }

        // Span
        if (pref_span == 1)
            editText_Span.setText("");
        else
            editText_Span.setText(Integer.toString(pref_span));

        // Start
        if (pref_start <= mFirstAddress) {
            editText_Start.clearComposingText();
        } else {
            editText_Start.setText(Integer.toString(pref_start));
        }
        mSkipProcess = false;
        this.updateButtons(pref_start, mOffset);
        this.buildChart(pref_start, pref_span);


//------- ALL OF THE BELOW IS JUNK?
//        String pref_theme = sharedPreferences.getString("pref_theme", "0");
//        int newTheme;
//        if (pref_theme.equals("0"))
//            newTheme = android.R.style.Theme_Holo_Light;
//        else
//            newTheme = android.R.style.Theme_Holo;

//        if (mCurrentTheme != newTheme){
//            Intent activity = new Intent(this,this.getClass());
//            this.startActivity(activity);
//            this.finish();
//            //return;
//        }
    }


    @Override
    protected void onPause() {
        Log.v("lifeCycle", "onPause");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("span", editText_Span.getText().toString());
        outState.putString("start", editText_Start.getText().toString());
        super.onSaveInstanceState(outState);
        Log.v("lifeCycle", "onSaveInstanceState-" + outState.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v("lifeCycle", "onRestoreInstanceState-" + savedInstanceState.toString());
        //if (savedInstanceState != null){
        editText_Span.setText(savedInstanceState.get("span").toString());
        editText_Start.setText(savedInstanceState.get("start").toString());
        super.onRestoreInstanceState(savedInstanceState);
        //}
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v("lifeCycle", "onCreateOptionsMenu-" + menu.toString());
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (VERSION.SDK_INT >= 14) {
            MenuItem shareMenuItem = menu.add("Share");
            ShareActionProvider shareProvider = new ShareActionProvider(this);
            shareMenuItem.setActionProvider(shareProvider);
            // Share Provider
            //mShareActionProvider = (ShareActionProvider) menu.findItem(shareMenuItem).getActionProvider();

            mShareActionProvider = (ShareActionProvider) shareMenuItem.getActionProvider();
            // Set default share intent
            mShareActionProvider.setShareIntent(createShareIntent());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v("lifeCycle", "onOptionsItemSelected");
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuSettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
//            case R.id.menuAbout:
//                intent = new Intent(this, AboutActivity.class);
//                startActivity(intent);
//                return true;
        }
        return false;
    }

    private String swapBin(int input, int length) {
        String bin = Integer.toBinaryString(input);
        String output = new StringBuffer(bin).reverse().toString();
        for (; output.length() < length; )
            output += "0";
        return output;
    }

    private Intent createShareIntent() {
        int count = addressList.size();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/html");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        //Some Data
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "DMXdip for android");

        String str;
        str = "Count: " + count + "\n";
        for (int i = 0; i < count; i++) {
            str += addressList.get(i).toString() + " : " + swapBin(addressList.get(i), 9) + "\n";
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, str);
        //startActivity(Intent.createChooser(shareIntent, null));
        return shareIntent;
    }


    @Override
    public void onClick(View v) {
        int start = 0;

        // Clear Button Presses
        switch (v.getId()) {
            case R.id.imageButton:              //Clear Button
                vib.vibrate(VIB_TIME);
                mSkipProcess = true;            // don't build chart twice on clear.
                editText_Start.setText("");
                mSkipProcess = false;
                editText_Span.setText("");
                break;
            case R.id.ToggleButton01:
            case R.id.ToggleButton02:
            case R.id.ToggleButton03:
            case R.id.ToggleButton04:
            case R.id.ToggleButton05:
            case R.id.ToggleButton06:
            case R.id.ToggleButton07:
            case R.id.ToggleButton08:
            case R.id.ToggleButton09:
                vib.vibrate(VIB_TIME);
                if (toggleButton[0].isChecked())
                    start += 1;
                if (toggleButton[1].isChecked())
                    start += 2;
                if (toggleButton[2].isChecked())
                    start += 4;
                if (toggleButton[3].isChecked())
                    start += 8;
                if (toggleButton[4].isChecked())
                    start += 16;
                if (toggleButton[5].isChecked())
                    start += 32;
                if (toggleButton[6].isChecked())
                    start += 64;
                if (toggleButton[7].isChecked())
                    start += 128;
                if (toggleButton[8].isChecked())
                    start += 256;
                if (mOffset)
                    start += 1;
                editText_Start.setText(String.valueOf(start));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.v("afterTextChanged", s.toString());
        int start = mFirstAddress;
        int span = 1;

        // Check Start has a length
        if (editText_Start.length() != 0) {
            int currentStart = Integer.parseInt(editText_Start.getText().toString());
            //if greater then last then make it that.
            if (currentStart > mLastAddress) {
                editText_Start.setText(Integer.toString(mLastAddress));
                editText_Start.selectAll();
            }
            if (currentStart < mFirstAddress)
                editText_Start.setText(Integer.toString(mFirstAddress));
            start = Integer.parseInt(editText_Start.getText().toString());
        }

        // Check Span has a length
        if (editText_Span.length() != 0) {
            int currentSpan = Integer.parseInt(editText_Span.getText().toString());
            if (currentSpan > 511) {
                editText_Span.setText("511");
                editText_Span.selectAll();
            }
            if (Integer.parseInt(editText_Span.getText().toString()) <= 0) {
                editText_Span.setText("1");
                editText_Span.selectAll();
            }
            span = Integer.parseInt(editText_Span.getText().toString());
        }

        if (mSkipProcess)
            return;
        Log.v("input", "start.count:" + start + "." + span);
        this.updateButtons(start, mOffset);
        this.buildChart(start, span);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("pref_start", start);
        editor.putInt("pref_span", span);
        editor.commit();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void updateButtons(int start, boolean offset) {
        final int length = 9;

        if (offset)
            start -= 1;

        String bin = swapBin(start, length);

        for (int i = 0; i < length; i++) {
            if (bin.charAt(i) == '1') {
                toggleButton[i].setBackgroundDrawable(button_on);
                toggleButton[i].setChecked(true);
            } else {
                toggleButton[i].setBackgroundDrawable(button_off);
                toggleButton[i].setChecked(false);
            }
        }
    }

    public void buildChart(int start, int span) {

        //Clear the list
        addressList.clear();

//		//FileStream
//		try {
//			fileOS = openFileOutput(FILENAME, Context.MODE_PRIVATE);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		try {
//			fileOS.write("<pre>\n".getBytes());
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        Log.v("buildChart", "---" + start + "." + span + "---");
        for (int i = start; i <= mLastAddress; i = i + span) {
            addressList.add(i);
//			try {
//				fileOS.write((i+" : "+swapBin(i, 9)+"\n").getBytes());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
        }
//		try {
//			fileOS.write("</pre>\n".getBytes());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        arrayAdapter.notifyDataSetChanged();
        listView.setSelection(0);
    }
}
