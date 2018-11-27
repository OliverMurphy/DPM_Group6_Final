package ca.mcgill.ecse211.project;

/**
 * This class localizes the robot by light detection. It uses two light pollers, one on the left of the robot
 * and one on the right of the robot. The robot is localized, by turning and straightening itself on a line in a full
 * circle. The straighten on a line method is also used by the odometry correction that occurs after localization and 
 * through out the demo as well.
 * 
 * @author Lucy Coyle
 * @author Oliver Murphy
 */


public class LightLocalizer {

	private LightPoller lpLeft;
	private LightPoller lpRight;
	private Navigation navigation;
	
	/**
	 * This is the default constructor for the class it takes in the light pollers and the navigation object
	 * @param lpLeft
	 * @param lpRight
	 * @param navigation
	 */
	
	public LightLocalizer(LightPoller lpLeft, LightPoller lpRight, Navigation navigation)  {
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
				navigation.moveLeftForward();
				while(!this.lpLeft.detectLine())//move left wheel forward until it sees the line
				{
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				 
				navigation.stopRobot();
			}
			  
			  
			if(!this.lpRight.detectLine() && this.lpLeft.detectLine()) //right sensor doesn't see a line and left does see a line
			  {
				navigation.moveRightForward();
				  while(!this.lpRight.detectLine()){try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					  
				  }
				  
				  navigation.stopRobot();
			  }
			
			try {
				Thread.sleep(10);
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
		  
		navigation.turn(-90); 
		  
		navigation.travelBackward(5);
		  
		straightenOnLine();
	}
	


	
}
