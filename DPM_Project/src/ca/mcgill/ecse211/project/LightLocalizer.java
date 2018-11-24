package ca.mcgill.ecse211.project;

import java.util.concurrent.TimeUnit;


import ca.mcgill.ecse211.odometer.Odometer;

/**
 * This class localizes the robot by light detection
 * @author Lucy Coyle
 * @author Oliver Murphy
 */


public class LightLocalizer {

	Odometer odometer;
	private LightPoller lpLeft;
	private LightPoller lpRight;
	private Navigation navigation;
	
	/**
	   * This is the default constructor of this class. It initilaizes the odometer, lightpoller, and navigation
	   * 
	   * @param odometer
	   * @param usPoller
	   * @param navigation
	   */
	public LightLocalizer(Odometer odometer, LightPoller lpLeft, LightPoller lpRight, Navigation navigation)  {
		this.odometer = odometer;
		this.lpLeft = lpLeft;
		this.navigation = navigation;
		this.lpRight = lpRight;
	}
	
	/**
	 * This method straightens the robot on the nearest line
	 */
	public void straightenOnLine() {
		while(!this.lpRight.detectLine()&& !this.lpLeft.detectLine()) {
			  navigation.moveForwardSlowly();
		}
		
		navigation.stopRobot();
		
		while(!this.lpRight.detectLine()|| !this.lpLeft.detectLine()){
			if(this.lpRight.detectLine()&& !this.lpLeft.detectLine()) //right sensor sees a line and left one doesn't
			{
				while(!this.lpLeft.detectLine())//move left wheel forward until it sees the line
				{
					navigation.moveLeftForward();
				}
				 
				navigation.stopRobot();
			}
			  
			  
			if(!this.lpRight.detectLine() && this.lpLeft.detectLine()) //right sensor doesn't see a line and left does see a line
			  {
				  while(!this.lpRight.detectLine()){
					  navigation.moveRightForward();
				  }
				  
				  navigation.stopRobot();
			  }
			
			try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e1) {}
		}
		
	}
	
	/**
	 * This method localizes the robot using the light sensors
	 */
	public void localize(){
		straightenOnLine();
		
		navigation.turn(90); 
		
		straightenOnLine();
		  
		navigation.turn(-90); //should it be turn?
		  
		navigation.travelBackward(5);
		  
		straightenOnLine();
	}
	


	
}
