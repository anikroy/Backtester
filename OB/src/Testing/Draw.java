package Testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jquantlib.exercise.EuropeanExercise;

public class Draw extends optionsData{  
	private static JPanel Candle=new JPanel();
	private static SimpleDateFormat df= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",Locale.ENGLISH);
	private static ArrayList        dataItemsO = new ArrayList(),
								    dataItemsU = new ArrayList();
	
    public Draw(){
        super();
    }
    
    //String[] gData = {date,open, high, low, adjClose, volume}
    public static void Price_Chart(String symbol, String underlying) throws ParseException{
        JFreeChart OptChart = Chart(symbol, "O"), 
        		   PChart   = Chart(underlying, "E");
        Candle.removeAll();
        if(!Backtester.init){Candle.setLayout(new GridLayout(1,0));}
        Candle.add(new ChartPanel(OptChart), BorderLayout.WEST);
        Candle.add(new ChartPanel(PChart), BorderLayout.EAST);
        Backtester.chart = Candle;
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static JFreeChart Chart(String symbol, String dType) throws ParseException{
	    String[] values = dType.equals("O") ? OptionsData.get(symbol).get(OptionsData.get(symbol).size()-1) : EquityData.get(symbol).get(EquityData.get(symbol).size()-1);
	    //date, open, high, low, adjClose, volume
	    double open = Double.parseDouble(values[1]),
	           high = Double.parseDouble(values[2]),
	           low = Double.parseDouble(values[3]),
	           adjClose = Double.parseDouble(values[4]),
	           volume = Double.parseDouble(values[5]);
	
	    if (dType.equals("O")){
	    	Date date = df.parse(values[0]);
		    OHLCDataItem item = new OHLCDataItem(date, open, high, low, adjClose, volume);
	    	dataItemsO.add(item); return update(dType);
	    }else {
	    	Date date = df.parse(values[0].substring(0, 4)+"/"+
				 			     values[0].substring(4, 6)+"/"+
							     values[0].substring(6, 8)+" "+ values[0].substring(8, values[0].length()));
		    OHLCDataItem item = new OHLCDataItem(date, open, high, low, adjClose, volume);
	    	dataItemsU.add(item); return update(dType);
	    }
    }
    
    private static JFreeChart update(String dType) throws ParseException{    
    	
        Collections.reverse(dType.equals("O") ? dataItemsO : dataItemsU);
        OHLCDataItem[] data = (OHLCDataItem[]) (dType.equals("O") ? dataItemsO : dataItemsU).toArray(new OHLCDataItem[(dType.equals("O") ? dataItemsO : dataItemsU).size()]);
        OHLCDataset dataset = new DefaultOHLCDataset("MSFT", data);
        
        JFreeChart chart=ChartFactory.createCandlestickChart("MSFT", "Time", "Price", dataset, false);
        chart.setBackgroundPaint(Color.black);
        chart.setBorderPaint(Color.green);
        chart.setBorderVisible(true);
        
        XYPlot plot=(XYPlot)chart.getPlot();
        plot.setBackgroundPaint(Color.black);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.CYAN);
        plot.setRangeGridlinePaint(Color.CYAN);

        ((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(false);
        ((DateAxis)plot.getDomainAxis()).setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
        ((CandlestickRenderer)plot.getRenderer()).setDrawVolume(true);
        return chart;
    }
}