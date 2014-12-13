
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

/**
 * Calculates equity option values with a number of methods
 *
 * @see http://quantlib.org/reference/_equity_option_8cpp-example.html
 *
 * @author Richard Gomes
 */
public class EquityOptions implements Runnable {

   
    @Override
    public void run() {

        QL.info("::::: " + this.getClass().getSimpleName() + " :::::");

        
        

        // set up dates
        final Calendar calendar = new Target();
        final Date todaysDate = new Date(15, Month.May, 1998);
        final Date settlementDate = new Date(17, Month.May, 1998);
        new Settings().setEvaluationDate(todaysDate);

        // our options
        final Option.Type type = Option.Type.Put;
        final double strike = 40.0;
        final double underlying = 36.0;
        /*@Rate*/final double riskFreeRate = 0.06;
        final double volatility = 0.2;
        final double dividendYield = 0.00;


        final Date maturity = new Date(17, Month.May, 1999);
        final DayCounter dayCounter = new Actual365Fixed();

        // define line formatting
        //              "         1         2         3         4         5         6         7         8         9"
        //              "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
        //              "                            Method      European      Bermudan      American";
        //              "================================== ============= ============= ============="
        //              "12345678901234567890123456789012345678901234 123.567890123 123.567890123 123.567890123";
        final String fmt    = "%34s %13.9f %13.9f %13.9f\n";
        final String fmttbd = "%34s %13.9f %13.9f %13.9f  (TO BE DONE)\n";

        // write column headings
        //                 "         1         2         3         4         5         6         7         8"
        //                 "12345678901234567890123456789012345678901234567890123456789012345678901234567890"
        System.out.println("                            Method      European      Bermudan      American");
        System.out.println("================================== ============= ============= =============");

        // Define exercise for European Options
        final Exercise europeanExercise = new EuropeanExercise(maturity);

        // Define exercise for Bermudan Options
		final int bermudanForwards = 4;
		final Date[] exerciseDates = new Date[bermudanForwards];
		for (int i = 1; i <= bermudanForwards; i++) {
            exerciseDates[i-1] = settlementDate.add(new Period(3*i, TimeUnit.Months));
        }
	    final Exercise bermudanExercise = new BermudanExercise(exerciseDates);

        // Define exercise for American Options
        final Exercise americanExercise = new AmericanExercise(settlementDate, maturity);

        // bootstrap the yield/dividend/volatility curves
        final Handle<Quote> underlyingH = new Handle<Quote>(new SimpleQuote(underlying));
        final Handle<YieldTermStructure> flatDividendTS = new Handle<YieldTermStructure>(new FlatForward(settlementDate, dividendYield, dayCounter));
        final Handle<YieldTermStructure> flatTermStructure = new Handle<YieldTermStructure>(new FlatForward(settlementDate, riskFreeRate, dayCounter));
        final Handle<BlackVolTermStructure> flatVolTS = new Handle<BlackVolTermStructure>(new BlackConstantVol(settlementDate, calendar, volatility, dayCounter));
        final Payoff payoff = new PlainVanillaPayoff(type, strike);

        final BlackScholesMertonProcess bsmProcess = new BlackScholesMertonProcess(underlyingH, flatDividendTS, flatTermStructure, flatVolTS);

        // European Options
        final VanillaOption europeanOption = new EuropeanOption(payoff, europeanExercise);

        // Bermudan options (can be thought as a collection of European Options)
        final VanillaOption bermudanOption = new VanillaOption(payoff, bermudanExercise);

        // American Options
        final VanillaOption americanOption = new VanillaOption(payoff, americanExercise);


        // Analytic formulas:

        // Black-Scholes for European
        String method = "Black-Scholes";
        europeanOption.setPricingEngine(new AnalyticEuropeanEngine(bsmProcess));
        System.out.printf(fmt, method, europeanOption.NPV(), Double.NaN, Double.NaN );

        // Barone-Adesi and Whaley approximation for American
        method = "Barone-Adesi/Whaley";
        americanOption.setPricingEngine(new BaroneAdesiWhaleyApproximationEngine(bsmProcess));
        System.out.printf(fmt, method, Double.NaN, Double.NaN, americanOption.NPV() );

        // Bjerksund and Stensland approximation for American
        method = "Bjerksund/Stensland";
        americanOption.setPricingEngine(new BjerksundStenslandApproximationEngine(bsmProcess));
        System.out.printf(fmt, method, Double.NaN, Double.NaN, americanOption.NPV() );

        // Ju Quadratic approximation for American
        method = "Ju Quadratic";
        americanOption.setPricingEngine(new JuQuadraticApproximationEngine(bsmProcess));
        System.out.printf(fmt, method, Double.NaN, Double.NaN, americanOption.NPV() );

        // Integral
        method = "Integral";
        europeanOption.setPricingEngine(new IntegralEngine(bsmProcess));
        System.out.printf(fmt, method, europeanOption.NPV(), Double.NaN, Double.NaN );

        int timeSteps = 801;

        // Binomial method
       
        

    }

}