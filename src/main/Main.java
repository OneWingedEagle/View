package main;
import drawingPanel.*;
import panels.Console;
import fem.*;
import graphics.ColorBar;
import math.*;

import static java.lang.Math.*;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.media.j3d.Transform3D;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main implements ActionListener, ItemListener,ChangeListener, DropTargetListener{

	private GUI gui;

	private Model model;
	private Thread thread;
	private int mheavy=1000,interaction;
	private boolean heavy,resultReady;
	private DecimalFormat formatter=new DecimalFormat("0.000E00");
	private String path = System.getProperty("user.dir") + "\\model.txt";
	DecimalFormat df=new DecimalFormat("#.00");


	Main()
	{		
		model=new Model();
		this.gui=new GUI(this.path);
		this.gui.setVisible(true);
		this.gui.vwp.fv.bGetValue.addActionListener(this);
		this.gui.vwp.bDeform.addActionListener(this);
		this.gui.vwp.stressDist.addItemListener(this);
		this.gui.vwp.bRotate.addActionListener(this);
		this.gui.vwp.bApplyVscale.addActionListener(this);
		this.gui.vwp.bAnimation.addActionListener(this);
		
		
		this.gui.vwp.jslider.addChangeListener(this );
		
		//gui.vwp.divert=false;
		/*Main.this.model.filePath= System.getProperty("user.dir") + "\\motWithRot3DCut.txt";
		loadModel();


		this.gui.vwp.bFullScreen.doClick();
		wait(5000);

		this.gui.vwp.bRotate.doClick();
		wait(10000);
		System.exit(0);*/
		new DropTarget(this.gui.vwp.canvas, this);

	}	



	public static void main(String[] args){

		new Main();
	}



	public void loadTransientTemp()  {

		Thread tr = new Thread() {

			public void run() {

				//	
				//	String app=System.getProperty("user.dir")+"\\transTmp.txt";		
				String app="C:\\JavaProjects\\proj7\\results2D\\appen.txt";		

				gui.vwp.runMotor=!gui.vwp.runMotor;			

				IntVect iv=new IntVect();

				double[][] nv=model.loadBunchScalar(app, iv,0);

				int[] nn=iv.el;

				int Lt=nv[0].length;
				int N=nn.length;
				double vm=0,vmin=1e40;

				for(int i=0;i<N;i++)
					for(int j=0;j<Lt;j++){

						double p=nv[i][j];
						if(p>vm) vm=p;
						if(p<vmin) vmin=p;

					}

				model.nodalScalarMax=vm;
				model.nodalScalarMin=vmin;

				int m=0;
				while(gui.vwp.runMotor){
					{

						int i=m%Lt;
						m++;
						for(int j=0;j<N;j++){

							model.node[nn[j]].scalar=nv[j][i];
							if(j==0) model.node[nn[j]].scalar=1;
							//	util.pr(model.node[nn[j]].scalar);
						}
						//	while(gui.vwp.runMotor){
						if(i==0)
							gui.vwp.paintNodalScalar(model);

						int nn2=model.numberOfRegions;

						for(int k=1;k<=nn2;k++){


							if(i>0){
								gui.vwp.surfFacets[k].reScaleNodalScalar(model,new ColorBar(0,1));
							}

						}

						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//	gui.vwp.rng+=.5*rst;

					}
				}
			}


		};

		tr.start();

	}

	public void loadModel(){	

		this.gui.vwp.loadMode();

		this.thread=new Thread(){

			@Override
			public void run(){
				System.gc();
				long m1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				

				Main.this.model.loadMesh(Main.this.model.filePath);


				long m2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				System.out.println("Used memory for  model setup: "+(m2-m1)/(1024*1024)+"MB");


				if(m2<m1 || (m2-m1)/(1024*1024)<Main.this.mheavy)
					Main.this.gui.vwp.setMesh(Main.this.model);
				else
					Main.this.heavy=true;

				Main.this.gui.setTitle("FEM Drawing Panel: "+Main.this.model.filePath);
				Main.this.gui.repaint();


				long m3 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				System.out.println("Used memory for drawing mesh: "+(m3-m2)/(1024*1024)+"MB");
				System.out.println();
				System.out.println(" Number of regions: "+Main.this.model.numberOfRegions);
				System.out.println(" Total number of elements: "+Main.this.model.numberOfElements);
				System.out.println(" Total number of nodes: "+Main.this.model.numberOfNodes);

				System.out.println();
			}

		};

		this.thread.start();


	}		


	public void loadFlux(){



		boolean result=this.model.loadFlux( this.model.filePath);
		
		this.interaction=2;

		if(result && !this.heavy){
			if(this.gui.vwp.runMotor){
				model.setNodalScalar(0);
				if(model.nAnimSteps==1)
				this.gui.vwp.paintNodalScalar(this.model);
			}
			else{
			
				this.gui.vwp.setVectField(this.model,4);
				
			}


		}	


	}	
	
	public void loadFlux(int i, double deg){



		boolean result=this.model.loadFlux( this.model.filePath,deg);

		if(result && !this.heavy){
	
				if(i==0)
				this.gui.vwp.setVectField(this.model,4);
				else
					this.gui.vwp.rescale(model);

		}	


	}	
	

	public void loadPotential(){


		boolean result=this.model.loadPotential( this.model.filePath);

		this.model.setFemCalc();

		if(result) this.model.setB();

		if(result && !this.heavy){

			this.gui.vwp.setVectField(this.model,4);
			this.gui.vwp.showVectField(true);
		}	


	}	



	public void loadTemp(){


		boolean result=this.model.loadTemper( this.model.filePath);
		if(result){

			this.gui.vwp.paintNodalScalar(this.model);
		}		
	}	

	public void loadStress(){


		boolean result=this.model.loadStress( this.model.filePath);
		if(result){
			this.gui.vwp.stressMode();

			this.gui.vwp.paintNodalScalar(this.model);
		}		
	}	

	public void loadNodalField(int mode){

		boolean result=this.model.loadNodalField( this.model.filePath, mode);


		if(result){
			interaction=2;
			this.gui.vwp.setVectField(this.model,mode);
		}	
		
		/*Vect v=new Vect(.092,0,0);
		
		for(int i=1;i<=model.numberOfNodes;i++)
			if(model.node[i].getCoord().sub(v).norm()<1e-4){
				model.node[i].getCoord().hshow();
				util.pr(i);
				model.node[i].getU().times(1e9).hshow();
				break;
			}*/
		
	
	}	



	public String readFirst(String filePath){
		try{

			Scanner scr=new Scanner(new FileReader(filePath));
			String first= scr.next();
			scr.close();
			return first;
		}
		catch(IOException e){return null;}

	}




	@Override
	public void actionPerformed(ActionEvent e)
	{	

		if(e.getSource()==this.gui.vwp.bDeform){

			interaction=1;
			gui.vwp.Vmax=1e-9*(model.maxEdgeLength+model.minEdgeLength)/model.uMax;
		
			gui.vwp.setSlider();
					
			gui.vwp.deformMesh(model);
			
		
			}
		
		else if(e.getSource()==this.gui.vwp.bAnimation){

			String data=util.getFile();
			if(data==null || data.equals("") )return;	
			
	
			model.loadAnimationData(data);

			loadModel();
			

			

			wait(2000);
			
			
			
			if(model.numberOfElements>30000)
				wait(2000);
			
			if(model.animRegs!=null && model.animRegs.length==0){
			model.animRegs=new int[model.numberOfRegions];
			for(int i=0;i<model.numberOfRegions;i++)
				model.animRegs[i]=i+1;
			}
			
			if(model.animChosenNode==-1){
			Vect v=model.loader.getVectData(model.chosenNodeCoord,model.dim);
			v.hshow();
			for(int i=1;i<=model.numberOfNodes;i++){
				if(model.node[i].getCoord().sub(v).norm()<1e-4){util.pr(i); v.hshow(); model.animChosenNode=i; break;}
			}
			}
	
			if(model.animChosenNode>0)
			model.node[model.animChosenNode].getCoord().hshow();

			if(model.animDataCode==0){
				if(model.animMode==0) fluxAnim();
				else if(model.animMode==1) animatePaint();
			}
			else if(model.animDataCode==1){
				/*if(model.animMode==0) */
				animateVectSurf();
				//else if(model.animMode==1) animateShape();
			}
			else if(model.animDataCode==2){
				if(model.animMode==0) animateVectSurf();
				else if(model.animMode==1) animateShape();
			}
			else if(model.animDataCode==3){
				animatePaint();
			}


		
		//	deformMeshAppen();

		}


		else if (e.getSource() == this.gui.vwp.bRotate){
			
			rotateRotor();
			
			/*

			model.filePath="C:\\JavaProjects\\prj6-inputVolt\\flux6A4th-0-90HalfDeg\\bunmot4thNewest.txt";
			model.filePath="C:\\JavaProjects\\prj6-inputVolt\\fluxMotorHalf\\bunmotorHalf.txt";
			model.filePath=System.getProperty("user.dir")+"\\motor8th.txt";
			model.filePath="C:\\JavaProjects\\prj6-inputVolt\\flux6A4th-0-90HalfDeg\\bunmot4thNewest.txt";
			
			model.filePath=System.getProperty("user.dir")+"\\motorHalf.txt";
		//	model.setNodalScalar(10);
		//	this.gui.vwp.paintNodalScalar(model);

			if(nr==0){
				//	model.filePath=System.getProperty("user.dir")+"\\mechDomWithRot2D.txt";
				
					

				loadModel();
				wait(5000);
				
				this.gui.vwp.bShowAxes.doClick();
			}

			nr++;
			loadModel();
			wait(5000);

			fluxAnim();

		*/}

		else if(e.getSource()==this.gui.vwp.fv.bGetValue)
			setPointValue();
		else if (e.getSource() == this.gui.vwp.bApplyVscale) {
			
			if(!this.gui.vwp.runMotor)
			{
			this.gui.vwp.rescale(model);
			}
			
			
		}


	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == gui.vwp.stressDist) {
			model.stressViewCode=gui.vwp.stressDist.getSelectedIndex();

			model.setNodalScalar(1);
			gui.vwp.paintNodalScalar(model);

		}
	}

	public void rotateRotor()  {
		Thread tr = new Thread() {

			public void run() {


				gui.vwp.rng=0;
				model.rotStep=1;
				
				gui.vwp.runMotor=!gui.vwp.runMotor;			

				while(gui.vwp.runMotor){

					gui.vwp.tfX2.setText(Double.toString(gui.vwp.rng));

				
					gui.vwp.rng+=model.rotStep;
				//	if(gui.vwp.rng>90) gui.vwp.rng-=90;

					//	if(gui.vwp.rng>20)break;

					Transform3D tr=new Transform3D();
					tr.rotZ(gui.vwp.rng*PI/180);


					for(int k=1;k<=model.numberOfRegions;k++){
						if(model.region[k].rotor){
							gui.vwp.surfFacets[k].setTransform(tr);
						}


					}

					try {
						new Thread().sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}
			}


		};

		tr.start();

	}

	public void fluxAnim()  {
		
	
		Thread tr = new Thread() {
			
			String file2 = System.getProperty("user.dir") + "\\Iclark.txt";

		double[][] Idq=model.loader.loadArrays(1800, 4, file2);
			
			String folder=model.animDataFolder+"\\";
			
			public void run() {

				int nex=1;
				int[] nxx={1,1,1};
				model.element[nex].setVertNumb(nxx);
				
				interaction=2;
				int Lt=model.nAnimSteps;
				gui.vwp.rng=0;
				
		
				int K=1;
		
				Vect T=new Vect(K*Lt);

				int vmode=model.animDataCode;
				
				int nnx=model.animChosenNode;
				int comp=model.animChonenNodeComp;
				boolean fillT=(nnx>0);

				Vect[][] BB=new Vect[model.nAnimSteps][1];
				
	boolean[] rc=new boolean[1+model.numberOfRegions];
				
				if(model.animRegs==null){
					for(int ir=1;ir<=model.numberOfRegions;ir++)
						rc[ir]=true;
				}
				else {

					for(int ir:model.animRegs){
					rc[ir]=true;
				}
				}
				
				boolean[] nc=new boolean[1+model.numberOfElements];
				
				for(int ir=1;ir<=model.numberOfRegions;ir++){
					
				if(rc[ir]){
					for( int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
						nc[i]=true;
					}
				}
				}
				
				if(model.batchAnim){
					
					int ix=0;
					for(int i=model.nBegin;i<=model.nEnd;i+=model.nInc){
					//String file=folder+model.animDataFile[i];
					int d=i%(model.nEnd);
				
					String file=folder+"\\"+model.fileCommon+d+"."+model.fileExtension;
					
					model.loadFlux(file,nc,d*model.rotStep);

					int v=i%(Idq.length);
					int ne=model.region[4].getFirstEl();
					//ne=9832;
					ne=nex;
					model.element[ne].setB(model.getElementCenter(ne).times(0).add(new Vect(Idq[v][1],Idq[v][2]).times(6)));
					
					BB[ix]=new Vect[model.numberOfElements];
					for(int ie=1;ie<model.numberOfElements;ie++)
					BB[ix][ie-1]=model.element[ie].getB();
					
					ix++;
					
					}
					
					
				}
				//gui.vwp.runMotor=!gui.vwp.runMotor;			
			
				int m=model.nBegin;
				int ix=0;
				while(true){
					
					if(fillT && m>=K*Lt) break;
						

					gui.vwp.tfX2.setText(Double.toString(gui.vwp.rng));
		
				
					gui.vwp.rng=m*model.rotStep;
					
				
					if(model.batchAnim){
						int iy=ix%BB.length;
						for(int ie=1;ie<model.numberOfElements;ie++)
							model.element[ie].setB(BB[iy][ie-1]);
					}
					else{
					//String file=folder+model.animDataFile[i];
					
						int d=m%(model.nEnd);
						String file=folder+"\\"+model.fileCommon+d+"."+model.fileExtension;
						
					model.loadFlux(file,nc,d*model.rotStep);
								
						int v=m%(Idq.length);
						int ne=model.region[4].getFirstEl();
						//ne=9832;
						ne=nex;
						model.element[ne].setB(model.getElementCenter(ne).times(0).add(new Vect(Idq[v][1],Idq[v][2]).times(6)));
					}

						if(m==0)
							gui.vwp.setVectField(model,4);
							else
								gui.vwp.rescale(model);
	
					
					if(fillT){
						T.el[m]=model.element[nnx].getB().el[comp];
						}

					Transform3D tr=new Transform3D();
					tr.rotZ(gui.vwp.rng*PI/180);


					for(int k=1;k<=model.numberOfRegions;k++){
						if(model.region[k].rotor)
							gui.vwp.surfFacets[k].setTransform(tr);

					}

					Main.wait(model.animTD);
					
					
					
					
					m+=model.nInc;
					ix++;
								

				}
				
				util.plot(T);
				T.show();
			}


		};

		tr.start();

	}

	public void animateShapey()  {

		Thread tr = new Thread() {
			public void run() {
				interaction=1;
			
				boolean[] rc=new boolean[1+model.numberOfRegions];
				
				if(model.animRegs==null){
					for(int ir=1;ir<=model.numberOfRegions;ir++)
						rc[ir]=true;
				}
				else {

					for(int ir:model.animRegs){
					rc[ir]=true;
				}
				}
				
				boolean[] nc=new boolean[1+model.numberOfNodes];
				
				for(int ir=1;ir<=model.numberOfRegions;ir++)
				if(rc[ir]){
					if(gui.vwp.surfFacets[ir]==null ||gui.vwp.surfFacets[ir].surfVertNumb==null ) continue;
					for(int j=0;j<gui.vwp.surfFacets[ir].surfVertNumb.length;j++){
						nc[gui.vwp.surfFacets[ir].surfVertNumb[j]]=true;
					}
				}
					
		

				int Lt=model.nAnimSteps;
		
				boolean vel=(model.animMode==4);
				if(vel) model.batchAnim=true;
				
			
				int K=1;
				
				int vmode=model.animDataCode;
			
				
				String folder=model.animDataFolder+"\\";
				
				Vect T=new Vect(K*Lt);
				
				
				int nnx=model.animChosenNode;
				int comp=model.animChonenNodeComp;
				
				boolean fillT=(nnx>0);
			
				//String file=folder+model.animDataFile[0];
				String file=folder+"\\"+model.fileCommon+0+"."+model.fileExtension;

							
				model.loadNodalField(file,model.animDataCode,nc);
				
				int nDefNodes=1;
				int[] map=new int[1];
				Vect[][] nv=new Vect[1][1];
				//========
				
				if(model.batchAnim){
				
				int ix=0;
				map=new int[1+model.numberOfNodes];
					
		
				for(int i=1;i<=model.numberOfNodes;i++){
					
				
					if(model.node[i].isDeformable())
					{
						map[ix]=i;
						ix++;
						}
				}
			
				 nDefNodes=ix;


				nv=new Vect[Lt][nDefNodes];
				ix=0;
			//	for(int i=0;i<model.nAnimSteps;i++){
				for(int i=model.nBegin;i<=model.nEnd;i+=model.nInc){
					//String file=folder+model.animDataFile[i];
					int d=i%(model.nEnd);
					
					//file=folder+model.animDataFile[i];

					file=folder+"\\"+model.fileCommon+d+"."+model.fileExtension;

					model.loadNodalField(file,model.animDataCode,nc);
					
					for(int j=0;j<nDefNodes;j++){
						nv[ix][j]=model.node[map[j]].getNodalVect(vmode);
			
					}
						
					ix++;
				
				}
						
				if(vel)	{
					
					 double rdt=1.0/model.dt;
									 
						Vect[][] veloc=new Vect[nDefNodes][Lt];
					
						for(int i=1;i<Lt;i++)
							for(int j=0;j<nDefNodes;j++){
								
									veloc[i][j]=nv[i][j].sub(nv[i-1][j]).times(rdt);
							}

						
					
						
						for(int j=0;j<nDefNodes;j++)
							veloc[0][j]= veloc[Lt-1][j].deepCopy();
						
						
						nv=veloc;
						
						
							}
				


				
				double vm=0;

				for(int i=0;i<Lt;i++)
					for(int j=0;j<nDefNodes;j++){
						double p=nv[i][j].norm();
						if(p>vm) vm=p;
						

					}

				model.uMax=vm;
				}

			
				

				int ix=0;
				int iy=0;
				int m=0;
				while(true){
					{
				
				Main.wait(model.animTD);
			
						
					if(fillT)
						if(m>=K*Lt) break;
					
					int i=m%model.nEnd;	
				
					gui.vwp.rng=m*model.rotStep;
						
						gui.vwp.tfX2.setText(df.format(gui.vwp.rng));
			
						iy=ix%nv.length;
						
						if(model.batchAnim)
						{
							for(int j=0;j<nDefNodes;j++)
								model.node[map[j]].setNodalVect(vmode,nv[iy][j]);
							
						
						}
						else{
							//file=folder+model.animDataFile[i];
							 file=folder+"\\"+model.fileCommon+i+"."+model.fileExtension;

util.pr(77);
							model.loadNodalField(file,vmode,nc);
							
						}
						
						if(m==0) {
							gui.vwp.Vmax=1e2*model.uMax;
							gui.vwp.setSlider();
						}
						gui.vwp.deformMesh(model);
						
					//	gui.vwp.deformMesh(model);

					
						if(fillT){
						T.el[m]=model.node[nnx].getNodalVect(vmode).el[comp];
						}
						
						Transform3D tr=new Transform3D();
						tr.rotZ(gui.vwp.rng*PI/180);


						for(int k=1;k<=model.numberOfRegions;k++){
							//util.pr(k);
							if(model.region[k].rotor){
								gui.vwp.surfFacets[k].setTransform(tr);
							}
						}
		
						m+=model.nInc;

						ix++;
					}
				}
				
				if(vmode==2)
					T.timesVoid(1e9);
				
				util.plot(T);
				
				T.show();
				
			}


		};

		tr.start();

	}
	

	public void animateShape()  {

		Thread tr = new Thread() {
			public void run() {

				//gui.vwp.runMotor=!gui.vwp.runMotor;			

				//int n0=11018;
				interaction=2;
				
				
				
				for(int k=1;k<=model.numberOfRegions;k++)
					gui.vwp.setRegion[k]=false;
				
				if(model.animRegs!=null)
				for(int k:model.animRegs){
				
					gui.vwp.setRegion[k]=true;
				}
				
					
				int Lt=model.nAnimSteps;
				
				
				int vmode=model.animDataCode;
				int K=1;
				
				String folder=model.animDataFolder+"\\";
				
				Vect T=new Vect(K*Lt);
				
				
				
				int nnx=model.animChosenNode;
				int comp=model.animChonenNodeComp;
				boolean fillT=(nnx>0);

				
				//String file=folder+model.animDataFile[0];
				String file=folder+"\\"+model.fileCommon+0+"."+model.fileExtension;

							
				model.loadNodalField(file,model.animDataCode);
			//	model.loadNodalField(file,model.animDataCode,n0);
				
				int nDefNodes=1;
				int[] map=new int[1];
				Vect[][] nv=new Vect[1][1];
				//========
				
				if(model.batchAnim){
				
				int ix=0;
				map=new int[1+model.numberOfNodes];
				
				for(int i=1;i<=model.numberOfNodes;i++)
					if(model.node[i].isDeformable())
						{
						map[ix]=i;
						ix++;
						}
			
				 nDefNodes=ix;
	
				

				nv=new Vect[Lt][nDefNodes];
				
				ix=0;
				
				for(int i=model.nBegin;i<=model.nEnd;i+=model.nInc){
					
					int d=i%model.nEnd;
					//file=folder+model.animDataFile[i];
					 file=folder+"\\"+model.fileCommon+d+"."+model.fileExtension;


					model.loadNodalField(file,model.animDataCode);

					
					for(int j=0;j<nDefNodes;j++)
						nv[ix][j]=model.node[map[j]].getNodalVect(vmode);
						
			
					ix++;
				}
							
			

				
				double vm=0;

				for(int i=0;i<Lt;i++)
					for(int j=0;j<nDefNodes;j++){
						double p=nv[i][j].norm();
						if(p>vm) vm=p;
						

					}

				model.uMax=vm;
				
				}
				
				//=-===============
				gui.vwp.Vmax=1e-2*model.uMax;
				gui.vwp.setSlider();
				
				int ix=0;
				int m=0;
				while(true){
					{
				
					
						Main.wait(model.animTD);
			
						
					if(fillT)
						if(m>=K*Lt) break;
						
			
						int i=m%model.nEnd;
					
						gui.vwp.tfX2.setText(Integer.toString(i));
			
					
						if(model.batchAnim)
						{
							int iy=ix%nv.length;
							
							for(int j=0;j<nDefNodes;j++)
								model.node[map[j]].setU(nv[iy][j]);
						}
						else{
							//file=folder+model.animDataFile[i];
							
							 file=folder+"\\"+model.fileCommon+i+"."+model.fileExtension;


								
							model.loadNodalField(file, vmode);
							//model.loadNodalField(file2,model.animDataCode,n0);
	
						}
						
						if(fillT){
						T.el[m]=model.node[nnx].getNodalVect(vmode).el[comp];
						}
						if(m==0) {
							gui.vwp.Vmax=1e2*model.uMax;
							gui.vwp.setSlider();
						}
						gui.vwp.deformMesh(model);
						
						
						Transform3D tr=new Transform3D();
						tr.rotZ(gui.vwp.rng*PI/180);


						for(int k=1;k<=model.numberOfRegions;k++){
							if(model.region[k].rotor)
								gui.vwp.surfFacets[k].setTransform(tr);



						}
		
						m+=model.nInc;

						ix++;
					}
				}
				
				
				if(vmode==2)
					T.timesVoid(1e9);
				
				util.plot(T);
				T.show();
				
			}


		};

		tr.start();

	}
	
	public void animatePaint()  {

		Thread tr = new Thread() {
			public void run() {


				int Lt=model.nAnimSteps;
				
		
				int K=1;
				
				int vmode=model.animDataCode;
				
				String folder=model.animDataFolder+"\\";
				
				Vect T=new Vect(K*Lt);
				
				int nnx=model.animChosenNode;
				boolean fillT=(nnx>0);
			
			//	String file=folder+model.animDataFile[0];
				//int d=i%(model.nEnd);
				
				String file=folder+"\\"+model.fileCommon+0+"."+model.fileExtension;

				if(model.animDataCode==0)
					model.loadFlux(file);
				else
					model.loadNodalField(file,model.animDataCode);
				
				int nDefNodes=1;
				int[] map=new int[1];
				Vect[][] nv=new Vect[1][1];
				//========
				
				boolean[] rc=new boolean[1+model.numberOfRegions];
				
				if(model.animRegs==null){
					for(int ir=1;ir<=model.numberOfRegions;ir++)
						rc[ir]=true;
				}
				else {

					for(int ir:model.animRegs){
					rc[ir]=true;
				}
				}
				
				boolean[] nc=new boolean[1+model.numberOfElements];
				
				for(int ir=1;ir<=model.numberOfRegions;ir++){
					
				if(rc[ir]){
					for( int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
						nc[i]=true;
					}
				}
				}
				
				
				if(model.batchAnim){
				
				int ix=0;
				map=new int[1+model.numberOfNodes];
				
				for(int i=1;i<=model.numberOfNodes;i++)
					if(model.node[i].isDeformable())
						{
						map[ix]=i;
						ix++;
						}
			
				 nDefNodes=ix;
	

			//	nv=new Vect[model.nAnimSteps][];
				

				
				 ix=0;

				for(int i=model.nBegin;i<=model.nEnd;i+=model.nInc){
	
				//	file=folder+model.animDataFile[i];
					int d=i%(model.nEnd);
					
					file=folder+"\\"+model.fileCommon+d+"."+model.fileExtension;

					if(model.animDataCode==0){
						
						nv=new Vect[model.nAnimSteps][model.numberOfElements];
						
						model.loadFlux(file,nc,d*model.rotStep);
					//	model.setNodalScalar(0);
						nv[ix]=new Vect[model.numberOfElements];
						for(int ie=1;ie<model.numberOfElements;ie++)
						nv[ix][ie-1]=model.element[ie].getB();
		
					}
					else{
						model.loadNodalField(file,model.animDataCode);
					nv[ix]=new Vect[nDefNodes];
					for(int j=0;j<nDefNodes;j++)
						nv[ix][j]=model.node[map[j]].getNodalVect(vmode);
					}
					
					
					ix++;
				
				}
							
				


				
				double vm=0;

				for(int i=0;i<Lt;i++)
					for(int j=0;j<nDefNodes;j++){
						double p=nv[i][j].norm();
						if(p>vm) vm=p;
						

					}

				model.uMax=vm;
				
				}
				
				//=-===============
				
			//	gui.vwp.setSlider();
				
				int m=0;
				while(true){
					{
				
					
		Main.wait(model.animTD);
			
						
					if(fillT)
						if(m>=K*model.nEnd) break;
					
					int i=m%model.nEnd;
				
						gui.vwp.rng=m*model.rotStep;
					
						gui.vwp.tfX2.setText(Double.toString(gui.vwp.rng));
			
						
						
						int d=i%(model.nEnd);
					
						 file=folder+"\\"+model.fileCommon+d+"."+model.fileExtension;
					
						if(model.batchAnim)
						{
							
							
							if(model.animDataCode==0){
								model.loadFlux(file,nc,gui.vwp.rng);
								model.setNodalScalar(0);

							}
							else 
							{
								for(int j=0;j<nDefNodes;j++)								
									model.node[map[j]].setNodalVect(vmode,nv[i][j]);
							 model.loadNodalField(file,vmode);
							 model.setNodalScalar(vmode+2);
							}
						
					
						}
						else{
							//file=folder+model.animDataFile[i];
				
					
							if(model.animDataCode==0){
								model.loadFlux(file,nc,gui.vwp.rng);
								model.setNodalScalar(0);

							}
							else 
							{
							 model.loadNodalField(file,vmode);
							model.setNodalScalar(vmode+2);
							}
						
						}
						
						
						
						gui.vwp.paintNodalScalar(model);
						
						
						Transform3D tr=new Transform3D();
						tr.rotZ(gui.vwp.rng*PI/180);

						for(int k=1;k<=model.numberOfRegions;k++){
							if(model.region[k].rotor)
								gui.vwp.surfFacets[k].setTransform(tr);
							//if(k!=1)
							//gui.vwp.surfFacets[k].deform(model);


						}

						
						if(fillT)
						T.el[m]=model.node[nnx].getNodalMass();
						
					
		
						m+=model.nInc;

					}
				}
				
				if(vmode==2)
					T.timesVoid(1e9);
				
				util.plot(T);
				
				T.show();
				
			}


		};

		tr.start();

	}
	
	
	public void animateVectSurf()  {

		Thread tr = new Thread() {
			public void run() {
				interaction=2;
			
				boolean[] rc=new boolean[1+model.numberOfRegions];
				
				if(model.animRegs==null){
					for(int ir=1;ir<=model.numberOfRegions;ir++)
						rc[ir]=true;
				}
				else {

					for(int ir:model.animRegs){
					rc[ir]=true;
				}
				}
				
				boolean[] nc=new boolean[1+model.numberOfNodes];
				
				for(int ir=1;ir<=model.numberOfRegions;ir++)
				if(rc[ir]){
					if(gui.vwp.surfFacets[ir]==null ||gui.vwp.surfFacets[ir].surfVertNumb==null ) continue;
					for(int j=0;j<gui.vwp.surfFacets[ir].surfVertNumb.length;j++){
						nc[gui.vwp.surfFacets[ir].surfVertNumb[j]]=true;
					}
				}
					
		

				int Lt=model.nAnimSteps;
		
				boolean vel=(model.animMode==4);
				if(vel) model.batchAnim=true;
				
			
				int K=1;
				
				int vmode=model.animDataCode;
			
				
				String folder=model.animDataFolder+"\\";
				
				Vect T=new Vect(K*Lt);
				
				
				int nnx=model.animChosenNode;
				int comp=model.animChonenNodeComp;
				
				boolean fillT=(nnx>0);
			
				//String file=folder+model.animDataFile[0];
				String file=folder+"\\"+model.fileCommon+0+"."+model.fileExtension;

							
				model.loadNodalField(file,model.animDataCode,nc);
				
				int nDefNodes=1;
				int[] map=new int[1];
				Vect[][] nv=new Vect[1][1];
				//========
				
				if(model.batchAnim){
				
				int ix=0;
				map=new int[1+model.numberOfNodes];
					
		
				for(int i=1;i<=model.numberOfNodes;i++){
					
				
					if(model.node[i].isDeformable())
					{
						map[ix]=i;
						ix++;
						}
				}
			
				 nDefNodes=ix;


				nv=new Vect[Lt][nDefNodes];
				ix=0;
			//	for(int i=0;i<model.nAnimSteps;i++){
				for(int i=model.nBegin;i<=model.nEnd;i+=model.nInc){
					//String file=folder+model.animDataFile[i];
					int d=i%(model.nEnd);
					
					//file=folder+model.animDataFile[i];

					file=folder+"\\"+model.fileCommon+d+"."+model.fileExtension;

					model.loadNodalField(file,model.animDataCode,nc);
					
					for(int j=0;j<nDefNodes;j++){
						nv[ix][j]=model.node[map[j]].getNodalVect(vmode);
			
					}
						
					ix++;
				
				}
						
				if(vel)	{
					
					 double rdt=1.0/model.dt;
									 
						Vect[][] veloc=new Vect[nDefNodes][Lt];
					
						for(int i=1;i<Lt;i++)
							for(int j=0;j<nDefNodes;j++){
								
									veloc[i][j]=nv[i][j].sub(nv[i-1][j]).times(rdt);
							}

						
					
						
						for(int j=0;j<nDefNodes;j++)
							veloc[0][j]= veloc[Lt-1][j].deepCopy();
						
						
						nv=veloc;
						
						
							}
				


				
				double vm=0;

				for(int i=0;i<Lt;i++)
					for(int j=0;j<nDefNodes;j++){
						double p=nv[i][j].norm();
						if(p>vm) vm=p;
						

					}

				model.uMax=vm;
				}

			
				

				int ix=0;
				int iy=0;
				int m=0;
				while(true){
					{
				
				Main.wait(model.animTD);
			
						
					if(fillT)
						if(m>=K*Lt) break;
					
					int i=m%model.nEnd;	
				
					gui.vwp.rng=m*model.rotStep;
						
						gui.vwp.tfX2.setText(df.format(gui.vwp.rng));
			
						iy=ix%nv.length;
						
						if(model.batchAnim)
						{
							for(int j=0;j<nDefNodes;j++)
								model.node[map[j]].setNodalVect(vmode,nv[iy][j]);
							
						
						}
						else{
							//file=folder+model.animDataFile[i];
							 file=folder+"\\"+model.fileCommon+i+"."+model.fileExtension;


							model.loadNodalField(file,vmode,nc);
							
						}
						
						if(m==0)
							gui.vwp.setVectField(model,vmode);
							else{
								gui.vwp.rescale(model);
							}

					
						if(fillT){
						T.el[m]=model.node[nnx].getNodalVect(vmode).el[comp];
						}
						
						Transform3D tr=new Transform3D();
						tr.rotZ(gui.vwp.rng*PI/180);


						for(int k=1;k<=model.numberOfRegions;k++){
							//util.pr(k);
							if(model.region[k].rotor){
								gui.vwp.surfFacets[k].setTransform(tr);
							}
						}
		
						m+=model.nInc;

						ix++;
					}
				}
				
				if(vmode==2)
					T.timesVoid(1e9);
				
				util.plot(T);
				
				T.show();
				
			}


		};

		tr.start();

	}
	
	


	public void setPointValue(){
		int dim=this.model.dim;
		if(gui.vwp.divert)
			Console.redirectOutput(this.gui.vwp.messageArea);
		Vect V=new Vect(dim);
		Vect P=new Vect(dim);

		if(this.gui.vwp.fv.pointValue.getSelectedIndex()==0){
			for(int i=0;i<dim;i++)
				this.gui.vwp.fv.tfValue[i].setText("");					
		}

		try{

			for(int i=0;i<dim;i++)
				P.el[i]=Double.parseDouble(this.gui.vwp.fv.tfPointCoord[i].getText());


		}
		catch(NumberFormatException e2)
		{for(int i=0;i<dim;i++) 
			this.gui.vwp.fv.tfPointCoord[i].setText("0.0");
		}

		if(this.resultReady ){

			if(this.gui.vwp.fv.pointValue.getSelectedIndex()==1)
				V=this.model.getBAt(P);
			else if(this.gui.vwp.fv.pointValue.getSelectedIndex()==3)
				V=this.model.getFOf(P);


			for(int i=0;i<dim;i++)
				this.gui.vwp.fv.tfValue[i].setText(this.formatter.format(V.el[i]));
		}

		else if(this.model.fluxLoaded && this.gui.vwp.fv.pointValue.getSelectedIndex()==1){
			int[] ne=this.model.getContainingElement(P);
			P=this.model.getElementCenter(ne[0]);
			V=this.model.element[ne[0]].getB();
			for(int i=0;i<dim;i++)
				this.gui.vwp.fv.tfValue[i].setText(this.formatter.format(V.el[i]));
			for(int i=0;i<dim;i++)
				this.gui.vwp.fv.tfPointCoord[i].setText(this.formatter.format(P.el[i]));

		}
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) 
	{}

	@Override
	public void dragExit(DropTargetEvent dte) 
	{ }

	@Override
	public void dragOver(DropTargetDragEvent dtde) 
	{ }

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) 
	{  }

	@Override
	public void drop(DropTargetDropEvent dtde) {	   
		try {
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();

			for (int i = 0; i < flavors.length; i++) {
				if (flavors[i].isFlavorJavaFileListType()) {
					dtde.acceptDrop(DnDConstants.ACTION_COPY);
					List list = (List) tr.getTransferData(flavors[i]);

					if(gui.vwp.divert)
						Console.redirectOutput(this.gui.vwp.messageArea);				
					this.model.filePath=list.get(i).toString();

					System.out.println("Dropped File: "+ this.model.filePath);
					String str=readFirst( this.model.filePath);

					if(str.equals("hexahedron") ||
							str.equals("tetrahedron")||
							str.equals("triangle")||
							str.equals("quadrangle")||
							str.equals("pyramid")||
							str.equals("prism")){
						this.thread=new Thread(){
							@Override
							public void run(){
								loadModel(); 
							}
						};
						this.thread.start();
						this.gui.vwp.nLoadedMesh++;
					}
					else if (str.equals("flux")){
						if(this.gui.vwp.nLoadedMesh>0){
							this.thread=new Thread(){
								@Override
								public void run(){
									loadFlux(); 
								}
							};

							this.thread.start();

						}
						else{
							noMeshLoaded();

						}
					}
					else if (str.equals("vPot")){

						if(this.gui.vwp.nLoadedMesh>0){
							this.thread=new Thread(){
								@Override
								public void run(){
									loadPotential(); 
								}
							};

							this.thread.start();

						}
						else{
							noMeshLoaded();
						}
					}

					else if (str.equals("stress")){
						if(this.gui.vwp.nLoadedMesh>0){
							this.thread=new Thread(){
								@Override
								public void run(){
									loadStress(); 
								}
							};

							this.thread.start();

						}
						else{
							noMeshLoaded();
						}
					}

					else if (str.equals("displacement")){
						if(this.gui.vwp.nLoadedMesh>0){
							this.thread=new Thread(){
								@Override
								public void run(){
									//paintDisplacement(); 
									loadNodalField(2); 
								}
							};

							this.thread.start();

						}
						else{
							noMeshLoaded();
						}
					}


					else if (str.equals("force_reluc")){
						if(this.gui.vwp.nLoadedMesh>0){
							this.thread=new Thread(){
								@Override
								public void run(){
									loadNodalField(0); 
								}
							};

							this.thread.start();

						}
						else{
							noMeshLoaded();
						}
					}

					else if (str.equals("force_ms")){
						if(this.gui.vwp.nLoadedMesh>0){
							this.thread=new Thread(){
								@Override
								public void run(){
									loadNodalField(1); 
								}
							};

							this.thread.start();

						}
						else{
							noMeshLoaded();
						}
					}

					else if (str.equals("surfForce_reluc")){
						if(this.gui.vwp.nLoadedMesh>0){
							this.thread=new Thread(){
								@Override
								public void run(){
									loadNodalField(3); 
								}
							};

							this.thread.start();

						}
						else{
							noMeshLoaded();
						}
					}

					else if (str.equals("temperature")){
						if(this.gui.vwp.nLoadedMesh>0){
							this.thread=new Thread(){
								@Override
								public void run(){
									loadTemp(); 
								}
							};

							this.thread.start();

						}
						else{
							noMeshLoaded();
						}
					}



					else {
						String msg="Invalid input file.";
						JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);
					}


				}
				dtde.dropComplete(true);

			}

			System.out.println("Drop failed: " + dtde);
			dtde.rejectDrop();
		} catch (Exception e) {
			e.printStackTrace();
			dtde.rejectDrop();
		}
	}


	private void noMeshLoaded(){
		String msg="No mesh loded yet.";
		JOptionPane.showMessageDialog(null, msg," ", JOptionPane. ERROR_MESSAGE);

	}

	public static void wait(int ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public double getDiffMax(Vect[] u, Vect[] v){
		double diff;
		double diffMax=0;
		for(int i=0;i<u.length;i++){
			diff=u[i].sub(v[i]).norm();
			if(diff>diffMax)
				diffMax=diff;
		}

		return diffMax;
	}

	public double getErrorMax(Vect[] u, Vect[] v){
		double diff;
		double diffMax=0;
		double vmax=0;
		for(int i=0;i<u.length;i++){
			double vn=v[i].norm();
			if(vn>vmax) vmax=vn;
			diff=u[i].sub(v[i]).norm();
			if(diff>diffMax)
				diffMax=diff;
		}

		if(vmax==0)
			return 0;

		return diffMax/vmax;
	}

	public void deleteDir(File dir) {
		if (dir.isDirectory()) {

			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				deleteDir(new File(dir, children[i]));

			}
		}
		else
			dir.delete();



	}

	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
		if(interaction==0) return;
	
		this.gui.vwp.vScalefact=Double.parseDouble(this.gui.vwp.tfX1.getText());
		this.gui.vwp.vScale=.02*this.gui.vwp.vScalefact*gui.vwp.vScale0*gui.vwp.jslider.getValue();
		
		gui.vwp.tfVectorScale.setText(formatter.format(this.gui.vwp.vScale));

		if(interaction==1)
		gui.vwp.deformMesh(model);
		else 	if(interaction==2)
		gui.vwp.scaleVectField(model);
		
		
	}






}

