/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.jquantlib.QL;
import org.jquantlib.Settings;
import org.jquantlib.daycounters.Actual365Fixed;
import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.exercise.AmericanExercise;
import org.jquantlib.exercise.BermudanExercise;
import org.jquantlib.exercise.EuropeanExercise;
import org.jquantlib.exercise.Exercise;
import org.jquantlib.instruments.EuropeanOption;
import org.jquantlib.instruments.Option;
import org.jquantlib.instruments.Payoff;
import org.jquantlib.instruments.PlainVanillaPayoff;
import org.jquantlib.instruments.VanillaOption;
import org.jquantlib.methods.lattices.AdditiveEQPBinomialTree;
import org.jquantlib.methods.lattices.CoxRossRubinstein;
import org.jquantlib.methods.lattices.JarrowRudd;
import org.jquantlib.methods.lattices.Joshi4;
import org.jquantlib.methods.lattices.LeisenReimer;
import org.jquantlib.methods.lattices.Tian;
import org.jquantlib.methods.lattices.Trigeorgis;
import org.jquantlib.pricingengines.AnalyticEuropeanEngine;
import org.jquantlib.pricingengines.vanilla.BaroneAdesiWhaleyApproximationEngine;
import org.jquantlib.pricingengines.vanilla.BinomialVanillaEngine;
import org.jquantlib.pricingengines.vanilla.BjerksundStenslandApproximationEngine;
import org.jquantlib.pricingengines.vanilla.IntegralEngine;
import org.jquantlib.pricingengines.vanilla.JuQuadraticApproximationEngine;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDAmericanEngine;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDBermudanEngine;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDEuropeanEngine;
import org.jquantlib.processes.BlackScholesMertonProcess;
import org.jquantlib.quotes.Handle;
import org.jquantlib.quotes.Quote;
import org.jquantlib.quotes.SimpleQuote;
import org.jquantlib.termstructures.BlackVolTermStructure;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.termstructures.volatilities.BlackConstantVol;
import org.jquantlib.termstructures.yieldcurves.FlatForward;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.Date;
import org.jquantlib.time.Month;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;
import org.jquantlib.time.calendars.Target;
import Testing.Instrument; 
/**
 *
 * @author ds
 */
public class Portfolio{
	private double PortfolioPnL = 0;
	//holds the positions [bid , ask, amount] 
	public LinkedHashMap<String, double[]> positions = new LinkedHashMap<String, double[]>();
	    
    //in instrument: String type,String underline,double strike, java.uti.date Date
    public static LinkedHashMap<String, Instrument> PricingData = new LinkedHashMap();

    //holds the data for all the securities [bid, ask, strike, time]
	public LinkedHashMap<String,double[]> Data = new LinkedHashMap<String, double[]>(); 
	//private HashMap<String,double[]> Data = new HashMap<String, double[]>(); 
	
	final static Date todaysDate = new Date(01, Month.March, 2013);
	final static Date settlementDate = new Date(03, Month.March, 2013);
    final static DayCounter dayCounter = new Actual365Fixed();
    final static Calendar calendar = new Target();
    final static double riskFreeRate = 0.02037;
    private double volGuess = 0.2;  	
	   
	//values [bid,ask, amount]
	public Portfolio(){}
	
	
	// values = [spot, amount]
	public void AddPosition(String ticker, double[] values){
		double[] nValues = new double[2];
		if(positions.containsKey(ticker)){
			if(positions.get(ticker)[1] + values[1] != 0){
				double pAmount = positions.get(ticker)[1];
				double pPrice = positions.get(ticker)[0];
				
				nValues[0] = ((pPrice * pAmount) + (values[0] * values[1]))/(pAmount + values[1]);
				nValues[1] = pAmount + values[1]; 
				positions.put(ticker,nValues); 
			} else{
				positions.remove(ticker);
			} 
		} else{
			positions.put(ticker, values);
		}
	}
	
	//values = [bid, ask, strike, time]
	
	public void UpdatePrice(String ticker, double[] values){
		Data.put(ticker, values);
	}
        
    public static java.util.Date getNearestDate(String ticker, java.util.Date currentDate) {
        long minDiff = -1, currentTime = currentDate.getTime();
        java.util.Date minDate = null;
        for (java.util.Date date : optionsData.spotData.get(ticker).keySet()) {
          long diff = Math.abs(currentTime - date.getTime());
          if ((minDiff == -1) || (diff < minDiff)) {
            minDiff = diff;
            minDate = date;
          }
        }
        return minDate;
    }

	public static VanillaOption Contract(String ticker){
	       
	                
	    Instrument cOption = PricingData.get(ticker);
	    new Settings().setEvaluationDate(todaysDate);
	    java.util.Date lastPriced = cOption.lastPriced();
	
	    double spotPrice = optionsData.spotData.get(cOption.getSpotName()).get(getNearestDate(cOption.getSpotName(),lastPriced));
	    final double dividendYield = 0;
	
	
	    
	    final Handle<YieldTermStructure> flatDividendTS = new Handle<YieldTermStructure>(new FlatForward(settlementDate, dividendYield, dayCounter));
	    final Handle<YieldTermStructure> flatTermStructure = new Handle<YieldTermStructure>(new FlatForward(settlementDate, riskFreeRate, dayCounter));
	    final Handle<BlackVolTermStructure> flatVolTS = new Handle<BlackVolTermStructure>(new BlackConstantVol(settlementDate, calendar, IV(ticker), dayCounter));
	
	    Option.Type type = cOption.getType();
	
	    
	    final Exercise americanExercise = new AmericanExercise(settlementDate, cOption.getMaturity());
	    
	    Handle<Quote> underlyingH = new Handle<Quote>(new SimpleQuote(spotPrice));
	    
	    
	    System.out.println("the strike is:" + cOption.getStrike());
	    System.out.println("the spot is: " +spotPrice);
	     final Payoff payoff = new PlainVanillaPayoff(type, cOption.getStrike());
	
	    BlackScholesMertonProcess bsmProcess = new BlackScholesMertonProcess(underlyingH, flatDividendTS, flatTermStructure, flatVolTS);
	    VanillaOption americanOption = new VanillaOption(payoff, americanExercise);
	    americanOption.setPricingEngine(new BjerksundStenslandApproximationEngine(bsmProcess));
	    return americanOption;         
	    }
	
	public static VanillaOption IVDummy(String ticker,double volGuess1){
	       
	                
	    Instrument cOption = PricingData.get(ticker);
	    new Settings().setEvaluationDate(todaysDate);
	    java.util.Date lastPriced = cOption.lastPriced();
	
	    double spotPrice = optionsData.spotData.get(cOption.getSpotName()).get(getNearestDate(cOption.getSpotName(),lastPriced));
	    final double dividendYield = 0;
	
	
	    
	    final Handle<YieldTermStructure> flatDividendTS = new Handle<YieldTermStructure>(new FlatForward(settlementDate, dividendYield, dayCounter));
	    final Handle<YieldTermStructure> flatTermStructure = new Handle<YieldTermStructure>(new FlatForward(settlementDate, riskFreeRate, dayCounter));
	    final Handle<BlackVolTermStructure> flatVolTS = new Handle<BlackVolTermStructure>(new BlackConstantVol(settlementDate, calendar, volGuess1, dayCounter));
	
	    Option.Type type = cOption.getType();
	    
	    final Exercise americanExercise = new AmericanExercise(settlementDate, cOption.getMaturity());
	    
	    Handle<Quote> underlyingH = new Handle<Quote>(new SimpleQuote(spotPrice));
	    
	    
	    System.out.println("the strike is:" + cOption.getStrike());
	    System.out.println("the spot is: " +spotPrice);
	     final Payoff payoff = new PlainVanillaPayoff(type, cOption.getStrike());
	
	    BlackScholesMertonProcess bsmProcess = new BlackScholesMertonProcess(underlyingH, flatDividendTS, flatTermStructure, flatVolTS);
	    VanillaOption americanOption = new VanillaOption(payoff, americanExercise);
	    americanOption.setPricingEngine(new BjerksundStenslandApproximationEngine(bsmProcess));
	    return americanOption;         
	    }
	

    public static double IV(String ticker){
        double error = 0.001;
        double Yi = 0;
        double P = 1;
        double Vega = 2;
        double volatility = 0.2;
        
        Instrument cOption = PricingData.get(ticker);
        P = cOption.getLastPrice();
        System.out.println(ticker+" "+volatility);
        while(Math.abs(Yi - P) > error){
        	try{
        		VanillaOption test = IVDummy(ticker,volatility);
                Yi = test.NPV();
                
                Vega = test.vega();
                volatility = volatility - ((Yi - P)/Vega);
        	}catch(Exception e){
        		volatility = 0.2;
        		VanillaOption test = IVDummy(ticker,volatility);
                Yi = test.NPV();
                
                Vega = test.vega();
                volatility = volatility - ((Yi - P)/Vega);
        	}
        }
        
        return volatility;
    }
    
	public double PortfolioDelta(){
		double pdelta = 0;
		for(String ticker: positions.keySet()){
			Instrument details = PricingData.get(ticker);
			if(details.isOption())
				pdelta += Contract(ticker).delta() * positions.get(ticker)[1];
			else pdelta += positions.get(ticker)[1];
		}
		return pdelta; 
	}
	
	public double PortfolioGamma(){
		double pgamma = 0; 
		for(String ticker: positions.keySet()){
			Instrument details = PricingData.get(ticker);
			if(details.isOption())
				pgamma += Contract(ticker).gamma() * positions.get(ticker)[1];
		}
		return pgamma; 
	}
	
	public double PortfolioVega(){
		double pvega = 0;
		for(String ticker: positions.keySet()){
			Instrument details = PricingData.get(ticker);
			if(details.isOption())
				pvega += Contract(ticker).vega() * positions.get(ticker)[1];
		}
		return pvega; 
	}
	
	public double PositionPnL(String ticker){
		if(positions.containsKey(ticker)){
			int amount = (int)positions.get(ticker)[1];
			double Cprice = this.BidAsk(ticker, -amount);
			double Bprice = positions.get(ticker)[0];
			return (Cprice - Bprice) * (int)amount;}
		else return 0;
	}
	
	public double Pnl(){
		double P = 0;
		for(String ticker: positions.keySet()){
			P += PositionPnL(ticker);
		}
		P += this.getPnL();
		return P; 
	}
	
	public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	public Boolean p(double x,double y){
		if(x > 0 && y >0) return true;
		if(x < 0 && y <0) return true;
		if(x < 0 && y >0) return false;
		if(x > 0 && y < 0)return false; 
		else return true; 
	}

	public int[] Sort(double[] num1)
	{
		 double[] num = num1.clone();
	     int j;
	     boolean flag = true;   // set flag to true to begin first pass
	     double temp;
	     int temp1;
	     //holding variable
	     int[] index = {0,1,2,3,4,5,6,7,8,9}; 
	     while ( flag )
	     {
	            flag= false;    //set flag to false awaiting a possible swap
	            for( j=0;  j < num.length -1;  j++ )
	            {
	                   if ( Math.abs(num[ j ]) < Math.abs(num[j+1]) )   // change to > for ascending sort
	                   {
	                           temp = num[ j ];                //swap elements
	                           num[ j ] = num[ j+1 ];
	                           num[ j+1 ] = temp;
	                           
	                           temp1 = index[j];
	                           index[j] = index[j+1];
	                           index[j+1] = temp1;
	                           flag = true;              //shows a swap occurred  
	                  } 
	            } 
	      }
	     return index; 
	} 
	
	public void OptionsSort(ArrayList<String> options)
	{
	     int j;
	     boolean flag = true;   // set flag to true to begin first pass
	     String temp;
	     while ( flag )
	     {
	            flag= false;    //set flag to false awaiting a possible swap
	            for( j=0;  j < options.size() -1;  j++ )
	            {
	                   if (Data.get(options.get(j))[2] >= Data.get(options.get(j+1))[2])   // change to > for ascending sort
	                   {
	                	   if((Data.get(options.get(j))[2] == Data.get(options.get(j+1))[2])){
	                		   if((Data.get(options.get(j))[3] == Data.get(options.get(j+1))[3])){
	                			   temp = options.get(j);
	                			   options.set(j, options.get(j+1));
	                			   options.set(j+1, temp);
	                			   flag = true;
	                		   }
	                	   } else{
	                		   temp = options.get(j);
                			   options.set(j, options.get(j+1));
                			   options.set(j+1, temp);
                			   flag = true;	
	                	   }
	                   } 
	           } 
	     } 
	} 
	
	public int HedgeDelta(){return (int) -this.PortfolioDelta();}
        
	public double getAmount(String ticker){
		if(positions.containsKey(ticker))
			return positions.get(ticker)[1];
		else return 0;
	}
	
	public double getPnL(){return PortfolioPnL;}
	
    public void LiquidatePositions(){
		PortfolioPnL = Pnl();
	}
	
	public double getBid(String ticker){
		if(Data.containsKey(ticker))
			return Data.get(ticker)[0];
		else return 0;
	}
	
	public double getAsk(String ticker){
		if(Data.containsKey(ticker))
			return Data.get(ticker)[1];
		else return 0;
		
	}
	
	public  double BidAsk(String ticker, int amount){
		double value = 0;
		if(amount > 0){
			value =  this.getAsk(ticker);
		}
		else if(amount <= 0){
			value = this.getBid(ticker);
		}
		return value;
	}
	
	public int getMinValue(ArrayList<Double> numbers){  
		  double minValue = (numbers.get(0));
		  int minindex = 1;
		  for(int i=1;i<numbers.size();i++){  
		    if((numbers.get(i)) < minValue){  
		      minValue = (numbers.get(i));
		      minindex = i;
		    }  
		  }  
		  return minindex;  
	}  
	
	public int getMaxValue(ArrayList<Double> numbers){  
		  double maxValue = (numbers.get(0));
		  int maxindex = 0;
		  for(int i=1;i<numbers.size();i++){  
		    if((numbers.get(i)) > maxValue){  
		      maxValue = (numbers.get(i));
		      maxindex = i;
		    }  
		  }  
		  return maxindex;  
	}  
	
	public int GetPortfoliiSize(){
		return positions.size();
	}
}
