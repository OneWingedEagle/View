package panels;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import components.*;

public class EditBlock extends JFrame implements ActionListener{

	
	public TextField[] tfxyz;
	private Button duplicate,rotate,translate,bClose;
	public Button bApply;
	public int mode=-1;
	public JComboBox combDuplic,rotPhi,rotTheta;
	private JPanel midPanel,vectPanel,rotPanel,dupPanel;

	 public EditBlock() {

			
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle(" Edit Block");
			setSize(220,350);
			
			

			JPanel panel = new JPanel(new GridLayout(3,1,10,10));
			panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			add(panel);
		
			String[] direction={" X1"," X2"," Y1"," Y2"," Z1"," Z2"};
			combDuplic = new JComboBox(direction);
			combDuplic.setFont(new Font("Times New Roman", 1, 13));
			combDuplic.setSelectedIndex(1);
		
			
			String[] rotPhiAng={"0","90"};
			rotPhi = new JComboBox(rotPhiAng);
			rotPhi.setFont(new Font("Times New Roman", 1, 13));
			rotPhi.setSelectedIndex(0);
			
			String[] rotThetaAng={"0","90"};
			rotTheta = new JComboBox(rotThetaAng);
			rotTheta.setFont(new Font("Times New Roman", 1, 13));
			rotTheta.setSelectedIndex(0);
			
			rotPanel=new JPanel(new GridLayout(2,2,10,10));
			rotPanel.add(new Label(" Azimutal "));
			rotPanel.add(rotPhi);
			rotPanel.add(new Label(" Polar "));
			rotPanel.add(rotTheta);
		
			
			dupPanel=new JPanel(new GridLayout(2,2,10,10));
			dupPanel.add(new Label("Duplicate at "));
			dupPanel.add(combDuplic);
			dupPanel.add(new Label(""));
			dupPanel.add(new Label(""));
			
			translate=new Button("Translate");
			duplicate=new Button("Duplicate");
			rotate=new Button("Rotate");
			bApply=new Button("Apply");
			bClose=new Button("Close");
			
		
			
			
			tfxyz=new TextField[3];
			Label[] lbxyz=new Label[3];
			lbxyz[0]=new Label(" X");
			lbxyz[1]=new Label(" Y");
			lbxyz[2]=new Label(" Z");
		    
			for(int i=0;i<3;i++)
				tfxyz[i]=new TextField("0.0");
	
			vectPanel=new JPanel(new GridLayout(3,2,10,10));
			for(int i=0;i<3;i++){
				vectPanel.add(lbxyz[i]);
				vectPanel.add(tfxyz[i]);
				
			}
			
			midPanel=new JPanel(new GridLayout(1,1,10,10));

			JPanel functionPanel=new JPanel(new GridLayout(3,2,10,10));
			functionPanel.add(translate);
			functionPanel.add(new Label());
			functionPanel.add(rotate);
			functionPanel.add(new Label());
			functionPanel.add(duplicate);
			functionPanel.add(new Label());

			JPanel applyPanel=new JPanel(new GridLayout(3,2,10,10));	
			
			for(int i=0;i<3;i++)
				applyPanel.add(new Label());
			applyPanel.add(bApply);
			applyPanel.add(new Label());
			applyPanel.add(bClose);
			
			
			panel.add(functionPanel);
			panel.add(midPanel);
			panel.add(applyPanel);

			add(panel);
			
			translate.addActionListener(this);
			duplicate.addActionListener(this);
			rotate.addActionListener(this);
			translate.addActionListener(this);
			bClose.addActionListener(this);
	}
	 
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==translate){
				midPanel.removeAll();
				midPanel.add(vectPanel);
				mode=0;	
				}
	else if(e.getSource()==rotate){
		midPanel.removeAll();
		midPanel.add(rotPanel);
		mode=1;	
		}
	else if(e.getSource()==duplicate){
	midPanel.removeAll();
	midPanel.add(dupPanel);
	mode=2;
	}
	
	
	else if(e.getSource()==bClose){
		midPanel.removeAll();
		mode=-1;
		setVisible(false);
	}
		
		midPanel.updateUI();
		
	}
	

	public static void main(String[] args){

		EditBlock eb=new EditBlock();
		eb.setVisible(true);
		
	}
	 

}




