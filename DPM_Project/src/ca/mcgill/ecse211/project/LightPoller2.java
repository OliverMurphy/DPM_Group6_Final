package ca.mcgill.ecse211.project;


import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

/**
 * This class implements a poller on the light sensor
 * @author Lucy Coyle
 */

import lejos.robotics.SampleProvider;

public class LightPoller2 extends Thread {

	private static Port lightPortRight;
	private static EV3ColorSensor lightSensorRight;
	private static SampleProvider lightSensorValuesRight;
	private static float[] lsDataRight;
	private float firstReading = -1;
	private float intensityRight;
	private float lightThreshold = 20;

  /**
   * This is the default constructor of this class. It initiates the light sensor
   * 
   * @param lPort
   */
  public LightPoller2(Port lPortRight) {
       lightPortRight = lPortRight;
       lightSensorRight = new EV3ColorSensor(lightPortRight);
	   lightSensorValuesRight = lightSensorRight.getRedMode();
       lsDataRight = new float[lightSensorRight.sampleSize()];
  }

  /**
   * This method is meant to detect a line by the light sensor and then return 1 if a line is found
   * 
   * @return 1 if line found
   * @return -1 if line not found
   */  
  public int detectLineRight() {
	  	lightSensorValuesRight.fetchSample(lsDataRight, 0);
		intensityRight = lsDataRight[0] * 100;
		if(firstReading == -1) {
			firstReading = intensityRight;
		}
		else if ((100 * Math.abs((intensityRight - firstReading)/firstReading)) > lightThreshold){	
			if(intensityRight < firstReading) {
				return 1;
			}
		}
		return -1;
}

}

