package materialData;


import graphics.ColorBar;

import java.awt.Color;
import java.util.Arrays;
import static java.lang.Math.random;
import javax.vecmath.Color3f;

import math.util;

	public class MaterialData {

		
		public Color[] color;
		

			
/*		Color.red,Color.blue,Color.green,Color.cyan,Color.gray,CoColor.b,new Color(.85f,.85f,.0f),Color.red,  Color.lightGray, Color.pink,
				Color.yellow,Color.magenta.brighter(),new Color(.5f,1f,.5f),new Color(.3f,.3f,1f),Color.gray,Color.gray,
				new Color(.6f,.9f,.1f),Color.blue,Color.magenta,Color.green.darker(),Color.red,Color.green,Color.blue,
				Color.black,Color.blue.brighter(),Color.pink,Color.magenta,Color.cyan,};*/
		
		
	/*	public Color[] color={Color.cyan,new Color(.85f,.85f,.0f),Color.red,  Color.lightGray, Color.pink,
				Color.yellow,Color.magenta.brighter(),new Color(.5f,1f,.5f),new Color(.3f,.3f,1f),Color.gray,Color.gray,
				new Color(.6f,.9f,.1f),Color.blue,Color.magenta,Color.green.darker(),Color.red,Color.green,Color.blue,
				Color.black,Color.blue.brighter(),Color.pink,Color.magenta,Color.cyan,};*/
	public Color3f[] color3f;
	
	public String[] List;
	
	public Color[] colb;
	
	
	public MaterialData(){
		
	}

	public MaterialData(int nregs){
		
		
		
		ColorBar cBarReg=new ColorBar(-2,nregs);
		int N=nregs;
		color=new Color[N];
		List=new String[N];
		for(int i=0;i<N;i++){
			color[i]=cBarReg.getColor(nregs-i-1) ;
		}
		
		ColorBar cBar=new ColorBar(0.0,1000.0);
		
		int L=1000;
		 colb=new Color[L];
		for(int i=0;i<L;i++)
			colb[i]=cBar.getColor(i);
			
	/*		
		color3f=new Color3f[color.length];
		for(int i=0;i<color3f.length;i++)
			color3f[i]=new Color3f(color[i]);*/
		
		
		
	}
	
/*	public  Color3f matColor3f(String material){
		Color3f matColor3f=new Color3f((float)(.5+.5*random()),(float)(.5+.5*random()),(float)(.5+.5*random())); 
		for(int i=0;i<List.length;i++)
		{
			if(material.startsWith(List[i]))
			{
				matColor3f=color3f[i];
				break;
			}
		
		}
		return matColor3f;
		
	}*/
	
public  Color matColor(String material){
		
		
		Color matColor=this.color[(int)(List.length*random())];

		for(int i=0;i<List.length;i++)
		{
			if(material.startsWith(List[i]))
			{
				matColor=color[i];
				break;
			}
		
		}
		return matColor;

}
	
	public  Color matColor(int ir,String material,int cc){
	
		return matColor(ir,material, cc,0);
		
		
	}
	
	public  Color matColor(int ir,String material,int cc, int bb){

		//if(1>0) return Color.cyan;
	
		if(cc<-1) return color[ir-1];
		if(cc<0 && bb==0) return color[ir-1];
		else if(bb==0 && cc>=0) {
			if(cc>99) cc=99;
			return colb[cc];
		}
	
		Color clr=colb[cc];

		double a=1.0+bb/1000.0;
	
		int r=(int)(clr.getRed()*a);
		if(r>255) r=255;
		
		int g=(int)(clr.getGreen()*a);
		if(g>255) g=255;
		
		int b=(int)(clr.getBlue()*a);
		if(b>255) b=255;
		
		Color matColor=new Color(r,g,b);
			
		
		return matColor;
		
	}
	
/*	public  Color reghColor(String material, int ir){
		Color matColor=this.color[ir];
		return matColor;
	}*/
	
public  Color matColorx(String material,int cc, int bb){

		
		if(cc==-1 && bb==0) return matColor(material);
		else if(bb==0 && cc>=0) {
			if(cc>99) cc=99;
			return colb[cc];
		}
	
		
		float h=.5f+cc/200.0f;
		float b=.5f+bb/200.0f;
		
		Color matColor=Color.getHSBColor(h,1,b);
		
		/*
		util.pr(a);
		int r=(int)(matColor.getRed()*a);
		if(r>255) r=255;
		
		int g=(int)(matColor.getGreen()*a);
		if(g>255) g=255;
		
		int b=(int)(matColor.getBlue()*a);
		if(b>255) b=255;
		
		matColor=new Color(r,g,b);
			*/
		
		return matColor;
		
	}
	
}
