package ca.mcgill.ecse211.project;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * This class allows the robot to gather a ring
 * @author Lucy Coyle
 */

public class GetRing {
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
	
	private SampleProvider ls;
	private float[] lsData;
	private Navigation navigation;
	
	
	
	private static final EV3LargeRegulatedMotor armMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final EV3LargeRegulatedMotor liftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	 /**
	   * This is the default constructor of this class. It initiates the light sensor
	   * 
	   * @param lightSensor
	   * @param lsData
	   */
	public GetRing(SampleProvider ls, float[] lsData){
		this.ls = ls;
		this.lsData = lsData;
	}
	 /**
	   * This is a constructor of this class. It initiates the light sensor and navigation
	   * 
	   * @param lightSensor
	   * @param lsData
	   * @param navigation
	   */
	public GetRing(SampleProvider ls, float[] lsData, Navigation navigation){
		this.ls = ls;
		this.lsData = lsData;
		this.navigation = navigation;
	}
	
	
	/**
	 * This method implements grabbing a ring from the tree
	 */
	public void grabRing() {
		armMotor.setSpeed(90);
		armMotor.rotate(-50);
		navigation.travelForward(9);
		armMotor.rotate(75);
		navigation.travelBackward(9);
		liftMotor.setSpeed(90);
		liftMotor.rotate(-90);
	}
	
	/**
	 * This method implements sensing whether or not a ring is on the tree
	 * 
	 * @return colour (of the ring, -1 if no ring)
	 */
	
	public int senseRing() {
		return colourClassify();		
	}
	
	/**
	 * This method gets one ring off of the tree and stores it
	 * @param x
	 * @param y
	 */
	
	public void getOneRing(int x, int y) {
		navigation.travelToTree(x, y);
		grabRing();
		int c = senseRing();
		switch(c) {
			case 1: Sound.beep();
				break;
			case 2: Sound.beep();
					Sound.beep();
				break;
			case 3: Sound.beep();
					Sound.beep();
					Sound.beep();
				break;
			case 4: Sound.beep();
					Sound.beep();
					Sound.beep();
					Sound.beep();
				break;
		}
	}
	
	
	/**
	 * This method calculates the colour sensed by the light sensor
	 * 
	 * @return colour 
	 */
	public int colourClassify() {
		ls.fetchSample(lsData, 0); 
		
		redSample = lsData[0];
		greenSample = lsData[1];
		blueSample = lsData[2];
	
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

}
