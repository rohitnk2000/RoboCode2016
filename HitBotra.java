package rkrb;
import robocode.*;
import robocode.util.Utils;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Random;


/**
 * A modular bot adhering to the RoboPart Interface.
 * 
 * @author Rohit Kulkarni
 * @version 5/12/16
 * 
 * @author Period - 1
 * @author Assignment - PartsBot
 * 
 * @author Sources - none
 */
public class HitBotra extends AdvancedRobot
{

    Random rand = new Random();
    
    private AdvancedEnemyBot enemy = new AdvancedEnemyBot();

    private RobotPart[] parts = new RobotPart[3]; // make three parts

    private final static int RADAR = 0;

    private final static int GUN = 1;

    private final static int TANK = 2;
    



    /**
     * runs all of PartsBot's methods
     */
    public void run()
    {
        
            parts[RADAR] = new SingleRadar();
            parts[GUN] = new SingleGun();
            parts[TANK] = new SingleTank();
        // initialize each part
        for ( int i = 0; i < parts.length; i++ )
        {
            // behold, the magic of polymorphism
            parts[i].init();
        }

        // iterate through each part, moving them as we go
        for ( int i = 0; true; i = ( i + 1 ) % parts.length )
        {
            // polymorphism galore!
            parts[i].move();
            if ( i == 0 )
            {
                execute();
            }
        }
    }


    /**
     * tracks robot once enemy bot is scanned
     * 
     * @param e
     *            scanned robot event
     */
    public void onScannedRobot( ScannedRobotEvent e )
    {
        SingleRadar radar = (SingleRadar)parts[RADAR];
        if ( radar.shouldTrack( e ) )
        {
            enemy.update( e, this );
        }
        
    }


    /**
     * changes robot to track once original dies
     * 
     * @param e
     *            robot death event
     */
    public void onRobotDeath( RobotDeathEvent e )
    {
        SingleRadar radar = (SingleRadar)parts[RADAR];
        if ( radar.wasTracking( e ) )
        {
            enemy.reset();
        }
        
        
    }

    /**
     * 
     * TODO Write your method description here.
     * @param x1
     *          initial x
     * @param y1
     *          intial y
     * @param x2
     *          final x
     * @param y2
     *          final y
     * @return
     *          absolute bearing
     * TODO Write your method description here.
     * @param angle
     *              takes angle of bearing
     * @return
     *              returns normalized bearing
     */
    public double normalizeBearing( double angle )
    {
        while ( angle > 180 )
        {
            angle -= 360;
        }
        while ( angle < -180 )
        {
            angle += 360;
        }
        return angle;
    }


    // ... declare the RobotPart interface and classes that implement it here
    // They will be _inner_ classes.
    /**
     * 
        represents all of the parts of the robot
     */
    public interface RobotPart
    {
        /**
         * 
         * calls the init method of each part of the bot
         */
        public void init();

        /**
         * 
         * calls the move method of each part of the bot
         */
        public void move();
    }


    /**
     * 
        controls radar of robot part
     */
    public class SingleRadar implements RobotPart
    {
        /**
         * sets setAdjustRadarForGunTurn to true for radar
         */
        public void init()
        {
            setAdjustRadarForGunTurn( true );
        }


        /**
         * moves radar for robot
         */
        public void move()
        {
         // Absolute angle towards target
            double angleToEnemy = getHeadingRadians() + Math.toRadians( enemy.getBearing() );
         
            // Subtract current radar heading to get the turn required to face the enemy, be sure it is normalized
            double radarTurn = Utils.normalRelativeAngle( angleToEnemy - getRadarHeadingRadians() );
         
            // Distance we want to scan from middle of enemy to either side
            // The 36.0 is how many units from the center of the enemy robot it scans.
            double extraTurn = Math.min( Math.atan( 36.0 / enemy.getDistance() ), Rules.RADAR_TURN_RATE_RADIANS );
         
            // Adjust the radar turn so it goes that much further in the direction it is going to turn
            // Basically if we were going to turn it left, turn it even more left, if right, turn more right.
            // This allows us to overshoot our enemy so that we get a good sweep that will not slip.
            radarTurn += (radarTurn < 0 ? -extraTurn : extraTurn);
         
            //Turn the radar
            setTurnRadarRightRadians(radarTurn);
        }


        /**
         * This is the action taken when tracking an enemy bot
         * 
         * @param e
         *          scanned robot event
         * @return
         *          if enemy is a certain distance away
         */
        public boolean shouldTrack( ScannedRobotEvent e )
        {
            // track if we have no enemy, the one we found is significantly
            // closer, or we scanned the one we've been tracking.
            return ( enemy.none() || e.getDistance() < enemy.getDistance() - 70
                || e.getName().equals( enemy.getName() ) );
        }


        /**
         * 
         * gets name of robot that was being tracked
         * @param e
         *          scanned robot death
         * @return
         *          the name of the robot being tracked
         */
        public boolean wasTracking( RobotDeathEvent e )
        {
            return e.getName().equals( enemy.getName() );
        }
    }

    /**
     * controls gun of the tank
     */
    public class SingleGun implements RobotPart
    {
        /**
         * sets setAdjustGunForRobotTurn to true
         */
        public void init()
        {
            setAdjustGunForRobotTurn( true );
        }

        /**
         * moves gun of robot using other methods
         */
        public void move()
        {
            // don't shoot if I've got no enemy
            if ( enemy.none() )
            {
                return;
            }
            if(enemy.getDistance() > 20)
            {
            // calculate firepower based on distance
            double firePower = Math.min( 500 / enemy.getDistance(), 3 );
            // calculate speed of bullet
            double bulletSpeed = 20 - firePower * 3;
            // distance = rate * time, solved for time
            long time = (long)( enemy.getDistance() / bulletSpeed );

            // calculate gun turn to predicted x,y location
            double futureX = enemy.getFutureX( time);
            double futureY = enemy.getFutureY( time);
            double absDeg = absoluteBearing( getX(), getY(), futureX, futureY );
            // non-predictive firing can be done like this:
//             double absDeg = absoluteBearing(getX(), getY(), enemy.getX(),
//             enemy.getY());

            // turn the gun to the predicted x,y location
            setTurnGunRight( normalizeBearing( absDeg - getGunHeading() ) );

            // if the gun is cool and we're pointed in the right direction, shoot!
            if ( getGunHeat() == 0 && Math.abs( getGunTurnRemaining() ) < 10 )
            {
                setFire( firePower );
            }
            }
            else
            {
                
                double absDeg = absoluteBearing(getX(), getY(), enemy.getX(),
                  enemy.getY());
                setTurnGunRight( normalizeBearing( absDeg - getGunHeading() ) );
                setFire(10);
            }
            
        }
        /**
         * TODO Write your method description here.
         * @param x1
         *          initial x
         * @param y1
         *          initial y
         * @param x2
         *          final x
         * @param y2
         *          final y
         * @return
         */
        double absoluteBearing( double x1, double y1, double x2, double y2 )
        {
            double xo = x2 - x1;
            double yo = y2 - y1;
            double hyp = Point2D.distance( x1, y1, x2, y2 );
            double arcSin = Math.toDegrees( Math.asin( xo / hyp ) );
            double bearing = 0;

            if ( xo > 0 && yo > 0 )
            { // both pos: lower-Left
                bearing = arcSin;
            }
            else if ( xo < 0 && yo > 0 )
            { // x neg, y pos: lower-right
                bearing = 360 + arcSin; // arcsin is negative here, actually 360 -
                                        // ang
            }
            else if ( xo > 0 && yo < 0 )
            { // x pos, y neg: upper-left
                bearing = 180 - arcSin;
            }
            else if ( xo < 0 && yo < 0 )
            { // both neg: upper-right
                bearing = 180 - arcSin; // arcsin is negative here, actually 180 +
                                        // ang
            }

            return bearing;
        }

    }

    /**
        controls tank of robot
     */
    public class SingleTank implements RobotPart
    {
        int turnDirection = 1;
        /**
         * changes colors of robot to red, white, and blue
         */
        public void init()
        {
            setColors( Color.black, Color.red, Color.red );
        }

        /**
         * changes the direction that the robot is moving
         */
        
        
        public byte moveDirection = -1;

        /**
         * moves the tank of the robot
         */
        
        public void move()
        {
            // switch directions if we've stopped
            if (getVelocity() == 0)
              moveDirection *= -1;
            if(enemy.getDistance() < 20)
            {
                setTurnRight(normalizeBearing( enemy.getBearing()) + 180 + (rand.nextInt(20) - 20));
                //add some randomness so that enemyBot cannot directly hit us
                setAhead(rand.nextInt(40));
                //more random so that we don't get hit
            }
            //only spiral w/o random if we are a certain distance away               
            if(enemy.getDistance() < 60)
            {
              setTurnRight(getHeading() * moveDirection);
            

             // spiral toward our enemy
             setTurnRight( normalizeBearing( enemy.getBearing() + 90
                 - ( 15 * moveDirection ) ) );
             setAhead( enemy.getDistance() * moveDirection );
            }
            
            else
            {
                // always square off against our enemy
                setTurnRight(normalizeBearing(enemy.getBearing() + 90));
                //checks to see if the robot is near any walls on the plane
                if(((getX() < getBattleFieldWidth() - 20) || getX() > 20) && (getY() < getBattleFieldHeight() - 20) || getY() > 20)
                {
                    // always square off against our enemy
                    setTurnRight(normalizeBearing(enemy.getBearing() + 110));
                    //110 instead of 90 so that enemy bot can't predict where 
                    //we will move after hitting a wall

                    // circle our enemy
                    setAhead(1000 * moveDirection);
                }
                else
                {
                    setTurnRight(getHeading() * moveDirection + rand.nextInt(90));
                    //circle randomly
                    setAhead(200 * moveDirection);
                }
            }
            
            
       
            
            
        }
          
        }
}