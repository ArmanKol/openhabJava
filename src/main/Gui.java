package main;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Gui {
	private final Item item = new Item(); // item.getAllItems() gebruikt in createLabels
	private final JFrame mainFrame = new JFrame(); // Maakt de GUI scherm aan
	private final GridLayout layout = new GridLayout(0,3); //Een layout van 2 columnen en oneindig rijen
	public Gui() {
		this.mainFrame.setLayout(this.layout);
		
		createLabels();
		
		this.mainFrame.setSize(800,800);
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setVisible(true);
	}
	
	/*
	 * Deze methode maakt voor elke item dat in openhab geconfigureerd is een label aan. De label bestaat uit de item naam en de knoppen ON en OFF.
	 */
	private void createLabels() {
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
