package fem;

import java.util.Arrays;

import ReadWrite.Loader;
import ReadWrite.Writer;
import materialData.MaterialData;
import math.IntVect;
import math.Mat;
import math.Vect;
import math.util;
import static java.lang.Math.*;

public class Model{

	public int dim=3;
	public int numberOfRegions, numberOfNodes,numberOfElements,numberOfSurfs,numberOfEdges,nXYedges,
	nBlocks,nBH,nLam;
	public int nodeNodeConnectionMax=27,nElVert=8,nBoundary=6;
	public int nElEdge=12,nElSurf=6;
	public double[] spaceBoundary;
	public String[] blockMaterial;
	public Region[] region;
	public Node[] node;
	public Element[] element;
	public Edge[] edge;
	public Edge[] edgeXY;
	private Calculator femCalc=new Calculator();
	public Loader loader=new Loader();
	public Writer writer=new Writer();
	public double scaleFactor,Bmax1=0,Bmin1=0,Bmax,Bmin,stressMax=0,nodalScalarMax=0,
	stressMin,nodalScalarMin=0,TempMin, TempMax,Jmin,Jmax,Jemin,Jemax,maxDim,minEdgeLength,maxEdgeLength,
	FmsMin,FmsMax=0,FedMin,FedMax,FreluctMax=0,FreluctMin=0,uMax=0,AuMax,nodalValueMax,defScale;
	public int numberOfUnknownEdges,numberOfKnownEdges,numberOfVarNodes
	,numberOfKnownPhis,numberOfUnknowns,analysisMode,stressViewCode,nodalStressMaxCode
	,numberOfUnknownU,numberOfUnknownUcomp,numberOfUnknownA,numberOfUnknownAcomp,defMode, nRotReg;
	public boolean deform,hasJ,hasM,forceLoaded,fluxLoaded,potentialLoaded,
	stressLoaded,forceCalcLoaded,motor,fullMotor,batchAnim;
	int[][] facetVert={{6,2,5},{7,4,3},{6,7,2},{0,4,1},{4,7,5},{0,1,3}};
	public byte elCode=4;
	public double nu0=1e7/(4*Math.PI);
	public String elType="hexahedron";

	public int animDataCode,animChonenNodeComp,nAnimSteps,
	animMode,nBegin,nEnd,nInc,animTD,animChosenNode,animChonenNodeCom;
	public int[] animRegs;
	public double dt,alpha1,alpha2,r1,r2,rm,TrqZ,height=.06,cpb=1,rotStep;
	//public String[] animDataFile;
	public String eddyFolder,filePath,meshFilePath,dataFilePath,fileCommon,fileExtension,
	fluxFilePath,eddyFilePath,fluxFolder,animDataFolder,chosenNodeCoord;

	public Model(){}

	public Model(String bun){
		new Model();
		loadMesh(bun);
	}


	public Model(int nRegions, int nElements,int nNodes, String elType){

		this.numberOfRegions=nRegions;
		this.numberOfElements=nElements;

		this.numberOfNodes=nNodes;

		this.setElType(elType);


		region=new Region[this.numberOfRegions+1];
		for(int i=1;i<=this.numberOfRegions;i++)
			region[i]=new Region(dim);

		element=new Element[this.numberOfElements+1];
		for(int i=1;i<=this.numberOfElements;i++)
			element[i]=new Element(elType);

		node=new Node[this.numberOfNodes+1];
		for(int i=1;i<=this.numberOfNodes;i++)
			node[i]=new Node(dim);
	}


	public void alloc(int nRegions, int nElements,int nNodes, String elType){

		this.numberOfRegions=nRegions;
		this.numberOfElements=nElements;

		this.numberOfNodes=nNodes;

		this.setElType(elType);

		region=new Region[this.numberOfRegions+1];
		for(int i=1;i<=this.numberOfRegions;i++)
			region[i]=new Region(dim);

		element=new Element[this.numberOfElements+1];
		for(int i=1;i<=this.numberOfElements;i++)
			element[i]=new Element(elType);

		node=new Node[this.numberOfNodes+1];
		for(int i=1;i<=this.numberOfNodes;i++)
			node[i]=new Node(dim);
	}
	public void loadMesh(String bunFilePath){

		loader.loadMesh(this, bunFilePath);


	}


	public Model deepCopy(){

		Model copy=new Model(this.numberOfRegions,this.numberOfElements,this.numberOfNodes,this.elType);

		for(int i=1;i<=this.numberOfRegions;i++){
			copy.region[i].setFirstEl(this.region[i].getFirstEl());
			copy.region[i].setLastEl(this.region[i].getLastEl());
		}


		for(int i=1;i<=this.numberOfElements;i++){
			copy.element[i].setVertNumb(this.element[i].getVertNumb());
			copy.element[i].setRegion(this.element[i].getRegion());
		}


		for(int i=1;i<=this.numberOfNodes;i++){
			copy.node[i].setCoord(this.node[i].getCoord());
		}
		copy.scaleFactor=this.scaleFactor;
		return copy;
	}




	public double edgeLength(int i){

		double length=node[edge[i].endNodeNumber[1]].getCoord().sub(node[edge[i].endNodeNumber[0]].getCoord()).norm();
		return length;
	}

	public void setMinEdge(){
		double minEdge=1e40;
		for(int i=1;i<=this.numberOfEdges;i++){
			if(edge[i].length<minEdge) minEdge=edge[i].length;
		}
		this.minEdgeLength=minEdge;
	}
	public void setMaxEdge(){
		double maxEdge=0;
		for(int i=1;i<=this.numberOfEdges;i++)
			if(edge[i].length>maxEdge) maxEdge=edge[i].length;

		this.maxEdgeLength=maxEdge;
	}

	public  void setMaxDim(){

		maxDim=getRmax();
	}




	public boolean loadFlux(String fluxFilePath,boolean[] nc){

		boolean b=loader.loadFlux(this,fluxFilePath,0,nc);
	
		if(b)
			this.setNodalScalar(0);
		return b;
	}
	
	public boolean loadFlux(String fluxFilePath,boolean[] nc,double deg){

		boolean b=loader.loadFlux(this,fluxFilePath,deg,nc);
		if(b)
			this.setNodalScalar(0);
		return b;
	}
	
	public boolean loadFlux(String fluxFilePath){

		boolean b=loader.loadFlux(this,fluxFilePath);
		if(b)
			this.setNodalScalar(0);
		return b;
	}
	
	public boolean loadFlux(String fluxFilePath,double angDeg){

		boolean b=loader.loadFlux(this,fluxFilePath,angDeg);
		/*if(b)
			this.setNodalScalar(0);*/
		return b;
	}


	public boolean loadPotential(String file){

		return loader.loadPotential(this,file);

	}
	public boolean loadStress(String stressFilePath){
		return loader.loadStress(this,stressFilePath);
	}

	public boolean loadTemper(String temperFilePath){
		return loader.loadTemper(this,temperFilePath);
	}

	public boolean loadNodalField(String filePath,int mode){

		return loader.loadNodalField(this,filePath,mode);


	}
	
	public boolean loadNodalField(String filePath,int mode,int n0){

		return loader.loadNodalField(this,filePath,mode,n0);


	}
	
	public boolean loadNodalField(String filePath,int mode,boolean[] nodeIn){

		return loader.loadNodalField(this,filePath,mode,nodeIn,0);


	}
	public void writeNodalField(String filePath,int mode){

		 writer.writeNodalField(this,filePath,mode);


	}
	
	public void writeNodalField(String filePath,int mode,boolean[] nc){

		 writer.writeNodalField(this,filePath,mode,nc);


	}
/*
	public boolean loadEdgeField(String edgeFilePath,int mode){
		return loader.loadNodalField(this,edgeFilePath,mode);
	}
	*/

	public Vect[][] loadBunch(String filePath,IntVect iv, int mode){

		return loader.loadBunch(this,filePath,iv,mode);


	}

	public double[][] loadBunchScalar(String filePath,IntVect iv, int mode){


		return loader.loadBunchScalar(this,filePath,iv,mode);


	}
	
	public void loadAnimationData(String filePath){

		loader.loadAnimationData(this,filePath);

	}

	public void reportData(){
		writer.reportData(this);
	}

	public Node[] elementNodes(int i){
		Node[] elementNode=new Node[nElVert];
		int[] vertNumb=element[i].getVertNumb();
		for(int j=0;j<nElVert;j++){
			elementNode[j]=node[vertNumb[j]];
		}

		return elementNode;
	}
	public Edge[] elementEdges(int i){
		Edge[] elementEdge=new Edge[nElEdge];
		int[] edgeNumb=element[i].getEdgeNumb();
		for(int j=0;j<nElEdge;j++)
			elementEdge[j]=edge[edgeNumb[j]];
		return elementEdge;
	}

	public int[] getContainingElement(Vect P){
		if(elCode==0) return getContaining3angElement(P);
		else if(elCode==1) return getContainingQuadElement(P);
		int[] elResult;
		int[] result=new int[7];
		for(int i=1;i<=numberOfElements;i++){
			elResult=elContains(i,P);
			result[0]=0;
			for(int j=0;j<6;j++)
				if(elResult[j]<0){
					result[0]=-1;
					break;
				}

			if(result[0]==-1) continue;		


			result[0]=i;
			for(int j=0;j<6;j++)
				result[j+1]=elResult[j];
			break;

		}



		return result;
	}

	public int[] getContaining3angElement(Vect P){
		int[] ne=new int[1];


		double[] S;
		double S0,St;
		for(int i=1;i<=numberOfElements;i++){
			S0=el3angArea(i);
			S=subS3ang(i,P);
			St=S[0]+S[1]+S[2];

			if(abs(1-St/S0)<1e-6){ ne[0]= i; break;}

		}

		return ne;
	}



	public int[] elContains(int i,Vect P){
		int n;
		int[] result=new int [nBoundary];
		for(int ns=0;ns<nBoundary;ns++){
			n=pointOnFace(i,ns,P);
			result[ns]=n;

		}

		return result;
	}

	public int pointOnFace(int i, int ns,Vect P){
		Vect v0,v1,v2,vP;

		v0=node[element[i].getVertNumb(facetVert[ns][0])].getCoord();
		v1=node[element[i].getVertNumb(facetVert[ns][1])].getCoord();
		v2=node[element[i].getVertNumb(facetVert[ns][2])].getCoord();
		vP=P.sub(v0);

		if(vP.norm()==0)
			return 0;
		double crossdot=v1.sub(v0).cross(v2.sub(v0)).dot(P.sub(v0));
		if(crossdot==0)
			return 0;
		else if(crossdot>1e-10)
			return -1;
		else
			return 1;

	}

	public int[] getContainingQuadElement(Vect P){

		int[] ne=new int[1];

		Vect[] v=new Vect[4];

		for(int i=1;i<=numberOfElements;i++){
			int[] vertNumb=element[i].getVertNumb();
			for(int j=0;j<4;j++)
				v[j]=node[vertNumb[j]].getCoord();

			double S0=v[1].sub(v[0]).cross(v[3].sub(v[0])).norm()+
			v[1].sub(v[2]).cross(v[3].sub(v[2])).norm();
			double S=0;
			for(int j=0;j<4;j++)
			{
				S=S+v[j].sub(P).cross(v[(j+1)%4].sub(P)).norm();
			}
			if(abs(1-S/S0)<1e-6) {ne[0]=i; break;}

		}

		return ne;

	}


	public double getElementArea(int i){
		if(elCode==0) return el3angArea(i);
		else 	if(elCode==1) return elQuadArea(i);
		else throw new NullPointerException(" Element is not 2D. ");

	}

	public double el3angArea(int i){
		Node[] vertexNode=elementNodes(i);

		Vect v1=vertexNode[1].getCoord().sub(vertexNode[0].getCoord());
		Vect v2=vertexNode[2].getCoord().sub(vertexNode[0].getCoord());
		double S=abs(v1.cross(v2).norm())/2;
		return S;
	}

	public double elQuadArea(int i){
		Node[] vertexNode=elementNodes(i);

		Vect v1=vertexNode[1].getCoord().sub(vertexNode[0].getCoord());
		Vect v2=vertexNode[3].getCoord().sub(vertexNode[0].getCoord());
		Vect v3=vertexNode[1].getCoord().sub(vertexNode[2].getCoord());
		Vect v4=vertexNode[3].getCoord().sub(vertexNode[2].getCoord());
		double S=(v1.cross(v2).norm()+v4.cross(v3).norm())/2;
		return S;
	}

	private void setElementB(int i){


		if(elCode==0) set3angElementB(i);
		else if(elCode==1) setQuadElementB(i);

		else{
			Node[] vertexNode=elementNodes(i);
			Vect zero=new Vect(3);
			Mat jac=femCalc.jacobian(zero,vertexNode);
			Vect B;
			Vect[] rotNe=femCalc.rotNe(jac,zero);
			B=getElementB(i,rotNe);

			element[i].setB(B);


		}


	}	

	private void setQuadElementB(int i){

		Node[] vertexNode=elementNodes(i);
		Vect zero=new Vect(2);
		Mat jac=new Mat();
		Vect[] rotNe;

		jac=femCalc.jacobian(zero,vertexNode);
		rotNe=femCalc.rotNeQuad(jac,zero);

		Vect B=getElementB(i,rotNe);

		element[i].setB(B);

	}


	private void set3angElementB(int i){

		Edge[] elEdge=elementEdges(i);
		double[] A=new double[3];
		for(int j=0;j<3;j++)
			A[j]=elEdge[j].Au;

		Vect[] rotNe=femCalc.rotNe3ang(this.elementNodes(i));
		Vect B=new Vect(2);		
		for(int j=0;j<3;j++)
			B=B.add(rotNe[j].times(A[j]));

		element[i].setB(B);
	}


	public void setElType(String type){
		elType=type;
		if(type.equals("triangle") ){
			elCode=0;
			nElVert=3;
			nElEdge=3;
			this.dim=2;
		}
		else if(type.equals("quadrangle") ){
			elCode=1;
			nElVert=4;
			nElEdge=4;
			this.dim=2;
		}
		else if(type.equals("tetrahedron") ){
			elCode=2;
			nElVert=4;
			nElEdge=6;
			dim=3;
		}

		else if(type.equals("prism") ){
			elCode=3;
			nElVert=6;
			nElEdge=9;
			dim=3;
			nElSurf=5;
		}
		else if(type.equals("hexahedron") ){
			elCode=4;
			nElVert=8;
			nElEdge=12;
			dim=3;
			nElSurf=6;
		}

		else if(type.equals("pyramid") ){
			elCode=5;
			nElVert=5;
			nElEdge=8;
			dim=3;
			nElSurf=5;
		}
		nBoundary=2*dim;
		nBoundary=2*dim;
	}



	private Vect getElementB(int i, Vect[] rotNe){

		Edge[] edge=elementEdges(i);
		Vect B=new Vect(dim);
		for(int j=0;j<nElEdge;j++)		{
			B=B.add(rotNe[j].times(edge[j].Au));
		}

		return B;

	}

	public double getElementQuadA(int i){
		Edge[] elEdge=elementEdges(i);
		double[] Ae=new double[4];
		for(int j=0;j<4;j++)
			Ae[j]=elEdge[j].Au;
		double A=0;
		Vect zero=new Vect(2);

		double[] Ne=femCalc.NeQuad(zero);

		for(int j=0;j<nElEdge;j++)	{		
			A= A+Ne[j]*Ae[j];
		}
		return  A;	

	}

	public double elementVolume(int i){
		if(dim==0) return 0;
		double vol=0;
		Node[] vertexNode=elementNodes(i);
		Mat jac;
		double detJac,ws;
		Vect localCo=new Vect(dim);
		ws=8;
		jac=femCalc.jacobian(localCo,vertexNode);
		detJac=abs(jac.determinant());

		vol=detJac*ws;

		return vol;

	}


	public void setEdge(){

		EdgeSet edgeSet=new EdgeSet();
		edgeSet.setEdge(this);

	}

	public Vect getElementA(int ie){

		Vect A=new Vect(dim);
		Vect zero=new Vect(dim);
		Node[] vertexNode=this.elementNodes(ie);
		Edge[] edge=this.elementEdges(ie);
		Mat jac=femCalc.jacobian(zero,vertexNode);
		Vect[] Ne=femCalc.Ne(jac,zero);

		for(int j=0;j<nElEdge;j++)	{		
			A= A.add(Ne[j].times(edge[j].Au));
		}
		return  A;	

	}

	public double getElementPhi(int ie){

		double phi=0;
		Vect zero=new Vect(dim);
		Node[] vertexNode=this.elementNodes(ie);
		double[] N=femCalc.N(zero);

		for(int j=0;j<nElVert;j++)	{		
			phi+=N[j]*vertexNode[j].getPhi();
		}
		return  phi;	

	}

	public Vect getElementdA(Node[] vertexNode,double[] Ae){

		Vect dA=new Vect(dim);
		Vect zero=new Vect(dim);
		Mat jac=femCalc.jacobian(zero,vertexNode);
		Vect[] Ne=femCalc.Ne(jac,zero);

		for(int j=0;j<nElEdge;j++)	{		
			dA= dA.add(Ne[j].times(Ae[j]));
		}
		return  dA;	

	}
	public double getElementQuadA(double[] dAe){

		double dA=0;
		Vect zero=new Vect(2);
		double[] Ne=femCalc.NeQuad(zero);
		for(int j=0;j<nElEdge;j++)	{		
			dA+=Ne[j]*dAe[j];
		}
		return  dA;	

	}


	public double getElementQuadPhi(int ie){

		double phi=0;
		Vect zero=new Vect(dim);
		Node[] vertexNode=this.elementNodes(ie);
		Mat jac=femCalc.jacobian(zero,vertexNode);
		double[] N=femCalc.NQuad(zero);

		for(int j=0;j<nElVert;j++)	{		
			phi+=N[j]*vertexNode[j].getPhi();
		}
		return  phi;	

	}

	public double getElementQuaddA(double[] dAe){

		double dA=0;
		Vect zero=new Vect(2);
		double[] Ne=femCalc.NeQuad(zero);
		for(int j=0;j<nElEdge;j++)	{		
			dA+=Ne[j]*dAe[j];
		}
		return  dA;	

	}

	public double getElement3angA(int ie){

		Edge[] edge=this.elementEdges(ie);
		double a=1.0/3;
		double[] localNe={a,a,a};
		double A=0;
		for(int j=0;j<3;j++)	{		
			A+=localNe[j]*edge[j].Au;
		}
		return  A;	

	}


	public double getElement3angPhi(int ie){

		double phi=0;
		Node[] vertexNode=this.elementNodes(ie);
		double a=1.0/3;
		double[] localNe={a,a,a};
		for(int j=0;j<nElVert;j++)	{		
			phi+=localNe[j]*vertexNode[j].getPhi();
		}
		return  phi;	

	}

	public double getElement3angdA(int ie,double[] dAe){

		double a=1.0/3;
		double[] localNe={a,a,a};
		double dA=0;
		for(int j=0;j<3;j++)	{		
			dA+=localNe[j]*dAe[j];
		}
		return  dA;	

	}



	public void setB(){

		double Bn2,Bmax2=0,Bmin2=0;

		for(int i=1;i<=numberOfElements;i++){
			setElementB(i);

			Bn2=element[i].getB().dot(element[i].getB());
			if(Bn2>Bmax2)
				Bmax2=Bn2;
			if(Bn2<Bmin2)
				Bmin2=Bn2;}

		Bmax=sqrt(Bmax2);
		Bmin=sqrt(Bmin2);

		setAuMax();

	}

	public int[] getRegNodes(int ir){

		boolean[] nc=new boolean[1+this.numberOfNodes];
		int[] nn=new int[this.numberOfNodes];
		int ix=0;
		for(int i=this.region[ir].getFirstEl();i<=this.region[ir].getLastEl();i++){		
			int[] vertNumb=this.element[i].getVertNumb();
			for(int j=0;j<nElVert;j++){
				int nx=vertNumb[j];
				if(!nc[nx]){

					nc[nx]=true;
					nn[ix]=nx;
					ix++;
				}
			}
		}

		int[] regNodes=new int[ix];
		for(int i=0;i<ix;i++)
			regNodes[i]=nn[i];

		return regNodes;


	}

	public int[] getRegEdges(int ir){

		boolean[] edc=new boolean[1+this.numberOfEdges];
		int[] ned=new int[this.numberOfEdges];
		int ix=0;
		for(int i=this.region[ir].getFirstEl();i<=this.region[ir].getLastEl();i++){		
			int[] edgeNumb=this.element[i].getEdgeNumb();
			for(int j=0;j<nElEdge;j++){
				int nx=edgeNumb[j];
				if(!edc[nx]){

					edc[nx]=true;
					ned[ix]=nx;
					ix++;
				}
			}
		}

		int[] regEdges=new int[ix];
		for(int i=0;i<ix;i++)
			regEdges[i]=ned[i];

		return regEdges;


	}

	public Vect getBAt(Vect P){
		return femCalc.getBAt(this, P);
	}


	public Vect[] getAllB(){	

		Vect[] B=new Vect[numberOfElements];

		for(int ie=0;ie<numberOfElements;ie++)
			B[ie]=element[ie+1].getB();
		return B;
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

	public double getBmax(){	

		double Bmax=0;

		for(int i=1;i<=numberOfElements;i++){
			double Bn=element[i].getB().norm();
			if(Bn>Bmax) Bmax=Bn;

		}
		return Bmax;
	}

	public double getRmax(){	

		double rmax=0;

		for(int i=1;i<=numberOfNodes;i++){

			double rn=node[i].getCoord().norm();
			if(rn>rmax) rmax=rn;

		}
		return rmax;
	}

	public void setAuMax(){	

		AuMax=0;

		for(int i=1;i<=numberOfEdges;i++){
			double aum=abs(edge[i].Au);
			if(aum>AuMax) AuMax=aum;

		}

	}

	public void setFemCalc(){
		femCalc=new Calculator(this);

	}

	public void setuMax(){	

		uMax=0;

		for(int i=1;i<=numberOfNodes;i++){
			if(node[i].u==null) continue;
			double a=node[i].u.norm();
			if(a>uMax) uMax=a;

		}

	}


	public Vect getFOf(Vect globalCo){

		int m[]=getContainingElement(globalCo);

		if(m[0]<=0) throw new NullPointerException("given point outside the space ");

	
		Vect F=new Vect(dim);
		for(int j=0;j<nElVert;j++)
			F=F.add(node[element[m[0]].getVertNumb(j)].F);

		return  F.times(1.0/nElVert);	

	}


	private double[] subS3ang(int ie, Vect P){

		Node[] vertexNode=elementNodes(ie);
		Vect v1=vertexNode[0].getCoord().sub(P);
		Vect v2=vertexNode[1].getCoord().sub(P);
		Vect v3=vertexNode[2].getCoord().sub(P);

		double[] N=new double[3];

		N[0]=v2.cross(v3).norm()/2;
		N[1]=v3.cross(v1).norm()/2;
		N[2]=v1.cross(v2).norm()/2;
		return N;
	}

	public Vect getElementCenter(int ie){


		Vect center=new Vect(dim);
		int[] vertNumb=element[ie].getVertNumb();
		for(int j=0;j<nElVert;j++)
			center=center.add(node[vertNumb[j]].getCoord());

		return center.times(1.0/nElVert);
	}

	public void setNodalScalar(int mode){
	
		double c=1.0/nElVert;
		double eps=1.0e-6;

		
		int[] count=new int[this.numberOfNodes+1];
		double[] str=new double[this.numberOfNodes+1];
		nodalScalarMax=-1e40;
		nodalScalarMin=1e40;

		Vect sv=null;
		double se=0;
		double st=.1;

		if(mode<2 || mode==10){

		for(int ir=1;ir<=this.numberOfRegions-2;ir++){
			for(int i=this.region[ir].getFirstEl();i<=this.region[ir].getLastEl();i++){
				if(mode==0){
					sv=element[i].getB();
					se=sv.norm();
					
				}
				
				else if(mode==1)sv=element[i].getStress();
				else if(mode==10) se=i;
			
				if(mode!=10 && sv==null) continue;

				if(mode==1){
					if(this.stressViewCode==0)
						se=sv.el[0];
					else if(this.stressViewCode==1)
						se=sv.el[1];
					else if(this.stressViewCode==2){
						if(this.dim==3)
							se=sv.el[2];
					}
					else if(this.stressViewCode==3)
						{
						Vect r=this.getElementCenter(i).v2();
						Vect rn=new Vect(2);
						if(r.norm()!=0)
						 rn=r.normalized();
						 if(dim==3) rn=rn.v3();
						se=util.tensorize(sv).mul(rn).dot(rn);
						
						}
					else if(this.stressViewCode==4)
					{
					Vect r=this.getElementCenter(i).v2();
					Vect rn=new Vect(2);
					if(r.norm()!=0)
					 rn=r.normalized();
					rn=new Vect(-rn.el[1],rn.el[0]);
					 if(dim==3) rn=rn.v3();
					se=util.tensorize(sv).mul(rn).dot(rn);
					
					}
					else if(this.stressViewCode==5)
						se=meis(sv);
				}

				if(abs(se)<eps) continue;
				if(se>nodalScalarMax) nodalScalarMax=se;
				if(se<nodalScalarMin)nodalScalarMin=se;
				int[] vertNumb=element[i].getVertNumb();
				for(int j=0;j<nElVert;j++){
					if(abs(se)>st){
						count[vertNumb[j]]++;
						str[vertNumb[j]]+=c*se;
					}
				}

			}
		}
		
		}


		if(nodalStressMaxCode==0){
			nodalScalarMax=-1e40;
			nodalScalarMin=1e40;
		}

		for(int i=1;i<=this.numberOfNodes;i++)
		{
			if(mode==11)
				node[i].scalar=i;
			else if(mode==2 || mode==3 || mode==4){

				Vect v=node[i].getNodalVect(mode-2);
				if(v!=null)
					node[i].scalar=v.norm();
				
			}
			else{
			if(count[i]>0)
				str[i]/=count[i];
			node[i].scalar=str[i]*nElVert;
			}

			if(nodalStressMaxCode==0){

				if(node[i].scalar>nodalScalarMax)
					nodalScalarMax=node[i].scalar;
				if(node[i].scalar<nodalScalarMin)
					nodalScalarMin=node[i].scalar;
			}

		}


		util.pr("nodalScalarMin: "+this.nodalScalarMin+"  nodalScalarMax: "+this.nodalScalarMax);
	}
	
	private double meis(Vect sv){
		double se=0;

		if(dim==2){
			double s1,s2,s12,s3;
			s1=sv.el[0];
			s2=sv.el[1];
			s12=sv.el[2];
			s3=0.3*(s1+s2);

			se=pow(s1-s2,2)+pow(s2-s3,2)+pow(s1-s3,2)+6*s12;
		}
		else{
			double s1,s2,s3,s12,s23,s13;
			s1=sv.el[0];
			s2=sv.el[1];
			s3=sv.el[2];
			s12=sv.el[3];
			s23=sv.el[4];
			s13=sv.el[5];
			se=pow(s1-s2,2)+pow(s2-s3,2)+pow(s1-s3,2)+6*s12+6*s23+6*s13;
		}

		se/=2;
		se=sqrt(se);

	return se;
	}

	public void setNodalStress(){/*
		double c=1.0/nElVert;
		double eps=1.0e-6;
		int[] count=new int[this.numberOfNodes+1];
		double[] str=new double[this.numberOfNodes+1];
		stressMax=-1e40;
		stressMin=1e40;

		Vect sv=null;

		double se=0;
		for(int i=1;i<=this.numberOfElements;i++){

			sv=element[i].getStress();
			if(sv==null) continue;

			if(this.stressViewCode>=0)
				se=sv.el[this.stressViewCode];
			else if(this.stressViewCode==-1)
				se=sv.norm();
			else if(sv!=null){

				if(dim==2){
					double s1,s2,s12,s3;
					s1=sv.el[0];
					s2=sv.el[1];
					s12=sv.el[2];
					s3=0.3*(s1+s2);

					se=pow(s1-s2,2)+pow(s2-s3,2)+pow(s1-s3,2)+6*s12;
				}
				else{
					double s1,s2,s3,s12,s23,s13;
					s1=sv.el[0];
					s2=sv.el[1];
					s3=sv.el[2];
					s12=sv.el[3];
					s23=sv.el[4];
					s13=sv.el[5];
					se=pow(s1-s2,2)+pow(s2-s3,2)+pow(s1-s3,2)+6*s12+6*s23+6*s13;
				}

				se/=2;
				se=sqrt(se);

			}


			if(abs(se)<eps) continue;
			if(se>stressMax)stressMax=se;
			if(se<stressMin)stressMin=se;
			int[] vertNumb=element[i].getVertNumb();
			for(int j=0;j<nElVert;j++){
				count[vertNumb[j]]++;
				str[vertNumb[j]]+=c*se;
			}

		}

		if(nodalStressMaxCode==0){
			nodalScalarMax=-1e40;
			nodalScalarMin=1e40;
		}

		for(int i=1;i<=this.numberOfNodes;i++)
		{
			if(count[i]>0)
				str[i]/=count[i];

			node[i].scalar=str[i]*nElVert;

			if(nodalStressMaxCode==0){

				if(node[i].scalar>nodalScalarMax)
					nodalScalarMax=node[i].scalar;
				if(node[i].scalar<nodalScalarMin)
					nodalScalarMin=node[i].scalar;
			}

		}

		util.pr("Stress Min: "+this.stressMin+"  Stress Max: "+this.stressMax);
		util.pr("Nodal Stress Min: "+this.nodalScalarMin+"  Nodal Stress Max: "+this.nodalScalarMax);
	*/}

	public void resultAt(Vect P){
		System.out.println();
		Vect Bn=this.getBAt(P);
		if(Bn.el[0]==1e10 && Bn.el[1]==-1e10) util.pr(" >>>>  Given point is located outside the analyzed space!");
		{
			System.out.print("At : ");	P.hshow();
			System.out.print("B(muT) : ");	Bn.times(1e6).hshow();

			System.out.println();

		}
	}

	public void setSliceBounds(){
		double epsAng=1e-6;

		for(int ir=1;ir<=this.numberOfRegions;ir++)
			if(this.region[ir].rotor)
				for(int i=this.region[ir].getFirstEl();i<=this.region[ir].getLastEl();i++)
				{
					int[] vertNumb=this.element[i].getVertNumb();
					for(int j=0;j<this.nElVert;j++)
						this.node[vertNumb[j]].rotor=true;
				}

		double tmax=-10,tmin=10,rmin=1e4,rmax=0,rm=0;
		double p2=2*PI;
		for(int i=1;i<=this.numberOfNodes;i++)
		{
			Vect z=this.node[i].getCoord();
			double s=new Vect(z.el[0],z.el[1]).norm();
			double t;
			if(s==0) t=0;
			else t=util.getAng(z);
			if(Math.abs(t-p2)<epsAng) t=0;

			if(t>tmax) tmax=t;
			if(t<tmin) tmin=t;
			if(s>rmax) rmax=s;
			if(s<rmin) rmin=s;
			if(this.motor){
				if(this.node[i].rotor) if(s>rm) rm=s;
			}

		}


		this.r1=rmin;
		this.r2=rmax;
		this.rm=rm;
		this.alpha1=tmin;
		this.alpha2=tmax;
		if(tmax-tmin>6) this.fullMotor=true;

	}

	public double getSliceAngle(){
		return this.alpha2-this.alpha1;
	}



}


