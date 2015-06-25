package math;
import static java.lang.Math.*;

import java.util.Arrays;
public class MatSolver {
	public boolean terminate;
	int nCall=0;
	double value0;
	
	public MatSolver(){}
	
	
	public static void main(String[] args) 
	{
		
		double[][] Ar={{7,2, 1},{2,8,-2},{1,-2,6}};
		Mat A=new Mat(Ar);
		
		Vect x=new Vect(1,2,3);
		Vect b=A.mul(x);
		MatSolver ms=new MatSolver();
	    Vect y=ms.jacobi(A,b);
	    y.show();
	   
	    int N=50;
	    Mat B=new Mat(N,N);
	    B=B.rand();
	    B.ddom(1,N*N);
	    B=B.times(1.0/(N*N));
	   // B.show();
	   x=x.linspace(1,N,N);
	   b=B.mul(x);
	   y=ms.jacobi(B,b);
	    y.hshow();
	   
	    	
	}
	
	
	public Vect jacobi(Mat A, Vect b){

		double errMax=1e-8;
		Vect x=new Vect(b.length);
		x.rand();
		Vect xp= new Vect(x.length);
	
		double res=1,resRatio=1,err0=A.mul(x).sub(b).abs().max();
		double beta=1;
		int k=0;
		for( k=0;(k<1001 && resRatio>errMax);k++){
			xp=x.deepCopy();
			for(int i=0;i<x.length;i++){
				double s=0;
				for(int j=0;j<x.length;j++)
					if(i!=j) s+=A.el[i][j]*xp.el[j];
				
				x.el[i]=(b.el[i]-s)/A.el[i][i];
			}

			res=A.mul(x).sub(b).abs().max()/err0;
			resRatio=res;

			if(k%20==0)
			System.out.println("k= "+k+"  residual= "+res);	
		}
		
		System.out.println("k= "+k+"  residual= "+res);	
		return x;
		
		
	}
	
	public Vect gaussit(Mat A, Vect b){

		Vect x=new Vect(b.length);
		x.rand();
		Vect temp=new Vect(b.length);
		Vect bit=temp;
		Mat Ls=A.lowerst();
		Mat U=A.upper();
		Vect Diff=new Vect();
		double res=1;
		double err=1e-6;
		int k=0;
		while(res>err){
			k++;
			if(k>1000) break;
			
		bit=b.sub(U.mul(x));
		temp=x;
		x=solvelow(Ls,bit);
		Diff=x.sub(temp);
		res=Diff.norm()/x.norm();
		}
		
		System.out.println("k= "+k+"  residual= "+res);	
		return x;
		
	}

	
	public Mat gaussel(Mat A, Mat B){
		int[] dim=A.size();
		if(dim[0]!=dim[1]) throw new IllegalArgumentException("Matrix is not square");
		int I=dim[0];
		Mat AB=A.aug(B);
		AB.low0(A.nCol);
		Mat X=new Mat(B.size());
		X=backSubMat(AB);
		return X;
		
	}
	
	
	public Vect gaussel(Mat A, Vect b){
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
	
	public Mat backSubMat(Mat AB){
		int I=AB.nRow;
		Mat X=new Mat(I,AB.nCol-I);
		for(int k=0;k<X.nCol;k++)
		X.el[I-1][k]=AB.el[I-1][I+k]/AB.el[I-1][I-1];
	
		for(int i=I-2;i>=0;i--){
			if(AB.el[i][i]==0) continue; /*X.setCol(new Vect(X.nRow),i);*/
			for(int k=0;k<X.nCol;k++){
			double s=0;
			for(int j=i+1;j<I;j++)
			s=s+AB.el[i][j]*X.el[j][k];
			X.el[i][k]=(AB.el[i][I+k]-s)/AB.el[i][i];
			}
		}
	
		return X;
			}
	
	public Mat forwardSub(Mat AB){
		int I=AB.nRow;
		Mat X=new Mat(I,AB.nCol-I);
		for(int k=0;k<X.nCol;k++)
		X.el[0][k]=AB.el[0][I+k]/AB.el[0][0];
	
		for(int i=1;i<I;i++){
			for(int k=0;k<X.nCol;k++){
			double s=0;
			for(int j=0;j<i;j++)
			s=s+AB.el[i][j]*X.el[j][k];
			X.el[i][k]=(AB.el[i][I+k]-s)/AB.el[i][i];
			}
		}
	
		return X;
			}
	
	public Vect solveup(Mat Ab){
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
	
	
	public Mat solvelow(Mat Ab){
		int I=Ab.nRow;
		int J=Ab.nCol;
		if(I!=J-1) throw new IllegalArgumentException("Matrix is not square");
		Mat x=new Mat(I,1);
		x.el[0][0]=Ab.el[0][J-1]/Ab.el[0][0];
	
		for(int i=1;i<I;i++){
			double s=0;
			for(int j=0;j<i;j++)
			s=s+Ab.el[i][j]*x.el[j][0];
			x.el[i][0]=(Ab.el[i][J-1]-s)/Ab.el[i][i];
		}
	
		return x;
	}
	
	
	
	
	public Mat solvelow(Mat A, Mat b){
		int I=A.nRow;
		int J=A.nCol;
		if(I!=J) throw new IllegalArgumentException("Matrix is not square");
		Mat x=new Mat(I,1);
		x.el[0][0]=b.el[0][0]/A.el[0][0];
	
		for(int i=1;i<I;i++){
			double s=0;
			for(int j=0;j<i;j++)
			s=s+A.el[i][j]*x.el[j][0];
			x.el[i][0]=(b.el[i][0]-s)/A.el[i][i];
		}
	
		return x;
	}
	
	public Vect solvelow(Mat A, Vect b){
		int I=A.nRow;
		int J=A.nCol;
		if(I!=J) throw new IllegalArgumentException("Matrix is not square");
		Vect x=new Vect(I);
		x.el[0]=b.el[0]/A.el[0][0];
	
		for(int i=1;i<I;i++){
			double s=0;
			for(int j=0;j<i;j++)
			s=s+A.el[i][j]*x.el[j];
			x.el[i]=(b.el[i]-s)/A.el[i][i];
		}
	
		return x;
	}
	public Mat solvelowband(Mat Ab,int b){
		int I=Ab.nRow;
		int J=Ab.nCol;
		if(I!=J-1) throw new IllegalArgumentException("Matrix is not square");
		Mat x=new Mat(I,1);
		x.el[0][0]=Ab.el[0][J-1]/Ab.el[0][0];
	
		for(int i=1;i<I;i++){
			double s=0;
			for(int j=max(0,i-b);j<i;j++)
			s=s+Ab.el[i][j]*x.el[j][0];
			x.el[i][0]=(Ab.el[i][J-1]-s)/Ab.el[i][i];
		}
	
		return x;
	}
	
	
	
	public Mat solveupband(Mat Ab,int band){
		int I=Ab.nRow;
		int J=Ab.nCol;
		if(I!=J-1) throw new IllegalArgumentException("Matrix is not square");
		Mat x=new Mat(I,1);
		x.el[I-1][0]=Ab.el[I-1][J-1]/Ab.el[I-1][I-1];
	
		for(int i=I-2;i>=0;i--){
			double s=0;
			for(int j=i+1;j<min(I,j+band);j++)
			s=s+Ab.el[i][j]*x.el[j][0];
			x.el[i][0]=(Ab.el[i][J-1]-s)/Ab.el[i][i];
		}
	
		return x;
		
	}

	
	
	public Vect steepGrad(Mat A,Vect b, double erc,int N){
		int I=A.nRow;
		int J=A.nCol;
		if(I!=J) throw new IllegalArgumentException("Matrix is not square");
		Vect x=new Vect(b.length);
		x.rand();

		Vect r=new Vect(I);
		r=b.sub(A.mul(x));

		double alpha;
		int k=0;
		while (r.norm()>erc){
			k++;
			if(k%10==0) System.out.println("k= "+k+" res="+r.norm());
			if(k>N)  {
				System.out.println("k= "+k+" res="+r.norm());
				break;
			}
			alpha=r.dot(r)/(r.dot(A.mul(r)));
			x=x.add(r.times(alpha));
			r=r.sub(A.mul(r).times(alpha));
		}
		System.out.println("k= "+k+" res="+r.norm());
		return x;
	}
	
	
	
	public Vect CG(Mat A,Vect b,double erc,int N){
		int I=A.nRow;
		int J=b.length;
		if(I!=J) throw new IllegalArgumentException("Matrix is not square");

		Vect x=new Vect(b.length);
		x.rand();
		
		Vect r=new Vect(I);
		r=b.sub(A.mul(x));
		Vect p=r;
		
		
		double alpha;
		int k=0;
		double c,temp;
		while (r.norm()>erc){
			k++;
			if(k%100==0)System.out.println("k= "+k+" res="+r.norm());
			if(k>N)  {
				System.out.println("k= "+k+" res="+r.norm());
				break;
			}
			alpha=r.dot(r)/(p.dot(A.mul(p)));
			x=x.add(p.times(alpha));
			temp=r.dot(r);
			r=r.sub(A.mul(p).times(alpha));
			c=r.dot(r)/temp;
			p=r.add(p.times(c));
		}
		System.out.println("k= "+k+" res="+r.norm());
		return x;
	}

	public Vect ICCG(Mat A,Vect b,double erc,int N){
		int I=A.nRow;
		int J=b.length;
		if(I!=J) throw new IllegalArgumentException("Matrix is not square");
		Vect x=new Vect();
		Mat Ci=A.diag();
		for(int i=0;i<A.nRow;i++)
			Ci.el[i][i]=1.0/sqrt(Ci.el[i][i]);
		A=Ci.mul(A).mul(Ci);
		b=Ci.mul(b);
	
		Vect r=new Vect(I);
		r=b.sub(A.mul(x));
		Vect p=r;
		
		double alpha;
		int k=0;
		double c,temp;
		while (r.norm()>erc){
			k++;
			if(k%100==0)System.out.println("k= "+k+" res="+r.norm());
			if(k>N)  {
				System.out.println("k= "+k+" res="+r.norm());
				break;
			}
			alpha=r.dot(r)/(p.dot(A.mul(p)));
			x=x.add(p.times(alpha));
			temp=r.dot(r);
			r=r.sub(A.mul(p).times(alpha));
			c=r.dot(r)/temp;
			p=r.add(p.times(c));
		}
		
	x=Ci.mul(x);
		System.out.println("k= "+k+" res="+r.norm());
		return x;
	}
	
	

	
}
