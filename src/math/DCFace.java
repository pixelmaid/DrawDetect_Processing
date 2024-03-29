/*
 * Codeable Objects by Jennifer Jacobs is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * Based on a work at hero-worship.com/portfolio/codeable-objects.
 *
 * This file is part of the Codeable Objects Framework.
 *
 *     Codeable Objects is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Codeable Objects is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Codeable Objects.  If not, see <http://www.gnu.org/licenses/>.
 */

package math;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;

import math.Geom;


public class DCFace extends DoublyConnectedEdgeList {

    public ArrayList<DCHalfEdge> innerComponents;
    protected Point origin;
    public int color;
    public DCFace() {
    	
    	color = new Random().nextInt()*0xFFFFFF;
    

    }

  
    public void setOrigin(Point point){
    	this.origin=point.copy();
    }
    
    public Point getOrigin(){
    	return this.origin;
    }
   
    public int compareTo(DCFace f) {
        return 0;
    }
    
    
    public void orderEdges(){
    	int missedEdges = 0;
    	ArrayList<DCHalfEdge> newEdges = new ArrayList<DCHalfEdge>(0);
    	Collections.sort(edges);
    
    	//copy over new edges;
    	for(int i=0;i<edges.size();i++){
    		DCHalfEdge repEdge = new DCHalfEdge(edges.get(i).start,edges.get(i).end);
    		this.edges.set(i,repEdge);
    		//System.out.println("start ="+edges.get(i).start.getX()+","+edges.get(i).start.getY()+" end="+edges.get(i).end.getX()+","+edges.get(i).end.getY());
    		
    	}
    	ArrayList<Point> dupVerticies = new ArrayList<Point>(0);
    	for(int i=0;i<this.edges.size();i++)
    	{
    		dupVerticies.add(this.edges.get(i).start);
    		dupVerticies.add(this.edges.get(i).end);
    	}
    	ArrayList<Point> verticies = Geom.removeDuplicateVerts(this);
    	//System.out.println("vert num="+verticies.size()+" edge num="+edges.size());
    	//find case based on verticies
    	
    	if(edges.size()==1){ //only 1 edge
    		//System.out.println("there is only one edge");
    		//findUnconnectedEdges(dupVerticies, verticies);
    		
    	}
    	else if (verticies.size()-edges.size()==1){ //incomplete shape missing one edge
    		//System.out.println("missing one edge");
    		findUnconnectedEdges(dupVerticies, verticies);
    	}
    	else if(verticies.size()-edges.size()==0){ //complete shape
    		//System.out.println("complete shape");
    		sortEdges(verticies);
    	}
    	
    	else if(verticies.size()-edges.size()==2){ //two distinct segments
    		//System.out.println("two distinct segments");
    	}
    	
    }
    
    /*public void makeBox(PApplet parent, DCHalfEdge edge, CompPoint point){ //makes a box shape out of the single edge
    	CompPoint focalIntersection = Geom.findIntersectionPoint(edge, point, 1.0/edge.m*-1.0);
    	
    	CompPoint startIntersection;
    	CompPoint endIntersection;
    	//DCHalfEdge perpEdge = new DCHalfEdge(intersection, point);
    	double edgeTheta = Geom.cartToPolar(point.getX()-focalIntersection.getX(),point.getY()-focalIntersection.getY())[1];
    	DCHalfEdge borderEdge;
    	DCHalfEdge topEdge;
    	DCHalfEdge bottomEdge;
    	
    	DCHalfEdge boxTop= new DCHalfEdge(new CompPoint(parent.width,0),new CompPoint(0,0));
    	DCHalfEdge boxLeft= new DCHalfEdge(new CompPoint(0,0),new CompPoint(0,parent.height));
    	DCHalfEdge boxBottom= new DCHalfEdge(new CompPoint(0,parent.height),new CompPoint(parent.width,parent.height));
    	DCHalfEdge boxRight= new DCHalfEdge(new CompPoint(parent.width,parent.height),new CompPoint(parent.width,0));
    	DoublyConnectedEdgeList box = new DoublyConnectedEdgeList();
    	box.addHalfEdge(boxTop);
    	box.addHalfEdge(boxLeft);
    	box.addHalfEdge(boxBottom);
    	box.addHalfEdge(boxRight);
    	
    	
    	edgeIntersectsPolygon(DCHalfEdge edge, DoublyConnectedEdgeList border)
    	this.deleteEdge(edge);
    	if(edgeTheta<45||edgeTheta>315){
    		
    		startIntersection = Geom.findIntersectionPoint(boxTop, point, edge.m);
    		endIntersection = Geom.findIntersectionPoint(boxBottom, point, edge.m);
    		endIntersection = Geom.findIntersectionPoint(edge, point, 1.0/edge.m*-1.0);
    		
    		borderEdge = boxRight;
    		topEdge = new DCHalfEdge(boxTop.start,startIntersection);
    		
    		bottomEdge = new DCHalfEdge(endIntersection,boxBottom.end);
    		this.addHalfEdge(borderEdge);
    		this.addHalfEdge(topEdge);
    		this.addEdgeAt(bottomEdge,edges.size());
    	}
    }*/
    
    public void findUnconnectedEdges(ArrayList<Point> dupVerticies, ArrayList<Point> verticies){
    	ArrayList<Point>unconnectedVerticies = new ArrayList<Point>(0);
    	ArrayList<DCHalfEdge> unconnectedEdges = new ArrayList<DCHalfEdge>(0);
    	
    	
    	for(int i =0;i<verticies.size();i++){
    		
    		dupVerticies.remove(verticies.get(i));
    		if(!dupVerticies.remove(verticies.get(i))){
    			unconnectedVerticies.add(verticies.get(i));
    			//System.out.println("unconnected vertex found at:"+verticies.get(i).getX()+","+verticies.get(i).getY());
    		}
    		
    	}
    	for(int i=0; i<this.edges.size();i++){
    		DCHalfEdge currentEdge = edges.get(i);
    		for(int j =0;j< unconnectedVerticies.size();j++){
    			Point vert = unconnectedVerticies.get(j);
    			if (currentEdge.start.compareTo(vert)==0 ||currentEdge.end.compareTo(vert)==0){
    				unconnectedEdges.add(currentEdge);
    			}
    		}
    	
    	}
		//System.out.println("unconnected edges:"+unconnectedEdges.size());
		
		//add in new edge so shape is complete
		this.edges.add(new DCHalfEdge(unconnectedVerticies.get(0),unconnectedVerticies.get(1)));
		
		sortEdges(verticies);
    }
    
    private void sortEdges(ArrayList<Point> verticies){
    	DCHalfEdge highestEdge1 = null;
		DCHalfEdge highestEdge2 = null;
		Collections.sort(verticies,new CmpY());
		/*System.out.println("unique vertex order:");
		for(int i=0;i<verticies.size();i++){
			System.out.println(verticies.get(i).getX()+","+verticies.get(i).getY());
		
		}*/
    	for(int i=0;i<edges.size();i++){
    		if (edges.get(i).start.compareTo(verticies.get(0))==0||edges.get(i).end.compareTo(verticies.get(0))==0){
    			if(highestEdge1==null){
    				highestEdge1=edges.get(i);
    			}
    			else if(highestEdge2==null){
    				highestEdge2=edges.get(i);
    				break;
    			}
    			
    		}
    		
    	}
    	/*
    	System.out.println("highestEdge1");
    	System.out.println(highestEdge1.start.getX()+","+highestEdge1.start.getY());
    	System.out.println(highestEdge1.end.getX()+","+highestEdge1.end.getY());
     	System.out.println("highestEdge2");	
    	System.out.println(highestEdge2.start.getX()+","+highestEdge2.start.getY());
    	System.out.println(highestEdge2.end.getX()+","+highestEdge2.end.getY());
    	*/
    	
    	
    	double angle1 = Geom.getHighestAngle(highestEdge1,verticies.get(0));
    	double angle2 = Geom.getHighestAngle(highestEdge2,verticies.get(0));
    	DCHalfEdge selectedEdge;
    	if(angle1>=angle2){
    		selectedEdge = highestEdge1;
    		
    		
    	}
    	else{
    		selectedEdge = highestEdge2;
    		
    	}
    	DCHalfEdge cleanedEdge;
    	if(selectedEdge.start.compareTo(verticies.get(0))==0)
    		cleanedEdge= new DCHalfEdge(verticies.get(0),selectedEdge.end);
    	else
    		cleanedEdge= new DCHalfEdge(verticies.get(0),selectedEdge.start);
	
		/*System.out.println("===============");
		System.out.println("highestEdge1");
    	System.out.println(highestEdge1.start.getX()+","+highestEdge1.start.getY());
    	System.out.println(highestEdge1.end.getX()+","+highestEdge1.end.getY());
     	System.out.println("highestEdge2");	
    	System.out.println(highestEdge2.start.getX()+","+highestEdge2.start.getY());
    	System.out.println(highestEdge2.end.getX()+","+highestEdge2.end.getY());
		*/
		ArrayList<DCHalfEdge>sortedEdges = new ArrayList<DCHalfEdge>(0);
		
			
		
		recurseSort(cleanedEdge,selectedEdge,sortedEdges);
		
    }
    
    
    private void recurseSort(DCHalfEdge currentEdge,DCHalfEdge edgeToRemove, ArrayList<DCHalfEdge>sortedEdges){
    	DCHalfEdge currentEdgeNew=null;
    	DCHalfEdge edgeToRemoveNew=null;
    	edges.remove(edgeToRemove);
		sortedEdges.add(currentEdge);
    	if(this.edges.size()!=0){
    		
    		for(int i=0;i<edges.size();i++){
    			DCHalfEdge checkEdge = edges.get(i);
    			if(checkEdge.start.compareTo(currentEdge.end)==0){
    			//	System.out.println("found edge at start");
    				currentEdgeNew = checkEdge;
    				edgeToRemoveNew = checkEdge;
    				
    				break;
    				
    			}
    			
    			if(checkEdge.end.compareTo(currentEdge.end)==0){
    				//System.out.println("found edge at end");
    				
    				currentEdgeNew = new DCHalfEdge(checkEdge.end,checkEdge.start);
    				edgeToRemoveNew = checkEdge;
    				
    				break;
    				
    			}
    		}
    		recurseSort(currentEdgeNew,edgeToRemoveNew,sortedEdges);
    	}
    	else{
    		edges= sortedEdges;
    	}
    	
    }
    
   
    
   
    
   
    
  /*  private void sortEdges(DCHalfEdge highestEdge,,sortedEdges){
    	
    }*/
    
    
    //rotates all edges around the origin by an increment of theta;
    public void rotate(double theta) {
       this.rotate(theta,this.origin);
    }
    


public void drawOffset(double offset){
	
	
	//parent.stroke(0);
	//parent.strokeWeight(1);
	for(int i=0;i<edges.size();i++){
		DCHalfEdge edge = edges.get(i);
		//parent.line((float)edge.start.getX()+(float)offset, (float) edge.start.getY(), (float)edge.end.getX()+(float)offset,(float)edge.end.getY());
		
	}
	
	
}

}

//compares y values of two points
class EdgeCmpY implements Comparator<Point2D> {
  public int compare(Point2D a, Point2D b) {
      return (a.getY() < b.getY()) ? -1 : (a.getY() > b.getY()) ? 1 : 0;
  }
}
