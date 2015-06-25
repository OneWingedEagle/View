package graphics;
import javax.vecmath.Point3d;
import math.Vect;;


public class P3d extends Point3d {

 public P3d(Vect v){
	 super();
	x=v.el[0];
	y=v.el[1];
	z=v.el[2];
	
}
 public P3d(double x, double y, double z){
	 super();
	this.x=x;
	this.y=y;
	this.z=z;
	
}
 
}
