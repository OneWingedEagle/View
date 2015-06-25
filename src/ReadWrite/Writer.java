package ReadWrite;

import static java.lang.Math.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import fem.EdgeSet;
import fem.Model;
import math.Mat;
import math.Vect;
import math.util;

public class Writer {
	
	public static void main(String[] args) throws IOException{
		Writer wr=new Writer();
		String nodeFile ="C:\\Users\\Hassan\\Desktop\\Triangle\\model.node";
		String elFile = "C:\\Users\\Hassan\\Desktop\\Triangle\\model.1.ele";
		//wr.writeNodeX(nodeFile);
		
	//	wr.writeMesh(nodeFile,elFile);
		
		}

	public void reportData(Model model){
		System.out.println();

		DecimalFormat formatter = new DecimalFormat("0.00E0");

		for(int i=1;i<=model.numberOfRegions;i++){

			System.out.println("model.region "+i +": ");
			System.out.println("Material: "+model.region[i].getMaterial());
			if(model.region[i].isNonLinear)
				System.out.println("   Nonlinear BH Curve"); 
			else{
				Vect v=model.region[i].getMur();
				System.out.printf("%10s","mur:"); v.hshow();	
			}
			Vect v=model.region[i].getSigma();
			System.out.printf("%10s","sigma: "); v.hshow();			
			v=model.region[i].getJ();
			System.out.printf("%10s","J: "); v.hshow();	

			if(model.region[i].hasM){
				v=model.region[i].getM();
				System.out.printf("%10s","M: "); v.hshow();				}

			System.out.println("Young's Moduls: "+formatter.format(model.region[i].getYng()));
			System.out.println("Posion's Ratio: "+model.region[i].getPois());
			System.out.println();

		}
		String method="";
		if(model.analysisMode==0)
			method=" Anlysis Type:   Magnetostatic" ;
		else if(model.analysisMode==1)
			method=" Anlysis Type:   Eddy current A method" ;
		else if(model.analysisMode==2)
			method=" Anlysis Type:   Eddy current A-phi- method" ;

		System.out.println();
		System.out.println("     "+method);
		System.out.println();

		for(int ir=1;ir<=model.numberOfRegions;ir++)
			if(model.region[ir].isNonLinear)
				System.out.println(" Region "+ir+" : Nonlinear B-H curve cosidered. ");
		System.out.println();
		if(model.deform)
			System.out.println(" Including Structural Alanysis. ");

		System.out.println(" Number of regions: "+model.numberOfRegions);
		System.out.println(" Number of elements: "+model.numberOfElements);
		System.out.println(" Number of nodes   : "+model.numberOfNodes+"    known: "+model.numberOfKnownPhis+" , unknown: "+model.numberOfVarNodes);
		System.out.println(" Number of edges   : "+model.numberOfEdges+"    known: "+model.numberOfKnownEdges+" , unknown: "+model.numberOfUnknownEdges);	
		System.out.println(" Total number of unknows   : "+model.numberOfUnknowns);	
		System.out.println();



	}
	
	
	public void writeNodalField(Model model,String nodalForceFile,int mode)
	{
		boolean[] nc=new boolean[1+model.numberOfNodes];
		for(int n=1;n<=model.numberOfNodes;n++)
			nc[n]=true;
		
		writeNodalField(model,nodalForceFile,mode,nc);
		
	}
	
	public void writeNodalField(Model model,String nodalForceFile,int mode,boolean[] nc){
		int dim=model.dim;
		try{
			PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(nodalForceFile)));
			if(mode==0)
				pw.println("force_reluc");
		
			else if(mode==2)
				pw.println("displacement");


			pw.println(dim);

			pw.println(model.numberOfNodes);

			for(int n=1;n<=model.numberOfNodes;n++)
			{

				if(!nc[n])continue;

				Vect v=model.node[n].getNodalVect(mode);
				if(v==null) continue;
				if(v.norm()==0) continue;

				if(mode==2) v=v.times(1e9);

				pw.format("%d\t",n);

				for(int k=0;k<dim;k++)
					pw.format("%E\t",v.el[k]);

				pw.println();

			}
			
			pw.close();
		} catch(IOException e){System.out.println("writing nodal file failed.");}

		System.out.println(" Magnetic nodal force was written to "+nodalForceFile);
	}
	

}
