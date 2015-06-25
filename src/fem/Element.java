package fem;

import math.Mat;
import math.Vect;
import math.util;


public class Element {
	private int nRegion;
	public int dim;
	private int[] vertexNumb;
	private int[] edgeNumb;
	public int[] edgeXYNumb;
	private Vect B;
	private Vect stress;
	private String type;
	private boolean hasJ,hasM,deformable,showVectField;

	public Element(String type){

		if(type.equals("triangle") ){
			this.vertexNumb=new int[3];
			this.edgeNumb=new int[3];
			dim=2;
		}
			
		else if(type.equals("quadrangle") ){
			this.vertexNumb=new int[4];
			this.edgeNumb=new int[4];
			dim=2;

		}
		else if(type.equals("tetrahedron") ){
			this.vertexNumb=new int[4];
			this.edgeNumb=new int[6];
			dim=3;

		}
		
		else if(type.equals("prism") ){
			this.vertexNumb=new int[6];
			this.edgeNumb=new int[9];
			dim=3;

		}
		else if(type.equals("hexahedron") ){
			this.vertexNumb=new int[8];
			this.edgeNumb=new int[12];
			dim=3;

		}
		
		else if(type.equals("pyramid") ){
			this.vertexNumb=new int[5];
			this.edgeNumb=new int[8];
			dim=3;

		}
		
	/*	

		this.B=new Vect(dim);
		this.nu=new Vect().ones(dim);*/



		
	}





	public void setB(Vect B){

		this.B=B;

	}
	

	public Vect getB(){
		return this.B.deepCopy();

	}

	public boolean hasB(){
		if(B!=null)
		return true;
		else return false;

	}

	public void setRegion(int nr){
		this.nRegion=nr;

	}
	
	public int getRegion(){
		
		return this.nRegion;
	}

	
	public void setStress(Vect  stress){
		this.stress=stress.deepCopy();

	}
	
	public void setStress(Mat  T){
		for(int i=0;i<dim;i++)
		this.stress.el[i]=T.el[i][i];
		stress.el[dim]=T.el[0][1];
		if(dim==3){
			stress.el[4]=T.el[1][2];
			stress.el[5]=T.el[0][2];
		}

	}

	public Vect getStress(){
		if(deformable)
		return this.stress.deepCopy();
		else
			return new Vect(3*(dim-1));

	}

	public Mat getStressTensor(){

		if(stress==null) return new Mat(dim,dim);
		else
		return util.tensorize(stress);

	}
	

	public int getDim(){
		
		return this.dim;
	}
	
	public String getShape(){
		return this.type;
	}
	
	public void setEdgeNumb(int[] ne){
		int nEdge=ne.length;
		edgeNumb=new int[nEdge];
		for(int i=0;i<nEdge;i++){
			edgeNumb[i]=ne[i];
		}
	}
	
	public int[] getEdgeNumb(){
		int nEdge=edgeNumb.length;
		int[] ne=new int[nEdge];
		for(int i=0;i<nEdge;i++)
			ne[i]=edgeNumb[i];
		return  ne;
	}
	
	public void setVertNumb(int[] nv){
		int nVert=nv.length;
		 vertexNumb=new int[nVert];
		for(int i=0;i<nVert;i++)
			vertexNumb[i]=nv[i];
	}
	
	public int[] getVertNumb(){
		int nVert=vertexNumb.length;
		int[] nv=new int[nVert];
		for(int i=0;i<nVert;i++)
			nv[i]=vertexNumb[i];
		return  nv;
	}
	
	public void setEdgeNumb(int j, int ne){

			edgeNumb[j]=ne;
	}
	
	public int getEdgeNumb(int j){

		return  edgeNumb[j];
	}
	
	public void setVertNumb(int j, int nv){
		
			vertexNumb[j]=nv;
	}
	
	public int getVertNumb(int j){

		return  vertexNumb[j];
	}
	
	public void setHasJ(boolean b){
		this.hasJ=b;
	}
	public void setHasM(boolean b){
		this.hasM=b;
	}
	
	public void setShowVectField(boolean b){
		this.showVectField=b;
	}
	public boolean toShowVectField(){
		return this.showVectField;
	}
	
	
	public void setDeformable(boolean b){
		this.deformable=b;
		if(b){

			stress=new Vect(3*(dim-1));
		}
		else{
			stress=null;
		}
	}
	public boolean hasJ(){
		return this.hasJ;
	}
	
	public boolean hasM(){
		return this.hasM;
	}

	public boolean isDeformable(){
		return this.deformable;
	}
		
}
