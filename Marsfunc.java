/* TITLE:  Marsdate
 * AUTHOR: Jeffery R Roche
 * DATE  : 02/20/1998
 *
 * PURPOSE:
 * This program calculates the current date on Mars according to a calendar
 * invented by Dr. Robert Zubrin.
 * 
 */

import java.awt.*;
import java.util.*;
import java.lang.*;
import java.text.*;


public class marsfunc extends java.applet.Applet{

    Thread clockThread = null;

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
           DaysAvg;

    int    CurrentMonth,
	   CurrentDay,
	   CurrentYear,
	   DaysInYear,
           SolsInYear,
           Month,
	   Date,
	   Year,
           strlen,
           x;

    long   MDay_long,
	   MYear_long;	

    String MonthMars,
           SDate;

   static final int applet_width = 145;

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
    public void start() {
        
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
        MonthMars = Month(DaysMars);
        DayMars     = DayofMonth(DaysMars);

  	// Format date and year for display
	MYear_long = Math.round(MarsYear); 
	MDay_long  = Math.round(DayMars);    
    }
    
   
    //paint method
    public void paint(Graphics g) {                                                            
	// Display the Martian Date  
	g.setFont(f1);
        SDate = MDay_long+" "+MonthMars+" "+MYear_long;
        strlen = getFontMetrics(f1).stringWidth(SDate);
        x = (applet_width - strlen)/2;  
 	g.drawString(SDate, x, 15); 
    }
    
     // stop method
     public void stop() {
     }
}

