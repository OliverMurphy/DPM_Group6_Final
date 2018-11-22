package ca.mcgill.ecse211.lab5;

import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * This class implements the ring finder. 
 * It can search a field for rings or classify rings while stationary.
 * @author Lucy Coyle 
 */

public class RingFinder {
	
	static double dist = 0.1; 
	
	//CALCULATED CONSTANTS                    
	//Blue ring
	static double RmiBlueRing = 0.1740282;
	static double GmiBlueRing = 0.7613724826;
	static double BmiBlueRing = 0.6245206902;
	
	//Green ring
	static double RmiGreenRing = 0.390903893;
	static double GmiGreenRing = 0.9108166127;
	static double BmiGreenRing = 0.1326924774;
	
	//Yellow ring
	static double RmiYellowRing = 0.7873629308;
	static double GmiYellowRing = 0.607377904;
	static double BmiYellowRing = 0.1056015167;
	
	//Orange ring
	static double RmiOrangeRing = 0.9377461875;
	static double GmiOrangeRing = 0.3391870432;
	static double BmiOrangeRing = 0.0747276652;
	

	//values taken from the sampleProvider
	static float redSample;
	static float greenSample;
	static float blueSample;
	
	//calculated vales derived from the sampleProvider
	static double redSampleMean;
	static double greenSampleMean;
	static double blueSampleMean;
	static boolean found;
	
	public static double TILE_SIZE = 30.48;
	
	/**
	 * Goes through search area looking for a ring of the right colour
	 * @param colour
	 * @return
	 */
	public static void find(int colour) throws InterruptedException{
		int[] tile = {Lab5.LLX, Lab5.LLY};
		found = false;
		travelTo(Lab5.LLX * TILE_SIZE, Lab5.LLY * TILE_SIZE);
		int direction = 1;
		while(!found) {
			if(tile[0] == Lab5.URX && tile[1] == Lab5.URY) {
				travelTo(Lab5.URX *TILE_SIZE, Lab5.URY * TILE_SIZE);
				break;
			}
			
			if((tile[0] < Lab5.URX && direction == 1)|| (tile[0] > Lab5.LLX && direction == -1) ) {
				tile[0] += direction;
			}
			else {
				tile[1]++;
				direction = direction * -1;
			}
			travelTo(tile[0]*TILE_SIZE , tile[1]*TILE_SIZE);
			Thread.sleep(500);
		}
	}
	
	/**
	 * Gets colour reading for ring detected by us sensor and then avoids the obstacle
	 * If its the right ring it travels to the last location
	 * @param c
	 * @return
	 */
	
	public static void detectRing(int c) throws InterruptedException {

		Lab5.usDistance.fetchSample(Lab5.usData, 0); // acquire data
        int distance = (int) (Lab5.usData[0] * 100.0); // extract from buffer, cast to int
		Lab5.leftMotor.setSpeed(90);
		Lab5.rightMotor.setSpeed(90);
		Lab5.leftMotor.rotate(Lab5.convertDistance(Lab5.WHEEL_RAD, distance - 3), true);
    	Lab5.rightMotor.rotate(Lab5.convertDistance(Lab5.WHEEL_RAD, distance - 3), false);
		int ring = getColourOfRing();
		if(ring == c) {
		 	Sound.beep();
		 	found = true;
		 	Lab5.leftMotor.rotate(Lab5.convertAngle(Lab5.WHEEL_RAD, Lab5.TRACK, 90), true);
	    	Lab5.rightMotor.rotate(Lab5.convertAngle(Lab5.WHEEL_RAD, Lab5.TRACK, -90), false);
			Lab5.leftMotor.rotate(Lab5.convertDistance(Lab5.WHEEL_RAD, 8), true);
	    	Lab5.rightMotor.rotate(Lab5.convertDistance(Lab5.WHEEL_RAD, 8), false);
		 	travelTo(Lab5.URX, Lab5.URY);
		}
		else {
		 	Sound.beep();
		 	Sound.beep();
		 	Lab5.leftMotor.rotate(Lab5.convertAngle(Lab5.WHEEL_RAD, Lab5.TRACK, 90), true);
	    	Lab5.rightMotor.rotate(Lab5.convertAngle(Lab5.WHEEL_RAD, Lab5.TRACK, -90), false);
			Lab5.leftMotor.rotate(Lab5.convertDistance(Lab5.WHEEL_RAD, 8), true);
	    	Lab5.rightMotor.rotate(Lab5.convertDistance(Lab5.WHEEL_RAD, 8), false);
		}
	}
	
	/**
	 * Loops through sensing ring, if ring is detected prints the colour
	 * @param lcd
	 * @return
	 */
	public static void colourClassify(TextLCD lcd) throws InterruptedException{
		lcd.clear();
		
		while(true){
			int c = getColourOfRing();
			if(c != -1) {
				lcd.drawString("Object Detected", 0, 0);
				switch (c){
				case 1: lcd.drawString("Blue", 0, 1);
					break;
				case 2: lcd.drawString("Green", 0, 1);
					break;
				case 3: lcd.drawString("Yellow", 0, 1);
					break;
				case 4: lcd.drawString("Orange", 0, 1);
					break;
				}
			}
			Thread.sleep(900);
			lcd.clear();
		}
	}
	
	/**
	 * Calculate the colour that the light sensor is currently sensing
	 * @return int colour
	 */
	public static int getColourOfRing() {
		Lab5.lightSensorValues.fetchSample(Lab5.lightSensorData, 0); 
		
		redSample = Lab5.lightSensorData[0];
		greenSample = Lab5.lightSensorData[1];
		blueSample = Lab5.lightSensorData[2];
	
		//light scaling
		redSampleMean = redSample/Math.sqrt(redSample*redSample + greenSample*greenSample + blueSample*blueSample);
		greenSampleMean = greenSample/Math.sqrt(redSample*redSample + greenSample*greenSample + blueSample*blueSample);
		blueSampleMean = blueSample/Math.sqrt(redSample*redSample + greenSample*greenSample + blueSample*blueSample);
		
		double euclidDistBlueRing = Math.sqrt( Math.pow((redSampleMean - RmiBlueRing), 2) + Math.pow((greenSampleMean - GmiBlueRing), 2) + Math.pow((blueSampleMean - BmiBlueRing), 2));
		double euclidDistGreenRing = Math.sqrt( Math.pow((redSampleMean - RmiGreenRing), 2) + Math.pow((greenSampleMean - GmiGreenRing), 2) + Math.pow((blueSampleMean - BmiGreenRing), 2));
		double euclidDistYellowRing = Math.sqrt( Math.pow((redSampleMean - RmiYellowRing), 2) + Math.pow((greenSampleMean - GmiYellowRing), 2) + Math.pow((blueSampleMean - BmiYellowRing), 2));
		double euclidDistOrangeRing = Math.sqrt( Math.pow((redSampleMean - RmiOrangeRing), 2) + Math.pow((greenSampleMean - GmiOrangeRing), 2) + Math.pow((blueSampleMean - BmiOrangeRing), 2));
		
		if(euclidDistBlueRing < dist * 1.5) {
			return 1;
		}
		
		if(euclidDistGreenRing < dist){
			return 2;
		}
		
		if(euclidDistYellowRing < dist){
			return 3;
		}
		
		if(euclidDistOrangeRing < dist){
			return 4;
		}
		return -1;
	}
	
	/**
	 * This travelTo method moves the robot to a point, if it detects a ring, it stops and gets the colour of the ring
	 * @param x, y coordinates of point to travel to
	 */
	
	public static void travelTo(double x, double y) throws InterruptedException {
		double position[] = Lab5.odometer.getXYT(); //get current position
		
		double delta_x = x - position[0]; //calculate change in x and y needed 
		double delta_y = y - position[1]; //to get to the way point
		
		double distance = Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2)); //calculate distance 
		
		double theta = Math.toDegrees(Math.acos(delta_y/distance)); //calculate angle required
		
		if (delta_x < 0) {		//If going to the negative x, change the theta
			theta = 360 - theta;
		} 
		
		double delta_theta = theta - position[2]; //calculate change in theta based on current angle
		
		if (delta_theta < -180) { //Adjust delta_theta
			delta_theta += 360;
		}
		
		Lab5.turnTo(delta_theta); //Turn to delta_theta
		
		Lab5.leftMotor.setSpeed(80);
	    Lab5.rightMotor.setSpeed(80);
		Lab5.usDistance.fetchSample(Lab5.usData, 0); // acquire data
        int distance_us = (int) (Lab5.usData[0] * 100.0); // extract from buffer, cast to int
		while(Math.abs(position[0] - x) > 1 || Math.abs(position[1] - y) > 1) {
			Lab5.leftMotor.forward();
			Lab5.rightMotor.forward();
			Lab5.usDistance.fetchSample(Lab5.usData, 0); // acquire data
	        distance_us = (int) (Lab5.usData[0] * 100.0); // extract from buffer, cast to int
	        if(distance_us <= 20) {
	        	Lab5.leftMotor.stop();
	        	Lab5.rightMotor.stop();
	        	detectRing(Lab5.r);
	        	break;
	        }
	        Thread.sleep(500);
			position = Lab5.odometer.getXYT();
		}
			
	}
	
}
