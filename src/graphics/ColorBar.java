package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import javax.swing.JPanel;

import math.util;

import components.Label;
import components.TextField;


public class ColorBar
{
    
    private Color color[];
    private double min,max;
    public TextField[] tfc;
    private Label[][] lb;
    private double[]  levels;
    private DecimalFormat df;

    
    public ColorBar()
    {
    	tfc=new TextField[2];
    	tfc[0]=new TextField("");
    	tfc[1]=new TextField("");
    	df=new DecimalFormat("0.00E00");
        set();
    }


    public ColorBar(double min, double max)
    {
        this.min = min;
        this.max = max;
        set();
    }

    public void setEnds(double min, double max){
    	this.min=min;
    	this.max=max;
    	if(levels!=null)
    	setLevels();

    	tfc[0].setText(df.format(min));
    	tfc[1].setText(df.format(max));
    	
 
    }
    
    public double[] getEnds(){
    	double[] ends=new double[2];
    	ends[0]=this.min;
    	ends[1]=this.max;
    	return ends;
    }

    public JPanel getColorBarPn(double min, double max,String name,int N)
    {
    	
    	
    	levels=new double[N];

    	JPanel pn=new JPanel(new GridLayout(N+4,1,5,0));

    	this.min=min;
    	this.max=max;
    	
    	set();
    	JPanel[] p1=new JPanel[N];
    	for(int j=0;j<N;j++)
    		p1[j]=new JPanel(new GridLayout(1,2,5,5));
    	
   
    	this.tfc[0].setPreferredSize(new Dimension(30, 10));
    	this.tfc[1].setPreferredSize(new Dimension(30, 10));
    
    	
       	
 
    	lb=new Label[N+2][2];
    
    	lb[0][0]=new Label(name,Label.LEFT);
    	lb[1][0]=new Label();
    	

    	
    	pn.add(lb[0][0]);
    	pn.add(lb[1][0]);
    	pn.add(this.tfc[1]);
    	
    	lb[2][0]=new Label(this.df.format(max));
		lb[2][0].setFont(new Font("Arial", 1, 12));
		lb[N+1][0]=new Label(this.df.format(min));
		lb[N+1][0].setFont(new Font("Arial", 1, 12));
    	
    for(int j=0;j<N;j++){
   
    lb[j+2][0]=new Label("0.0");
	lb[j+2][0].setFont(new Font("Arial", 0, 12));
    lb[j+2][1]=new Label(""); 
    lb[j+2][1].setOpaque(true);	
	double value=max-j*(max-min)/N;
	levels[j]=value;
	
	 lb[j+2][0].setText(df.format(levels[j]));
	lb[j+2][1].setBackground(getColor(value));
    
	p1[j].add(lb[j+2][0]);
	p1[j].add(lb[j+2][1]);
	pn.add(p1[j]);
  
    }
	pn.add(this.tfc[0]);
	
	setLevels();

    
    	return pn;
    }
    
    

    private void setLevels(){

    	int N=levels.length;
    	for(int j=0;j<N;j++){
			
    		double value=max-j*(max-min)/(N-1);
    		
			levels[j]=value;
			
			 lb[j+2][0].setText(df.format(levels[j]));
	
			
    	}

		
    }
    

   
    public Color getColor(double b)
    {
        return getColor(b, min, max);
    }

    public Color getColor(double b, double bmin, double bmax)
    {
        int mode = Math.max(0, Math.min(color.length-1, (int)(((b - bmin) / (bmax - bmin)) * (double)(color.length-1))));

        if(color[mode]==null) return Color.red;
     
        return color[mode];
      
    }

    private void set()
    {
        color = new Color[160];
        for(int c = 0; c < 20; c++)
        {
            int red = 0;
            int green = 0;
            int blue = 100 + (c * 155) / 20;
            color[c] = new Color(red, green, blue);
        }

        for(int c = 20; c < 25; c++)
        {
            int red = 0;
            int green = 0;
            int blue = 255;
            color[c] = new Color(red, green, blue);
        }

        for(int c = 25; c < 48; c++)
        {
            int red = 0;
            int green = ((c - 25) * 255) / 23;
            int blue = 255;
            color[c] = new Color(red, green, blue);
        }

        for(int c = 48; c < 73; c++)
        {
            int red = 0;
            int green = 190 + ((80 - c) * 65) / 32;
            int blue = ((73 - c) * 255) / 25;
            color[c] = new Color(red, green, blue);
        }

        for(int c = 73; c < 80; c++)
        {
            int red = 0;
            int green = 190 + ((80 - c) * 65) / 32;
            int blue = 0;
            color[c] = new Color(red, green, blue);
        }

        for(int c = 80; c < 87; c++)
        {
            int red = 0;
            int green = 190 + ((c - 80) * 65) / 32;
            int blue = 0;
            color[c] = new Color(red, green, blue);
        }

        for(int c = 87; c < 112; c++)
        {
            int red = ((c - 87) * 255) / 25;
            int green = 190 + ((c - 80) * 65) / 32;
            int blue = 0;
            color[c] = new Color(red, green, blue);
        }

        for(int c = 112; c < 160; c++)
        {
            int red = 255;
            int green = ((160 - c) * (160 - c) * 255) / 48 / 48;
            int blue = 0;
            color[c] = new Color(red, green, blue);
        }
        
       /* for(int c = 0; c < 160; c++)
        	 color[c]= color[c].darker();*/

    }

}
