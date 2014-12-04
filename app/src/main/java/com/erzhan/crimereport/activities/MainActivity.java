package com.erzhan.crimereport.activities;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.erzhan.crimereport.API.Constants;
import com.erzhan.crimereport.Message;
import com.erzhan.crimereport.R;
import com.erzhan.crimereport.classes.Crime;
import com.erzhan.crimereport.API.AsyncTaskFetchCrimes;
import com.erzhan.crimereport.API.MyJsonParser;
import com.erzhan.crimereport.fragments.FragmentCrimes;
import com.erzhan.crimereport.fragments.FragmentMyGoogleMap;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Stack;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private ArrayList<Crime> crimes;
    private JSONArray crimesJson;

    private FragmentCrimes fragmentCrimes;
    private FragmentMyGoogleMap fragmentMap;

    private AsyncTaskFetchCrimes asyncTaskFetchCrimes;

    public boolean isInternetAvailable()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    public void setAsyncTaskFetchCrimesResult(JSONArray jsonArray)
    {
        try {
            this.crimesJson = jsonArray;
            if (crimesJson != null) {
                crimes = MyJsonParser.parseArrayCrimes(crimesJson);

                // /if first initialization
                if (fragmentCrimes == null) {
                    fragmentCrimes = new FragmentCrimes();
                    fragmentCrimes.setAllCrimes(crimes);
                    showFragmentCrimeList();
                }
                //if action_reload_crimes
                else{
                    fragmentCrimes.reloadListView(crimes);
                }

            } else {
                //error
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isInternetAvailable()) {
            asyncTaskFetchCrimes = (AsyncTaskFetchCrimes)
                    new AsyncTaskFetchCrimes(this).execute();
        }
        else {
            Message.message(this,
                    getResources().getString(R.string.internet_not_available));
        }

        //set navi buttons onclicklisters
        TextView textView = (TextView)findViewById(R.id.tab_map);
        textView.setOnClickListener(this);
        textView = (TextView)findViewById(R.id.tab_crimes);
        textView.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_reload_crimes)
        {
            asyncTaskFetchCrimes = new AsyncTaskFetchCrimes(this);
            asyncTaskFetchCrimes.execute();
            return true;
        }
        else if (id == R.id.add_report) {
            return true;
        }
        else if (id == R.id.action_about)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }
    public void showFragmentCrimeList()
    {
        if (fragmentCrimes == null){
            fragmentCrimes = new FragmentCrimes();
        }
        showFragment(fragmentCrimes);
    }
    public void showFragmentMap(int crimeIndex)
    {
        if (fragmentMap == null){
            fragmentMap = new FragmentMyGoogleMap();
            fragmentMap.setCrimeList(crimes);
        }
        if (crimeIndex != -1) {
            fragmentMap.focusOnCrime(crimeIndex);
        }
        showFragment(fragmentMap);
    }
    private void showFragment(Fragment f)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, f);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {

        int index = view.getId();

        //tab crimes list clicked
        if (index == R.id.tab_crimes) {
            view.setBackgroundResource(R.color.navi_button_pressed);
            ((TextView)findViewById(R.id.tab_map)).setBackgroundResource(R.color.navi_button_idle);
            showFragmentCrimeList();
        }
        //tab map clicked
        else if (index == R.id.tab_map) {
            view.setBackgroundResource(R.color.navi_button_pressed);
            ((TextView)findViewById(R.id.tab_crimes)).setBackgroundResource(R.color.navi_button_idle);
            showFragmentMap(-1);
        }
        else {
            try {
                Intent intent = new Intent(this, ActivityCrime.class);

                //put crimeJSON into intent
                intent.putExtra(Constants.CrimeJsonObject, crimesJson.get(index).toString());
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
}
