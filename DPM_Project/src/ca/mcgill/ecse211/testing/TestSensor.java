package ca.mcgill.ecse211.testing;

import ca.mcgill.ecse211.project.GetRing;

/**
 * This class tests the sensors
 * @author Lucy Coyle
 */

import ca.mcgill.ecse211.project.LightPoller;
import ca.mcgill.ecse211.project.UltrasonicPoller;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * This class implements a test of the light, color, and ultrasonic sensors
 * @author Lucy Coyle
 */

public class TestSensor {
	
	//Set up us sensor
	private static final Port usPort = LocalEV3.get().getPort("S2");
//	public static SensorModes usSensor = new EV3UltrasonicSensor(usPort); 
//	public static SampleProvider usDistance = usSensor.getMode("Distance"); 
//	public static float[] usData = new float[usDistance.sampleSize()];
	
	//Set up light sensor
	public static Port lightPort = LocalEV3.get().getPort("S1");
//	public static EV3ColorSensor lightSensor = new EV3ColorSensor(lightPort);
//	public static SampleProvider lightSensorValues = lightSensor.getRedMode();
//	static float[] lightSensorData = new float[lightSensor.sampleSize()];

	//Set up screen display
	public static final TextLCD lcd = LocalEV3.get().getTextLCD();
	
	//Declare sensor poller
	private static UltrasonicPoller ultrasonicPoller;
	private static LightPoller lightPoller;

	/**
	 * This main method implements the logic for the sensor test
	 */
	public static void main(String[] args){

		// clear the display
		lcd.clear();

		// Choose light or ultrasonic sensor test
		lcd.drawString("< Left   | Right     >", 0, 0);
		lcd.drawString("         |            ", 0, 1);
		lcd.drawString(" light   | ultrasonic ", 0, 2);
		lcd.drawString(" test    | test       ", 0, 3);
		
		int buttonChoice = Button.waitForAnyPress();

		if(buttonChoice == Button.ID_LEFT) {
			lcd.clear();

			// Choose light or ultrasonic sensor test
			lcd.drawString("< Left   | Right     >", 0, 0);
			lcd.drawString("         |            ", 0, 1);
			lcd.drawString(" line    | colour     ", 0, 2);
			lcd.drawString(" test    | test       ", 0, 3);
			buttonChoice = Button.waitForAnyPress();
			
			if(buttonChoice == Button.ID_LEFT) {
				lightSensorLineTest();
			}
			else {
				lightSensorColourTest();
			}
			
		}
		else {
			ultrasonicSensorTest();
		}
		
		Button.waitForAnyPress(); 
		System.exit(0);
	}
	
	/**
	 * This main method implements the logic for the light sensor colour test
	 * 1. create a new instance of GetRing
	 * 2. call colourClassify on getRing 
	 * 3. print the color read by the light sensor to the lcd display
	 */
	
	public static void lightSensorColourTest() {
//		lightSensorValues = lightSensor.getRGBMode();
//		lightSensorData = new float[3];
//		GetRing getRing = new GetRing(lightSensorValues, lightSensorData);
		lcd.clear();
		
		while(true){
			int c = 1;
//			int c = getRing.senseRing();
			if(c != -1) {
				lcd.drawString("Object Detected", 0, 0);
				switch (c){
				case 1: lcd.drawString("Blue", 0, 1);
					break;
				case 2: lcd.drawString("Green", 0, 1);
					break;
				case 3: lcd.drawString("Yellow", 0, 1);
					break;
				case 4: lcd.drawString("Orange", 0, 1);
					break;
				}
			}
			try {
				Thread.sleep(900);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lcd.clear();
		}
		
	}
	
	/**
	 * This main method implements the logic for the light sensor line detection test
	 * 1. create a new instance of LightPoller
	 * 2. call detectLine() on getRing 
	 * 3. if a line is detected, beep
	 */
	public static void lightSensorLineTest() {
		
		lightPoller = new LightPoller(lightPort);
		(new Thread() {
			public void run() {
				try {
					while(true){
						if(TestSensor.lightPoller.detectLine() == true) {
							Sound.beep();
						};
						Thread.sleep(50);
					}
				}catch(InterruptedException e) {
					Sound.beep();
				}
			}
		}).start();		
	}
	
	
	/**
	 * This main method implements the logic for the ultrasonic sensor test
	 * 
	 * 1. create a new instance of UltrasonicPoller 
	 * 2. print the result of getDistance to the lcd display which is the 
	 * 	  distance read by the ultrasonic sensor
	 */
	public static void ultrasonicSensorTest() {
		ultrasonicPoller = new UltrasonicPoller(usPort);

		(new Thread() {
			public void run() {
				try {
					while(true){
						lcd.clear();
						lcd.drawString("Distance" + TestSensor.ultrasonicPoller.getDistance(), 0, 0);
						Thread.sleep(1000);
					}
				}catch(InterruptedException e) {
					Sound.beep();
				}
			}
		}).start();
	}
}
