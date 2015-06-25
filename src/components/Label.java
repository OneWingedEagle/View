package components;

import java.awt.Font;

import javax.swing.JLabel;
public class Label extends JLabel {
	Font tfFont = new Font("Arial", 0, 13);
	public Label(){
	super();	
	setFont(tfFont);
	}
	public Label(String st, int alignment ){
		super();	
		setText(st);
		setHorizontalAlignment(alignment);
		setFont(tfFont);
		}
	
	public Label(String str){
		super();	
		setText(str);
		setFont(tfFont);
		setHorizontalAlignment(RIGHT);
		}
}
