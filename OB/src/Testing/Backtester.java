package Testing;

import Testing.optionsData;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;
import java.io.IOException;
import java.text.ParseException;


public class Backtester extends optionsData{
    public static Draw charts = null;
    public static Logs log =   new Logs();

    public static JFrame mainFrame = new JFrame();
    public static JPanel chart, logs;

    //(JPanel) charts.Price_Chart(),
    //(JPanel) log.Trade_Log()
    public static void main(String[] args) throws IOException, ParseException {
        Testing.optionsData.runBackTest("AAPLD0513430.00", "AAPL");
        System.out.println("The runBackTest is finished");
    }

    public static void update(String i){
        log.update(i);
        
        mainFrame.repaint();
    }

    public static void initMainFrame(){
    	menu();
        chart.setBackground(Color.BLACK);
        mainFrame.getContentPane().add(chart);
        mainFrame.getContentPane().add(logs);
        mainFrame.setLayout(new GridLayout(2,1));
        mainFrame.setMinimumSize(new Dimension(800, 500));
        mainFrame.getContentPane().setBackground(Color.BLACK);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static void menu(){
        JMenuBar menuBar=new JMenuBar();
        JMenu File = new JMenu("File");
        JMenu algos = new JMenu("Algo");
        JMenu Chart = new JMenu("Charts");
        JMenu Risk = new JMenu("Risk Parameters");
        JMenu Greeks = new JMenu("Greeks");
        JMenu Surfaces = new JMenu("Surfaces");
        File.setMnemonic(KeyEvent.VK_F);

        menuBar.add(File);     menuBar.add(Chart);
        menuBar.add(Risk);     menuBar.add(Greeks);
        menuBar.add(Surfaces); menuBar.add(algos);

        mainFrame.setJMenuBar(menuBar);	
    }
}