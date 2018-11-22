package ca.mcgill.ecse211.testing;

import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerDisplay;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import ca.mcgill.ecse211.project.GyroSensorPoller;
import ca.mcgill.ecse211.project.LightLocalizer;
import ca.mcgill.ecse211.project.LightPoller;
import ca.mcgill.ecse211.project.Navigation;
import ca.mcgill.ecse211.project.UltrasonicLocalizer;
import ca.mcgill.ecse211.project.UltrasonicPoller;
import ca.mcgill.ecse211.project.WiFiParameters;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

/**
 * This class implements the test for localization timing, ensuring under 30 seconds
 * @author Lucy Coyle
 */

public class TestLocalizationTiming {
	
	public static Navigation navigation;
	
	public static final double WHEEL_RAD = 2.1;
	public static final double TRACK = 10.3;
	
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	private static final Port usPort = LocalEV3.get().getPort("S2");
	public static Port lightPort = LocalEV3.get().getPort("S1");
//	public static Port colourPort = LocalEV3.get().getPort("S3");
	public static Port gyroPort = LocalEV3.get().getPort("S3");
	
	public static LightPoller lightPoller  = new LightPoller(lightPort);
	public static UltrasonicPoller usPoller = new UltrasonicPoller(usPort);
	public static GyroSensorPoller gPoller = new GyroSensorPoller(gyroPort);

	public static final TextLCD lcd = LocalEV3.get().getTextLCD();
	
	public static Odometer odometer;

	public static UltrasonicLocalizer usLocalizer;
	public static LightLocalizer lightLocalizer;
	
	//Game Parameters
	
	public static String teamColour;
	public static int corner;		
	public static int UR_x;		
	public static int LL_x;		
	public static int UR_y;		
	public static int LL_y;		
	public static int Island_UR_x;	
	public static int Island_LL_x;
	public static int Island_UR_y;
	public static int Island_LL_y;
	public static int Tunnel_UR_x;
	public static int Tunnel_LL_x;
	public static int Tunnel_UR_y;
	public static int Tunnel_LL_y;
	public static int Tree_x;
	public static int Tree_y;
	
	
	/**
	 * This main method implements the logic for the beta demo
	 * @throws OdometerExceptions 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws OdometerExceptions, InterruptedException {
		
		odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD);

		navigation = new Navigation(leftMotor, rightMotor, TRACK, WHEEL_RAD, odometer);
		
		usLocalizer = new UltrasonicLocalizer(odometer, usPoller, navigation);
		lightLocalizer = new LightLocalizer(odometer, lightPoller, navigation);
		
		// clear the display
		lcd.clear();

		// Press button to start
		lcd.drawString("Press any button", 0, 0);
		lcd.drawString("    to start    ", 0, 1);
		
		Button.waitForAnyPress();
		
		//Step 1: Receive parameters from game controller
		WiFiParameters p = new WiFiParameters();
		p.getParameters();
		teamColour = "green";
		corner = p.greenCorner;		
		UR_x = p.Green_UR_x;		
		LL_x = p.Green_LL_x;		
		UR_y = p.Green_UR_y;		
		LL_y = p.Green_LL_y;	
		Tunnel_UR_x = p.TNG_UR_x;
		Tunnel_LL_x = p.TNG_LL_x;
		Tunnel_UR_y = p.TNG_UR_y;
		Tunnel_LL_y = p.TNG_LL_y;
		Tree_x = p.TG_x;
		Tree_y = p.TG_y;
		Island_UR_x = p.Island_UR_x;	
		Island_LL_x = p.Island_LL_x;
		Island_UR_y = p.Island_UR_y;
		Island_LL_y = p.Island_LL_y;
		lcd.clear();
		
//		Button.waitForAnyPress();
		
		(new Thread() {
			public void run() {
				OdometerDisplay odometryDisplay = null;
				try {
					odometryDisplay = new OdometerDisplay(lcd);
				} catch (OdometerExceptions e1) {}
				
				Thread odoThread = new Thread(odometer);
				odoThread.start();
				Thread odoDisplayThread = new Thread(odometryDisplay);
				odoDisplayThread.start();
				
				//Step 2: Localize	
				usLocalizer.localize();
				try {
					lightLocalizer.localize();
				} catch (InterruptedException e) {
				}
				
				//Set x, y, theta based on corner
				navigation.initializeXYT(corner);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Sound.beep();
				Sound.beep();
				Sound.beep();
			}
		}).start();
		
		Button.waitForAnyPress(); 
		System.exit(0);
	}
	
}
