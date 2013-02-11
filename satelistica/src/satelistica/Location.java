
/**
 * satelistica
 * A simple wrapper for Predict4Java a java implementation of NORAD algorithms for satellite pass prediction and positioning.
 * http://satelistica.com/code
 *
 * Copyright (C) 2012 Luis Bustamante http://protonumerique.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Luis Bustamante http://protonumerique.net
 * @modified    02/06/2013
 * @version     0.1.1 (1)
*/

package satelistica;

import uk.me.g4dpz.satellite.*;
import org.json.*;

import processing.core.*;

public class Location extends GPS {
	GroundStationPosition GSPos;
	/*
	*GEOCODING URLS
	 */
	String name;
	String textValue = "";
	String BASE_URL = "http://geocoding.cloudmade.com/4d139834de3d4f34a51600b3fe235b23/geocoding/v2/find.js?query=";
	String FORMAT = "&format=json";
	String elevationURL="http://open.mapquestapi.com/elevation/v1/profile?&shapeFormat=raw&latLngCollection=";//"http://maps.googleapis.com/maps/api/elevation/json?locations=";
	String extraParams="&sensor=false";
	
	
	float heightAMSL;
	
	/**
	 * 
	 * Main Constructor, doesn'n need any extra libraries
	 * @param lat
	 * 				The latitude of your location
	 * @param lon
	 * 				The longitude of your location
	 * @param h
	 * 				The altitude of your location
	 * @param name
	 * 				The name of your location
	 */
	public Location(float lat, float lon, float h, String name){
		
		super(lat, lon, h );
		
		heightAMSL = h;
		
		latitude = lat;
	    longitude = lon;
		//altitude = EARTH_RADIUS + heightAMSL*0.001F; //convert height from Meters to kilometers
		
		GSPos= new GroundStationPosition(latitude, longitude, heightAMSL);
		setCoordinates(latitude, longitude, heightAMSL*0.001F);
		
		this.name = name;
	}
	
	/**
	 * 
	 * Constructor, requires the importing of the json4Processing (https://github.com/agoransson/JSON-processing/downloads)Library to parse the geocoding response
	 * @param PApplet
	 * 				The parent PApplet
	 * @param name
	 * 				The name of our location
	 */
	
	public Location(PApplet app, String name){
		super();
		getAddress(app, name);
		processElevation(app);
		
		GSPos= new GroundStationPosition(latitude, longitude, heightAMSL);
		setCoordinates(latitude, longitude, heightAMSL*0.001F);//convert height from Meters to kilometers
		CartesianPos = computePosOnSphere();
		this.name = name;
	}
	/**
	 * 
	 * Constructor, doesn'n need any extra libraries
	 * Picks a default location
	 */
	
	public Location(){
		super();
		heightAMSL = 34F;
		
		altitude = EARTH_RADIUS + heightAMSL*0.001F;//convert to kilometers
		
		GSPos= new GroundStationPosition(latitude, longitude, heightAMSL);
		
		this.name = "Berlin";
	} 
	/**
	 * 
	 * Try to geocode the given name, or default to a location in defaultLocations.txt - Will ask for JSON for Processing lib
	 * @param PApplet
	 * 				The parent PApplet
	 * @param query
	 * 				The name of the desired location. Might fail or return a false Location. Better to read the geocoding.cloudmade.com API Reference.
	 */
	public void getAddress(PApplet app, String query) {


		  // Get the JSON formatted response
		  query = query.replace(' ', '&');
		  //try {
		  String response = app.loadStrings( BASE_URL + query)[0];
		  
		  // Make sure we got a response.
		  if ( response != null ) {
		    //PApplet.println(response);
		    try {
		      // Initialize the JSONObject for the response
		      JSONObject root = new JSONObject( response );
		      JSONObject features = root.getJSONArray( "features" ).getJSONObject(0);
		      JSONObject centroid = features.getJSONObject( "centroid" );
		      // Get the "condition" JSONObject
		      JSONArray coords = centroid.getJSONArray("coordinates");

		      // Get the "temperature" value from the condition object
		      double lat = coords.getDouble(0);
		      double lon = coords.getDouble(1);
		      // Print the temperature
		      //println( "Lat: "+lat );
		      latitude = (float) lat;
		      longitude = (float) lon;
		      
		      PApplet.println("lat: " +latitude +" lon: "+ longitude);
		      //heightAMSL = getElevation();
		     
		    }
		    catch(Exception e) {
		    	 PApplet.println("No response for Address or no Json library imported, please try again after installing the library (https://github.com/agoransson/JSON-processing/downloads) or checking your spelling");
		    	 PApplet.println("Setting your location to Berlin, Germany...");
		    	 PApplet.println(e);
		    }
		  }
		
		
	}
	
	
	/**
	 * 
	 * Get elevation of the geocoded location - Will ask for JSON for Processing lib
	 * @param PApplet
	 * 				The parent PApplet
	 */
	
	public void processElevation(PApplet app) {
		
			  String query = elevationURL + latitude +","+ longitude;//+extraParams;
			  //try {
			  String response = app.loadStrings( query )[0];
			  double locAltitude = 0.0;
			  if ( response != null ) {
			    try {
	
			      JSONObject results = new JSONObject( response );
			      JSONObject profile = results.getJSONArray( "elevationProfile" ).getJSONObject(0);
			      double el = profile.getDouble("height");
			      locAltitude = el;
			      PApplet.println("Altitude: " + locAltitude);
			    } 
			    catch (Exception e) {
			    	PApplet.println(e);
			    }
	
			    //println(response);
			  }
			  else  if ( response == null ) {
				  PApplet.println("wRONG: " + query);
			  }
			  heightAMSL = (float)locAltitude;
			  
			  /*}catch(Exception e){
				  
				  PApplet.println("No response for elevation or no Json library imported, would you mind downloading the library (https://github.com/agoransson/JSON-processing/downloads) or checking your spelling?");
			  }*/
		
		
	}
	
	/**
	 * 
	 * Get name of the location
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * 
	 * Get altitude of the location (above sea level)
	 */
	public float getHeightAMSL(){
		return heightAMSL;
	}


}
