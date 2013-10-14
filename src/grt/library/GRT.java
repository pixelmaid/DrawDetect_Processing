package grt.library;

import processing.core.*;
import oscP5.*;
import netP5.*;

/**
 * This is a template class and can be used to start a new processing library or tool.
 * Make sure you rename this class as well as the name of the example package 'template' 
 * to your own lobrary or tool naming convention.
 * 
 * @example Hello 
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

public class GRT {
	
	// myParent is a reference to the parent sketch
	PApplet myParent;
	private OscP5 oscP5;
	private NetAddress grtBackend;
	PFont font;
	private boolean initialized;
	private boolean record;
	private int pipelineMode;
	  private int numDimensions;
	  private int targetVectorSize;
	  private int trainingClassLabel;
	  private int predictedClassLabel;
	   
	  private float maximumLikelihood;
	  private float[] preProcessedData;
	  private float[] featureExtractionData;
	  private float[] classLikelihoods;
	  private float[] classDistances;
	  private float[] regressionData;
	  private float[] targetVector;
	  private int[] classLabels;
	  
	  public static final int CLASSIFICATION_MODE = 0;
	  public static final int REGRESSION_MODE = 1;
	  public static final int TIMESERIES_MODE = 2;
	  //Classifier Types
	  public static final int ANBC = 0;
	  public static final int ADABOOST = 1;
	  public static final int GMM = 2;
	  public static final int KNN = 3;
	  public static final int MINDIST = 4;
	  public static final int SOFTMAX = 5;
	  public static final int SVM = 6;
	  
	
	public final static String VERSION = "##version##";
	

	/**
	 * a Constructor, usually called in the setup() method in your sketch to
	 * initialize and start the library.
	 * 
	 * @example Hello
	 * @param theParent
	 */
	public GRT(PApplet theParent) {
		 initialized = false;
		 myParent = theParent;
		 font = myParent.loadFont("Helvetica-12.vlw");
	}
	
	
	 /**
	   Main constructor used to initialize the instance.
	   
	   @param int pipelineMode: sets the mode that the pipeline will run in. This should be a valid pipeline mode, either CLASSIFICATION_MODE, REGRESSION_MODE, or TIMESERIES_MODE
	   @param int numInputs: sets the size of your data vector, this is the data that you will send to the GRT GUI
	   @param int numOutputs: this parameter is only used for REGRESSION_MODE, in which case it sets the target vector size
	   @param String grtIPAddress: the IP address of the machine running the GRT GUI. If it is running on the same machine as this Processing Sketch this should be "127.0.0.1"
	   @param int grtPort: the network port that the GRT GUI is listening for connections on. This is set by the OSC Receive Port setting in the GRT GUI
	   @param int listenerPort: the network port that this Processing Sketch should listen for OSC messages from the GRT GUI
	  */
	  public GRT(PApplet theParent, int pipelineMode,int numInputs,int numOutputs,String grtIPAddress,int grtPort,int listenerPort){
	    initialized = init( pipelineMode, numInputs, numOutputs, grtIPAddress, grtPort, listenerPort );
	    	myParent = theParent;
			 font = myParent.loadFont("Helvetica-12.vlw");
	  }
	  
	  /**
	   This function initalizes the GRT backend. It will set up any memory required for running the GRT backend and will then send a Setup message to the GRT GUI via OSC.
	   
	   @param int pipelineMode: sets the mode that the pipeline will run in. This should be a valid pipeline mode, either CLASSIFICATION_MODE, REGRESSION_MODE, or TIMESERIES_MODE
	   @param int numInputs: sets the size of your data vector, this is the data that you will send to the GRT GUI
	   @param int numOutputs: this parameter is only used for REGRESSION_MODE, in which case it sets the target vector size
	   @param String grtIPAddress: the IP address of the machine running the GRT GUI. If it is running on the same machine as this Processing Sketch this should be "127.0.0.1"
	   @param int grtPort: the network port that the GRT GUI is listening for connections on. This is set by the OSC Receive Port setting in the GRT GUI
	   @param int listenerPort: the network port that this Processing Sketch should listen for OSC messages from the GRT GUI
	   @return returns true if the initalization was successful, false otherwise
	  */
	  public boolean init(int pipelineMode,int numInputs,int numOutputs,String grtIPAddress,int grtPort,int listenerPort){
	    
	    initialized = false;
	    
	    if( pipelineMode != CLASSIFICATION_MODE && pipelineMode != REGRESSION_MODE && pipelineMode != TIMESERIES_MODE ){
	      return false;
	    }
	    
	    this.pipelineMode = pipelineMode;
	    this.numDimensions = numInputs;
	    
	    if( pipelineMode == REGRESSION_MODE ) targetVectorSize = numOutputs;
	    else targetVectorSize = 0;
	    
	    //Init the prediction data, this will be resized when any new prediction data is received
	    preProcessedData = new float[ numDimensions ];
	    featureExtractionData = new float[ 1 ]; 
	    classLikelihoods = new float[ 1 ];
	    classDistances = new float[ 1];
	    regressionData = new float[ targetVectorSize ];
	    targetVector = new float[ targetVectorSize ];
	    classLabels = new int[ 1 ];
	    
	    trainingClassLabel = 1;
	    record = false;
	    predictedClassLabel = 0;
	    maximumLikelihood = 0;
	    System.out.println("setting up osc");
	    //Setup the I/O networks
	    oscP5 = new OscP5(this,listenerPort);
	    System.out.println("set up osc success");
	    grtBackend = new NetAddress(grtIPAddress,grtPort);
	  
	    //Send the setup message
	    OscMessage msg = new OscMessage("/Setup");
	    msg.add( pipelineMode );
	    msg.add( numInputs );
	    msg.add( numOutputs );
	    oscP5.send(msg, grtBackend); 
	    
	    initialized = true;
	    
	    return true;
	  }
	  
	  /**
	   This function sends the data to the GRT GUI. The size of the data vector must match the numDimensions parameter.
	   
	   You need to initalize the GRT backend before you use this function.
	   
	   @param float[] data: the data you want to send to the GRT GUI
	   @return returns true if the data was sent successful, false otherwise
	  */
	 public boolean sendData(double[] data){
	    
	    if( !initialized ) return false;
	    
	    if( data.length != numDimensions ) return false;
	      
	    OscMessage msg = new OscMessage("/Data");
	      
	    for(int i=0; i<numDimensions; i++){
	      msg.add( data[i] );
	    }
	    oscP5.send(msg, grtBackend); 
	      
	    return true;
	  }
	  
	  /**
	   This function sends the target data to the GRT GUI. The size of the target vector must match the targetVectorSize parameter.
	   
	   This function is only useful if you have the GRT in REGRESSION_MODE.
	   You need to initalize the GRT backend before you use this function.
	   
	   @param float[] targetVector: the targetVector you want to send to the GRT GUI
	   @return returns true if the targetVector was sent successful, false otherwise
	  */
	 public boolean sendTargetVector(float[] targetVector){
	    
	    if( !initialized ) return false;
	    
	    if( targetVector.length != targetVectorSize ) return false;
	    
	    OscMessage msg = new OscMessage("/TargetVector");
	    for(int i=0; i<targetVectorSize; i++){
	      msg.add( targetVector[i] );
	    }
	    oscP5.send(msg, grtBackend); 
	    
	    //Save the target vector so we can draw it
	    this.targetVector = targetVector;
	    
	    return true;
	  }

	 
	 /**
	   This function sets the current training class label and then sends the updated training class label to the GRT GUI.
	   
	   This function is only useful if you have the GRT in CLASSIFICATION_MODE or TIMESERIES_MODE.
	   You need to initalize the GRT backend before you use this function.
	   
	   @return returns true if the targetVector was sent successful, false otherwise
	  */
	  public boolean setTrainingClassLabel(int label){
	    
	    if( !initialized ) return false;
	    
	    trainingClassLabel = label;
	    
	    OscMessage msg = new OscMessage("/TrainingClassLabel");
	    msg.add( trainingClassLabel );
	    oscP5.send(msg, grtBackend); 
	      
	    return true;
	  }
	  
	 
	  /**
	   This function increments the current training class label and then sends the updated training class label to the GRT GUI.
	   
	   This function is only useful if you have the GRT in CLASSIFICATION_MODE or TIMESERIES_MODE.
	   You need to initalize the GRT backend before you use this function.
	   
	   @return returns true if the targetVector was sent successful, false otherwise
	  */
	  public boolean incrementTrainingClassLabel(){
	    
	    if( !initialized ) return false;
	    
	    trainingClassLabel++;
	    
	    OscMessage msg = new OscMessage("/TrainingClassLabel");
	    msg.add( trainingClassLabel );
	    oscP5.send(msg, grtBackend); 
	      
	    return true;
	  }
	  
	  /**
	   This function decrements the current training class label and then sends the updated training class label to the GRT GUI.
	   
	   The training class label can not be less than 1.
	   
	   This function is only useful if you have the GRT in CLASSIFICATION_MODE or TIMESERIES_MODE.
	   You need to initalize the GRT backend before you use this function.
	   
	   @return returns true if the targetVector was sent successful, false otherwise
	  */
	  public boolean decrementTrainingClassLabel(){
	    
	    if( !initialized ) return false;
	    
	    if( trainingClassLabel <= 1 ) return false;
	    trainingClassLabel--;
	    
	    OscMessage msg = new OscMessage("/TrainingClassLabel");
	    msg.add( trainingClassLabel );
	    oscP5.send(msg, grtBackend); 
	      
	    return true;
	  }
	  
	  /**
	   This function sends a start recording message to the GRT GUI.
	   
	   You need to initalize the GRT backend before you use this function.
	   
	   @return returns true if the start recording message was sent successful, false otherwise
	  */
	 public boolean startRecording(){
	    
	    if( !initialized ) return false;
	    
	    record = true;
	    OscMessage msg = new OscMessage("/Record");
	    msg.add( 1 );
	    oscP5.send(msg, grtBackend); 
	      
	    return true; 
	  }
	  
	  /**
	   This function sends a stop recording message to the GRT GUI.
	   
	   You need to initalize the GRT backend before you use this function.
	   
	   @return returns true if the stop recording message was sent successful, false otherwise
	  */
	  public boolean stopRecording(){
	    
	    if( !initialized ) return false;
	    
	    record = false;
	    OscMessage msg = new OscMessage("/Record");
	    msg.add( 0 );
	    oscP5.send(msg, grtBackend); 
	      
	    return true; 
	  }
	  
	  /**
	   This function sends a train message to the GRT GUI.
	   
	   You need to initalize the GRT backend before you use this function.
	   
	   @return returns true if the train message was sent successful, false otherwise
	  */
	  public boolean startTraining(){
	    
	    if( !initialized ) return false;
	    OscMessage msg = new OscMessage("/Train");
	    msg.add( 1 );
	    oscP5.send(msg, grtBackend); 
	      
	    return true;
	  }
	  
	  /**
	   This function sends a message to the GRT GUI indicating that it should save the current training dataset to a file.
	   
	   You need to initalize the GRT backend before you use this function.
	   
	   @param String filename: the name of the file you want to save the training data to
	   @return returns true if the SaveTrainingDatasetToFile message was sent successful, false otherwise
	  */
	public  boolean saveTrainingDatasetToFile( String filename ){
	    
	    if( !initialized ) return false;
	    
	    OscMessage msg = new OscMessage("/SaveTrainingDatasetToFile");
	    msg.add( filename );
	    oscP5.send(msg, grtBackend);
	    
	    return true;
	  }
	  
	  /**
	   This function sends a message to the GRT GUI indicating that it should load a training dataset from a file.
	   
	   You need to initalize the GRT backend before you use this function.
	   
	   @param String filename: the name of the file you want to load the training data from
	   @return returns true if the loadTrainingDatasetFromFile message was sent successful, false otherwise
	  */
	  public boolean loadTrainingDatasetFromFile( String filename ){
	    
	      if( !initialized ) return false;
	     
	      OscMessage msg = new OscMessage("/LoadTrainingDatasetFromFile");
	      msg.add( filename );
	      oscP5.send(msg, grtBackend);
	      
	      return true;
	  }
	  
	  /**
	   This function sends a message to the GRT GUI indicating that it should clear any training data.
	   
	   You need to initalize the GRT backend before you use this function.
	   
	   @return returns true if the ClearTrainingDataset message was sent successful, false otherwise
	  */
	  public boolean clearTrainingDataset( ){
	    
	      if( !initialized ) return false;
	     
	      OscMessage msg = new OscMessage("/ClearTrainingDataset");
	      oscP5.send(msg, grtBackend);
	      
	      return true;
	  }
	  
	  /**
	   This function sends a message to the GRT GUI to set a specific classifier. You can also use this function to select if
	   the classifier should use scaling or null rejection and to update the null rejection threshold.
	   
	   Note that calling this function will automatically update your classifier, so you will need to retrain any classification
	   model after doing this before you can use the new classifier.
	   
	   You need to initalize the GRT backend before you use this function.
	   
	   @param int classifierType: this should be one of the classifier types (see the list of defined classifier types at the top of this class)
	   @param boolean useScaling: if true, then the GRT will automatically scale your data
	   @param boolean useNullRejection: if true, then the GRT will automatically try to reject new data that has a low probability
	   @param double nullRejectionThreshold: this value controls the null rejection threshold. See http://www.nickgillian.com/wiki/pmwiki.php/GRT/AutomaticGestureSpotting for more information.
	   @return returns true if the setClassifier message was sent successful, false otherwise
	  */
	 public boolean setClassifier(int classifierType,boolean useScaling,boolean useNullRejection,double nullRejectionThreshold ){
	    
	      if( !initialized ) return false;
	      
	     if( classifierType < ANBC || classifierType > SVM ) return false;
	      
	      if( nullRejectionThreshold < 0 ) return false;
	      
	      OscMessage msg = new OscMessage("/SetClassifier");
	      msg.add( classifierType );
	      msg.add( useScaling ? 1 : 0 );
	      msg.add( useNullRejection ? 1 : 0 );
	      msg.add( nullRejectionThreshold );
	      oscP5.send(msg, grtBackend);
	      
	      return true;
	  }
	  
	  
	  public boolean drawInfoText( float x, float y ){
	    
	      if( !initialized ) return false;
	      
	      //Draw the info text
	      myParent.fill( 41 ); 
	      myParent.textFont(font, 12);
	    
	      //Draw the status
	      int spacer = 20;
	  
	      //Draw the pipeline mode
	      switch( getPipelineMode() ){
	        case  CLASSIFICATION_MODE:
	        	myParent.text("PipelineMode: CLASSIFICATION", x, y);
	        break;
	        case  REGRESSION_MODE:
	        	myParent.text("PipelineMode: REGRESSION", x, y);
	        break;
	        case  TIMESERIES_MODE:
	        	myParent.text("PipelineMode: TIMESERIES", x, y);
	        break;
	      }
	      y += spacer*2;
	    
	      //Draw the training info
	      myParent.text("-----------TrainingInfo-----------", x, y); 
	  
	      y += spacer;
	      myParent.text("Recording: " + (getRecordingStatus()==true?"YES":"NO"), x, y);
	  
	      y += spacer;
	      switch( getPipelineMode() ){
	        case  CLASSIFICATION_MODE:
	        	myParent.text("TrainingClassLabel: " + getTrainingClassLabel(), x, y);
	        break;
	        case  REGRESSION_MODE:
	          String targetVectorText = "TargetVector: ";
	          for(int i=0; i<targetVector.length; i++)
	            targetVectorText += " " + targetVector[i];
	          myParent.text(targetVectorText, x, y);
	        break;
	        case  TIMESERIES_MODE:
	        break;
	      }
	      
	      //Draw the prediction info
	      y += spacer*2;
	      myParent.text("-----------PredictionInfo-----------", x, y); 
	      
	      //Draw the prediction data
	      y += spacer;
	      
	      float[] regressionResults = this.getRegressionData();
	      switch( getPipelineMode() ){
	        case  CLASSIFICATION_MODE:
	        	String type ="";
	        	int l = getPredictedClassLabel();
	        	if(l==0){
	        		type = "null";
	        	}
	        	else if (l==1){
	        		type = "spiral";
	        	}
	        	else if (l==2){
	        		type = "polygon";
	        	}
	        	else if (l==3){
	        		type = "line segment";
	        	}
	        	myParent.text("PredictedClassLabel: " + type + " ("+l+")", x, y);
	          y += spacer;
	          
	          myParent.text("MaximumLikelihood: " + getMaximumLikelihood(), x, y);
	          y += spacer;
	          
	          String classLikelihoodsText = "ClassLikelihoods: ";
	          for(int i=0; i<classLikelihoods.length; i++)
	            classLikelihoodsText += "    " + (classLikelihoods[i]>1.0e-5?classLikelihoods[i]:0);
	          myParent.text(classLikelihoodsText, x, y);
	          y += spacer;
	          
	          String classDistancesText = "ClassDistances: ";
	          for(int i=0; i<classDistances.length; i++)
	            classDistancesText += "    " + classDistances[i];
	          myParent.text(classDistancesText, x, y);
	          y += spacer;
	          
	        break;
	        case  REGRESSION_MODE:
	          if( regressionResults.length > 0 ){
	            myParent.text("PredictedRegressionData: " + regressionResults[0], x, y);
	            y += spacer;
	          }
	        break;
	        case  TIMESERIES_MODE:
	        break;
	      }
	     
	      return true;
	  }
	  
	  public boolean drawTrainingInfo( float x, float y ){
		    
	      if( !initialized ) return false;
	      
	      //Draw the info text
	      myParent.fill( 41 ); 
	      myParent.textFont(font, 12);
	    
	      //Draw the status
	      int spacer = 20;
	  
	      //Draw the pipeline mode
	      switch( getPipelineMode() ){
	        case  CLASSIFICATION_MODE:
	        	myParent.text("PipelineMode: CLASSIFICATION", x, y);
	        break;
	        case  REGRESSION_MODE:
	        	myParent.text("PipelineMode: REGRESSION", x, y);
	        break;
	        case  TIMESERIES_MODE:
	        	myParent.text("PipelineMode: TIMESERIES", x, y);
	        break;
	      }
	      y += spacer*2;
	    
	
	  
	      y += spacer;
	      myParent.text("Recording: " + (getRecordingStatus()==true?"YES":"NO"), x, y);
	  
	      y += spacer;
	      switch( getPipelineMode() ){
	        case  CLASSIFICATION_MODE:
	        	myParent.text("TrainingClassLabel: " + getTrainingClassLabel(), x, y);
	        break;
	        case  REGRESSION_MODE:
	          String targetVectorText = "TargetVector: ";
	          for(int i=0; i<targetVector.length; i++)
	            targetVectorText += " " + targetVector[i];
	          myParent.text(targetVectorText, x, y);
	        break;
	        case  TIMESERIES_MODE:
	        break;
	      }
	      return true;
	  }
	  
	  public boolean drawTestingInfo( float x, float y , String[] labelNames){
		    
	      if( !initialized ) return false;
	      
	      //Draw the info text
	      myParent.fill( 41 ); 
	      myParent.textFont(font, 12);
	    
	      //Draw the status
	      int spacer = 20;
	 
	      float[] regressionResults = this.getRegressionData();
	      switch( getPipelineMode() ){
	        case  CLASSIFICATION_MODE:
	        	int l = getPredictedClassLabel();
	        	String type="";
	        	if(l!=0){
	        		type =labelNames[l-1];
	        	}
	        	else{
	        		type = "Null";
	        	}

	        	myParent.text("PredictedClassLabel: " + type + " ("+l+")", x, y);
	          y += spacer;
	          
	          myParent.text("MaximumLikelihood: " + getMaximumLikelihood(), x, y);
	          y += spacer;
	          
	          String classLikelihoodsText = "ClassLikelihoods: ";
	          for(int i=0; i<classLikelihoods.length; i++)
	            classLikelihoodsText += "    " + (classLikelihoods[i]>1.0e-5?classLikelihoods[i]:0);
	          myParent.text(classLikelihoodsText, x, y);
	          y += spacer;
	          
	          String classDistancesText = "ClassDistances: ";
	          for(int i=0; i<classDistances.length; i++)
	            classDistancesText += "    " + classDistances[i];
	          myParent.text(classDistancesText, x, y);
	          y += spacer;
	          
	        break;
	        case  REGRESSION_MODE:
	          if( regressionResults.length > 0 ){
	            myParent.text("PredictedRegressionData: " + regressionResults[0], x, y);
	            y += spacer;
	          }
	        break;
	        case  TIMESERIES_MODE:
	        break;
	      }
	     
	      return true;
	  }
	  
	  
	 
	  /**
	   Returns if the instance has been initialized.
	   
	   @return returns true if the instance has been initialized, false otherwise
	  */
	 public boolean getInitialized(){
	    return initialized;
	  }
	  
	  /**
	   Returns if a start recording message has been sent to the GRT GUI.
	   
	   @return returns true if a start recording message has been sent to the GRT GUI, false otherwise
	  */
	  public boolean getRecordingStatus(){
	    return record;
	  }
	  
	  /**
	   @return returns the current pipelineMode
	  */
	  public int getPipelineMode(){
	    return pipelineMode;
	  }
	  
	  /**
	   @return returns the current trainingClassLabel
	  */
	public  int getTrainingClassLabel(){
	    return trainingClassLabel; 
	  }
	  
	  /**
	   @return returns the most recent predictedClassLabel, received from the GRT GUI
	  */
	public  int getPredictedClassLabel(){
	    return predictedClassLabel;
	  }
	  
	  /**
	   @return returns the most recent maximumLikelihood, received from the GRT GUI
	  */
	public  double getMaximumLikelihood(){
	    return maximumLikelihood; 
	  }
	  
	  /**
	   @return returns the most recent preProcessedData, received from the GRT GUI
	  */
	public   float[] getPreProcessedData(){
	    return preProcessedData; 
	  }
	  
	  /**
	   @return returns the most recent featureExtractionData, received from the GRT GUI
	  */
	public   float[] getFeatureExtractionData(){
	    return featureExtractionData; 
	  }
	  
	  /**
	   @return returns the most recent classLikelihoods, received from the GRT GUI
	  */
	public   float[] getClassLikelihoods(){
	    return classLikelihoods; 
	  }
	  
	  /**
	   @return returns the most recent classDistances, received from the GRT GUI
	  */
	public   float[] getClassDistances(){
	    return classDistances; 
	  }
	  
	  /**
	   @return returns the most recent regressionData, received from the GRT GUI
	  */
	public   float[] getRegressionData(){
	    return regressionData; 
	  }
	  
	  /**
	   @return returns the most recent classLabels, received from the GRT GUI
	  */
	public   int[] getClassLabels(){
	    return classLabels; 
	  }
	  
	  /**
	   This function is called anytime a new OSC message is received. It will then try and parse the message.
	  */
	  void oscEvent(OscMessage theOscMessage) {
	  
	    if( parseMessage( theOscMessage ) ) return;
	 
	  }
	  
	  /**
	   This function parses OSC messages from the GRT GUI. 
	   
	   @param OscMessage theOscMessage: the osc message to parse
	   @return returns true if the message was successfully parsed, false otherwise
	  */
	  boolean parseMessage(OscMessage theOscMessage) {
	    
	    if(theOscMessage.checkAddrPattern("/PreProcessedData")==true) {
	      int N = theOscMessage.get(0).intValue();
	      if( preProcessedData.length != N ){
	        preProcessedData = new float[N]; 
	      }
	      for(int i=0; i<N; i++){
	        preProcessedData[i] =  theOscMessage.get(i+1).floatValue();
	      }
	      return true;
	    }
	    
	    if(theOscMessage.checkAddrPattern("/FeatureExtractionData")==true) {
	      int N = theOscMessage.get(0).intValue();
	      if( featureExtractionData.length != N ){
	        featureExtractionData = new float[N]; 
	      }
	      for(int i=0; i<N; i++){
	        featureExtractionData[i] =  theOscMessage.get(i+1).floatValue();
	      }
	      return true;
	    }
	    
	    if(theOscMessage.checkAddrPattern("/Prediction")==true) {
	      if(theOscMessage.checkTypetag("if")) {
	        predictedClassLabel = theOscMessage.get(0).intValue();
	        maximumLikelihood = theOscMessage.get(1).floatValue();
	        return true;
	      }   
	    }
	    
	    if(theOscMessage.checkAddrPattern("/ClassLikelihoods")==true) {
	        int N = theOscMessage.get(0).intValue();
	        if( classLikelihoods.length != N ) classLikelihoods = new float[N];
	        
	        for(int i=0; i<N; i++){
	          classLikelihoods[i] = theOscMessage.get(1+i).floatValue();
	        }
	        return true;
	    }
	    
	    if(theOscMessage.checkAddrPattern("/ClassDistances")==true) {
	        int N = theOscMessage.get(0).intValue();
	        if( classDistances.length != N ) classDistances = new float[N];
	        
	        for(int i=0; i<N; i++){
	          classDistances[i] = theOscMessage.get(1+i).floatValue();
	        }
	        return true;
	    }
	    
	    if(theOscMessage.checkAddrPattern("/ClassLabels")==true) {
	      int N = theOscMessage.get(0).intValue();
	      if( classLabels.length != N ) classLabels = new int[N]; 

	      for(int i=0; i<N; i++){
	        classLabels[i] = theOscMessage.get(i+1).intValue();
	      }
	      return true;
	    }
	    
	     if(theOscMessage.checkAddrPattern("/RegressionData")==true) {
	        int N = theOscMessage.get(0).intValue();
	        if( regressionData.length != N ) regressionData = new float[N];
	        for(int i=0; i<N; i++){
	          regressionData[i] = theOscMessage.get(1+i).floatValue();
	        }
	        return true;
	    }
	    
	    return false;
	 
	  }
}

