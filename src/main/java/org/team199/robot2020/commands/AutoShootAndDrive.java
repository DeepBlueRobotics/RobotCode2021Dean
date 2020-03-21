package org.team199.robot2020.commands;

import com.playingwithfusion.TimeOfFlight;

import org.team199.lib.Limelight;
import org.team199.lib.RobotPath;
import org.team199.robot2020.subsystems.Drivetrain;
import org.team199.robot2020.subsystems.Feeder;
import org.team199.robot2020.subsystems.Intake;
import org.team199.robot2020.subsystems.Shooter;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class AutoShootAndDrive extends SequentialCommandGroup {
    public AutoShootAndDrive(Drivetrain drivetrain, Intake intake, Feeder feeder, Shooter shooter, 
                             Limelight lime, Translation2d target, PowerDistributionPanel pdp,
                             int feederPDPPort, RobotPath[] paths, RobotPath.Path path) {

        TimeOfFlight shooterDistanceSensor = feeder.getShooterDistanceSensor();
        ShooterHorizontalAim aim = new ShooterHorizontalAim(drivetrain, lime);
        AutoShoot shoot = new AutoShoot(feeder, intake, shooter, shooterDistanceSensor, 3);
        switch(path) {
            case PATH1:
            case PATH2:
            case PATH3:
                addCommands(
                    aim,
                    shoot,
                    new ParallelCommandGroup(
                        paths[0].getPathCommand(),   new AutoBallPickup(feeder, intake, drivetrain, pdp, feederPDPPort, 5)
                    ),
                    aim,
                    shoot
                );
                break;
            case PATH4:
                addCommands(
                    new ParallelCommandGroup(
                        paths[0].getPathCommand(),   new AutoBallPickup(feeder, intake, drivetrain, pdp, feederPDPPort, 2)
                    ),
                    aim,
                    shoot,
                    new ParallelCommandGroup(
                        paths[1].getPathCommand(),   new AutoBallPickup(feeder, intake, drivetrain, pdp, feederPDPPort, 2)
                    ),
                    new ParallelCommandGroup(
                        paths[2].getPathCommand(),   new AutoBallPickup(feeder, intake, drivetrain, pdp, feederPDPPort, 1)
                    ),
                    aim, 
                    shoot
                );
                break;
            case PATH5:
            case PATH6:
            case PATH7:
            case OFF:
            default:
                break;
        }
    }
}