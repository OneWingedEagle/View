package graphics;
import javax.vecmath.Vector3f;
import math.Vect;;

public class V3f extends Vector3f {

 public V3f(Vect v){
	 super();
	x=(float)v.el[0];
	y=(float)v.el[1];
	z=(float)v.el[2];
	
}
 public V3f(double x, double y, double z){
	 super();
	this.x=(float)x;
	this.y=(float)y;
	this.z=(float)z;
	
}
 
 public V3f(float x, float y, float z){
	 super();
	this.x=x;
	this.y=y;
	this.z=z;
	
}
}
