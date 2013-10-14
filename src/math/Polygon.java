package math;

import java.util.ArrayList;
import java.util.Collections;

import math.Geom;
import math.Vec2d;


import processing.core.PApplet;
import processing.core.PGraphics;




public class Polygon {
	private Point origin;
	protected ArrayList<Point> points;
	private int sideNum = 0;
	private double sideLength = 0;
	//private ArrayList<Hole> holes;
	boolean closed = true;
	private static double DEFAULT_LENGTH = 20;
	
	public Polygon(){
		this(0,0);
	}
	
	public Polygon(Point o){
		this.origin=o;
		points= new ArrayList<Point>();
	//	holes = new ArrayList<Hole>();
	}
	
	public Polygon(int sides, double length){
		this(0,0,sides,length);
	}
	
	public Polygon(double x, double y, int sides){
		this(x,y,sides,DEFAULT_LENGTH);
	}
	
	public Polygon setRadius(double r,int sides){
		this.clearPoints();
		sideNum  = sides;
		sideLength = 2*r*Math.sin(Math.toRadians(180/sides));
		for(int i=0;i<sides;i++){
			double theta = 360/sides*i;
			Point p = new Point(0,0,Math.toRadians(theta)+Math.PI/2 - Math.PI/sides,r);
			this.addPoint(p);
		}
		return this;
	}
	
	public void setOrigin(Point o){
		this.origin=o;
	}
	
	public Point getOrigin(){
		return this.origin;
	}
	
	
	public Polygon(double x, double y,int sides, double length){
		this(new Point(x,y));
		sideNum  = sides;
		sideLength = length;
		if(sides!=0){
		double a = length/(2*Math.sin(Math.toRadians(180/sides)));
		for(int i=0;i<sides;i++){
			double theta = 360/sides*i;
			Point p = new Point(0,0,Math.toRadians(theta)+Math.PI/2 - Math.PI/sides,a);
			this.addPoint(p);
		}
		//this.setPointsAbsolute();
		//this.setPointsRelativeTo(new Point(0,0));
		}
		
		/*double angle = 360/(double)sides;
		resetTurtle();
		for(int i=0;i<sides;i++){
			  this.forward(length);
			  this.right(angle);
			}*/
		
	}
	
	/*public void addHole(Hole h){
		h.setParent(this);
		holes.add(h);
	}*/
	
	public Polygon(ArrayList<Point> pts) {
		this(new Point(0,0));
		this.points=pts;
		this.setOrigin(Geom.findCentroid(this.getPoints()));
	
		
	}

	public void addPoint(Double x, Double y){
		addPoint(new Point(x,y));
		
		
	}
	
	protected void addPoint(Point point){
		this.points.add(point);
	}
	public void reversePoints(){
		Collections.reverse(this.points);
	}
	public void clearPoints(){
		this.points.clear();
	}
	
	public ArrayList<Point> getPoints(){
		return this.points;
	}
	public void setPoints(ArrayList<Point> p){
		this.points = p;
	}
	



}




