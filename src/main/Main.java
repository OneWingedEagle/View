package main;
import drawingPanel.*;
import panels.Console;
import fem.*;
import graphics.ColorBar;
import graphics.V3f;
import math.*;

import static java.lang.Math.*;

import java.awt.Color;
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

import components.EditLight;

public class Main implements ActionListener, ItemListener,ChangeListener, DropTargetListener{

	private GUI gui;

	private Model model;
	private Thread thread;
	private int mheavy=1000,interaction;
	private double timeDelay;
	private boolean heavy,resultReady;
	private DecimalFormat formatter=new DecimalFormat("0.000E00");
	private String path = System.getProperty("user.dir") + "\\model.txt";
	DecimalFormat df=new DecimalFormat("#.00");
	
	//============
	int numbRubixMove=0;
	int numbRubixMoveMax=20;
	int[][][] regArray;
	Mat[] RR;
	int angDiv;
	int[][] rubixMove=new int[numbRubixMoveMax][3];


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
		
		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++)
				for(int k=0;k<2;k++)
					this.gui.vwp.rubix.bMove[i][j][k].addActionListener(this);


		this.gui.vwp.jslider.addChangeListener(this );
	
		//gui.vwp.divert=true;
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
			if(this.gui.vwp.plotOption.getSelectedIndex()==1){
				model.setNodalScalar(0);
				//if(model.nAnimSteps==1)
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



				if(model.animMode==0 || model.animMode==-1) {
					if(model.animMode==-1)
						model.batchAnim=true;

					animateVectSurf();
				}
				else if(model.animMode==1) 
					animateShape();
			}
			else if(model.animDataCode==3){
				animatePaint();
			}




		}


		else if (e.getSource() == this.gui.vwp.bRotate){
			
			gui.vwp.runMotor=!gui.vwp.runMotor;		

			//	rotateRotor();
			if(numbRubixMove==0)
			prepareRubix();
			
			Thread tr = new Thread() {

				
				public void run() {
			//	runRubix();	
					if(gui.vwp.runMotor)
					dealRubix();
					else
			solveRubix();
				}
					
			
			};
			
			tr.start();

		}

		else if(e.getSource()==this.gui.vwp.fv.bGetValue)
			setPointValue();
		else if (e.getSource() == this.gui.vwp.bApplyVscale) {

			if(!this.gui.vwp.runMotor)
			{
				this.gui.vwp.rescale(model);
			}


		}else{
			
			for(int i=0;i<3;i++)
				for(int j=0;j<3;j++)
					for(int k=0;k<2;k++)
						if (e.getSource() == this.gui.vwp.rubix.bMove[i][j][k]){
							
							moveRubix(i,j,k);
							break;
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
	public void prepareRubix()  {
	/*	Thread tr = new Thread() {

			
			public void run() {*/
				

				interaction=1;
				gui.vwp.Vmax=1;
				gui.vwp.setSlider();

				Vect c1=new Vect(3);
				int[] nn1=model.getRegNodes(1);
				for(int jj=0;jj<nn1.length;jj++){
					c1=c1.add(model.node[nn1[jj]].getCoord());
				
				}				
			
				c1=c1.times(1.0/nn1.length);
				
				Vect c2=new Vect(3);
				int[] nn2=model.getRegNodes(2);
				for(int jj=0;jj<nn2.length;jj++){
					c2=c2.add(model.node[nn2[jj]].getCoord());
				
				}				
			
				c2=c2.times(1.0/nn2.length);
				
				double d=c1.sub(c2).norm();

				gui.vwp.rng=0;
				
				 angDiv=6;
				model.rotStep=90.0/angDiv;

				double rotRad0=model.rotStep*PI/180;
				
			/*	Mat[] */RR=new Mat[3];
				
	
					RR[0]=util.rotEuler(new Vect(1,0,0), rotRad0);
				
					RR[1]=util.rotEuler(new Vect(0,1,0), rotRad0);
		
					RR[2]=util.rotEuler(new Vect(0,0,1), rotRad0);
		
		/*		int[][][]*/ regArray=new int[3][3][3];		
				
				int ix=0;
				for(int i=0;i<3;i++)
					for(int j=0;j<3;j++)
						for(int k=0;k<3;k++){
						
							ix++;
								
						Vect c=new Vect(3);
								int[] nn=model.getRegNodes(ix);
								for(int jj=0;jj<nn.length;jj++){
									c=c.add(model.node[nn[jj]].getCoord());
								
								}				
							
								c=c.times(1.0/nn.length).add(d);
								
								int i1=(int)(1.1*c.el[0]/d);
								int j1=(int)(1.1*c.el[1]/d);
								int k1=(int)(1.1*c.el[2]/d);
								
								
								regArray[i1][j1][k1]=ix;
							}
				
	/*
				
				Mat R=new Mat();

				int turn1,turn2,ccw,nSteps=0;
						
						
				for (int t=0;t<3;t++){
				
					
					turn1 =(int)(Math.random()*9);
					
					if(turn1%3==1){
						if(Math.random()>.5) turn1--; else turn1++;
					}
					
				
					
					int[] group1=group(turn1,regArray);
					
					int[] group2=new int[9];
					
					boolean pair=false;
					
					int dd=turn1%3;
					
					if(dd!=1 &&Math.random()>.8) pair=true;
					
					
			
					if(pair){
						turn2=turn1+1-dd;
						group2=group(turn2,regArray);
					}
					else{
						turn2=-1;
					}

					
							
					ccw=1;
					if(Math.random()>.5) ccw=-1;
		
					nSteps++;
					
					
					for(int k=0;k<angDiv;k++){
			
				
					
					for(int i=0;i<group1.length;i++){
						
							int ir1=group1[i];	
							
							R=getRotMat(RR,turn1,ccw);
					
								gui.vwp.surfFacets[ir1].runRubix(R);
								
								if(pair){
									
								int ir2=group2[i];	
									
								gui.vwp.surfFacets[ir2].runRubix(R);
								}
								
								}
					
					int td=10+(int)(50*timeDelay);
				
					util.pause(td*0);
				}
					reorderRegs(turn1,turn2,ccw,regArray);
				
				//	util.pause(200);
					rubixMove[t][0]=turn1;
					rubixMove[t][1]=turn2;
					rubixMove[t][2]=ccw;
					
					numbRubixMove++;

				}
			*/
								

	//		}


		
/*
		};

		tr.start();
*/
	}

	public void dealRubix()  {

				int turn1,turn2,ccw,nSteps=0;
				Mat R=new Mat();		
			
				int N=numbRubixMoveMax;
	
				
				for (int t=0;t<N;t++){
				
					
					turn1 =(int)(Math.random()*9);
					
					if(turn1%3==1){
						if(Math.random()>.5) turn1--; else turn1++;
					}
				
					
					int[] group1=group(turn1,regArray);
					
					int[] group2=new int[9];
					
					boolean pair=false;
					
					int dd=turn1%3;
					
					if(dd!=1 &&Math.random()>.8) pair=true;
					
					
			
					if(pair){
						turn2=turn1+1-dd;
						group2=group(turn2,regArray);
					}
					else{
						turn2=-1;
					}

							
					ccw=1;
					if(Math.random()>.5) ccw=-1;
		

					nSteps++;
					
					
					for(int k=0;k<angDiv;k++){
			
				
					
					for(int i=0;i<group1.length;i++){
						
							int ir1=group1[i];	
							
							R=getRotMat(RR,turn1,ccw);
					
								gui.vwp.surfFacets[ir1].runRubix(R);
								
								if(pair){
									
								int ir2=group2[i];	
									
								gui.vwp.surfFacets[ir2].runRubix(R);
								}
								
								}
					
					int td=10+(int)(50*timeDelay);
				
					util.pause(td*0);
				}
					reorderRegs(turn1,turn2,ccw,regArray);
				
					rubixMove[t][0]=turn1;
					rubixMove[t][1]=turn2;
					rubixMove[t][2]=ccw;
					
					numbRubixMove++;

				}
				
								

	}
	
public void solveRubix(){
		
		//gui.vwp.runMotor=!gui.vwp.runMotor;		
		Mat R=new Mat();

		int turn1,turn2,ccw, nSteps=0;
		
	
		for(int t=0;t<numbRubixMoveMax;t++){
			
			
			boolean pair=false;
	
			
			//===============
			int tr=numbRubixMoveMax-1-t;
			
			turn1=rubixMove[tr][0];
			turn2=rubixMove[tr][1];
			ccw=-rubixMove[tr][2];
			
			if(turn2>=0) pair=true;
			
		
			
			int[] group1=group(turn1,regArray);
			
			int[] group2=new int[9];
	
			if(pair){
				group2=group(turn2,regArray);
			}
		



			nSteps++;

			for(int k=0;k<angDiv;k++){
	
			for(int i=0;i<group1.length;i++){
				
					int ir1=group1[i];	
					
					R=getRotMat(RR,turn1,ccw);
			
						gui.vwp.surfFacets[ir1].runRubix(R);
						
						if(pair){
							
						int ir2=group2[i];	
							
						gui.vwp.surfFacets[ir2].runRubix(R);
						}
						
						}
			
			int td=100+(int)(200*timeDelay);
		
			util.pause(td);
		}
			
			reorderRegs(turn1,turn2,ccw,regArray);
		
		//	util.pause(200);
			
		//	numbRubixMove++;
			//util.pr(numbRubixMove+"  "+numbRubixMoveMax);
			
		//	if(numbRubixMove==numbRubixMoveMax) return;
			}

		
	}
	
	public void runRubix(){
		
		Mat R=new Mat();

		int turn1,turn2,ccw, nSteps=0;
		
		synchronized(this){
		while(gui.vwp.runMotor){
			
			
			turn1 =(int)(Math.random()*9);
			
			if(turn1%3==1){
				if(Math.random()>.5) turn1--; else turn1++;
			}
			
			if(Math.random()>.5) ccw=-1; else ccw=1;
			
		
			
			int[] group1=group(turn1,regArray);
			
			int[] group2=new int[9];
			
			boolean pair=false;
			
			int dd=turn1%3;
			
			if(dd!=1 &&Math.random()>.8) pair=true;
			
			
	
			if(pair){
				turn2=turn1+1-dd;
				group2=group(turn2,regArray);
			}
			else{
				turn2=-1;
			}



			nSteps++;

			for(int k=0;k<angDiv;k++){
	
		
			
			for(int i=0;i<group1.length;i++){
				
					int ir1=group1[i];	
					
					R=getRotMat(RR,turn1,ccw);
			
						gui.vwp.surfFacets[ir1].runRubix(R);
						
						if(pair){
							
						int ir2=group2[i];	
							
						gui.vwp.surfFacets[ir2].runRubix(R);
						}
						
						}
			
			int td=10+(int)(50*timeDelay);
		
			util.pause(td);
		}
			
			reorderRegs(turn1,turn2,ccw,regArray);
		
		//	util.pause(200);
			
			numbRubixMove++;
			}
		}
		
	}
	
public void moveRubix(int ii,int jj, int kk){
		
		Mat R=new Mat();

		int turn1,nSteps=0;
		turn1=ii*3+jj;


		int ccw;
	
		
		int[] group1=group(turn1,regArray);
		
		int[] group2=new int[9];
		
		boolean pair=false;
		
/*		

		if(pair){
			turn2=turn1+1-dd;
			group2=group(turn2,regArray);
		}
		else{
			turn2=-1;
		}
*/
				
		ccw=1;
		if(kk>0) ccw=-1;


		nSteps++;

		for(int k=0;k<angDiv;k++){

	
		
		for(int i=0;i<group1.length;i++){
			
				int ir1=group1[i];	
				
				R=getRotMat(RR,turn1,ccw);
		
					gui.vwp.surfFacets[ir1].runRubix(R);
					
					if(pair){
						
					int ir2=group2[i];	
						
					gui.vwp.surfFacets[ir2].runRubix(R);
					}
					
					}
		
		int td=10+(int)(50*timeDelay);
	
		util.pause(td);
	}
		
		reorderRegs(turn1,-1,ccw,regArray);
	
	//	util.pause(200);
		
		numbRubixMove++;
		
		
		
	}


	private int[] group(int turn, int[][][] regArray){
		
		int[] group=new int[9];
		
		int ib=0,jb=0,kb=0,ie=0,je=0,ke=0;;
		ib=0;ie=3;
		jb=0;je=3;
		kb=0;ke=3;
		if(turn<3){
		ib=turn;ie=ib+1;
		}
		else if(turn<6){
			jb=turn-3;je=jb+1;
			}
		else if(turn<9){
			kb=turn-6;ke=kb+1;
			}
			
	int ix=0;
	for(int i=ib;i<ie;i++)
		for(int j=jb;j<je;j++)
			for(int k=kb;k<ke;k++)
				{
					group[ix++]=regArray[i][j][k];
				}
	
	return group;
		
	}
	
	private Mat getRotMat(Mat[] RR,int turn,int ccw){
		Mat R;
		
		int k=0;
		if(turn<3){
			k=0;
			}
		else if(turn<6){
		k=1;
		}
		else if(turn<9){
			k=2;
		}
	
		R=RR[k];
		if(ccw<0) return R.transp();

		return R;
	}
	

	private  void reorderRegs(int n1,int n2,int ccw,int[][][] array){
		
		int I=array.length;
		int J=array[0].length;
		int K=array[0][0].length;
	
		int[][][] array1=new int[I][J][K];
				
		for(int i=0;i<I;i++)
			for(int j=0;j<J;j++)
				for(int k=0;k<K;k++)
					array1[i][j][k]=array[i][j][k];
		
	
		if(n1<3){
			int i=n1;
	
				for(int j=0;j<J;j++)
					for(int k=0;k<K;k++){
						if(ccw>0)
						array[i][j][k]=array1[i][k][2-j];
						else
							array[i][j][k]=array1[i][2-k][j];
					}
		}
		else if(n1<6){
			int j=n1-3;
	
				for(int i=0;i<I;i++)
					for(int k=0;k<K;k++){
						if(ccw>0)
						array[i][j][k]=array1[2-k][j][i];
						else
							array[i][j][k]=array1[k][j][2-i];
					}
		}
		
		else if(n1<9){
			int k=n1-6;
	
				for(int i=0;i<I;i++)
					for(int j=0;j<J;j++){
						if(ccw>0)
						array[i][j][k]=array1[j][2-i][k];
						else
							array[i][j][k]=array1[2-j][i][k];
					}
		}
		
		if(n2<0) return;
		
		if(n2<3){
			int i=n2;
	
				for(int j=0;j<J;j++)
					for(int k=0;k<K;k++){
						if(ccw>0)
						array[i][j][k]=array1[i][k][2-j];
						else
							array[i][j][k]=array1[i][2-k][j];
					}
		}
		else if(n2<6){
			int j=n2-3;
	
				for(int i=0;i<I;i++)
					for(int k=0;k<K;k++){
						if(ccw>0)
						array[i][j][k]=array1[2-k][j][i];
						else
							array[i][j][k]=array1[k][j][2-i];
					}
		}
		
		else if(n2<9){
			int k=n2-6;
	
				for(int i=0;i<I;i++)
					for(int j=0;j<J;j++){
						if(ccw>0)
						array[i][j][k]=array1[j][2-i][k];
						else
							array[i][j][k]=array1[2-j][i][k];
					}
		}

		
	}


	private void fluxAnim()  {


		Thread tr = new Thread() {


			public void run() {

				String folder=model.animDataFolder+"\\";


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

				String file2;

				double[][] Idq = null;
				int nex=1;
				double magn=0;
				int[] nxx={1,1,1};

				if(model.numberOfRegions==1700){

					file2= "C:\\Works\\proj8\\Iclark.txt";
					Idq=model.loader.loadArrays(1800, 4, file2);
					model.element[nex].setVertNumb(nxx);
					magn=5;
				}


				interaction=2;
				int Lt=model.nAnimSteps;

				gui.vwp.rng=0;

				Transform3D tr=new Transform3D();
				Transform3D tr2=new Transform3D();


				int K=1;

				Vect T=new Vect(K*Lt);

				int vmode=model.animDataCode;

				int nnx=model.animChosenNode;
				int comp=model.animChonenNodeComp;
				boolean fillT=(nnx>0);

				Vect[][] BB=new Vect[model.nAnimSteps][1];



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
						int d=i%(model.nEnd-model.nBegin+1)+model.nBegin;

						String file=folder+"\\"+model.fileCommon+d+"."+model.fileExtension;

						model.loadFlux(file,nc,d*model.rotStep);



						if(model.numberOfElements==1){

							double fc=50;
							double fd=25;
							double wc=fc*2*Math.PI;
							double wd=fd*2*Math.PI;

							double dt=1.0/fc/90;
							double t=ix*dt;
							double alpha=0,beta=0,gama=0;



							double Am=.2;


							double u=2*Am*cos(wd*t)/3;
							double v=Am*sin(wd*t)/sqrt(3)-u/2;
							u*=3./2;
							v*=3./2;
							double w=-u-v;
							double small=1e-6;

							if(abs(cos(wc*t))>small)
								alpha=u/cos(wc*t);

							if(abs(cos(wc*t-2*PI/3))>small)
								beta=v/cos(wc*t-2*PI/3);

							if(abs(cos(wc*t+2*PI/3))>small)
								gama=w/cos(wc*t+2*PI/3);


							double roofup=1;
							double rooflow=-1;
							/*			if(alpha>roofup) alpha=roofup;
							if(beta>roofup) beta=roofup;
							if(gama>roofup) gama=roofup;

							if(alpha<rooflow) alpha=rooflow;
							if(beta<rooflow) beta=rooflow;
							if(gama<rooflow) gama=rooflow;*/

							if(alpha>roofup ||beta>roofup ||gama>roofup
									||alpha<rooflow||beta<rooflow||gama<rooflow) 
							{
								alpha=0;
								beta=0;
								gama=0;
							}



							Mat Tab=new Mat(3,3);
							Tab.el[0][0]=1;
							Tab.el[0][1]=-.5;
							Tab.el[0][2]=-.5;
							Tab.el[1][0]=0;
							Tab.el[1][1]=sqrt(3)/2;
							Tab.el[1][2]=-sqrt(3)/2;
							Tab.el[2][0]=.5;
							Tab.el[2][1]=.5;
							Tab.el[2][2]=.5;
							Tab=Tab.times(2.0/3);


							double ia=cos(wc*t)*(alpha);
							double ib=cos(wc*t-2*PI/3)*(beta);
							double ic=cos(wc*t+2*PI/3)*(gama);


							Vect Iabc=new Vect(ia,ib,ic);

							Vect Ialfabeta=Tab.mul(Iabc);

							double bx=Ialfabeta.el[0];
							double by=Ialfabeta.el[1];

							model.element[1].setB(new Vect(bx,by));

						}




						else if(model.numberOfElements==-1){
							double wd=5*2*Math.PI;
							double wc=10*2*Math.PI;

							double dt=1.0/10/180;
							double t=ix*dt;
							double alpha=1,beta=1;
							if(Math.cos(wc*t)!=0 && Math.sin(wc*t)!=0){
								/* alpha=1-Math.cos(wc*t)*Math.sin(wd*t)/
			Math.sqrt(
					Math.pow(Math.sin(wd*t),2)*Math.pow(Math.cos(wc*t),4)+
					Math.pow(Math.cos(wd*t),2)*Math.pow(Math.sin(wc*t),4)
					);
								 */
								/*			 alpha=1./Math.sqrt(Math.pow(Math.cos(wc*t),2)+
	 Math.pow(Math.sin(wc*t),2)*Math.pow(Math.tan(wd*t),2)/Math.pow(Math.tan(wc*t),2));

beta=alpha*abs(Math.tan(wd*t)/Math.tan(wc*t));

alpha*=Math.signum(Math.tan(wc*t));
beta*=Math.signum(Math.tan(wd*t));*/

								alpha=Math.cos(wd*t)/Math.cos(wc*t);
								beta=Math.sin(wd*t)/Math.sin(wc*t);

								/*			 if(Math.sin(wd*t)>0 && Math.cos(wd*t)>0 &&Math.sin(wc*t)>0 && Math.cos(wc*t)<0) alpha*=-1;
else if(Math.sin(wd*t)>0 && Math.cos(wd*t)>0 &&Math.sin(wc*t)<0 && Math.cos(wc*t)<0){ alpha*=-1;beta*=-1;}
else if(Math.sin(wd*t)>0 && Math.cos(wd*t)>0 &&Math.sin(wc*t)<0 && Math.cos(wc*t)>0){ beta*=-1;}
else if(Math.sin(wd*t)>0 && Math.cos(wd*t)<0 &&Math.sin(wc*t)>0 && Math.cos(wc*t)>0){ alpha*=-1;}
else if(Math.sin(wd*t)>0 && Math.cos(wd*t)<0 &&Math.sin(wc*t)>0 && Math.cos(wc*t)<0){ }
else if(Math.sin(wd*t)>0 && Math.cos(wd*t)<0 &&Math.sin(wc*t)<0 && Math.cos(wc*t)<0){beta*=-1; }
else if(Math.sin(wd*t)<0 && Math.cos(wd*t)<0 &&Math.sin(wc*t)<0 && Math.cos(wc*t)<0){}*/
								// beta=alpha*Math.tan(wd*t)/Math.tan(wc*t);



							}

							double roof=2;
							if(alpha>roof) alpha=roof;
							if(beta>roof) beta=roof;
							if(alpha<-roof) alpha=-roof;
							if(beta<-roof) beta=-roof;




							/*	Mat Tdq=new Mat(3,3);
Tdq.el[0][0]=cos(wc*t);
Tdq.el[0][1]=cos(wc*t-2*PI/3);
Tdq.el[0][2]=cos(wc*t+2*PI/3);
Tdq.el[1][0]=-sin(wc*t);
Tdq.el[1][1]=-sin(wc*t-2*PI/3);
Tdq.el[1][2]=-sin(wc*t+2*PI/3);
Tdq.el[2][0]=sqrt(2)/2;
Tdq.el[2][1]=Tdq.el[2][0];
Tdq.el[2][2]=Tdq.el[2][0];
Tdq=Tdq.times(sqrt(2.0/3));*/

							Mat Tab=new Mat(3,3);
							Tab.el[0][0]=1;
							Tab.el[0][1]=-.5;
							Tab.el[0][2]=-.5;
							Tab.el[1][0]=0;
							Tab.el[1][1]=sqrt(3)/2;
							Tab.el[1][2]=-sqrt(3)/2;
							Tab.el[2][0]=sqrt(2)/2;
							Tab.el[2][1]=sqrt(2)/2;
							Tab.el[2][2]=sqrt(2)/2;
							Tab=Tab.times(sqrt(2.0/3));

							alpha=1-.5*cos(wd*t);	
							beta=1-.5*sin(wd*t-2*PI/3);
							double	gama=1-.5*sin(wd*t+2*PI/3);

							double ia=cos(wc*t)*alpha;
							double ib=cos(wc*t-2*PI/3)*beta;
							double ic=cos(wc*t+2*PI/3)*gama;

							Vect Iabc=new Vect(ia,ib,ic);

							Vect Ialfabeta=Tab.mul(Iabc);


							//double bx=Math.cos(wc*t)*alpha;
							//	double by=Math.sin(wc*t)*beta;

							double bx=Ialfabeta.el[0];
							double by=Ialfabeta.el[1];

							model.element[1].setB(new Vect(bx,by));

						}

						if(model.numberOfRegions==1700){
							int v=i%(BB.length);
							int ne=model.region[4].getFirstEl();
							//ne=9832;
							ne=nex;
							model.element[ne].setB(model.getElementCenter(ne).times(0).add(new Vect(Idq[v][1],Idq[v][2]).times(magn)));
						}


						BB[ix]=new Vect[model.numberOfElements];
						for(int ie=1;ie<=model.numberOfElements;ie++)
							BB[ix][ie-1]=model.element[ie].getB();

						ix++;

					}


				}
				//gui.vwp.runMotor=!gui.vwp.runMotor;			

				int m=model.nBegin;
				int ix=0;
				while(true){

					if(fillT && m>=K*(model.nEnd-model.nBegin+1)) break;


					gui.vwp.tfX2.setText(Double.toString(gui.vwp.rng));


					gui.vwp.rng=m*model.rotStep;


					if(model.batchAnim){
						int iy=ix%BB.length;
						for(int ie=1;ie<=model.numberOfElements;ie++)
							model.element[ie].setB(BB[iy][ie-1]);

					}
					else{
						//String file=folder+model.animDataFile[i];

						int d=m%(model.nEnd-model.nBegin+1)+model.nBegin;
						String file=folder+"\\"+model.fileCommon+d+"."+model.fileExtension;

						model.loadFlux(file,nc,d*model.rotStep);

						if(model.numberOfRegions==1700){
							int v=m%(BB.length);
							int ne=model.region[4].getFirstEl();
							//ne=9832;
							ne=nex;
							model.element[ne].setB(model.getElementCenter(ne).times(0).add(new Vect(Idq[v][1],Idq[v][2]).times(magn)));
						}
					}


					if(m==0)
						gui.vwp.setVectField(model,4);
					else
						gui.vwp.rescale(model);


					if(fillT){
						T.el[ix]=model.element[nnx].getB().el[comp];
					}

					tr.rotZ(gui.vwp.rng*PI/180);


					tr2.rotZ(-5*gui.vwp.rng*PI/180);


					for(int k=1;k<=model.numberOfRegions;k++){
						if(model.region[k].rotor)
							gui.vwp.surfFacets[k].setTransform(tr);
						else if(k>8) gui.vwp.surfFacets[k].setTransform(tr2);

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



	public void animateShape()  {

		Thread tr = new Thread() {
			public void run() {

				String path=System.getProperty("user.dir")+"\\CanvasImages";
				File shotsFolder = new File(path);
				deleteDir(shotsFolder);
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


						int d=i%(model.nEnd-model.nBegin+1)+model.nBegin;

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
							if(m>=K*(model.nEnd-model.nBegin+1)) break;


						int i=m%model.nEnd+model.nBegin;

						gui.vwp.tfX2.setText(Integer.toString(i));


						if(model.batchAnim)
						{
							int iy=ix%nv.length;
					
							for(int j=0;j<nDefNodes;j++){
								model.node[map[j]].setU(nv[iy][j]);
								//nv[iy][j].hshow();
							}
						}
						else{
							//file=folder+model.animDataFile[i];

							file=folder+"\\"+model.fileCommon+i+"."+model.fileExtension;



							model.loadNodalField(file, vmode);
							//model.loadNodalField(file2,model.animDataCode,n0);

						}

						if(fillT){

							T.el[ix]=model.node[nnx].getNodalVect(vmode).el[comp];
						}
						if(m==0) {
							gui.vwp.Vmax=1e2*model.uMax;
							gui.vwp.setSlider();
						}
						gui.vwp.vScale=1e7;
						gui.vwp.deformMesh(model);


						Transform3D tr=new Transform3D();
						tr.rotZ(gui.vwp.rng*PI/180);


						for(int k=1;k<=model.numberOfRegions;k++){
							if(model.region[k].rotor)
								gui.vwp.surfFacets[k].setTransform(tr);



						}

						m+=model.nInc;
						
						gui.vwp.bShot.doClick();
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

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
				//int d=i%(model.nEnd-model.nBegin+1);

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
						int d=i%(model.nEnd-model.nBegin+1)+model.nBegin;

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
				int ix=0;
				int m=0;
				while(true){
					{


						Main.wait(model.animTD);


						if(fillT)
							if(m>=K*(model.nEnd-model.nBegin+1)) break;

						int i=m%model.nEnd+model.nBegin;

						gui.vwp.rng=m*model.rotStep;

						gui.vwp.tfX2.setText(Double.toString(gui.vwp.rng));

						file=folder+"\\"+model.fileCommon+i+"."+model.fileExtension;

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
							T.el[ix]=model.node[nnx].getNodalMass();



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
				util.pr(Lt);

				boolean vel=(model.animMode==-1);
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
						int d=i%(model.nEnd-model.nBegin+1)+model.nBegin;

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
							if(m>=K*(model.nEnd-model.nBegin+1)) break;

						int i=m%model.nEnd+model.nBegin;	

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
							T.el[ix]=model.node[nnx].getNodalVect(vmode).el[comp];
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
									util.pr(666);
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

		try{
		this.gui.vwp.vScalefact=Double.parseDouble(this.gui.vwp.tfX1.getText());
		}
		catch(Exception ex)
		{ex.printStackTrace();}
		
		this.gui.vwp.vScale=.02*this.gui.vwp.vScalefact*gui.vwp.vScale0*gui.vwp.jslider.getValue();


		gui.vwp.tfVectorScale.setText(formatter.format(this.gui.vwp.vScale));

		if(interaction==1)
			gui.vwp.deformMesh(model);
		else 	if(interaction==2)
			gui.vwp.scaleVectField(model);
		else 	if(interaction==3)
			timeDelay=this.gui.vwp.vScale;


	}






}

