
package satelistica;



import java.util.*;

import processing.core.*;
import uk.me.g4dpz.satellite.*;

/**
 * A commodity class to manage multiple Satellites based on a given tle file, offering some extra features. 
 * The preferred URL to access TLE data is celestrak: "http://celestrak.com/NORAD/elements/"
 * There a wide selection of TLE Lists is kept and updated periodically
 * @example SimpleSatelliteGroup
 *
 */

public class SatelliteGroup {
	
	// myParent is a reference to the parent sketch
	PApplet myParent;
	
	protected String dataSource;
	protected ArrayList<TLE> tlelist;
	public Location location;
	public Calendar startCalendar;
	public ArrayList<Sat> satellites;
	public ArrayList<Sat> onSight;
	public String locationName;
	
	
	public final static String VERSION = "0.1.1";
	

	/**
	 * Constructor
	 * 			
	 * @param PApplet theParent
	 *          The Applet Parent
	 * @param String url
	 *          URL to a TLE list like: "http://celestrak.com/NORAD/elements/visual.txt"
	 */
	public SatelliteGroup(PApplet theParent, String url) {
		myParent = theParent;
		dataSource = url;
		initTLE(dataSource);
		location = new Location();
		buildSats();
		startTracking();
	}
	
	/**
	 * Constructor
	 * 
	 * @example SimpleSatelliteGroup			
	 * @param PApplet theParent
	 *          The Applet Parent
	 * @param String url
	 *          URL to a TLE list like: "http://celestrak.com/NORAD/elements/visual.txt"
	 * @param Location l
	 *          An object of Type Location (See reference). This is the place on earth where the tracking takes place
	 */
	
	public SatelliteGroup(PApplet theParent, String url, Location l) {
		myParent = theParent;
		dataSource = url;
		initTLE(dataSource);
		location = l;
		buildSats();
		startTracking();
	}
	
	/**
	 * Constructor
	 * 			
	 * @param PApplet theParent
	 *          The Applet Parent
	 * @param String url
	 *          URL to a TLE list like: "http://celestrak.com/NORAD/elements/visual.txt"
	 */
	public SatelliteGroup(PApplet theParent) {
		myParent = theParent;
		//dataSource = url;
		//parseTLE(dataSource);
		location = new Location();
		tlelist = new ArrayList<TLE>();
		satellites = new ArrayList<Sat>();
		//buildSats();
		//startTracking();
	}
	
	/**
	 * 
	 * parse each TLE from the text file and create new lists for building sats
	 * @param String filename
	 * 				The URL of a properly formatted tle set
	 */
	
	public void initTLE(String filename) {

		  String[] tles = myParent.loadStrings(filename);
		  tlelist = new ArrayList<TLE>();
		  satellites = new ArrayList<Sat>();
		  onSight = new ArrayList<Sat>();

		  final String [] tle = new String [3];
		  int cnt =0;
		  for (int i = 0; i < tles.length; i++) {

		    String linea = tles[i].trim();
		    //if(i == 0)tle[0] = linea;
		    switch (cnt) {
		    case 0:
		      tle[0] = linea;
		      cnt++;
		      break;
		    case 1:
		      tle[1] = linea;
		      PApplet.println(linea);
		      cnt++;
		      break;
		    case 2:
		      tle[2] = linea;
		      PApplet.println(linea);
		      cnt = 0;
		      try {
		        TLE t = new TLE(tle);
		        tlelist.add(t);
		      }
		      catch(Exception e) {
		    	  PApplet.println(e);
		      }
		      break;
		    default:
		      break;
		    }
		  }
	}
	
	/**
	 * 
	 * add a TLE list from the given text file to the existing lists
	 * @param String filename
	 * 				The URL of a properly formatted tle set
	 */
	
	public void addTLEList(String filename) {

		  String[] tles = myParent.loadStrings(filename);
		  if(tlelist==null)tlelist = new ArrayList<TLE>();
		  if(satellites==null)satellites = new ArrayList<Sat>();
		  if(onSight==null)onSight = new ArrayList<Sat>();

		  final String [] tle = new String [3];
		  int cnt =0;
		  for (int i = 0; i < tles.length; i++) {

		    String linea = tles[i].trim();
		    //if(i == 0)tle[0] = linea;
		    switch (cnt) {
		    case 0:
		      tle[0] = linea;
		      cnt++;
		      break;
		    case 1:
		      tle[1] = linea;
		      PApplet.println(linea);
		      cnt++;
		      break;
		    case 2:
		      tle[2] = linea;
		      PApplet.println(linea);
		      cnt = 0;
		      try {
		        TLE t = new TLE(tle);
		        Sat sat = new Sat(t);
		        if(!checkDuplicates(t)){
		        	tlelist.add(t);
		        	addSat(sat);
		        	}
		      }
		      catch(Exception e) {
		    	  PApplet.println(e);
		      }
		      break;
		    default:
		      break;
		    }
		  }
	}
	
	/**
	 * 
	 * parse each TLE from the text file and feed it to an ArrayList
	 * @param String filename
	 * 				The URL of a properly formatted tle set
	 */
	
	public void addElement(String [] element) {

		  String[] tle = element;
		  
		  
		  //final String [] tle = new String [3];
		  int cnt =0;
		  for (int i = 0; i < tle.length; i++) {

		    String linea = tle[i].trim();
		    //if(i == 0)tle[0] = linea;
		    switch (cnt) {
		    case 0:
		      tle[0] = linea;
		      cnt++;
		      break;
		    case 1:
		      tle[1] = linea;
		      PApplet.println(linea);
		      cnt++;
		      break;
		    case 2:
		      tle[2] = linea;
		      PApplet.println(linea);
		      cnt = 0;
		      try {
		        TLE t = new TLE(tle);
		        Sat sat = new Sat(t);
		        if(!checkDuplicates(t)){
		        	tlelist.add(t);
		        	addSat(sat);
		        	}
		        
		      }
		      catch(Exception e) {
		    	  PApplet.println(e);
		      }
		      break;
		    default:
		      break;
		    }
		  }
	}
	

	public void addSat(Sat sat) {
			
			satellites.add(sat);
			sat.beginTrack(location);
		
	}
	/**
	 * 
	 * Add satellite objects to the Group
	 * 
	 */
	public void addSats(Sat[] sats) {
		
		for (int i = 0; i < sats.length; i++) {
			
			tlelist.add(sats[i].getTLE());
			satellites.add(sats[i]);
		}
		startTracking();
	}
	
	/**
	 * 
	 * Make satellite objects based on parsed TLE set 
	 * 
	 */
		
	public void buildSats() {
		
		  for (Iterator<TLE> i = tlelist.iterator(); i.hasNext();) {
			
		    Sat sat = new Sat(i.next()); 
		    //sat.beginTrack(location);
		    satellites.add(sat);
		  }
	}
	
	/**
	 * 
	 * Initialize Tracking of satellites over your location
	 * 
	 */
	
	public void startTracking(){
		
		for (Iterator<Sat> i = satellites.iterator(); i.hasNext();) {
			
			Sat sat = (Sat)i.next();
			sat.beginTrack(location);
		}
		
	}
	
	/**
	 * 
	 * Call every loop to keep updating satellite position based on the loaded TLE
	 * 
	 */
	
	public void track(){
		
		for (Iterator<Sat> i = satellites.iterator(); i.hasNext();) {
			
			Sat psat = (Sat)i.next();
			psat.updateCoordinates(new Date());
			psat.updatePosition();
			psat.updateScreenPosition(myParent);
			
		}
		
	}
	

	/**
	 * 
	 * print Satellite names
	 * 
	 */
		
	public void printNames(){
		
		for (int i = 0; i < tlelist.size(); i++) {
			TLE tle = (TLE)tlelist.get(i);
			PApplet.println(tle.getName());
			
		}
		
		
	}
	
	/**
	 * @param Date d
	 * 		A Date object
	 * 
	 * @return an ArrayList of Sat objects which are passing over the given location
	 */
	public ArrayList<Sat> getPassingSats(Date d) {
		
		if (satellites!=null) {
			for (Iterator<Sat> i = satellites.iterator(); i.hasNext();) {
				
				Sat sat = (Sat)i.next();
				try{
					if(sat.isPassing(d)){
						if (!onSight.contains(sat)) {
			             onSight.add(sat);
			            }
					} else if(!sat.isPassing(d)){
						 if (onSight.contains(sat)) {
					            int n = onSight.indexOf(sat); 
					            onSight.remove(n);
						 }
						
					}
					
				}catch(Exception e){}
				
			}
			
		}
		return onSight;
	}
	
	/**
	 * 
	 * @return the ArrayList of all Sat objects 
	 */
	public ArrayList<Sat> getSats () {
		return satellites;
		
	}
	
	/**
	 * @param Date d
	 * 		A Date object
	 * 
	 * @return The satellite object with the highest elevation
	 */
	public Sat mostVisible(Date d){
		Sat sat = null;
		float elevation = 0;
		if (onSight != null){
			for (Iterator<Sat> i = onSight.iterator(); i.hasNext();) {
				Sat s = (Sat)i.next();
				float el = s.getElevation(d); 
				if(el>elevation) {
					
					elevation = el;
					sat = s;
					
				}
			}
			
		}
		return sat;
	}
	/**
	 * sets the location where the tracking takes place.
	 * 
	 */
	public void setLocation(Location loc) {
		 location = loc;
	}
	
	/**
	 * return if the Group is empty or not.
	 * 
	 * @return Boolean
	 */
	public boolean hasSats() {
		 return(onSight.size()>0);
	}
	
	/**
	 * 
	 * Check if a new element is already on the list
	 * 
	 */
	
	public boolean checkDuplicates(TLE t){
		boolean duplicate = false;
		if(tlelist.size() > 0){
			for (Iterator<TLE> i = tlelist.iterator(); i.hasNext();) {
				
				TLE t1 = i.next();
				if(t1.toString().equals(t.toString())){
					duplicate = true;
					PApplet.println("DUPLICATED");
					}else{
						PApplet.println("ADDED");
						}
			}
			
		}
		return duplicate;
	}
	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}
	/**
	 * 
	 * Set the scaling factor  
	 * 
	 **/
	public void setDistanceScalingFactor(float s){
		
		location.setScale(s);
		for (Iterator<Sat> i = satellites.iterator(); i.hasNext();) {
			
			Sat sat = (Sat)i.next();
			sat.setScale(s);
		}
		
	}


}

