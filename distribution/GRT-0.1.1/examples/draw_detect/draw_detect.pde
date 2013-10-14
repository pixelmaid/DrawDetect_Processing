import math.*;

import oscP5.*;
import netP5.*;
import controlP5.*;
import grt.library.*;

/**
Draw-Detect alpha

 Author: Jennifer Jacobs
 
 Info: This sketch is a basic drawing detection app that uses the Gesture Recognition toolkit.
 http://www.nickgillian.com/wiki/pmwiki.php/GRT/GUIProcessing

The goal of this system was to explore initial methods of incorporating machine learning into a digital illustration tool. The working system currently classifies end user-created vector line drawings based on a small sample set of user-defined classes. 

Input:
User draw shapes (square, spiral, diamond, squiggle etc.) and plain text class labels corresponding to different categories of shapes.
Output:
Prediction of which class a new user drawn shape belongs to (e.g. squiggle), along with a visualization of the prediction metrics of the system (class likelihood, distance to other classes etc.). Draw-Detect scales the points and edges of each user-defined shape within a target area and translates them to the origin. Following this transformation, Draw-Detect calculates the following features for each shape instance: accumulated edge length (sum of all of the edges), accumulated angle (calculating angles between -180 and 180),accumulated relative angle (calculated after normalizing the length of each edge), accumulated slope, total number of points, distance between the start point and end point, the scaled width, height and area of the shape. Because of the transformation and scaling, the system is invariant to the scale and position, and generally able to recognize similar shapes that differ in size and location. During both training and prediction phases, Draw-Detect also displays a visualization of the features of a shape to the user.

 */


//Set the pipeline mode, the number of inputs and the number of outputs
final int pipelineMode = GRT.CLASSIFICATION_MODE;
final int numInputs = 9;
final int numOutputs = 1;
final int CHANGE = 0;
final int DRAW = 1;
final int DELETE = 4;
final int SPIRAL_LABEL=1;
final int POLYGON_LABEL=2;
final int LINE_LABEL=3;
final int TRAIN=5;
final int TEST=6;

boolean startup = false;
int active =  color(0, 160, 100);
float dZx;
float dZy;
float dZw;
float dZh;
float ox;
float oy;
float ow;
float oh;
float ty=70;
float ey = 225;
float fy = 325;
boolean trained = true;
int mode = DRAW;
int pMode = TRAIN;
float maxLikely=0;
float[]  classDist;
float[] classLikely;
String pClass = "null";
ArrayList<Float> savedExamples = new ArrayList<Float>();
int pClassNum = 0;
Textlabel pClassL;
Textlabel pClassT;
Textlabel maxLikelyL;
Textlabel maxLikelyT;
Textlabel classDistL;
Textlabel classDistT;
Textlabel classLikelyL;
Textlabel classLikelyT;


String modeName = "training tools";
boolean clear = true;
//Create a new GRT instance, this will initalize everything for us and send the setup message to the GRT GUI
GRT grt = new GRT(this, pipelineMode, numInputs, numOutputs, "127.0.0.1", 5000, 5001 );

PointDrawer pd;
ControlP5 cp5;
RadioButton modeToggle;
Button testB;
Button trainB;
Button saveB;
Button clearB;
Button deleteB;
Numberbox labelB;
Textfield textF;

PFont font = loadFont("Helvetica-12.vlw");
//Create some global variables to hold our data
float[] data = new float[ numInputs ];
float[] targetVector = new float[ numOutputs ];
String[] labelNames = new String[100];
int last = 1;
Textlabel[] features = new Textlabel[numInputs];

double[] lastF = new double[numInputs];
float[] bound = new float[numInputs];
float[] lbound = new float[numInputs];

void setup() {
  size(1000, 600);
   grt.setClassifier(grt.MINDIST, true, true, 2.0 );
  grt.clearTrainingDataset();
   for(int i=0;i<100;i++){
   String name = "default_"+(i+1);
   labelNames[i]=name;
  }
   dZx = width/2+10;
  dZy=30;
  dZw = width/2-20;
  dZh =height-40;
  ox=10;
  oy=height*0.7;
  ow =width/2-10;
  oh = height*0.3-10;
  
  lbound[0]=0;
  lbound[1]=-8000;
  lbound[2]=-25;
  lbound[3]=-200;
  lbound[4]=0;
  lbound[5]=0;
  lbound[6]=0;
  lbound[7]=0;
  lbound[8]=0;
  //lbound[9]=dZx;
  //lbound[10]=dZy;
  
  
  bound[0]=1000;
  bound[1]=8000;
  bound[2]=25;
  bound[3]=200;
  bound[4]=500;
  bound[5]=150;
  bound[6]=100;
  bound[7]=100;
  bound[8]=10000;
 // bound[9]=dZx+dZw;
  //bound[10]=dZy+dZh;
  
 
  frameRate(30);

  pd = new PointDrawer(this);
  cp5 = new ControlP5(this);
  cp5.setFont(font, 12);

  cp5.addButton("draw")
    .setValue(0)
      .setPosition(10, ty+30)
        .setSize(60, 20)
          ;

  // and add another 2 buttons
  cp5.addButton("move")
    .setValue(1)
      .setPosition(75, ty+30)
        .setSize(60, 20)
          ;
  // and add another 2 buttons
  cp5.addButton("delete")
    .setValue(2)
      .setPosition(65*2+10, ty+30)
        .setSize(60, 20)
          ;   
  // and add another 2 buttons
  cp5.addButton("clear")
    .setValue(3)
      .setPosition(65*3+10, ty+30)
        .setSize(60, 20)
          ;


  // and add another 2 buttons
  cp5.addButton("generate spiral")
    .setValue(4)
      .setPosition(155*0+10, ty+90)
        .setSize(150, 20)
        .hide()
          ;

  cp5.addButton("generate r polygon")
    .setValue(5)
      .setPosition(155*1+10, ty+90)
        .setSize(150, 20)
        .hide()
          ;
  cp5.addButton("generate line")
    .setValue(5)
      .setPosition(155*2+10, ty+90)
        .setSize(150, 20)
        .hide()
          ;


  trainB = cp5.addButton("train")
    .setValue(6)
      .setPosition(155*0+10, ty+85)
        .setSize(150, 20)
        .hide();
          ;
  
       
 
  saveB = cp5.addButton("save example")
    .setValue(8)
      .setPosition(155*0+10, ty+130)
        .setSize(150, 20)
          ;

  deleteB = cp5.addButton("delete last")
    .setValue(8)
      .setPosition(155*1+10, ty+130)
        .setSize(150, 20)
          ;
          
   clearB = cp5.addButton("clear all data")
    .setValue(8)
      .setPosition(155*2+10, ty+130)
        .setSize(150, 20)
          ;


 labelB = cp5.addNumberbox("label")
   .setValue(9)
      .setPosition(155*0+10, ty+85)
        .setSize(150, 20)
         .setRange(1,100)
           .setDirection(Controller.VERTICAL) // change the control direction to left/right
           .setValue(1)
            .setScrollSensitivity(10)

     ;
     
  textF= cp5.addTextfield("label name")
     .setPosition(155*1+10, ty+85)
     .setSize(200,20)
     .setFocus(true)
     .setText(labelNames[0]);
     
     ;

 

  modeToggle = cp5.addRadioButton("mode")
    .setPosition(10, 40)
      .setSize(40, 20)
        .setColorActive(color(255))
          .setColorLabel(color(255))
            .setItemsPerRow(2)
              .setSpacingColumn(90)
                .addItem("train mode", 1)
                  .addItem("test mode", 2);

  ;
  
  
/*pClassL = cp5.addTextlabel("pClass")
     .setPosition(155*0+10, ty+85)
     .setText("predicted class")
     .hide();
pClassT = cp5.addTextlabel("pClassT")
     .setPosition(155*0+10+100, ty+85)
     .setText("null")
     .setColor(color(255,0,0))
     .hide();
maxLikelyL = cp5.addTextlabel("maxLikely")
     .setPosition(155*1+10, ty+85)
     .setText("max likelyhood")
     .hide();
pClassT = cp5.addTextlabel("pClassT")
     .setPosition(155*0+10+100, ty+85)
     .setText("null")
     .setColor(color(255,0,0))
     .hide(); 
   */  


  modeToggle.activate("train mode");

  startup = true;
}

void draw() {
  background(102);  
  drawCanvas();
  pd.drawPoints();
  if ( !grt.getInitialized() ) {
    background(255, 0, 0);  
    println("WARNING: GRT Not Initalized. You need to call the setup function!");
    return;
  }

 

    
 



  //Grab the mouse data and send it to the GRT backend via OSC
  //data[0] = mouseX;
  //data[1] = mouseY;
  //grt.sendData( data );
  drawOutput();
   if(pMode == TRAIN){
    drawExampleData();
  }
  drawFeatures();
 
}

public void drawFeatures(){
   int x = 10;
   float y = fy;
   float spacer = 20;
 text("total length",x,y);
     y+=spacer;
     
  text("total angle",x,y);
     y+=spacer;
     
     text("relative angle",x,y);
     y+=spacer;

text("total slope",x,y);
     y+=spacer;

text("total points",x,y);
     y+=spacer;
 

text("start-end distance",x,y);
     y+=spacer;
 
text("scaled width",x,y);
     y+=spacer;

text("scaled height",x,y);
     y+=spacer;

text("scaled area",x,y);
     y+=spacer;
  
// text("centroid x",x,y);
  //   y+=spacer;
//text("centroid y",x,y);

 float[] comp = pd.compareStats();
   float[] bar = pd.getNormals();
 
   noStroke();
  for(int i=0;i<numInputs;i++){
     noFill();
                  stroke(40);
                  strokeWeight(1);
                  rect(155,fy+spacer*i-10,200,10);
                  fill(255);
                   noStroke();
    
    if(comp!=null){
     fill(255,comp[i],comp[i]);
    }
    else{
      fill(255);
    }
     rect(155,fy+spacer*i-10,bar[i],10);
  }
    //ellipse(10,fy+175+25*i+5,10,10);
    
}

public void drawExampleData(){
    drawLabel(10, ey, "saved examples");
    int x = 10;
   float y = ey+35;
   float spacer = 20;
   float xspacer = 155;
    for(int i=0; i<savedExamples.size(); i++){

               text(labelNames[i]+" ("+(i+1)+")"+" : "+round(savedExamples.get(i)), x, y);
               if((i+1)%3==0){
                  y += spacer;
                  x =10;
               }
               else{
                  x += xspacer;
               }

              //classLikelihoodsText += "    " + (classLikely[i]>1.0e-5?classLikely[i]:0);
            }
            
      fy=y+45;
          

}

public void drawOutput(){
   
 if(pMode ==TEST){
   int x = 10;
   float y = ty+95;
   float spacer = 20;
     
     
            String type="";
            if(pClassNum!=0){
              type =labelNames[pClassNum-1];
            }
            else{
              type = "Null";
            }

            text("PredictedClassLabel: " + type + " ("+pClassNum+")", x, y);
            y += spacer;
           int m = round(map(maxLikely,0,1,0,100));
            text("Maximum Likelihood (0-100): "+m,x, y);
            float mw = map(maxLikely,0,1,1,150);
             noFill();
            strokeWeight(1);
            stroke(40);
            rect(x+180,y-10,150,10);
            
            if(pd.points.size()>1){
          
            fill(255);
            noStroke();
            rect(x+180,y-10,mw,10);
            }
            y += spacer;
          if(classLikely!=null){
           text("Class Likelihoods ((0-100): ",x, y);
           y += spacer;

            for(int i=0; i<classLikely.length; i++){
                         int c = round(map(classLikely[i],0,1,0,100));

               text(labelNames[i]+": "+c, x, y);
                float cw = map(classLikely[i],0,1,1,150);
               noFill();
                  stroke(40);
                  strokeWeight(1);
                  rect(x+180,y-10,150,10);
                  fill(255);
                   noStroke();
               if(pd.points.size()>1){
                 
                 rect(x+180,y-10,cw,10);
               }
                y += spacer;

              //classLikelihoodsText += "    " + (classLikely[i]>1.0e-5?classLikely[i]:0);
            }
          
            
             text("Class Distances (0-100+): ",x, y);
           y += spacer;

            for(int i=0; i<classDist.length; i++){
                int d = round(map(classDist[i],0,1,0,100));
               text(labelNames[i]+": "+d, x, y);
                float dw = map(classDist[i],0,1,1,150);
                if(dw>150){
                  dw = 150;
                }
                 noFill();
                  stroke(40);
                  strokeWeight(1);
                  rect(x+180,y-10,150,10);
                  fill(255);
                   noStroke();
               if(pd.points.size()>1){
                 rect(x+180,y-10,dw,10);
               }
                y += spacer;

              //classLikelihoodsText += "    " + (classLikely[i]>1.0e-5?classLikely[i]:0);
            }
           fy=y+25;
          }
  }
  
  
    
}

public void drawCanvas() {
  noStroke();
  //draw canvas
  fill(234);
  rect(dZx, dZy, dZw, dZh);

  //draw output window
 // fill(255);
  //rect(ox, oy, ow, oh);

 // drawLabel(ox, oy-20, "console");
  drawLabel(dZx, dZy-20, "canvas");
  drawLabel(10, 10, "mode");
  drawLabel(10, ty, "drawing tools");
  drawLabel(10, ty+60, modeName);
  drawLabel(10, fy-35, "features");
 // drawLabel(240, ty+160, "predicteds");

}

public void drawLabel(float x, float y, String text) {
  fill(40); 
 noStroke();
  rect(x, y, 110, 20);

  fill(234);
  textFont(font, 12);
  text(text, x+5, y+13);
}

public void controlEvent(ControlEvent theEvent) {
  if (startup) {
     clear = false;
     labelNames[Math.round(last)-1] = textF.getText();
    if (theEvent.isFrom(modeToggle)) {

      if (theEvent.getValue()==2) {
       if(savedExamples.size()>0){
        grt.startTraining();
       }
        pMode = TEST;
        modeName = "prediction";
        saveB.hide();
        trainB.hide();
        labelB.hide();
        textF.hide();
       // testB.show();
        deleteB.hide();
        clearB.hide();
      //  pClassL.show();
       // pClassT.show();
 /*
maxLikelyL.show();
maxLikelyT.show();
classDistL.show();
classDistT.show();
classLikelyL.show();
classLikelyT.show();*/

      }
      else {
        pMode = TRAIN;
        modeName = "training tools";
        saveB.show();
        labelB.show();
        textF.show();
       // testB.hide();
        trainB.show();
        deleteB.show();
        clearB.show();
     /*   pClassL.hide();
        pClassT.hide();
        maxLikelyL.hide();
maxLikelyT.hide();
classDistL.hide();
classDistT.hide();
classLikelyL.hide();
classLikelyT.hide();
*/


      }
      // myColorBackground = color(int(theEvent.group().value()*50),0,0);
    }
    else if(theEvent.isFrom(textF)) {
    
  }
    else if(theEvent.isFrom(labelB)){
          
          grt.setTrainingClassLabel(Math.round(labelB.getValue()));
          textF.setText(labelNames[Math.round(labelB.getValue())-1]);
          last = Math.round(labelB.getValue());

    }
    else {
       

      String e =theEvent.getController().getName();
      if (e=="draw") {
        mode = DRAW;
      }
      if (e=="move") {
        mode = CHANGE;
      }
      if (e=="delete") {
        mode = DELETE;
      }
      if (e=="clear") {
        pd.clearPoints();
        clear = true;
      }
      

  
      if (e=="save example") {
        if (pd.points.size()>=2) {
          pd.saveExample();
        }
      } 
   
     if (e=="clear all data") {
        grt.clearTrainingDataset();
      }    
     if (e=="predict") {
        //if (pd.points.size()>=2) {
        //  grt.sendData(pd.getStats());
       // }
      }

      if (e=="train") {
       // grt.startTraining();
       // trained = true;
      }
    }
  }
}



void mousePressed() {
  if (inDrawZone()) {
    if (mode == DRAW) {
      pd.addPoint(mouseX, mouseY);
    }
    if (mode == CHANGE) {
      pd.pointSelect(mouseX, mouseY);
    }
    if (mode ==DELETE) {
      pd.deletePoint(mouseX, mouseY);
    }
  }
}

boolean inDrawZone() {
  if ((mouseX>dZx && mouseX<dZx+dZw)&&(mouseY>dZy && mouseY<dZy+dZh)) {
    return true;
  }
  return false;
}

void mouseDragged() {
  if (inDrawZone()) {  
    if (mode == CHANGE) {

      pd.movePoint(mouseX, mouseY);
    }
     if (mode == DRAW) {
      pd.addPoint(mouseX, mouseY);
    }
   if( pMode == TEST) { 
   if (pd.points.size()>=2) {
          grt.sendData(pd.getStats());
           println("sent data");
           pClassNum = grt.getPredictedClassLabel();
           maxLikely = (float)grt.getMaximumLikelihood();
           classLikely = grt.getClassLikelihoods();
           classDist = grt.getClassDistances();
           println(classDist);
        }
   }
  }
}

void mouseReleased() {
  pd.pointDeselect();
   
}

void keyPressed() {

  switch( keyCode) {
  case 38:  
   labelB.setValue( labelB.getValue()-1);
    break;
   case 40:
     labelB.setValue( labelB.getValue()+1);

  }
}

