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

