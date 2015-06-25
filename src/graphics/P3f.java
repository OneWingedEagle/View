package graphics;
import javax.vecmath.Point3f;
import math.Vect;
import static java.lang.Math.*;

public class P3f extends Point3f {

 public P3f(Vect v){
	 super();
	x=(float)v.el[0];
	y=(float)v.el[1];
	if(v.length==2)
		z=0;
	else
	z=(float)v.el[2];
	
}
  
 public P3f(double x, double y, double z){
	 super();
	this.x=(float)x;
	this.y=(float)y;
	this.z=(float)z;
	
}
 
 public P3f(float x, float y, float z){
	 super();
	this.x=x;
	this.y=y;
	this.z=z;
	
}
 
 
 public P3f add(P3f p1){
	 return new P3f(this.x+p1.x,this.y+p1.y,this.z+p1.z);
 }
 
 public P3f sub(P3f p1){
	 return new P3f(this.x-p1.x,this.y-p1.y,this.z-p1.z);
 }
 
 public P3f times(double a){
	 return new P3f(this.x*a,this.y*a,this.z*a);
 }
 
 public P3f normalized(){
	 double a=1.0/sqrt(this.x*this.x+this.y*this.y+this.z*this.z);
	 return new P3f(this.x*a,this.y*a,this.z*a);
 }
 
}
