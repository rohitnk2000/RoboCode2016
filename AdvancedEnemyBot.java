package rkrb;

import robocode.*;


/**
 * Record the advanced state of an enemy bot.
 * 
 * @author Rohit Kulkarni
 * @version 5/10/16
 * 
 * @author Period - 1
 * @author Assignment - AdvancedEnemyBot
 * 
 * @author Sources - none
 */
public class AdvancedEnemyBot extends EnemyBot
{
    private double x;

    private double y;


    /**
     * resets all procedures
     */
    public AdvancedEnemyBot()
    {
        reset();
    }


    /**
     * returns x value of enemy bot
     * 
     * @return x value of enemy bot
     */
    public double getX()
    {
        return x; // Fix this!!
    }


    /**
     * returns x value of enemy bot
     * 
     * @return x value of enemy bot
     */
    public double getY()
    {
        return y; // Fix this!!
    }


    /**
     * updates all enemy information and gets the absolute bearing of enemy bot
     * 
     * @param e
     *            - scanned event
     * @param robot
     *            - enemy bot
     */
    public void update( ScannedRobotEvent e, Robot robot )
    {
        super.update( e );
        double absBearingDeg = ( robot.getHeading() + e.getBearing() );
        if ( absBearingDeg < 0 )
        {
            absBearingDeg += 360;
        }
        x = robot.getX() + Math.sin( Math.toRadians( absBearingDeg ) ) * 
                                                        e.getDistance();
        y = robot.getY() + Math.cos( Math.toRadians( absBearingDeg ) ) * 
                                                        e.getDistance();
    }


    /**
     * uses sin to get the heading of the robot
     * 
     * @param when
     *            - value that describes when to return x
     * @return - a double that is equivalent to the degrees to turn for the
     *         enemy bot
     */
    public double getFutureX( long when )
    {
        return x + Math.sin( Math.toRadians( getHeading() ) ) * getVelocity() 
                                                                        * when;
    }


    /**
     * uses cos to get the heading of the robot
     * 
     * @param when
     *            - value that describes when to return y
     * @return - a double that is equivalent to the degrees to turn for the
     *         enemy bot
     */
    public double getFutureY( long when )
    {
        return y + Math.cos( Math.toRadians( getHeading() ) ) * getVelocity() 
                                                                        * when;
    }


    /**
     * resets x and y coordinates
     */
    public void reset()
    {
        super.reset();
        x = 0;
        y = 0;
    }

}