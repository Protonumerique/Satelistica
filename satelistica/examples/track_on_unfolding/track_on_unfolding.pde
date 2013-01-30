/**
 * This code visualizes the position of passing sats over Bogotá, Colombia with the help of the Unfolding library - http://unfoldingmaps.org/. 
 * Change the city to whatever you want, Geocoding is done by cloudmade, if it doesn't find it, try finding the lat/lon yourself and use a different constructor.
 * You don't need to import the Json4Processing lib in this example because Unfolding contains it already.
 *
 * 
 * 2012 Luis Bustamante
 * 
 * This demo & library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * http://creativecommons.org/licenses/LGPL/2.1/
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 **/

import satelistica.*;
import java.util.*;

/*********
 Unfolding imports
 You can use custom MBtiles made with TileMill
 **********/
import processing.opengl.*;
import codeanticode.glgraphics.*;
import de.fhpotsdam.unfolding.mapdisplay.*;
import de.fhpotsdam.unfolding.utils.*;
import de.fhpotsdam.unfolding.marker.*;
import de.fhpotsdam.unfolding.tiles.*;
import de.fhpotsdam.unfolding.interactions.*;
import de.fhpotsdam.unfolding.ui.*;
import de.fhpotsdam.unfolding.*;
import de.fhpotsdam.unfolding.core.*;
import de.fhpotsdam.unfolding.data.*;
import de.fhpotsdam.unfolding.geo.*;
import de.fhpotsdam.unfolding.texture.*;
import de.fhpotsdam.unfolding.events.*;
import de.fhpotsdam.utils.*;
import de.fhpotsdam.unfolding.providers.*;

UnfoldingMap map;

satelistica.Location loc;//A Location object defines the observer's position on Earth
SatelliteGroup sats;//A satellite group to predict their loctions at a given time
Predictor predictor;//An object that calculates the position of satellites at ani given time ( a Date object)
Sat chosenSat; //By default, the Satellite with the highest elevation over our location, or the one we click on...

long interval = 500, previousTime = 0;


String filepath = "http://celestrak.com/NORAD/elements/";//A pretty good and reliable source of updated TLE sets 
String altFilePath = "http://www.tle.info/data/";///In case celestrak is down, but sometimes it gets all cranky and there are misterious orbital behaviours
String []filenames= {
  filepath +"visual", filepath+"supplemental/gps", filepath +"military", filepath+"stations", altFilePath + "music"
};
int nList=0;
;

String filename = filenames[0];
ArrayList satMarkers, passingSats;


de.fhpotsdam.unfolding.geo.Location mapLocation;
SimplePointMarker locMarker;

PFont font ;
boolean defined = false;

void setup() {
  size(1200, 720, GLConstants.GLGRAPHICS);
  smooth();
  //Uncomment this if you want to use mbtiles
  /*
  String mbTilesConnectionString = "jdbc:sqlite:";
  mbTilesConnectionString += sketchPath("data/satelistica.mbtiles");//You can made your own tiles using Tilemill or a similar service

  map = new UnfoldingMap(this, new MBTilesMapProvider(mbTilesConnectionString));
  map.setZoomRange(2, 8);
  MapUtils.createDefaultEventDispatcher(this, map);
  */
  //comment this part fpr mbtiles
  map = new de.fhpotsdam.unfolding.Map(this, 
  	new OpenStreetMap.CloudmadeProvider(MapDisplayFactory.OSM_API_KEY, 30635));
  map.setTweening(true);
  map.zoomToLevel(3);
  map.panTo(new de.fhpotsdam.unfolding.geo.Location(40f, 8f));
  MapUtils.createDefaultEventDispatcher(this, map);
  //
  
  try {
    loc = new satelistica.Location(this, "Bogota");
    sats = new SatelliteGroup(this, filename +".txt", loc);
    satMarkers = sats.getSats();
    passingSats = sats.getPassingSats(new Date());
    mapLocation = new de.fhpotsdam.unfolding.geo.Location(loc.getCoordinates().y, loc.getCoordinates().x);
    println(loc.getCoordinates().x+","+loc.getCoordinates().y);
    locMarker =new SimplePointMarker(mapLocation);
  } 
  catch (Exception e) {
    println("ALGO FALLÓ");
    loc = new satelistica.Location();
    sats = new SatelliteGroup(this, filename + ".txt");
    satMarkers = sats.getSats();
    passingSats = sats.getPassingSats(new Date());
    mapLocation = new de.fhpotsdam.unfolding.geo.Location(new satelistica.Location().getCoordinates().y, new satelistica.Location().getCoordinates().x);
    locMarker = new SimplePointMarker(mapLocation);
  }
  chosenSat =(Sat)sats.mostVisible(new Date());
  predictor = new Predictor(loc, chosenSat);

  map.setTweening(true);
  map.zoomToLevel(4);
  map.panTo(new de.fhpotsdam.unfolding.geo.Location(mapLocation.x, mapLocation.y));

  //***TEXT***//
  String[] fontList = PFont.list();
  font = createFont(fontList[0], 32);
}

void draw() {
  drawMap();
}



/********MOUSE functions*********/
boolean overCircle(float x, float y, int diameter) {
  float disX = x - mouseX;
  float disY = y - mouseY;
  if (sqrt(sq(disX) + sq(disY)) < diameter/2 ) {
    return true;
  } 
  else {
    return false;
  }
}

void keyPressed() {

  
  if (key==ENTER)satMarkers = sats.getSats();//Force an update of passing satellites.

  
}

void mouseReleased() {
  defined=false;
}

