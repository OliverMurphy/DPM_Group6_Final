package ca.mcgill.ecse211.lab5;

/**
 * This class uses the ultrasonic sensor to situate itself in the square.
 * @author Oliver Murphy 
 * @author Arielle Lasry
 * @author Lucy Coyle
 */

import java.util.concurrent.TimeUnit;
import ca.mcgill.ecse211.lab5.LightLocalizer;
import ca.mcgill.ecse211.lab5.Lab5;
import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class UltrasonicLocalizer extends Thread {

	private SampleProvider us;
	private float[] usData;
	private Odometer odometer;
	
	
	double wheelRad = Lab5.WHEEL_RAD;
	double track = Lab5.TRACK;
    private static final int ROTATE_SPEED = 50;
    private static final int fallingLimit = 30;
    private static final int risingLimit = 70;

	private int distance;
	private double readings[] = new double[3];
	private boolean one = true;
	private boolean two = true;
	private boolean three = true;
	private boolean four = true;
	private double alpha;
	private double beta;
	private double location [] = new double[3];
	private double deltaTheta;
	private double spin = 360.0;
	private double semiSpin = 180.0;
	private double specialSpin = 135.0;
	private double currTheta;
	private double finalTheta;

	public UltrasonicLocalizer(Odometer odometer, SampleProvider us, float[] usData){ 
		this.odometer = odometer;
		this.us = us;
	    this.usData = usData;
	}	
	
	public void run()
	{
		fallingEdge();
	}
	
	/**
	 * This method looks for diminishing distances by storing the previous three distances in an array.
	 */
	void fallingEdge()
	{
		Lab5.leftMotor.setSpeed(ROTATE_SPEED);
		Lab5.rightMotor.setSpeed(ROTATE_SPEED);
		
		us.fetchSample(usData, 0); // acquire data
        distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
        if(distance < fallingLimit)//do a 90 if you start facing the wall
        {
        	Lab5.leftMotor.rotate(convertAngle(wheelRad, track, semiSpin), true);
    	    Lab5.rightMotor.rotate(-convertAngle(wheelRad, track, semiSpin), false);
    	    odometer.setXYT(0, 0, 0);
        }
		
		Lab5.leftMotor.rotate(convertAngle(wheelRad, track, spin), true);
	    Lab5.rightMotor.rotate(-convertAngle(wheelRad, track, spin), true);
		
		readings[0] = 0;
		readings[1] = 0;
		readings[2] = 0;
	    
		//code from ultrasonic sensor
		//finds the first wall
	    while(one) 
	    {
	    	us.fetchSample(usData, 0); // acquire data
	        distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
	        if(distance < fallingLimit)
	        {
	        	if(this.distance < readings[0] && this.distance < readings[1] && distance < readings[2])
	        	{
	        		Sound.beep();
	        		Lab5.leftMotor.stop(true);
		        	Lab5.rightMotor.stop(false);
		        	
		        	location = odometer.getXYT();
		        	alpha = location[2];//save angle for calculation
		        	one = false;//stop while loop

	        	}
	        	
	        	else
	        	{
	        		readings[1] = readings [0];
		        	readings[2] = readings [1];
		        	readings[0] = this.distance;
	        	}
	        	
	        }
	    }
	  
	    
	    
	    Lab5.leftMotor.rotate(-convertAngle(wheelRad, track, spin), true);
	    Lab5.rightMotor.rotate(convertAngle(wheelRad, track, spin), true);
	    
	    readings[0] = 0;
		readings[1] = 0;
		readings[2] = 0;
	    
		//sleep this value
	    try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    //finds the second wall
	    while(two) 
	    {
		    us.fetchSample(usData, 0); // acquire data
	        distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
	        if(distance < fallingLimit)
	        {
	        	//if distacne is smaller then 3 previous distance in the array stop
	        	if(this.distance < readings[0] && this.distance < readings[1] && distance < readings[2])
	        	{
	        		Sound.beep();
	        		Lab5.leftMotor.stop(true);
		        	Lab5.rightMotor.stop(false);
		        
		        	location = odometer.getXYT();
		        	beta = location[2];//save angle
		        	two =  false;//stop while loop
	        	}
	        	else
	        	{
	        		readings[1] = readings [0];
		        	readings[2] = readings [1];
		        	readings[0] = this.distance;
	        	}
	        	
	        }
	    }
	    
	    //tutorial calculation
	    if(alpha <= beta)
	    {
	    	deltaTheta = (45 - ((alpha + beta)/2));
	    }
	    else if(alpha > beta)
	    {
	    	deltaTheta = (225 - ((alpha + beta)/2));
	    }
	    //just the above part doesnt properly work so we added a correction
	    
	    location = odometer.getXYT();
    	currTheta = location[2];//get current theta displayed on odometer
    	finalTheta = 180 - (currTheta + deltaTheta);//correction
    	
    	turnTo(finalTheta);
    	odometer.setTheta(0);
    	try {
    		Thread.sleep(2000);
    	}
    	catch(InterruptedException e) {
    		Sound.beep();
    	}
    	
    	
	}
	
	/**
	 * This method is the same as falling edge except it looks for increasing distance values
	 */
	void risingEdge()
	{
		Lab5.leftMotor.setSpeed(ROTATE_SPEED);
		Lab5.rightMotor.setSpeed(ROTATE_SPEED);
		
		us.fetchSample(usData, 0); // acquire data
        distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
        if(distance < risingLimit)//do a 180 if you start facing the wall
        {
        	Lab5.leftMotor.rotate(convertAngle(wheelRad, track, specialSpin), true);
    	    Lab5.rightMotor.rotate(-convertAngle(wheelRad, track, specialSpin), false);
    	    odometer.setXYT(0, 0, 0);
        }
		
		Lab5.leftMotor.rotate(convertAngle(wheelRad, track, spin), true);
	    Lab5.rightMotor.rotate(-convertAngle(wheelRad, track, spin), true);//start doing a 360
		
		readings[0] = 0;
		readings[1] = 0;
		readings[2] = 0;
	    
		//code from ultrasonic sensor
	    while(three) 
	    {
	    	us.fetchSample(usData, 0); // acquire data
	        distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
	        if(distance < fallingLimit)
	        {
	        	if(this.distance > readings[0] && this.distance > readings[1] && distance > readings[2])
	        	{
	        		Sound.beep();
	        		Lab5.leftMotor.stop(true);
		        	Lab5.rightMotor.stop(false);
		        	
		        	location = odometer.getXYT();
		        	alpha = location[2];//save angle
		        	three = false;//stop while loop

	        	}
	        	
	        	else
	        	{
	        		readings[1] = readings [0];
		        	readings[2] = readings [1];
		        	readings[0] = this.distance;
	        	}
	        	
	        }
	    }
	  
	    
	    
	    Lab5.leftMotor.rotate(-convertAngle(wheelRad, track, spin), true);
	    Lab5.rightMotor.rotate(convertAngle(wheelRad, track, spin), true);//start doing another 360 in opposite direction
	    
	    readings[0] = 0;
		readings[1] = 0;
		readings[2] = 0;
	    
		//sleep this value
	    try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    while(four) 
	    {
	    	System.out.println(distance + "   ");
		    us.fetchSample(usData, 0); // acquire data
	        distance = (int) (usData[0] * 100.0); // extract from buffer, cast to int
	        if(distance < fallingLimit)
	        {
	        	
	        	if(this.distance > readings[0] && this.distance > readings[1] && distance > readings[2])
	        	{
	        		Sound.beep();
	        		Lab5.leftMotor.stop(true);
		        	Lab5.rightMotor.stop(false);
		        	
		        	location = odometer.getXYT();
		        	beta = location[2];// save angle
		        	four =  false;//stop loop
	        	}
	        	else
	        	{
	        		readings[1] = readings [0];
		        	readings[2] = readings [1];
		        	readings[0] = this.distance;
	        	}
	        	
	        }
	    }
	    
	    if(alpha <= beta)
	    {
	    	deltaTheta = (45 - ((alpha + beta)/2));
	    }
	    else if(alpha > beta)
	    {
	    	deltaTheta = (225 - ((alpha + beta)/2));
	    }
	    location = odometer.getXYT();
    	currTheta = location[2];
    	finalTheta = 180 - (currTheta + deltaTheta);
    	
    	turnTo(finalTheta);
    	
    	
    	
		
	}
	
	
	/**
	 * This method makes the robot rotate on itself.
	 * @param theta
	 */
	void turnTo(double theta) {
		Lab5.leftMotor.setSpeed(ROTATE_SPEED);
		Lab5.rightMotor.setSpeed(ROTATE_SPEED);

	    Lab5.leftMotor.rotate(convertAngle(wheelRad, track, theta), true);
	    Lab5.rightMotor.rotate(-convertAngle(wheelRad, track, theta), false);
	}
	
	/**
	 * Converts the distance to the number of turns the wheel must make.
	 * @param radius
	 * @param distance
	 * @return
	 */
	private static int convertDistance(double radius, double distance) {
	    return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	/**
	 * Converts the angle to a distance and calls convertDistance.
	 * @param radius
	 * @param width
	 * @param angle
	 * @return
	 */
	private static int convertAngle(double radius, double width, double angle) {
	    return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
