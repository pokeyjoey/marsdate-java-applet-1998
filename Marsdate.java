/* TITLE:  Marsdate
 * AUTHOR: Jeffery R Roche
 * DATE  : 02/20/1998
 *
 * PURPOSE:
 * This program calculates the current date on Mars according to a calendar
 * invented by Dr. Robert Zubrin.
 * 
 */
import java.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.text.*;

class JulianTime
{
/* Class: JulianTime, v. 0.99b3 by Robert B. Schmunk, 1997-09-05."
*/
	public static final double JD_2000 = 2451545.;

	public long   unixEpoch = 0;
	public double dayUTC = 0.;
	public double dayTT = 0.;
	public double delta2000 = 0.;
	public double utc2tt = 0.;


	public JulianTime ()
	{
	SetJulianTime (System.currentTimeMillis ());
	}

	public JulianTime (long inTimeMillis)
	{
	SetJulianTime (inTimeMillis);
	}

	public void SetJulianTime (long inTimeMillis)
	{
	unixEpoch  = inTimeMillis;
	dayUTC     = JulianDay (inTimeMillis);

	// Equation A. 18.
	delta2000  = dayUTC - JD_2000; // interim value only
	utc2tt = 66. + (0.95 + 0.0035 * (delta2000 / 365.25)) * (delta2000 / 365.25);

	dayTT      = JulianDay (inTimeMillis + (long)(utc2tt * 1000.));
	delta2000  = dayTT - JD_2000;
	}

	/* Returns Julian day for specified time using formula from Numerical
	Recipes.
	*/
	public double JulianDay (long inTimeMillis)
	{
	Date theDate   = new Date (inTimeMillis);
	long theYear   = theDate.getYear ()    + 1900;
	long theMonth  = theDate.getMonth ()   + 1;
	long theDay    = theDate.getDate ();
	long theHour   = theDate.getHours ();
	long theMinute = theDate.getMinutes () +
	theDate.getTimezoneOffset ();
	long theSecond = theDate.getSeconds ();

	long jy = theYear;
	long jm;
	if (theMonth > 2)
	   jm = theMonth + 1;
	else
	{
	   --jy;
	   jm = theMonth + 13;
	}
	long julianDay = (long)(365.25 * jy) + (long)(30.6001 * jm)+ theDay + 1720995;
	long ja = (long)(0.01 * jy);
	julianDay += 2 - ja + (long)(0.25 * ja);

	double fraction = (((theHour - 12.) * 3600.) + (theMinute *60.) + theSecond) / (24. * 3600.);

	return (double)(julianDay) + fraction;
	}
}


class MarsOrbit extends JulianTime
{
/* Class: MarsOrbit, v. 0.99b12 by Robert B. Schmunk, 1997-11-07."

For use in Pathfinder LTST Clock, etc. Equation numbers are all in
reference to Allison, M. 1997. Geophys. Res. Lett. 24, 1967-1970.
*/
	static final double TIME_RATIO = 1.02749125;

	static final double PF_LONGITUDE = 33.52;

	private double meanAnomaly = 0.;
	private double alphaFMS = 0.;
	private double ellSRad = 0.;
	private double ellSDeg = 0.;
	private double alphaS = 0.;
	private double deltaS = 0.;
	private double rM = 0.;
	private double lambdaM = 0.;
	private double equationOfTime = 0.;
	private double primeMeridian = 0.;

	public MarsOrbit ()
	{
	super (System.currentTimeMillis ());
	CalculateOrbit ();
	}

	public MarsOrbit (long inTimeMillis)
	{
	super (inTimeMillis);
	CalculateOrbit ();
	}

	public void CalculateOrbit ()
	{
	// Equation A. 1.
	meanAnomaly = (19.41 + 0.5240212 * delta2000) *
	MathStuff.RAD_PER_DEG;

	// Equation A. 2.
	alphaFMS = 270.39 + 0.5240384 * delta2000;

	// Equation A. 3.
	ellSDeg = alphaFMS + (10.691 + 3.7e-7 * delta2000) * Math.sin(meanAnomaly)
	  + 0.623 * Math.sin (2. * meanAnomaly)+ 0.050 * Math.sin (3. * meanAnomaly)
	  + 0.005 * Math.sin (4. * meanAnomaly);
	ellSDeg = MathStuff.FitInRange (ellSDeg, 360.);
	ellSRad = ellSDeg * MathStuff.RAD_PER_DEG;

	// Equation A. 4.
	alphaS = ellSDeg - 2.860 * Math.sin (2. * ellSRad)
	   + 0.071 * Math.sin (4. * ellSRad) - 0.002 * Math.sin (6. * ellSRad);

	// Equation A. 5.
	deltaS = MathStuff.DEG_PER_RAD * Math.asin (0.4256 * Math.sin (ellSRad)) + 0.25 * Math.sin (ellSRad);

	// Equation A. 6.
	rM     = 1.5236 * (1.00436 - 0.09309 * Math.cos (meanAnomaly) - 0.00436 * Math.cos (2. * meanAnomaly)- 0.00031 * Math.cos (3. * meanAnomaly));

	// Equation A. 7.
	lambdaM = ellSDeg + 85.06 + 0.000029 * delta2000;
	lambdaM = MathStuff.FitInRange (lambdaM, 360.);

	// Equation A. 10.
	equationOfTime = alphaFMS - alphaS;
	// equationOfTime =  2.860 * Math.sin (2. * ellSRad)
	// - 0.071 * Math.sin (4. * ellSRad)
	// + 0.002 * Math.sin (6. * ellSRad)
	// - ( (10.691 + 3.7e-7 * delta2000) * Math.sin (meanAnomaly)
	// + 0.623 * Math.sin (2. * meanAnomaly)
	// + 0.050 * Math.sin (3. * meanAnomaly)
	// + 0.005 * Math.sin (4. * meanAnomaly) );

	// Equation A. 11.
	primeMeridian = 72.3003 + 350.8919851 * (dayTT - 2444239.5);
	}

	public double GetLMST (double longitude,boolean useEq12)
	{
	// Equation A. 12 *or* 13.
	double mst0;
	double mst;
	if (useEq12)
	{
	  mst0   = (primeMeridian - alphaFMS + 180.) / 360.;
	  mst    = (mst0 - (long)(mst0)) * 24.;
	}
	else
	{
	  mst0   = (dayTT - 2440692) / TIME_RATIO;
	  mst    = (mst0 - (long)(mst0)) * 24.;
	}
	// Equation A. 14.
	double lmst  = mst - longitude * (24. / 360.);
	return MathStuff.FitInRange (lmst, 24.);
	}

	public double GetLTST (double longitude,boolean useEq12)
	{
	// Equation A. 15.
	double ltst = GetLMST (longitude, useEq12) + equationOfTime/ 15.;
	return MathStuff.FitInRange (ltst, 24.);
	}


}

class MathStuff
{
	public static double RAD_PER_DEG = Math.PI / 180.;
	public static double DEG_PER_RAD = 180. / Math.PI;

	static double FitInRange (double inNumber,double inMaximum)
	{
	if (inNumber < 0)
	   inNumber += inMaximum * (1. -(double)(long)(inNumber / inMaximum));
	if (inNumber > inMaximum)
	   return inNumber % inMaximum;
	else
	   return inNumber;
	}
}

public class marsdate extends java.applet.Applet implements Runnable {

    Thread dateThread = null;

    Font   f1 = new Font("Courier", Font.BOLD, 18);

    double DaysEarth,
	   AlongEarth,
           EarthDate,
           MarsToday,
	   DayMars,
	   DaysMars,
           DaysMars1,
	   AlongMars,
	   MarsYear,
           DaysAvg,
           lmst,           // Local Mars Solar Time 
           Longitude; ;

    int    CurrentMonth,
	   CurrentDay,
	   CurrentYear,
	   DaysInYear,
           SolsInYear,
           Month,
	   Date,
	   Year,
           hours, 
	   minutes,
	   seconds,        // Holds time values for display
           strlen,
                x;               ;

    long   MDay_long,
	   MYear_long,
	   lmsthr,         // LMST hour
	   lmstmi,         // LMST minute
	   lmstse;         // LMST second 	

    String MonthMars,
           SDate;

   static final int applet_width = 170;

    // Method Computes Martian Month
    public String Month(double DaysPassed)
	     { String Month;

	       Month  = " ";
       
  	       if ((DaysPassed >= 1) && (DaysPassed <= 61)) {
            		Month = "Gemini";
  	       } 
 	       else if ((DaysPassed >= 62) && (DaysPassed <= 126)) {
            		Month = "Cancer";
  	       } 
  	       else if ((DaysPassed >= 127) && (DaysPassed <= 192)) {
            		Month = "Leo";
  	       } 
  	       else if ((DaysPassed >= 193) && (DaysPassed <= 257)) {
            		Month = "Virgo";
  	       } 
  	       else if ((DaysPassed >= 258) && (DaysPassed <= 317)) {
            		Month = "Libra";
  	       } 
  	       else if ((DaysPassed >= 318) && (DaysPassed <= 371)) {
            		Month = "Scorpius";
  	       }  
  	       else if ((DaysPassed >= 372) && (DaysPassed <= 421)) {
            		Month = "Sagittarius";
  	       } 
  	       else if ((DaysPassed >= 422) && (DaysPassed <= 468)) {
            		Month = "Capricorn";
  	       } 
  	       else if ((DaysPassed >= 469) && (DaysPassed <= 514)) {
            		Month = "Aquarius";
	       } 
  	       else if ((DaysPassed >= 515) && (DaysPassed <= 562)) {
            		Month = "Pisces";
  	       } 
  	       else if ((DaysPassed >= 563) && (DaysPassed <= 613)) {
            		Month = "Aries";
  	       } 
  	       else if (DaysPassed >= 614) {
            		Month = "Taurus";
  	       } 
 
               return (Month);
        }

    // Method Computes Martian Day of Month
    public double DayofMonth(double DaysPassed)
	     { double MonthDate;

	      MonthDate = 0;

  	      if ((DaysPassed >= 1) && (DaysPassed <= 61)) {
            		MonthDate   = DaysPassed;
  	      } 
 	      else if ((DaysPassed >= 62) && (DaysPassed <= 126)) {
            		MonthDate   = DaysPassed - 62 + 1;
  	      } 
  	      else if ((DaysPassed >= 127) && (DaysPassed <= 192)) {
            		MonthDate   = DaysPassed - 127 + 1;
  	      } 
  	      else if ((DaysPassed >= 193) && (DaysPassed <= 257)) {
            		MonthDate   = DaysPassed - 193 + 1;
  	      } 
  	      else if ((DaysPassed >= 258) && (DaysPassed <= 317)) {
            		MonthDate   = DaysPassed - 258 + 1;
   	      } 
  	      else if ((DaysPassed >= 318) && (DaysPassed <= 371)) {
            		MonthDate   = DaysPassed - 318 + 1;
  	      }  
  	      else if ((DaysPassed >= 372) && (DaysPassed <= 421)) {
            		MonthDate   = DaysPassed - 372 + 1;
  	      } 
  	      else if ((DaysPassed >= 422) && (DaysPassed <= 468)) {
            		MonthDate   = DaysPassed - 422 + 1;
  	      } 
  	      else if ((DaysPassed >= 469) && (DaysPassed <= 514)) {
            		MonthDate   = DaysPassed - 469 + 1;
	      } 
  	      else if ((DaysPassed >= 515) && (DaysPassed <= 562)) {
            		MonthDate   = DaysPassed - 515 + 1;
  	      } 
  	      else if ((DaysPassed >= 563) && (DaysPassed <= 613)) {
            		MonthDate   = DaysPassed - 563 + 1;
  	      } 
  	      else if (DaysPassed >= 614) {
		        MonthDate   = DaysPassed - 614 + 1;
  	      } 
 
              return (MonthDate);
        }


   //start method
    public void start() 
    {

        // Get Longitude desired from HTML
        String SLongitude = getParameter("MLongitude");
        Double PLongitude  = new Double(SLongitude);
        Longitude = PLongitude.doubleValue();	
     
        // First find out what time it is on Earth. From that, get the
        // Julian Day info, then calculate Mars info
        long timeMillis       = System.currentTimeMillis ();
        JulianTime julianTime = new JulianTime (timeMillis);
        MarsOrbit  marsOrbit  = new MarsOrbit  (timeMillis);

         // ** Retreive LMST from MarsOrbit object
        lmst    = marsOrbit.GetLMST (Longitude, true);
        lmsthr  = (long)lmst;
        lmstmi  = (long)( (lmst  - lmsthr) * 60.);
        lmstse  = (long)(((lmst  - lmsthr) * 60. - lmstmi) * 60.);
        hours   = (int) (lmsthr);
        minutes = (int) (lmstmi);
        seconds = (int) (lmstse);
        
	Date now = new Date();
        
	// Initialize Constants 
  	DaysAvg    = 30.4;
  	DaysInYear = 365;
  	SolsInYear = 669;
  	DayMars    = 0;
  	MonthMars  = " ";
	
	// Create a GregorianCalendar with the default time zone 
	// and the current date and time 
  	Calendar calendar = new GregorianCalendar();
  	Date trialTime    = new Date();   
  	calendar.setTime(trialTime);

	// Get todays date in gregorian 
  	CurrentMonth = calendar.get(Calendar.MONTH);                                                        
  	CurrentDay   = calendar.get(Calendar.DAY_OF_MONTH);
  	CurrentYear  = calendar.get(Calendar.YEAR);

	// Compute Martian Date constants
  	DaysEarth  = calendar.get(Calendar.DAY_OF_YEAR);
 	AlongEarth = DaysEarth/DaysInYear; 
  	EarthDate  = CurrentYear + AlongEarth;
  	MarsToday  = ((8.0/15.0) * (EarthDate - 1961.0)) + 1.0;
  	MarsYear   = Math.floor(MarsToday);
  	AlongMars  = MarsToday - MarsYear;
  	DaysMars   = Math.round(SolsInYear * AlongMars);

        // Compute the Martian month and day of month
        MonthMars  = Month(DaysMars);
        DayMars    = DayofMonth(DaysMars);

  	// Format date and year for display
	MYear_long = Math.round(MarsYear); 
	MDay_long  = Math.round(DayMars);    
    }

    public void run() 
    {
        // loop terminates when clockThread is set to null in stop()
        while (Thread.currentThread() == dateThread) 
        {
            repaint();
            try 
            { Thread.sleep(1027);} 
            catch (InterruptedException e)
            {}
        }
    }
    
   
    //paint method
    public void paint(Graphics g) 
    {
	seconds = seconds + 1;
	if (seconds == 60)
	{
  	    seconds = 0;
	    minutes = minutes + 1;
	}
	if (minutes == 60)
	{
  	    minutes = 0;
	    hours   = hours + 1;
	}
	if (hours   == 24)
	{
	    hours   = 0;
	}
    

	// Test whether midnight has passed
	if ((hours == 0) && (minutes == 0) && (seconds == 0))
	{
            if (DaysMars == 669)
	    {
               DaysMars  = 1;
	       DayMars   = 1;
               MonthMars = "Gemini"; 
               MarsYear  = MarsYear + 1;
            }
            else
            {
               DaysMars  = DaysMars + 1; 
	       DayMars   = DayofMonth(DaysMars);
               MonthMars = Month(DaysMars);
            }	    
            MDay_long  = Math.round(DayMars);
	    MYear_long = Math.round(MarsYear);
	}	     
                                                            
	// Display the Martian Date  
	g.setFont(f1);
        SDate = MDay_long+" "+MonthMars+" "+MYear_long;
        strlen = getFontMetrics(f1).stringWidth(SDate);
        x = (applet_width - strlen)/2;  
 	g.drawString(SDate, x, 15); 
    }
    
     // stop method
 public void stop() {
        dateThread = null;
    }
}

