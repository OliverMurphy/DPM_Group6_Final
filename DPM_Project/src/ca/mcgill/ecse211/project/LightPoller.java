package ca.mcgill.ecse211.project;


import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

/**
 * This class implements a poller on the light sensor
 * @author Lucy Coyle
 */

import lejos.robotics.SampleProvider;

public class LightPoller extends Thread {
	private Port lightPort;
	private EV3ColorSensor lightSensor;
	private SampleProvider lightSensorValues;
	private float[] lsData;
	private float firstReading = -1;
	private float intensity;
	private float lightThreshold = 20;

  /**
   * This is the default constructor of this class. It initiates the light sensor
   * 
   * @param lPort
   */
  public LightPoller(Port lPortLeft) {
	   lightPort = lPortLeft;
	   lightSensor = new EV3ColorSensor(lightPort);
	   lightSensorValues = lightSensor.getRedMode();
       lsData = new float[lightSensor.sampleSize()];
  }

  /**
   * This method is meant to detect a line by the light sensor and then return 1 if a line is found
   * 
   * @return 1 if line found
   * @return -1 if line not found
   */
  public int detectLine() {
	  	lightSensorValues.fetchSample(lsData, 0);
		intensity = lsData[0] * 100;
		if(firstReading == -1) {
			firstReading = intensity;
		}
		else if ((100 * Math.abs((intensity - firstReading)/firstReading)) > lightThreshold){	
			if(intensity < firstReading) {
				return 1;
			}
		}
		return -1;
  }

}

