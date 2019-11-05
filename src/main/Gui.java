package main;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Gui {
	private final Item item = new Item(); // item.getAllItems() used in createLabels @author Arman Koldaguc
	private final JFrame mainFrame = new JFrame(); // Creates the GUI window
	private static final Logger LOG = Logger.getLogger(Gui.class.getName()); // Used for logging
	
	public Gui() {
		final GridLayout layout = new GridLayout(0,3);
		this.mainFrame.setLayout(layout);
		
		createLabels();
		
		this.mainFrame.setSize(800,800);
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setVisible(true);
	}
	
	
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
