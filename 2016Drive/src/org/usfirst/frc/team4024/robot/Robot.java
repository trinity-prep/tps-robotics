
package org.usfirst.frc.team4024.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	//Misc Declartion
    String autoSelected;
    SendableChooser chooser;
    CameraServer camera;
	
    //Control IO Declartion
    Joystick joy;
    Joystick armCon;
    Joystick io;
    //Button IO Declartion
    JoystickButton kickerBut;
	JoystickButton kickerButBack;
	JoystickButton armLeft;
	JoystickButton armRight;
	JoystickButton armBoth;
	JoystickButton lockLeft;
	JoystickButton lockRight;
	JoystickButton kickerHalf;
	JoystickButton shieldUp;
	JoystickButton shieldDown;
	JoystickButton lockArmUp;
	JoystickButton lockArmDown;
	
	//Motor Con Declartion
	CANTalon rl;
	CANTalon fl;
	CANTalon fr;
	CANTalon rr;
	//Talon kickerTal;
	
	Talon liftGear;
	
	Talon leftArm;
	Talon shieldMaster;
	Talon shieldSlave;
	Talon lockArm;
	
	//Sensor Dec
	DigitalInput lim1;
	Encoder leftArmEncode;
	Encoder shieldMasterEncode;
	Encoder shieldSlaveEncode;
	Encoder lockArmEncode;
	
	//Control Var Dec
	boolean armSideLeft;
	boolean armSideRight;
	boolean armsTogether;
	boolean leftLocked;
	double leftArmSetPosition;
	boolean lockLeftButtonState;
	boolean prevLeftLockButtonState;
	boolean isOperatorControl = true;
	
	//Control Constants DecDef
	final double pGain = 0.0185;
	final String standardAuto = "Standard";
	final String flapAuto = "Flap";
	final String roughAuto = "Rough Terrain";
	final String rampartsAuto = "Ramparts";
	final String moatAuto = "Moat";
	final String playDefense = "Play Defense";
	final String rockWallAuto = "Rock Wall";
	final double leftArmMechLockPosition = 170;
	
    public void robotInit() {
    	//Misc Def
        chooser = new SendableChooser();
        chooser.addDefault("Standard", standardAuto);
        chooser.addObject("Flap", flapAuto);
        chooser.addObject("Rough Terrain", roughAuto);
        chooser.addObject("Ramparts", rampartsAuto);
        chooser.addObject("Moat", moatAuto);
        chooser.addObject("Play Defense", playDefense);
        chooser.addObject("Rock Wall", rockWallAuto);
        SmartDashboard.putData("Auto choices", chooser);
        camera = CameraServer.getInstance();
        
        //Control IO Def
        joy = new Joystick(0);
        armCon = new Joystick(1);
        io = new Joystick(2);
        
        //Button Def
        kickerBut = new JoystickButton(io,11);
    	kickerButBack = new JoystickButton(io,8);
    	kickerHalf = new JoystickButton(joy,4);
    	shieldUp= new JoystickButton(io,2);
    	shieldDown = new JoystickButton(io,5);
    	lockArmUp = new JoystickButton(io,6);
    	lockArmDown = new JoystickButton(io,7);
    	
    	//Current Arm Button Definitions (not 3d)
    	lockLeft = new JoystickButton(armCon,1);
        
        //Motor Con Def
    	rl = new CANTalon(2); //was 4
    	fl = new CANTalon(1); //was 3
    	fr = new CANTalon(4); //was 1
    	rr = new CANTalon(3); //was 2
    	//kickerTal = new Talon(0);
    	
    	liftGear = new Talon(6);
    	
    	leftArm = new Talon(2);
    	shieldMaster = new Talon(3);
    	shieldSlave = new Talon(0);
    	lockArm = new Talon(1);
    	
    	//Motor Con Config
    	rl.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
    	fl.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
    	fr.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
    	rr.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
    	rl.enableBrakeMode(false);
    	fl.enableBrakeMode(false);
    	fr.enableBrakeMode(false);
    	rr.enableBrakeMode(false);
    	
    	//Sensor Def
    	leftArmEncode = new Encoder(0,1);
    	shieldMasterEncode = new Encoder(2,3);
    	shieldSlaveEncode = new Encoder(4,5);
    	lockArmEncode = new Encoder(6,7);
    	
    	//Control Variables Def
    	armSideLeft = false;
    	armSideRight = false;
    	armsTogether = false;
    	leftLocked = false;
    	leftArmSetPosition = 0;
    	lockLeftButtonState = false;
    	prevLeftLockButtonState = false;
    	
    	//Camera Config
    	camera.setQuality(30);
    	camera.startAutomaticCapture();
    }
    
    public void autonomousInit() {
    	autoSelected = (String)chooser.getSelected();
    	System.out.println("Auto selected: " + autoSelected);
    }

    public void autonomousPeriodic() 
    {
    	switch (autoSelected)
    	{
    		case flapAuto:
    			flapAutoMethod();
    			break;
    		case roughAuto:
    			roughAutoMethod();
    			break;
    		case moatAuto:
    			moatAutoMethod();
    			break;
    		case rampartsAuto:
    			rampartsAutoMethod();
    			break;
    		case standardAuto:
    			standardAutoMethod();
    			break;
    		case playDefense:
    			playDefenseAuto();
    			break;
    		case rockWallAuto:
    			rockWallAuto();
    			break;
    		default:
    			standardAutoMethod();
    			break;
    	}
    }

    public void teleopPeriodic() {
    	leftArmEncode.reset();
    	
    	leftArm.set(0);
    	
    	fr.set(0);
    	rr.set(0);
    	rl.set(0);
    	fl.set(0);

    	lockLeftButtonState = false;
    	prevLeftLockButtonState = false;
    	leftLocked = false;
    	leftArmSetPosition = 0;
    	
    	while (isOperatorControl() && isEnabled())
        {
    		//Drive Code
        	if(Math.abs(joy.getRawAxis(1)) > 0.3 || Math.abs(joy.getRawAxis(5)) > 0.3)
        	{
        		rl.set(joy.getRawAxis(1));
        		fl.set(joy.getRawAxis(1));
        		fr.set(-joy.getRawAxis(5));
        		rr.set(-joy.getRawAxis(5));
        	}
        	else
        	{
	        	fl.set(0);
	        	rl.set(0); 
	        	fr.set(0);
	        	rr.set(0);
        	}
        	
        	//Kicker Code
        	/*
        	if (kickerBut.get()== true)
        	{
        		kickerTal.set(1);
        	}
        	else if (kickerButBack.get() == true)
        	{
        		kickerTal.set(-0.2);
        	}
        	else if (kickerHalf.get()== true)
        	{
        		kickerTal.set(0.5);
        	}
        	else
        	{
        		kickerTal.set(0);
        	}
        	*/
        	
        	//Toggle Left Lock State
        	lockLeftButtonState = lockLeft.get();
        	if (lockLeftButtonState && !prevLeftLockButtonState)
        	{
        		leftLocked = !leftLocked;
        		leftArmSetPosition = Math.abs(leftArmEncode.get());
        	}
        	prevLeftLockButtonState = lockLeftButtonState;
        	
        	//Control Arms
        	if (leftLocked == false)
        	{
	        	if (true) //why?
	        	{
	        		if (Math.abs(armCon.getY()) >= 0.1)
	        		{
	        			leftArm.set(armCon.getY());
	        		}
	        		else if (!leftLocked)
	        			leftArm.set(0);
	        	}
        	}
        	else if (leftLocked == true)
        	{
        		leftArm.set(-((leftArmSetPosition-Math.abs(leftArmEncode.get())) * pGain));	
        	}
        	
        	//Shield Control3
        	if (shieldUp.get()==true  && shieldDown.get()!=true)
        	{
        		shieldMaster.set(-0.4);
        		shieldSlave.set(0.4);
        	}
        	else if (shieldDown.get()==true)
        	{
        		shieldMaster.set(0.1);
        		shieldSlave.set(-0.1);
        	}
        	else
        	{
        		shieldMaster.set(0);
        		shieldSlave.set(0);
        	}
        	
        	//Shield Prop Control
        	if (lockArmUp.get() == true && lockArmDown.get() == false)
        	{
        		lockArm.set(-0.5);
        	}
        	else if (lockArmDown.get() == true)
        	{
        		lockArm.set(0.3);
        	}
        	else
        	{
        		lockArm.set(0);
        	}
        	
        	//Left Arm Values
        	SmartDashboard.putNumber("Left Arm Encoder Value:", Math.abs(leftArmEncode.get()));
        	SmartDashboard.putNumber("Left Arm Lock Value:", leftArmSetPosition);
        	SmartDashboard.putBoolean("Left Arm Lock State:", leftLocked);
        	SmartDashboard.putNumber("Left Arm Speed", (-(leftArmSetPosition-Math.abs(leftArmEncode.get())) * pGain));
        	
        	//Control Values
        	SmartDashboard.putNumber("Manual Servo Control Value", io.getY());
        	
        	SmartDashboard.putNumber("Arm Value", armCon.getY());
        }
    }
    
    public void testPeriodic() {
    
    }
    
    //these methods are 2016 specific
    public void standardAutoMethod()
    {
    	leftArmEncode.reset();
       	//leftArm.set(0.3);
       	
       	fr.set(0.4);
       	rr.set(0.4);
       	fl.set(-0.4);
       	rl.set(-0.4);
       	
       	double time = System.currentTimeMillis();
        	
        while (!isOperatorControl()  && isEnabled())
       	{
        	/*
        	if (leftArmEncode.get() >= 50)
        	{
        		leftArm.set(0);
        	}
        	*/
        	if (System.currentTimeMillis()-time >= 1000)
        	{
        		fr.set(0.2);
        		rr.set(0.2);
        		fl.set(-0.2);
        		rl.set(-0.2);
        	}
       	}
        	
        leftArmEncode.reset();
    }
    
    public void flapAutoMethod()
    {
    	leftArmEncode.reset();
    	double value = 107;
    	leftArm.set((value-Math.abs(leftArmEncode.get())) * (-1*pGain));
       	
       	double time = System.currentTimeMillis();
        	
        while (!isOperatorControl()  && isEnabled())
       	{	
        	leftArm.set((value-Math.abs(leftArmEncode.get())) * (-1*pGain));
        	
        	if (System.currentTimeMillis()-time >= 1000 && System.currentTimeMillis()-time <= 2500)
        	{
        		fr.set(0.6);
               	rr.set(0.6);
               	fl.set(-0.6);
               	rl.set(-0.6);
        	}
        	
        	if (System.currentTimeMillis()-time >= 2500)
        	{
        		fr.set(0);
        		rr.set(0);
        		fl.set(0);
        		rl.set(0);
        	}
       	}
    }
    
    public void roughAutoMethod()
    {
    	leftArmEncode.reset();
       	//leftArm.set(0.3);
       	
       	fr.set(0.8);
       	rr.set(0.8);
       	fl.set(-0.8);
       	rl.set(-0.8);
       	
       	double time = System.currentTimeMillis();
        	
        while (!isOperatorControl()  && isEnabled())
       	{
        	/*
        	if (leftArmEncode.get() >= 50)
        	{
        		leftArm.set(0);
        	}
        	*/
        	if (System.currentTimeMillis()-time >= 2750)
        	{
        		fr.set(0);
        		rr.set(0);
        		fl.set(0);
        		rl.set(0);
        	}
       	}
        	
        leftArmEncode.reset();
    }
    
    public void rampartsAutoMethod()
    {
    	leftArmEncode.reset();
       	//leftArm.set(0.3);
       	
       	fr.set(-0.8);
       	rr.set(-0.8);
       	fl.set(0.8);
       	rl.set(0.8);
       	
       	double time = System.currentTimeMillis();
        	
        while (!isOperatorControl()  && isEnabled())
       	{
        	/*
        	if (leftArmEncode.get() >= 50)
        	{
        		leftArm.set(0);
        	}
        	*/
        	if (System.currentTimeMillis()-time >= 3250)
        	{
        		fr.set(0);
        		rr.set(0);
        		fl.set(0);
        		rl.set(0);
        	}
       	}
        	
        leftArmEncode.reset();
    }
    
    public void moatAutoMethod()
    {
    	leftArmEncode.reset();
       	//leftArm.set(0.3);
       	
       	fr.set(-1);
       	rr.set(-1);
       	fl.set(1);
       	rl.set(1);
       	
       	double time = System.currentTimeMillis();
        	
        while (!isOperatorControl()  && isEnabled())
       	{
        	/*
        	if (leftArmEncode.get() >= 50)
        	{
        		leftArm.set(0);
        	}
        	*/
        	if (System.currentTimeMillis()-time >= 3000)
        	{
        		fr.set(0);
        		rr.set(0);
        		fl.set(0);
        		rl.set(0);
        	}
       	}
        	
        leftArmEncode.reset();
    }
    
    public void rockWallAuto()
    {
    	leftArmEncode.reset();
       	//leftArm.set(0.3);
       	
       	fr.set(0.8);
       	rr.set(0.8);
       	fl.set(-0.8);
       	rl.set(-0.8);
       	
       	double time = System.currentTimeMillis();
        	
        while (!isOperatorControl()  && isEnabled())
       	{
        	/*
        	if (leftArmEncode.get() >= 50)
        	{
        		leftArm.set(0);
        	}
        	*/
        	if (System.currentTimeMillis()-time >= 3250)
        	{
        		fr.set(0);
        		rr.set(0);
        		fl.set(0);
        		rl.set(0);
        	}
       	}
        	
        leftArmEncode.reset();
    }
    
    public void playDefenseAuto()
    {
    	leftArmEncode.reset();
    }
    
    //Autonomous Arm Code
    /*
    leftArmEncode.reset();
   	leftArm.set(0.3);
    	
    while (!isOperatorControl()  && isEnabled())
   	{
    	if (leftArmEncode.get() >= 50)
    	{
    		leftArm.set(0);
    	}
   	}
    	
    leftArmEncode.reset();
    */
    
}
