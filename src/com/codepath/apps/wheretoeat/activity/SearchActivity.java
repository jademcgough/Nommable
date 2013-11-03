package com.codepath.apps.wheretoeat.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.wheretoeat.R;
import com.codepath.apps.wheretoeat.YelpApp;
import com.codepath.apps.wheretoeat.models.Restaurant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	private LocationClient locationClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
        // check Google Play service APK is available and up to date.
        // see http://developer.android.com/google/play-services/setup.html
        final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
        	// directs user to update google play services if no workie
        	GooglePlayServicesUtil.getErrorDialog(result, this, 0).show();
        }

        locationClient = new LocationClient(this, this, this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	
    @Override
    protected void onResume() {
            super.onResume();

            locationClient.connect();
    }

    @Override
    protected void onPause() {
            super.onPause();

            locationClient.disconnect();
    }

	public void findRestaurant(View v) {
		YelpApp.getRestClient().search("restaurant", createMockLocation(), new JsonHttpResponseHandler() {
			// convert response to objects, create intent, pass to results activity
			
			@Override
			public void onSuccess(int httpResponse, JSONObject jsonResponse) {
				//Log.d("DEBUG", jsonResponse.toString());
				try {
					Log.d("DEBUG", jsonResponse.getJSONArray("businesses").toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("DEBUG", e.getMessage());
				}
			}
						
			@Override
			public void onFailure(Throwable e, JSONObject error) {
				Log.e("ERROR", e.toString());
				Toast.makeText(SearchActivity.this, "Unable to access Yelp", Toast.LENGTH_SHORT).show();
			}
			
		});
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();
	}

	@SuppressLint("NewApi")
	@Override
	public void onConnected(Bundle connectionHint) {
		Location loc = locationClient.getLastLocation();
		Log.d("DEBUG", "connected");
		
		// mocking this for now - can't get it to work with my emulator
		
		if (loc != null){
			Log.d("DEBUG", "location=" + loc.toString());
		} else {
			// TODO: Handle this better
			Toast.makeText(this, "Unable to access current location", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}
	
	//Define the location update callback by implementing LocationListener
	@Override
	public void onLocationChanged(Location location) {
		String msg = "Updated Location: " + Double.toString(location.getLatitude()) + "," 
				+ Double.toString(location.getLongitude());
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
	}
	@Override
	public void onProviderDisabled(String provider) {
	}
	@Override
	public void onProviderEnabled(String provider) {
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	@SuppressLint("NewApi")
	private Location createMockLocation() {
//		locationClient.setMockMode(true);  // locationclient will throw an error if it hasn't been connected yet
		Location loc = new Location("flp");
		loc.setLatitude(37.794593);
		loc.setLongitude(-122.441254);
		loc.setAccuracy(3.0f);
		loc.setTime(System.currentTimeMillis());
		loc.setElapsedRealtimeNanos(3000L); // this won't work for anything below API 17, but this won't be necessary when we drop mocks
//		locationClient.setMockLocation(loc);
		return loc;
	}
}


