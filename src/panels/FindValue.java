package panels;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import components.*;

public class FindValue extends JFrame implements ActionListener{

	
	public TextField[] tfPointCoord, tfValue;
	
	public Button bGetValue,bAply;
	public JComboBox pointValue;

	 public FindValue() {
		 
			
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle(" Find ");
			setSize(200,300);
			
			String[] valueOption = { "None", "B", "Je", "F" };
			this.pointValue = new JComboBox(valueOption);
			this.pointValue.setSelectedIndex(0);
			this.bGetValue = new Button("Find");

			JPanel panel = new JPanel(new GridLayout(3,1,10,10));
			panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			add(panel);
			
			JPanel findPanel = new JPanel(new GridLayout(3, 2));

			for (int k = 0; k < 2; k++)
				findPanel.add(new Label());
			findPanel.add(this.pointValue);
			findPanel.add(this.bGetValue);
			for (int k = 0; k < 2; k++)
				findPanel.add(new Label());

			this.tfPointCoord = new TextField[3];
			this.tfValue = new TextField[3];
			JPanel valuePanel = new JPanel(new GridLayout(4, 1));
			valuePanel.add(new Label("Value :",Label.LEFT));

			JPanel coordPanel = new JPanel(new GridLayout(4, 1));
			coordPanel.add(new Label("At Point:",Label.LEFT));

			for (int k = 0; k < 3; k++) {
				this.tfPointCoord[k] = new TextField("0.0");
				this.tfPointCoord[k].setPreferredSize(new Dimension(50, 25));
				this.tfValue[k] = new TextField();
				this.tfValue[k].setEditable(false);
				this.tfValue[k].setBackground(Color.white);
				this.tfValue[k].setPreferredSize(new Dimension(80, 25));

				coordPanel.add(this.tfPointCoord[k], JPanel.RIGHT_ALIGNMENT);
				valuePanel.add(this.tfValue[k]);
			}

			panel.add(findPanel);
			panel.add(coordPanel);
			panel.add(valuePanel);
		
			
	}
	 
	public void actionPerformed(ActionEvent e) {
		/*if(e.getSource()==translate){
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
		*/
	}
	

	public static void main(String[] args){

		FindValue eb=new FindValue();
		eb.setVisible(true);
		
	}
	 

}




