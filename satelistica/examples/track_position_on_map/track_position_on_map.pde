import controlP5.*;

import org.json.*;

import satelistica.*;
import java.util.*;

/**********A Location, a Satellite group and a predictor************/
Location loc;
SatelliteGroup sats;
Predictor predictor;
PVector position;
PVector locPos;

/*******Initial track values********/
String filepath = "http://celestrak.com/NORAD/elements/";//
String altFilePath = "http://www.tle.info/data/";///In case celestrak is down, but it gets all cranky and there are misterious orbital behaviours
String []filenames= {
  filepath +"visual", filepath+"supplemental/gps", filepath +"military", filepath+"stations", filepath+"tle-new", "classfd", altFilePath + "music"//"gps-ops", filepath +"military", filepath+"stations"
};
String filename = filenames[0];
ArrayList<Sat> passing;
float lastA = 0, lastE=0;
long interval = 500, previousTime = 0;


PFont font ;
PImage mundo;


ControlP5 cp5;

String textValue = "";

Textfield myTextfield;


void setup() {
  size(840, 420);
  smooth();
  try {
    loc = new Location(this, "Bogota");
    sats = new SatelliteGroup(this, filename +".txt", loc);
  } 
  catch (Exception e) {
    sats = new SatelliteGroup(this, filename + ".txt");
  }
  locPos = new PVector();
  sats.printNames();
  passing = sats.getPassingSats(new Date());
  predictor = new Predictor(loc, sats.mostVisible(new Date()));

  String[] fontList = PFont.list();
  font = createFont(fontList[0], 32);
  mundo = loadImage("earthDarker.jpg");
}



void draw() {

  background(#151515);
  image(mundo, 0, 0);
  //tint(20);
  Date d = new Date();
  long time = millis();
  fill(255, 20);
  stroke(#8A8A8B);
  PVector lp = loc.getCoordinates();
  float px = map(lp.x, -180, 180, 0, width);
  float py = map(lp.y, 90, -90, 0, height);
  pushMatrix();  
  translate(px, py);
  ellipse(0, 0, 100, 100);
  fill(255, 30);
  stroke(#D8D8D8);
  ellipse(0, 0, 50, 50);
  fill(255);
  ellipse(0, 0, 2, 2);
  textSize(14);
  text(loc.getName(), 10, 0);
  popMatrix();

  if (sats.hasSats()) {// Si hay satélites pasando en la lista, haga algo. 
    sats.track();
    Sat sat = (Sat)sats.mostVisible(d);
    //sat.updateCoordinates(d);
    position = sat.getCoordinates();
    float azimuth = sat.getAzimuth(d);
    float elevation = sat.getElevation(d);
    printSats(sat);/// Una forma rápida y arbitraria de dar algun output visual... En un par de días hay que pulirlo mucho más. El punto rosado es el objeto con más elevación.

  }
  else {
    println("no Sats");
  }

  if (time - previousTime > interval*5) {//Renew Sat list every 5 sec
    previousTime = time;
    passing = sats.getPassingSats(d);
  }
}



void mousePressed() {//El hacer click fuerza una actualización 
  Date d = new Date();
  passing = sats.getPassingSats(d);
}

void printSats(Sat sat) {
  PVector lp = loc.getCoordinates();
  float px = map(lp.x, -180, 180, 0, width);
  float py = map(lp.y, 90, -90, 0, height);
  PVector pos = sat.getCoordinates();
  stroke(#FF79D0);
  //noStroke();
  displayOrbit(sat);
  float x = map(pos.x, -180, 180, 0, width);
  float y = map(pos.y, 90, -90, 0, height);
  SatModel body = new SatModel(new PVector(x, y), 90, 10, 15);
  //body.updatePos(new PVector(x, y));
  body.updateDir(new PVector(px, py));
  body.display();
  //ellipse(x, y, 5, 5);
  fill(255);
  if (overCircle(x, y, 5)) {
    fill(255);
    text(sat.getName(), x+10, y);
  }

  if (passing.size() > 1) {
    for (Iterator<Sat> i = passing.iterator(); i.hasNext();) {
      Sat psat = (Sat)i.next();
      if (sat != psat) {

        pos = psat.getCoordinates();
        x = map(pos.x, -180, 180, 0, width);
        y = map(pos.y, 90, -90, 0, height);
        fill(#FAC165);
        noStroke();
        body = new SatModel(new PVector(x, y), 90, 8, 12);
        //body.updatePos(new PVector(x, y));
        body.updateDir(new PVector(px, py));
        body.display();
        //ellipse(x, y, 5, 5);
        if (overCircle(x, y, 5)) {
          fill(255);
          text(psat.getName(), x+10, y);
        }
      }
    }
  }
}

void displayOrbit(Sat sat) {
  predictor.setSat(sat);
  ArrayList <PVector> orbit  = predictor.getOrbitAsCoordinates(new Date(), width/2, 60, 60);
  noFill();
  beginShape();
  for (Iterator<PVector> i = orbit.iterator(); i.hasNext();) {
    PVector v = (PVector)i.next();
    float ox = map(v.x, -180, 180, 0, width);
    float oy = map(v.y, 90, -90, 0, height);
    if (ox>=width|| ox<= 0) {
      vertex (ox+5, -100);
      vertex(-5, -100);
      vertex(-5, oy);
    }
    else if (ox<width|| ox> 0) {
      vertex (ox, oy);
    }
  }
  endShape();
}

/********MOUSE functions**********/
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

