package ca.mcgill.ecse211.project;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;

/**
 * This class implements a poller on the ultrasonic sensor. It collects the distance sensed by the sensor.
 * @author Lucy Coyle
 */

import lejos.robotics.SampleProvider;

public class UltrasonicPoller {

	private int distance;
	private Port usPort;
	private SensorModes usSensor; 
	private SampleProvider usDistance; 
	private float[] usData;

  
  /**
   * This is the default constructor of this class. It initiates the ultrasonic sensor
   * 
   * @param uPort
   */
  public UltrasonicPoller(Port uPort) {
	usPort = uPort;
    usSensor = new EV3UltrasonicSensor(usPort); 
	usDistance = usSensor.getMode("Distance"); 
	usData = new float[usDistance.sampleSize()];
    
  }
  
  /**
   * This method returns the distance detected by the ultrasonic sensor
   * 
   * @return distance
   */
  public int getDistance() {
	  usSensor.fetchSample(usData, 0); // acquire data
      this.distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
	  return this.distance;
  }

}
