package math;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.vecmath.Matrix3d;

import org.math.plot.Plot2DPanel;

public class util {

	
	public static void main(String[] args) throws Exception{
		int N=50,M=1;
		double[][] ems=new double[M][N];
		double mu0=4*PI*1e-7;
		double z4_0=-.99974858;
		double z4_1=-0.00076057;
		double z4_2=-.00089881;
		double z4_3=.00008964;
		double z4_4=.00012688;
		double z5=-.32974548;
		z5=-.34426261;
		double z6=7457.27;	
	
		double E=2e11;
		double v=.3;
		double B,beta,b1,z4,I4,c1,c2,c3,tau;
		Plot2DPanel plot = new Plot2DPanel();
		Vect Bv=new Vect().linspace(.05,1.9, N-1);;

		
		for(int j=0;j<M;j++){
			tau=j*5e5;
			for(int i=0;i<N;i++){
				
				I4=pow(Bv.el[i],2);
			
				b1=I4/(E*mu0);
				beta=1+(2*v*z5)*b1;
				z4=z4_0+z4_1*I4+z4_2*I4*I4+z4_3*pow(I4,3)+z4_4*pow(I4,4);
				c1=-1/(4*v*z6*b1);
				c2=8*v*z6*b1/(beta*beta);
				c3=.5*(1+2*v+4*v*z4+z5);
				
				ems[j][i]=1e6*(c1*(beta*(1-pow(1+c2*(tau/E-c3*b1),.5))));
				double k1=1/(1+2*v*z5*b1);
				//ems[j][i]=1e6*k1*(tau/E-c3*b1);
				
				double B2=I4;
				double B4=I4*I4;
				double B6=B4*B2;
				double B8=B4*B4;
				double p0,p1,p2,p3,p4;
	
				p0=z4_0;
				p1=z4_1;
				p2=z4_2;
				p3=z4_3;
				p4=z4_4;
				
				
				
				 k1=1/(1-2*v*(1+2*v+4*v*p0));
				 
				//ems[j][i]=1e6*k1*(mu0*tau-.5*B2*(1+2*v+4*v*(p0+p1*B2+p2*B4+p3*B6+p4*B8)+z5));
			//	ems[j][i]=k1*(mu0*tau-.5*(p1*B2+p2*B4+p3*B6+p4*B8));
				//ems[j][i]=.5*k1*(p1*B2+p2*B4+p3*B6+p4*B8);
				
				 ems[j][i]=k1*(mu0*tau-.5*(p1*B2+p2*B4+p3*B6+p4*B8));
				
				
			}
			
					
			plot.addLinePlot("my plot", Bv.el, ems[j]);
		}
		
	
		JFrame frame = new JFrame("a plot panel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);
		//util.show(ems);


	}
	
	public static double max(double[] x){
		double max=x[0];
		for(int i=1;i<x.length;i++)
		if(x[i]>max)
			max=x[i];
		return max;
	}
	
	public static int max(int[] x){
		int max=x[0];
		for(int i=1;i<x.length;i++)
		if(x[i]>max)
			max=x[i];
		return max;
	}	
	public static int indmax(double[] x){
		int indmax=0;
		double max=x[0];
		for(int i=1;i<x.length;i++)
		if(x[i]>max)
			indmax=i;
		return indmax;
	}
	public static int indpiv(Mat x,int j){

		int indmax=j;
		double max=abs(x.el[0][j]);
		for(int i=j;i<x.nRow;i++)
		if(abs(x.el[i][j])>max){
			max=abs(x.el[i][j]);
			indmax=i;
				}
		return indmax;
	}
	
	public static double[] linspace(double a, double b,int N){
		double[] v=new double[N];
		double d=(b-a)/(N-1);
		for(int i=0;i<N;i++)
		v[i]=a+i*d;
		return v;
	}
	
	public static double[][] copy(double[][] a){
		int I=a.length;
		int J=a[0].length;
		double[][] a1=new double[I][J];
		for(int i=0;i<I;i++)
			for(int j=0;j<J;j++)
				a1[i][j]=a[i][j];			
				
		return a1;
	}
	
	public static double[][] cubicSpl(double[][] xy){
		int I=xy.length-1;
		double[][] coefs=new double[I][4];
		int L=I-1;
		double[] h=new double[I];
		for(int i=0;i<I;i++)
			h[i]=xy[i+1][0]-xy[i][0];

		Vect b=new Vect(L);
		for(int i=0;i<b.length;i++)
			b.el[i]=6*((xy[i+2][1]-xy[i+1][1])/h[i+1]-(xy[i+1][1]-xy[i][1])/h[i]);

		Mat A=new Mat(L,L);
		
			A.el[0][0]=2*(h[0]+h[1]);
			A.el[0][1]=h[1];
			
			for(int i=1;i<L-1;i++){
				
				A.el[i][i-1]=h[i];
				A.el[i][i]=2*(h[i]+h[i+1]);
				A.el[i][i+1]=h[i+1];
			
			}
			
			A.el[L-1][L-2]=h[L-1];
			A.el[L-1][L-1]=2*(h[L-2]+h[L-1]);
			
			Vect M1=gaussel(A, b);
			double[] M=new double[xy.length];
			M[0]=0; M[xy.length-1]=0;
			for(int i=1;i<M.length-1;i++){
				M[i]=M1.el[i-1];
			}

			for(int i=0;i<coefs.length;i++){
				coefs[i][0]=(M[i+1]-M[i])/(6*h[i]);
				coefs[i][1]=M[i]/2;
				coefs[i][2]=(xy[i+1][1]-xy[i][1])/h[i]-h[i]*(M[i+1]+2*M[i])/6;
				coefs[i][3]=xy[i][1];
			}

			
		return coefs;
	}
	
	public static Vect gaussel(Mat A, Vect b){
		int[] dim=A.size();
		if(dim[0]!=dim[1]) throw new IllegalArgumentException("Matrix is not square");
		int I=dim[0];
		Mat Ab=new Mat();
		Ab=A.aug(b);
		Ab.low0();
		Vect x=new Vect(I);
		x=solveup(Ab);
		return x;
		
	}
	
	public static Mat rotEuler(Vect rotAx,double alpha)
	{
		double e1,e2,e3,e4;
		   
		   e1=rotAx.el[0]*sin(alpha/2);
		   e2=rotAx.el[1]*sin(alpha/2);
		   e3=rotAx.el[2]*sin(alpha/2);
		   e4=cos(alpha/2);
		  
			Mat M=new Mat(3,3);
		   M.el[0][0]=pow(e1,2)-pow(e2,2)-pow(e3,2)+pow(e4,2);
		   M.el[0][1]=2*(e1*e2-e3*e4);
		   M.el[0][2]=2*(e1*e3+e2*e4);
		   M.el[1][0]=2*(e1*e2+e3*e4);
		   M.el[1][1]=-pow(e1,2)+pow(e2,2)-pow(e3,2)+pow(e4,2);
		   M.el[1][2]=2*(e2*e3-e1*e4);
		   M.el[2][0]=2*(e1*e3-e2*e4);
		   M.el[2][1]=2*(e2*e3+e1*e4);
		   M.el[2][2]=-pow(e1,2)-pow(e2,2)+pow(e3,2)+pow(e4,2);
		   return M;
	}
	

	public static Matrix3d mat3d(Vect V, Vect oldAx ){

		int dim=V.length;
	
		Mat M1;
		M1=rotMat(V,oldAx);
		double[] mArray=new double[9];

	for(int i=0;i<dim;i++)
	for(int j=0;j<dim;j++)
		mArray[3*i+j]=M1.el[i][j];
	
	if(dim==2)
		mArray[8]=1;

		Matrix3d M=new Matrix3d(mArray);
		return M;
	}
	
	public static Matrix3d mat3d(Mat A){
		
		int dim=A.nRow;
		
		double[] mArray=new double[9];

		for(int i=0;i<dim;i++)
		for(int j=0;j<dim;j++)
			mArray[3*i+j]=A.el[i][j];
		
		if(dim==2)
			mArray[8]=1;
		
		Matrix3d M=new Matrix3d(mArray);
		
		return M;
	}

	
	
	public static Matrix3d rMat2D(Vect newAx, Vect oldAx ){

		double[] mArray=new double[9];

		Mat M1=util.rotMat2D(newAx, oldAx);

		mArray[0]=M1.el[0][0];
		mArray[1]=M1.el[0][1];
		mArray[2]=0;
		mArray[3]=M1.el[1][0];
		mArray[4]=M1.el[1][1];
		mArray[5]=0;
		mArray[6]=0;
		mArray[7]=0;
		mArray[8]=1;

		Matrix3d M=new Matrix3d(mArray);
		return M;
		}
	
/*	public static Matrix3d mat3dScale(Vect scale ){


		double[] mArray=new double[9];


		mArray[0]=scale.el[0];
		mArray[1]=0;
		mArray[2]=0;
		mArray[3]=0;
		mArray[4]=scale.el[1];
		mArray[5]=0;
		mArray[6]=0;
		mArray[7]=0;
		mArray[8]=scale.el[2];
		Matrix3d M=new Matrix3d(mArray);

		return M;
		}*/


	
	
	
	public static Mat rotMat(Vect newAx,Vect oldAx){

		if(newAx.length==2) return rotMat2D(newAx,oldAx);
		
	 	Mat M=new Mat(3,3);
		
	 	double newAxn=newAx.norm();
	 	  if(newAxn==0){M.eye(); return M;}
	 	 double oldAxn=oldAx.norm();
				 double alpha,cos;
				 Vect rotAx=oldAx.cross(newAx);
				 if(rotAx.norm()==0){M.eye(); return M;}
				rotAx.normalize();
				
				 
				 cos=oldAx.dot(newAx)/(newAxn*oldAxn);
				 if(cos>=1)
					 alpha=0;
				 else if(cos<=-1)alpha=PI;
				 else
					 alpha=acos(cos);

				 return rotEuler(rotAx,alpha);
 }
	
	public static Mat rotMat2D(Vect newAx,Vect oldAx){

	 	 double ang1=getAng(oldAx);
	 	 double ang2=getAng(newAx);

	 	 return rotMat2D(ang2-ang1);
 }
	
	public static Mat rotMat2D(double rad){

	 	Mat M=new Mat(2,2);
		M.el[0][0]=cos(rad);
		M.el[1][1]=	M.el[0][0];
		M.el[0][1]=-sin(rad);
		M.el[1][0]=-M.el[0][1];
	 	
	return M;
 }

	public static Mat tensorize(Vect v){
		int dim=(v.length+3)/3;
		Mat S=new Mat(dim,dim);
		for(int i=0;i<dim;i++)
			S.el[i][i]=v.el[i];
		if(dim==2) {
			S.el[0][1]=v.el[2];
			S.el[1][0]=v.el[2];
		}
		else {
			S.el[0][1]=v.el[3];
			S.el[1][0]=v.el[3];
			S.el[1][2]=v.el[4];
			S.el[2][1]=v.el[4];
			S.el[0][2]=v.el[5];
			S.el[2][0]=v.el[5];
		}
		return S;
		
	}
	
	public static Vect vectorize(Mat S){
		int dim=S.nCol;
		int L=3*(dim-1);
		Vect v=new Vect(L);
		for(int i=0;i<dim;i++)
			v.el[i]=S.el[i][i];
		
		if(dim==2) {
			v.el[2]=S.el[0][1];
		}
		else {
			v.el[3]=S.el[0][1];
			v.el[4]=S.el[1][2];
			v.el[5]=S.el[0][2];
		
		}
		return v;
		
	}

	public static Mat rotMatrix(Mat Q1,Mat Q2){
		Mat T=new Mat(3,3);
		for(int i=0;i<3;i++)
			for(int j=0;j<3;j++)
				T.el[i][j]=Q1.getColVect(i).dot(Q2.getColVect(j));
		return T;
	}
	
	public static Vect solveup(Mat Ab){
		int I=Ab.nRow;
		int J=Ab.nCol;
		if(I!=J-1) throw new IllegalArgumentException("Matrix is not square");
		Vect x=new Vect(I);
		x.el[I-1]=Ab.el[I-1][J-1]/Ab.el[I-1][I-1];
	
		for(int i=I-2;i>=0;i--){
			double s=0;
			for(int j=i+1;j<I;j++)
			s=s+Ab.el[i][j]*x.el[j];
			x.el[i]=(Ab.el[i][J-1]-s)/Ab.el[i][i];
		}
	
		return x;
			}
	
	public static double getAng(Vect v){
		double ang=0;
		if(v.norm()==0) return ang;
		else if(v.el[0]>=0 && v.el[1]>=0) ang=atan(abs(v.el[1]/v.el[0]));
		else if(v.el[0]<=0 && v.el[1]>=0) ang=PI-atan(abs(v.el[1]/v.el[0]));
		else if(v.el[0]>=0 && v.el[1]<=0) ang=2*PI-atan(abs(v.el[1]/v.el[0]));
	
		else ang=atan(abs(v.el[1]/v.el[0]))+Math.PI;
		
		return ang;
	}
	
	
	public  static String getFile(int mode){
		String filePath="";
		FileDialog fd;
		if(mode==0)
		fd= new FileDialog(new Frame(),"Select bun  file",FileDialog.LOAD);
		else
		fd= new FileDialog(new Frame(),"Select bun  file",FileDialog.SAVE);
		fd.setVisible(true);
		fd.toFront();
		String Folder=fd.getDirectory();
		String File = fd.getFile();
		if(Folder!=null && File!=null)
		{

			filePath=Folder+"\\"+File;

		}
		fd.dispose();
		return filePath;
	}
	
	public  static String getFile(){
		
		return getFile(0);
	}
	
	public static int[] sortind(int[] a){
		int[] ind=new int[a.length];
		int[][] v=new int[a.length][2];
		 for(int i=0;i<a.length;i++){
			 v[i][0]=a[i];
			 v[i][1]=i;
		 }
		 int[] temp=new int[2];
		 for(int i=0;i<a.length-1;i++){
			 for(int j=0;j<a.length-i-1;j++)
			 if(v[j+1][0]<v[j][0]){
					 temp=v[j];    
					v[j]=v[j+1];
					 v[j+1]=temp;

				  }
		
		 }
		 for(int i=0;i<a.length;i++)
			 ind[i]=v[i][1];
		return ind;
}

	public static double[][][] grid(double[] x, double[] y){
		double[][][] grid= new double[2][y.length][x.length];
		for(int i=0;i<y.length;i++)
			for(int j=0;j<x.length;j++){
				grid[0][i][j]=x[j];
				grid[1][i][j]=y[i];}
		return grid;
			}
	
	public static void show(double[][] A){
		for(int i=0;i<A.length;i++){
			for(int j=0;j<A[0].length;j++)
				System.out.format("%12.4f",A[i][j]);
			System.out.println();
	}
		System.out.println();
	}
	public static void show(double[] v){
		for(int i=0;i<v.length;i++)
				System.out.format("%12.4f\n",v[i]);
			System.out.println();
	}
	
	public static void hshow(double[] v){
		for(int i=0;i<v.length;i++)
				System.out.format("%12.4f",v[i]);
			System.out.println();
	}
	
	public static double[] times(double[] v,double a){
		double[] y=new double[v.length];
		for(int i=0;i<v.length;i++)
				y[i]=a*v[i];
		return y;
	}
	
	public static double[][] times(double[][] M,double a){
		double[][] y=new double[M.length][M[0].length];
		for(int i=0;i<M.length;i++)
			for(int j=0;j<M[0].length;j++)
				y[i][j]=M[i][j]*a;
		return y;
	}
	
	public static void show(int[][] A){
		for(int i=0;i<A.length;i++){
			for(int j=0;j<A[0].length;j++)
				System.out.format("%d\t",A[i][j]);
			System.out.println();
	}
		System.out.println();
	}
	public static void show(int[] v){
		for(int i=0;i<v.length;i++)
				System.out.format("%d\n",v[i]);
			System.out.println();
	}
	public static void hshow(int[] v){
		for(int i=0;i<v.length;i++)
				System.out.format("%d\t",v[i]);
			System.out.println();
	}
	
	public static void show(byte[] v){
		for(int i=0;i<v.length;i++)
				System.out.format("%d\t",v[i]);
			System.out.println();
	}
	
	public static void show(boolean[] v){
		for(int i=0;i<v.length;i++)
				System.out.format("%s\t",v[i]);
			System.out.println();
	}
	
	public static void show(boolean[][] A){
		for(int i=0;i<A.length;i++){
			for(int j=0;j<A[0].length;j++)
				System.out.format("%s\t",A[i][j]);
			System.out.println();
	}
	}
	
	public static void pr(double a){
	
				System.out.println(a);
	
	}
	
	public static void pr(String a){
		
		System.out.println(a);

}
	public static void pr(int a){
		
		System.out.println(a);

}
	public static void pr(boolean b){
		
		System.out.println(b);

}

	public static void plot(Vect y){
		double[] x=new double[y.length];
		for(int i=0;i<x.length;i++)
			x[i]=i;
		plot(x,y.el);
	}
	
	public static void plot(double[] y){
		double[] x=new double[y.length];
		for(int i=0;i<x.length;i++)
			x[i]=i;
		plot(x,y);
	}
	

	public static void plot(Vect x, Vect y){
		plot(x.el,y.el);
	}
	
	public static void plot(double[] x, double[] y){
		
		plot("y=f(x)",Color.black,x,y);
	}
	
public static void plot(double[][] XY){
		
		plot("y=f(x)",Color.black,XY);
	}
	
	public static void plot(String name, Color c,double[] x, double[] y){

		 double[][] A=new double[x.length][2];
		 for(int i=0;i<x.length;i++){
			 A[i][0]=x[i];
			 A[i][1]=y[i];
			 
		 }
		
		 plot(name,c,A);
		 
		
	}
	
	public static void plot(String name, Color c,double[][] XY){
		
		 Plot2DPanel plot = new Plot2DPanel();

		 plot.addLinePlot(name, c, XY);

		  JFrame frame = new JFrame("a plot panel");
		   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  frame.setSize(500,400);
		  frame.setContentPane(plot);
		  frame.setVisible(true);
		
		
	}
	
	public static Vect Atiken(Vect v2,Vect v1, Vect v){
		Vect Av=new Vect(v.length);
		for(int i=0;i<Av.length;i++)
			Av.el[i]=(v2.el[i]*v.el[i]-v1.el[i]*v1.el[i])/(v2.el[i]-2*v1.el[i]+v.el[i]);
		
		return Av;
	}
	public  static double  Atiken(double x2,double x1, double x){
		double Ax;
		
			Ax=(x2*x-x1*x1)/(x2-2*x1+x);
		
		return Ax;
	}

	
	public static void quickSort(double[] x){
		 Sort.quick(x);
		
	}
	
	
	public static void quickSort(double[] x, int[] ind){
		 Sort.quick(x,ind);
		
	}
	public static int search(int[] A,int ic,int a){
		int m=-1;
		for(int i=0;i<ic+1;i++){
			if(A[i]==a){
				m=i;
				break;
			}
		}
		return m;
	}

	public static int search(int[] A,int a){
		int m=-1;
		for(int i=0;i<A.length;i++){
			if(A[i]==a){
				m=i;
				break;
			}
		}
		return m;
	}
	
	public static void pause(int sec){
		try {
			new Thread().sleep(sec);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
