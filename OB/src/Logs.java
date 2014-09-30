import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Logs {
	private static JTextField TradeLog=new JTextField();
	private JPanel panel=new JPanel();
	
	public Component Trade_Log(){
		panel.setBackground(Color.black);
	    
	    JLabel header = new JLabel("Bigger text");
	    header.setSize(new Dimension(java.awt.Frame.MAXIMIZED_HORIZ, 
	    									java.awt.Frame.MAXIMIZED_VERT));
	    header.setFont(new Font("Arial", 0, 12));
	    header.setAutoscrolls(true);
	    header.setBackground(Color.white);
	    
	    panel.add(header);
	    
		TradeLog.setPreferredSize(new Dimension(java.awt.Frame.MAXIMIZED_HORIZ, 
												java.awt.Frame.MAXIMIZED_VERT));
		
		TradeLog.setEditable(false);
		TradeLog.setForeground(Color.BLUE);
		TradeLog.setBackground(Color.BLUE);
		
		
		
		return TradeLog;		
	}

}
