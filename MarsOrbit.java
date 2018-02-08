import java.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.text.*;

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