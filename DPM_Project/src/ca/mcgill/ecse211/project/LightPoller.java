package ca.mcgill.ecse211.project;


import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

/**
 * This class implements a poller on the light sensor
 * @author Lucy Coyle
 */

import lejos.robotics.SampleProvider;

public class LightPoller extends Thread {
	private static Port lightPortLeft;
	private static EV3ColorSensor lightSensorLeft;
	private static SampleProvider lightSensorValuesLeft;
	private static float[] lsDataLeft;
	private float firstReading = -1;
	private float intensityLeft;
	private float lightThreshold = 20;

  /**
   * This is the default constructor of this class. It initiates the light sensor
   * 
   * @param lPort
   */
  public LightPoller(Port lPortLeft) {
	   lightPortLeft = lPortLeft;
	   lightSensorLeft = new EV3ColorSensor(lightPortLeft);
	   lightSensorValuesLeft = lightSensorLeft.getRedMode();
       lsDataLeft = new float[lightSensorLeft.sampleSize()];
  }

  /**
   * This method is meant to detect a line by the light sensor and then return 1 if a line is found
   * 
   * @return 1 if line found
   * @return -1 if line not found
   */
  public int detectLineLeft() {
	  	lightSensorValuesLeft.fetchSample(lsDataLeft, 0);
		intensityLeft = lsDataLeft[0] * 100;
		if(firstReading == -1) {
			firstReading = intensityLeft;
		}
		else if ((100 * Math.abs((intensityLeft - firstReading)/firstReading)) > lightThreshold){	
			if(intensityLeft < firstReading) {
				return 1;
			}
		}
		return -1;
  }

}

