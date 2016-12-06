package rkrb;

import robocode.*;

import java.util.*;
import robocode.util.Utils;

import java.awt.Color;
import java.awt.geom.Point2D;


/**
 * A competitive robot utilizing many other snippets and our original coding to
 * battle and (hopefully) defeat HaveAtIt bot as well as peer robots.
 * 
 * @author Rohit Kulkarni & Ritik Batra
 * @version May 18, 2016
 * 
 * @author Period - 1
 * @author Assignment - Robocode Final
 * 
 * @author Sources - Robocode Repository :) and Mr. Peck (PartsBot skeleton)
 * 
 *         Code Snippets:
 *         Shooter -- http://205.173.41.10/robocode/RoboLessons.html#%5B%5B
 *                    Basic%20Targeting%5D%5D
 *         PartsBot -- http://205.173.41.10/robocode/RoboLessons.html#%5B%5B
 *                     Improved%20Movement%5D%5D
 *         Sensors -- http://205.173.41.10/robocode/RoboLessons.html#%5B%5B
 *                    Basic%20Scanning%5D%5D
 *         AbsoluteBearing -- Predictive Shooter
 *         NormalizeBearing -- Predictive Shooter
 *         Spiral movement -- Spiraler Bot
 *         Strafe movement -- Strafe Bot
 *                            http://205.173.41.10/robocode/RoboLessons.html#%
 *                            5B%5BBasic%20Movement%5D%5D
 *         Width Lock Radar 1v1 -- http://robowiki.net/wiki/One_on_One_Radar
 *         Shooter -- Predictive Shooter
 *         Gun Heat Lock Melee-- http://robowiki.net/wiki/Melee_Radar#Gun_Heat_
 *                               Lock
 *         
 * 
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
     * This runs all the methods in the code.
     */
    public void run()
    {
        parts[TANK] = new Tank();
        parts[GUN] = new Gun();
        parts[RADAR] = new Radar();

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
     * This is the event depending on if it gets hit by a bullet.
     * 
     * @param e
     *            This is the situation that it gets hit by a bullet.
     */
    public void onHitByBullet( HitByBulletEvent e )
    {
        // This is if the robot is hit by a bullet
        this.out.println( "Ouch! You hit me!" );
    }


    /**
     * This is the event depending on if it scans another robot.
     * 
     * @param e
     *            This is the situation that it scanned a robot.
     */
    public void onScannedRobot( ScannedRobotEvent e )
    {
        Radar radar = (Radar)parts[RADAR];
        if ( radar.shouldTrack( e ) )
        {
            // This looks for a new target
            this.out.println( "You are my new target!!" );
            enemy.update( e, this );
        }
    }


    /**
     * This is the event depending on if it scans a dead robot.
     * 
     * @param e
     *            This is the situation that it scanned a dead robot.
     */
    public void onRobotDeath( RobotDeathEvent e )
    {
        Radar radar = (Radar)parts[RADAR];
        if ( radar.wasTracking( e ) )
        {
            // This prints out Victory upon killing an enemy :)
            this.out.println( "VICTORY!!" );
            enemy.reset();
        }
    }


    /**
     * This is the event depending on if it hits the wall.
     * 
     * @param e
     *            This is the situation that it hits the wall.
     */
    public void onHitWall( HitWallEvent e )
    {
        // This is if the robot hits a wall
        this.out.println( "Ouch! I hit the wall :(" );
    }


    // ... put normalizeBearing and absoluteBearing methods here
    /**
     * computes the absolute bearing between two points
     * 
     * @param x1
     *            This is the original x.
     * @param y1
     *            This is the original y.
     * @param x2
     *            This is the next x.
     * @param y2
     *            This is the next y
     * @return bearing This is the double that describes the bearing to enemy.
     */
    public double absoluteBearing( double x1, double y1, double x2, double y2 )
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
            bearing = 360 + arcSin; // arcsin is negative here, actually 360
                                    // -
                                    // ang
        }
        else if ( xo > 0 && yo < 0 )
        { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        }
        else if ( xo < 0 && yo < 0 )
        { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180
                                    // +
                                    // ang
        }

        return bearing;
    }


    /**
     * normalizes a bearing to between +180 and -180
     * 
     * @param angle
     *            This is the angle to turn the radar.
     * @return angle This is the smallest angle change.
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


    /**
     * ... declare the RobotPart interface and classes that implement it here
     * They will be _inner_ classes.
     */
    public interface RobotPart
    {
        /**
         * This initializes the bot.
         */
        public void init();


        /**
         * This moves the bot.
         */
        public void move();

    }


    /**
     * This class controls the radar.
     */
    public class Radar implements RobotPart
    {
        /**
         * This controls the radar.
         */
        public void init()
        {
            // initialize radar operation
            setAdjustRadarForGunTurn( true );
        }


        /**
         * This moves the radar and turns it.
         */
        public void move()
        {
            // if there are less than 3 people.
            if ( getOthers() < 3 )
            {

                // Absolute angle towards target
                double angleToEnemy = getHeadingRadians()
                                + Math.toRadians( enemy.getBearing() );

                // Subtract current radar heading to get the turn required to
                // face
                // the enemy, be sure it is normalized
                double radarTurn = Utils.normalRelativeAngle( angleToEnemy
                    - getRadarHeadingRadians() );

                // Distance we want to scan from middle of enemy to either side
                // The 36.0 is how many units from the center of the enemy
                // robot it scans.
                double extraTurn = Math.min( Math.atan( 36.0
                    / enemy.getDistance() ), Rules.RADAR_TURN_RATE_RADIANS );

                // Adjust the radar turn so it goes that much further in the
                // direction it is going to turn
                // Basically if we were going to turn it left, turn it even
                // more left, if right, turn more right.
                // This allows us to overshoot our enemy so that we get a good
                // sweep
                // that will not slip.
                radarTurn += ( radarTurn < 0 ? -extraTurn : extraTurn );

                // Turn the radar
                setTurnRadarRightRadians( radarTurn );
            }
            // if there is more than 2 robots, do this.
            else
            {
                // Radar sweeps around constantly
                setTurnRadarRightRadians( 999999 );
                if ( !enemy.none() )
                {
                    // if enemy, absolute bearing is that enemy's bearing
                    double absoluteBearing = getHeadingRadians()
                                    + Math.toRadians( enemy.getBearing() );
                    // if enemy is weak
                    if ( enemy.getEnergy() < 20 )
                    {
                        // turn radar towards the enemy and face them
                        // (predicting movement)
                        setTurnRadarRightRadians(
                            3.5 * Utils.normalRelativeAngle( absoluteBearing
                                - getRadarHeadingRadians() ) );
                    }
                }
            }
        }


        /**
         * This is the boolean statement
         * 
         * @param e
         *            This is the event of scanning a robot.
         * 
         * @return ( enemy.none() || e.getDistance() < enemy.getDistance() - 70
         *         || e.getName().equals( enemy.getName() ) ) This determines
         *         whether the enemy should be tracked.
         */
        public boolean shouldTrack( ScannedRobotEvent e )
        {
            // track if we have no enemy, the one we found is significantly
            // closer, or we scanned the one we've been tracking.
            return ( enemy.none() || e.getDistance() < enemy.getDistance()
                            - 100
                || e.getName().equals( enemy.getName() ) );
        }


        /**
         * This controls the radar.
         * 
         * @param e
         *            This is the event of scanning a dead robot.
         * 
         * @return e.getName().equals( enemy.getName() ) If enemy is dead, the
         *         robot finds another target.
         */
        public boolean wasTracking( RobotDeathEvent e )
        {
            // if enemy is dead, the robot finds another target.
            return e.getName().equals( enemy.getName() );
        }
    }


    /**
     * This is the class that controls the gun.
     */
    public class Gun implements RobotPart
    {
        /**
         * This is the initialization method.
         */
        public void init()
        {
            // initialize gun operation
            setAdjustGunForRobotTurn( true );
        }


        /**
         * This moves the gun according to how we want it to.
         */
        public void move()
        {
            // don't shoot if I've got no enemy
            if ( enemy.none() )
            {
                return;
            }
            // if there are less than 3 enemies.
            if ( getOthers() < 3 )
            {
                // if an enemy is nearby
                if ( enemy.getDistance() > 20 )
                {
                    // calculate firepower based on distance
                    double firePower = Math.min( 500
                            / enemy.getDistance(), 3 );
                    // calculate speed of bullet
                    double bulletSpeed = 20 - firePower * 3;
                    // distance = rate * time, solved for time
                    long time = (long)( enemy.getDistance() / bulletSpeed );

                    // calculate gun turn to predicted x,y location
                    double futureX = enemy.getFutureX( time ) - 5;
                    double futureY = enemy.getFutureY( time ) + 5;
                    double absDeg = absoluteBearing( getX(), getY(), futureX,
                        futureY );
                    // non-predictive firing can be done like this:
                    // double absDeg = absoluteBearing(getX(), getY(),
                    // enemy.getX(),
                    // enemy.getY());

                    // turn the gun to the predicted x,y location
                    setTurnGunRight( normalizeBearing( absDeg
                        - getGunHeading() ) );

                    // if the gun is cool and we're pointed in the right
                    // direction,
                    // shoot!
                    if ( getGunHeat() == 0 && Math.abs( getGunTurnRemaining() )
                                    < 10 )
                    {
                        setFire( firePower );
                    }
                }
                else
                {
                    // if the robot is far away, get its bearing
                    double absDeg = absoluteBearing( getX(), getY(),
                        enemy.getX(), enemy.getY() );
                    // turn gun towards them
                    setTurnGunRight( normalizeBearing( absDeg
                        - getGunHeading() ) );
                    // fire weak bullets
                    setFire( 10 );
                }
            }
            // if more than 2 enemies:
            else
            {
                // calculate firepower based on distance
                double firePower = Math.min( 500
                        / enemy.getDistance(), 3 );
                // calculate speed of bullet
                double bulletSpeed = 20 - firePower * 3;
                // distance = rate * time, solved for time
                long time = (long)( enemy.getDistance() / bulletSpeed );

                // calculate gun turn to predicted x,y location
                double futureX = enemy.getFutureX( time ) - 5;
                double futureY = enemy.getFutureY( time ) + 5;
                double absDeg = absoluteBearing( getX(), getY(), futureX,
                    futureY );
                // non-predictive firing can be done like this:
                // double absDeg = absoluteBearing(getX(), getY(),
                // enemy.getX(),
                // enemy.getY());

                // turn the gun to the predicted x,y location
                setTurnGunRight( normalizeBearing( absDeg
                    - getGunHeading() ) );

                // if the gun is cool and we're pointed in the right
                // direction,
                // shoot!
                if ( getGunHeat() == 0 && Math.abs( getGunTurnRemaining() )
                                < 10 )
                {
                    setFire( firePower );
                }
            }
        }
    }


    /**
     * This moves the tanks and turns it.
     */
    public class Tank implements RobotPart
    {
        /**
         * This is the initialization for the tank.
         */
        public void init()
        {
            // this is the initial coloration.
            setColors( Color.black, Color.black, Color.red, Color.black,
                Color.black );
        }

        // this is used when strafing
        private byte moveDirection = 1;


        /**
         * This moves the tank to spiral around the enemy.
         */
        public void move()
        {
            // reverse direction if we stopped
            if ( getVelocity() == 0 )
            {
                setMaxVelocity( 8 );
                moveDirection *= -1;
            }
            // every 50 seconds, the color becomes dark.
            if ( getTime() % 50 == 0 )
            {
                setColors( Color.black, Color.black, Color.red, Color.black,
                    Color.black );
            }
            // every 25 seconds, the color becomes light (CAPTAIN AMERICA)
            else if ( getTime() % 50 == 25 )
            {
                setColors( Color.blue, Color.red, Color.white, Color.red,
                    Color.red );
            }
            // if there are less than 3 enemies.
            if ( getOthers() < 3 )
            {
                // if the enemy is nearby
                if ( enemy.getDistance() < 20 )
                {
                    // turn to a predicted position of the enemy bot.
                    setTurnRight( normalizeBearing( enemy.getBearing() ) + 180
                        + ( rand.nextInt( 20 ) - 20 ) );
                    // add some randomness so that enemyBot cannot directly hit
                    // us
                    setAhead( rand.nextInt( 40 ) );
                    // more random so that we don't get hit
                }
                // only spiral w/o random if we are a certain distance away
                if ( enemy.getDistance() < 60 )
                {
                    // Either continue forward or turn around.
                    setTurnRight( getHeading() * moveDirection );
                    // spiral toward our enemy
                    setTurnRight( normalizeBearing( enemy.getBearing() + 90
                        - ( 15 * moveDirection ) ) );
                    // move enemy distance
                    setAhead( enemy.getDistance() * moveDirection );
                }
                else
                {
                    // always square off against our enemy
                    setTurnRight( normalizeBearing( enemy.getBearing()
                        + 90 ) );
                    // checks to see if the robot is near any walls on the
                    // plane
                    if ( ( ( getX() < getBattleFieldWidth() - 20 )
                                    || getX() > 20 )
                        && ( getY() < getBattleFieldHeight() - 20 )
                        || getY() > 20 )
                    {
                        // always square off against our enemy
                        setTurnRight( normalizeBearing( enemy.getBearing()
                            + 110 ) );
                        // 110 instead of 90 so that enemy bot can't predict
                        // where
                        // we will move after hitting a wall

                        // circle our enemy
                        setAhead( 1000 * moveDirection );
                    }
                    else
                    {
                        // turns a random direction if near a wall.
                        setTurnRight( getHeading() * moveDirection
                            + rand.nextInt( 90 ) );
                        setAhead( 200 * moveDirection );
                    }
                }
            }
            // if there are more than 2 enemies
            else
            {
                // This is used to create a wall margin
                final int wallMargin = 200;
                // strafe by changing direction every 20 ticks
                if ( getTime() % 20 == 0 )
                {
                    moveDirection *= -1;
                }
                // if the bot is too far from the wall
                if (!( getX() < wallMargin || getX() > getBattleFieldWidth()
                                - wallMargin || getY() < wallMargin
                    || getY() > getBattleFieldHeight() - wallMargin ))
                {
                    // move
                    setAhead(enemy.getDistance()*moveDirection);
                }
                // if the bot is too close to the wall
                if ( getX() < 50 || getX() > getBattleFieldWidth()
                                - 50 || getY() < 50
                    || getY() > getBattleFieldHeight() - 50 )
                {
                    // turn slightly toward our enemy
                    setTurnRight( normalizeBearing( enemy.getBearing()
                        + 15 ) );
                }
                // if enemy is too powerful
                if ( enemy.getEnergy() > 75 )
                {
                    // turns away from enemy robot
                    setTurnRight( normalizeBearing( enemy.getBearing()
                        - 180 ) );
                }

                // if enemy nearby
                if ( enemy.getDistance() < 50 )
                {
                    // move and fire with power
                    setTurnRight( normalizeBearing( enemy.getBearing()
                        + 90 ) );
                    setAhead( enemy.getDistance() * moveDirection );
                }
                // overall, move and strafe every 20 seconds
                setAhead( enemy.getDistance() * moveDirection );
            }
        }
    }
}