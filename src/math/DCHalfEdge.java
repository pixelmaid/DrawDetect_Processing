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

import java.util.Collections;
import java.util.Vector;

import math.Geom;


public class DCHalfEdge implements Comparable<DCHalfEdge> {

    public Point start; //pointer to start point
    public Point end = null;  //pointer to end point
    public Point direction; //directional vector, from "start", points to "end", normal of |left, right|
    public Point left = null;  //pointer to site on the left side of edge
    public Point right = null; //pointer to site on the left side of edge
    public DCHalfEdge intersectedEdge = null; //edge that this edge will be added beneath if a merge takes place
    private double length; //euclidean length of edge;
    public int infiniteEdge = 0;

    
    public boolean inner = false; //determines if edge needs to be cut first because it is an inner edge
   public double m; //directional coefficients satisfying equation y = m*x + b (edge lies on this line)
   public double b;

    public DCHalfEdge neighbor = null; //twin to edge
    
    public double rotation = 0;
    public Point focus;


    public DCHalfEdge(Point start, Point end) {//constructor for end that is predetermined
        this.start = start;
        this.end = end;

        m = (start.getY() - end.getY()) / (start.getX() - end.getX()); //calculate the slope of the line by the inverse of the slope of the line through left and right
        b = start.getY() - (m * start.getX()); //calculate the y intercept with y=mx+b


        this.length = Geom.distance(start, end);
        this.focus=start;
        //System.out.println("slope="+m+"length="+this.length);
    }
    
    
    public DCHalfEdge(Point origin, double rad, double theta) {//constructor for end that is predetermined
        this.start = origin;
        
        this.end = Geom.polarToCart(rad, theta).copy();
        this.end.setX(this.end.getX()+this.start.getX());
        this.end.setY(this.end.getY()+this.start.getY());

        m = (start.getY() - end.getY()) / (start.getX() - end.getX()); //calculate the slope of the line by the inverse of the slope of the line through left and right
        b = start.getY() - (m * start.getX()); //calculate the y intercept with y=mx+b


        this.length = Geom.distance(start, end);
        this.focus=start;
       // System.out.println("slope="+m+"length="+this.length);
    }

    public DCHalfEdge(Point start, Point left, Point right) {//constructor
        this.start = start;
        this.left = left;
        this.right = right;

        m = (right.getX() - left.getX()) / (left.getY() - right.getY()); //calculate the slope of the line by the inverse of the slope of the line through left and right
        b = start.getY() - m * start.getX(); //calculate the y intercept with y=mx+b

        direction = new Point((right.getY() - left.getY()), -(right.getX() - left.getX()));
        this.focus = start;


    }

    public void setleft(Point left) {
        this.left = left;
    }

    public void setright(Point right) {
        this.right = right;
    }
    
    
    public double getSlope(){
    	/*System.out.println("start X="+this.start.getX());
    	System.out.println("start Y="+this.start.getY());
    	System.out.println("end X="+this.end.getX());
    	System.out.println("end Y="+this.end.getY());
    	*/
    	 this.m = (this.start.getY() - this.end.getY()) / (this.start.getX() - this.end.getX()); //calculate the slope of the line by the inverse of the slope of the line through left and right
    	 
    	 if(this.m>Geom.BIG){
    		 return Double.POSITIVE_INFINITY;
    	 }
    	 else{
    		 return this.m;
    	 }
    		
    }
    
    
    public double getYIntercept(){
   	 		this.m = (start.getY() - end.getY()) / (start.getX() - end.getX()); //calculate the slope of the line by the inverse of the slope of the line through left and right
          this.b = start.getY() - m * start.getX(); //calculate the y intercept with y=mx+b
          return b;
   		
   }
    public Point getMidPoint(){
    	return Geom.getMidpoint(this.start, this.end);
    }
    
    public double getLength(){
    	this.length = Geom.distance(start, end);
    	return this.length;
    }

    public int compareTo(DCHalfEdge e) {//finds edge with smallest y coordinate
    	Vector<Point> verticies = new Vector<Point>();
    	verticies.add(e.start);
    	verticies.add(e.end);
    	verticies.add(this.start);
    	verticies.add(this.end);
    	Collections.sort(verticies);
    	/*for(int i=0;i<verticies.size();i++){
    		System.out.println("vertex at"+i+"="+verticies.get(i).getX()+","+verticies.get(i).getY());
    	}*/
    	if(this.start==verticies.get(0)|| this.end==verticies.get(0)){
    		return -1;
    	}
    	else{
    		return 1;
    	}
    }


    public void moveTo(double x, double y, Point focus) {
        double dStartX = start.getX() - focus.getX();
        double dStartY = start.getY() - focus.getY();
        
        double dEndX = end.getX() - focus.getX();
        double dEndY = end.getY() - focus.getY();

        start.setX(dStartX + x);
        start.setY(dStartY + y);
        end.setX(dEndX+x);
        end.setY(dEndY+y);

        //m = (start.getY() - end.getY()) / (start.getX() - end.getX()); //calculate the slope of the line by the inverse of the slope of the line through left and right
        b = start.getY() - (m * start.getX()); //calculate the y intercept with y=mx+b

    }
    
    public void moveBy(double x, double y) {
        
        start.setX(start.getX() + x);
        start.setY(start.getY() + y);
        end.setX(end.getX() + x);
        end.setY(end.getY() + y);

        m = (start.getY() - end.getY()) / (start.getX() - end.getX()); //calculate the slope of the line by the inverse of the slope of the line through left and right
        b = start.getY() - (m * start.getX()); //calculate the y intercept with y=mx+b

    }

    public void rotate(double theta, Point _focus) {

        double[] startRT = Geom.cartToPolar(start.getX() - _focus.getX(), start.getY() - _focus.getY());
        double[] endRT = Geom.cartToPolar(end.getX() - _focus.getX(), end.getY() - _focus.getY());
        double startTheta = startRT[1];
        double startR = startRT[0];

        double endTheta = endRT[1];
        double endR = endRT[0];

        double newStartTheta = startTheta + theta;
        double newEndTheta = endTheta + theta;


        Point newStart = Geom.polarToCart(startR, newStartTheta);
        Point newEnd = Geom.polarToCart(endR, newEndTheta);
        start.setX(newStart.getX() + _focus.getX());
        start.setY(newStart.getY() + _focus.getY());
        end.setX(newEnd.getX() + _focus.getX());
        end.setY(newEnd.getY() + _focus.getY());

        m = (start.getY() - end.getY()) / (start.getX() - end.getX()); //calculate the slope of the line by the inverse of the slope of the line through left and right
        b = start.getY() - (m * start.getX()); //calculate the y intercept with y=mx+b
        this.rotation= theta;

    }
    
    
    //calculates the actual angle of the line
    public void normalizeRotation(){
    	
    	
    	
    }
    
   
    
    
    
}