/* TITLE:  Marsclock
 * AUTHOR: Jeffery R Roche
 * DATE  : 03/15/1998
 *
 * PURPOSE:
 * This program calculates Mars Local Solar Time at the Mars Pathfinder Landing site on Mars.
 * This is done by calculating the number of milliseconds passed between a reference date and
 * the current date, converting Earth milliseconds to Mars milliseconds, and calculating the 
 * current time on Mars. The result can then displayed on a web page.
 * 
 */
import java.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.text.*;

public class lmstpack extends java.applet.Applet implements Runnable {
    // Initialize Variables

    Thread clockThread = null;

    Font   f1 = new Font("Courier", Font.BOLD, 18);

   long    lmsthr,         // LMST hour
	   lmstmi,         // LMST minute
	   lmstse;         // LMST second  
           
   int     hours, 
	   minutes,
	   seconds,        // Holds time values for display
           strlen,
                x;               

   double  lmst,           // Local Mars Solar Time 
           Longitude; 

   String time;    
     
   static final int applet_width = 100;
   // static final double PF_LONGITUDE = 33.52;

    public void start() {
        if (clockThread == null) {
            clockThread = new Thread(this, "Clock");
            clockThread.start();
        }

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
    }

    public void run() {
        // loop terminates when clockThread is set to null in stop()
        while (Thread.currentThread() == clockThread) {
            repaint();
            try {
                Thread.sleep(1027);
            } catch (InterruptedException e){
            }
        }
    }

    public void paint(Graphics g) {
	// Display the Martian time
 
	seconds = seconds + 1;
	if (seconds == 60)
	  {
  	    seconds = 0;
	    minutes = minutes + 1;
	  }
	if (minutes == 60)
	  {
	    minutes = 0;
	    if (hours == 24)
	      {
                hours = 0;
	      }
	    else
              {
                hours = hours + 1;
	      }
	}
         
	g.setFont(f1);
	if ((minutes <= 9) && (seconds <= 9))
	   {time = hours + ":" + "0" + minutes + ":" + "0" + seconds;}
        else if ((minutes <= 9) && (seconds >= 9))
                {time = hours + ":" + "0" + minutes + ":" + seconds;}
        else if ((minutes >= 9) && (seconds <= 9))
                {time = hours + ":" + minutes + ":" + "0" + seconds;}
        else if ((minutes >= 9) && (seconds >= 9))
                {time = hours + ":" + minutes + ":" + seconds;}
    
        strlen = getFontMetrics(f1).stringWidth(time);
        x = (applet_width - strlen)/2;     
        g.drawString(time, x, 15);
    }
    public void stop() {
        clockThread = null;
    }
}


