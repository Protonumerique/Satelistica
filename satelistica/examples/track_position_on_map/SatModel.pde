class SatModel {

  float theta=0, gamma = 0;
  float innerRadius;
  float outerRadius;
  float amplitude;
  int numSteps=20;
  PVector myPos, direction;

  SatModel(PVector p, int a, int ir, int or) {
    myPos = p;
    amplitude = radians(a);
    innerRadius = ir;
    outerRadius= or;
    direction = new PVector();
  }
  
  public void updatePos(PVector v) {

    myPos = v;
   
  }
  
  public void updateDir(PVector d) {

    direction = d;
  }
  public void display() {
    pushMatrix();
    translate(myPos.x, myPos.y);
    // translate mouse position into polar coordinates
    // in the polar space the vector components are interpreted as:
    // x = radius
    // y = angle
    noStroke();
    fill(255);
    ellipse(0, 0, innerRadius-5, innerRadius-5);
    PVector dir = direction;
    dir.sub(myPos);
    dir = toPolar(dir);
    // ensure we always interpolate the angle over the smaller difference
    if (abs(theta-dir.y)>PI) {
      if (theta>dir.y) {
        theta-=TWO_PI;
      }
      else {
        dir.y-=TWO_PI;
      }
    }
    // interpolate to the new angle, adaptive speed based on current velocity
    theta+=(dir.y-theta)*abs(dir.y-theta)*0.5;
    // avoid "over-spinning"
    theta%=TWO_PI;
    // create the arc as tri strip
    noStroke();
    beginShape(TRIANGLE_STRIP);
    PVector p=new PVector();
    for (float i=0,t=theta-PI*0.25; i<numSteps; i++) {
      // convert theta back into cartesian coordinate space
      // the radius of 1 means the resulting vector will be normalized
      p.set(new PVector(1, t));
      p = toCartesian(p);
      // scale point to inner/outer radius
      vertex(p.x*innerRadius, p.y*innerRadius);
      vertex(p.x*outerRadius, p.y*outerRadius);
      t+=amplitude/numSteps;
    }
    endShape();
   
    fill(#F3FA32);
     p.set(new PVector(1, theta+HALF_PI));
     p = toCartesian(p);
     pushMatrix();
     rectMode(CENTER);
    translate(p.x*innerRadius, p.y*innerRadius);
    rotate(theta);
    rect(0,innerRadius/3, innerRadius*.6, innerRadius);
    //rect(-10, innerRadius*2, 40, 60);
    popMatrix();
    
    p.set(new PVector(1, theta-HALF_PI));
    p = toCartesian(p);
    pushMatrix();
    translate(p.x*innerRadius, p.y*innerRadius);
    rotate(theta - PI);
    rect(0,innerRadius/3, innerRadius*.6, innerRadius);
    //rect(0, innerRadius*3, 40, 60);
    popMatrix();
    
    // draw comparison vector to mouse vs. arc positions
    dir=toCartesian(dir);
    stroke(255, 0, 0);
    //line(0, 0, dir.x, dir.y);
    PVector arcPos= toCartesian(new PVector(innerRadius, theta));
    stroke(0, 0, 255);
    //line(0, 0, arcPos.x, arcPos.y);
    popMatrix();
  }
  public final PVector toCartesian(PVector vec) {
    float xx =  (vec.x * cos(vec.y));
    float y = (vec.x * sin(vec.y));
    float x = xx;

    return new PVector(x, y);
  }
  public final PVector toPolar(PVector vec) {
    float r = sqrt(vec.x * vec.x + vec.y * vec.y);
    float yy = atan2(vec.y, vec.x);
    float xx = r;
    return new PVector(xx, yy);
  }
}

