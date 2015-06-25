package fem;

import math.Vect;

public class Edge {

	public double  length,Au,prevAu;
	public int[] endNodeNumber=new int[2];
	public boolean edgeKnown,hasJ,PBC,common;
	public int map;
	public Vect F;
	
	public Edge(int n1,int n2)
	{

	if(n1<n2){
	endNodeNumber[0]=n1;
	endNodeNumber[1]=n2;
	}
	else{
		endNodeNumber[0]=n2;
		endNodeNumber[1]=n1;
	}

	}
	
	public void setKnownA(double Au){
		edgeKnown=true;
		this.Au=Au;

	}
	
	public void setSolvedAL(double Au){
		this.prevAu=this.Au;
		this.Au=Au;
	}


	public void setLength(double length){
				
		this.length=length;
	}

	public double getDiffAu(){
		return Au-prevAu;
	}

	public void setAu(double Au) {

		this.Au=Au;
		
	}
	
	public double getAu() {

		return this.Au;
		
	}

}
