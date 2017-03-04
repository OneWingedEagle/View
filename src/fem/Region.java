package fem;
import static java.lang.Math.PI;
import math.Vect;
import math.util;


public class Region {

	public int dim,BHnumber,lamBNumber,colorCode=-1,brightenss=0;
	private int[] RGB;
	private String material,name;
	private double mu0=PI*4e-7;
	private Vect mur,sigma,nu,J,M;
	private double pois,yng,ro,factJ;
	private int firstElement,lastElement;
	public  boolean hasJ,hasM,isConductor,isNonLinear,deformable,MS,rotor;
	
	public Region(int dim)
	{
		this.dim=dim;
		 mur=new Vect().ones(dim);
		 nu=mur.times(mu0).inv();
		 sigma=new Vect(3);
		 J=new Vect(dim);
		 M=new Vect(dim);
		 
	}
	
	public void setFirstEl(int first){
		this.firstElement=first;
		
	}
	
	public void setLastEl(int last){
		this.lastElement=last;
		
	}
	
	public int getFirstEl(){
		return this.firstElement;
		
	}
	
	public int getLastEl(){
		return this.lastElement;
	}
	
	public void setMaterial(String mat){
		this.material=mat;
		
	}
	public void setName(String name){
		this.name=name;
		
	}
	
	public String getMaterial(){
		return this.material;
		
	}
	
	public String getName(){
		return this.name;
		
	}
	public void setJ(Vect J){

		this.J=J.deepCopy();
		if(J.norm()==0)
		this.hasJ=false;
		else
		this.hasJ=true;
	}
	
	public Vect getJ(){

		return this.J.deepCopy();
	
	}
	
	public void setM(Vect M){
	
		this.M=M.deepCopy();
		if(M.norm()==0)
		this.hasM=false;
		else
		this.hasM=true;
	
	}
	
	public Vect getM(){

		return this.M.deepCopy();
	
	}
		
	
	public void setSigma(Vect sigma){
		this.sigma=sigma.deepCopy();
		if(sigma.norm()>0)
		this.isConductor=true;
		
	}
	
	public Vect getSigma(){

		return this.sigma.deepCopy();
	}
	

	public void setMur(Vect mur){

	this.mur=mur.deepCopy();
	

//	this.mur=new Vect().ones(dim);
		this.nu=this.mur.times(this.mu0).inv();
		
	
				
	}
	
	public Vect getNu(){

		return this.nu.deepCopy();
	
	}
	
	public Vect getMu(){

		return this.nu.inv();
	
	}
	
	public Vect getMur(){

		return this.mur.deepCopy();
	
	}
	
	public void setNonLinear(boolean b){
		this.isNonLinear=b;
				
	}
	
	public void setPois(double pois){
		this.pois=pois;
		
	}
	public void setYng(double yng){
		this.yng=yng;
	}

	public void setRo(double ro){
		this.ro=ro;
	}
	
	public double getPois(){
	return	this.pois;
		
	}

	public double getYng(){
		return	this.yng;
			
		}

	public double getRo(){
		return	this.ro;
			
		}
	
	public void setFactJ(double fj){
		this.factJ=fj;
	}
	
	public double getFactJ(){
		return	this.factJ;
			
		}
	
	public void setDeformable(boolean b){
		this.deformable=b;
	}
	public int getNumbElements(){
		return this.lastElement-this.firstElement+1;
	}
	
	public void setColorCode(int code,int brght){
		 this.colorCode=code;
		 this.brightenss=brght;;
	}
	public int getColorCode(){
		return this.colorCode;
	}
	public int getColorBrightness(){
		return this.brightenss;
	}
	public void setRGB(int[] rgb){
		this.RGB=new int[3];
		for(int i=0;i<3;i++)
		this.RGB[i]=rgb[i];
	}
	public int[] getRGB(){
		return this.RGB;
	}
}
	
	
