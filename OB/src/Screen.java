import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.AbstractDocument.Content;
import java.awt.*;

public class Screen{
	private JPanel panel=new JPanel(),
			       LogPanel=new JPanel(),
			       CandlePanel=new JPanel();
	
	public Content panel(){		
		LogPanel.add((new Logs()).Trade_Log()); 
		
		CandlePanel.add((new Draw()).Price_Chart());
		CandlePanel.setLayout(new BoxLayout(CandlePanel, BoxLayout.Y_AXIS));
		
		panel.setBackground(Color.black);
		panel.setLayout(new GridLayout(4,4));
		
		return (Content) panel;
	}
	
}
