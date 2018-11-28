package ca.mcgill.ecse211.project;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * This class allows the robot to gather a ring. It uses the logic and calculations from Lab 5 to classify the 
 * colour of the ring. It also implements the movement of the robot arms for grabbing a ring, as well as the logic
 * that is followed once the tree is reached to grab a ring from a side and handle the case where a side has no ring.
 * Our robot works best when collecting one ring at a time, so this is what this class implements.
 * 
 * @author Lucy Coyle
 */

public class GetRing {
	private static final double COLOUR_ERROR_MARGIN = 0.1; 
	static final double BLUE_ERROR = 1.5; 	//Error margin must be larger for blue ring
	
	//CALCULATED CONSTANTS                    
	//Blue ring
	private static final double RmiBlueRing = 0.1740282;
	private static final double GmiBlueRing = 0.7613724826;
	private static final double BmiBlueRing = 0.6245206902;
	
	//Green ring
	private static final double RmiGreenRing = 0.390903893;
	private static final double GmiGreenRing = 0.9108166127;
	private static final double BmiGreenRing = 0.1326924774;
	
	//Yellow ring
	private static final double RmiYellowRing = 0.7873629308;
	private static final double GmiYellowRing = 0.607377904;
	private static final double BmiYellowRing = 0.1056015167;
	
	//Orange ring
	private static final double RmiOrangeRing = 0.9377461875;
	private static final double GmiOrangeRing = 0.3391870432;
	private static final double BmiOrangeRing = 0.0747276652;
	
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
		navigation.travelForward(13);
		armMotor.rotate(75);
		navigation.travelBackward(13);
		liftMotor.setSpeed(90);
		liftMotor.rotate(-90);
	}
	
	/**
	 * This method gets one ring off of the tree and stores it
	 * @param x
	 * @param y
	 */
	
	public void getRing(int x, int y) {
		
		int c = -1;
		int side = 0;
		
		while(side < 1 && c < 1) {
			grabRing();
			
			//Detect colour of the ring and beep accordingly
			c = senseRing();
		
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
			if(c < 1) {
				//If no ring, move to another side of the tree and try to get a ring, 
				//if there's no possible side, give up
				if(!navigation.moveSideOfTree(x, y)) {
					break;
				}
				
			}
			side++;
		}
		
		
	}
	
	
	/**
	 * This method calculates the colour sensed by the light sensor
	 * 
	 * @return colour	1 for blue, 2 for green, 3 for yellow, 4 for orange, -1 for no colour 
	 */
	public int senseRing() {
		
		ls.fetchSample(lsData, 0); 
		
		float redSample = lsData[0];
		float greenSample = lsData[1];
		float blueSample = lsData[2];
	
		//light scaling
		double totalMean = Math.sqrt(redSample * redSample + greenSample * greenSample + blueSample * blueSample);
		double redSampleMean = redSample / totalMean;
		double greenSampleMean = greenSample / totalMean;
		double blueSampleMean = blueSample / totalMean;

		double euclidDistBlueRing = Math.sqrt( Math.pow((redSampleMean - RmiBlueRing), 2) + Math.pow((greenSampleMean - GmiBlueRing), 2) + Math.pow((blueSampleMean - BmiBlueRing), 2));
		double euclidDistGreenRing = Math.sqrt( Math.pow((redSampleMean - RmiGreenRing), 2) + Math.pow((greenSampleMean - GmiGreenRing), 2) + Math.pow((blueSampleMean - BmiGreenRing), 2));
		double euclidDistYellowRing = Math.sqrt( Math.pow((redSampleMean - RmiYellowRing), 2) + Math.pow((greenSampleMean - GmiYellowRing), 2) + Math.pow((blueSampleMean - BmiYellowRing), 2));
		double euclidDistOrangeRing = Math.sqrt( Math.pow((redSampleMean - RmiOrangeRing), 2) + Math.pow((greenSampleMean - GmiOrangeRing), 2) + Math.pow((blueSampleMean - BmiOrangeRing), 2));
		
		if(euclidDistBlueRing < COLOUR_ERROR_MARGIN * BLUE_ERROR) {
			return 1;
		}
		
		if(euclidDistGreenRing < COLOUR_ERROR_MARGIN){
			return 2;
		}
		
		if(euclidDistYellowRing < COLOUR_ERROR_MARGIN){
			return 3;
		}
		
		if(euclidDistOrangeRing < COLOUR_ERROR_MARGIN){
			return 4;
		}
		return -1;
	}

}
