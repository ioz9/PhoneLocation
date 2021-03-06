package org.jimhopp.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jimhopp.android.model.PhoneLocationModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class PhoneLocationActivity extends Activity {
	
	private final class Timer implements Runnable {
        TextView time;
        boolean done;
        
        Timer(TextView time) {
        	this.time = time;
        	done = false;
        }
        private Handler handler = new Handler();
        private Runnable updateAge = new Runnable() {

			@Override
			public void run() {
				Time now = new Time();
			   	now.setToNow();
		   		time.setText(now.format("%H:%M:%S"));
		   		updateAge(model.getGPSLocation(), now, ageG);
		   		updateAge(model.getNetworkLocation(), now, ageN);
		   		updateDistance();
		   		updateGPS(model.getGPSLocation());
		   		updateNetwork(model.getNetworkLocation());
			}
        };
        
        public void done() { done = true; }
		@Override
		public void run() {
			while (!done) {
				handler.post(updateAge);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	TextView latG, latN,
	         lonG, lonN,
	         altG, altN,
	         accuracyG, accuracyN,
	         statusG, statusN,
	         ageG, ageN,
	         speedG, speedN,
	         providerG, providerN,
	         distance;
	
	PhoneLocationModel model;
	
	final String NO_LOC_DATA = "no location data";
	
	Timer clock;

	void updateAge(Location loc, Time time, TextView textView) {
		Time locTime = new Time();
	   	long delta;
	   	if (loc != null) {
	   		locTime.set(loc.getTime());
	   		delta = (time.normalize(true) - locTime.normalize(true)) / 1000;
	   		textView.setText(DateUtils.formatElapsedTime(delta)+
	   				Thread.currentThread().getName()	 
	        		+ String.valueOf(Thread.currentThread().getId()));
	   	}
	   	else {
	   		textView.setText("n/a " + Thread.currentThread().getName()	 
	        		+ String.valueOf(Thread.currentThread().getId()));
	   	}
	}
	
	void updateGPS(Location loc) {
		if (loc != null) {
			latG.setText(String.valueOf(loc.getLatitude()));
			lonG.setText(String.valueOf(loc.getLongitude()));
			altG.setText(loc.hasAltitude() ? String.valueOf(loc.getAltitude()) : "n/a");
			accuracyG.setText(String.valueOf(loc.getAccuracy()));
			speedG.setText(loc.hasSpeed() ? String.valueOf(loc.getSpeed()) : "n/a");
			providerG.setText(Thread.currentThread().getName()	 
        		+ String.valueOf(Thread.currentThread().getId()));
		}
		else {
			updateGPSUnknown();
		}
	}
	
	void updateGPSUnknown() {
        latG.setText(NO_LOC_DATA);
        lonG.setText(NO_LOC_DATA);
        altG.setText(NO_LOC_DATA);
        accuracyG.setText(NO_LOC_DATA);
        speedG.setText(NO_LOC_DATA);
        providerG.setText(Thread.currentThread().getName()	 
        		+ String.valueOf(Thread.currentThread().getId()));
        }
	
	void updateNetwork(Location loc) {
		if (loc != null) {
			latN.setText(String.valueOf(loc.getLatitude()));
			lonN.setText(String.valueOf(loc.getLongitude()));
			altN.setText(loc.hasAltitude() ? String.valueOf(loc.getAltitude()) : "n/a");
			accuracyN.setText(String.valueOf(loc.getAccuracy()));
			speedN.setText(loc.hasSpeed() ? String.valueOf(loc.getSpeed()) : "n/a");
			providerN.setText(loc.getProvider());
		}
		else{
			updateNetworkUnknown();
		}
	}
	
	void updateNetworkUnknown() {
        latN.setText(NO_LOC_DATA);
        lonN.setText(NO_LOC_DATA);
        altN.setText(NO_LOC_DATA);
        accuracyN.setText(NO_LOC_DATA);
        speedN.setText(NO_LOC_DATA);
        providerN.setText(NO_LOC_DATA);
        ageN.setText(NO_LOC_DATA);
        }

	void updateDistance() {
    	if (model.getGPSLocation() != null && model.getNetworkLocation() != null)
    		distance.setText(String.valueOf(model.getGPSLocation().distanceTo(model.getNetworkLocation())));
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        latG = (TextView)findViewById(R.id.latitude);
        lonG = (TextView)findViewById(R.id.longitude);
        altG = (TextView)findViewById(R.id.altitude);
        accuracyG = (TextView)findViewById(R.id.accuracy);
        statusG = (TextView)findViewById(R.id.status);
        ageG = (TextView)findViewById(R.id.age);
        speedG = (TextView)findViewById(R.id.speed);
        providerG = (TextView)findViewById(R.id.provider);
        
        latN = (TextView)findViewById(R.id.latitudeN);
        lonN = (TextView)findViewById(R.id.longitudeN);
        altN = (TextView)findViewById(R.id.altitudeN);
        accuracyN = (TextView)findViewById(R.id.accuracyN);
        statusN = (TextView)findViewById(R.id.statusN);
        ageN = (TextView)findViewById(R.id.ageN);
        speedN = (TextView)findViewById(R.id.speedN);
        providerN = (TextView)findViewById(R.id.providerN);
        distance = (TextView)findViewById(R.id.distance);
        
        clock = new Timer((TextView)findViewById(R.id.clock));
        new Thread(clock).start();
               
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        model = new PhoneLocationModel(lm);
    }
    public void onDestroy() {
    	super.onDestroy();
    	clock.done();
    }
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	    menu.add(Menu.NONE, 1, Menu.NONE, "Email Location").setAlphabeticShortcut('e');
	    menu.add(Menu.NONE, 2, Menu.NONE, "Exit").setAlphabeticShortcut('x');
	    return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
			Location locG = model.getGPSLocation();
			Location locN = model.getNetworkLocation();
			String body;
			if (locG != null && locN != null) {
				String text = "My current locations:\n" +
						"GPS    : "	+ (locG != null ? locG.getLatitude() : -999)
						+ ", " + (locG != null ? locG.getLongitude() : -999) + "\n" 
						+ "Network: "	+ (locN != null ? locN.getLatitude() : -999)
						+ ", " + (locN != null ? locN.getLongitude() : -999);
				String qmap_link;
				try {
					String query_string = "size=512x512"
					     + "&markers=" 
						 + URLEncoder.encode("color:blue|label:G|" 
					             + locG.getLatitude() + "," + locG.getLongitude(), "UTF-8")
					     + "&markers="
					     + URLEncoder.encode("color:green|label:N|" 
					             + locN.getLatitude() + "," + locN.getLongitude(), "UTF-8")
					     + "&sensor=true";
					qmap_link = "http://maps.googleapis.com/maps/api/staticmap?" + query_string;
				} catch (UnsupportedEncodingException e) {
					qmap_link = e.getMessage();
				}
				body = text + "\n " + qmap_link;
			}
			else {
				body = "(location unknown!)";
			}			
			Uri addr = Uri.fromParts("mailto", "jimhopp@gmail.com", null);
			Intent email = new Intent(Intent.ACTION_SENDTO, addr);
			email.putExtra(Intent.EXTRA_TEXT, body);
			email.putExtra(Intent.EXTRA_SUBJECT, "my location");
			PackageManager pm = getPackageManager();
			if (pm.resolveActivity(email, PackageManager.MATCH_DEFAULT_ONLY) != null) {
				startActivity(email);
			}
			else {
				Toast toast = Toast.makeText(this, "Sorry, email not configured on this device",
						Toast.LENGTH_SHORT);
				toast.show();			
			}
			return true;
			
		case 2: 
			finish();
			return true;

		default:
				break;
		}
		return false;
	}
}