package main;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Gui {
	private final Item item = new Item(); // item.getAllItems() used in createLabels
	private final JFrame mainFrame = new JFrame(); // Creates the GUI window

	public Gui() {
		final GridLayout layout = new GridLayout(0,3);
		this.mainFrame.setLayout(layout);
		
		createLabels();
		
		this.mainFrame.setSize(800,800);
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setVisible(true);
	}
	
	
	/*
	 * This method creates all the labels for all the items that is configured in openHab. The label has the item name and a on and off button.
	 */
	public void createLabels() {
		JLabel text;
		JButton buttonOn;
		JButton buttonOff;
		
		for(Item item : item.getAllItems()) {
			text = new JLabel(item.getName());
			buttonOn = new JButton("ON");
			buttonOff = new JButton("OFF");
			
			buttonOn.addActionListener(new ActionListener(){  
				public void actionPerformed(ActionEvent event){  
					item.changeState(item.getName(), "ON");
		        }  
		    });
			
			buttonOff.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){  
					item.changeState(item.getName(), "OFF");
		        }  
		    });
			
			this.mainFrame.add(text);
			this.mainFrame.add(buttonOn);
			this.mainFrame.add(buttonOff);
		}
	}
}
