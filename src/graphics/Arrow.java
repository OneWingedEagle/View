package graphics;
import math.Mat;
import math.Vect;
import math.util;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;



public class Arrow extends TransformGroup{
	double mode;
	Shape3D arrowShape;
	public Vect scale=new Vect(1,1);
	public Arrow(int mode){
		this.mode=mode;	

	}
	
	private void initialize(){

	double base=0,height=0;


	base=.05*scale.el[0];
	height=.4*scale.el[1];

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
	
	
	public void setColor(Color color){
		
		  Color3f color3=new  Color3f(color);

		arrowShape.getAppearance().getColoringAttributes().setColor(color3);
	}


}
