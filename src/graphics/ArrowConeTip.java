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


public class ArrowConeTip extends Arrow {
	double mode;
	Shape3D arrowShape;

	public 	TransformGroup tgCone,tgAx;
	public Transform3D transCone,transAx;
	Cone cone;
	Cylinder axis;

	public Vect scale=new Vect(1,1);
	
	public ArrowConeTip(int mode){
		super(mode);
	}
	
	public void ArrowConeTipx(int mode){
		
		this.mode=mode;
		 this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	
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


public void setColor(Color color){
	
	  Color3f color3=new  Color3f(color);

	arrowShape.getAppearance().getColoringAttributes().setColor(color3);
}



}
