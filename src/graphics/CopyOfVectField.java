package graphics;
import java.awt.Color;

import math.Mat;
import math.Vect;
import math.util;
import fem.Model;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Matrix3d;

public class  CopyOfVectField extends TransformGroup{


	public TransformGroup field;
	public Arrow[] arrow;
	public int nRegNodes,mode,vectMode,dim;
	public Vect[] P;
	public Vect[] V;
	public int[] neNumb;
	public double vScale=1;

	public CopyOfVectField(){	}

	public CopyOfVectField(Model model, ColorBar cBar,int[] surfVertNumbs,int nr,int mode,int vectMode){


		this.vectMode=vectMode;
		this.mode=mode;
		this.dim=model.dim;
		if(mode<3) setNodalField(model,surfVertNumbs,cBar,nr,mode,vectMode);
		else if(mode==4)setElementField(model,cBar,nr,mode,vectMode);
		

	}

	public CopyOfVectField(Model model,ColorBar cBar, int nr,int mode,int vectMode)
	{

	

		this.dim=model.dim;
		nRegNodes=0;
		this.vectMode=vectMode;
		this.mode=mode;

		if(mode<3) setNodalField(model,cBar,nr,mode,vectMode);
		else if(mode==4)setElementField(model,cBar,nr,mode,vectMode);
		

	}

		private void setNodalField(Model model,ColorBar cBar,int nr,int mode,int vectMode){
			
			int[] regNodes=model.getRegNodes(nr);
			setNodalField(model,regNodes,cBar,nr,mode,vectMode);

	}

	private void setNodalField(Model model,int[] regNodes,ColorBar cBar,int nr,int mode,int vectMode){
		
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		
		int nodeNumber;
		boolean[] counted=new boolean[model.numberOfNodes+1];

		
		if(regNodes==null){
			for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++)
			{
				for(int j=0;j<model.nElVert;j++)
				{

					nodeNumber=model.element[i].getVertNumb(j);
					if(!counted[nodeNumber]){
						
						if(model.node[nodeNumber].getNodalVect(mode)==null) continue;
						double Fn=model.node[nodeNumber].getNodalVect(mode).norm();
						if(Fn==0) continue;
						counted[nodeNumber]=true;
						nRegNodes++;
					}
				}
			}
			
		}
		else {
			for(int i=0;i<regNodes.length;i++){
			
					if(model.node[regNodes[i]].getNodalVect(mode)!=null)
						if(model.node[regNodes[i]].getNodalVect(mode).norm()!=0)
						counted[regNodes[i]]=true;
			}
			
			for(int i=1;i<counted.length;i++)
				if(counted[i]) nRegNodes++;
		}

		P=new Vect[nRegNodes];
		V=new Vect[nRegNodes];

		neNumb=new int[nRegNodes];

		int ix=0;

		for(int i=1;i<=model.numberOfNodes;i++)
			if(counted[i])
			{

				P[ix]=model.node[i].getCoord();
				//V[ix]=model.node[i].getNodalVect(mode);
				V[ix]=new Vect(0,0,vScale);
				neNumb[ix]=i;
				
				ix++;


			}


			
			util.pr(" number of arrows: "+ix);



		arrow=new Arrow[nRegNodes];
		double Vmax=1;
	
		 if(mode==0) {Vmax=model.FreluctMax;}
		else if(mode==1) { Vmax=model.FmsMax;}
		else if(mode==2) { Vmax=model.uMax;}
		else if(mode==3) { Vmax=model.FedMax;}
		else if(mode==4) { Vmax=model.Bmax;}

		if(Vmax>0)
			vScale=.2*(model.minEdgeLength+model.maxEdgeLength)/Vmax;
		double magn;
	
		Color color;


		field=new TransformGroup();
		for(int j=0;j<nRegNodes;j++){	
		/*
			if(V[j]==null)
				V[j]=new Vect(3);
			magn=V[j].norm();
			//magn=1;
			arrowSize=vScale*magn;
			color=cBar.getColor(magn);*/
			arrow[j]=new Arrow(vectMode);
			field.addChild(arrow[j]);

		}

	}

	private void setElementField(Model model,ColorBar cBar,int nr,int mode,int vectMode){
		boolean[] counted;

	counted=new boolean[model.region[nr].getNumbElements()+1];

	int p=0;
	int ix=0;
	for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++)
	{
		p++;
		if(!model.element[i].hasB() || model.element[i].getB().norm()<model.Bmax/100) continue;
		counted[p]=true;
		ix++;

	}

	nRegNodes=ix;
	P=new Vect[nRegNodes];
	V=new Vect[nRegNodes];

	neNumb=new int[nRegNodes];
	p=0;
	ix=0;
	for(int i=model.region[nr].getFirstEl();i<=model.region[nr].getLastEl();i++)
	{
		p++;
		if(!counted[p]) continue;

		P[ix]=model.getElementCenter(i);

		//V[ix]=model.element[i].getB();
		V[ix]=new Vect(0,0,vScale);

		neNumb[ix]=i;
		ix++;
	}


	arrow=new Arrow[nRegNodes];
	double Vmax=model.Bmin;
	Vmax=model.Bmax;

	if(Vmax>0)
		vScale=.2*(model.minEdgeLength+model.maxEdgeLength)/Vmax;


	field=new TransformGroup();
	for(int j=0;j<nRegNodes;j++){	

		arrow[j]=new Arrow(vectMode);
		field.addChild(arrow[j]);

	}

	}

/*	public void rescale(double a){
		
		Transform3D tr;
		
		double vScale=a/this.vScale;
		
		for(int j=0;j<this.nRegNodes;j++){
			
			tr=new Transform3D();
			this.arrow[j].getTransform(tr);
								
				tr.setScale(vScale);
				
				this.arrow[j].setTransform(tr);
			
	
		}
	}*/


	public void scale(Model model,Mat rotMat,ColorBar cBar, int fieldMode){
	
		Transform3D tr;

	for(int j=0;j<this.getnRegNode();j++){
		
		tr=new Transform3D();
		Vect v=new Vect(0,0,1);
		//if(model.node[this.nodalField[ir].nodeNumb[j]].isDeformable())
		if(fieldMode==4)
			v=model.element[this.neNumb[j]].getB();
		else 
			v=model.node[this.neNumb[j]].getNodalVect(fieldMode);
		
		v.hshow();
		
		double scale=v.norm();
		tr.setScale(scale*vScale);
		
		util.pr(scale*vScale);

		if(scale==0) continue;


		this.arrow[j].setColor(cBar.getColor(scale));
		
	
		Matrix3d M =util.mat3d(v.v3(),new Vect(0,0,1));	

		tr.setRotation(M);

			Mat R=new Mat(3,3);
			for(int m=0;m<3;m++)
			for(int n=0;n<3;n++)
			R.el[m][n]=M.getElement(m, n);
			

		

			Vect P;
			if(fieldMode==4)
				P=model.getElementCenter(this.neNumb[j]);
			else
				P=model.node[this.neNumb[j]].getCoord();		
			
			
			if(rotMat!=null)
			{
				P=rotMat.mul(P);
			}

			tr.setTranslation(new V3f(P.v3()));			
			
	this.arrow[j].setTransform(tr);
	
	}


	}
	
	public void repaint(ColorBar cBar){
		

		
		for(int j=0;j<this.nRegNodes;j++){
			
			double Vn=V[j].norm();
			Color color=cBar.getColor(Vn);

			arrow[j].setColor(color);
		
	
		}
		

	}
	
	

	
	public int getnRegNode(){
		return this.nRegNodes;
	}

	public void showField(boolean b){
		if(b){

			if(field.getParent()==null)		
				addChild(field);

		}
		else{

			removeChild(field);
		}

	}
	public double getVectScale(){
		return this.vScale;
	}



}
	