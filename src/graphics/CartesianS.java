package graphics;
import fem.EdgeSet;

import java.awt.Color;
import java.awt.Font;
import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
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


public class CartesianS extends TransformGroup{
	public Transform3D transX;
	public Shape3D[] surf;
	public double scale=1;
	
	public CartesianS(double[] boundary, Color color, Color color2, Color color1){	
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
		
		LineArray axis=new LineArray(8,LineArray.COORDINATES|LineArray.COLOR_3);
		
		P3f[] vn=new P3f[4];
		vn[0]=new P3f(-Xm,-Ym,0);
		vn[1]=new P3f(Xm,-Ym,0);
		vn[2]=new P3f(Xm,Ym,0);
		vn[3]=new P3f(-Xm,Ym,0);
		
		axis.setCoordinate(0,vn[0]);
		axis.setCoordinate(1,vn[1]);
		axis.setCoordinate(2,vn[1]);
		axis.setCoordinate(3,vn[3]);
		axis.setCoordinate(4,vn[3]);
		axis.setCoordinate(5,vn[2]);
		axis.setCoordinate(6,vn[2]);
		axis.setCoordinate(7,vn[0]);
		

		Color3f color3=new Color3f(color);
		for(int j=0;j<8;j++)
		axis.setColor(j,new Color3f(color3));
	
		Appearance facetApp = new Appearance();
		Appearance edgeApp = new Appearance();

		Color3f ambientColour = new Color3f(color3);
		Color3f emissiveColour = new Color3f(.0f, .0f, .0f);
		Color3f specularColour = new Color3f(.1f, .1f, .1f);
		Color3f diffuseColour =new Color3f(.8f*color3.getX(), .8f*color3.getY(), .8f*color3.getZ());
		float shininess = 10.0f;


		Material material=new Material(ambientColour, emissiveColour,
				diffuseColour, specularColour, shininess);

		facetApp.setMaterial(material);
		
		TransparencyAttributes tat=new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)(.7));	

		facetApp.setTransparencyAttributes(tat);
		
		
		QuadArray sf = new QuadArray(4,  GeometryArray.COORDINATES | GeometryArray.NORMALS |QuadArray.COLOR_3);

		V3f normal=new V3f(0,0,1);
		V3f normal2=new V3f(0,0,-1);
	
		for(int j=0;j<4;j++){
			sf.setCoordinate(j,vn[j]);
			sf.setNormal(j,normal);
			sf.setNormal(j,normal2);
		}
		
		Shape3D surf=new Shape3D(sf,facetApp);

		addChild(surf);		
		
	
	}
	


	}
