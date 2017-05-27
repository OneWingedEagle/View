package graphics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.*;
import math.IntVect;
import math.Mat;
import math.Vect;
import math.util;
import fem.EdgeSet;
import fem.Model;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.IndexedLineArray;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Text2D;



public class SurfFacets extends TransformGroup{

	private Shape3D surfFacets;
	private Shape3D vectField,arrowEdges;
	private Shape3D surfEdges;
	private int nElements,nFirst,nSurfEdges,nSurfQuads,nSurf3angs,elCode,nNodesT,nNodesQ,nArrows;
	private IndexedQuadArray faceth;
	private IndexedQuadArray arrowBase;
	private IndexedTriangleArray facet3angh;
	private TriangleArray facet3ang;
	private IndexedTriangleArray arrowFace;
	private LineArray allEdge;
	private IndexedLineArray allEdgeh;
	private IndexedLineArray arrows;
	public int[] surfVertNumb;
	public boolean[] surfElements;
	public double[] nodalVals;
	public double nodalScalarScale=1,defScale;
	public boolean showRegion,setRegion,showVectField,showRegEdge,showRegFace,allNodesVect,nodalPainted;
	private RenderingAttributes facetRA,edgeRA,fieldRA;
	
	public Arrow[] arrow;
	Transform3D[] trans;
	public int nRegNodes,mode,vectMode;
	public Vect[] V;
	public double vScale=1;
	//public int[] neNumb;
	private PolygonAttributes pa;

	int ii,nArrowHeadDivs;
	int dim,nr,arrMode,fieldMode;
	public int[][] surface3angNodes;


	public SurfFacets(){}

	public SurfFacets(Model model, int ir,Color color,double transp){
		this.dim=model.dim;
		this.nr=ir;
	
		this.showRegion=true;
		this.showRegFace=true;
		this.showRegEdge=true;
		

		pa=new PolygonAttributes();
		pa.setPolygonOffsetFactor(1.f);
		pa.setPolygonOffset(1e-8f);
		
		facetRA = new RenderingAttributes( );
		facetRA.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
		   
		facetRA.setVisible(true);
		
		edgeRA = new RenderingAttributes( );
		edgeRA.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
		   
		edgeRA.setVisible(true);
		
		fieldRA = new RenderingAttributes( );
		fieldRA.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
		   
		fieldRA.setVisible(true);
		
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		this.setCapability(Group.ALLOW_CHILDREN_WRITE);
		this.nr=ir;
		
		

		util.pr("Now drawing mesh...");
		this.elCode=model.elCode;
		if(this.elCode==0) setFacets3ang(model,ir,color,transp);
		else if(this.elCode==1) setFacetsQuad(model,ir,color,transp);
		else if(this.elCode==2) setFacetsTetra(model,ir,color,transp);
		else if(this.elCode==3) setFacetsWedge(model,ir,color,transp);
		else if(this.elCode==4) setFacetsHexa(model,ir,color,transp);
		else if(this.elCode==5) setFacetsPyramid(model,ir,color,transp);
		
		
		  
		  
		
		
	}


	public void setFacets3ang(Model model, int ir,Color color, double transp){

	
		 this.defScale=model.defScale;

		int nElVert=model.nElVert;
		this.nFirst=model.region[ir].getFirstEl();
		this.nElements=model.region[ir].getLastEl()-this.nFirst+1;

		if(this.nElements==0) return;

		Vect[] v=new Vect[nElVert];
		int[] vertNumb;

		this.surfVertNumb=model.getRegNodes(ir);
		int[] map=new int[model.numberOfNodes+1];

		int nNodes=this.surfVertNumb.length;
		P3f[] coords=new P3f[nNodes];
		for(int j=0;j<nNodes;j++){
			map[this.surfVertNumb[j]]=j;
			coords[j]=new P3f(model.node[this.surfVertNumb[j]].getCoord().v3());

		}



		int[] coordIndices=new int[3*this.nElements];

		int ix=0;
		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++)
		{
			vertNumb=model.element[i].getVertNumb();

			for(int j=0;j<nElVert;j++){
				v[j]=model.node[vertNumb[j]].getCoord();
				if(model.node[vertNumb[j]].u!=null)
					v[j]= v[j].add(model.node[vertNumb[j]].u.times(this.defScale));

				coordIndices[3*ix+j]=map[vertNumb[j]];
			}

			ix++;

		}

		this.nSurf3angs=this.nElements;

		
		util.pr("region: ir "+ir+" , nSurfEdges: "+nSurf3angs);

		
		this.facet3angh = new IndexedTriangleArray(nNodes,	GeometryArray.COORDINATES  | 
				GeometryArray.COLOR_3,3*this.nElements);
		this.facet3angh.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		this.facet3angh.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);




		Color3f[] colors= new Color3f[nNodes];


		for(int i=0;i<nNodes;i++){
			colors[i]=new Color3f(Color.RED);

		}

		this.facet3angh.setCoordinates(0, coords);
		this.facet3angh.setColors(0, colors);
		this.facet3angh.setCoordinateIndices(0, coordIndices);
		this.facet3angh.setColorIndices(0, coordIndices);
		//======


				Appearance facetApp = new Appearance();
				Appearance edgeApp = new Appearance();

				  facetApp.setRenderingAttributes(facetRA);
				  edgeApp.setRenderingAttributes(edgeRA);

				Color3f color3=new Color3f(color);

				int[][] xx=new int[0][0];

				EdgeSet eds=new EdgeSet();
				int[][] edgeNodes=eds.getEdgeNodes(model,ir, ir, xx);

				this.nSurfEdges=edgeNodes.length-1;

				//  color3=new Color3f(.75f,.95f,.95f);
				// color3=new Color3f(.25f,.25f,.25f);
				color3=new Color3f(color.darker());

				this.allEdgeh=new IndexedLineArray(this.surfVertNumb.length,GeometryArray.COORDINATES ,2*this.nSurfEdges);

				this.allEdgeh.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
				this.allEdgeh.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);


				this.allEdgeh.setCoordinates(0, coords);


				coordIndices=new int[2*this.nSurfEdges];
				ix=0;
				for(int i=0; i<this.nSurfEdges;i++){

					coordIndices[2*ix]=map[edgeNodes[i+1][0]];
					coordIndices[2*ix+1]=map[edgeNodes[i+1][1]];
					ix++;

				}

				this.allEdgeh.setCoordinateIndices(0, coordIndices);


		
				Color3f edCol=new Color3f(color.darker().darker().darker());

				LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);
				ColoringAttributes ca=new  ColoringAttributes(edCol, ColoringAttributes.SHADE_GOURAUD);

				ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);


				edgeApp.setColoringAttributes(ca);
				

				edgeApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);

				edgeApp.setLineAttributes(la);


				TransparencyAttributes t_attr1,t_attr2,t_attr3;
				t_attr1 =new TransparencyAttributes(TransparencyAttributes.NONE,0f);
				t_attr2 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);		
				t_attr3 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)(.8*transp));	

				if(!model.region[ir].getMaterial().startsWith("air")){

					facetApp.setTransparencyAttributes(t_attr1);
					edgeApp.setTransparencyAttributes(t_attr1);
				}
				else{

					facetApp.setTransparencyAttributes(t_attr2);
					edgeApp.setTransparencyAttributes(t_attr3);

				}

				facetApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
				
				facetApp.setPolygonAttributes(pa);
				
				edgeApp.setPolygonAttributes(pa);
				
				this.surfFacets=new Shape3D(this.facet3angh,facetApp);
				this.surfEdges=new Shape3D(this.allEdgeh,edgeApp);

				this.surfEdges.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
				
				showFacets(true);
				showEdges(true);


	}

	public void setFacetsQuad(Model model, int ir,Color color,double transp){


		 this.defScale=model.defScale;

		int nElVert=model.nElVert;
		this.nFirst=model.region[ir].getFirstEl();
		this.nElements=model.region[ir].getLastEl()-this.nFirst+1;

		if(this.nElements==0) return;

		int[] vertNumb;

		this.surfVertNumb=model.getRegNodes(ir);
		int[] map=new int[model.numberOfNodes+1];

		int nNodes=this.surfVertNumb.length;

		P3f[] coords=new P3f[nNodes];
		for(int j=0;j<nNodes;j++){
			map[this.surfVertNumb[j]]=j;

			Vect  v=model.node[this.surfVertNumb[j]].getCoord();
			if(model.node[this.surfVertNumb[j]].u!=null)
				v= v.add(model.node[this.surfVertNumb[j]].u.times(this.defScale));
			coords[j]=new P3f(v.v3());

		}



		int[] coordIndices=new int[4*this.nElements];

		int ix=0;
		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++)
		{
			vertNumb=model.element[i].getVertNumb();

			for(int j=0;j<nElVert;j++){


				coordIndices[4*ix+j]=map[vertNumb[j]];
			}

			ix++;

		}

		this.nSurfQuads=this.nElements;

		this.faceth = new IndexedQuadArray(nNodes,	GeometryArray.COORDINATES  | GeometryArray.COLOR_3/*| IndexedQuadArray.NORMALS,*/,4*this.nElements);
		this.faceth.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		this.faceth.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);




		Color3f[] colors= new Color3f[nNodes];


		for(int i=0;i<nNodes;i++){
			colors[i]=new Color3f(Color.RED);

		}

		this.faceth.setCoordinates(0, coords);
		this.faceth.setColors(0, colors);
		this.faceth.setCoordinateIndices(0, coordIndices);
		this.faceth.setColorIndices(0, coordIndices);
		//======


				Appearance facetApp = new Appearance();
				Appearance edgeApp = new Appearance();
				
				  facetApp.setRenderingAttributes(facetRA);
				  edgeApp.setRenderingAttributes(edgeRA);

				Color3f color3=new Color3f(color);


				int[][] xx=new int[0][0];

				EdgeSet eds=new EdgeSet();
				int[][] edgeNodes=eds.getEdgeNodes(model,ir, ir, xx);

				this.nSurfEdges=edgeNodes.length-1;

				//  color3=new Color3f(.75f,.95f,.95f);
				// color3=new Color3f(.25f,.25f,.25f);
				color3=new Color3f(color.darker());

				this.allEdgeh=new IndexedLineArray(this.surfVertNumb.length,GeometryArray.COORDINATES ,2*this.nSurfEdges);

				this.allEdgeh.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
				this.allEdgeh.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);



				this.allEdgeh.setCoordinates(0, coords);


				coordIndices=new int[2*this.nSurfEdges];
				ix=0;
				for(int i=0; i<this.nSurfEdges;i++){

					coordIndices[2*ix]=map[edgeNodes[i+1][0]];
					coordIndices[2*ix+1]=map[edgeNodes[i+1][1]];
					ix++;

				}

				
				
				this.allEdgeh.setCoordinateIndices(0, coordIndices);

				LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);
				
				
				Color3f edCol=new Color3f(color.darker().darker());

				ColoringAttributes ca=new  ColoringAttributes(edCol, ColoringAttributes.SHADE_GOURAUD);

				ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);


				edgeApp.setColoringAttributes(ca);
				

				edgeApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);

				edgeApp.setLineAttributes(la);


				edgeApp.setLineAttributes(la);


				TransparencyAttributes t_attr1,t_attr2,t_attr3;
				t_attr1 =new TransparencyAttributes(TransparencyAttributes.NONE,0f);
				t_attr2 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);		
				t_attr3 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)(.8*transp));	

				if(!model.region[ir].getMaterial().startsWith("air")){

					facetApp.setTransparencyAttributes(t_attr1);
					edgeApp.setTransparencyAttributes(t_attr1);
				}
				else{

					facetApp.setTransparencyAttributes(t_attr2);
					edgeApp.setTransparencyAttributes(t_attr3);

				}

				edgeApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
				facetApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
				
				facetApp.setPolygonAttributes(pa);
				
				edgeApp.setPolygonAttributes(pa);


				this.surfFacets=new Shape3D(this.faceth,facetApp);
				this.surfEdges=new Shape3D(this.allEdgeh,edgeApp);

				this.surfEdges.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
				this.surfEdges.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
				//	this.surfEdges.setCapability(Shape3D.ALLOW);


				showFacets(true);
				showEdges(true);


	}




	public void setFacetsTetra(Model model, int ir,Color color,double transp){

		 this.defScale=model.defScale;

		double offFactor;
		if(model.minEdgeLength>1e-2)
			offFactor=3e-4;
		else
			offFactor=3e-1*model.minEdgeLength;
		
		offFactor=0;
		
		

		this.nFirst=model.region[ir].getFirstEl();
		this.nElements=model.region[ir].getLastEl()-this.nFirst+1;
		if(this.nElements==0) return;
		int nEdge=model.numberOfEdges;

		int ns=4;
		IntVect[] edgeElement=new IntVect[nEdge+1];
		for(int i=1;i<=nEdge;i++)
			edgeElement[i]=new IntVect(ns);


		boolean[] edgeCounted=new boolean[nEdge+1];
		int[] indx=new int[nEdge+1];
		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
			int[] edgeNumb=model.element[i].getEdgeNumb();
			for(int j=0;j<model.nElEdge;j++){
				int ne=edgeNumb[j];

							
				if(indx[ne]==edgeElement[ne].length-1)
					edgeElement[ne].extend(ns);

				edgeElement[ne].el[(indx[ne]++)]=i;
				edgeCounted[ne]=true;
			}			
		}

		int nx=0;
		int k;
		boolean[] onSurf=new boolean[nEdge+1];

		for(int i=1;i<=nEdge;i++){
			if(!edgeCounted[i]) continue;
			k=0;
			for(int j=0;j<indx[i];j++)
				if(edgeElement[i].el[j]>0)
					k++;
			int[] nn=new int[2*indx[i]];
			int jx=0;
			int n1=model.edge[i].endNodeNumber[0];
			for(int j=0;j<indx[i];j++){
				int[] edgeNumb=model.element[edgeElement[i].el[j]].getEdgeNumb();
				for(int p=0;p<model.nElEdge;p++){
					int ep=edgeNumb[p];
					if(ep==i) continue;
					if(model.edge[ep].endNodeNumber[0]==n1) 
						nn[jx++]=model.edge[ep].endNodeNumber[1];
					else
						if(model.edge[ep].endNodeNumber[1]==n1) 
							nn[jx++]=model.edge[ep].endNodeNumber[0];
				}

			}

			Arrays.sort(nn);

			int q=0;
			for(int p=1;p<nn.length;p++)
				if(nn[p]!=nn[p-1]) q++;

			if(q==indx[i]){
				onSurf[i]=true;
				nx++;
			}

		}

		this.nSurfEdges=nx;

		int[] onSurfEdgeNumber=new int[nx+1];
		nx=0;
		for(int i=1;i<=nEdge;i++)
			if(onSurf[i])
				onSurfEdgeNumber[++nx]=i;

		int[][] surface3angNodes1=new int[1+3*this.nSurfEdges][3];
		int ix=0;		
		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){


			int[] vertNumb=model.element[i].getVertNumb();
			int[] edgeNumb=model.element[i].getEdgeNumb();
			if(onSurf[edgeNumb[0]] && onSurf[edgeNumb[1]] && onSurf[edgeNumb[2]])
			{
				surface3angNodes1[ix][0]=vertNumb[0];
				surface3angNodes1[ix][1]=vertNumb[2];
				surface3angNodes1[ix][2]=vertNumb[1];

				ix++;
			}
			if(onSurf[edgeNumb[0]] && onSurf[edgeNumb[3]]  && onSurf[edgeNumb[4]])
			{
				surface3angNodes1[ix][0]=vertNumb[0];
				surface3angNodes1[ix][1]=vertNumb[1];
				surface3angNodes1[ix][2]=vertNumb[3];

				ix++;
			}
			if(onSurf[edgeNumb[1]] && onSurf[edgeNumb[4]]  && onSurf[edgeNumb[5]])
			{
				surface3angNodes1[ix][0]=vertNumb[1];
				surface3angNodes1[ix][1]=vertNumb[2];
				surface3angNodes1[ix][2]=vertNumb[3];

				ix++;
			}

			if(onSurf[edgeNumb[2]] && onSurf[edgeNumb[5]]  && onSurf[edgeNumb[3]])
			{
				surface3angNodes1[ix][0]=vertNumb[2];
				surface3angNodes1[ix][1]=vertNumb[0];
				surface3angNodes1[ix][2]=vertNumb[3];

				ix++;
			}



		}




		this.nSurf3angs=ix;


		util.pr("region: ir "+ir+" , nSurfEdges: "+nx+" , nSurf3angs: "+ix);

		this.surface3angNodes=new int[ix][3];

		for(int i=0;i<ix;i++)
			for(int j=0;j<3;j++)
				this.surface3angNodes[i][j]=surface3angNodes1[i][j];

		surface3angNodes1=null;

		this.facet3ang = new TriangleArray(3*this.nSurf3angs,
				GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.COLOR_3);	
		
		this.facet3ang.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		this.facet3ang.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
		this.facet3ang.setCapability(GeometryArray.ALLOW_NORMAL_WRITE);

		P3f[][] vertex=new P3f[this.nSurf3angs][3];

		V3f[][] normal=new V3f[this.nSurf3angs][3];
		for(int i=0;i<this.nSurf3angs;i++){
			int[] nn=new int[3];
			Vect[] v=new Vect[3];
			for(int j=0;j<3;j++){
				nn[j] =this.surface3angNodes[i][j];

				v[j] =model.node[nn[j]].getCoord();
				if(model.node[nn[j]].u!=null)
					v[j]= v[j].add(model.node[nn[j]].u.times(this.defScale));
			}

			Vect v1=v[1].sub( v[0]);
			Vect v2=v[2].sub( v[0]);
			Vect vn=v2.cross(v1);
			vn.normalize();
			V3f nOutward=new V3f(vn);
			for(int j=0;j<3;j++){

				Vect dvn=vn.times(offFactor);
				Vect vp=v[j].add(dvn);

				vertex[i][j]=new P3f(vp);

				normal[i][j]=nOutward;
			}		
		}



		Color3f color3=new Color3f(color);

		for(int i=0;i<this.nSurf3angs;i++){
			for(int j=0;j<3;j++){
				this.facet3ang.setCoordinate(3*i+j,vertex[i][j]);
				this.facet3ang.setNormal(3*i+j,normal[i][j]);
				this.facet3ang.setColor(3*i+j,color3);

			}
		}

		Appearance facetApp = new Appearance();
		Appearance edgeApp = new Appearance();
		
		
		color3=new Color3f(color);
		Color3f edCol=new Color3f(color.darker().darker());

		
		ColoringAttributes ca=new  ColoringAttributes(edCol, ColoringAttributes.SHADE_GOURAUD);
		edgeApp.setColoringAttributes(ca);
		ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

		
		facetApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		facetApp.setRenderingAttributes(edgeRA);
		edgeApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
	   	edgeApp.setRenderingAttributes(edgeRA);


		Color3f ambientColour = new Color3f(color3);
		Color3f emissiveColour = new Color3f(.0f, .0f, .0f);
		Color3f specularColour = new Color3f(.1f, .1f, .1f);
		Color3f diffuseColour =new Color3f(.8f*color3.getX(), .8f*color3.getY(), .8f*color3.getZ());
		float shininess = 10.0f;


		Material material=new Material(ambientColour, emissiveColour,
				diffuseColour, specularColour, shininess);
		
		material.setCapability(Material.ALLOW_COMPONENT_WRITE);


		facetApp.setMaterial(material);

		
		facetApp.setPolygonAttributes(pa);
		
		edgeApp.setPolygonAttributes(pa);


		this.allEdge=new LineArray(2*this.nSurfEdges,GeometryArray.COORDINATES |GeometryArray.COLOR_3);
		this.allEdge.setCapability(GeometryArray.ALLOW_COLOR_WRITE);

		for(int i=0; i<this.nSurfEdges;i++)
		{

			int n1=model.edge[onSurfEdgeNumber[i+1]].endNodeNumber[0];
			Vect v1=model.node[n1].getCoord();
			if(model.node[n1].u!=null)
				v1= v1.add(model.node[n1].u.times(this.defScale));

			int n2=model.edge[onSurfEdgeNumber[i+1]].endNodeNumber[1];
			Vect v2=model.node[n2].getCoord();
			if(model.node[n2].u!=null)
				v2= v2.add(model.node[n2].u.times(this.defScale));

			this.allEdge.setCoordinate(2*i,new P3f(v1));
			this.allEdge.setCoordinate(2*i+1,new P3f(v2));
			this.allEdge.setColor(2*i,edCol);
			this.allEdge.setColor(2*i+1,edCol);
		}

		LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);

		TransparencyAttributes t_attr1,t_attr2,t_attr3;
		t_attr1 =new TransparencyAttributes(TransparencyAttributes.NONE,.0f);
		t_attr2 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);		
		t_attr3 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)(.8*transp));	

		edgeApp.setLineAttributes(la);
		if(!model.region[ir].getMaterial().startsWith("air")){

			facetApp.setTransparencyAttributes(t_attr1);
			edgeApp.setTransparencyAttributes(t_attr1);
		}
		else{

			facetApp.setTransparencyAttributes(t_attr2);
			edgeApp.setTransparencyAttributes(t_attr3);

		}
		edgeApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		facetApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);

	  	edgeApp.setRenderingAttributes(edgeRA);
	    facetApp.setRenderingAttributes(facetRA);


		this.surfFacets=new Shape3D(this.facet3ang,facetApp);
		this.surfEdges=new Shape3D(this.allEdge,edgeApp);
		
	    
		this.surfEdges.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		this.surfFacets.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		this.surfEdges.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
		this.surfFacets.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);

		showFacets(true);
		showEdges(true);

	}


	public void setFacetsHexa(Model model, int ir,Color color,double transp){

		 this.defScale=model.defScale;

		this.nFirst=model.region[ir].getFirstEl();
		this.nElements=model.region[ir].getLastEl()-this.nFirst+1;
		if(this.nElements==0) return;
		int nEdge=model.numberOfEdges;

		int ns=6;
		IntVect[] edgeElement=new IntVect[nEdge+1];
		for(int i=1;i<=nEdge;i++)
			edgeElement[i]=new IntVect(ns);


		boolean[] edgeCounted=new boolean[nEdge+1];
		int[] indx=new int[nEdge+1];
		
		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
			
			int[] edgeNumb=model.element[i].getEdgeNumb();

			for(int j=0;j<model.nElEdge;j++){
				int ne=edgeNumb[j];
				if(indx[ne]==edgeElement[ne].length-1)
					edgeElement[ne].extend(ns);

				edgeElement[ne].el[(indx[ne]++)]=i;
				edgeCounted[ne]=true;
			}			
		}

		
		int nx=0;
		int k;
		boolean[] onSurf=new boolean[nEdge+1];

		for(int i=1;i<=nEdge;i++){
			if(!edgeCounted[i]) continue;
			k=0;
			for(int j=0;j<indx[i];j++)
				if(edgeElement[i].el[j]>0)
					k++;
			int[] nn=new int[2*indx[i]];
			int jx=0;
			int n1=model.edge[i].endNodeNumber[0];
			for(int j=0;j<indx[i];j++){
				int[] edgeNumb=model.element[edgeElement[i].el[j]].getEdgeNumb();
				for(int p=0;p<model.nElEdge;p++){
					int ep=edgeNumb[p];
					if(ep==i) continue;
					if(model.edge[ep].endNodeNumber[0]==n1) {
					nn[jx++]=model.edge[ep].endNodeNumber[1];
					}
					else{
						if(model.edge[ep].endNodeNumber[1]==n1) 
							nn[jx++]=model.edge[ep].endNodeNumber[0];
											}
				}

			}
			Arrays.sort(nn);
			int q=0;
			for(int p=1;p<nn.length;p++)
				if(nn[p]!=nn[p-1]) q++;

		if(q==indx[i])
			
			{
			
				onSurf[i]=true;
				nx++;
			}

		}


		this.nSurfEdges=nx;
		
		if(this.nSurfEdges==0) return;
		
		int[] onSurfEdgeNumber=new int[nx+1];
		nx=0;
		for(int i=1;i<=nEdge;i++)
			if(onSurf[i])
				onSurfEdgeNumber[++nx]=i;

		int[][] surfaceQuadNodes1=new int[1+2*this.nSurfEdges][4];

		int[][] edgeLocalNumb={{0,1,4,5},{0,2,8,9},{1,3,10,11},{2,3,6,7},{4,6,8,10},{5,7,9,11}};
		int[][] nodeLocalNumb={{4,7,6,5},{3,2,6,7},{0,4,5,1},{0,1,2,3},{1,5,6,2},{0,3,7,4}};


		int ix=0;		
		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){


			int[] vertNumb=model.element[i].getVertNumb();
			int[] edgeNumb=model.element[i].getEdgeNumb();

			for(int is=0;is<ns;is++){
				boolean b=true;
				for(int j=0;j<4;j++)
					if(!onSurf[edgeNumb[edgeLocalNumb[is][j]]]) {
						b=false;
						break;
					}

				if(b){
					for(int j=0;j<4;j++)
						surfaceQuadNodes1[ix][j]=vertNumb[nodeLocalNumb[is][j]];

					ix++;
				}

			}

		}



		this.nSurfQuads=ix;
		

		util.pr("region: ir "+ir+" , nSurfEdges: "+nx+" , nSurfQuads: "+ix);

		if(this.nSurfQuads==0) return;

		
		int[] coordIndices=new int[4*this.nSurfQuads];
		
		int[] map=new int[model.numberOfNodes+1];
	
		boolean[] nc=new boolean[model.numberOfNodes+1];
		int[] surfVertNumb1=new int[4*this.nSurfQuads];
		int p=0;
		for(int i=0;i<this.nSurfQuads;i++)
			for(int j=0;j<4;j++){

				if(!nc[surfaceQuadNodes1[i][j]]){
					
					surfVertNumb1[p]=surfaceQuadNodes1[i][j];
					map[surfVertNumb1[p]]=p;		
					nc[surfaceQuadNodes1[i][j]]=true;
					
					p++;

				}
			}

		surfElements=new boolean[this.nElements];

	int kx=0;
	for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
		
			int[] vertNumb=model.element[i].getVertNumb();

			for(int j=0;j<vertNumb.length;j++){
				int nn=vertNumb[j];
				if(nc[nn]){
					surfElements[kx]=true;	
					break;
				}
			}
			
			kx++;
	}
	



		int nNodes=p;
		this.nNodesQ=p;
		
		int nCorners=4*this.nSurfQuads;

		this.surfVertNumb=new int[nNodes];

		p=0;

		for(int i=0;i<this.nSurfQuads;i++)
			for(int j=0;j<4;j++){

				coordIndices[p]=map[surfaceQuadNodes1[i][j]];
				p++;

			}
		

		for(int i=0;i<nNodes;i++)
			this.surfVertNumb[i]=surfVertNumb1[i];


		P3f[] coords=new P3f[nNodes];

		for(int i=0;i<nNodes;i++){
			int nnx=this.surfVertNumb[i];

			Vect v=model.node[nnx].getCoord();
			
			if(model.node[nnx].u!=null)
				v= v.add(model.node[nnx].u.times(this.defScale));
			coords[i]=new P3f(v);
		}

		this.faceth = new IndexedQuadArray(nNodes,  GeometryArray.COORDINATES
				| GeometryArray.NORMALS |GeometryArray.COLOR_3,nCorners);
		this.faceth.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		this.faceth.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
		this.faceth.setCapability(GeometryArray.ALLOW_NORMAL_WRITE);


	
		int[] normalIndices1 =new int[4*this.nSurfQuads];

		V3f[] normals1=new V3f[this.nSurfQuads];
		p=0;
		for(int i=0;i<this.nSurfQuads;i++){
			int[] nn=new int[4];
			Vect[] v=new Vect[4];
			for(int j=0;j<4;j++){

				nn[j] =surfaceQuadNodes1[i][j];

				normalIndices1[p]=i;

				if(j<3){
					v[j] =model.node[nn[j]].getCoord();

					if(model.node[nn[j]].u!=null)
						v[j]= v[j].add(model.node[nn[j]].u.times(this.defScale));
				}

				p++;
			}

			Vect v1=v[1].sub( v[0]);
			Vect v2=v[2].sub( v[1]);
			Vect vn=v1.cross(v2);

			if(vn.norm()==0)
				vn=new Vect(0,0,1);
			else
				vn.normalize();

			/* for(int m=0;m<3;m++)
				 vn.el[m]=(int)(vn.el[m]*100)*0.01;*/
			 
			V3f nOutward=new V3f(vn);
			normals1[i]=nOutward;

		}
		


		List<V3f> list=new ArrayList<V3f>();
		
		for(int i=0;i<this.nSurfQuads;i++){
				list.add(normals1[i]);
		}
		

		Set<V3f> set = new HashSet<V3f>(list);
		
		ArrayList<V3f> un = new ArrayList<V3f>(set);
		
	
		int[] key=new int[this.nSurfQuads];
		
		for(int i=0;i<this.nSurfQuads;i++){
			for(int j=0;j<un.size();j++){
				if(un.get(j).equals(list.get(i))){
					key[i]=j;
					break;
				}
			}
		}		
		
		V3f[] normals=new V3f[set.size()];
		for(int j=0;j<un.size();j++){
			normals[j]=un.get(j);
		}
		
		int[] normalIndices =new int[4*this.nSurfQuads];
		for(int i=0;i<normalIndices.length;i++)
			normalIndices[i]=key[normalIndices1[i]];
		
			Color3f cl3=new Color3f(color);
			Color3f[] colors=new Color3f[nNodes];
			for(int i=0;i<nNodes;i++){
				colors[i]=cl3;
			}


			
			this.faceth.setCoordinates(0, coords);
			this.faceth.setNormals(0, normals);
			this.faceth.setColors(0, colors);

			this.faceth.setCoordinateIndices(0, coordIndices);
			this.faceth.setNormalIndices(0, normalIndices);
			this.faceth.setColorIndices(0, coordIndices);

		
		Color3f color3=new Color3f(color);


		Appearance facetApp = new Appearance();
		Appearance edgeApp = new Appearance();
		facetApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		facetApp.setRenderingAttributes(edgeRA);
		edgeApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
	   	edgeApp.setRenderingAttributes(edgeRA);

		Color3f ambientColour = new Color3f(color3);
		Color3f emissiveColour = new Color3f(0,0,0);
		Color3f specularColour = new Color3f(.1f,.1f,.1f);
		Color3f diffuseColour =new Color3f(.8f*color3.getX(), .8f*color3.getY(), .8f*color3.getZ());
		float shininess = 10.0f;
		

		Material material=new Material(ambientColour, emissiveColour,
				diffuseColour, specularColour, shininess);
		material.setCapability(Material.ALLOW_COMPONENT_WRITE);

		facetApp.setMaterial(material);


	
		color3=new Color3f(color);
		Color3f edCol=new Color3f(color.darker().darker().darker());


		//==========

		this.allEdgeh=new IndexedLineArray(this.surfVertNumb.length,GeometryArray.COORDINATES ,2*this.nSurfEdges);

		this.allEdgeh.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		this.allEdgeh.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);


		this.allEdgeh.setCoordinates(0, coords);


		coordIndices=new int[2*this.nSurfEdges];
		 p=0;
		for(int i=0; i<this.nSurfEdges;i++){

			int[] endNodes=model.edge[onSurfEdgeNumber[i+1]].endNodeNumber;
			if(map[endNodes[0]]>-1 && map[endNodes[1]]>-1){

			coordIndices[2*p]=map[endNodes[0]];
			coordIndices[2*p+1]=map[endNodes[1]];
			p++;
			}

		}
		
		
		this.allEdgeh.setCoordinateIndices(0, coordIndices);


		
		LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);

		TransparencyAttributes t_attr1,t_attr2,t_attr3;
		t_attr1 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,.0f);
		t_attr2 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);		
		t_attr3 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)(.8*transp));	

		edgeApp.setLineAttributes(la);
		
		Color3f ambientColour2 = new Color3f(color3);
		Color3f emissiveColour2 = new Color3f(0,0,0);
		Color3f specularColour2 = new Color3f(.1f,.1f,.1f);
		Color3f diffuseColour2 =new Color3f(.8f*color3.getX(), .8f*color3.getY(), .8f*color3.getZ());
		float shininess2 = 10.0f;
		
		Material edgeMat=new Material(ambientColour2, emissiveColour2,
				diffuseColour2, specularColour2, shininess2);
		edgeMat.setCapability(Material.ALLOW_COMPONENT_WRITE);
		edgeApp.setMaterial(edgeMat);

		
		if(!model.region[ir].getMaterial().startsWith("air")){

			facetApp.setTransparencyAttributes(t_attr1);
			edgeApp.setTransparencyAttributes(t_attr1);
		}
		else{

			facetApp.setTransparencyAttributes(t_attr2);
			edgeApp.setTransparencyAttributes(t_attr3);

		}

		ColoringAttributes ca=new  ColoringAttributes(edCol, ColoringAttributes.SHADE_GOURAUD);


		edgeApp.setColoringAttributes(ca);
		ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

	   	edgeApp.setRenderingAttributes(edgeRA);
	    facetApp.setRenderingAttributes(facetRA);

	    
		edgeApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		facetApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);

		


		facetApp.setPolygonAttributes(pa);
		
		edgeApp.setPolygonAttributes(pa);
		

		this.surfFacets=new Shape3D(this.faceth,facetApp);
		this.surfEdges=new Shape3D(this.allEdgeh,edgeApp);
		this.surfEdges.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		this.surfFacets.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		this.surfEdges.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
		this.surfFacets.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
		
		showFacets(true);
		showEdges(true);


	}



	public void setFacetsPyramid(Model model, int ir,Color color,double transp){


		 this.defScale=model.defScale;
		double offFactor;
		if(model.minEdgeLength>1e-2)
			offFactor=3e-4;
		else
			offFactor=3e-1*model.minEdgeLength;
		this.nFirst=model.region[ir].getFirstEl();
		this.nElements=model.region[ir].getLastEl()-this.nFirst+1;
		if(this.nElements==0) return;
		int nEdge=model.numberOfEdges;

		int ns=6;
		IntVect[] edgeElement=new IntVect[nEdge+1];
		for(int i=1;i<=nEdge;i++)
			edgeElement[i]=new IntVect(ns);

		boolean[] edgeCounted=new boolean[nEdge+1];
		int[] indx=new int[nEdge+1];
		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
			int[] edgeNumb=model.element[i].getEdgeNumb();
			for(int j=0;j<model.nElEdge;j++){
				int ne=edgeNumb[j];

				if(indx[ne]==edgeElement[ne].length-1)
					edgeElement[ne].extend(ns);

				edgeElement[ne].el[(indx[ne]++)]=i;
				edgeCounted[ne]=true;
			}			
		}

		int nx=0;
		int k;
		boolean[] onSurf=new boolean[nEdge+1];

		for(int i=1;i<=nEdge;i++){
			if(!edgeCounted[i]) continue;
			k=0;
			for(int j=0;j<indx[i];j++)
				if(edgeElement[i].el[j]>0)
					k++;
			int[] nn=new int[2*indx[i]];
			int jx=0;
			int n1=model.edge[i].endNodeNumber[0];
			for(int j=0;j<indx[i];j++){
				int[] edgeNumb=model.element[edgeElement[i].el[j]].getEdgeNumb();
				for(int p=0;p<model.nElEdge;p++){
					int ep=edgeNumb[p];
					if(ep==i) continue;
					if(model.edge[ep].endNodeNumber[0]==n1) 
						nn[jx++]=model.edge[ep].endNodeNumber[1];
					else
						if(model.edge[ep].endNodeNumber[1]==n1) 
							nn[jx++]=model.edge[ep].endNodeNumber[0];
				}

			}
			Arrays.sort(nn);
			int q=0;
			for(int p=1;p<nn.length;p++)
				if(nn[p]!=nn[p-1]) q++;

			if(q==indx[i]){
				onSurf[i]=true;
				nx++;
			}

		}

		this.nSurfEdges=nx;
		int[] onSurfEdgeNumber=new int[nx+1];
		nx=0;
		for(int i=1;i<=nEdge;i++)
			if(onSurf[i])
				onSurfEdgeNumber[++nx]=i;


		Color3f color3=new Color3f(color);


		int[][] surface3angNodes1=new int[1+2*this.nSurfEdges][3];
		int ix=0;		
		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){


			int[] vertNumb=model.element[i].getVertNumb();
			int[] edgeNumb=model.element[i].getEdgeNumb();
			if(onSurf[edgeNumb[0]] && onSurf[edgeNumb[4]] && onSurf[edgeNumb[5]])

			{
				surface3angNodes1[ix][0]=vertNumb[0];
				surface3angNodes1[ix][1]=vertNumb[1];
				surface3angNodes1[ix][2]=vertNumb[4];

				ix++;
			}


			if(onSurf[edgeNumb[1]] && onSurf[edgeNumb[5]] && onSurf[edgeNumb[6]])


			{
				surface3angNodes1[ix][0]=vertNumb[1];
				surface3angNodes1[ix][1]=vertNumb[2];
				surface3angNodes1[ix][2]=vertNumb[4];
				ix++;
			}

			if(onSurf[edgeNumb[2]] && onSurf[edgeNumb[6]]&& onSurf[edgeNumb[7]])


			{
				surface3angNodes1[ix][0]=vertNumb[2];
				surface3angNodes1[ix][1]=vertNumb[3];
				surface3angNodes1[ix][2]=vertNumb[4];
				ix++;
			}

			if(onSurf[edgeNumb[3]] && onSurf[edgeNumb[7]]&& onSurf[edgeNumb[4]])


			{
				surface3angNodes1[ix][0]=vertNumb[3];
				surface3angNodes1[ix][1]=vertNumb[0];
				surface3angNodes1[ix][2]=vertNumb[4];
				ix++;
			}

		}

		this.surface3angNodes=new int[ix][3];

		for(int i=0;i<ix;i++)
			for(int j=0;j<3;j++)
				this.surface3angNodes[i][j]=surface3angNodes1[i][j];

		surface3angNodes1=null;

		this.nSurf3angs=ix;

		util.pr("region: ir "+ir+" , nSurfEdges "+this.nSurfEdges+" , nSurfQuads: "+this.nSurfQuads+" , nSurf3angs: "+this.nSurf3angs);

		if(this.nSurf3angs>0){
			this.facet3ang = new TriangleArray(3*this.nSurf3angs,
					GeometryArray.COORDINATES | GeometryArray.NORMALS /*|GeometryArray.COLOR_3*/);

			P3f[][] vertex=new P3f[this.nSurf3angs][3];

			V3f[][] normal=new V3f[this.nSurf3angs][3];
			for(int i=0;i<this.nSurf3angs;i++){
				int[] nn=new int[3];
				Vect[] v=new Vect[3];
				for(int j=0;j<3;j++){
					nn[j] =this.surface3angNodes[i][j];

					v[j] =model.node[nn[j]].getCoord();
					if(model.node[nn[j]].u!=null)
						v[j]= v[j].add(model.node[nn[j]].u.times(this.defScale));
				}

				Vect v1=v[1].sub( v[0]);
				Vect v2=v[2].sub( v[0]);
				Vect vn=v2.cross(v1);
				vn.normalize();
				V3f nOutward=new V3f(vn);
				for(int j=0;j<3;j++){

					Vect dvn=vn.times(offFactor);
					Vect vp=v[j].add(dvn);

					vertex[i][j]=new P3f(vp);
					normal[i][j]=nOutward;
				}		
			}

			for(int i=0;i<this.nSurf3angs;i++){
				for(int j=0;j<3;j++){
					this.facet3ang.setCoordinate(3*i+j,vertex[i][j]);
					this.facet3ang.setNormal(3*i+j,normal[i][j]);
					//facet3ang.setColor(3*i+j,color3);

				}
			}
		}

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

		// color=Color.black;
		color3=new Color3f(color);
		Color3f edCol=new Color3f(color.darker());

		this.allEdge=new LineArray(2*this.nSurfEdges,GeometryArray.COORDINATES |GeometryArray.COLOR_3);
		this.allEdge.setCapability(GeometryArray.ALLOW_COLOR_WRITE);


		for(int i=0; i<this.nSurfEdges;i++)
		{
			int n1=model.edge[onSurfEdgeNumber[i+1]].endNodeNumber[0];
			Vect v1=model.node[n1].getCoord();
			if(model.node[n1].u!=null)
				v1= v1.add(model.node[n1].u.times(this.defScale));

			int n2=model.edge[onSurfEdgeNumber[i+1]].endNodeNumber[1];
			Vect v2=model.node[n2].getCoord();
			if(model.node[n2].u!=null)
				v2= v2.add(model.node[n2].u.times(this.defScale));


			this.allEdge.setCoordinate(2*i,new P3f(v1));
			this.allEdge.setCoordinate(2*i+1,new P3f(v2));
			this.allEdge.setColor(2*i,edCol);
			this.allEdge.setColor(2*i+1,edCol);

		}

		LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);

		TransparencyAttributes t_attr1,t_attr2,t_attr3;
		t_attr1 =new TransparencyAttributes(TransparencyAttributes.NONE,.0f);
		t_attr2 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);		
		t_attr3 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)(.8*transp));	

		edgeApp.setLineAttributes(la);
		if(!model.region[ir].getMaterial().startsWith("air")){

			facetApp.setTransparencyAttributes(t_attr1);
			edgeApp.setTransparencyAttributes(t_attr1);
		}
		else{

			facetApp.setTransparencyAttributes(t_attr2);
			edgeApp.setTransparencyAttributes(t_attr3);

		}
		
		facetApp.setPolygonAttributes(pa);
		
		edgeApp.setPolygonAttributes(pa);


		this.surfFacets=new Shape3D(this.facet3ang,facetApp);
		this.surfEdges=new Shape3D(this.allEdge,edgeApp);
		
		showFacets(true);
		showEdges(true);
		

	}




	public void setFacetsWedge(Model model, int ir,Color color,double transp){

		 this.defScale=model.defScale;

		this.nFirst=model.region[ir].getFirstEl();
		this.nElements=model.region[ir].getLastEl()-this.nFirst+1;
		if(this.nElements==0) return;
		int nEdge=model.numberOfEdges;

		int ns=6;
		IntVect[] edgeElement=new IntVect[nEdge+1];
		for(int i=1;i<=nEdge;i++)
			edgeElement[i]=new IntVect(ns);

		boolean[] edgeCounted=new boolean[nEdge+1];
		int[] indx=new int[nEdge+1];
		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
			
		/*	double t=util.getAng(model.getElementCenter(i).v2());
			if(t>PI/18) continue;*/

			int[] edgeNumb=model.element[i].getEdgeNumb();
			for(int j=0;j<model.nElEdge;j++){
				int ne=edgeNumb[j];

				if(indx[ne]==edgeElement[ne].length-1)
					edgeElement[ne].extend(ns);

				edgeElement[ne].el[(indx[ne]++)]=i;
				edgeCounted[ne]=true;
			}			
		}

		int nx=0;
		int k;
		boolean[] onSurf=new boolean[nEdge+1];

		for(int i=1;i<=nEdge;i++){
			if(!edgeCounted[i]) continue;
			k=0;
			for(int j=0;j<indx[i];j++)
				if(edgeElement[i].el[j]>0)
					k++;
			int[] nn=new int[2*indx[i]];
			int jx=0;
			int n1=model.edge[i].endNodeNumber[0];
			for(int j=0;j<indx[i];j++){
				int[] edgeNumb=model.element[edgeElement[i].el[j]].getEdgeNumb();
				for(int p=0;p<model.nElEdge;p++){
					int ep=edgeNumb[p];
					if(ep==i) continue;
					if(model.edge[ep].endNodeNumber[0]==n1) 
						nn[jx++]=model.edge[ep].endNodeNumber[1];
					else
						if(model.edge[ep].endNodeNumber[1]==n1) 
							nn[jx++]=model.edge[ep].endNodeNumber[0];
				}

			}
			Arrays.sort(nn);
			int q=0;
			for(int p=1;p<nn.length;p++)
				if(nn[p]!=nn[p-1]) q++;

			if(q==indx[i]){
				onSurf[i]=true;
				nx++;
			}
		}
		
		
		

		this.nSurfEdges=nx;
	

		int[][] surfaceFacetNodesQ=new int[1+2*this.nSurfEdges][4];
		int[][] surfaceFacetNodesT=new int[1+2*this.nSurfEdges][3];

		int[][] edgeLocalNumbQ={{0,3,6,7},{1,4,7,8},{2,5,8,6}};
		int[][] nodeLocalNumbQ={{3,4,1,0},{4,5,2,1},{5,3,0,2}};
		int[][] edgeLocalNumbT={{0,1},{3,4}};
		int[][] nodeLocalNumbT={{0,1,2},{3,5,4}};


		int ix=0,iy=0;		

		for(int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){

			int[] vertNumb=model.element[i].getVertNumb();
			int[] edgeNumb=model.element[i].getEdgeNumb();

			for(int is=0;is<3;is++){
				boolean b=true;
				for(int j=0;j<4;j++)
					if(!onSurf[edgeNumb[edgeLocalNumbQ[is][j]]]) {
						b=false;
						break;
					}

				if(b){
					for(int j=0;j<4;j++)
						surfaceFacetNodesQ[ix][j]=vertNumb[nodeLocalNumbQ[is][j]];

					ix++;
				}


			}

			for(int is=0;is<2;is++){
				boolean b=true;
				for(int j=0;j<2;j++)
					if(!onSurf[edgeNumb[edgeLocalNumbT[is][j]]]) {
						b=false;
						break;
					}

				if(b){
					for(int j=0;j<3;j++)
						surfaceFacetNodesT[iy][j]=vertNumb[nodeLocalNumbT[is][j]];

					iy++;
				}


			}

		}



		this.nSurfQuads=ix;
		this.nSurf3angs=iy;

		util.pr("region: ir "+ir+" , nSurfEdges: "+nx+" , nSurfQuads: "+ix+" , nSurf3angs: "+iy);



		int[] coordIndicesQ=new int[4*this.nSurfQuads];
		int[] coordIndicesT=new int[3*this.nSurf3angs];

		int[] mapQ=new int[model.numberOfNodes+1];
		int[] mapT=new int[model.numberOfNodes+1];
		
		int[] surfVertNumbQ=new int[4*this.nSurfQuads];
		int[] surfVertNumbT=new int[3*this.nSurf3angs];
		
	
		for(int i=1;i<=model.numberOfNodes;i++){
			mapQ[i]=-1;
			mapT[i]=-1;
		}
	
		
		boolean[] nc=new boolean[model.numberOfNodes+1];
		
		int p=0;
		for(int i=0;i<this.nSurfQuads;i++)
			for(int j=0;j<4;j++){
				if(nc[surfaceFacetNodesQ[i][j]]) continue;

				if(nc[surfaceFacetNodesQ[i][j]]) continue;
				surfVertNumbQ[p]=surfaceFacetNodesQ[i][j];
				mapQ[surfVertNumbQ[p]]=p;		
				nc[surfVertNumbQ[p]]=true;
					
					p++;
			
			}
		
		 nNodesQ=p;
		 
		 nc=new boolean[model.numberOfNodes+1];
			 p=0;
			for(int i=0;i<this.nSurf3angs;i++)
				for(int j=0;j<3;j++){

					if(nc[surfaceFacetNodesT[i][j]]) continue;
					surfVertNumbT[p]=surfaceFacetNodesT[i][j];
					mapT[surfVertNumbT[p]]=p;		
					nc[surfVertNumbT[p]]=true;
					
					p++;
			
				
				}
			
		 nNodesT=p;
		 
	 

		p=0;
		for(int i=0;i<this.nSurfQuads;i++)
			for(int j=0;j<4;j++){
				coordIndicesQ[p]=mapQ[surfaceFacetNodesQ[i][j]];
				p++;

			}
		
		p=0;
		for(int i=0;i<this.nSurf3angs;i++)
			for(int j=0;j<3;j++){
				
				coordIndicesT[p]=mapT[surfaceFacetNodesT[i][j]];
				p++;
			}


		
		this.surfVertNumb=new int[this.nNodesQ+this.nNodesT];

		for(int i=0;i<this.nNodesQ;i++)
			surfVertNumb[i]=surfVertNumbQ[i];
		
		for(int i=0;i<this.nNodesT;i++)
			surfVertNumb[i+nNodesQ]=surfVertNumbT[i];
			
		                  
		
	

		P3f[] coordsQ=new P3f[this.nNodesQ];
		for(int i=0;i<this.nNodesQ;i++){
			int nnx=surfVertNumbQ[i];
			coordsQ[i]=new P3f(model.node[nnx].getCoord());
		}

		P3f[] coordsT=new P3f[this.nNodesT];
		for(int i=0;i<this.nNodesT;i++){
			int nnx=surfVertNumbT[i];
			coordsT[i]=new P3f(model.node[nnx].getCoord());
		}
		

		
		util.pr(" number of nodes: "+this.surfVertNumb.length);

		
		this.faceth = new IndexedQuadArray(this.nNodesQ,  GeometryArray.COORDINATES | GeometryArray.NORMALS
				|GeometryArray.COLOR_3,4*this.nSurfQuads);

		this.faceth.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		this.faceth.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
		this.faceth.setCapability(GeometryArray.ALLOW_NORMAL_WRITE);


		int[] normalIndices1 =new int[4*this.nSurfQuads];

		V3f[] normals1=new V3f[this.nSurfQuads];
		p=0;
		for(int i=0;i<this.nSurfQuads;i++){
			int[] nn=new int[4];
			Vect[] v=new Vect[4];
			for(int j=0;j<4;j++){

				nn[j] =surfaceFacetNodesQ[i][j];

				normalIndices1[p]=i;

				if(j<3){
					v[j] =model.node[nn[j]].getCoord();

					if(model.node[nn[j]].u!=null)
						v[j]= v[j].add(model.node[nn[j]].u.times(this.defScale));
				}

				p++;
			}

			Vect v1=v[1].sub( v[0]);
			Vect v2=v[2].sub( v[1]);
			Vect vn=v1.cross(v2);

			vn.normalize();

			V3f nOutward=new V3f(vn);
			normals1[i]=nOutward;

		}
		


		List<V3f> list=new ArrayList<V3f>();
		
		for(int i=0;i<this.nSurfQuads;i++){
				list.add(normals1[i]);
		}
		

		Set<V3f> set = new HashSet<V3f>(list);
		
		ArrayList<V3f> un = new ArrayList<V3f>(set);
		
	
		int[] key=new int[this.nSurfQuads];
		
		for(int i=0;i<this.nSurfQuads;i++){
			for(int j=0;j<un.size();j++){
				if(un.get(j).equals(list.get(i))){
					key[i]=j;
					break;
				}
			}
		}		
		
		V3f[] normals=new V3f[set.size()];
		for(int j=0;j<un.size();j++)
			normals[j]=un.get(j);
		
		int[] normalIndices =new int[4*this.nSurfQuads];
		for(int i=0;i<normalIndices.length;i++)
			normalIndices[i]=key[normalIndices1[i]];
	
	
		Color3f[] colors=new Color3f[nNodesQ];


		for(int i=0;i<nNodesQ;i++){

			colors[i]=new Color3f(color);
		}


		this.faceth.setCoordinates(0, coordsQ);
		this.faceth.setNormals(0, normals);
		this.faceth.setColors(0, colors);

		this.faceth.setCoordinateIndices(0, coordIndicesQ);
		this.faceth.setNormalIndices(0, normalIndices);
		this.faceth.setColorIndices(0, coordIndicesQ);


		
		
		this.facet3angh = new IndexedTriangleArray(this.nNodesT,	GeometryArray.COORDINATES |
				 GeometryArray.NORMALS |GeometryArray.COLOR_3,3*this.nSurf3angs);
		this.facet3angh.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		this.facet3angh.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
		this.facet3angh.setCapability(GeometryArray.ALLOW_NORMAL_WRITE);
		
		
		
		 p=0;
		 
		 normalIndices1=new int[3*this.nSurf3angs];
		
		 normals1=new V3f[this.nSurf3angs];
		 

	 for(int i=0;i<this.nSurf3angs;i++){
			 int[] nn=new int[3];
			 Vect[] v=new Vect[3];

			 for(int j=0;j<3;j++){
				 
			normalIndices1[p]=i;
			nn[j] =surfaceFacetNodesT[i][j];
		
			v[j] =model.node[nn[j]].getCoord();
	
			 if(model.node[nn[j]].u!=null)
				 v[j]= v[j].add(model.node[nn[j]].u.times(this.defScale));
			 
			 p++;
			 }
							 
			 Vect v1=v[1].sub( v[0]);
			 Vect v2=v[2].sub( v[0]);
			 Vect vn=v2.cross(v1);
			
			 vn.normalize();
			 
			 for(int m=0;m<3;m++)
			 vn.el[m]=(int)(vn.el[m]*100)*0.01;
			 
			 
			 V3f nOutward=new V3f(vn);
	
				normals1[i]=nOutward;
		
		 }

	 	list=new ArrayList<V3f>();
		
		for(int i=0;i<this.nSurf3angs;i++){
				list.add(normals1[i]);
		}
		

		set = new HashSet<V3f>(list);
		
		 un = new ArrayList<V3f>(set);
		
	
		 key=new int[this.nSurf3angs];
		
		for(int i=0;i<this.nSurf3angs;i++){
			for(int j=0;j<un.size();j++){
				if(un.get(j).equals(list.get(i))){
					key[i]=j;
					break;
				}
			}
		}		
		
		normals=new V3f[set.size()];
		for(int j=0;j<un.size();j++)
			normals[j]=un.get(j);
		
		 normalIndices =new int[3*nSurf3angs];
		for(int i=0;i<normalIndices.length;i++)
			normalIndices[i]=key[normalIndices1[i]];

		
		 colors=new Color3f[nNodesT];


		for(int i=0;i<nNodesT;i++){

			colors[i]=new Color3f(color);
		}

		
		this.facet3angh.setCoordinates(0, coordsT);
		this.facet3angh.setNormals(0, normals);
		this.facet3angh.setColors(0, colors);
		
		this.facet3angh.setCoordinateIndices(0, coordIndicesT);
		this.facet3angh.setNormalIndices(0, normalIndices);
		this.facet3angh.setColorIndices(0, coordIndicesT);

		Color3f color3=new Color3f(color);


		Appearance facetApp = new Appearance();
		Appearance edgeApp = new Appearance();

		Color3f ambientColour = new Color3f(color);
		Color3f emissiveColour = new Color3f(0,0,0);
		Color3f specularColour = new Color3f(.1f,.1f,.1f);
		
		Color3f diffuseColour =new Color3f(.8f*color3.getX(), .8f*color3.getY(), .8f*color3.getZ());
		
	
		
		float shininess = 10.0f;

		Material material=new Material(ambientColour, emissiveColour,
				diffuseColour, specularColour, shininess);

		facetApp.setMaterial(material);
		
		facetApp.setCapability(Appearance.ALLOW_MATERIAL_READ);
		facetApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		facetApp.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		material.setCapability(Material.ALLOW_COMPONENT_WRITE);

		color3=new Color3f(color);
		Color3f edCol=new Color3f(color.darker().darker().darker());
		//Color3f edCol=new Color3f(Color.black);

		this.allEdgeh=new IndexedLineArray(this.surfVertNumb.length,GeometryArray.COORDINATES,2*this.nSurfEdges);
		this.allEdgeh.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		this.allEdgeh.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);

		P3f[] edgeCoords=new P3f[nNodesQ+nNodesT];
		for(int i=0; i<nNodesQ;i++)
			edgeCoords[i]=coordsQ[i];
		
		for(int i=0; i<nNodesT;i++)
			edgeCoords[i+nNodesQ]=coordsT[i];
		
		int[] onSurfEdgeNumber=new int[this.nSurfEdges+1];
		nx=0;
		for(int i=1;i<=nEdge;i++)
			if(onSurf[i])
				onSurfEdgeNumber[++nx]=i;
		
		int[] edgeCoordIndices=new int[2*this.nSurfEdges];
		for(int i=0; i<nSurfEdges;i++){
			int n1=model.edge[onSurfEdgeNumber[i+1]].endNodeNumber[0];
			if(mapQ[n1]>=0)
			edgeCoordIndices[2*i]=mapQ[n1];
			else
				edgeCoordIndices[2*i]=mapT[n1]+nNodesQ;
			
			edgeCoordIndices[2*i+1]=0;
			int n2=model.edge[onSurfEdgeNumber[i+1]].endNodeNumber[1];
			if(mapQ[n2]>=0)
				edgeCoordIndices[2*i+1]=mapQ[n2];
				else
					edgeCoordIndices[2*i+1]=mapT[n2]+nNodesQ;
		
		}
		        

		this.allEdgeh.setCoordinates(0,edgeCoords);
		this.allEdgeh.setCoordinateIndices(0,edgeCoordIndices);
		


		LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);

		TransparencyAttributes t_attr1,t_attr2,t_attr3;
		t_attr1 =new TransparencyAttributes(TransparencyAttributes.NONE,.0f);
		t_attr2 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);		
		t_attr3 =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)(.8*transp));	

		edgeApp.setLineAttributes(la);
		if(!model.region[ir].getMaterial().startsWith("air")){

			facetApp.setTransparencyAttributes(t_attr1);
			edgeApp.setTransparencyAttributes(t_attr1);
		}
		else{

			facetApp.setTransparencyAttributes(t_attr2);
			edgeApp.setTransparencyAttributes(t_attr3);

		}

		
		
		    facetApp.setRenderingAttributes(facetRA);
		    
		
		ColoringAttributes ca=new  ColoringAttributes(edCol, ColoringAttributes.SHADE_GOURAUD);

		ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

	   	edgeApp.setColoringAttributes(ca);
	   	edgeApp.setRenderingAttributes(edgeRA);
		
		edgeApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		
		facetApp.setPolygonAttributes(pa);
		
		edgeApp.setPolygonAttributes(pa);


		this.surfFacets=new Shape3D(this.facet3angh,facetApp);
		this.surfFacets.insertGeometry(this.faceth,0);
		this.surfEdges=new Shape3D(this.allEdgeh,edgeApp);
		this.surfEdges.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		this.surfFacets.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		
		showFacets(true);
		showEdges(true);


	}


	
	public void render(){

		if(this.nElements==0) return;
		surfFacets.getAppearance().getRenderingAttributes().setVisible(this.showRegion && this.showRegFace);
		surfEdges.getAppearance().getRenderingAttributes().setVisible(this.showRegion && this.showRegEdge);
		if(vectField!=null)
		vectField.getAppearance().getRenderingAttributes().setVisible(this.showRegion);
	}
	
	public void setVisible(boolean b){
		if(this.nElements==0) return;
		surfFacets.getAppearance().getRenderingAttributes().setVisible(b&&this.showRegion && this.showRegFace);
		surfEdges.getAppearance().getRenderingAttributes().setVisible(b&&this.showRegion && this.showRegEdge);

	}
	
	public void refreshField(){
		if(this.nElements==0) return;

		
		vectField.getAppearance().getRenderingAttributes().setVisible(this.showVectField);
	}
	

	public void showEdges(boolean b){
		if(this.surfEdges==null) return;

		if(b){
			if(this.surfEdges.getParent()==null)
				addChild(this.surfEdges);
		}
		else

			removeChild(this.surfEdges);

	}

	public void showFacets(boolean b){

		if(this.surfFacets==null) return;


		if(b){
			if(this.surfFacets.getParent()==null)
				addChild(this.surfFacets);
		}
		else
			removeChild(this.surfFacets);

	}




	public void setFaceColor3ang(Color color){
		int N=this.surfVertNumb.length;
		Color3f[] colors=new Color3f[N];
		for(int i=0;i<N;i++)
			colors[i]=new Color3f(color);

		this.facet3angh.setColors(0,colors);
	}

	public void setFaceColorQuad(Color color){

		int N=this.surfVertNumb.length;
		Color3f[] colors=new Color3f[N];
		for(int i=0;i<N;i++)
			colors[i]=new Color3f(color);

		this.faceth.setColors(0,colors);

	}

	public void setFaceColor(Color color){
		if(this.surfFacets==null) return;

		if(this.elCode==0) {setFaceColor3ang(color); return;}
		else if(this.elCode==1) {setFaceColorQuad(color); return;}
		else if(this.elCode==2) {setFaceColorTetra(color); return;} 
		else if(this.elCode==3) {setFaceColorWedge(color); return;} 
	//	else if(this.elCode==5) {setFaceColorPyramid(color); return;} 

		Color3f color3=new Color3f(color);

		int nNodes=this.surfVertNumb.length;
		Color3f cl3=new Color3f(color);
		Color3f[] colors=new Color3f[nNodes];
		for(int i=0;i<nNodes;i++){
			colors[i]=cl3;
		}
		this.faceth.setColors(0,colors);
		
		this.surfFacets.getAppearance().getMaterial().setAmbientColor(color3);
		this.surfFacets.getAppearance().getMaterial().setDiffuseColor(color3);


	}


	public void setFaceColorWedge(Color color){
		if(this.surfFacets==null) return;

		Color3f color3=new Color3f(color);

	
		Color3f[] colors=new Color3f[nNodesQ];
		for(int i=0;i<nNodesQ;i++)
			colors[i]=new Color3f(color);

		this.faceth.setColors(0,colors);
		
		
		Color3f[] colorsT=new Color3f[nNodesT];
		for(int i=0;i<nNodesT;i++){

			colorsT[i]=new Color3f(color);
		}

		this.facet3angh.setColors(0, colorsT);
		


		this.surfFacets.getAppearance().getMaterial().setAmbientColor(color3);
		Color3f diffuseColour =new Color3f(.8f*color3.getX(), .8f*color3.getY(), .8f*color3.getZ());
		this.surfFacets.getAppearance().getMaterial().setDiffuseColor(new Color3f(diffuseColour));


		
		
	}
	
	public void setFaceColorTetra(Color color){
		if(this.surfFacets==null) return;

		Color3f color3=new Color3f(color);

		int Nt=this.nSurf3angs;
		int N=3*Nt;
		Color3f[] colors=new Color3f[N];
		for(int i=0;i<N;i++)
			colors[i]=new Color3f(color);

		this.facet3ang.setColors(0,colors);

		this.surfFacets.getAppearance().getMaterial().setAmbientColor(color3);
		this.surfFacets.getAppearance().getMaterial().setDiffuseColor(color3);


	}

	public void setEdgeColor(Color color){
		
		
	
		
		if(this.allEdgeh==null) return;

		int nNodes=this.surfVertNumb.length;
		Color3f[] cld=new Color3f[nNodes];
		
		for(int i=0;i<nNodes;i++){
			cld[i]=new Color3f(color);
	}

		
		this.allEdgeh.setColors(0,cld);
		
		if(this.dim==3)
		{
		Color3f color3=new Color3f(color/*.darker()*/);
		this.surfEdges.getAppearance().getMaterial().setAmbientColor(color3);
		this.surfEdges.getAppearance().getMaterial().setDiffuseColor(color3);
		}
		


	}
	
	
	public void setEdgeColor(Color color,double transp){

		
		if(this.allEdge==null) return;
		Color3f color3=new Color3f(color);
		this.surfEdges.getAppearance().getColoringAttributes().setColor(color3);
		
		TransparencyAttributes tr_attr =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);	
		this.surfEdges.getAppearance().setTransparencyAttributes( tr_attr);


	}

	public void setFaceTransparency(double transp){
		if(this.surfFacets==null) return;

		TransparencyAttributes tr_attr;
		
		if(transp==0)
			tr_attr =new TransparencyAttributes();
		else
			tr_attr =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);	
		this.surfFacets.getAppearance().setTransparencyAttributes( tr_attr);

	}

	public void setEdgeTransparency(double transp){

		if(this.allEdge==null) return;
		TransparencyAttributes tr_attr =new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,(float)transp);	
		this.surfEdges.getAppearance().setTransparencyAttributes( tr_attr);

	}




	public void paintNodalScalar(Model model){

		if(this.nElements==0) return;

		if(model.dim==2) {paintNodalScalar2D( model); return;}

		double strMax=this.nodalScalarScale*model.nodalScalarMax;
		double strMin=this.nodalScalarScale*model.nodalScalarMin;


		ColorBar cBar=new ColorBar(strMin,strMax);



		//this.surfFacets.setAppearance(null);
		//this.surfFacets.getAppearance().getMaterial().setDiffuseColor(new Color3f(0,0,0));
		//this.surfFacets.getAppearance().getMaterial().setEmissiveColor(new Color3f(0,0,0));
		//this.surfFacets.getAppearance().getMaterial().setSpecularColor(new Color3f(0,0,0));
		this.surfFacets.getAppearance().getMaterial().setAmbientColor(new Color3f(0,0,0));

		int nNodes=this.surfVertNumb.length;
		this.nodalVals=new double[nNodes];

		Color3f[] cld=new Color3f[nNodesQ];
	
		/*
	Color[] ss=new Color[8];
	
		ss[0]=new Color(0,153,0);
		ss[1]=new Color(0,0,200);
		ss[2]=new Color(255,255,0);
		ss[3]=new Color(255,255,255);
		ss[4]=new Color(215,119,0);
		ss[5]=new Color(204,0,0);
		
		ss[6]=new Color(0,0,0);
		*/
		
/*		ss[0]=new Color(204,0,0);
		ss[1]=new Color(215,119,0);
		ss[2]=new Color(0,0,200);
		ss[3]=new Color(0,153,0);
	
		ss[4]=new Color(255,255,0);
		ss[5]=new Color(255,255,255);
		ss[6]=new Color(0,0,0);*/
		for(int i=0;i<this.nNodesQ;i++){
	

			double sn=model.node[this.surfVertNumb[i]].scalar;
			this.nodalVals[i]=sn;
			cld[i]=new Color3f( cBar.getColor(sn));
//======================
			//if(sn>0)
		//	cld[i]=new Color3f( ss[(int)(sn)-1]);
			//else cld[i]=new Color3f(0,0,0);
	//====================	

		}
		this.faceth.setColors(0,cld);
		
		
		
		if(model.elCode==3){
		
		cld=new Color3f[nNodesT];

		int ix=nNodesQ;
		for(int i=0;i<this.nNodesT;i++){

			
			double sn=model.node[this.surfVertNumb[ix]].scalar;
			this.nodalVals[ix]=sn;
			cld[i]=new Color3f( cBar.getColor(sn));
			ix++;

		}
		this.facet3angh.setColors(0,cld);
		}
		


		//this.surfEdges.getAppearance().setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,.8f));

	}
	

	public void paintNodalScalar2D(Model model){

		if(this.nElements==0) return;


		double strMax=model.nodalScalarMax;
		double strMin=model.nodalScalarMin;

		
		ColorBar cBar=new ColorBar(strMin,strMax);


		int Nn=this.surfVertNumb.length;


		this.nodalVals=new double[Nn];

		Color3f[] faceColor=new Color3f[Nn];


		for(int i=0;i<Nn;i++){
			double sn=this.nodalScalarScale*model.node[this.surfVertNumb[i]].scalar;

			this.nodalVals[i]=sn;
		//	if(i==0) util.pr(this.surfVertNumb[i]);

			faceColor[i]=new Color3f(cBar.getColor(sn));

		}

		if(model.elCode==0)
			this.facet3angh.setColors(0,faceColor);
		else
			this.faceth.setColors(0,faceColor);
		
		this.surfEdges.getAppearance().setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,.8f));

	}


	
	public void reScaleNodalScalar( ColorBar cBar){

		if(this.nElements==0) return;

		if(this.elCode==3) { reScaleNodalScalarWedge(cBar); return;}


		int N;

		N=this.surfVertNumb.length;

		Color3f[] cld=new Color3f[N];

		if(this.elCode>1)
			this.surfFacets.setAppearance(null);
		for(int i=0;i<N;i++){

			double colorValue=this.nodalVals[i];
			cld[i]=new Color3f(cBar.getColor(colorValue));


		}

		if(this.elCode!=0)
			this.faceth.setColors(0,cld);
			else
				this.facet3angh.setColors(0,cld);

		this.surfEdges.getAppearance().setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,.8f));


	}
	
	public void reScaleNodalScalar(Model model, ColorBar cBar){

		if(this.nElements==0) return;

		if(this.elCode==3) { reScaleNodalScalarWedge(cBar); return;}


		int N;

		N=this.surfVertNumb.length;


		Color3f[] cld=new Color3f[N];

		if(this.elCode>1)
			this.surfFacets.setAppearance(null);
		for(int i=0;i<N;i++){

			double colorValue=model.node[this.surfVertNumb[i]].scalar;

			cld[i]=new Color3f(cBar.getColor(colorValue));


		}

		if(this.elCode!=0)
		this.faceth.setColors(0,cld);
		else
			this.facet3angh.setColors(0,cld);
		this.surfEdges.getAppearance().setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.BLEND_ONE,.8f));


	}
	
	public void deformReg(Model model, double defScale){
		
		if(model.elCode==3) { deformRegWedge(model,defScale); return;}
		
		P3f[] newCoord=new P3f[this.surfVertNumb.length];
		int[] coInd=new int[model.numberOfNodes+1];

		for(int i=0;i<this.surfVertNumb.length;i++)
		{

			coInd[this.surfVertNumb[i]]=i;
			Vect v=model.node[this.surfVertNumb[i]].getCoord();


			if(model.node[this.surfVertNumb[i]].u!=null){
				Vect dd=model.node[this.surfVertNumb[i]].getU().times(defScale).v3();
				v=v.add(dd);
				
				}


			newCoord[i]=new P3f(v.v3());

		}
		
		int p=0;
		
		V3f[] normals=new V3f[this.nSurfQuads];
		for(int i=0;i<this.nSurfQuads;i++){
			Vect[] v=new Vect[4];
			for(int j=0;j<4;j++){

				int nny=this.faceth.getCoordinateIndex(p);
				if(j<3){
					v[j] =new Vect(newCoord[nny]);

				}
				
				p++;

			}

			Vect v1=v[1].sub( v[0]);
			Vect v2=v[2].sub( v[1]);
			Vect vn=v1.cross(v2);

			vn.normalize();

			V3f nOutward=new V3f(vn);
			normals[i]=nOutward;

		}
	//	this.faceth.setNormals(0,normals);
		this.faceth.setCoordinates(0,newCoord);
		
		this.allEdgeh.setCoordinates(0,newCoord);
		
		return;
	}


	public void runRubix(Mat R){


		P3f[] newCoord=new P3f[this.surfVertNumb.length];
		Vect vr=new Vect();
		for(int i=0;i<this.surfVertNumb.length;i++)
		{
			
			float[] cc=new float[3];
			faceth.getCoordinates(i,cc);
			Vect v=new Vect(cc[0],cc[1],cc[2]);

			vr=R.mul(v);
			
	
			newCoord[i]=new P3f(vr);

		}

		this.faceth.setCoordinates(0,newCoord);
		
		this.allEdgeh.setCoordinates(0,newCoord);
		
		
		return;
	}

	
	
	public void deformRegWedge(Model model, double defScale){
		
		P3f[] newCoordQ=new P3f[this.nNodesQ];
		P3f[] newCoordT=new P3f[this.nNodesT];

		if(this.faceth!=null){
		for(int i=0;i<this.nNodesQ;i++)
		{

			Vect v=model.node[this.surfVertNumb[i]].getCoord();


			if(model.node[this.surfVertNumb[i]].u!=null){
				Vect dd=model.node[this.surfVertNumb[i]].getU().times(defScale).v3();
				v=v.add(dd);
						
				
				}


			newCoordQ[i]=new P3f(v.v3());

		}
		
	
	
		
	
		this.faceth.setCoordinates(0,newCoordQ);
		
		}
		
		if(this.facet3angh!=null){
		
	
		for(int i=0;i<this.nNodesT;i++)
		{

			
			Vect v=model.node[this.surfVertNumb[i+this.nNodesQ]].getCoord();


			if(model.node[this.surfVertNumb[i+this.nNodesQ]].u!=null){
				Vect dd=model.node[this.surfVertNumb[i+this.nNodesQ]].getU().times(defScale).v3();

				
				v=v.add(dd);
				
				}


			newCoordT[i]=new P3f(v.v3());

		}
		
		
		
		this.facet3angh.setCoordinates(0,newCoordT);
		
		}
	
		if(allEdgeh!=null){
		P3f[] newEdgeCoords=new P3f[nNodesQ+nNodesT];
		for(int i=0; i<nNodesQ;i++)
			newEdgeCoords[i]=newCoordQ[i];
		
		for(int i=0; i<nNodesT;i++)
			newEdgeCoords[i+nNodesQ]=newCoordT[i];
	
		this.allEdgeh.setCoordinates(0,newEdgeCoords);
		}
		
		return;
	}

	
	public void deform(Model model){
		if(this.elCode==1 || this.elCode==4) {
			deformQuadsIndex(model);
			return;
		}

	}

	public void deformQuadsIndex(Model model){
		this.ii++;
		P3f[] newCoord=new P3f[this.surfVertNumb.length];
		int[] coInd=new int[1+model.numberOfNodes];
		for(int i=0;i<this.surfVertNumb.length;i++)
		{

			coInd[this.surfVertNumb[i]]=i;
			Vect v=model.node[this.surfVertNumb[i]].getCoord().v3();

			
			P3f p=new P3f(v);

			
			double d;

			d=.4*pow(v.el[0]+.4,2)*sin(12*(.05*this.ii-v.el[0]));
			
		
			
			newCoord[i]=new P3f(p.x,p.y,p.z+d);


		}

				
		
		this.faceth.setCoordinates(0,newCoord);
		this.allEdgeh.setCoordinates(0,newCoord);
		
		if(this.elCode>1)
		{
			
			int p=0;
			
			V3f[] normals=new V3f[this.nSurfQuads];
			for(int i=0;i<this.nSurfQuads;i++){
				Vect[] v=new Vect[4];
				for(int j=0;j<4;j++){

					int nny=this.faceth.getCoordinateIndex(p);
					if(j<3){
						v[j] =new Vect(newCoord[nny]);

					}
					
					p++;

				}
		Vect v1=v[1].sub( v[0]);
		Vect v2=v[2].sub( v[1]);
		Vect vn=v1.cross(v2);

		vn.normalize();

		V3f nOutward=new V3f(vn);
		normals[i]=nOutward;

	}
			this.faceth.setNormals(0,normals);
			
		}
			
	}



	public void	 reScaleNodalScalarWedge(ColorBar cBar){if(this.nElements==0) return;


	Color3f[] cld=new Color3f[nNodesQ];

	for(int i=0;i<this.nNodesQ;i++){

		double sn=this.nodalVals[i];
		
		cld[i]=new Color3f( cBar.getColor(sn));

	}
	this.faceth.setColors(0,cld);
	
	 cld=new Color3f[nNodesT];

	int ix=nNodesQ;
	for(int i=0;i<this.nNodesT;i++){

	
		cld[i]=new Color3f( cBar.getColor(this.nodalVals[ix]));
		ix++;

	}
	this.facet3angh.setColors(0,cld);
	
	}
	
	public void setVectField(Model model,ColorBar cBar, int fieldMode, int arrMode)
	{
	

		this.arrMode=arrMode;
		this.fieldMode=fieldMode;
		if(fieldMode==4) {
				if(this.arrMode<2) setElementField2D1(model,cBar);
			else if(this.arrMode==2) setElementField3D0(model,cBar);
			//else if(this.arrMode==3) setElementField3D1(model,cBar);
			else if(this.arrMode==3) setElementField3DK(model,cBar,6);
			else if(this.arrMode==4) setElementField3DArrow(model,cBar,1);
			return;}
		else setNodalField(model,cBar);
	
	}
	
	public void setNodalField(Model model,ColorBar cBar){
		
		if(dim==2)
			setNodalField2D1(model,cBar);
		else if(this.arrMode==2)
			setNodalField3D0(model,cBar,false);
		else if(this.arrMode==3)
			setNodalField3D1(model,cBar,false);
	
	}

	public void setArrows0(Model model){

		int[] nn=model.getRegNodes(nr);
		int N=nn.length;
		
		nArrows=N;
		if(N==0) return;
		
	
		P3f[] coords=new P3f[4*N];

		int p=0;
		double d=.002;
		for(int i=0;i<N;i++){
			
			Vect v=model.node[nn[i]].getCoord().v3();
				coords[p]=new P3f(v);
				p++;
				coords[p]=new P3f(v.add(new Vect(0,0,d)));
				p++;
				coords[p]=new P3f(v.add(new Vect(.25*d,0,.75*d)));
				p++;
				coords[p]=new P3f(v.add(new Vect(-.25*d,0,.75*d)));
				p++;
			}


				this.arrows=new IndexedLineArray(4*N,GeometryArray.COORDINATES ,6*N);

				this.arrows.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
				this.arrows.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);

				this.arrows.setCoordinates(0, coords);


				int[] coordIndices=new int[6*N];
				p=0;
				for(int i=0; i<N;i++){
					int k=4*i;

					coordIndices[p]=k;
					p++;
					coordIndices[p]=k+1;
					p++;
					coordIndices[p]=k+1;
					p++;
					coordIndices[p]=k+2;
					p++;
					coordIndices[p]=k+1;
					p++;
					coordIndices[p]=k+3;
					p++;
				}

				this.arrows.setCoordinateIndices(0, coordIndices);


				Color color=Color.red;
		
				Color3f edCol=new Color3f(color.darker().darker().darker());

				LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);
				ColoringAttributes ca=new  ColoringAttributes(edCol, ColoringAttributes.SHADE_GOURAUD);

				ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);

				Appearance app=new Appearance();

				app.setColoringAttributes(ca);
				

				app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);

				app.setLineAttributes(la);


				this.vectField=new Shape3D(this.arrows,app);

				this.vectField.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);


			//	addChild(this.vectField);

	}
	

	
	public void setElementField3D0(Model model,ColorBar cBar){

		int N=0;
		int ix=0;
		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++){
			Vect B=model.element[i].getB();
			
			double scale=B.norm();
			if( scale>0 ) 
				{N++;
				model.element[i].setShowVectField(true);
						
				}
			else 
				model.element[i].setShowVectField(false);
		
			ix++;
		
		}
		nArrows=N;

		if(N==0) return;
		
		int K=5;
		double d=0.2*(model.maxEdgeLength+model.minEdgeLength);
		double dx=d/8;
		
		Vect[] av=new Vect[K];
		
		
		av[0]=new Vect(-dx,-dx,0);
		av[1]=new Vect(dx,-dx,0);
		av[2]=new Vect(dx,dx,0);
		av[3]=new Vect(-dx,dx,0);
		av[4]=new Vect(0,0,d);
		
		Vect[] an=new Vect[4];
		
		an[0]=new Vect(0,-1,.125).normalized();
		an[1]=new Vect(1,0,.125).normalized();
		an[2]=new Vect(0,1,.125).normalized();
		an[3]=new Vect(-1,0,.125).normalized();
		
		
	
		
		int nVecrts=K*N;
		
		P3f[] coords=new P3f[nVecrts];
	
		int[] edgeCoordIndices=new int[16*N];
		
		Color3f[] colors= new Color3f[N];

		int[] edgeColorIndices=new int[16*N];	


	

		Vect z=new Vect(0,0,1);
		

		int p=0;
		
		int iy=0,iz=0,iw=0;
		ix=0;

		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++){
	
			if(!model.element[i].toShowVectField()) continue;
		
	
			
			Vect B=model.element[i].getB();
			
			double scale=B.norm();
			
			colors[ix]=new Color3f(cBar.getColor(scale));
			
					
					
			for(int j=0; j<16;j++){
				edgeColorIndices[iz++]=ix;
			}
			
		
			ix++;
			
			Mat R=util.rotMat(B.normalized(), z);

			Vect P=model.getElementCenter(i).v3();

			for(int j=0; j<5;j++){
				Vect v=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(v));
				
				p++;
				
			}
		
			}

		
			

		int[][] localIndEdge={{0,1},{1,2},{2,3},{3,0},{0,4},{1,4},{2,4},{3,4}};
	
		


		p=0;
		ix=0;
		 iy=0;
		 int t=0,m=0;
		 
		for(int i=0; i<N;i++){
		

		for(int j=0; j<8;j++){
			for(int k=0; k<2;k++)
			edgeCoordIndices[m++]=ix+localIndEdge[j][k];
			}
				
			
				ix+=K;
		}
		
	
					
				this.arrows=new IndexedLineArray(5*N,GeometryArray.COORDINATES  |
							 GeometryArray.COLOR_3,16*N);
			
						this.arrows.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
						this.arrows.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);


				this.arrows.setCoordinates(0, coords);		
				this.arrows.setCoordinateIndices(0, edgeCoordIndices);	
				this.arrows.setColors(0, colors);	
				this.arrows.setColorIndices(0, edgeColorIndices);		
			
			
			
				LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);
				
							
				Appearance app=new Appearance();

				
				app.setLineAttributes(la);
				
				app.setRenderingAttributes(fieldRA);

				

			
				arrowEdges=new Shape3D(arrows,app);


			
				addChild(arrowEdges);

	}
	
	
	
	public void setElementField3D1(Model model,ColorBar cBar){

		int N=0;
		int ix=-1;
		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++){		
			ix++;
			if(!this.surfElements[ix]) continue;
			Vect B=model.element[i].getB();
			double scale=B.norm();
			if( scale>0 ) 
				{N++;
				model.element[i].setShowVectField(true);
						
				}
			else 
				model.element[i].setShowVectField(false);
	
		
		}
		nArrows=N;

		if(N==0) return;
		
		int K=5;
		double d=0.2*(model.maxEdgeLength+model.minEdgeLength);
		double dx=d/8;
		
		Vect[] av=new Vect[K];
		
		
		av[0]=new Vect(-dx,-dx,0);
		av[1]=new Vect(dx,-dx,0);
		av[2]=new Vect(dx,dx,0);
		av[3]=new Vect(-dx,dx,0);
		av[4]=new Vect(0,0,d);
		
		Vect[] an=new Vect[4];
		
		an[0]=new Vect(0,-1,.125).normalized();
		an[1]=new Vect(1,0,.125).normalized();
		an[2]=new Vect(0,1,.125).normalized();
		an[3]=new Vect(-1,0,.125).normalized();
		
		
	
		
		int nVecrts=K*N;
		
		P3f[] coords=new P3f[nVecrts];
		int[] sideCoordIndices=new int[12*N];	
	
		int[] baseCoordIndices=new int[4*N];

		int[] edgeCoordIndices=new int[16*N];
		
		Color3f[] colors= new Color3f[N];
		
		int[] sideColorIndices=new int[12*N];	
		int[] edgeColorIndices=new int[16*N];	
		int[] baseColorIndices=new int[4*N];	

	

		Vect z=new Vect(0,0,1);
		

		int p=0;
		
		int iy=0,iz=0,iw=0;
		ix=0;

		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++){
	
			if(!model.element[i].toShowVectField()) continue;
		
	
			
			Vect B=model.element[i].getB();
			
			double scale=B.norm();
			
			colors[ix]=new Color3f(cBar.getColor(scale));
			
			for(int j=0; j<12;j++){
				sideColorIndices[iy++]=ix;
			}
			
					
			for(int j=0; j<16;j++){
				edgeColorIndices[iz++]=ix;
			}
			
			for(int j=0; j<4;j++){
				baseColorIndices[iw++]=ix;
			}
			
			ix++;
			
			Mat R=util.rotMat(B.normalized(), z);

			Vect P=model.getElementCenter(i).v3();

			for(int j=0; j<5;j++){
				Vect v=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(v));
				
				p++;
				
			}
		
			}

		
			

		int[][] localInd={{0,1,4},{1,2,4},{2,3,4},{3,0,4}};
		int[][] localIndEdge={{0,1},{1,2},{2,3},{3,0},{0,4},{1,4},{2,4},{3,4}};
	
		


		p=0;
		ix=0;
		 iy=0;
		 int t=0,m=0;
		 
		for(int i=0; i<N;i++){
		
		for(int j=0; j<4;j++){
					for(int k=0; k<3;k++){
						sideCoordIndices[p]=ix+localInd[j][k];
						p++;
					}
					
					baseCoordIndices[t++]=ix+3-j;
					
		}
		
		for(int j=0; j<8;j++){
			for(int k=0; k<2;k++)
			edgeCoordIndices[m++]=ix+localIndEdge[j][k];
			}
				
			
				ix+=K;
				iy+=4;
		}
		
		float a=.8f;
		
		Color3f[] edgeColors= new Color3f[N];
		for(int j=0; j<N;j++){
			
		edgeColors[j]=new Color3f(a*colors[j].x,a*colors[j].y,a*colors[j].z);
		}
		
		
			this.arrowFace=new IndexedTriangleArray(nVecrts,GeometryArray.COORDINATES  |
					/*GeometryArray.NORMALS | */GeometryArray.COLOR_3,12*N);
	
				this.arrowFace.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
				this.arrowFace.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
				
				
				
				this.arrowBase=new IndexedQuadArray(5*N,GeometryArray.COORDINATES  |
						/*GeometryArray.NORMALS |*/ GeometryArray.COLOR_3,4*N);
		
					this.arrowBase.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
					this.arrowBase.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
					
				this.arrows=new IndexedLineArray(5*N,GeometryArray.COORDINATES  |
							 GeometryArray.COLOR_3,16*N);
			
						this.arrows.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
						this.arrows.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);


				this.arrows.setCoordinates(0, coords);		
				this.arrows.setCoordinateIndices(0, edgeCoordIndices);	
				this.arrows.setColors(0, edgeColors);	
				this.arrows.setColorIndices(0, edgeColorIndices);		
			
			
				this.arrowFace.setCoordinates(0, coords);				
			//	this.arrowFace.setNormals(0, normals);
				this.arrowFace.setCoordinateIndices(0, sideCoordIndices);				
			//	this.arrowFace.setNormalIndices(0, normalIndices);
				this.arrowFace.setColors(0, colors);
				this.arrowFace.setColorIndices(0, sideColorIndices);
				
				this.arrowBase.setCoordinates(0, coords);
				this.arrowBase.setCoordinateIndices(0, baseCoordIndices);
				//this.arrowBase.setNormals(0, baseNormals);
				//this.arrowBase.setNormalIndices(0, baseNormalIndices);			
				this.arrowBase.setColors(0, colors);
				this.arrowBase.setColorIndices(0, baseColorIndices);
				

			
				LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);
				
				Color3f color3=new Color3f(.5f,.3f,.3f);
				
				Color3f ambientColour = new Color3f(color3);
				Color3f emissiveColour = new Color3f(.1f*color3.getX(),.1f*color3.getY(), .1f*color3.getZ());
				Color3f specularColour = new Color3f(.0f, .0f, .0f);
				Color3f diffuseColour =new Color3f(color3);
				float shininess = 1.0f;


				Material material=new Material(ambientColour, emissiveColour,
						diffuseColour, specularColour, shininess);
				
				Appearance app=new Appearance();

				app.setMaterial(material);
				
				app.setLineAttributes(la);
				
				app.setRenderingAttributes(fieldRA);


				this.vectField=new Shape3D(this.arrowFace,app);
				this.vectField.insertGeometry(this.arrowBase,0);
			//	this.vectField.insertGeometry(this.arrows,0);
				

				this.vectField.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
				
				arrowEdges=new Shape3D(arrows,app);


				addChild(this.vectField);
				
				addChild(arrowEdges);

				
				
	}
	
	public void setElementField3DK(Model model,ColorBar cBar,int K1){

		nArrowHeadDivs=K1;
		int N=0;
		int ix=-1;
		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++){		
			ix++;
			if(!this.surfElements[ix]) continue;
			Vect B=model.element[i].getB();
			double scale=B.norm();
			if( scale>0 ) 
				{N++;
				model.element[i].setShowVectField(true);
						
				}
			else 
				model.element[i].setShowVectField(false);
	
		
		}
		nArrows=N;

		if(N==0) return;
		
		//int K=5;
		double d=0.2*(model.maxEdgeLength+model.minEdgeLength);
		double dx=d/5;
		
		int K=K1+1;
		
		if(K1>4) K++;
	
		Vect[] av=new Vect[K];
		
		for(int i=0;i<K1;i++){
			double x=dx*cos(i*2*PI/K1);
			double y=dx*sin(i*2*PI/K1);
			av[i]=new Vect(x,y,0);
		}
		av[K1]=new Vect(0,0,d);
		if(K1>4) 
			av[K-1]=new Vect(0,0,0);	
		
		int nVecrts=K*N;
		
		P3f[] coords=new P3f[nVecrts];
		int[] sideCoordIndices=new int[K1*3*N];	
	
		int nQuads=1;
		if(K1>4) nQuads=K1/2;
		int[] baseCoordIndices=new int[nQuads*4*N];

		int[] edgeCoordIndices=new int[4*K1*N];
		
		Color3f[] colors= new Color3f[N];
		
		int[] sideColorIndices=new int[3*K1*N];	
		int[] edgeColorIndices=new int[4*K1*N];	
		int[] baseColorIndices=new int[nQuads*4*N];	

	

		Vect z=new Vect(0,0,1);
		

		int p=0;
		
		int iy=0,iz=0,iw=0;
		ix=0;

		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++){
	
			if(!model.element[i].toShowVectField()) continue;
		
	
			
			Vect B=model.element[i].getB();
			
			double scale=B.norm();
			
			colors[ix]=new Color3f(cBar.getColor(scale));
			
			for(int j=0; j<3*K1;j++){
				sideColorIndices[iy++]=ix;
			}
			
					
			for(int j=0; j<4*K1;j++){
				edgeColorIndices[iz++]=ix;
			}
			
			for(int j=0; j<nQuads*4;j++){
				baseColorIndices[iw++]=ix;
			}
			
			ix++;
			
			Mat R=util.rotMat(B.normalized(), z);

			Vect P=model.getElementCenter(i).v3();

			for(int j=0; j<K;j++){
				Vect v=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(v));
				
				p++;
				
			}
		
			}

		
	
		int[][] localInd=new int[K1][3];
		int[][] localIndEdge=new int[2*K1][2];//{{0,1},{1,2},{2,3},{3,0},{0,4},{1,4},{2,4},{3,4}};
		
		for(int j=0; j<K1;j++){
			localInd[j][0]=j;
			if(j<K1-1)
				localInd[j][1]=j+1;
			else
				localInd[j][1]=0;
			
			localInd[j][2]=K1;
			
			localIndEdge[j][0]=j;
			if(j<K1-1)
				localIndEdge[j][1]=j+1;
			else
				localIndEdge[j][1]=0;
			
			localIndEdge[K1+j][0]=j;
			localIndEdge[K1+j][1]=K1;
		}


		p=0;
		ix=0;

		 int t=0,m=0;
		 
		for(int i=0; i<N;i++){
		
		for(int j=0; j<K1;j++){
					for(int k=0; k<3;k++){
						sideCoordIndices[p]=ix+localInd[j][k];
						p++;
					}
					
		}
		
		for(int k=0; k<nQuads;k++){
	
			if(nQuads==1){
			for(int j=0; j<4;j++){
				int index=ix+3-j;
					baseCoordIndices[t++]=index;
			}
			}
			else{
					for(int j=0; j<3;j++){
						int index=k*2+ix+2-j;
						if(index==K1) index=ix;
							baseCoordIndices[t++]=index;
					}
					baseCoordIndices[t++]=ix+K-1;
			}
			
			
		}
			
	
		for(int j=0; j<2*K1;j++){
			for(int k=0; k<2;k++)
			edgeCoordIndices[m++]=ix+localIndEdge[j][k];
			}
				
			
				ix+=K;
		}

		float a=.8f;
		
		Color3f[] edgeColors= new Color3f[N];
		for(int j=0; j<N;j++){
			
		edgeColors[j]=new Color3f(a*colors[j].x,a*colors[j].y,a*colors[j].z);
		}
		
		
			this.arrowFace=new IndexedTriangleArray(nVecrts,GeometryArray.COORDINATES  |
					/*GeometryArray.NORMALS | */GeometryArray.COLOR_3,3*K1*N);
	
				this.arrowFace.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
				this.arrowFace.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
				
				
				
				this.arrowBase=new IndexedQuadArray(K*N,GeometryArray.COORDINATES  |
						/*GeometryArray.NORMALS |*/ GeometryArray.COLOR_3,4*nQuads*N);
		
					this.arrowBase.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
					this.arrowBase.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
					
				this.arrows=new IndexedLineArray(K*N,GeometryArray.COORDINATES  |
							 GeometryArray.COLOR_3,4*K1*N);
			
						this.arrows.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
						this.arrows.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);


				this.arrows.setCoordinates(0, coords);		
				this.arrows.setCoordinateIndices(0, edgeCoordIndices);	
				this.arrows.setColors(0, edgeColors);	
				this.arrows.setColorIndices(0, edgeColorIndices);		
			
			
				this.arrowFace.setCoordinates(0, coords);				
			//	this.arrowFace.setNormals(0, normals);
				this.arrowFace.setCoordinateIndices(0, sideCoordIndices);				
			//	this.arrowFace.setNormalIndices(0, normalIndices);
				this.arrowFace.setColors(0, colors);
				this.arrowFace.setColorIndices(0, sideColorIndices);
				
				this.arrowBase.setCoordinates(0, coords);
				this.arrowBase.setCoordinateIndices(0, baseCoordIndices);
				//this.arrowBase.setNormals(0, baseNormals);
				//this.arrowBase.setNormalIndices(0, baseNormalIndices);			
				this.arrowBase.setColors(0, colors);
				this.arrowBase.setColorIndices(0, baseColorIndices);
				

			
				LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);
				
				Color3f color3=new Color3f(.5f,.3f,.3f);
				
				Color3f ambientColour = new Color3f(color3);
				Color3f emissiveColour = new Color3f(.1f*color3.getX(),.1f*color3.getY(), .1f*color3.getZ());
				Color3f specularColour = new Color3f(.0f, .0f, .0f);
				Color3f diffuseColour =new Color3f(color3);
				float shininess = 1.0f;


				Material material=new Material(ambientColour, emissiveColour,
						diffuseColour, specularColour, shininess);
				
				Appearance app=new Appearance();

				app.setMaterial(material);
				
				app.setLineAttributes(la);
				
				app.setRenderingAttributes(fieldRA);
				

				app.setPolygonAttributes(pa);

				this.vectField=new Shape3D(this.arrowFace,app);
				this.vectField.insertGeometry(this.arrowBase,0);
			//	this.vectField.insertGeometry(this.arrows,0);
				

				this.vectField.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
				
				arrowEdges=new Shape3D(arrows,app);


				addChild(this.vectField);
				
				addChild(arrowEdges);

				
				
	}
	
	private void setElementField3DArrow(Model model,ColorBar cBar,int vectMode){
	
	
		
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

	
		

		Vect[] P=new Vect[this.nElements];
		 V=new Vect[this.nElements];

		//neNumb=new int[this.nElements];

		int ix=0;

		int kx=-1;
		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++)
		{
			kx++;
			if(!this.surfElements[kx]) continue;

				P[ix]=model.getElementCenter(i);
				V[ix]=model.element[i].getB();
				ix++;


			}


			
			util.pr(" number of arrows: "+ix);



		arrow=new Arrow[this.nElements];
		double Vmax=1;
	
		/* if(mode==0) {Vmax=model.FreluctMax;}
		else if(mode==1) { Vmax=model.FmsMax;}
		else if(mode==2) { Vmax=model.uMax;}
		else if(mode==3) { Vmax=model.FedMax;}
		else if(mode==4) */{ Vmax=model.Bmax;}

		//if(Vmax>0)
		//	vScale=.2*(model.minEdgeLength+model.maxEdgeLength)/Vmax;
		
		
		trans=new Transform3D[this.nElements];
	
	Color color=Color.red.darker();

		for(int j=0;j<this.nElements;j++){	
			if(!this.surfElements[j]) continue;

			trans[j]=new Transform3D();
			trans[j].setTranslation(new V3f(P[j]));
			double Vn=V[j].norm();

			
	
			
			double a=Vn*vScale;
			
			if(model.fluxNormalized){
				
				if(Vn/model.Bmax>1e-2)
				a=1./10;
				else
					a=1./100;
			}
			
			arrow[j]=new Arrow(vectMode);

			Matrix3d Mr =util.mat3d(V[j],new Vect(0,0,1));	


			
			trans[j].setRotation(Mr);
			trans[j].setScale(a);
				
			color=cBar.getColor(Vn);
			
			
			arrow[j].setColor(color);
			
			arrow[j].scale.el[0]=vScale;
			arrow[j].scale.el[1]=a;
			

			arrow[j].setTransform(trans[j]);
			
			
			this.addChild(arrow[j]);
			

			
		}

	}
	
private void rescaleElementField3DArrow(Model model,ColorBar cBar,double a){

	Color color=Color.red.darker();
		
		for(int j=0;j<this.nElements;j++){	
			if(!this.surfElements[j]) continue;
		
			//Matrix3d M =util.mat3dScale(new Vect(a*arrow[j].scale.el[0],a*arrow[j].scale.el[0],a*arrow[j].scale.el[1]));	
			//trans[j].setScale(arrow[j].scale.el[1]);
		//	trans[j].setRotation(M);
			
/*			arrow[j].transCone.setScale(a);
			arrow[j].transAx.setScale(a);
			arrow[j].tgCone.setTransform(arrow[j].transCone);
			arrow[j].tgAx.setTransform(arrow[j].transAx);*/

			double Vn=V[j].norm();
			color=cBar.getColor(Vn);
						
			arrow[j].setColor(color);
			
			trans[j].setScale(a*arrow[j].scale.el[1]);
			
			arrow[j].setTransform(trans[j]);
			
		
			
		}
		

	}
	
	
	public void setElementField2D1(Model model,ColorBar cBar){

		int N=0;
		int ix=0;
		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++){
			Vect B=model.element[i].getB();
			
			double scale=B.norm();
			if( scale>0 ) 
				{N++;
				model.element[i].setShowVectField(true);
						
				}
			else 
				model.element[i].setShowVectField(false);
		
			ix++;
		
		}
		nArrows=N;

		if(N==0) return;
		
		int K=7;
		double h=0.5*(model.maxEdgeLength+model.minEdgeLength);
		double dx=h/15;
		double h1=8*dx;
	
		Vect[] av=new Vect[K];
		
		
		av[0]=new Vect(-dx,0);
		av[1]=new Vect(dx,0);
		av[2]=new Vect(dx,h1);
		av[3]=new Vect(-dx,h1);
		av[4]=new Vect(2*dx,h1);
		av[5]=new Vect(0,h);
		av[6]=new Vect(-2*dx,h1);
		
			
	
		
		int nVecrts=K*N;
		
		P3f[] coords=new P3f[nVecrts];
		int[] faceCoordIndices=new int[9*N];	
		
		int[] edgeCoordIndices=new int[12*N];
		
		Color3f[] colors= new Color3f[N];
		
		int[] faceColorIndices=new int[9*N];	
		int[] edgeColorIndices=new int[12*N];	

	
		Vect y=new Vect(0,1);

		int p=0;
		
		int iy=0,iz=0,iw=0;
		ix=0;

		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++){
	
			if(!model.element[i].toShowVectField()) continue;
		
	
			
			Vect B=model.element[i].getB();
			
			double scale=B.norm();
			
			colors[ix]=new Color3f(cBar.getColor(scale));
			
			for(int j=0; j<9;j++){
				faceColorIndices[iy++]=ix;
			}
			
					
			for(int j=0; j<12;j++){
				edgeColorIndices[iz++]=ix;
			}
			
		
			
			ix++;
			
			Mat R=util.rotMat2D(B.normalized(), y);

			Vect P=model.getElementCenter(i);

			for(int j=0; j<K;j++){
				Vect v=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(v.v3()));
				
				p++;
				
			}
		
			}

		int[][] localIndFace={{0,1,2},{2,3,0},{4,5,6}};
		int[][] localIndEdge={{0,1},{1,2},{0,3},{4,5},{5,6},{6,4}};
	
		p=0;
		ix=0;
	
		 int m=0;
		 
		for(int i=0; i<N;i++){
		
		for(int j=0; j<3;j++){
					for(int k=0; k<3;k++){
						faceCoordIndices[p]=ix+localIndFace[j][k];
						p++;
					}
					
					
		}
		
		for(int j=0; j<6;j++){
			for(int k=0; k<2;k++)
			edgeCoordIndices[m++]=ix+localIndEdge[j][k];
			}
				
			
				ix+=K;
				
		}
		
		float a=.8f;
		
		Color3f[] edgeColors= new Color3f[N];
		for(int j=0; j<N;j++){
			
		edgeColors[j]=new Color3f(a*colors[j].x,a*colors[j].y,a*colors[j].z);
		}
		
		
				this.arrowFace=new IndexedTriangleArray(nVecrts,GeometryArray.COORDINATES  |
					GeometryArray.COLOR_3,9*N);
	
				this.arrowFace.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
				this.arrowFace.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
			
				this.arrows=new IndexedLineArray(nVecrts,GeometryArray.COORDINATES  |
							 GeometryArray.COLOR_3,12*N);
			
						this.arrows.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
						this.arrows.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);


				this.arrows.setCoordinates(0, coords);		
				this.arrows.setCoordinateIndices(0, edgeCoordIndices);	
				this.arrows.setColors(0, edgeColors);	
				this.arrows.setColorIndices(0, edgeColorIndices);		
			
			
				this.arrowFace.setCoordinates(0, coords);				
				this.arrowFace.setCoordinateIndices(0, faceCoordIndices);				
				this.arrowFace.setColors(0, colors);
				this.arrowFace.setColorIndices(0, faceColorIndices);
			
			
				LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);
		
				
				Appearance app=new Appearance();

				
				app.setLineAttributes(la);
				
				app.setRenderingAttributes(fieldRA);


				this.vectField=new Shape3D(this.arrowFace,app);
				//this.vectField.insertGeometry(this.arrows,0);
				

				this.vectField.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
				
				arrowEdges=new Shape3D(arrows,app);


				addChild(this.vectField);
				
				addChild(arrowEdges);

	}
	
	
	public void setNodalField2D1(Model model,ColorBar cBar){

		int vmode=this.fieldMode;
		int[] nn=model.getRegNodes(nr);
		int N=0;
		int ix=0;
		for(int i=0;i<nn.length;i++){
			int nx=nn[i];
			
			Vect v=model.node[nx].getNodalVect(vmode);
			if(v==null) continue;
			double scale=v.norm();
			if( scale>0 ) 
				{N++;
				model.node[nx].setShowVectField(true);
						
				}
			else 
				model.node[nx].setShowVectField(false);
		
			ix++;
		
		}
		nArrows=N;

		if(N==0) return;
		
		int K=7;
		double h=0.5*(model.maxEdgeLength+model.minEdgeLength);
		double dx=h/15;
		double h1=8*dx;
	
		Vect[] av=new Vect[K];
		
		
		av[0]=new Vect(-dx,0);
		av[1]=new Vect(dx,0);
		av[2]=new Vect(dx,h1);
		av[3]=new Vect(-dx,h1);
		av[4]=new Vect(2*dx,h1);
		av[5]=new Vect(0,h);
		av[6]=new Vect(-2*dx,h1);
		
			
	
		
		int nVecrts=K*N;
		
		P3f[] coords=new P3f[nVecrts];
		int[] faceCoordIndices=new int[9*N];	
		
		int[] edgeCoordIndices=new int[12*N];
		
		Color3f[] colors= new Color3f[N];
		
		int[] faceColorIndices=new int[9*N];	
		int[] edgeColorIndices=new int[12*N];	

	
		Vect y=new Vect(0,1);

		int p=0;
		
		int iy=0,iz=0,iw=0;
		ix=0;

		for(int i=0;i<nn.length;i++){
			int nx=nn[i];
	
			if(!model.node[nx].toShowVectField()) continue;
		
	
			
			Vect v=model.node[nx].getNodalVect(vmode);
			
			double scale=v.norm();
			
			colors[ix]=new Color3f(cBar.getColor(scale));

			
			for(int j=0; j<9;j++){
				faceColorIndices[iy++]=ix;
			}
			
					
			for(int j=0; j<12;j++){
				edgeColorIndices[iz++]=ix;
			}
			
		
			
			ix++;
			
			Mat R=util.rotMat2D(v.normalized(), y);

			Vect P=model.node[nx].getCoord();

			for(int j=0; j<K;j++){
				Vect w=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(w).v3());
				
				p++;
				
			}
		
			}

		int[][] localIndFace={{0,1,2},{2,3,0},{4,5,6}};
		int[][] localIndEdge={{0,1},{1,2},{0,3},{4,5},{5,6},{6,4}};
	
		p=0;
		ix=0;
	
		 int m=0;
		 
		for(int i=0; i<N;i++){
		
		for(int j=0; j<3;j++){
					for(int k=0; k<3;k++){
						faceCoordIndices[p]=ix+localIndFace[j][k];
						p++;
					}
					
					
		}
		
		for(int j=0; j<6;j++){
			for(int k=0; k<2;k++)
			edgeCoordIndices[m++]=ix+localIndEdge[j][k];
			}
				
			
				ix+=K;
				
		}
		
		float a=.8f;
		
		Color3f[] edgeColors= new Color3f[N];
		for(int j=0; j<N;j++){
			
		edgeColors[j]=new Color3f(a*colors[j].x,a*colors[j].y,a*colors[j].z);
		}
		
		
				this.arrowFace=new IndexedTriangleArray(nVecrts,GeometryArray.COORDINATES  |
					GeometryArray.COLOR_3,9*N);
	
				this.arrowFace.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
				this.arrowFace.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
			
				this.arrows=new IndexedLineArray(nVecrts,GeometryArray.COORDINATES  |
							 GeometryArray.COLOR_3,12*N);
			
						this.arrows.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
						this.arrows.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);


				this.arrows.setCoordinates(0, coords);		
				this.arrows.setCoordinateIndices(0, edgeCoordIndices);	
				this.arrows.setColors(0, edgeColors);	
				this.arrows.setColorIndices(0, edgeColorIndices);		
			
			
				this.arrowFace.setCoordinates(0, coords);				
				this.arrowFace.setCoordinateIndices(0, faceCoordIndices);				
				this.arrowFace.setColors(0, colors);
				this.arrowFace.setColorIndices(0, faceColorIndices);
			
			
				LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);
		
				
				Appearance app=new Appearance();

				
				app.setLineAttributes(la);
				
				app.setRenderingAttributes(fieldRA);


				this.vectField=new Shape3D(this.arrowFace,app);
				//this.vectField.insertGeometry(this.arrows,0);
				

				this.vectField.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
				
				arrowEdges=new Shape3D(arrows,app);


				addChild(this.vectField);
				
				addChild(arrowEdges);

	}
	
	public void setNodalField3D0(Model model,ColorBar cBar,boolean all){

		int mode=this.fieldMode;
		allNodesVect=all;
		
		int[] nn=new int[1];
		if(all)
		nn=model.getRegNodes(nr);
		else
		nn=this.surfVertNumb;
	
		
		int N=0;
		
		int ix=0;
		for(int i=0;i<nn.length;i++){
			int nx=nn[i];

			Vect v=model.node[nx].getNodalVect(mode);
			if(v==null) {continue;}
			

			
			double scale=v.norm();
			if( scale>0 ) 
				{N++;
				model.node[nx].setShowVectField(true);
						
				}
			else 
				model.node[nx].setShowVectField(false);
		
			ix++;
		
		}
		
		nArrows=N;
		
		if(N==0) return;
		
		int K=10;
		double d=0.2*(model.maxEdgeLength+model.minEdgeLength);
		

		double h1=.8*d;
		
		double dx=d/16;
		
		Vect[] av=new Vect[K];
		
		
		av[0]=new Vect(-dx,-dx,h1);
		av[1]=new Vect(dx,-dx,h1);
		av[2]=new Vect(dx,dx,h1);
		av[3]=new Vect(-dx,dx,h1);
		av[4]=new Vect(0,0,d);
		
		double dx2=dx/2;
		av[5]=new Vect(0,0,0);
		av[6]=new Vect(-dx2,-dx2,h1);
		av[7]=new Vect(dx2,-dx2,h1);
		av[8]=new Vect(dx2,dx2,h1);
		av[9]=new Vect(-dx2,dx2,h1);
	
			
		
		int nVecrts=K*N;
		int K1=24,K1t=K1/3;
		int K2=4,K3=24,K3h=K3/2;
		
		P3f[] coords=new P3f[nVecrts];
		

		int[] edgeCoordIndices=new int[K3*N];
		
		Color3f[] colors= new Color3f[N];
		
		int[] edgeColorIndices=new int[K3*N];	

	

		Vect z=new Vect(0,0,1);
		

		int p=0;
		
		int iy=0,iz=0,iw=0;
		ix=0;

		for(int i=0;i<nn.length;i++){
	
			
			int nx=nn[i];
			
			if(!model.node[nx].toShowVectField()) continue;
		
	
			
			Vect v=model.node[nx].getNodalVect(mode);
		
			
			double scale=v.norm();
			
			colors[ix]=new Color3f(cBar.getColor(scale));
		
					
			for(int j=0; j<K3;j++){
				edgeColorIndices[iz++]=ix;
			}
			
		
			
			ix++;
			
			Mat R=util.rotMat(v.normalized(), z);

			Vect P=model.node[nx].getCoord();

			for(int j=0; j<K;j++){
				Vect w=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(w));
				
				p++;
				
			}
		
			}

		
			

		int[][] localIndEdge={{0,1},{1,2},{2,3},{3,0},{0,4},{1,4},{2,4},{3,4},{5,6},{5,7},{5,8},{5,9}};
	
		


		p=0;
		ix=0;
		 iy=0;
		 int t=0,m=0;
		 
		for(int i=0; i<N;i++){
		
		
		for(int j=0; j<K3h;j++){
			for(int k=0; k<2;k++)
			edgeCoordIndices[m++]=ix+localIndEdge[j][k];
			}
				
			
				ix+=K;
				iy+=K2;
		}
		
		float a=.8f;
	
		
		
				this.arrows=new IndexedLineArray(nVecrts,GeometryArray.COORDINATES  |
							 GeometryArray.COLOR_3,K3*N);
			
						this.arrows.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
						this.arrows.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);


				this.arrows.setCoordinates(0, coords);		
				this.arrows.setCoordinateIndices(0, edgeCoordIndices);	
				this.arrows.setColors(0, colors);	
				this.arrows.setColorIndices(0, edgeColorIndices);		
				
			
			
				

			
				LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);
	
				
				Appearance app=new Appearance();

				
				app.setLineAttributes(la);
				
				app.setRenderingAttributes(fieldRA);


				arrowEdges=new Shape3D(arrows,app);

			
				addChild(arrowEdges);

	}
	
	public void setNodalField3D1(Model model,ColorBar cBar,boolean all){
		int mode=this.fieldMode;
		allNodesVect=all;
		
		int[] nn=new int[1];
		if(all)
		nn=model.getRegNodes(nr);
		else
		nn=this.surfVertNumb;
	
		
		int N=0;
		
		int ix=0;
		for(int i=0;i<nn.length;i++){
			int nx=nn[i];

			Vect v=model.node[nx].getNodalVect(mode);
			if(v==null) {continue;}
			

			
			double scale=v.norm();
			if( scale>0 ) 
				{N++;
				model.node[nx].setShowVectField(true);
						
				}
			else 
				model.node[nx].setShowVectField(false);
		
			ix++;
		
		}
		
		nArrows=N;
		
		if(N==0) return;
		
		int K=10;
		double d=0.2*(model.maxEdgeLength+model.minEdgeLength);
		

		double h1=.8*d;
		
		double dx=d/16;
		
		Vect[] av=new Vect[K];
		
		
		av[0]=new Vect(-dx,-dx,h1);
		av[1]=new Vect(dx,-dx,h1);
		av[2]=new Vect(dx,dx,h1);
		av[3]=new Vect(-dx,dx,h1);
		av[4]=new Vect(0,0,d);
		
		double dx2=dx/2;
		av[5]=new Vect(0,0,0);
		av[6]=new Vect(-dx2,-dx2,h1);
		av[7]=new Vect(dx2,-dx2,h1);
		av[8]=new Vect(dx2,dx2,h1);
		av[9]=new Vect(-dx2,dx2,h1);
	
			
		
		int nVecrts=K*N;
		int K1=24,K1t=K1/3;
		int K2=4,K3=24,K3h=K3/2;
		
		P3f[] coords=new P3f[nVecrts];
		int[] sideCoordIndices=new int[K1*N];	
	
		int[] baseCoordIndices=new int[K2*N];

		int[] edgeCoordIndices=new int[K3*N];
		
		Color3f[] colors= new Color3f[N];
		
		int[] sideColorIndices=new int[K1*N];	
		int[] edgeColorIndices=new int[K3*N];	
		int[] baseColorIndices=new int[K2*N];	

	

		Vect z=new Vect(0,0,1);
		

		int p=0;
		
		int iy=0,iz=0,iw=0;
		ix=0;

		for(int i=0;i<nn.length;i++){
	
			
			int nx=nn[i];
			
			if(!model.node[nx].toShowVectField()) continue;
		
	
			
			Vect v=model.node[nx].getNodalVect(mode);
		
			
			double scale=v.norm();
			
			colors[ix]=new Color3f(cBar.getColor(scale));
			
			for(int j=0; j<K1;j++){
				sideColorIndices[iy++]=ix;
			}
			
					
			for(int j=0; j<K3;j++){
				edgeColorIndices[iz++]=ix;
			}
			
			for(int j=0; j<K2;j++){
				baseColorIndices[iw++]=ix;
			}
			
			ix++;
			
			Mat R=util.rotMat(v.normalized(), z);
			
	
	
			Vect P=model.node[nx].getCoord();

			for(int j=0; j<K;j++){
				Vect w=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(w));
				
				p++;
				
			}
		
			}

		
			

		int[][] localInd={{0,1,4},{1,2,4},{2,3,4},{3,0,4},{5,7,6},{5,8,7},{5,9,8},{5,6,9}};
		int[][] localIndEdge={{0,1},{1,2},{2,3},{3,0},{0,4},{1,4},{2,4},{3,4},{5,6},{5,7},{5,8},{5,9}};
	
		


		p=0;
		ix=0;
		 iy=0;
		 int t=0,m=0;
		 
		for(int i=0; i<N;i++){
		
		for(int j=0; j<K1t;j++){
					for(int k=0; k<3;k++){
						sideCoordIndices[p]=ix+localInd[j][k];
						p++;
					}
					
					if(j<K2)
					baseCoordIndices[t++]=ix+3-j;
					
		}
		
		for(int j=0; j<K3h;j++){
			for(int k=0; k<2;k++)
			edgeCoordIndices[m++]=ix+localIndEdge[j][k];
			}
				
			
				ix+=K;
				iy+=K2;
		}
		
		float a=.8f;
		
		Color3f[] edgeColors= new Color3f[N];
		for(int j=0; j<N;j++){
			
		edgeColors[j]=new Color3f(a*colors[j].x,a*colors[j].y,a*colors[j].z);
		}
		
			this.arrowFace=new IndexedTriangleArray(nVecrts,GeometryArray.COORDINATES  |
					/*GeometryArray.NORMALS | */GeometryArray.COLOR_3,K1*N);
	
				this.arrowFace.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
				this.arrowFace.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
				
				
				
				this.arrowBase=new IndexedQuadArray(nVecrts,GeometryArray.COORDINATES  |
						/*GeometryArray.NORMALS |*/ GeometryArray.COLOR_3,K2*N);
		
					this.arrowBase.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
					this.arrowBase.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
					
				this.arrows=new IndexedLineArray(nVecrts,GeometryArray.COORDINATES  |
							 GeometryArray.COLOR_3,K3*N);
			
						this.arrows.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
						this.arrows.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);


				this.arrows.setCoordinates(0, coords);		
				this.arrows.setCoordinateIndices(0, edgeCoordIndices);	
				this.arrows.setColors(0, edgeColors);	
				this.arrows.setColorIndices(0, edgeColorIndices);		
				
			
			
				this.arrowFace.setCoordinates(0, coords);				
			//	this.arrowFace.setNormals(0, normals);
				this.arrowFace.setCoordinateIndices(0, sideCoordIndices);				
			//	this.arrowFace.setNormalIndices(0, normalIndices);
				this.arrowFace.setColors(0, colors);
				this.arrowFace.setColorIndices(0, sideColorIndices);
				
				this.arrowBase.setCoordinates(0, coords);
				this.arrowBase.setCoordinateIndices(0, baseCoordIndices);
				//this.arrowBase.setNormals(0, baseNormals);
				//this.arrowBase.setNormalIndices(0, baseNormalIndices);			
				this.arrowBase.setColors(0, colors);
				this.arrowBase.setColorIndices(0, baseColorIndices);
				

			
				LineAttributes la=new LineAttributes(1.0f,LineAttributes.PATTERN_SOLID,false);
				
			/*	Color3f color3=new Color3f(.5f,.3f,.3f);
				
				Color3f ambientColour = new Color3f(color3);
				Color3f emissiveColour = new Color3f(.1f*color3.getX(),.1f*color3.getY(), .1f*color3.getZ());
				Color3f specularColour = new Color3f(.0f, .0f, .0f);
				Color3f diffuseColour =new Color3f(color3);
				float shininess = 1.0f;


				Material material=new Material(ambientColour, emissiveColour,
						diffuseColour, specularColour, shininess);*/
				
				Appearance app=new Appearance();

			//	app.setMaterial(material);
				
				app.setLineAttributes(la);
				
				app.setRenderingAttributes(fieldRA);


				this.vectField=new Shape3D(this.arrowFace,app);
				this.vectField.insertGeometry(this.arrowBase,0);
			//	this.vectField.insertGeometry(this.arrows,0);
				

				this.vectField.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
				
				arrowEdges=new Shape3D(arrows,app);


				addChild(this.vectField);
				
				addChild(arrowEdges);

	}
	
	
	public void scaleVectField(Model model,double a){

		a*=5;

	

		int[] nn=model.getRegNodes(nr);
		int N=nn.length;
		
		nArrows=N;
		
		if(N==0) return;
		
		P3f[] coords=new P3f[4*N];
		int p=0;
		double d=.002*a;
		for(int i=0;i<N;i++){
			
			Vect v=model.node[nn[i]].getCoord().v3();
				coords[p]=new P3f(v);
				p++;
				coords[p]=new P3f(v.add(new Vect(0,0,d)));
				p++;
				coords[p]=new P3f(v.add(new Vect(.25*d,0,.75*d)));
				p++;
				coords[p]=new P3f(v.add(new Vect(-.25*d,0,.75*d)));
				p++;
			}
				
			

				this.arrows.setCoordinates(0, coords);


	}
	
	
	
	
	

public void rescaleVectField(Model model,ColorBar cBar,double a){
	if(this.fieldMode==4)
	{
		if(arrMode<2)
		rescaleElementField2D(model,cBar,a);
		else
		//rescaleElementField3D(model,cBar,a);
		rescaleElementField3DK(model,cBar,a);
		
	}
	else
		if(model.dim==2)
			rescaleNodalField2D(model,cBar,a);
		else		
			rescaleNodalField3D(model,cBar,a,allNodesVect);
		
}


	
public void rescaleElementField3D(Model model,ColorBar cBar,double a){

	if(this.arrMode==4){
		rescaleElementField3DArrow(model, cBar, a);
		}
	
	int N=this.nArrows;

	if(N==0) return;
		
		int K=5;
		double d=a*0.5*(model.maxEdgeLength+model.minEdgeLength);
		double dx=d/8;
		
		Vect[] av=new Vect[K];
		av[0]=new Vect(-dx,-dx,0);
		av[1]=new Vect(dx,-dx,0);
		av[2]=new Vect(dx,dx,0);
		av[3]=new Vect(-dx,dx,0);
		av[4]=new Vect(0,0,d);
	
		P3f[] coords=new P3f[K*N];
		Color3f[] faceColors=new Color3f[1];
		if(arrMode==3)faceColors=new Color3f[N];
		Color3f[] edgeColors= new Color3f[N];
		
		Vect z=new Vect(0,0,1);
		

		int p=0;
		int ix=0;
		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++){
			
			if(!model.element[i].toShowVectField()) continue;
		
			Vect B=model.element[i].getB();
			
			double scale=B.norm();
			

			edgeColors[ix]=new Color3f(cBar.getColor(scale).darker());
			if(arrMode==3)
				faceColors[ix]=new Color3f(cBar.getColor(scale));
			ix++;
					
			Mat R=util.rotMat(B.normalized(), z);

			Vect P=model.getElementCenter(i).v3();
			
			if(model.fluxNormalized){

				if(scale/model.Bmax>1e-2)
				scale=vScale/10;
				else
					scale=vScale/100;
					
			}
			
		

			for(int j=0; j<5;j++){
				Vect v=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(v));		
								
				p++;
			
				
			}
		
		
			}

		

		this.arrows.setCoordinates(0, coords);
		this.arrows.setColors(0, edgeColors);
	
			
		if(arrMode==3){
		this.arrowFace.setCoordinates(0, coords);
		this.arrowBase.setCoordinates(0, coords);

		this.arrowFace.setColors(0, faceColors);
		this.arrowBase.setColors(0, faceColors);
		}
		
	
	}

public void rescaleElementField3DK(Model model,ColorBar cBar,double a){
	
	int K1=this.nArrowHeadDivs;
	
	if(this.arrMode==4){
		rescaleElementField3DArrow(model, cBar, a);
		}
	
	int N=this.nArrows;

	if(N==0) return;
		
		int K=K1+1;
		if(K1>0) K++;
		
		double d=a*0.5*(model.maxEdgeLength+model.minEdgeLength);
		double dx=d/6;
		
		Vect[] av=new Vect[K];
		
		for(int i=0;i<K1;i++){
			double x=dx*cos(i*2*PI/K1);
			double y=dx*sin(i*2*PI/K1);
			av[i]=new Vect(x,y,0);
		}
		av[K1]=new Vect(0,0,d);
		if(K1>4) 		av[K-1]=new Vect(0,0,0);
	
		P3f[] coords=new P3f[K*N];
		Color3f[] faceColors=new Color3f[1];
		if(arrMode==3)faceColors=new Color3f[N];
		Color3f[] edgeColors= new Color3f[N];
		
		Vect z=new Vect(0,0,1);
		

		int p=0;
		int ix=0;
		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++){
			
			if(!model.element[i].toShowVectField()) continue;
		
			Vect B=model.element[i].getB();
			
			double scale=B.norm();
			

			edgeColors[ix]=new Color3f(cBar.getColor(scale).darker());
			if(arrMode==3)
				faceColors[ix]=new Color3f(cBar.getColor(scale));
			ix++;
					
			Mat R=util.rotMat(B.normalized(), z);

			Vect P=model.getElementCenter(i).v3();
			
			if(model.fluxNormalized){

				if(scale/model.Bmax>1e-2)
				scale=vScale/10;
				else
					scale=vScale/100;
					
			}
			
		

			for(int j=0; j<K;j++){
				Vect v=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(v));		
								
				p++;
			
				
			}
		
		
			}

		

		this.arrows.setCoordinates(0, coords);
		this.arrows.setColors(0, edgeColors);
	
			
		if(arrMode==3){
		this.arrowFace.setCoordinates(0, coords);
		this.arrowBase.setCoordinates(0, coords);

		this.arrowFace.setColors(0, faceColors);
		this.arrowBase.setColors(0, faceColors);
		}
		
	
	}



public void rescaleNodalField3D(Model model,ColorBar cBar,double a,boolean all){

	int N=this.nArrows;
	
	int mode=this.fieldMode;

	if(N==0) return;

	int K=10;
	
	
	double d=a*0.25*(model.maxEdgeLength+model.minEdgeLength);
	


	double h1=.8*d;
	
	double dx=d/32;
	
	Vect[] av=new Vect[K];
	
	
	av[0]=new Vect(-dx,-dx,h1);
	av[1]=new Vect(dx,-dx,h1);
	av[2]=new Vect(dx,dx,h1);
	av[3]=new Vect(-dx,dx,h1);
	av[4]=new Vect(0,0,d);
	
	double dx2=dx/2;
	av[5]=new Vect(0,0,0);
	av[6]=new Vect(-dx2,-dx2,h1);
	av[7]=new Vect(dx2,-dx2,h1);
	av[8]=new Vect(dx2,dx2,h1);
	av[9]=new Vect(-dx2,dx2,h1);

		
	
	int nVecrts=K*N;
	
	P3f[] coords=new P3f[nVecrts];

	Color3f[] edgeColors= new Color3f[N];
	Color3f[] faceColors=new Color3f[1];
	if(arrMode==3)faceColors=new Color3f[N];
		
		Vect z=new Vect(0,0,1);
		

		int p=0;
		
		int[] nn=new int[1];
		if(all)
		nn=model.getRegNodes(nr);
		else
		nn=this.surfVertNumb;

		int ix=0;
	for(int i=0;i<nn.length;i++){
			
			int nx=nn[i];
			
			if(!model.node[nx].toShowVectField()) continue;
		
			
			Vect v=model.node[nx].getNodalVect(mode);
		
			
			double scale=v.norm();

			edgeColors[ix]=new Color3f(cBar.getColor(scale).darker());
			if(arrMode==3)
				faceColors[ix]=new Color3f(cBar.getColor(scale));
			ix++;
				
			Mat R=util.rotMat(v.normalized(), z);

			Vect P=model.node[nx].getCoord();

			for(int j=0; j<K;j++){
	
				Vect w=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(w));		
								
				p++;	
				
			}	
		
			}
		
			this.arrows.setCoordinates(0, coords);
			this.arrows.setColors(0, edgeColors);
			
		if(arrMode==3){
		this.arrowFace.setCoordinates(0, coords);
		this.arrowFace.setColors(0, faceColors);
		
		this.arrowBase.setCoordinates(0, coords);
		this.arrowBase.setColors(0, faceColors);
		}
	
	}


 public void rescaleElementField2D(Model model,ColorBar cBar,double a)  throws NullPointerException{

	int N=this.nArrows;

	if(N==0) return;
	
	int K=7;
	double h=a*0.5*(model.maxEdgeLength+model.minEdgeLength);
	double dx=h/15;
	double h1=8*dx;

	Vect[] av=new Vect[K];
	
	
	av[0]=new Vect(-dx,0);
	av[1]=new Vect(dx,0);
	av[2]=new Vect(dx,h1);
	av[3]=new Vect(-dx,h1);
	av[4]=new Vect(2*dx,h1);
	av[5]=new Vect(0,h);
	av[6]=new Vect(-2*dx,h1);
	
	int nVecrts=K*N;
	
	Vect y=new Vect(0,1);
	
	P3f[] coords=new P3f[nVecrts];
	Color3f[] colors= new Color3f[N];
	Color3f[] edgeColors= new Color3f[N];

	
	int ix=0;
	int p=0;
		for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++){
			
			if(!model.element[i].toShowVectField()) continue;
		
			Vect B=model.element[i].getB();
			
			double scale=B.norm();

			colors[ix]=new Color3f(cBar.getColor(scale));
			edgeColors[ix++]=new Color3f(cBar.getColor(scale).darker());
	
			Mat R=util.rotMat2D(B.normalized(), y);

			Vect P=model.getElementCenter(i);

			for(int j=0; j<K;j++){
				Vect v=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(v.v3()));		
								
				p++;
			
				
			}
		
		
			}

		
		this.arrows.setCoordinates(0, coords);
		this.arrows.setColors(0, edgeColors);

		if(arrMode==1){
		this.arrowFace.setCoordinates(0, coords);
		this.arrowFace.setColors(0, colors);
		}
	
	}
	

public void rescaleNodalField2D(Model model,ColorBar cBar,double a){


	
	int N=this.nArrows;

	if(N==0) return;
	
	int K=7;
	double h=a*0.5*(model.maxEdgeLength+model.minEdgeLength);
	double dx=h/15;
	double h1=8*dx;

	Vect[] av=new Vect[K];
	
	
	av[0]=new Vect(-dx,0);
	av[1]=new Vect(dx,0);
	av[2]=new Vect(dx,h1);
	av[3]=new Vect(-dx,h1);
	av[4]=new Vect(2*dx,h1);
	av[5]=new Vect(0,h);
	av[6]=new Vect(-2*dx,h1);
	
	int nVecrts=K*N;
	
	Vect y=new Vect(0,1);
	
	P3f[] coords=new P3f[nVecrts];
	Color3f[] colors= new Color3f[N];
	Color3f[] edgeColors= new Color3f[N];

	
	int p=0;
	int ix=0;
int[] nn=model.getRegNodes(nr);
	
	for(int i=0;i<nn.length;i++){
		int nx=nn[i];

		if(!model.node[nx].toShowVectField()) continue;
	

		
			Vect v=model.node[nx].getNodalVect(fieldMode);
			
			double scale=v.norm();
			
			
			colors[ix]=new Color3f(cBar.getColor(scale));
			edgeColors[ix++]=new Color3f(cBar.getColor(scale).darker());
		
			Mat R=util.rotMat2D(v.normalized(), y);

			Vect P=model.node[nx].getCoord();

			for(int j=0; j<K;j++){
				Vect w=R.mul(av[j].times(scale));
				coords[p]=new P3f(P.add(w).v3());		
			
				p++;
			
				
			}
		
	}
			

		
		this.arrows.setCoordinates(0, coords);
		this.arrows.setColors(0, edgeColors);
		if(arrMode==1){
		this.arrowFace.setCoordinates(0, coords);
		this.arrowFace.setColors(0, colors);

		}
	
	}
	
	
	
	public void showVectField(boolean b)
	{
		if(b)
			addChild(vectField);
		else
			removeChild(vectField);
			
	}
	
	public void switchVisibility()
	{
		this.showRegion=!this.showRegion;
		this.showRegFace=this.showRegion;
		this.showRegEdge=this.showRegion;

	
			
	}

}
