package ca.mcgill.ecse211.testing;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerDisplay;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import ca.mcgill.ecse211.project.LightLocalizer;


/**
 * This class tests localization
 * @author Lucy Coyle
 */

import ca.mcgill.ecse211.project.LightPoller;
import ca.mcgill.ecse211.project.Navigation;
import ca.mcgill.ecse211.project.UltrasonicLocalizer;
import ca.mcgill.ecse211.project.UltrasonicPoller;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.Sound;

/**
 * This class implements a test of the localization
 * @author Lucy Coyle
 */

public class TestLocalization {
	
	public static LightPoller lightPollerL;
	public static LightPoller lightPollerR;
	public static UltrasonicPoller usPoller;
	public static Navigation navigation;
	
	public static final double WHEEL_RAD = 2.1;
	public static final double TRACK = 11.2;
	
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	
	private static final Port usPort = LocalEV3.get().getPort("S2");
	public static Port lightPort1 = LocalEV3.get().getPort("S1");
	public static Port lightPort2 = LocalEV3.get().getPort("S3");

	public static final TextLCD lcd = LocalEV3.get().getTextLCD();
	
	public static Odometer odometer;
	
	public static UltrasonicLocalizer usLocalizer;
	public static LightLocalizer lightLocalizer;

	/**
	 * This main method implements the logic for the localization test
	 * @throws OdometerExceptions
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws OdometerExceptions, InterruptedException{
		odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);
		OdometerDisplay odometryDisplay = new OdometerDisplay(lcd);

		navigation = new Navigation(leftMotor, rightMotor, TRACK, WHEEL_RAD, odometer);
		

		// clear the display
		lcd.clear();

		// Choose partial or full sensor test
		lcd.drawString("< Left  | Right   >", 0, 0);
		lcd.drawString("        | full     ", 0, 1);
		lcd.drawString(" partial| localize ", 0, 2);
		lcd.drawString(" test   | test     ", 0, 3);
		
		int buttonChoice = Button.waitForAnyPress();
		Thread odoThread = new Thread(odometer);
		odoThread.start();
		Thread odoDisplayThread = new Thread(odometryDisplay);
		odoDisplayThread.start();
		
		if(buttonChoice == Button.ID_LEFT) {
			lcd.clear();

			// Choose light or ultrasonic localization test
			lcd.drawString("< Left   | Right     >", 0, 0);
			lcd.drawString("         |            ", 0, 1);
			lcd.drawString(" light   | us         ", 0, 2);
			lcd.drawString(" test    | test       ", 0, 3);
			buttonChoice = Button.waitForAnyPress();
			
			if(buttonChoice == Button.ID_LEFT) {
				(new Thread() {
					public void run() {
						lightPollerL = new LightPoller(lightPort1);
						lightPollerR = new LightPoller(lightPort2);
						lightLocalizer = new LightLocalizer(odometer, lightPollerL, lightPollerR, navigation);
						lightLocalizer.localize();
					}
				}).start();
			}
			else {
				(new Thread() {
					public void run() {
						usPoller = new UltrasonicPoller(usPort);
						usLocalizer = new UltrasonicLocalizer(odometer, usPoller, navigation);
						usLocalizer.localize();
					}
				}).start();
			}
			
		}
		else {	
			(new Thread() {
				public void run() {
					Sound.beep();
					lightPollerL = new LightPoller(lightPort1);
					lightPollerR = new LightPoller(lightPort2);
					usPoller = new UltrasonicPoller(usPort);
					
					usLocalizer = new UltrasonicLocalizer(odometer, usPoller, navigation);
					lightLocalizer = new LightLocalizer(odometer, lightPollerL, lightPollerR, navigation);
					
					usLocalizer.localize();
					Sound.beep();
					lightLocalizer.localize();
				}
			}).start();
		}
		
		Button.waitForAnyPress(); 
		System.exit(0);
	}
}
