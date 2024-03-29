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




public class Point extends Point2D implements Comparable<Point>{
	public double x;
	public double y;
	public boolean selected = false; // used for mouse events

	public Point(double _x, double _y) {
		
		x = _x;
		y = _y;
		
	}
	
	
	public Point(double orgX, double orgY, double theta, double rad) {

        double xPos = Math.cos(theta)*rad+orgX;
        double yPos =  Math.sin(theta)*rad+orgY;

        x=xPos;
        y=yPos;

    }
	
	public boolean withinRange(double range, double x, double y){
		
		/*if(this.getX()==0 && this.getY()==0){
			System.out.println("comparing p1="+this.getX()+","+this.getY()+" and p2="+x+","+y+" distance ="+Geom.distance(this, new Point(x,y)));
		}*/
		
		if(Geom.distance(this, new Point(x,y))<range){
			return true;
		}
		else{
			return false;
		}
		/*if((Math.abs(this.getX()-x)<=range)&&(Math.abs(this.getY()-y)<=range)){
			return true;
		}
		else{
			return false;
		}*/
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}


	public void setY(double y) {
		this.y = y;
	}

	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;

	}

	public int compareToX(Point o) {
		return (this.getX() < o.getX()) ? -1 : (this.getX() > o.getX()) ? 1 : 0;
	}

	public int compareTo(Point o) {
		/*if ((this.getX() == o.getX()) && (this.getY() == o.getY())) {
			return 0;
		}*/
		//System.out.println("comparing");
		if(this.withinRange(0.1,o.getX(), o.getY())){
			//System.out.println("within range");

			return 0;
		}
		else if (this.getY() < o.getY()) {
			return -1;
		}
		//if y coords are same, check x
		else if (this.getY() == o.getY()) {
			if (this.getX() < o.getX()) {
				return -1;
			} else {
				return 1;
			}
		} else if (this.getY() > o.getY()) {
			return 1;
		} else {
			return -2;
		}
	}


	public Point difference(Point p) {
		Point d = new Point(this.getX() - p.getX(), this.getY() - p.getY());
		return d;

	}

	public Point add(Point p) {
		Point d = new Point(this.getX() + p.getX(), this.getY() + p.getY());
		return d;

	}

	public void scale(double scaleVal) {
		//Point s = new Point(this.getX() * scaleVal, this.getY() * scaleVal);
		this.x*=scaleVal;
		this.y*=scaleVal;
	}

	
	public void scaleX(double scaleVal) {
		//Point s = new Point(this.getX() * scaleVal, this.getY() * scaleVal);
		this.x*=scaleVal;
		
	}

	
	public void scaleY(double scaleVal) {
		//Point s = new Point(this.getX() * scaleVal, this.getY() * scaleVal);
		this.y*=scaleVal;
		
	}
	public double angle(Point p) {

		double x = p.x - this.x;
		double y = p.y - this.y;

		return Geom.cartToPolar(x, y)[1];
	}

	public void moveTo(double x, double y, Point focus) {
        double dX = x - focus.getX();
        
        double dY = y - focus.getY();

        this.setX(dX + this.getX());
        this.setY(dY + this.getY());

    }
	
	
	 public void moveBy(double x, double y) {  
	        this.setX(this.getX() + x);
	        this.setY(this.getY() + y);
	      
	 }
	
	
	//moves a point towards a target point by a specified distance
	public void moveToPolar(double dist, Point _focus){

		double[] pointRT = Geom.cartToPolar(this.getX() - _focus.getX(), this.getY() - _focus.getY());
		double pointTheta = pointRT[1];
		double pointR = pointRT[0];


		double newPointR = pointR*dist;



		Point newPoint = Geom.polarToCart(newPointR, pointTheta);

		this.setX(newPoint.getX() + _focus.getX());
		this.setY(newPoint.getY() + _focus.getY());

	}

	//moves a point towards a target point by a specified distance
	public void moveToEuclidean(double dist, Point target){

		double df = Math.sqrt(Math.pow(this.getX()-target.getX(),2)+Math.pow(this.getY()-target.getY(),2));
		double dp = df-dist;
		double x2 = target.getX() + dp/df * (this.getX()-target.getX());
		double y2 = target.getY() + dp/df * (this.getY()-target.getY());
		this.setX(x2);
		this.setY(y2);


	}


	public Point rotate(double theta, Point _focus) {

		double[] pointRT = Geom.cartToPolar(this.getX() - _focus.getX(), this.getY() - _focus.getY());
		double pointTheta = pointRT[1];
		double pointR = pointRT[0];


		double newPointTheta = pointTheta + theta;



		Point newPoint = Geom.polarToCart(pointR, newPointTheta);
		
		this.setX(newPoint.getX() + _focus.getX());
		this.setY(newPoint.getY() + _focus.getY());
		return this;

	}

	public Point copy() {
		return new Point(x,y);
	}


	public void draw(float strokeWeight) {
		//parent.strokeWeight(strokeWeight);
		//parent.point((float)this.x, (float)this.y);
		
	}



	public void print(float strokeWeight) {
		//parent.strokeWeight(strokeWeight);
		//parent.point((float)this.x, (float)this.y);
		//TODO:implement print method
		
	}


	

	
	
}
