public void drawMap() {
  map.draw();
  Date d = new Date();
  long time = millis();
  ScreenPosition locPos = map.getScreenPosition(mapLocation);
  strokeWeight(2);
  fill(255, 20);
  stroke(#8A8A8B);
  pushMatrix();  
  translate(locPos.x, locPos.y);
  ellipse(0, 0, 100, 100);
  fill(255, 30);
  stroke(#D8D8D8);
  ellipse(0, 0, 50, 50);
  fill(255);
  ellipse(0, 0, 2, 2);
  textSize(24);
  text(loc.getName(), 10, 0);
  popMatrix();
  // Fixed-size marker
  if (sats.hasSats()) {

    if (!defined)chosenSat = (Sat)sats.mostVisible(new Date());
    printSats(chosenSat, locPos);
    float azimuth = chosenSat.getAzimuth(d);
    float elevation = chosenSat.getElevation(d);
    sats.track();
  }

  if (time - previousTime > interval*5) {//Renew Sat list every 5 sec
    previousTime = time;
    //satMarkers = sats.getPassingSats(d);
    satMarkers = sats.getSats();
    println("UPDATING...");
  }
}

/*
Use unfolding markers to print the found Satellites
*/

void printSats(Sat hsat, ScreenPosition locPos) {

  de.fhpotsdam.unfolding.geo.Location sm = new de.fhpotsdam.unfolding.geo.Location(hsat.getCoordinates().y, hsat.getCoordinates().x);
  ScreenPosition sPos = map.getScreenPosition(sm);
  stroke(#7AFC0A);
  line(locPos.x, locPos.y, sPos.x, sPos.y);
  //
  //noStroke();
  displayOrbit(hsat);
  SatModel body = new SatModel(new PVector(sPos.x, sPos.y), 90, 15, 20);
  //body.updatePos(new PVector(x, y));
  body.updateDir(new PVector(locPos.x, locPos.y));
  body.display();

  //ellipse(x, y, 5, 5);
  fill(255);
  if (overCircle(sPos.x, sPos.y, 20)) {
    fill(255);
    text(hsat.getName(), sPos.x+10, sPos.y);
  }

  if (passingSats.size() > 1) {
    for (Iterator<Sat> i = passingSats.iterator(); i.hasNext();) {
      Sat sat = (Sat)i.next();
      if (hsat != sat) {

        sm = new de.fhpotsdam.unfolding.geo.Location(sat.getCoordinates().y, sat.getCoordinates().x);
        sPos = map.getScreenPosition(sm);
        strokeWeight(2);
        stroke(67, 211, 227, 100);
        body = new SatModel(new PVector(sPos.x, sPos.y), 90, 10, 15);
        //body.updatePos(new PVector(x, y));
        body.updateDir(new PVector(locPos.x, locPos.y));
        body.display();
        //ellipse(sPos.x, sPos.y, 12, 12);

        //ellipse(x, y, 5, 5);
        if (overCircle(sPos.x, sPos.y, 15)) {
          fill(255);
          text(sat.getName(), sPos.x+10, sPos.y);
          if (mousePressed)defined = true; 
          chosenSat = sat;
        }
      }
    }
  }
}

/*
Display orbit of chosen Object
 */

void displayOrbit(Sat sat) {
  predictor.setSat(sat);
  ArrayList <PVector> orbit  = predictor.getOrbitAsCoordinates(new Date(), width/2, 60, 60);
  noFill();
  stroke(200, 100);
  strokeWeight(3);
  beginShape(LINES);
  for (Iterator<PVector> i = orbit.iterator(); i.hasNext();) {
    PVector v = (PVector)i.next();
    de.fhpotsdam.unfolding.geo.Location sm = new de.fhpotsdam.unfolding.geo.Location(v.y, v.x);
    ScreenPosition sPos = map.getScreenPosition(sm);
    float ox = sPos.x;//map(v.x, -180, 180, 0, width);
    float oy = sPos.y;//map(v.y, 90, -90, 0, height);

    vertex (ox, oy);
  }
  endShape();
}

