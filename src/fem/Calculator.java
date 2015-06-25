package fem;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import math.Mat;
import math.Vect;
import math.util;

public class Calculator {
	public int dim,elCode,nElVert,nElEdge,numberOfElements,numberOfNodes,numberOfRegions;
	public double dt,rdt;
	public Calculator()
	{

	}

	public Calculator(Model model)
	{
		this.nElVert=model.nElVert;
		this.nElEdge=model.nElEdge;
		this.numberOfElements=model.numberOfElements;
		this.numberOfNodes=model.numberOfNodes;
		this.numberOfRegions=model.numberOfRegions;
		this.dim=model.dim;
		this.elCode=model.elCode;
	}


	public Vect getElementB(Model model,int i, Vect[] rotNe){

		Edge[] elEdge=model.elementEdges(i);
		Vect B=new Vect(model.dim);
		for(int j=0;j<model.nElEdge;j++)		{
			B=B.add(rotNe[j].times(elEdge[j].Au));
		}

		return B;

	}

	public double getElementAQ(Model model,int i){
		Edge[] elEdge=model.elementEdges(i);
		double[] Ae=new double[4];
		for(int j=0;j<4;j++)
			Ae[j]=elEdge[j].Au;
		double A=0;
		Vect zero=new Vect(2);

		double[] Ne=NeQuad(zero);

		for(int j=0;j<model.nElEdge;j++)	{		
			A= A+Ne[j]*Ae[j];
		}
		return  A;	

	}




	public Vect[] gradN(Mat jac,Vect localCo){

		Mat invJac=jac.inv();
		Vect[] gradN=new Vect[this.nElVert];
		Vect[] localGradN=localGradN(localCo);

		for(int i=0;i<this.nElVert;i++)
			gradN[i]=invJac.mul(localGradN[i]);

		return gradN;
	}



	public  Vect[] rotNe(Mat jac, Vect localCo){

		if(this.elCode==1) return rotNeQuad(jac,localCo);

		Vect[] rotNe=new Vect[this.nElEdge];

		double a=localCo.el[0];
		double b=localCo.el[1];
		double c=localCo.el[2];

		Mat invJac=jac.inv3();
		Vect[] grad=new Vect[3];

		for(int j=0;j<3;j++)
			grad[j]=invJac.getColVect(j);

		rotNe[0]= grad[1].times(-(1-c)).add(grad[2].times(-(1-b))).times(0.125).cross(grad[0]); 
		rotNe[1]= grad[1].times((1-c)).add(grad[2].times(-(1+b))).times(0.125).cross(grad[0]);
		rotNe[2]= grad[1].times(-(1+c)).add(grad[2].times(+(1-b))).times(0.125).cross(grad[0]);
		rotNe[3]= grad[1].times((1+c)).add(grad[2].times(+(1+b))).times(0.125).cross(grad[0]);
		rotNe[4]= grad[0].times(-(1-c)).add(grad[2].times(-(1-a))).times(0.125).cross(grad[1]);
		rotNe[5]= grad[0].times((1-c)).add(grad[2].times(-(1+a))).times(0.125).cross(grad[1]);
		rotNe[6]= grad[0].times(-(1+c)).add(grad[2].times(+(1-a))).times(0.125).cross(grad[1]);
		rotNe[7]= grad[0].times((1+c)).add(grad[2].times(+(1+a))).times(0.125).cross(grad[1]);
		rotNe[8]= grad[0].times(-(1-b)).add(grad[1].times(-(1-a))).times(0.125).cross(grad[2]);
		rotNe[9]= grad[0].times((1-b)).add(grad[1].times(-(1+a))).times(0.125).cross(grad[2]);
		rotNe[10]= grad[0].times(-(1+b)).add(grad[1].times(+(1-a))).times(0.125).cross(grad[2]);
		rotNe[11]= grad[0].times((1+b)).add(grad[1].times(+(1+a))).times(0.125).cross(grad[2]);


		return rotNe;
	}


	public double[] NQuad(Vect localCo){
		double a,b;
		a=localCo.el[0];b=localCo.el[1];;
		double[] N=new double[this.nElVert];
		N[0]=(1+a)*(1+b)*0.25;
		N[1]=(1-a)*(1+b)*0.25;
		N[2]=(1-a)*(1-b)*0.25;
		N[3]=(1+a)*(1-b)*0.25;

		return N;
	}
	public Vect[] rotNeQuad(Mat jac, Vect localCo){


		double a=localCo.el[0];
		double b=localCo.el[1];

		Vect[] rotNe=new Vect[this.nElVert];

		Mat invJac=jac.inv();

		Vect[] grad=new Vect[2];
		grad[0]=invJac.getColVect(0);
		grad[1]=invJac.getColVect(1);

		Vect v;
		v=grad[0].times(1+b).add(grad[1].times(1+a)).times(.25);
		rotNe[0]= new Vect(v.el[1],-v.el[0]);
		v=grad[0].times(-(1+b)).add(grad[1].times(1-a)).times(.25);
		rotNe[1]= new Vect(v.el[1],-v.el[0]);
		v=grad[0].times(b-1).add(grad[1].times(a-1)).times(.25);
		rotNe[2]= new Vect(v.el[1],-v.el[0]);
		v=grad[0].times(1-b).add(grad[1].times(-(1+a))).times(.25);
		rotNe[3]= new Vect(v.el[1],-v.el[0]);

		return rotNe;
	}

	public double[] NeQuad(Vect localCo){

		double a=localCo.el[0];
		double b=localCo.el[1];

		double[] Ne=new double[this.nElVert];

		Ne[0]= (1+a)*(1+b)*0.25; 
		Ne[1]= (1-a)*(1+b)*0.25; 
		Ne[2]= (1-a)*(1-b)*0.25; 
		Ne[3]= (1+a)*(1-b)*0.25; 


		return Ne;
	}


	Vect[] localGradN(Vect localCo){
		if(this.elCode==0) return localGradN3ang();
		else if(this.elCode==1) return localGradQuad(localCo);
		double a=localCo.el[0];
		double b=localCo.el[1];
		double c=localCo.el[2];

		Vect[] gradN=new Vect[this.nElVert];

		gradN[0]=new Vect((1+b)*(1+c),(1+a)*(1+c),(1+a)*(1+b));
		gradN[1]=new Vect(-(1+b)*(1+c),(1-a)*(1+c),(1-a)*(1+b));
		gradN[2]=new Vect(-(1-b)*(1+c),-(1-a)*(1+c),(1-a)*(1-b));
		gradN[3]=new Vect((1-b)*(1+c),-(1+a)*(1+c),(1+a)*(1-b));
		gradN[4]=new Vect((1+b)*(1-c),(1+a)*(1-c),-(1+a)*(1+b));
		gradN[5]=new Vect(-(1+b)*(1-c),(1-a)*(1-c),-(1-a)*(1+b));
		gradN[6]=new Vect(-(1-b)*(1-c),-(1-a)*(1-c),-(1-a)*(1-b));
		gradN[7]=new Vect((1-b)*(1-c),-(1+a)*(1-c),-(1+a)*(1-b));


		for(int i=0;i<this.nElVert;i++)
			gradN[i].timesVoid(0.125);
		return gradN;
	}

	public Vect[] localGradN3ang(){


		Vect[] gradN=new Vect[3];

		gradN[0]=new Vect(1,0,0);
		gradN[1]=new Vect(0,1,0);
		gradN[2]=new Vect(0,0,1);

		return gradN;
	}


	public Vect[] localGradQuad(Vect localCo){
		double a=localCo.el[0];
		double b=localCo.el[1];

		Vect[] gradN=new Vect[this.nElVert];

		gradN[0]=new Vect((1+b),(1+a));
		gradN[1]=new Vect(-(1+b),(1-a));
		gradN[2]=new Vect(-(1-b),-(1-a));
		gradN[3]=new Vect((1-b),-(1+a));


		for(int i=0;i<this.nElVert;i++)
			gradN[i].timesVoid(0.25);
		return gradN;
	}

	Vect[] gradN3ang(Model model,int ie){
		Node[] vertexNode=model.elementNodes(ie);

		Vect v1=vertexNode[0].getCoord();
		Vect v2=vertexNode[1].getCoord();
		Vect v3=vertexNode[2].getCoord();

		double rS=.5/el3angArea(model,ie);

		Vect[] gradN=new Vect[3];

		gradN[0]=new Vect(v2.el[1]-v3.el[1],v3.el[0]-v2.el[0]).times(rS);
		gradN[1]=new Vect(v3.el[1]-v1.el[1],v1.el[0]-v3.el[0]).times(rS);
		gradN[2]=new Vect(v1.el[1]-v2.el[1],v2.el[0]-v1.el[0]).times(rS);

		return gradN;
	}

	public double getElementArea(Model model,int i){
		if(this.elCode==0) return el3angArea(model,i);
		else 	if(this.elCode==1) return elQuadArea(model,i);
		else throw new NullPointerException(" Element is not 2D. ");

	}

	public double el3angArea(Model model,int i){
		Node[] vertexNode=model.elementNodes(i);

		Vect v1=vertexNode[1].getCoord().sub(vertexNode[0].getCoord());
		Vect v2=vertexNode[2].getCoord().sub(vertexNode[0].getCoord());
		double S=abs(v1.cross(v2).norm())/2;
		return S;
	}

	public double elQuadArea(Model model,int i){
		Node[] vertexNode=model.elementNodes(i);

		Vect v1=vertexNode[1].getCoord().sub(vertexNode[0].getCoord());
		Vect v2=vertexNode[3].getCoord().sub(vertexNode[0].getCoord());
		Vect v3=vertexNode[1].getCoord().sub(vertexNode[2].getCoord());
		Vect v4=vertexNode[3].getCoord().sub(vertexNode[2].getCoord());
		double S=(v1.cross(v2).norm()+v4.cross(v3).norm())/2;
		return S;
	}

	double[] N(Vect localCo){
		if(elCode==0) return N3ang1st(localCo);
		else if(elCode==1) return NQuad(localCo);
		double a,b,c;
		a=localCo.el[0];b=localCo.el[1];c=localCo.el[2];
		double[] N=new double[this.nElVert];
		N[0]=(1+a)*(1+b)*(1+c)*0.125;
		N[1]=(1-a)*(1+b)*(1+c)*0.125;
		N[2]=(1-a)*(1-b)*(1+c)*0.125;
		N[3]=(1+a)*(1-b)*(1+c)*0.125;
		N[4]=(1+a)*(1+b)*(1-c)*0.125;
		N[5]=(1-a)*(1+b)*(1-c)*0.125;
		N[6]=(1-a)*(1-b)*(1-c)*0.125;
		N[7]=(1+a)*(1-b)*(1-c)*0.125;

		return N;
	}

	public  Vect[] Ne(Mat jac,Vect localCo){
		double a=localCo.el[0];
		double b=localCo.el[1];
		double c=localCo.el[2];
		Vect[] Ne=new Vect[this.nElEdge];
		Mat invJac=jac.inv3();

		Vect[] grad=new Vect[3];

		for(int j=0;j<3;j++)
			grad[j]=invJac.getColVect(j);

		Ne[0]= grad[0].times((1-b)*(1-c)*0.125); 
		Ne[1]= grad[0].times((1+b)*(1-c)*0.125); 
		Ne[2]= grad[0].times((1-b)*(1+c)*0.125); 
		Ne[3]= grad[0].times((1+b)*(1+c)*0.125); 
		Ne[4]= grad[1].times((1-a)*(1-c)*0.125); 
		Ne[5]= grad[1].times((1+a)*(1-c)*0.125); 
		Ne[6]= grad[1].times((1-a)*(1+c)*0.125); 
		Ne[7]= grad[1].times((1+a)*(1+c)*0.125); 
		Ne[8]= grad[2].times((1-a)*(1-b)*0.125); 
		Ne[9]= grad[2].times((1+a)*(1-b)*0.125); 
		Ne[10]= grad[2].times((1-a)*(1+b)*0.125); 
		Ne[11]= grad[2].times((1+a)*(1+b)*0.125); 


		return Ne;

	}

	//============

	public double[] N3ang(Node[] vertexNode, Vect globalCo){

		Vect v1=vertexNode[0].getCoord().sub(globalCo);
		Vect v2=vertexNode[1].getCoord().sub(globalCo);
		Vect v3=vertexNode[2].getCoord().sub(globalCo);

		double[] N=new double[3];

		N[0]=v2.cross(v3).norm();
		N[1]=v3.cross(v1).norm();
		N[2]=v1.cross(v2).norm();

		double S=N[0]+N[1]+N[2];

		for(int i=0;i<3;i++)
			N[i]/=S;

		return N;


	}

	public double[] N3ang(Model model,int ie, Vect globalCo){
		Node[] vertexNode=model.elementNodes(ie);

		return N3ang(vertexNode,globalCo);

	}

	public double[] N3ang(Model model,int ie){
		Node[] vertexNode=model.elementNodes(ie);
		Vect globalCo=vertexNode[0].getCoord().add(vertexNode[1].getCoord()).add(vertexNode[2].getCoord()).times(1.0/3);

		return N3ang(vertexNode,globalCo);


	}

	public double[] N3ang(Node[] vertexNode){

		Vect globalCo=vertexNode[0].getCoord().add(vertexNode[1].getCoord()).add(vertexNode[2].getCoord()).times(1.0/3);
		return N3ang(vertexNode,globalCo);


	}


	public double[] Ne3ang(Node[] vertexNode){
		return N3ang(vertexNode);

	}

	double[] Ne3ang(Node[] vertexNode, Vect globalCo){

		return N3ang(vertexNode, globalCo);
	}
	
	double[] N3ang1st(Vect localCo){
		double[] N=new double[3];
		N[0]=localCo.el[0];
		N[1]=localCo.el[1];
		N[2]=1-N[0]-N[1];
		return N;
	}

	public double[] Ne3ang1st(Vect localCo){
		return N3ang1st(localCo);
	}
	

	double[] Ne3ang(Model model,int ie, Vect globalCo){
		return Ne3ang(model.elementNodes(ie), globalCo);
	}


	public Vect[] rotNe3ang(Node[] vertexNode){

		Vect v1=vertexNode[0].getCoord();
		Vect v2=vertexNode[1].getCoord();
		Vect v3=vertexNode[2].getCoord();

		double rS=1.0/v2.sub(v1).cross(v3.sub(v1)).norm();

		Vect[] rotNe=new Vect[3];

		rotNe[0]=new Vect(v3.el[0]-v2.el[0], v3.el[1]-v2.el[1]).times(rS);
		rotNe[1]=new Vect(v1.el[0]-v3.el[0], v1.el[1]-v3.el[1]).times(rS);
		rotNe[2]=new Vect(v2.el[0]-v1.el[0],v2.el[1]-v1.el[1]).times(rS);

		return rotNe;
	}

	private Vect[] rotNe3ang1st(Mat jac,int ie){


		Vect[] rotNe=new Vect[this.nElEdge];


		double Jr=1.0/(jac.determinant());
		Mat P=new Mat(3,2);
		P.el[0][0]=jac.el[2][1]-jac.el[2][2];
		P.el[0][1]=jac.el[1][2]-jac.el[1][1];
		P.el[1][0]=jac.el[2][2]-jac.el[2][0];
		P.el[1][1]=jac.el[1][0]-jac.el[1][2];
		P.el[2][0]=jac.el[2][0]-jac.el[2][1];
		P.el[2][1]=jac.el[1][1]-jac.el[1][0];
		
		P=P.times(Jr);
	
		Vect[] grad=new Vect[3];
		
		for(int i=0;i<3;i++){
			grad[i]=P.rowVect(i);
			
			rotNe[i]=new Vect(grad[i].el[1], -grad[i].el[0]);
			
		}

		return rotNe;


	}

	private Vect[] rotNe3ang2nd(Mat jac, Vect lc){

		double[] tu=lc.el;
		
		Vect[] rotNe=new Vect[6];


		double Jr=1.0/(jac.determinant());
		Mat P=new Mat(3,2);
		P.el[0][0]=jac.el[2][1]-jac.el[2][2];
		P.el[0][1]=jac.el[1][2]-jac.el[1][1];
		P.el[1][0]=jac.el[2][2]-jac.el[2][0];
		P.el[1][1]=jac.el[1][0]-jac.el[1][2];
		P.el[2][0]=jac.el[2][0]-jac.el[2][1];
		P.el[2][1]=jac.el[1][1]-jac.el[1][0];
		
		P=P.times(Jr);
		

		Vect[] grad=new Vect[3];
		
		for(int i=0;i<3;i++){
			grad[i]=P.rowVect(i).times(4*tu[i]-1);
		}
		
		grad[3]=P.rowVect(0).times(4*tu[1]).add(P.rowVect(1).times(4*tu[0]));
		grad[4]=P.rowVect(1).times(4*tu[2]).add(P.rowVect(2).times(4*tu[1]));
		grad[5]=P.rowVect(0).times(4*tu[2]).add(P.rowVect(2).times(4*tu[0]));
				
		

		for(int i=0;i<3;i++){
			rotNe[i]=new Vect(grad[i].el[1], -grad[i].el[0]);

		}


		return rotNe;


	}
	

	private Vect[] rotNe3ang(Model model,int ie){
		Node[] vertexNode=model.elementNodes(ie);
		return rotNe3ang(vertexNode);

	}


	public double[] N3ang2nd(Vect localCo){

		double[] N=new double[6];
		double[] tu=localCo.el;
		
		for(int i=0;i<3;i++)
			N[i]=tu[i]*(2*tu[i]-1);

		N[3]=4*tu[0]*tu[1];
		N[4]=4*tu[1]*tu[2];
		N[5]=4*tu[0]*tu[2];

		return N;
	}
	
	public double[] Ne3ang2nd(Vect localCo){
		return N3ang2nd(localCo);
	}
	
	public Vect[] rotNe3ang2nd(Node[] vertexNode,Vect globalCo){

		Vect v1=vertexNode[0].getCoord().sub(globalCo);
		Vect v2=vertexNode[1].getCoord().sub(globalCo);
		Vect v3=vertexNode[2].getCoord().sub(globalCo);

		double[] N=new double[6];

		double[] su=new double [3];
		su[0]=v2.cross(v3).norm();
		su[1]=v3.cross(v1).norm();
		su[2]=v1.cross(v2).norm();

		double S=su[0]+su[1]+su[2];
		double rS=1.0/S;
		double[] tu=new double [3];
		for(int i=0;i<3;i++)
			 tu[i]=su[i]*rS;

		Vect[] rotNe=new Vect[6];

		Vect[] rn=new Vect[3];
		rn[0]=new Vect(v3.el[0]-v2.el[0], v3.el[1]-v2.el[1]).times(rS);
		rn[1]=new Vect(v1.el[0]-v3.el[0], v1.el[1]-v3.el[1]).times(rS);
		rn[2]=new Vect(v2.el[0]-v1.el[0],v2.el[1]-v1.el[1]).times(rS);
		rotNe[0]=rn[0].times(4*tu[0]-1);
		rotNe[1]=rn[1].times(4*tu[1]-1);
		rotNe[2]=rn[2].times(4*tu[2]-1);
		rotNe[3]=rn[0].times(4*tu[1]).add(rn[1].times(4*tu[0]));
		rotNe[4]=rn[1].times(4*tu[2]).add(rn[2].times(4*tu[1]));
		rotNe[2]=rn[2].times(4*tu[0]).add(rn[0].times(4*tu[2]));


		return rotNe;
	}


	public Mat jacobian(Vect localCo,Node[] vertexNode){
		if(this.elCode==0) return jacobian3ang(vertexNode,localCo);
		Mat J=new Mat(this.dim,this.dim);
		Vect[] gN=new Vect[this.nElVert];

		gN=localGradN(localCo);

		for(int i=0;i<this.nElVert;i++) {
			for(int j=0;j<this.dim;j++)
				for(int k=0;k<this.dim;k++)
					J.el[j][k]+=gN[i].el[j]* vertexNode[i].getCoord(k);
		}

		return J;
	}


	public Mat jacobian3ang(Node[] vertexNode,Vect localCo){

		Mat J=new Mat(3,3);
		Vect[] gN=new Vect[this.nElVert];

		gN=localGradN3ang();

		for(int i=0;i<this.nElVert;i++) 
			J.el[0][i]=1;

		for(int i=0;i<this.nElVert;i++) {
			for(int j=1;j<3;j++)
				for(int k=0;k<3;k++)
					J.el[j][k]+=gN[i].el[k]* vertexNode[i].getCoord(j-1);
		}


		return J;
	}


	public Vect gradPhi(Node[] vertexNode,double[] nodePhi){

		Vect gradPhi=new Vect(3);
		Vect zero=new Vect(3);
		Vect[] gradN=new Vect[this.nElVert];
		Mat G=jacobian(zero,vertexNode).inv3();
		Vect[] localGradN=localGradN(zero);
		for(int i=0;i<this.nElVert;i++){
			gradN[i]=G.mul(localGradN[i]);
			gradPhi= gradPhi.add(gradN[i].times(nodePhi[i]));
		}


		return  gradPhi;	

	}


	public Vect getElementCenter(Model model,int ie){


		Vect center=new Vect(this.dim);
		int[] vertNumb=model.element[ie].getVertNumb();
		for(int j=0;j<this.nElVert;j++)
			center=center.add(model.node[vertNumb[j]].getCoord());


		return center.times(1.0/this.nElVert);
	}




	public Vect globalCo(Node[] vertex,double[]  localN){
		Vect v=new Vect(this.dim);
		for(int i=0;i<this.nElVert;i++) 
			v=v.add( vertex[i].getCoord().times(localN[i]));
		return v;
	}

	public  Vect localCo(Model model,int[] m,Vect globalCo){
		Vect lc=new Vect(3);
		Vect dlc=new Vect(3);
		Vect gc=new Vect(3);
		double[] localN;

		Node[] vertex=model.elementNodes(m[0]);
		double resNorm=1;
		double error=1e-6;

		if(m[1]==0) lc.el[0]=-1;
		if(m[2]==0) lc.el[0]=1;
		if(m[3]==0) lc.el[1]=-1;
		if(m[4]==0) lc.el[1]=1;
		if(m[5]==0) lc.el[2]=-1;
		if(m[6]==0) lc.el[2]=1;

		boolean[] known=new boolean[3];
		for(int j=0;j<3;j++)
			if(lc.el[j]!=0) known[j]=true;

		for(int i=0;(i<1 && resNorm>error);i++){
			localN=N(lc);
			gc=globalCo(vertex,localN);

			Vect res=globalCo.sub(gc);

			resNorm=res.norm();
			for(int j=0;j<3;j++)
				if(!known[j])
					lc.el[j]+=dlc.el[j];
		}

		return lc;
	}

	private Vect localCoQuad(Model model,int ie,Vect globalCo){
		Vect lc=new Vect(2);
		Vect dlc=new Vect(2);
		Vect gc=new Vect(2);
		double[] localN;

		Node[] vertex=model.elementNodes(ie);
		double resNorm=1;
		double error=1e-12*model.scaleFactor;

		Vect[] v=new Vect[4];


		int[] vertNumb=model.element[ie].getVertNumb();
		for(int j=0;j<4;j++)
			v[j]=model.node[vertNumb[j]].getCoord();


		if(v[0].sub(globalCo).cross(v[1].sub(globalCo)).norm()<error) lc.el[1]=1;
		if(v[1].sub(globalCo).cross(v[2].sub(globalCo)).norm()<error) lc.el[0]=-1;
		if(v[2].sub(globalCo).cross(v[3].sub(globalCo)).norm()<error) lc.el[1]=-1;
		if(v[3].sub(globalCo).cross(v[0].sub(globalCo)).norm()<error) lc.el[0]=1;

		boolean[] known=new boolean[2];
		for(int j=0;j<2;j++)
			if(lc.el[j]!=0) known[j]=true;

		for(int i=0;(i<1 && resNorm>error);i++){
			localN=NQuad(lc);
			gc=globalCo(vertex,localN);

			Vect res=globalCo.sub(gc);

			resNorm=res.norm();
			for(int j=0;j<2;j++)
				if(!known[j])
					lc.el[j]+=dlc.el[j];
		}

		return lc;
	}

	public Vect getBAt(Model model,Vect globalCo){
		Vect na=new Vect(this.dim);
		na.el[0]=1e10;
		na.el[1]=-1e10;
		int[] m=model.getContainingElement(globalCo);
		if(m[0]<=0) return na;
		if(this.elCode==0) return model.element[m[0]].getB();
		Vect lc;
		if(this.dim==3){
			lc=localCo(model,m,globalCo);
		}
		else{
			lc=localCoQuad(model,m[0],globalCo);

		}
		return getBAt(model, m[0], lc);

	}

	public Vect getBAt(Model model,int i,Vect lc){
		if(model.fluxLoaded || model.elCode==0) return model.element[i].getB();

		Vect B=new Vect(this.dim);
		Mat jac=new Mat(this.dim,this.dim);
		Node[] vertex=model.elementNodes(i);
		Edge[] elEdges=model.elementEdges(i);
		Vect[] rotNe=new Vect[this.nElEdge];
		if(this.dim==3){
			jac=jacobian(lc,vertex);
			rotNe=rotNe(jac,lc);
		}
		else if(this.elCode==1){
			jac=jacobian(lc,vertex);

			rotNe=rotNeQuad(jac,lc);
		}

		for(int j=0;j<this.nElEdge;j++)	{	
			B= B.add(rotNe[j].times(elEdges[j].Au));
		}

		return B;
	}

}
