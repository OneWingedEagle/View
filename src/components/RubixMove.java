package components;

import graphics.V3f;

import javax.media.j3d.DirectionalLight;
import javax.swing.BorderFactory;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Color3f;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class RubixMove extends JFrame implements ActionListener{


	
	public Button[][][]  bMove;
	public Color3f[] color;


	public RubixMove() {

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle(" Rubix moves");
		setSize(500,400);
	
		JPanel panel = new JPanel(new GridLayout(1,3,10,10));
		panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		

		add(panel);
		
		int I=3;
		int J=3;
		int K=2;
		
		JPanel[] panels=new JPanel[3];
		for(int i=0;i<I;i++){
		panels[i] = new JPanel(new GridLayout(3,2,0,5));
		}

		
		bMove=new Button[I][J][K];
				
		for(int i=0;i<I;i++){
			for(int j=0;j<J;j++)
				for(int k=0;k<K;k++){
					if(k==0)
				bMove[i][j][k]=new Button("<");
					else
				bMove[i][j][k]=new Button(">");
					bMove[i][j][k].setFont( new Font("Times New Roman", 1, 20));
					//bMove[i][j][k].setName(i+"0"+j+"0"+k);
					
					if(i==0) bMove[i][j][k].setBackground(Color.red.darker());
					else if(i==1) bMove[i][j][k].setBackground(Color.green.darker());
					else bMove[i][j][k].setBackground(Color.cyan);
					panels[i].add(bMove[i][j][k]);
			}
	
				panel.add(panels[i]);
			}


		

	}

	


	public static void main2(String[] args){

		RubixMove eb=new RubixMove();
		eb.setVisible(true);

	}




	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}



}




