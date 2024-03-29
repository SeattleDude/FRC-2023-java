// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

import java.text.DecimalFormat;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
import edu.wpi.first.wpilibj.motorcontrol.Spark;

import static frc.robot.slowModeEnums.*;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  
  private final Spark m_UpperDowner = new Spark(3); // up and down motor for the elevator thing
  private final Spark m_innerOuter = new Spark(2); // in and out for the arm
  private final Spark m_grabber = new Spark(4); // grabber for the arm
  // make another spark motorcontroller (m_prettyLights)
  private final Spark m_prettyLights = new Spark(9);

  private final PWMVictorSPX m_leftDrive = new PWMVictorSPX(0);
  private final PWMVictorSPX m_rightDrive = new PWMVictorSPX(1);
  
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftDrive, m_rightDrive);

  private final XboxController m_driver = new XboxController(0); // init a controller (Joystick) object that is on USB port 0
  private final XboxController m_operator = new XboxController(1); // another for the operator of the system
  private final Timer m_timer = new Timer();
  public static slowModeEnums SlowMode = OFF; // init slowmode to off // NEED to test

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_leftDrive.setInverted(true); // I set the left side to be inverted, 
    // this should fix the driving backwards with forwards input problem

    CameraServer.startAutomaticCapture(0); // this should fix the problem with no camera feed

  }

  @Override
  public void robotPeriodic() {
    if (DriverStation.getAlliance() == Alliance.Red) {            // This code here should make the LED strips turn 
      m_prettyLights.set(0.61);                             // the same color as the alliance color
    } else if (DriverStation.getAlliance() == Alliance.Blue) {
      m_prettyLights.set(0.87);
    }
  }

  /** This function is run once each time the robot enters autonomous mode. */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // Drive for 2 seconds
    if (m_timer.get() < 2.0) {
      m_robotDrive.tankDrive(0.5, 0.5); // drive forwards half speed
    } else {
      m_robotDrive.stopMotor(); // stop robot
    }
  }

  /** This function is called once each time the robot enters teleoperated mode. */
  @Override
  public void teleopInit() {}

  DecimalFormat df = new DecimalFormat("#.#"); // this might not even be used IDK 

  boolean startPressed = false; // init as false for sureness (that's a word)

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {

    boolean rightBumpPressed = m_driver.getRightBumperPressed();
    boolean leftBumpPressed = m_driver.getLeftBumperPressed();

    double leftY = m_driver.getLeftY(); // get inputs from sticks
    double rightY = m_driver.getRightY();

    // arm operator control inputs
    double OPLeftY = m_operator.getLeftY(); // get left input for arm operator (Add the axis on the bot this corresponds to)
    double OPRightY = m_operator.getRightY(); // get right input for arm operator
    boolean OPLeftTrig = m_operator.getAButton(); // in control for arm
    boolean OPRightTrig = m_operator.getYButton(); // out control for arm

    boolean leftPressed = false; // init val for if the triggers are pressed
    boolean rightPressed = false;

    m_innerOuter.setInverted(true); // set these to inverted because the controllers are backwards with their joysticks
    m_UpperDowner.setInverted(true);

    
    // if (OPLeftTrig > 0.8) {
    //   leftPressed = true;
    //   System.out.println("left trig pressed [OP]");
    // } else if (OPRightTrig > 0.8) {
    //   rightPressed = true;
    //   System.out.println("right trig pressed [OP]");
    // } else {
    //   leftPressed = false;
    //   rightPressed = false;
    //   System.out.println("Neither Trigger is pressed [OP]");
    // }

    if (OPRightTrig) {
      m_grabber.set(-1); // make grabber retract
      System.out.println("Making grabber retract! [OP]");
    } else if (OPLeftTrig) {
      m_grabber.set(1); // make grabber extend
      System.out.println("Making grabber Grab (Extend)! [OP]");
    } else {
      m_grabber.set(0);
    }


    // left OP joystick for up-down
    // right OP joystick for in-out
    // left/right OP triggers for Grabber (right squeeze, left release)
    
    m_innerOuter.set(OPRightY * 0.66); // set the inner and outer motor to move according to the operator input
    System.out.println("inner Outer move! [OP]" + OPRightY * 0.66); // divide by 2 on the signal sent to slow the movement

    m_UpperDowner.set(-OPLeftY); // go up or down based on the input given from the operator joysticks, might need to be reversed; it does
    System.out.println("inner Outer move! [OP]" + OPLeftY);

    if (rightBumpPressed) {
      SlowMode = ON;
      m_robotDrive.tankDrive(m_driver.getLeftY() / 2, m_driver.getRightY() / 2); //  div by 2 to get half speed for slow mode
      System.out.println("Slowmode Active!");
    } else if (leftBumpPressed) {
      SlowMode = OFF;
      m_robotDrive.tankDrive(leftY, rightY, false);  // might still cause a backwards problem, We'll see; it did, fixed? WOOOOOOOOOOOO IT WORKSSSSSSS
      // the squared inputs are set to true by default it seems... hopefully this fixes the slowness problem
    }

    if (SlowMode == ON) {
      m_robotDrive.tankDrive(leftY / 2, rightY / 2); // slowmode drive speed
      System.out.println("Slowmode Drive!");
    } else if (SlowMode == OFF) {
      System.out.println("Slowmode Drive OFF!");
      m_robotDrive.tankDrive(leftY, rightY, false); // regular drive mode
    }

  }

  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    m_prettyLights.set(0.57); // should set LED strips to be a hot pink in test mode  
  }
}
