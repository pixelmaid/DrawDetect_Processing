class PointDrawer {
  ArrayList <Point> points;
  Point selected = null;
  float limit = 10;
  Point centroid = null;
  float w = 0;
  float h = 0;
  double [] stats;
  PApplet parent;
  PointDrawer(PApplet p) {
    clearPoints();
    parent = p;
  } 

  void addPoint(float x, float y) {
    Point p = new Point(x, y);
    points.add(p);
    if (points.size()>=2) {
      stats = getStats();

      for (int i=0;i<9;i++) {
        //features[i].setText(Double.toString(Math.round(stats[i])));
      }
      //features[9].setText(Double.toString(Math.round(stats[9]-dZx))+","+Double.toString(Math.round(stats[10]-dZy)));
    }
  }


  void addPoint(Point p) {

    points.add(p);
    getStats();

    for (int i=0;i<9;i++) {
      features[i].setText(Double.toString(stats[i]));
    }
  }

  void clearPoints() {
    
    if (stats!=null) {
      for (int i=0;i<9;i++) {
        lastF[i]=stats[i];
      }
      //lastF[9]= centroid.getX();
      //lastF[10]=centroid.getY();
     
    }
    points = new ArrayList<Point>();
    centroid = null;
    w=0;
    h=0;
    maxLikely=0;
  }

  float[] compareStats() {
if(stats!=null){
    float[] diffList = new float[numInputs];

    for (int i=0;i<numInputs;i++) {
      float cM = map((float)stats[i], lbound[i], bound[i], 0, 100);
     float lM = map((float)lastF[i], lbound[i], bound[i], 0, 100);
     float diff = abs(cM-lM);
    //println("diff="+diff);
      float mappedDiff = map(diff, 0,100, 255, 0);
      diffList[i]=mappedDiff;
         // println("currentStat "+i+"="+stats[i]+" || "+"lastStat "+i+"="+lastF[i]+" || "+ "mappedDiff "+i+"="+mappedDiff);
    }
    
   
         // println("=======================\n");

    return diffList;
}
return null;
  }

  float[] getNormals() {

    float[] normals = new float[numInputs];

    for (int i=0;i<numInputs;i++) {
      if(points.size()>0&&stats!=null){
      float cM = map((float)stats[i], lbound[i], bound[i], 0,200);
      if(cM<0){
        cM=0;
       }
      if(cM<=200){
        
        normals[i]=cM;
      }
      else{
        normals[i]=200;
      }
       
      }
      else{
        normals[i]=0;
    }
    
    }

    return normals;

  }
  void drawPoints() {
    for (int i=0;i<points.size();i++) {
      if (i>0) {
        strokeWeight(1);
        stroke(41);
        line((float)points.get(i).x, (float)points.get(i).y, (float)points.get(i-1).x, (float)points.get(i-1).y);
      }
      strokeWeight(5);
      stroke(255, 0, 0);
      point((float)points.get(i).x, (float)points.get(i).y);
    }
    if (centroid!=null) {
      strokeWeight(8);
      stroke(0, 255, 0);
      point((float)centroid.x, (float)centroid.y);
    }
    if (w!=0) {
      strokeWeight(1);
      stroke(0, 255, 0);
      noFill();
      //rect((float)centroid.x-w/2, (float)centroid.y-h/2, w, h);
    }
  }

  void movePoint(float x, float y) {
    if (selected!=null) {
      selected.setX(x);
      selected.setY(y);
    }
  }

  void pointDeselect() {
    selected = null;
  }

  void pointSelect(float x, float y) {
    for (int i=0;i<points.size();i++) {
      float pX = (float)points.get(i).x;
      float pY = (float)points.get(i).y;
      if (abs(pX-x)<limit && abs(pY-y)<limit) {
        selected = points.get(i);
        break;
      }
    }
  }

  void deletePoint(float x, float y) {
    for (int i=0;i<points.size();i++) {
      float pX = (float)points.get(i).x;
      float pY = (float)points.get(i).y;
      if (abs(pX-x)<limit && abs(pY-y)<limit) {
        points.remove(i);
        break;
      }
    }
  }



  void scalePoints() {
  }

  boolean saveExample() {
    int label = (Math.round(labelB.getValue()));
    grt.setTrainingClassLabel(label);

    stats = getStats();
    grt.startRecording();
    try {
      Thread.sleep(200);
    } 
    catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    grt.sendData(stats);
    try {
      Thread.sleep(200);
    } 
    catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }    
    grt.stopRecording();
    try {
      Thread.sleep(200);
    } 
    catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
   if(savedExamples.size()<label){
     savedExamples.add(1.0);  
   }
   else{
     savedExamples.set(label-1,savedExamples.get(label-1)+1.0);
   }
   
    return true;
  }

  double [] getStats() {
    stats = Geom.stats(points);
    /*System.out.println("\n=========FEATURE SET=============");
     System.out.println("accumulated length = "+stats[0]);
     System.out.println("accumulated angle = "+stats[1]);
     System.out.println("accumulated relative angle = "+stats[2]);
     System.out.println("accumulated slope = "+stats[3]);
     System.out.println("num points = "+stats[4]);
     System.out.println("distance from start to end = "+stats[5]);
     System.out.println("width = "+stats[6]);
     System.out.println("height = "+stats[7]);
     System.out.println("area = "+stats[8]);
     System.out.println("=========FEATURE SET=============\n");*/

//centroid = new Point(stats[9], stats[10]);
    w = (float)Geom.getWidth(points);
    h = (float)Geom.getHeight(points);
    double [] nStats = new double[9];
    for(int i=0;i<9;i++){
      nStats[i] = stats[i];
    }
    //return stats;
    return nStats;
  }
}

