// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
// import frc.robot.slowMode;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  private final PWMVictorSPX m_leftDrive = new PWMVictorSPX(0);
  private final PWMVictorSPX m_rightDrive = new PWMVictorSPX(1);
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftDrive, m_rightDrive);
  private final XboxController m_driver = new XboxController(0); // init a controller (Joystick) object that is on USB port 0
  // private final Joystick m_operator = new Joystick(1); // another for the operator of the system
  private final Timer m_timer = new Timer();
  public static slowMode SlowMode = slowMode.OFF;
  // private boolean slowMode;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_rightDrive.setInverted(true);
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

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    if (m_driver.getStartButton()) {
      SlowMode = SlowMode == slowMode.ON ? slowMode.OFF : slowMode.ON;
    }
    if (SlowMode == slowMode.OFF) {
    m_robotDrive.tankDrive(m_driver.getLeftY(), m_driver.getRightY());
    //System.out.println(-m_driver.getLeftY() + " Left stick val\n"); // I put a - in front of the y val to invert 
    //System.out.println(-m_driver.getRightY() + " Right stick val\n"); // so forward is positive and back is neg, 
    // this is inverse to the actual values that are input in the GUI
    }
    m_robotDrive.tankDrive(m_driver.getLeftY() / 2, m_driver.getRightY() / 2); //  div by 2 to get half speed
  }

  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
