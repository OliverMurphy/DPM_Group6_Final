package ca.mcgill.ecse211.lab5;

/**
 * This class moves the coordinate (0,0).
 *  @author Oliver Murphy 
 * @author Arielle Lasry
 * @author Lucy Coyle
 */
import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class LightLocalizer extends Thread{

	
	double wheelRad = Lab5.WHEEL_RAD;
	double track = Lab5.TRACK;
	private static final int FORWARD_SPEED = 100;
    private static final int ROTATE_SPEED = 50;

	private double quarterSpin = 90.0;
	private boolean one = true;
	private boolean two = true;
	static int num = 6; 
	private float firstReading = -1;
	private float intensity;
	private float lightThreshold = 30;
	
	private Odometer odometer; //instantiate
	
	//initialize the light sensor
	
	//constructor
	public LightLocalizer(Odometer odometer)  {

		this.odometer = odometer;
	}
	
	public void run(){
		yRun();
		try {
		xRun();
		}
		catch (InterruptedException e) {
			Sound.beep();
		}
		
	}
	/**
	 * This method moves forward until it sees a black line.
	 */
	void yRun()
	{
		Lab5.leftMotor.setSpeed(FORWARD_SPEED);
		Lab5.rightMotor.setSpeed(FORWARD_SPEED);

		Lab5.leftMotor.forward();
		Lab5.rightMotor.forward();//move forward
		
		while(one)
		{
			
			Lab5.lightSensorValues.fetchSample(Lab5.lightSensorData, 0);
			intensity = Lab5.lightSensorData[0] * 100;
			if(firstReading == -1) {
				firstReading = intensity;
			}
			else if ((100 * Math.abs((intensity - firstReading)/firstReading)) > lightThreshold) //if see a black line
			{
				if(intensity < firstReading) {
					Sound.beep();
					Lab5.leftMotor.stop(true);
					Lab5.rightMotor.stop(false);//stop motors
					firstReading = -1;
					one = false;
				}
			}
			//back to initial location now
						
		}
		
	}
	/**
	 * This method moves forward until it sees the black line then it moves to (0,0)
	 */
	void xRun() throws InterruptedException{
		Lab5.leftMotor.setSpeed(ROTATE_SPEED);
		Lab5.rightMotor.setSpeed(ROTATE_SPEED);

		Lab5.leftMotor.rotate(convertAngle(wheelRad, track, quarterSpin), true);
		Lab5.rightMotor.rotate(-convertAngle(wheelRad, track, quarterSpin), false); //turned 90 deg
		
		Lab5.leftMotor.setSpeed(FORWARD_SPEED);
		Lab5.rightMotor.setSpeed(FORWARD_SPEED);

		Lab5.leftMotor.forward();
		Lab5.rightMotor.forward();
		//move forward until see black line
		while(two)
		{
			Lab5.lightSensorValues.fetchSample(Lab5.lightSensorData, 0);
			intensity = Lab5.lightSensorData[0] * 100;
			if(firstReading == -1) {
				firstReading = intensity;
			}
			else if ((100 * Math.abs((intensity - firstReading)/firstReading)) > lightThreshold) //if see a black line
			{
				if(intensity < firstReading) {
					Sound.beep();
					Lab5.leftMotor.stop(true);
					Lab5.rightMotor.stop(false);//stop motors
					firstReading = -1;
					Lab5.leftMotor.setSpeed(ROTATE_SPEED);
					Lab5.rightMotor.setSpeed(ROTATE_SPEED);
					Lab5.leftMotor.rotate(convertDistance(wheelRad, 7.5), true);
					Lab5.rightMotor.rotate(convertDistance(wheelRad, 7.5), false);
					Lab5.leftMotor.rotate(-convertAngle(wheelRad, track, quarterSpin), true);
					Lab5.rightMotor.rotate(convertAngle(wheelRad, track, quarterSpin), false);
					Lab5.leftMotor.rotate(convertDistance(wheelRad, 7.5), true);
					Lab5.rightMotor.rotate(convertDistance(wheelRad, 7.5), false);
					odometer.setX(0);
					odometer.setY(0);
					Thread.sleep(500);
					two =  false;
				}
			}
		}
		
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
