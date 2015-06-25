package graphics;
import fem.EdgeSet;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Line2D;

import javax.media.j3d.Appearance;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import math.Vect;
import math.util;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Text2D;


public class Cartesian extends TransformGroup{
	
	public Cartesian(double[] boundary, Color xColor, Color yColor, Color zColor){	
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		TransformGroup axes=new TransformGroup();
		double axisSize=util.max(boundary);
		float[] bound=new float[6];
		for(int i=0;i<6;i++)
			bound[i]=(float)(boundary[i]/axisSize);
		float Xm,Ym,Zm;
		Xm=(bound[1]-bound[0])/2;
		Ym=(bound[3]-bound[2])/2;
		Zm=(bound[5]-bound[4])/2;	

		float XTrans=(bound[0]+bound[1])/2;
		float YTrans=(bound[2]+bound[3])/2;
		float ZTrans=(bound[4]+bound[5])/2;	

		Transform3D tf=new Transform3D();
		tf.setTranslation(new Vector3f(XTrans,YTrans,ZTrans));
		this.setTransform(tf);
		LineArray axis=new LineArray(6,LineArray.COORDINATES|LineArray.COLOR_3);
		axis.setCoordinate(0,new Point3f(-Xm,0,0));
		axis.setCoordinate(1,new Point3f(Xm,0,0));
		axis.setCoordinate(2,new Point3f(0,-Ym,0));
		axis.setCoordinate(3,new Point3f(0,Ym,0));
		axis.setCoordinate(4,new Point3f(0,0,-Zm));
		axis.setCoordinate(5,new Point3f(0,0,Zm));

		axis.setColor(0,new Color3f(xColor));
		axis.setColor(1,new Color3f(xColor));
		axis.setColor(2,new Color3f(yColor));
		axis.setColor(3,new Color3f(yColor));
		axis.setColor(4,new Color3f(zColor));
		axis.setColor(5,new Color3f(zColor));
		
		LineAttributes la=new LineAttributes(1f,LineAttributes.PATTERN_SOLID,false);
		Appearance app=new Appearance();
		app.setLineAttributes(la);

		
		TransformGroup tgX=new TransformGroup();
		TransformGroup tgY=new TransformGroup();
		TransformGroup tgZ=new TransformGroup();

		
		Shape3D axisShape=new Shape3D(axis,app);
		addChild(axisShape);

		Font3D myF3D = new Font3D(new Font("Serif", Font.BOLD,
				 1), new FontExtrusion(new Line2D.Double(0, 0, .02,
				0)));
				
		Text3D X1 = new Text3D(myF3D, "X", new Point3f(0.0f,
				 0.0f, 0.0f));
		 
		X1.setString("X");
				
		Appearance appx = new Appearance();

		Color3f ambientColour = new Color3f(xColor);
		Color3f emissiveColour = new Color3f(xColor);
		Color3f specularColour = new Color3f(.0f, .0f, .0f);
		Color3f diffuseColour =new Color3f(.0f, .0f, .0f);
		float shininess = 10.0f;


		Material materialx=new Material(ambientColour, emissiveColour,
				diffuseColour, specularColour, shininess);

		appx.setMaterial(materialx);
		
		Shape3D X=new Shape3D(X1,appx);
		
		
		
		Appearance appy = new Appearance();

		Color3f ambientColourY = new Color3f(yColor);
		Color3f emissiveColourY = new Color3f(yColor.darker());



		Material materialy=new Material(ambientColourY, emissiveColourY,
				diffuseColour, specularColour, shininess);

		appy.setMaterial(materialy);
		
	//-----------------------
		
		Text3D Y1 = new Text3D(myF3D, "Y", new Point3f(0.0f,
				 0.0f, 0.0f));
		Shape3D Y=new Shape3D(Y1,appy);

	
		
		
		Appearance appz = new Appearance();

		Color3f ambientColourZ = new Color3f(zColor);
		Color3f emissiveColourZ = new Color3f(zColor.darker());



		Material materialz=new Material(ambientColourZ, emissiveColourZ,
				diffuseColour, specularColour, shininess);

		appz.setMaterial(materialz);
		
	//-----------------------
		
		Text3D Z1 = new Text3D(myF3D, "Z", new Point3f(0.0f,
				 0.0f, 0.0f));
		Shape3D Z=new Shape3D(Z1,appz);

		Transform3D transX=new Transform3D();
		Transform3D transY=new Transform3D();
		Transform3D transZ=new Transform3D();
		double PI=Math.PI;
		transX.rotX(PI/2);
		transY.rotX(PI/2);
		transZ.rotX(PI/2);
		
		transX.setTranslation(new Vector3f(1.02f*Xm,0,0));	
		transX.setScale(.1);
		transY.setTranslation(new Vector3f(0f,1.02f*Ym,0f));	
		transY.setScale(.1);
		transZ.setTranslation(new Vector3f(-.1f,0,1.02f*Zm));
		transZ.setScale(.1);
		
		tgX.setTransform(transX);
		tgY.setTransform(transY);
		tgZ.setTransform(transZ);
		
		tgX.addChild(X);
		tgY.addChild(Y);
		tgZ.addChild(Z);
		
		
			Color3f amb = new Color3f(.5f, .5f, 0.5f);
		   Color3f dif = new Color3f(.3f, 0.3f, 0.3f);
		   Color3f spec = new Color3f(.2f, 0.2f, 0.2f);
		   Color3f emis = new Color3f(0.4f, 0.2f, 0.1f);
		   amb = new Color3f(xColor);
		  Material material1=new Material(amb, dif, spec, emis, 1.0f);
		  amb = new Color3f(yColor);
		  Material material2=new Material(amb, dif, spec, emis, 1.0f);
		  amb = new Color3f(zColor);
		  Material material3=new Material(amb, dif, spec, emis, 1.0f);
		 
		  Appearance ap1=new Appearance();
		  ap1.setMaterial(material1);
		  Appearance ap2=new Appearance();
		  ap2.setMaterial(material2);
		  Appearance ap3=new Appearance();
		  ap3.setMaterial(material3);
		
		Cone cX =new Cone(.01f,.05f,ap1);
		Cone cY =new Cone(.01f,.05f,ap2);
		Cone cZ =new Cone(.01f,.05f,ap3);

		TransformGroup tgCx=new TransformGroup();
		TransformGroup tgCy=new TransformGroup();
		TransformGroup tgCz=new TransformGroup();
		
		Transform3D transCx=new Transform3D();
		Transform3D transCy=new Transform3D();
		Transform3D transCz=new Transform3D();
			
		
		transCx.rotZ(-PI/2);
		transCz.rotX(PI/2);

		transCx.setTranslation(new Vector3f(Xm,0,0));
		transCy.setTranslation(new Vector3f(0f,Ym,0f));
		transCz.setTranslation(new Vector3f(0f,0f,Zm));
		
	
		tgCx.setTransform(transCx);
		tgCy.setTransform(transCy);
		tgCz.setTransform(transCz);
		
		
		tgCx.addChild(cX);
		tgCy.addChild(cY);
		tgCz.addChild(cZ);

		axes=new TransformGroup();
		axes.addChild(tgX);
		axes.addChild(tgY);
		axes.addChild(tgZ);
		axes.addChild(tgCx);
		axes.addChild(tgCy);
		axes.addChild(tgCz);
		

		
		addChild(axes);		
	
		


	}
	


	}
