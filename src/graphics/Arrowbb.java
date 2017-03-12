package graphics;
import math.Vect;
import math.util;

import java.awt.Color;

import javax.media.j3d.Appearance;

import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;

import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3f;


import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;

import static java.lang.Math.*;


public class Arrowbb extends TransformGroup {
	double mode;
	Shape3D arrowShape;

	public 	TransformGroup tgCone,tgAx;
	public Transform3D transCone,transAx;
	Cone cone;
	Cylinder axis;

	public Vect scale=new Vect(1,1);
	public Arrowbb(int mode){

		
		this.mode=mode;
		 this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			 if(mode==-2)
				setArrowm2();
			 else if(mode==-1)
			setArrowm1();
			else if(mode==0)
			setArrow0();
			else if(mode==1)
			setArrow1();
		
		else if(mode==2)
			setArrow2Dm2();
		else if(mode==3)
			setArrow2Dm1();
		else if(mode==4)
			setArrow2D0();
		else if(mode==5)
			setArrow2D1();
			
	
	}
	
	
	public void setArrowm2(){
		
		
		Color3f color3=new Color3f(Color.red);

	double base=0,height=0;

		base=.1*scale.el[0];
		height=.4*scale.el[1];

	 LineArray head=new LineArray(6, GeometryArray.COORDINATES);

	 P3f[] vert=new P3f[4];

		vert[0]=new P3f(0,height,0);
		vert[1]=new P3f(-.3*base,.9*height,0);
		vert[2]=new P3f(.3*base,.9*height,0);
		vert[3]=new P3f(0,0,0);


		head.setCoordinate(0,vert[0]);
		head.setCoordinate(1,vert[1]);
		head.setCoordinate(2,vert[0]);
		head.setCoordinate(3,vert[2]);
		head.setCoordinate(4,vert[0]);
		head.setCoordinate(5,vert[3]);

	LineAttributes la=new LineAttributes(1.8f,LineAttributes.PATTERN_SOLID,false);
	ColoringAttributes ca=new  ColoringAttributes(color3, ColoringAttributes.SHADE_FLAT);
	Appearance app=new Appearance();

	ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

	app.setLineAttributes(la);
	app.setColoringAttributes(ca);


	arrowShape=new Shape3D(head,app);
			
			TransformGroup arrow=new TransformGroup();
		
			arrow.addChild(arrowShape);
	}
	
	public void setArrowm1(){
		
		double base=.2*scale.el[0];
		P3f[] vertex=new P3f[4];
		vertex[0]=new P3f(-base/2,-base/2,0);
		vertex[1]=new P3f(base/2,-base/2,0);
		vertex[2]=new P3f(0,base/2,0);
		vertex[3]=new P3f(0,0,scale.el[1]);

		LineArray edge=new LineArray(12, GeometryArray.COORDINATES);


				edge.setCoordinate(0,vertex[0]);
				edge.setCoordinate(1,vertex[1]);
				edge.setCoordinate(2,vertex[1]);
				edge.setCoordinate(3,vertex[2]);
				edge.setCoordinate(4,vertex[2]);
				edge.setCoordinate(5,vertex[0]);
				edge.setCoordinate(6,vertex[0]);
				edge.setCoordinate(7,vertex[3]);
				edge.setCoordinate(8,vertex[1]);
				edge.setCoordinate(9,vertex[3]);
				edge.setCoordinate(10,vertex[2]);
				edge.setCoordinate(11,vertex[3]);
				
				
				Color3f color3=new Color3f();
		
				ColoringAttributes ca=new  ColoringAttributes(color3, ColoringAttributes.SHADE_FLAT);
				Appearance app=new Appearance();

				ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);


				app.setColoringAttributes(ca);
				
				arrowShape=new Shape3D(edge,app);
							
					
				    addChild(arrowShape);
					   
			

					  
		 }
	
	public void setArrow0(){
		
		double base=.1*scale.el[0];
	
	//this.P=P.v3();
	
	IndexedQuadArray pyramid = new IndexedQuadArray(8,
		        IndexedQuadArray.COORDINATES | IndexedQuadArray.NORMALS, 24);
		    P3f[] cubeCoordinates = { 
		    	new P3f(0,.0,scale.el[1]),
		    	new P3f(0,0,scale.el[1]),
		    	new P3f(0,-0,scale.el[1]),
		    	new P3f(0,0,scale.el[1]),
		    	new P3f(base,base,0),
		    	new P3f(-base,base,0),
		    	new P3f(-base,-base,0),
		    	new P3f(base,-base,0),
		    	};
		    Vector3f[] normals = {
		    		new Vector3f(0.0f, 0.0f, -1.0f),
		        new Vector3f(0.0f, 0.0f, 1.0f),
		        new Vector3f(-1.0f, 0.0f, 0.0f),
		        new Vector3f(1.0f, 0.0f, 0.0f),
		        new Vector3f(0.0f, -1.0f, 0.0f),
		        new Vector3f(0.0f, 1.0f, 0.0f) };

		    int coordIndices[] = { 0, 1, 2, 3, 7, 6, 5, 4, 0, 3, 7, 4, 5, 6, 2, 1,
			        0, 4, 5, 1, 6, 7, 3, 2 };
		    
		 int normalIndices[] = { 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3,
			        4, 4, 4, 4, 5, 5, 5, 5 };


		    pyramid.setCoordinates(0, cubeCoordinates);
		 pyramid.setNormals(0, normals);
		    pyramid.setCoordinateIndices(0, coordIndices);
		 pyramid.setNormalIndices(0, normalIndices);
		 
		    Appearance app = new Appearance();
			app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

			
		    Color3f color3=new  Color3f(Color.red);
		    
    
		    Color3f emissiveColour = new Color3f(.2f*color3.getX(), .2f*color3.getY(), .2f*color3.getZ());
		    Color3f specularColour = new Color3f(.1f, .1f, .1f);
		    Color3f diffuseColour =new Color3f(.8f*color3.getX(), .8f*color3.getY(), .8f*color3.getZ());
		    float shininess = 10.0f;
		    
		    app.setMaterial(new Material(color3, emissiveColour,
		        diffuseColour, specularColour, shininess));
		    
		    TransparencyAttributes tAtt =new TransparencyAttributes(TransparencyAttributes.NONE,0);
		    app.setTransparencyAttributes(tAtt);
		    
		    arrowShape=new Shape3D(pyramid,app);
		   
		    addChild(arrowShape);

		  }

	
	public void setArrow1(){

	
		double radius=0,height=0,coneHeight=0,coneRadius;

			height=scale.el[1];
			radius=.1*scale.el[0];
			coneHeight=.8*height;
			coneRadius=2*radius;

			Color3f color3=new  Color3f();
		
		    Color3f emissiveColour = new Color3f(.3f*color3.getX(), .3f*color3.getY(), .3f*color3.getZ());
		    Color3f specularColour = new Color3f(.1f, .1f, .1f);
		    Color3f diffuseColour =new Color3f(.8f*color3.getX(), .8f*color3.getY(), .8f*color3.getZ());
		    float shininess = 10.0f;
		    
		    Material material=new Material(color3, emissiveColour,
			        diffuseColour, specularColour, shininess);

		Appearance app=new Appearance();
		app.setMaterial(material);
		
		app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		


		 cone=new Cone() ;

			cone =new Cone((float)(coneRadius),(float)(coneHeight),app);
			
			
		 tgCone=new TransformGroup();

		transCone=new Transform3D();
	

		transCone.rotX(PI/2);
		

		transCone.setTranslation(new V3f(new Vect(0,0,(height))));	

		tgCone.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		tgCone.setTransform(transCone);
		tgCone.addChild(cone);
		
		axis=new Cylinder();

			axis=new Cylinder((float)(radius),(float)(height),app);
		 tgAx=new TransformGroup();
		 tgAx.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		 
		 transAx=new Transform3D();
		transAx.rotX(PI/2);
		

			transAx.setTranslation(new V3f(new Vect(0,0,height/2)));	
			
			tgAx.setTransform(transAx);
			
			tgAx.addChild(axis);
					
				
			addChild(tgCone);
			
			addChild(tgAx);

	}
	
	public void setArrow2D0(){
		
		Color3f color3=new Color3f(Color.red);
		Color3f color31=new Color3f(Color.red.darker());
	double base=0,height=0;


	base=.05*scale.el[0];
	height=.4*scale.el[1];
	
	TriangleArray  nail=new TriangleArray(3,TriangleArray.COORDINATES | TriangleArray.NORMALS);
	P3f[] vert=new P3f[3];
	V3f normal=new V3f(0,0,1);


		vert[0]=new P3f(0,height,0);
		vert[1]=new P3f(-base,0,0);
		vert[2]=new P3f(base,0,0);
		
	
	for(int i=0;i<3;i++){
		nail.setCoordinate(i,vert[i]);

		nail.setNormal(i,normal);
	}
	
	LineAttributes la=new LineAttributes(1.8f,LineAttributes.PATTERN_SOLID,false);
	ColoringAttributes ca=new  ColoringAttributes(color3, ColoringAttributes.SHADE_FLAT);
	Appearance app=new Appearance();
	

	ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

	app.setLineAttributes(la);
	app.setColoringAttributes(ca);
	
	
		arrowShape=new Shape3D(nail,app);
	
		addChild(arrowShape);



}
	
	public void setArrow2D1(){
		

		Color3f color3=new Color3f(Color.red);

		double base=0,height=0,hh;
		base=.1*scale.el[0]/*/(1+5*scale)*/;
		height=scale.el[1]/2;
		hh=1.3*base;

	TriangleArray  head=new TriangleArray(3,TriangleArray.COORDINATES | TriangleArray.NORMALS);
	QuadArray  tail=new QuadArray(4,QuadArray.COORDINATES | QuadArray.NORMALS );
	P3f[] vert=new P3f[7];
	V3f normal=new V3f(0,0,1);

		vert[0]=new P3f(0,height,0);
		vert[1]=new P3f(-.5*base,height-hh,0);
		vert[2]=new P3f(.5*base,height-hh,0);
		vert[3]=new P3f(-.2*base,height-hh,0);
		vert[4]=new P3f(-.2*base,0,0);
		vert[5]=new P3f(.2*base,0,0);
		vert[6]=new P3f(.2*base,height-hh,0);
	
	
	for(int i=0;i<3;i++){
		head.setCoordinate(i,vert[i]);
		head.setNormal(i,normal);
	}
	
		for(int i=0;i<4;i++){
			tail.setCoordinate(i,vert[i+3]);
			tail.setNormal(i,normal);
			
	
		}
		
		LineAttributes la=new LineAttributes(1.8f,LineAttributes.PATTERN_SOLID,false);
		ColoringAttributes ca=new  ColoringAttributes(color3, ColoringAttributes.SHADE_FLAT);
		Appearance app=new Appearance();

		ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

		app.setLineAttributes(la);
		app.setColoringAttributes(ca);

	
		arrowShape=new Shape3D(head,app);
		arrowShape.insertGeometry(tail,0);
	


}
	



public void setArrow2Dm1(){
	
	
		Color3f color3=new Color3f(Color.red);
	
	double base=0,height=0;

		base=.1*scale.el[0];
		height=.4*scale.el[1];
	
	 LineArray head=new LineArray(6, GeometryArray.COORDINATES );

	 P3f[] vert=new P3f[3];

		vert[0]=new P3f(0,height,0);
		vert[1]=new P3f(-.5*base,0,0);
		vert[2]=new P3f(.5*base,.0,0);

	
		head.setCoordinate(0,vert[0]);
		head.setCoordinate(1,vert[1]);
		head.setCoordinate(2,vert[1]);
		head.setCoordinate(3,vert[2]);
		head.setCoordinate(4,vert[2]);
		head.setCoordinate(5,vert[0]);

		LineAttributes la=new LineAttributes(1.8f,LineAttributes.PATTERN_SOLID,false);
		ColoringAttributes ca=new  ColoringAttributes(color3, ColoringAttributes.SHADE_FLAT);
		Appearance app=new Appearance();

		ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

		app.setLineAttributes(la);
		app.setColoringAttributes(ca);



		arrowShape=new Shape3D(head,app);
			
			TransformGroup arrow=new TransformGroup();
		
			arrow.addChild(arrowShape);
			
}

public void setArrow2Dm2(){
		
	Color3f color3=new Color3f(Color.red);

double base=0,height=0;

	base=.1*scale.el[0];
	height=.4*scale.el[1];

 LineArray head=new LineArray(6, GeometryArray.COORDINATES);

 P3f[] vert=new P3f[4];

	vert[0]=new P3f(0,height,0);
	vert[1]=new P3f(-.3*base,.9*height,0);
	vert[2]=new P3f(.3*base,.9*height,0);
	vert[3]=new P3f(0,0,0);


	head.setCoordinate(0,vert[0]);
	head.setCoordinate(1,vert[1]);
	head.setCoordinate(2,vert[0]);
	head.setCoordinate(3,vert[2]);
	head.setCoordinate(4,vert[0]);
	head.setCoordinate(5,vert[3]);


	LineAttributes la=new LineAttributes(1.8f,LineAttributes.PATTERN_SOLID,false);
	ColoringAttributes ca=new  ColoringAttributes(color3, ColoringAttributes.SHADE_FLAT);
	Appearance app=new Appearance();

	ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

	app.setLineAttributes(la);
	app.setColoringAttributes(ca);



	arrowShape=new Shape3D(head,app);
		
		TransformGroup arrow=new TransformGroup();
	
		arrow.addChild(arrowShape);
		
}

public void setColor(Color color){
	
	  Color3f color3=new  Color3f(color);

	if(mode==0 || mode==1 ){
		
		    
		    
		    Color3f emissiveColour = new Color3f(.2f*color3.getX(), .2f*color3.getY(), .2f*color3.getZ());
		    Color3f specularColour = new Color3f(.1f, .1f, .1f);
		    Color3f diffuseColour =new Color3f(.8f*color3.getX(), .8f*color3.getY(), .8f*color3.getZ());
		    float shininess = 10.0f;
		    
		    Material mat=new  Material(color3, emissiveColour,
			        diffuseColour, specularColour, shininess);
		    if(mode==0)
		    arrowShape.getAppearance().setMaterial(mat);
		    else{
		    	this. axis.getAppearance().setMaterial(mat);
		    	this. cone.getAppearance().setMaterial(mat);
		    }
		
	}
	
	else
	arrowShape.getAppearance().getColoringAttributes().setColor(color3);
}



}
