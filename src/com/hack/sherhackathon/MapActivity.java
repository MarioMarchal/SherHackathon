/*
 * The activity for the pools/bain libres.
 * 
 * Mario -- Cleaning up and understanding...
 * 
 */


package com.hack.sherhackathon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.location.Location;
import android.app.ProgressDialog;
import android.content.Intent;



public class MapActivity extends ActionBarActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener{

	protected static LocationClient locationclient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		/*
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		*/
		//ActionBar actionBar = getSupportActionBar();
		//actionBar.hide();
		
		locationclient = new LocationClient(this, this, this);
        locationclient.connect();        
	}
	
	protected void onStop() {
        if (locationclient.isConnected()){
		// Disconnecting the client invalidates it.
        locationclient.disconnect();
        }
        super.onStop();
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub	
		//this.finish();
	}

	@Override
	public void onConnected(Bundle arg0) {
		// if connects to location client ok display map
		getSupportFragmentManager().beginTransaction()
		.add(R.id.container, new PlaceholderFragment()).commit();	
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		//this.finish();
	}
		
/**
 * A placeholder fragment containing a simple view.
 * 
 * Not sure --- but significant part of this activity...
 * 
 */
	
public static class PlaceholderFragment extends Fragment {
		  
		  LatLng POS;
		  LatLng piscine;
		  private GoogleMap mapper;
		  JSONObject js;
		  JSONObject jse;
		  double lat;
		  double longi;
		  ArrayList<ArrayList<Double>> pairs = new ArrayList<ArrayList<Double>>();		  
		  ArrayList<String> names = new ArrayList<String>();
		  ArrayList<ArrayList<Double>> pairs2 = new ArrayList<ArrayList<Double>>();
          ArrayList<String> names2 = new ArrayList<String>();
		  double myLat;
		  double myLong;
		 String currName;
		 ProgressDialog pd;
		
		 
		 public void onCreate (Bundle savedInstanceState){
			 super.onCreate(savedInstanceState);			 
			 //get info from dataset as early as possible...
			 new SportPoints().execute();
		 }

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_map, container, false);
			return rootView;
		}
		
		//
		public void onActivityCreated(Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);
            
            pd = new ProgressDialog(getActivity());
            pd.setTitle("Recherche en cours...");
            pd.setMessage("Veuillez patienter svp");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
     
            // moved to onCreate to for earlier execution
            //new SportPoints().execute();
                        
            //--------- map related ------------           
            /*
            // Acquire a reference to the system Location Manager
             LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            // Define a listener that responds to location updates
             LocationListener locationListener = new LocationListener() {
                  
                    public void onLocationChanged(Location location) {
                      // Called when a new location is found by the network location provider.
                      //makeUseOfNewLocation(location);
                        myLong = location.getLongitude();
                        myLat = location.getLatitude();
                        POS = new LatLng(myLat, myLong);                        
                    }
                    public void onStatusChanged(String provider, int status, Bundle extras) {}
                    public void onProviderEnabled(String provider) {}
                    public void onProviderDisabled(String provider) {}
               };
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            */ 
  
            // add the Map fragment
            mapper = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();          
            mapper.setMyLocationEnabled(true);
            
            //Get current location from location client
            Location currentlocation = locationclient.getLastLocation();            
            myLong = currentlocation.getLongitude();
            myLat = currentlocation.getLatitude();
            POS = new LatLng(myLat, myLong);
            
            /*
            // add marker
            mapper.addMarker(new MarkerOptions().position(POS)
                    .title("You are here!")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            */
            // Move the camera instantly to your location with a zoom.
            mapper.moveCamera(CameraUpdateFactory.newLatLngZoom(POS, 12));              
        }		
		
		
		
		public void mapStuff(){
			// red for closed pools
			boolean printRed = true;			
			
			// print the open pools (i.e. pairs)
			for(int i=0; i<pairs.size(); i++){
				printRed = false;
				//mapper = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
				piscine = new LatLng((double)(pairs.get(i)).get(0), (double)(pairs.get(i)).get(1));
				currName=(String)names.get(i); 
				mapper.addMarker(new MarkerOptions().position(piscine)
			        .title(currName)
			        .snippet("Open")
			        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				//pd.dismiss();
				 mapper.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {	
				        public void onInfoWindowClick(Marker marker) {
				        	//Log.d("The Current name is",currName);
							Intent it = new Intent(getActivity(), InfoActivity.class);
				            Bundle extras=new Bundle();
				            extras.putString("Name",marker.getTitle());
				            it.putExtras(extras);
				            if(!marker.getTitle().equals("You are here"))
				            startActivity(it);	
				        }
				    });
			}

            // plot the closed pools (i.e. pairs2, names2)
			if (printRed){            
	            for(int i=0; i<pairs2.size(); i++){                
	            	//((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		            piscine=new LatLng((double)(pairs2.get(i)).get(0), (double)(pairs2.get(i)).get(1));
		            currName=(String)names2.get(i); 
		            mapper.addMarker(new MarkerOptions().position(piscine)
		            		.title(currName));
		            //pd.dismiss();
		            mapper.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {		
		                    public void onInfoWindowClick(Marker marker) {
		                        //handleClick();
		                        Log.d("The Current name is",currName);
		                        Intent it = new Intent(getActivity(), InfoActivity.class);
		                        Bundle extras=new Bundle();
		                        extras.putString("Name",marker.getTitle());
		                        it.putExtras(extras);
		                        if(!marker.getTitle().equals("You are here"))
		                        startActivity(it);    
		                    }
		                });
	            }            
        }
		//get rid of progress dialog after points are marked.
        pd.dismiss();
		}
		
		//
		private static String readUrl(String urlString) throws Exception {
		    BufferedReader reader = null;
		    try {
		        URL url = new URL(urlString);
		        reader = new BufferedReader(new InputStreamReader(url.openStream()));
		        StringBuffer buffer = new StringBuffer();
		        int read;
		        char[] chars = new char[1024];
		        while ((read = reader.read(chars)) != -1)
		            buffer.append(chars, 0, read); 

		        return buffer.toString();
		    } finally {
		        if (reader != null)
		            reader.close();
		    }
		}
		
		
		/**
		 * 
		 *
		 */
		class SportPoints  extends AsyncTask<String, String, String> {
            
            @Override
            protected String doInBackground(String... params) {
                
                try {
                    js= new JSONObject(readUrl("http://donnees.ville.sherbrooke.qc.ca/storage/f/2014-03-21T19%3A42%3A17.534Z/bain-libre-interieur.json"));
                    JSONObject jss=js.getJSONObject("EVTS");
                    JSONArray ja=jss.getJSONArray("EVT");
                    for(int i=0; i<ja.length(); i++){
                        JSONObject jo=ja.getJSONObject(i);
                        
                        //-------------- check dates ------------
                        
                        //get the dates, hours the pool is available
                        String dateStart = jo.getString("DT01");
                        String dateEnd = jo.getString("DT02");
                        String hrStart = jo.getString("HR01");
                        String hrEnd = jo.getString("HR02");
                        
                        //the day is sometimes empty...
                        String day;    
                        try{
                            day = jo.getString("HOR");
                        } catch (Exception e) {
                            day = " ";
                        }
                        
                        int weekdaynum = 0;
                        switch (day) {
                           case "Dimanche": weekdaynum=1; break;
                           case "Lundi": weekdaynum=2; break;
                           case "Mardi": weekdaynum=3; break;
                           case "Mercredi": weekdaynum=4; break;
                           case "Jeudi": weekdaynum=5; break;
                           case "Vendredi": weekdaynum=6; break;
                           case "Samedi": weekdaynum=7; break;
                           default: weekdaynum=0; break;
                        }                        
                        
                        //convert to date
                        Date dateStartOK = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA_FRENCH).parse(dateStart);
                        Date dateEndOK = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA_FRENCH).parse(dateEnd);                        
                        Date hrStartOK = new SimpleDateFormat("HH:mm:ss", Locale.CANADA_FRENCH).parse(hrStart);
                        Date hrEndOK = new SimpleDateFormat("HH:mm:ss", Locale.CANADA_FRENCH).parse(hrEnd);
                        
                        //convert to calendar
                        Calendar dateStartCal = Calendar.getInstance();
                        dateStartCal.setTime(dateStartOK);                        
                        Calendar dateEndCal = Calendar.getInstance();
                        dateEndCal.setTime(dateEndOK);   
                        Calendar hrStartCal = Calendar.getInstance();
                        hrStartCal.setTime(hrStartOK); 
                        Calendar hrEndCal = Calendar.getInstance();
                        hrEndCal.setTime(hrEndOK);
                        
                        int hrStartvalue = hrStartCal.get(Calendar.HOUR_OF_DAY);
                        //int minStartvalue = hrStartCal.get(Calendar.MINUTE);
                        int hrEndvalue = hrEndCal.get(Calendar.HOUR_OF_DAY);
                        //int minEndvalue = hrEndCal.get(Calendar.MINUTE);
                        
                        //current date time                        
                        Calendar dateNow = Calendar.getInstance();
                        //int Nowday = dateNow.get(Calendar.DAY_OF_MONTH);
                        //int NowMonth = dateNow.get(Calendar.MONTH);
                        int NowHr = dateNow.get(Calendar.HOUR_OF_DAY);
                        //int NowMin = dateNow.get(Calendar.MINUTE);                        
                        int NowDayofWeek = dateNow.get(Calendar.DAY_OF_WEEK);
                        
                        /*
                        //log
                        Log.d("date start: ", dateStartOK.toString());
                        Log.d("date end: ", dateEndOK.toString());                        
                        //Log.d("date now: ", dateNow.toString());                        
                        Log.d("hour start: ", hrStartOK.toString());
                        Log.d("hour end: ", hrEndOK.toString());
                        Log.d("day : ", day.toString());
                        //Log.d("today's date : ", dateNow.toString());
                        Log.d("now, min : ", ("" + NowMin));
                        Log.d("now, day of week : ", ("" +NowDayofWeek));
                        */
                        
                        /*
                        boolean dateOK = false;
                        boolean hrminOK = false;
                        boolean dayOK = false;
                        boolean minOK = false;
                        
                        if(dateNow.before(dateEndCal) && dateNow.after(dateStartCal)){
                            dateOK = true;                            
                        };                                
                        //time
                        if ((NowHr >= hrStartvalue && NowHr <= hrEndvalue )){  // && (NowMin >= minStartvalue && NowMin <= minEndvalue )){
                            hrminOK = true;
                        };
                        if (NowMin >= minStartvalue && NowMin <= minEndvalue ){
                            minOK = true;
                        };
                        //day
                        if (weekdaynum == NowDayofWeek || weekdaynum == 0){
                            dayOK = true;
                        };
                        */
                        
                        /*
                        Log.d("date ok : ", "" + dateOK);
                        Log.d("hr OK : ", "" + hrminOK);
                        Log.d("min OK : ", "" + minOK);
                        Log.d("day ok : ", "" + dayOK);
                        */
                        
                        //-----------------
                        //if ok with today's date then add to arrays
                        //compare date                        
                        if( (dateNow.before(dateEndCal) && dateNow.after(dateStartCal))
                                //time
                                && (NowHr >= hrStartvalue && NowHr <= hrEndvalue )// && (NowMin >= minStartvalue && NowMin <= minEndvalue )
                                //day
                                && (weekdaynum == NowDayofWeek || weekdaynum == 0)
                                ) 
                        {
                            //add values to be plotted..
                            String t=jo.getString("GEOM");
                            names.add(jo.getString("LOC"));
                            String s=t.substring(7,t.length()-1);
                            
                            String[] arr=s.split(" ");
                            lat=Double.parseDouble(arr[0]);
                            longi=Double.parseDouble(arr[1]);
                            ArrayList<Double> values = new ArrayList<Double>();
                            values.add(lat);
                            values.add(longi);
                            pairs.add(values);
                            
                        }
                        else{
                            //add values to be plotted.. but that are NOT available right now
                            String t=jo.getString("GEOM");
                            names2.add(jo.getString("LOC"));
                            String s=t.substring(7,t.length()-1);
                            
                            String[] arr=s.split(" ");
                            lat=Double.parseDouble(arr[0]);
                            longi=Double.parseDouble(arr[1]);
                            ArrayList<Double> values = new ArrayList<Double>();
                            values.add(lat);
                            values.add(longi);
                            pairs2.add(values);
                        }                 
                    }                
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
			
			protected void onPostExecute(String file_url) {					
					mapStuff();
		    }
	}
	}


}
