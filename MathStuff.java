import java.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.text.*;

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
