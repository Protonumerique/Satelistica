/**
 * Satellite group uses a Predictor object to predict passes during the next hour, of the "most visible" satellites over your location.
 * a pass is defined by the time a satellite gains height over the horizon and the time it disappears again.
 */
/* 
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
 */


import org.json.*; //Try to geolocate the specified location. Json is needed to parse the response. If it fails or the library isn't installed the location will default to Berlin, Germany.

import satelistica.*;

Location loc;

SatelliteGroup sats;
Predictor predictor;

int count = 0;
String url = "http://celestrak.com/NORAD/elements/visual.txt";

void setup() {
  size(400, 400);
  smooth();

  sats = new SatelliteGroup(this, url);//Initialize the satellite group with a TLE file from celestrak.com
  sats.printNames();
  loc = new Location(this, "Bogota");//Set the location to one of your preference, if it defaults to berlin, check if jason4processing is in your libraries, or change the spelling
  while (count < sats.getSats().size()) {
    predictor = new Predictor(loc, sats.getSats().get(count)); //Create a new predictor for each satellite on the list
    println("\nPASSES OVER "+ loc.getName()+" FOR "+sats.getSats().get(count).getName()+"\n");//
    String [] passes = predictor.getPassesAsString(new Date(), 1);//Get the passes within the next hour
    for (int i = 0; i < passes.length; i++)println(passes[i]+"\n");
    count ++;
  }
}

