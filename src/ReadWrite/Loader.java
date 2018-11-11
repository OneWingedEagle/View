package ReadWrite;


import static java.lang.Math.sqrt;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JOptionPane;


import math.IntVect;
import math.Mat;
import math.Vect;
import math.util;
import fem.*;

/**
 * TODO Put here a description of what this class does.
 *
 * @author Hassan.
 *         Created Aug 15, 2012.
 */
public class Loader {
	
	private String regex="[:; ,\\t]+";

	
	public void loadMesh(Model model, String bunFilePath){
		try{
			FileReader fr=new FileReader(bunFilePath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			String s;
			String[] sp;

			String elType=br.readLine();
			model.setElType(elType);

			
			br.readLine();		
			line=br.readLine();
			model.numberOfNodes=getIntData(line);

			br.readLine();		
			line=br.readLine();
			model.numberOfElements=getIntData(line);

			model.element=new Element[model.numberOfElements+1];
			for(int i=1;i<=model.numberOfElements;i++){
				model.element[i]=new Element(elType);
			}
			
			model.spaceBoundary=new double[model.nBoundary];
			
			for(int i=0;i<model.dim;i++){
				model.spaceBoundary[2*i]=1e10;
				model.spaceBoundary[2*i+1]=-1e10;
			}

			model.node=new Node[model.numberOfNodes+1];

			for(int i=1;i<=model.numberOfNodes;i++)
				model.node[i]=new Node(model.dim);

			br.readLine();		
			line=br.readLine();
			model.numberOfRegions=getIntData(line);
			model.region=new Region[model.numberOfRegions+1];
			for(int i=1;i<=model.numberOfRegions;i++)
				model.region[i]=new Region(model.dim);

			br.readLine();		
			line=br.readLine();
			model.scaleFactor=Double.parseDouble(line);

			double factor=1.0/model.scaleFactor;
			for(int i=1;i<=model.numberOfElements;i++){
				
				line=br.readLine();
				int[] vertNumb=this.getCSInt(line);
				model.element[i].setVertNumb(vertNumb);
	
			}

			
			
			for(int i=1;i<=model.numberOfNodes;i++){
				line=br.readLine();
				double[] xyz=this.getCSV(line);
				Vect z=new Vect(xyz);
			
				model.node[i].setCoord(z.times(factor));
				
				if(model.node[i].getCoord(0)<model.spaceBoundary[0]) model.spaceBoundary[0]=model.node[i].getCoord(0);
				else if(model.node[i].getCoord(0)>model.spaceBoundary[1]) model.spaceBoundary[1]=model.node[i].getCoord(0);

				if(model.node[i].getCoord(1)<model.spaceBoundary[2]) model.spaceBoundary[2]=model.node[i].getCoord(1);
				else if(model.node[i].getCoord(1)>model.spaceBoundary[3]) model.spaceBoundary[3]=model.node[i].getCoord(1);
				if(model.dim==3){
					if(model.node[i].getCoord(2)<model.spaceBoundary[4]) model.spaceBoundary[4]=model.node[i].getCoord(2);
					else if(model.node[i].getCoord(2)>model.spaceBoundary[5]) model.spaceBoundary[5]=model.node[i].getCoord(2);
				}	
			
			
				}
			
				for(int i=1;i<=model.numberOfRegions;i++){
				
				line=br.readLine();
				sp=line.split(regex);
				String[] str=new String[10];
				int k=0;
				for(int j=0;j<sp.length;j++){
					if(!sp[j].equals(""))
						str[k++]=sp[j];
					
				}
				model.region[i].setFirstEl(Integer.parseInt(str[0]));	
				
				model.region[i].setLastEl(Integer.parseInt(str[1]));
				model.region[i].setName(str[2]);
				model.region[i].setMaterial(str[2]+"mat");

				int nx=0;
				String[] spc=line.split(regex);
				nx=spc.length;
		
				int cc=-1,bb=0;
				if(nx==4)
				cc=Integer.parseInt(str[nx]);
				else if(nx==5){
					cc=Integer.parseInt(str[nx-2]);
					bb=Integer.parseInt(str[nx-1]);
				}
				else if(nx==6){
					cc=-100;
					util.pr(nx);
					int[] rgb=new int[3];
					rgb[0]=Integer.parseInt(str[nx-3]);
					rgb[1]=Integer.parseInt(str[nx-2]);
					rgb[2]=Integer.parseInt(str[nx-1]);
					model.region[i].setRGB(rgb);
					
				}
			
				model.region[i].setColorCode(cc,bb);

				
		}
				
				line=br.readLine();
			
				if(line!=null)
				model.motor=getBooleanData(line);

				if(model.motor){
					line=br.readLine();

					if(line!=null) {
				
					sp=line.split(regex);
					String[] str=new String[3];
					int k=0;
					for(int j=0;j<sp.length;j++){
						if(!sp[j].equals(""))
							str[k++]=sp[j];
					}

					int rotBegin,rotEnd;

					rotBegin=Integer.parseInt(str[0]);
					rotEnd=Integer.parseInt(str[1]);

					for(int ir=rotBegin;ir<=rotEnd;ir++){
						model.region[ir].rotor=true;
						model.nRotReg++;
					}
				}
				
				}
				
			System.out.println();
			System.out.println("Loading mesh file completed.");

			br.close();
			fr.close();

			for(int ir=1;ir<=model.numberOfRegions;ir++)
				for( int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++)
					model.element[i].setRegion(ir);
			
			model.setMaxDim();
			
			model.center=new Vect(model.dim);
			model.center.el[0]=(model.spaceBoundary[0]+model.spaceBoundary[1])/2;
			model.center.el[1]=(model.spaceBoundary[2]+model.spaceBoundary[3])/2;
			if(model.dim==3)
			model.center.el[2]=(model.spaceBoundary[4]+model.spaceBoundary[5])/2;
				
				
		}
		catch(IOException e){
			e.printStackTrace();

			//System.err.println("Error in loading model file.");
		}


	}	

	
	public boolean loadFlux(Model model,String fluxFilePath){

		return loadFlux(model,fluxFilePath,0);
}

	public boolean loadFlux(Model model,String fluxFilePath, double angDeg){
		
		boolean[] elIn=new boolean[1+model.numberOfElements];
		for(int i=1;i<=model.numberOfElements;i++)
			/*if(model.element[i].getRegion()>7)*/ elIn[i]=true;

		return loadFlux(model,fluxFilePath,angDeg,elIn);
	}

public boolean loadFlux(Model model,String fluxFilePath, double angDeg,boolean[] elementIn){

boolean rotating=true;
	if(angDeg==0) rotating=false;
	

	Mat R=new Mat();
	if(rotating){

	//	angDeg=angDeg-(int)(angDeg/360.0);
		
		R=util.rotMat2D(-angDeg*Math.PI/180);
	}
	
	try{
		FileReader fr=new FileReader(fluxFilePath);
		BufferedReader br = new BufferedReader(fr);
		String line=br.readLine();
		 line=br.readLine();
		util.pr(line);
		int dim=Integer.parseInt(line);
		line=br.readLine();
		int nElements=Integer.parseInt(line);
		

		if(nElements!=model.numberOfElements) {

		
			String msg="Flux file does not match the mesh";
			//throw new IllegalArgumentException(msg);
			
		}

		
		double Bn2,Bmax2=-1e40,Bmin2=1e40;
		
		for(int ir=1;ir<=model.numberOfRegions;ir++)
			for( int i=model.region[ir].getFirstEl();i<=model.region[ir].getLastEl();i++){
			
			
					
				Vect B1=new Vect(dim);
		
/*				if(model.numberOfRegions==12 && ir>10){
					model.element[i].setB(B1);
					continue;
				}*/
				line=br.readLine();
				double[] data=this.getCSV(line);
			for(int j=0;j<dim;j++)
				B1.el[j]=data[j];
			
			/*//if(ir<3) B1.timesVoid(.1);
			double mm=B1.norm();
			*/
/*			B1.normalize();
			B1=B1.times(1-model.getElementCenter(i).norm());*/

			if(rotating && model.region[ir].rotor) B1=R.mul(B1);
			
			if(!elementIn[i])
			{
				B1=new Vect(dim);
			}

			model.element[i].setB(B1);
			
			Bn2=B1.dot(B1);
			
			if(Bn2>Bmax2)
				Bmax2=Bn2;
			if(Bn2<Bmin2)
				Bmin2=Bn2;

		}


		model.Bmax=sqrt(Bmax2);
		model.Bmin=sqrt(Bmin2);
		br.close();
fr.close();
		System.out.println("Flux was loaded from "+fluxFilePath+" to the model.");
		model.fluxLoaded=true;
		return true;
	}
	catch(IOException e){
		e.printStackTrace();

		//System.err.println("Error in loading flux file.");
	return false;
	}

}	
	public boolean loadPotential(Model model,String vPotFile){

		try{
			Scanner scr=new Scanner(new FileReader(vPotFile));

			scr.nextLine();
			int dim=Integer.parseInt(scr.next());

			int nEdges=Integer.parseInt(scr.next());
			if(model.dim==2) model.setEdge();
			if(nEdges!=model.numberOfEdges) {
								String msg="Vector potential file does not match the mesh";
								throw new IllegalArgumentException(msg);
				//JOptionPane.showMessageDialog(null, msg," ", JOptionPane. INFORMATION_MESSAGE);
			}
	
			for(int i=1;i<=nEdges;i++){
			
				double Au=Double.parseDouble(scr.next());
					
				model.edge[i].setAu(Au);
			
			}


			scr.close();

			System.out.println("Vector potential was loaded from "+vPotFile+" to the model.");
			model.potentialLoaded=true;
			return true;
		}
		catch(IOException e){System.err.println("Error in loading vector potential file.");
		return false;
		}

	}	

	
	public boolean loadTemper(Model model,String temperFilePath){

		try{
			Scanner scr=new Scanner(new FileReader(temperFilePath));

			scr.next();
			int dim=Integer.parseInt(scr.next());
			model.dim=dim;
			int nNodes=Integer.parseInt(scr.next());
			
			if(nNodes!=model.numberOfNodes) {
				String msg="temperature  file does not match the mesh";
				throw new IllegalArgumentException(msg);
			//	JOptionPane.showMessageDialog(null, msg," ", JOptionPane. INFORMATION_MESSAGE);
			}


			double tmax=0,tmin=1e10;
			
			while(scr.hasNext()){
				
				int nn=Integer.parseInt(scr.next());

					double T=Double.parseDouble(scr.next());
					model.node[nn].scalar=T;
					if(T>tmax)
						tmax=T;
					if(T<tmin)
						tmin=T;
			}

			
			
				model.nodalScalarMax=tmax;
				model.nodalScalarMin=tmin;
				
			scr.close();


			System.out.println("scalar field was loaded from "+temperFilePath+" to the model.");
			return true;
		}
		catch(IOException e){System.err.println("Error in loading nodal data.");


		return false;
		}
	}
	
	
	public Vect[][] loadBunch(Model model,String nodalForceFile,IntVect nn, int mode){
		int dim=model.dim;
		Vect[][] v1=new Vect[1][1];
		Vect[][] veloc=new Vect[1][1];
		
		boolean vel=(mode>5 && mode<12);
		
	
		try{
			FileReader fr=new FileReader(nodalForceFile);
			BufferedReader br = new BufferedReader(fr);
			String line;

			line=br.readLine();
			line=br.readLine();
			line=br.readLine();
			int nModelNodes=Integer.parseInt(line);
			
			if(nModelNodes!=model.numberOfNodes) System.err.println("Nodal Field file does not mach the mesh");
			int[] nn1=new int[nModelNodes];
			int ix=0;
			for(int i=0;i<=nModelNodes;i++)
			{
				line=br.readLine();

				if(line==null || line.equals("") || line.startsWith("step")) break;
				
				nn1[ix++]= Integer.parseInt(line);
			}
			
			int N=ix;
			nn.el=Arrays.copyOf(nn1,N);
			
			nn.length=N;
			
			v1=new Vect[3000][N];
			
			ix=0;
			for(int i=0;i<3000;i++){

				for(int j=0;j<N;j++){
					line=br.readLine();
				if(line==null)break;
					v1[i][j]=getVectData(line,dim);
					
				}
				if(line==null)break;

				if(line.startsWith("step")) continue;


				ix++;
				line=br.readLine();
			}
			
			int Lt=ix;
				
			util.pr(Lt);
			util.pr(N+" <<");
				

			
			Vect[][] v2=new Vect[Lt][N];
			
			for(int i=0;i<Lt;i++)
			 for(int k=0;k<N;k++)
					v2[i][k]=v1[i][k].deepCopy();
			

		if(vel)	{
				
				 double dt=.01/180;
				 double rdt=1.0/dt;
					
			 
					veloc=new Vect[Lt][N];
				
					for(int i=0;i<Lt;i++)
						for(int j=0;j<N;j++){
							if(i==0)
								veloc[i][j]=new Vect(dim);
							else
								veloc[i][j]=v2[i][j].sub(v2[i-1][j]).times(rdt);
						}

					
				
					
					for(int j=0;j<N;j++)
						veloc[0][j]= veloc[Lt-1][j];
					
					
						}


			
			
			br.close();
			fr.close();

			if(vel)
			return veloc;
			else return v2;
				 
		
		} catch(IOException e){System.err.println("IOException: " + e.getMessage());}
		
		return v1;

		
	}
	
	public double[][] loadBunchScalar(Model model,String file,IntVect nn, int mode){
		
		double[][] v1=new double[1][1];

		try{;
			FileReader fr=new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;

			line=br.readLine();
			line=br.readLine();
			line=br.readLine();
			int nNodes=Integer.parseInt(line);

			//line=br.readLine();
			int[] nn1=new int[nNodes];
			int ix=0;
			for(int i=0;i<nn1.length;i++){
				
				line=br.readLine();
				if(line.startsWith("step")) break;

				nn1[ix++]=Integer.parseInt(line);
			}
			
			int[] nn2=Arrays.copyOf(nn1, ix);

			int N=ix;

			nn.el=nn2;
			nn.length=N;
			
			v1=new double[N][1000];
			
			boolean edf=false;
			 ix=0;
			while(!edf){
				line=br.readLine();
				if(line==null) {edf=true; break;}

				if(line.startsWith("step")) {ix++; continue;}
				 for(int k=0;k<N;k++){
					 if(k>0)
					 line=br.readLine();
					 
						v1[k][ix]=Double.parseDouble(line);
					
				
						
					
				
				 }
			}

			int L=ix;
		
			double[][] v2=new double[N][L];
			
			for(int i=0;i<N;i++)
			 for(int j=0;j<L;j++)
					v2[i][j]=v1[i][j];

			return v2;
		
		} catch(IOException e){System.err.println("IOException: " + e.getMessage());}
		
		return v1;

		
	}
	
	public void loadAnimationData(Model model,String filePath){

		try{
			FileReader fr=new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			
			String line;
			line=br.readLine();
			
			model.animDataCode=getIntData(line);
			
			line=br.readLine();
			line=br.readLine();
			model.filePath=br.readLine();
			
			util.pr(model.filePath);
			
			line=br.readLine();
			line=br.readLine();
			model.animDataFolder=br.readLine();
			
			line=br.readLine();
			line=br.readLine();
			model.fileCommon=br.readLine();
			
			line=br.readLine();
			line=br.readLine();
			String sp[]=line.split(regex);
			int L=sp.length;
			
			boolean intNumbs=true;
			for(int i=0;i<sp[L-1].length();i++)
				if(sp[L-1].charAt(i)=='.') intNumbs=false;
			
			int nSteps=1;
		
			int n1=0,n2=0, d=1;
			double a1=0,a2=0,h=1;
			
			if(intNumbs){
				n1=Integer.parseInt(sp[L-3]);
				n2=Integer.parseInt(sp[L-2]);
				d=Integer.parseInt(sp[L-1]);
				if(d!=0)
				 nSteps=(n2-n1)/d+1;
			}
			else{
			a1=Double.parseDouble(sp[L-3]);
			a2=Double.parseDouble(sp[L-2]);
			h=Double.parseDouble(sp[L-1]);
			if(h!=0)
			nSteps=(int)Math.round((a2-a1)/h)+1;
			}
			
		

			model.nBegin=n1;
			model.nEnd=n2;
			model.nInc=d;
			model.nAnimSteps=nSteps;
			
			line=br.readLine();
			line=br.readLine();
			model.fileExtension=br.readLine();
			
			
			br.readLine();
			line=br.readLine();
			model.animMode=getIntData(line);
			
			br.readLine();
			line=br.readLine();
			model.batchAnim=getBooleanData(line);
			
			
			line=br.readLine();
			line=br.readLine();
			
			model.animTD=getIntData(line);
			
			line=br.readLine();
			line=br.readLine();
			line=br.readLine();
			try{
			model.animRegs=this.getCSInt(line);
			}catch(NumberFormatException e){
				if(line.equals("all"))
				model.animRegs=new int[0];
			}
	
		
			br.readLine();
			line=br.readLine();
			model.rotStep=getScalarData(line);
			model.rotStep=getScalarData(line);
			
			br.readLine();
			line=br.readLine();
			model.dt=getScalarData(line);
			
			br.readLine();
			line=br.readLine();
			model.animChosenNode=getIntData(line);
			line=br.readLine();
			if(model.animChosenNode>0)
			model.animChonenNodeComp=getIntData(line);
			else
				model.chosenNodeCoord=br.readLine();

			
		/*	
			String[] animFile=new String[nSteps];
			

			if(intNumbs){
			
				
				int ix=0;
				for(int i=n1;i<=n2;i+=d){
					
					animFile[ix++]=fileCommon+i+"."+extension;
				}
			}
				else{
					int ix=0;
					for(double x=a1;x<=a2;x+=h){
						animFile[ix++]=fileCommon+x+"."+extension;
					}
				}
					
	model.animDataFile=animFile;*/
	
				
		}
		catch(IOException e){
			e.printStackTrace();
			//System.err.println("Error in loading model file.");
		}


	}
	
	public boolean loadStress(Model model,String stressFilePath){
	/*	double[] sh=new double[48*14+40] ;
		double[] h=new double[48*14+40] ;
		int ix=0;
		Vect v0=new Vect(0.08211, 0.0079);*/

		try{
			Scanner scr=new Scanner(new FileReader(stressFilePath));

			scr.next();
			int dim=Integer.parseInt(scr.next());
			int L=3*(dim-1);
			
			int nElements=Integer.parseInt(scr.next());
			if(nElements!=model.numberOfElements) {
				String msg="Stress file doesnt match the mesh";
				throw new IllegalArgumentException(msg);
				//JOptionPane.showMessageDialog(null, msg," ", JOptionPane. INFORMATION_MESSAGE);
			}
			scr.nextLine();
	
		
			model.stressViewCode=Integer.parseInt(scr.next());
			model.nodalStressMaxCode=Integer.parseInt(scr.next());
			model.nodalScalarMin=Double.parseDouble(scr.next());
			model.nodalScalarMax=Double.parseDouble(scr.next());
			
			
			while(scr.hasNext()){
				
				int ne=Integer.parseInt(scr.next());

					Vect ss=new Vect(L);
					for(int j=0;j<L;j++)
						ss.el[j]=Double.parseDouble(scr.next());
					
					//if(model.element[ne].getRegion()!=1) continue;

					model.element[ne].setDeformable(true);
					model.element[ne].setStress(ss);
			
				//	Vect v1=model.getElementCenter(ne).v2();
					
			/*		if( v1.sub(v0).norm()<1e-4){
						h[ix]=model.getElementCenter(ne).el[2];
						
						sh[ix++]=ss.el[1];
					}*/
						
			}
/*			
			util.plot(h,sh);
			util.show(sh);
			util.show(h);*/
	
	
			model.stressViewCode=5;
		model.setNodalScalar(1);

			scr.close();
			
/*			for(int i=1;i<=model.numberOfElements;i++){
				Vect c=model.getElementCenter(i);
				if(Math.abs(c.el[2])<.02 && Math.abs(c.norm()-.075)<.001){
					c.hshow();
					model.element[i].getStress().hshow();
				}
			}*/

			System.out.println("Stress was loaded from "+stressFilePath+" to the model.");
			return true;
		}
		catch(IOException e){
			//System.err.println("Error in loading stress file.");
			e.printStackTrace();

		return false;
		}



	}


	public boolean loadNodalField(Model model,String stressFilePath,int mode){
		
		boolean[] nodeIn=new boolean[1+model.numberOfNodes];
		for(int i=1;i<=model.numberOfNodes;i++)
			nodeIn[i]=true;
		return loadNodalField( model, stressFilePath, mode,nodeIn,0);

	}
	
	public boolean loadNodalField(Model model,String stressFilePath,int mode,int n0){
		
		boolean[] nodeIn=new boolean[1+model.numberOfNodes];
		for(int i=1;i<=model.numberOfNodes;i++)
			nodeIn[i]=true;
		return loadNodalField( model, stressFilePath, mode,nodeIn,n0);

	}

	public boolean loadNodalField(Model model,String stressFilePath,int mode,boolean[] nodeIn,int n0){

		try{
			Scanner scr=new Scanner(new FileReader(stressFilePath));

			//int n0=6590;
			//n0=11018;
			scr.next();
			int dim=Integer.parseInt(scr.next());
			model.dim=dim;
			int nx=Integer.parseInt(scr.next());
			
			int ned=0;
			
			if(mode==3){
				model.setEdge();
				ned=model.numberOfEdges;

				if(nx!=model.nXYedges) {
				
					String msg="Nodal field file does not match the mesh";
					throw new IllegalArgumentException(msg);
					//JOptionPane.showMessageDialog(null, msg," ", JOptionPane. INFORMATION_MESSAGE);
				}
			}
			else if(nx!=model.numberOfNodes) {
				String msg="Nodal field file does not match the mesh";
				throw new IllegalArgumentException(msg);
				//JOptionPane.showMessageDialog(null, msg," ", JOptionPane. INFORMATION_MESSAGE);
			}

	
			double sn2=0,smax2=0,smin2=0;
			int nn;
			
			int i=0;
			while(scr.hasNext() ){
	
				i++;
				
			
				
				nn=Integer.parseInt(scr.next())+n0;
				if( !nodeIn[nn]) {
					scr.nextLine();
					continue;
				
				}
				
			
			
					Vect v=new Vect(dim);
					for(int j=0;j<dim;j++)
						v.el[j]=Double.parseDouble(scr.next());
	
			//	if(model.node[nn].getR()<.0546 || util.getAng(model.node[nn].getCoord())>Math.PI/6) continue;
					
				//	if(model.node[nn].getR()>.059) continue;
				
					model.node[nn].setDeformable(true);	
					
						if(mode==0)
							model.node[nn].setF(v);
						else if(mode==1)
							model.node[nn].setFms(v);
						else if(mode==2){
							v.timesVoid(1e-9);
						model.node[nn].setU(v);
						}
						else if(mode==3)
							model.edge[nn+ned].F=v.deepCopy();
						sn2=v.dot(v);
			
						
					if(sn2>smax2)
						smax2=sn2;
					if(sn2<smin2)
						smin2=sn2;
			}

		
	
			if(mode==0){
				model.FreluctMax=sqrt(smax2);
			}

			else	if(mode==1){
				model.FmsMax=sqrt(smax2);

			}
			else if(mode==2){
				model.uMax=sqrt(smax2);

			}
			else	if(mode==3){
				model.FedMax=sqrt(smax2);

			}
			if(mode>0)
				model.forceLoaded=true;
			
	
			scr.close();

			
			System.out.println("Force was loaded from "+stressFilePath+" to the model.");
			return true;
		}
		catch(IOException e){
			e.printStackTrace();

		//	System.err.println("Error in loading nodal field.");
		
			
		return false;
		}
	}

/*	public boolean loadSurfForce(Model model,String filePath,int mode){
		
		model.setEdge();

		try{
			Scanner scr=new Scanner(new FileReader(filePath));

			scr.next();
			int dim=Integer.parseInt(scr.next());
			model.dim=dim;
			int nedxy=Integer.parseInt(scr.next());

			if(nedxy!=model.nXYedges)
			{
				util.pr(nedxy+"  "+model.nXYedges);
				String msg="surface force file does not match the mesh";
				JOptionPane.showMessageDialog(null, msg," ", JOptionPane. INFORMATION_MESSAGE);
				return false;
			}


			double sn2=0,smax2=0,smin2=0;
			for(int i=1;i<=nedxy;i++){

				Vect v=new Vect(dim);
				for(int j=0;j<dim;j++)
					v.el[j]=Double.parseDouble(scr.next());

				
					model.edge[i+model.numberOfEdges].F=v.deepCopy();
			}

			scr.close();


			System.out.println("Force was loaded from "+filePath+" to the model.");
			return true;
		}
		catch(IOException e){System.err.println("Error in loading force.");


		return false;
		}
	}*/
	
 public Vect loadNodalU(String uuFile,int n,int p){
	 	double[] a=new double[1000];
	 	Vect v=new Vect(1000);
	 	int L=0;
	 try{
		FileReader fr=new FileReader(uuFile);
		BufferedReader br = new BufferedReader(fr);
		String line;
		String s;
		String[] sp;
		String regex="[ ,\\t]+";
		boolean found=false;
		for(int i=0;i<100000;i++){
			line=br.readLine();
			if(line==null) break;
			sp=line.split(regex);
			if(sp.length<2){
				int nn=Integer.parseInt(sp[0]);
				if(nn==n){
					found=true;

					for(int ir=0;ir<=p;ir++)
					line=br.readLine();
					
					sp=line.split(regex);
					L=sp.length;
				
					for(int k=0;k<L;k++){
						a[k]=Double.parseDouble(sp[k]);
						
					}
					
				}
			}
			
			v=new Vect(L);
				for(int k=0;k<L;k++){
					v.el[k]=a[k];
				}
		
			if(found){
			break;
			}
		}
				
	
 }catch(IOException e){System.err.println("file not found.");}
	
 return v;

 } 
 
 
 public void loadNodalU2(String uuFile,int n,int p){
		
	 try{
		FileReader fr=new FileReader(uuFile);
		BufferedReader br = new BufferedReader(fr);
		String line;
		String s;
		String[] sp;
		String regex="[ ,\\t]+";

		for(int i=0;i<100000;i++){
			line=br.readLine();
			if(line==null) break;
			sp=line.split(regex);
			if(sp.length<2){
				int nn=Integer.parseInt(sp[0]);
				if(nn==n){
					for(int ir=0;ir<=p;ir++)
					line=br.readLine();
					
					sp=line.split(regex);
					for(int k=0;k<sp.length;k++)
						util.pr(Double.parseDouble(sp[k]));
				}
			}
		}
	
 }catch(IOException e){System.err.println("file not found.");}

 } 
 
 private int[] getBCdata(String line,Vect B){
	 
	 int[] bctp=new int[2];
	 String[] sp=line.split("");	
	 int pair=-1,bct=0;
	 	int k=0;
	 	while(!sp[k].equals(":")){ k++;}	
	 	k++;
	 	if(k==sp.length) return bctp;
		if(sp[k].equals(" ")) k++;
	 	String s=sp[k];
	 	
	 	 if(s.equals("P")){
	 		 
		if(sp[k+1].equals("S")){
			bct=2;
				}
	 	
		else if(sp[k].equals("A")){

			bct=3;
		}
			
 		sp=line.split(regex);
		 String s2=sp[sp.length-1];
		 if(s2.equals(","))
			 s2=sp[sp.length-2];
		pair=Byte.parseByte(s2)-1;
	
		}
	 	
	 else if(s.equals("N"))
			bct=0;
	 	
	else if(s.equals("D")){
			sp=line.split(regex);
			bct=1;
			 k=0;
				while(k<sp.length && !sp[k].equals("[")){k++;}	
				
				if(k<sp.length-1){
				k+=1;
				if(sp[k].equals(" ")) k++;
			
				for( int p=0;p<B.length;p++)
					B.el[p]=Double.parseDouble(sp[k+p]);
				}
			
		}
		
		
		
		bctp[0]=bct;
		bctp[1]=pair;
		
	return bctp;
 }
 
 public Vect getVectData(String line, int dim){	

	 String[] sp=line.split(regex);	

	Vect v=new Vect(dim);
	int p0=0;
	if(sp[0].equals(""))
	{
		p0=1;
	}

	for( int p=0;p<dim;p++){

		v.el[p]=Double.parseDouble(sp[p+p0]);
	}

	return v;
	}
 
 private double[] getArrayData(String line, int L){
		String[] sp=line.split(regex);	
		double[] v=new double[L];
		int k=0;
		while(!sp[k].equals("[")){k++;}			
		k+=1;
		if(sp[k].equals(" ")) k++;
	
		for( int p=0;p<L;p++)
			v[p]=Double.parseDouble(sp[k+p]);
		
	return v;
}
 private double getScalarData(String line){
		String[] sp=line.split(regex);	
	return Double.parseDouble(sp[sp.length-1]);
}
 
 private int getIntData(String line){
		String[] sp=line.split(regex);	
		return Integer.parseInt(sp[sp.length-1]);
}

	private boolean getBooleanData(String line){
		boolean b=false;
		String[] sp=line.split(regex);	
		
		if(sp[sp.length-1].startsWith("t"))	
			b=true;
		
		return b;

	}
 
 private String getStringData(String line){
		String[] sp=line.split(":");	
		String[] sp2=sp[sp.length-1].split(" ");
	return sp2[sp2.length-1];
}
 
	private double[] getCSV(String line){
		
		String[] sp=line.split(regex);	
	
		int p0=0;
		if(sp[0].equals(""))
		{
			p0=1;
		}
		int L=sp.length-p0;

		double[] v=new double[L];

		for( int p=0;p<L;p++){

			v[p]=Double.parseDouble(sp[p+p0]);
		}

		return v;
	}
	
	private int[] getCSInt(String line){
		String[] sp=line.split(regex);	
		int L=sp.length;
		int[] v=new int[L];
		for( int p=0;p<L;p++)
					v[p]=Integer.parseInt(sp[p]);

		return v;
	}
	
	public double[] loadArray(){
		String file=util.getFile();
		if(file==null || file.equals("") )  throw new NullPointerException("file not found.");
		return loadArray(file);
	}

	public double[] loadArray(String arrayPath){

		try{
			FileReader fr=new FileReader(arrayPath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			String s;
			String[] sp;

			int N=100000;
			
			double[] x1=new double[N];
			
			int i=0;
			line=br.readLine();
			while(line!=null){
				if(i>N) break;
				x1[i++]=Double.parseDouble(line);
				line=br.readLine();
				
			}

			double[] x=Arrays.copyOf(x1, i);
			
				return x;
				
		}
		catch(IOException e){
			e.printStackTrace();//System.err.println("Error in loading model file.");
		}


		return null;
	}	

	public double[][] loadArrays(int n, int m,String arrayPath){

		try{
			FileReader fr=new FileReader(arrayPath);
			BufferedReader br = new BufferedReader(fr);
			String line;
			String s;
			String[] sp;

		
			
			double[][] A=new double[n][m];
			
			for(int i=0;i<n;i++){
				line=br.readLine();
				if(line==null) continue;
				double[] x=getCSV(line);
				for(int j=0;j<m;j++)
					A[i][j]=x[j];
		
				
				
			}

			
				return A;
				
		}
		catch(IOException e){
			e.printStackTrace();//System.err.println("Error in loading model file.");
		}


		return null;
	}

 
}
