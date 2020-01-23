package org.team199.robot2020.subsystems;

import edu.wpi.first.wpilibj2.command.PIDSubsystem;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.controller.PIDController;

import org.team199.lib.MotorControllerFactory;
import org.team199.robot2020.Constants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;

public class Shooter extends PIDSubsystem {
    private final WPI_VictorSPX flywheel = MotorControllerFactory.createVictor(Constants.Drive.FLYWHEEL_MOTOR);
    private final Encoder encoder = new Encoder(new DigitalInput(4), new DigitalInput(5), false, EncodingType.k1X );
    private final SimpleMotorFeedforward ff = new SimpleMotorFeedforward(-3.42857142857, 6.0/35);
    private double targetSpeed;

    public Shooter() {
        super(new PIDController(Constants.Shooter.KP, Constants.Shooter.KI, Constants.Shooter.KD));
        enable();
        setTargetSpeed(0);
        SmartDashboard.putNumber("Shooter Target Speed", 0);
        SmartDashboard.putNumber("Shooter kP", 0);
        SmartDashboard.putNumber("Shooter kI", 0);
        SmartDashboard.putNumber("Shooter kD", 0);
        SmartDashboard.putNumber("Shooter kV", 0);
        SmartDashboard.putNumber("Shooter kS", 0);
        encoder.setDistancePerPulse(-1/8.75);
        encoder.setSamplesToAverage(24);
    }

    public void useOutput(double output, double setpoint) { // set flywheel speed
        flywheel.setVoltage(output + ff.calculate(setpoint)); // TODO: add feedforward
    }
    

    public double getMeasurement() { // get current speed
        return encoder.getRate();
    }

    public double getCurrentDistance() {
        return encoder.getDistance();
    }

    public double getTargetSpeed() {
        return targetSpeed;
    }

    public void setTargetSpeed(double speed) {
        setSetpoint(speed);
        targetSpeed = speed;
    }

    public double getP() {
        return getController().getP();
    }

    public void setP(double kP) {
        getController().setP(kP);
    }

    public double getI() {
        return getController().getI();
    }

    public void setI(double kI) {
        getController().setI(kI);
    }

    public double getD() {
        return getController().getD();
    }

    public void setD(double kD) {
        getController().setD(kD);
    }

    
}