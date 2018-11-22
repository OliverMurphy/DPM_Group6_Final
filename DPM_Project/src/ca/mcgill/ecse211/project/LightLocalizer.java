package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Sound;

/**
 * This class localizes the robot by light detection
 * @author Lucy Coyle
 * @author Oliver Murphy
 */


public class LightLocalizer {

	Odometer odometer;
	private LightPoller lpLeft;
	private LightPoller2 lpRight;
	private Navigation navigation;
	
	/**
	   * This is the default constructor of this class. It initilaizes the odometer, lightpoller, and navigation
	   * 
	   * @param odometer
	   * @param usPoller
	   * @param navigation
	   */
	public LightLocalizer(Odometer odometer, LightPoller lpLeft, LightPoller2 lpRight, Navigation navigation)  {
		this.odometer = odometer;
		this.lpLeft = lpLeft;
		this.navigation = navigation;
		this.lpRight = lpRight;
	}
	
	public void localize()
	{
		while(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == -1) 
		  {
			  navigation.moveForward();
		  }
		navigation.stopRobot();
		
		//move  one isn't on a line
		  if(this.lpRight.detectLineRight() == 1 && this.lpLeft.detectLineLeft() == -1) //right sensor sees a line and left one doesn't
		  {
			  Sound.beep();
			  Sound.beep();
			  while(this.lpLeft.detectLineLeft() == -1)//move left wheel forward until it sees the line
			  {
				  navigation.moveLeftForward();
			  }
			 
			  navigation.stopRobot();
			  
			  
		  }
		  
		  
		  if(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == 1) //right sensor doesn't see a line and left does see a line
		  {
			  Sound.beep();
			  while(this.lpRight.detectLineRight() == -1)
			  {
				  navigation.moveRightForward();
			  }
			  
			  navigation.stopRobot();
		  }
		  
		  navigation.turn(90); //should it be turn?
		  
		  navigation.travelBackward(1);
		  
		  while(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == -1) 
		  {
			  navigation.moveForward();
		  }
		  navigation.stopRobot();
		  
		  if(this.lpRight.detectLineRight() == 1 && this.lpLeft.detectLineLeft() == -1) //right sensor sees a line and left one doesn't
		  {
			  Sound.beep();
			  Sound.beep();
			  while(this.lpLeft.detectLineLeft() == -1)//move left wheel forward until it sees the line
			  {
				  navigation.moveLeftForward();
			  }
			 
			  navigation.stopRobot();
			  
		  }
		  
		  
		  if(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == 1) //right sensor doesn't see a line and left does see a line
		  {
			  Sound.beep();
			  while(this.lpRight.detectLineRight() == -1)
			  {
				  navigation.moveRightForward();
			  }
			  
			  navigation.stopRobot();
		  }
		  navigation.turn(-90); //should it be turn?
		  
		  navigation.travelBackward(5);
		  
		  while(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == -1) 
		  {
			  navigation.moveForward();
		  }
		  navigation.stopRobot();
		  
		  if(this.lpRight.detectLineRight() == 1 && this.lpLeft.detectLineLeft() == -1) //right sensor sees a line and left one doesn't
		  {
			  Sound.beep();
			  Sound.beep();
			  while(this.lpLeft.detectLineLeft() == -1)//move left wheel forward until it sees the line
			  {
				  navigation.moveLeftForward();
			  }
			 
			  navigation.stopRobot();
			  
		  }
		  
		  
		  if(this.lpRight.detectLineRight() == -1 && this.lpLeft.detectLineLeft() == 1) //right sensor doesn't see a line and left does see a line
		  {
			  Sound.beep();
			  while(this.lpRight.detectLineRight() == -1)
			  {
				  navigation.moveRightForward();
			  }
			  
			  navigation.stopRobot();
		  }
		  
		  //reset odometer to whatever corner you are in
	}
	
//	/**
//	* This method is where the logic for the light localizer will run
//	 * @throws InterruptedException 
//	*/
//	public void localize() throws InterruptedException{
//		
//		reachLine();
//		Thread.sleep(50);
//		navigation.travelBackward(15);
//		navigation.turn(90);
//		reachLine();	
//		Thread.sleep(50);
//		navigation.travelBackward(8);
//		navigation.turn(-90);
//		navigation.travelForward(10);
//	}
//	/**
//	 * This method moves forward until it sees a black line.
//	 */
//	void reachLine(){
//		navigation.moveForward();
//		while(true){
//			if(lightPoller.detectLineLeft() == 1) {
//				navigation.stopRobot();
//				return;
//			}
//		}			
//		
//	}
//	

	
}
