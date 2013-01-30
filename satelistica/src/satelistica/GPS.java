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
 * @modified    01/30/2013
 * @version     0.1.1 (1)
*/
package satelistica; 

import processing.core.*;

public class GPS {
	public PVector coordinates;
	public PVector screenPos;
	public PVector CartesianPos;
	private float startLatitude=  52.519171F;
	private float startLongitude = 13.4060912F;
	private float startAltitude = 0.03F;
	public float latitude, longitude, altitude;
	boolean isVisible;
	public static final float EARTH_RADIUS = 6371;//EARTH RADIUS in KM
	public float SCALAR = 0.03F;//Default scaling
	
	/**
	 * 
	 * Constructor
	 * @param float lat
	 *          Latitude value
	 * @param float lon
	 *          Longitude value
	 * @param float alt
	 *          Altitude value
	 */
	public GPS(float lat, float lon, float alt){
		
		setCoordinates(lat, lon, alt);
		CartesianPos = computePosOnSphere();
		screenPos = new PVector();
		latitude = startLatitude;
		longitude = startLongitude;
		
	}
	
	public GPS(){
		
		setCoordinates(startLatitude, startLongitude, startAltitude);
		CartesianPos = computePosOnSphere();
		screenPos = new PVector();
	    altitude = EARTH_RADIUS;
	   
		
	}
	public void setCoordinates(float lat, float lon, float h){
		
		
		latitude = lat;
		longitude = lon;
		if(longitude > 180)longitude-=360;
		
		//h = h * 0.001f;//covert meter input to kilometers 
		altitude = (EARTH_RADIUS + h) * SCALAR;
		coordinates = new PVector(longitude, latitude, altitude);
	}
	
	public PVector computePosOnSphere(){
		PVector p = new PVector(coordinates.z, (float)Math.toRadians(coordinates.x) + (float)Math.PI, (float)Math.toRadians(coordinates.y));
	    PVector pv = toCartesian(p);
		return pv;
	} 
	public PVector getCoordinates(){
		return coordinates;}
	
	public PVector getPosition(){
		return CartesianPos;}
	
	public PVector getScreenPosition(){
		return screenPos;}
	
	/**
	 * 
	 * Get the altitude   
	 * 
	 **/
	public float getAltitude(){
		return altitude;}
	
	public void draw(){
		
		
	}
	/**
	 * Calculate the position in 3D Space
	 **/

	public void updatePosition() {
		
		
		CartesianPos = computePosOnSphere();
	}
	
	/**
	 * Calculate the Screen position
	 * 
	 **/

	public void updateScreenPosition(PApplet app){
		
		 screenPos=new PVector(app.screenX(-CartesianPos.x, -CartesianPos.y, CartesianPos.z), app.screenY(-CartesianPos.x, -CartesianPos.y, CartesianPos.z));

	}
	
	public boolean isVisible(PVector camPos){
	    // the dot product between 2 normalized vectors is an indication
	    // how closely aligned those 2 directions are
	    // if the result is >0.5 then the angle between the vectors
	    // is less than 90 degrees...
	    // here we check the difference between (normalized) camera position
	    // and the position of our image
	    // we use this as a tool to hide images if they are on the
	    // back side of the currently visible globe section
	    // the isVisible flag is used below in drawAsImage()

		PVector vn = CartesianPos;
		 vn.normalize();
		float dt = vn.dot(camPos);//pass a PVector object with the camera rotations to calculate visibility of 2D objects
		return isVisible = dt > 0.05;
		
		
	}
	
	/**
	 * Check if the mouse is over the Screen Position
	 * 
	 **/
	public boolean mouseOver(PApplet app) {
	    boolean mOver= false;
	    if (app.mouseX >= screenPos.x -5 && app.mouseX <= screenPos.x +5) {
	      if (app.mouseY >= screenPos.y -5 && app.mouseY <= screenPos.y +5) {
	        mOver=true;
	      }
	    }
	    return mOver;
	}
	
	/**
	 * 
	 * Transform Polar to Cartesian coordinates   
	 * 
	 **/
	public final PVector toCartesian(PVector vec) {
        final float a = (float) (vec.x * Math.cos(vec.z));
        final float xx = (float) (a * Math.cos(vec.y));
        final float yy = (float) (vec.x * Math.sin(vec.z));
        final float zz = (float) (a * Math.sin(vec.y));
        
        return new PVector(xx,yy,zz);
    }
	
	/**
	 * 
	 * Set the scaling factor  
	 * 
	 **/
	public void setScale(float s){
		
		SCALAR = s;
	}
	
}
