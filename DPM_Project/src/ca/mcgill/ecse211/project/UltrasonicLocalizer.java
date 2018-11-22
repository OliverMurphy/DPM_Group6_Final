package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Sound;


/**
 * This class localizes the robot by ultrasonic light detection
 * @author Lucy Coyle
 */

public class UltrasonicLocalizer {

	private Odometer odometer;
	
    private static final int fallingLimit = 30;

	private int distance;
	private int direction;
	UltrasonicPoller usPoller;
	Navigation navigation;
	

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
	 * This method looks for diminishing distances by storing the previous three distances in an array.
	 */
	public void localize(){
	
        if(usPoller.getDistance() < fallingLimit){
        	navigation.turn(180);
    	    odometer.setXYT(0, 0, 0);
        }
        //navigation.travelForward(5);
        direction = 1;
        
        double alpha = findWall();

	    try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    direction = -1;
	    navigation.turn(-100);
        double beta = findWall();
        
        double deltaTheta = 0;
	    if(alpha <= beta)
	    {
	    	deltaTheta = (217 - ((alpha + beta)/2));
	    }
	    else if(alpha > beta)
	    {
	    	deltaTheta = (27 - ((alpha + beta)/2));
	    }

    	double currTheta = odometer.getXYT()[2];
    	double finalTheta = currTheta + deltaTheta;
    	navigation.turn(360 - finalTheta);
    	odometer.setTheta(0);
    
    	
    	
	}
        
	/**
	 * This method rotates until a wall is identified
	 * @return angle (where wall is found)
	 */
	public double findWall() {
		navigation.continuousCircle(direction);
		
		double readings[] = {0, 0, 0};
	    
	    while(true) {
	    	this.distance = usPoller.getDistance();
	        if(this.distance < fallingLimit){
	        	if(this.distance < readings[0] && this.distance < readings[1] && distance < readings[2]){
//	        		Sound.beep();
	        		navigation.stopRobot();
		        
		        	return odometer.getXYT()[2];//save angle for calculation
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
