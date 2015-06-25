package components;

import graphics.V3f;

import javax.media.j3d.DirectionalLight;
import javax.swing.BorderFactory;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Color3f;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class EditLight extends JFrame implements ActionListener{


	private TextField[][] tfDir,tfColor;
	public Button bApply,bReset;
	public V3f[] direction;
	public Color3f[] color;
	public double[][] refCol, refDir;
	private DecimalFormat df=new DecimalFormat("#.0#");
	public EditLight(){

	}

	public EditLight(int N) {

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle(" Adjust lights");
		setSize(300,500);

		JPanel panel = new JPanel(new GridLayout(2*N,4,5,5));
		panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		add(panel);


		Label[] lb=new Label[2*N];

		tfDir=new TextField[N][3];
		tfColor=new TextField[N][3];

		direction=new V3f[N];
		color=new Color3f[N];
		
		refCol=new double[N][3];
		refDir=new double[N][3];
		double[] a={.6,.9,.2,.8,.2,.4,.35};
		double[][] v={{.0,.0,1},{.5,.5,-1},{.5,1,.5},{.5,-1,.5},{1,.5,.5,},{-1,.5,.5,},{0,0,0}};
		
		for(int i=0;i<N;i++){
		for(int j=0;j<3;j++){
			refCol[i][j]=a[i];
			refDir[i][j]=v[i][j];
		}
		
		}
		

		for(int i=0;i<N;i++){
			if(i<N-1){
				lb[2*i]=new Label("color"+(i+1));
				lb[2*i+1]=new Label("direc."+(i+1));
				for(int j=0;j<3;j++){
					tfColor[i][j]=new TextField(df.format(refCol[i][j]));
					tfDir[i][j]=new TextField(df.format(refDir[i][j]));
				}

			}
			else
			{
				lb[2*i]=new Label("ambCol");
				lb[2*i+1]=new Label();
			

			for(int j=0;j<3;j++){
				tfColor[i][j]=new TextField(df.format(refCol[i][j]));
				tfDir[i][j]=new TextField("0.0");
			}
			}
		}


		for(int i=0;i<N;i++){


			if(i<N-1){
					panel.add(lb[2*i]);
					for(int j=0;j<3;j++){
						panel.add(tfColor[i][j]);
					}
		
					panel.add(lb[2*i+1]);
					for(int j=0;j<3;j++){
						panel.add(tfDir[i][j]);
					}


			}
			else{
				panel.add(lb[2*i]);
				for(int j=0;j<3;j++){
					panel.add(tfColor[i][j]);
				}
			}

		}


		for(int j=0;j<2;j++)
			panel.add(new Label());

		bApply=new Button("Apply");
		bReset=new Button("Reset");

		bReset.addActionListener(this);

		panel.add(bReset);
		panel.add(bApply);
		


		//pack();

		setColors();

	}

	
	public void setColors() {

		for(int i=0;i<direction.length;i++){
			float[] a=new float[3];
			for(int j=0;j<3;j++)
				a[j]=Float.parseFloat(tfDir[i][j].getText());
			direction[i]=new V3f(a[0],a[1],a[2]);

			for(int j=0;j<3;j++)
				a[j]=Float.parseFloat(tfColor[i][j].getText());

			color[i]=new Color3f(a[0],a[1],a[2]);
		}


	}	
	
	public void reset() {


		for(int i=0;i<direction.length;i++){
			for(int j=0;j<3;j++){
			this.tfColor[i][j].setText(df.format(refCol[i][j]));
			this.tfDir[i][j].setText(df.format(refDir[i][j]));
					}
		}

		setColors();

	}


	@Override
	public void actionPerformed(ActionEvent arg0) {

		reset();
		
	}
	



	public static void main2(String[] args){

		EditLight eb=new EditLight(4);
		eb.setVisible(true);

	}



}




