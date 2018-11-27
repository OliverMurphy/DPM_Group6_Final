package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.odometer.Odometer;


/**
 * This class localizes the robot by ultrasonic light detection. It works using falling edge localization when
 * the robot begins facing away from the wall. If the robot is initially facing the wall, it turns 180 degrees so that
 * it is no longer facing the wall, then the falling edge localization is used.
 * 
 * @author Lucy Coyle
 */

public class UltrasonicLocalizer {
	
    private static final int FALLING_LIMIT = 30;
    private static final int ANGLE_CORRECTION = 27;
    
	private int distance;
	private int direction;
	private UltrasonicPoller usPoller;
	private Navigation navigation;
	private Odometer odometer;
	
	  /**
	   * This is the default constructor of this class. It initilaizes the odometer, uspoller, and navigation
	   * 
	   * @param odometer
	   * @param usPoller
	   * @param navigation
	   */
	public UltrasonicLocalizer(Odometer odometer, UltrasonicPoller usPoller, Navigation navigation){ 
		this.odometer = odometer;
	    this.usPoller = usPoller;
	    this.navigation = navigation;
	}
	
	/**
	 * This method localizes the robot using ultrasonic falling edge localization
	 */
	public void localize(){
	
        if(usPoller.getDistance() < FALLING_LIMIT){
        	//If robot is originally facing the wall, it is turned 180 degrees
        	navigation.turn(180);
    	    odometer.setXYT(0, 0, 0);
        }
        
        direction = 1;
        
        double alpha = findWall();

	    try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    direction = -1;
	    navigation.turn(-100);	//Turning part way with out searching for the next line speeds up localization and 
	    						//prevents the same wall from being sensed twice
        double beta = findWall();
        
        double deltaTheta = 0;
	    if(alpha <= beta){
	    	deltaTheta = (180 + ANGLE_CORRECTION - ((alpha + beta)/2));
	    }
	    else if(alpha > beta){
	    	deltaTheta = (0 + ANGLE_CORRECTION - ((alpha + beta)/2));
	    }

    	double currTheta = odometer.getXYT()[2];
    	double finalTheta = currTheta + deltaTheta;
    	navigation.turn(360 - finalTheta);
    	odometer.setTheta(0);
    
	}
        
	/**
	 * This method rotates until a wall is identified, three readings are used to prevent false positives
	 * @return angle (where wall is found)
	 */
	public double findWall() {
		navigation.continuousCircle(direction);
		
		double readings[] = {0, 0, 0};
	    
	    while(true) {
	    	this.distance = usPoller.getDistance();
	        if(this.distance < FALLING_LIMIT){
	        	if(this.distance < readings[0] && this.distance < readings[1] && distance < readings[2]){
	        		navigation.stopRobot();
		        	return odometer.getXYT()[2];
	        	}
	        	else{
	        		readings[1] = readings [0];
		        	readings[2] = readings [1];
		        	readings[0] = this.distance;
	        	}
	        	
	        }
	    }
	}
	    
}
