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

import java.util.*;

import processing.core.PVector;
import uk.me.g4dpz.satellite.*;


public class Predictor {
	
	public Location location;
	public Sat sat;
	private GroundStationPosition GSPos;
	private TLE tle;
	
	/**
	 * 
	 * constructor
	 * @param Location loc
	 *          A Location to predict passes over
	 * @param Sat psat
	 *          A satellite object
	 */
	public Predictor(Location loc, Sat psat){
		sat = psat;
		location = loc;
		GSPos = loc.GSPos;
		tle =sat.getTLE();
		
	}
	/**
	 * 
	 * constructor
	 * @param Location loc
	 *          A Location to predict passes over
	 * @param Sat psat
	 *          A satellite object
	 */
	public Predictor(Sat psat){
		sat = psat; 
		if(sat.getlocation()!=null){location = sat.getlocation();}else if(sat.getlocation()==null){location = new Location();}///If no location given, then location defaults to Berlin
		
		GSPos = location.GSPos;
		tle =sat.getTLE();
	}
	
	/**
	 * 
	 * A method to change the location
	 * @param Location loc
	 *          Change the location for predictions
	 */
	public void setLocation(Location loc){
		location = loc;
	}
	/**
	 * 
	 * A method to change the satellite
	 * @param Location loc
	 *          Change the location for predictions
	 */
	public void setSat(Sat s){
		sat = s;
	}
	
	/**
	 * 
	 * A method to return a String array with the passes over the given location from a Start Date to 'ha' hours ahead 
	 * @param Date sd
	 *          Date to start predicting (use new Date() to set it to now). See Java.Util.Date Javadocs for more information on setting up Dates and Calendars
	 * @param int ha
	 * 			Number of hours ahead to predict passes        
	 */
	public String [] getPassesAsString(Date sd, int ha){
		
		ArrayList<SatPassTime> passes = getPasses(sat.getTLE(), sd, ha);
		String [] passList = new String[passes.size()];
		int ct = 0;
		for(Iterator<SatPassTime> i = passes.iterator(); i.hasNext();){
			SatPassTime spt = (SatPassTime)i.next();
			String str =  spt.toString();
			passList[ct]= str;
			ct++;
		}
		return passList;
	}
	
	/**
	 * 
	 * A method to return a two-dimensional Array with the start time and end time of each pass over the given location from a Start Date to 'ha' hours ahead 
	 * @param Date sd
	 *          Date to start predicting (use new Date() to set it to now). See Java.Util.Date Javadocs for more information on setting up Dates and Calendars
	 * @param int ha
	 * 			Number of hours ahead to predict pases        
	 */
	public Date[][] getPassesAsDates(Date sd, int ha){
		
		
		
		ArrayList<SatPassTime> passes = getPasses(sat.getTLE(), sd, ha);
		Date [][] passList = new Date[passes.size()][2];
		int ct = 0;
		for(Iterator<SatPassTime> i = passes.iterator(); i.hasNext();){
			SatPassTime spt = (SatPassTime)i.next();
			Date d1 = spt.getStartTime();
			Date d2 = spt.getEndTime();
			passList [ct][0] = d1;
			passList [ct][1] = d2;
			ct++;
		}
		
		return passList;
		
	}
	
	/**
	 * 
	 * A method to return an ArrayList of "SatPassTime" objects from a Start Date to 'ha' hours ahead.
	 * @param TLE tle
	 *          a TLE object
	 * @param Date start        
	 *          Date to start predicting (use new Date() to set it to now). See Java.Util.Date Javadocs for more information on setting up Dates and Calendars
	 * @param int ha
	 * 			Number of hours ahead to predict pases        
	 */
	private ArrayList<SatPassTime> getPasses(final TLE theTLE, final Date start, final int ha) {
		
		ArrayList<SatPassTime> passes = new ArrayList<SatPassTime>();
		 try {
		  PassPredictor predictor = new PassPredictor(theTLE, GSPos);
		  
		  Date trackStartDate = start;
		  final Date trackEndDate = new Date(start.getTime() + (ha * 60L * 60L * 1000L));
		  Date lastAOS;
		  try {
		    int ct = 0;
		    do {

		      final SatPassTime pass = predictor.nextSatPass(trackStartDate);
		      lastAOS = pass.getStartTime();
		      passes.add(pass);
		   
		      trackStartDate = new Date(pass.getEndTime().getTime() + (threeQuarterOrbitMinutes() * 60L * 1000L));
		      ct++;
		    } 
		    while (lastAOS.compareTo (trackEndDate) < 0);
		  }
		  catch(Exception e) {
		  }
		 }catch(Exception e) {
		  }
		  return passes;
		}
	
	/**
	 * 
	 * A method to return an ArrayList of "PVector" positions(x,y,z) from  minutesBefore the reference date to minutesAfter.
	 * @param Date refDate
	 *          the reference Date, can be the present date or a date in the past or future
	 * @param int res        
	 *          Resolution, or increment rate to generate predictions (tricky to define, but it should be kept low, specially for long time spans)
	 * @param int minutesBefore
	 * 			Used to define a date in the past, substracting minutes to the refDate. Pass 0 if it must be the same refDate
	 * @param int minutesAfter
	 * 			Used to define a date in the future, adding minutes to the refDate. Pass 0 if it must be the same refDate       
	 */
public ArrayList<PVector> getOrbitPositions(Date refDate, int res, int minutesBefore, int minutesAfter){
		
		ArrayList<PVector> positions = new ArrayList<PVector>();
		
		int incrementSeconds = (int)(((minutesBefore+minutesAfter)*60) / res);
			try {
				
				 
				 Date trackDate = new Date(refDate.getTime() - (minutesBefore * 60L * 1000L));
				 final Date endDateDate = new Date(refDate.getTime() + (minutesAfter * 60L * 1000L));
				 int ct = 0;
				 while (trackDate.before(endDateDate)) {
					 SatPos p = sat.getPositionObject(trackDate);
					 GPS gps = new GPS((float)(p.getLatitude()/ (Math.PI * 2.0) * 360), (float)(p.getLongitude()/ (Math.PI * 2.0) * 360), (float)p.getAltitude());
					 gps.updatePosition();
					 PVector vi = gps.getPosition(); 
					 positions.add(vi);
					 trackDate = new Date(trackDate.getTime() + (incrementSeconds * 1000));
					 ct ++;
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		
		return positions;
	}
	
	/**
	 * 
	 * A method to return an ArrayList of "PVector" coordinates (lat,lon) from  minutesBefore the reference date to minutesAfter.
	 * @param Date refDate
	 *          the reference Date, can be the present date or a date in the past or future
	 * @param int res        
	 *          Resolution, or increment rate to generate predictions (tricky to define, but it should be kept low, specially for long time spans)
	 * @param int minutesBefore
	 * 			Used to define a date in the past, substracting minutes to the refDate. Pass 0 if it must be the same refDate
	 * @param int minutesAfter
	 * 			Used to define a date in the future, adding minutes to the refDate. Pass 0 if it must be the same refDate       
	 */
	public ArrayList<PVector> getOrbitAsCoordinates(Date refDate, int res, int minutesBefore, int minutesAfter){
		
		ArrayList<PVector> positions = new ArrayList<PVector>();
		
		int incrementSeconds = (int)(((minutesBefore+minutesAfter)*60) / res);
			try {
				
				 
				 Date trackDate = new Date(refDate.getTime() - (minutesBefore * 60L * 1000L));
				 final Date endDateDate = new Date(refDate.getTime() + (minutesAfter * 60L * 1000L));
				 int ct = 0;
				 while (trackDate.before(endDateDate)) {
					 SatPos p = sat.getPositionObject(trackDate);
					 GPS gps = new GPS((float)(p.getLatitude()/ (Math.PI * 2.0) * 360), (float)(p.getLongitude()/ (Math.PI * 2.0) * 360), (float)p.getAltitude());
					 PVector vi = gps.getCoordinates(); 
					 positions.add(vi);
					 trackDate = new Date(trackDate.getTime() + (incrementSeconds * 1000));
					 ct ++;
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		
		return positions;
	}
	
	/**
	 * 
	 * Custom method 
	 *            
	 */
	private int threeQuarterOrbitMinutes() {
		  return (int)(24.0 * 60.0 / tle.getMeanmo() * 0.75);
	}
	
	
	

}
