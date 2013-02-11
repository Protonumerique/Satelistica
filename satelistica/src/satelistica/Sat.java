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

import java.util.Date;

import processing.core.*;
import uk.me.g4dpz.satellite.*;


public class Sat extends GPS{
	
	String name;
	public  PVector pos3D = null;
	private Satellite satellite;
	GroundStationPosition GSPos;
	public Location location;
	public TLE element;
	SatPos position;
	public String type;
	
	
	Sat(String[]tle){
		super();
		name = tle[0];
		element = new TLE(tle);
		satellite = SatelliteFactory.createSatellite(element); 
		location = new Location();	
	}
	
	Sat(TLE tle){
		super();
		name = tle.getName();
		element = tle;		
		satellite = SatelliteFactory.createSatellite(element); 
		location = new Location();		
	}
	
	public void beginTrack(Location loc){
		location = loc;
		GSPos = location.GSPos;
		updateCoordinates(new Date());
		
	}
	
	public void updateCoordinates(Date d){
		
		try {
		      //SatPassTime next= pp.nextSatPass(d);
			  
		      position = satellite.getPosition(GSPos, d);
		      float h = (float) (position.getAltitude());
			  float lat = (float) (position.getLatitude()/ (Math.PI * 2.0) * 360);
			  float lon = (float)(position.getLongitude()/ (Math.PI * 2.0) * 360);//PApplet.map ((float)(position.getLongitude()/ (Math.PI * 2.0) * 360), 0, 360, -180, 180);
			  setCoordinates(lat, lon, h);
			  //PApplet.println("tracking "+name + "at latitude: " +latitude+ "at longitude " + longitude );
		     }catch(Exception e){
		    	 
		    	 PApplet.println(e);
		     }
		     
		
	}
	
	public SatPos getPositionObject(Date d){
		
		position = satellite.getPosition(GSPos, d);
		return position;
	}
	
	/**
     * @return the elevation
     */

	public float getElevation(Date d) {
		position = getPositionObject(d);
		
        return (float)(position.getElevation()/ (Math.PI * 2.0) * 360);
    }
	
	/**
     * @return the azimuth
     */

	public float getAzimuth(Date d) {
		position = getPositionObject(d);
        return (float)(position.getAzimuth()/ (Math.PI * 2.0) * 360);
    }
	
	/**
     * @return if is passing over our location
     */
    public  boolean isPassing(Date d) {
    	position = getPositionObject(d);
    	boolean passing = false;
    	if(getElevation(d)>Math.toRadians(10)){passing = true;}
        return passing;
    }
	
	/**
     * @return the aboveHorizon
     */
    public  boolean isAboveHorizon(Date d) {
    	position = getPositionObject(d);
        return position.isAboveHorizon();
    }
    

    /**
     * @return the TLE of this satellite
     */
	public TLE getTLE(){
		return element;
	}
	
	/**
     * @return the name of this Satellite
     */
	public String getName(){
		return name;
	}
	
	/**
     * @return the Location over which we are predicting
     */
	public Location getlocation(){
		return location;
	}
	
	/**
     * @return the position as string
     */
	public String posToString(){
		
		return position.toString();
	}
	
	/**
     *  Additional method to set the time of the track
     */
	public void setTime(Date d){
		position.setTime(d);
		
	}
	
	/**
     *  set the type of satellite
     */
	public void setType(String t){
		type = t;
		
	}
	
	/**
     *  get the type of satellite
     */
	public String getType(){
		return type;
		
	}
	
	
	 
	
}
