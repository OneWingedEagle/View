package fem;
import math.Vect;
import math.util;

public class Node {

	public Vect F,Fms,u,A;
	private Vect coord;
	private double phi,nodalMass;
	public double scalar;
	private boolean[] uKnown,aKnown;
	public boolean[]onBound;
	private boolean deformable,phiKnown,phiVar,hasF,hasFms,showVectField;
	public boolean common,PBC,rotor;
	private byte dim;
	private int map;

	public Node(int dim)
	{
	this.dim=(byte)dim;
	coord=new Vect(dim);
	uKnown=new boolean[dim];
	if(dim==2) onBound=new boolean[4];
	}
	
	public void setPhi(double phi){
		this.phi=phi;
		
	}
	
	public double getPhi(){
		return this.phi;
		
	}
	
	public void setNodalMass(double mass){
		nodalMass=mass;		
	}
	
	public double getNodalMass(){
		return this.nodalMass;
		
	}
	
	public Vect getNodalVect(int k){


		 if(k==0)
		{
			if(F==null) return null;
			else{
			return this.F.deepCopy();
			}
		
		}
		else if(k==1) {

		if(Fms==null) return null;
			else
			{
			return this.Fms.deepCopy();
			}
		}
		else if(k==2)
			{
			if(u==null) return null;
			else
			return this.u.deepCopy();
			}
		else
			return null;
		
	}
	
	public void setNodalVect(int k, Vect v){


		 if(k==0)
		{
			 if(v==null)
				 F=null;
			 else
			F=v.deepCopy();
					
		}
		else if(k==1) {
			 if(v==null)
				 Fms=null;
			 else
			Fms=v.deepCopy();
					
		}
		else if(k==2)
			{
			 if(v==null)
				 u=null;
			 else
			u=v.deepCopy();
					
		}
		
		
	}


	
	public void setDeformable(boolean b){
		this.deformable=b;
		if(b){
			F=new Vect(dim);
			u=new Vect(dim);
			Fms=new Vect(dim);
		}
		else
		{
			F=null;
			u=null;
			Fms=null;
		}
	}

	public boolean isDeformable(){
		return this.deformable;
	}
	
	public void setPhiVar(boolean b){
		this.phiVar=b;
	}

	public boolean isPhiVar(){
		return this.phiVar;
	}
	
/*	public void setTemp(double T){
		temp=T;		
	}
	
	public double getTemp(){
		return this.temp;
	}
*/
	public void setPhiKnown(boolean b){
		this.phiKnown=b;
	}

	public boolean isPhiKnown(){
		return this.phiKnown;
	}

	public void setHasF(boolean b){
		 this.hasF=b;
	}
	public boolean hasF(){
		return this.hasF;
	}
	
	public void setHasFms(boolean b){
		 this.hasFms=b;
	}
	public boolean hasFms(){
		return this.hasFms;
	}
	
	public void setU_is_known(boolean b){
		for(int k=0;k<dim;k++)
		uKnown[k]=b;
	}
	
	public void setA_is_known(boolean b){
		for(int k=0;k<dim;k++)
		aKnown[k]=b;
	}
	
	public void setU_is_known(int i,boolean b){
		uKnown[i]=b;
	}
	public void setA_is_known(int i,boolean b){
		aKnown[i]=b;
	}
	
	public boolean is_U_known(){

		for(byte k=0;k<dim;k++)
			if(!uKnown[k]) return false;
		
		return true;
	}
	public boolean is_A_known(){

		for(byte k=0;k<dim;k++)
			if(!uKnown[k]) return false;
		
		return true;
	}

	public boolean has_U_known(){
		for(byte k=0;k<dim;k++)
			if(uKnown[k]) return true;
		return false;
	}
	public boolean has_A_known(){
		for(byte k=0;k<dim;k++)
			if(aKnown[k]) return true;
		return false;
	}
	
	public boolean is_U_known(int i){
		return uKnown[i];
	}
	
	public boolean is_A_known(int i){
		return aKnown[i];
	}

	public void setKnownU(Vect u){
		this.u=u.deepCopy();
		setU_is_known(true);
	}
	
	public void setKnownA(Vect A){
		this.A=A.deepCopy();
		setA_is_known(true);
	}
	
	public void setKnownA(int k,double a){
		this.A.el[k]=a;
		setA_is_known(k,true);
	}
	
	
	public void setKnownU(int k,double a){
		this.u.el[k]=a;
		setU_is_known(k,true);
	}
	
	public void setKnownU(double a, double b, double c){
		setKnownU(new Vect(a,b,c));
		
	}
	
	public void setKnownA(double a, double b, double c){
		setKnownA(new Vect(a,b,c));
		
	}
	
	
	public void setU(Vect u){
		this.u=u.deepCopy();
	}
	
	public void setA(Vect a){
		this.A=a.deepCopy();
	}
	
	public void setU(int k,double a){
		this.u.el[k]=a;

	}
	
	public void setA(int k,double a){
		this.A.el[k]=a;

	}

	public Vect getU(){
		return	this.u.deepCopy();

		}
	public Vect getA(){
		return	this.A.deepCopy();

		}
	
	public double getU(int k){
		return	this.u.el[k];

		}
	public double getA(int k){
		return	this.A.el[k];

		}
	
	public void setCoord(Vect coord){
			this.coord=coord.deepCopy();

		}
	
	public Vect getCoord(){
		return	this.coord.deepCopy();

		}
	
	public double getR(){
		return	this.coord.norm();

		}
	
	public void setCoord(int i, double u){
		this.coord.el[i]=u;

	}
	public double getCoord(int i){
		return this.coord.el[i];

	}
	
	
	public void setF(Vect F){
		this.F=F.deepCopy();
		
	}
	public void setFms(Vect F){
		this.Fms=F.deepCopy();
		
	}


	public void setMap(int map){
		this.map=map;
		
	}
	
	public int getMap(){
		return this.map;
		}
	
	public void setShowVectField(boolean b){
		this.showVectField=b;
	}
	public boolean toShowVectField(){
		return this.showVectField;
	}
	
}
