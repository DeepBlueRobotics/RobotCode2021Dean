package org.team199.lib;

import java.io.IOException;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team199.robot2020.subsystems.Drivetrain;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.PathfinderFRC;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;

public class RobotPath {

    private Trajectory leftTrajectory, rightTrajectory;
    private Drivetrain drivetrain;
    private Encoder leftEncoder, rightEncoder;
    private AHRS gyro;
    private EncoderFollower leftEncoderFollower, rightEncoderFollower;
    private Notifier notifier;
    private boolean isInit;

    public RobotPath(String pathName) throws IOException {
        leftTrajectory = PathfinderFRC.getTrajectory(pathName + ".left");
        leftTrajectory = PathfinderFRC.getTrajectory(pathName + ".right");
        isInit = false;
    }

    public void init(Drivetrain drivetrain, Encoder leftEncoder, Encoder rightEncoder, AHRS gyro, int ticksPerRev,
            double wheelDiameter, double maxVelocity) {
        if (isInit) {
            return;
        }
        this.drivetrain = drivetrain;
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
        rightEncoder.setReverseDirection(true);
        this.gyro = gyro;
        leftEncoderFollower = new EncoderFollower(leftTrajectory);
        leftEncoderFollower.configureEncoder(leftEncoder.get(), ticksPerRev, wheelDiameter);
        leftEncoderFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / maxVelocity, 0);
        rightEncoderFollower = new EncoderFollower(rightTrajectory);
        rightEncoderFollower.configureEncoder(rightEncoder.get(), ticksPerRev, wheelDiameter);
        rightEncoderFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / maxVelocity, 0);
        isInit = true;
    }

    public void start() {
        if (!isInit) {
            return;
        }
        notifier = new Notifier(this::followPath);
        notifier.startPeriodic(leftTrajectory.get(0).dt);
    }

    public void stop() {
        if (!isInit) {
            return;
        }
        notifier.stop();
    }

    private void followPath() {
        if (leftEncoderFollower.isFinished() || rightEncoderFollower.isFinished()) {
            notifier.stop();
        } else {
            double leftSpeed = leftEncoderFollower.calculate(leftEncoder.get());
            double rightSpeed = rightEncoderFollower.calculate(rightEncoder.get());
            double heading = gyro.getAngle();
            double desiredHeading = Pathfinder.r2d(leftEncoderFollower.getHeading());
            double headingDifference = Pathfinder.boundHalfDegrees(desiredHeading - heading);
            double turn = 0.8 * (-1.0 / 80.0) * headingDifference;
            if (!SmartDashboard.getBoolean("Characterized Drive", false)) {
                drivetrain.tankDrive(leftSpeed + turn, rightSpeed - turn);
            } else {
                double[] charParams = drivetrain.characterizedDrive(leftSpeed + turn, rightSpeed - turn);
                drivetrain.tankDrive(charParams[0], charParams[1]);
            }
        }
    }

}