package ca.mcgill.ecse211.testing;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import ca.mcgill.ecse211.project.GetRing;
import ca.mcgill.ecse211.project.Navigation;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

/**
 * This class tests colour detection after ring is grabbed
 * @author Lucy Coyle
 */

public class TestColourDetection {

	public static Navigation navigation;
	
	public static final double WHEEL_RAD = 2.1;
	public static final double TRACK = 11.2;
	
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	
	public static Port colourPort = LocalEV3.get().getPort("S3");

	public static final TextLCD lcd = LocalEV3.get().getTextLCD();
	
	public static Odometer odometer;

	public static EV3ColorSensor colourSensor = new EV3ColorSensor(colourPort);
	public static SampleProvider colourSensorValues = colourSensor.getRGBMode();
	static float[] colourSensorData = new float[3];

	/**
	 * This main method implements the logic for the localization test
	 * 
	 * 1. create a new instance of the GetRing class
	 * 2. call grabRing()
	 * 3. print the color detected depending on what is read 
	 * 
	 * @throws OdometerExceptions
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws OdometerExceptions, InterruptedException{
		odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);

		navigation = new Navigation(leftMotor, rightMotor, TRACK, WHEEL_RAD, odometer);
		

		// clear the display
		lcd.clear();

		lcd.drawString("Press any button", 0, 0);
		lcd.drawString("    to start    ", 0, 1);
		
		Button.waitForAnyPress();
		
		Thread odoThread = new Thread(odometer);
		odoThread.start();
		
		(new Thread() {
			public void run() {
				GetRing g = new GetRing(colourSensorValues, colourSensorData, navigation);
				g.grabRing();
				int c = g.senseRing();
				switch(c){
					case 1:
						lcd.drawString("BLUE", 0, 2);
						break;
					case 2:
						lcd.drawString("GREEN", 0, 2);
						break;
					case 3:
						lcd.drawString("YELLOW", 0, 2);
						break;
					case 4:
						lcd.drawString("ORANGE", 0, 2);
						break;
					case -1:
						lcd.drawString("ERROR", 0, 2);
						break;
				}
			}
		}).start();
		
		Button.waitForAnyPress(); 
		System.exit(0);
	}
}
