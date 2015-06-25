package components;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;

import components.*;

public class EditReg extends JFrame implements ActionListener, ItemListener{

	
	public TextField tfEdgeTransp,tfFaceTransp;
	private Button bFaceColor,bEdgeColor;
	public Button bApply;
	public int mode=-1;
	public JCheckBox edch,fcch,regch;
	public Color faceColor, edgeColor;
	public boolean hideFace,hideEdge;
	
	 public EditReg() {

			
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle(" Edit Block");
			setSize(220,180);
			
			
			JPanel panel = new JPanel(new GridLayout(4,2,10,10));
			panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			add(panel);
		
			
			bFaceColor=new Button("face color");
			bFaceColor.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

			
			bEdgeColor=new Button("edge color");
			bEdgeColor.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			regch=new JCheckBox("hide both");
			fcch=new JCheckBox("hide face");
			edch=new JCheckBox("hide edge");
			
			edch.setSelected(true);
			fcch.setSelected(true);
			regch.setSelected(true);
			
			bApply=new Button("Apply");
			
			tfFaceTransp=new TextField("0");
			tfEdgeTransp=new TextField("0");
			Label lb1=new Label("fac transp.");
			Label lb2=new Label("edge transp.");
			JPanel p1=new JPanel(new GridLayout(1,2,0,0));
			JPanel p2=new JPanel(new GridLayout(1,2,0,0));
			p1.add(lb1);
			p1.add(tfFaceTransp);

			p2.add(lb2);
			
			p2.add(tfEdgeTransp);

		
		
		panel.add(bFaceColor);
		panel.add(bEdgeColor);
		panel.add(fcch);
		panel.add(edch);
		panel.add(p1);
			panel.add(p2);
		panel.add(bApply);
		panel.add(regch);
	//	panel.add(bClose);
	
		

			add(panel);
			
		
			
			fcch.addItemListener(this);
			edch.addItemListener(this);
			regch.addItemListener(this);
			
			bFaceColor.addActionListener(this);
			bEdgeColor.addActionListener(this);
	}
	 
	public void actionPerformed(ActionEvent e) {

	
	  if(e.getSource()==bFaceColor){
	 faceColor=JColorChooser.showDialog(new JFrame(), "Pick a Color",this.faceColor);
	}
	else if(e.getSource()==bEdgeColor){
		edgeColor=JColorChooser.showDialog(new JFrame(), "Pick a Color",this.edgeColor);

	}
		
		
	}
	
	
		public void itemStateChanged(ItemEvent e){
			if (e.getSource() == fcch) {
			if(fcch.isSelected())
				hideFace=true;
			else
				hideFace=false;
			}
			else if (e.getSource() == edch) {
				if(edch.isSelected())
					hideEdge=true;
				else
					hideEdge=false;
				}
			else if (e.getSource() == regch) {
				/*if(regch.isSelected())*/{
				fcch.setSelected(regch.isSelected());
				edch.setSelected(regch.isSelected());
				}
			
			
				}
					

		}



	public static void main(String[] args){

		EditReg eb=new EditReg();
		eb.setVisible(true);
		
	}
	 

}




