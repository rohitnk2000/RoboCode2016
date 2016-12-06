package rkrb;

import robocode.*;


/**
 * Record the advanced state of an enemy bot.
 * 
 * @author Ritik Batra and Rohit Kulkarni
 * @version May 9, 2016
 * 
 * @author Period - 1
 * @author Assignment - AdvancedEnemyBot
 * 
 * @author Sources - None
 */
public class AdvancedEnemyBot extends EnemyBot
{
    private double x;

    private double y;


    /**
     * This method simply resets at the start of the code.
     */
    public AdvancedEnemyBot()
    {
        reset();
    }

    /**
     * This method returns the x value of the enemy.
     * 
     * @return x
     *          the x-value of the enemy robot.
     */
    public double getX()
    {
        return x;
    }

    /**
     * This method returns the y value of the enemy.
     * 
     * @return y
     *          the y-value of the enemy robot.
     */
    public double getY()
    {
        return y;
    }

    /**
     * This updates the robot's understanding of the enemy robots.
     * 
     * @param e
     *          this is the event of the robot scanning an enemy.
     *          
     * @param robot
     *          this specifies which robot is scanned.
     */
    public void update( ScannedRobotEvent e, Robot robot )
    {
        super.update( e );
        double absBearingDeg = ( robot.getHeading() + e.getBearing() );
        if ( absBearingDeg < 0 )
        {
            absBearingDeg += 360;
        }
        // yes, you use the _sine_ to get the X value because 0 deg is North
        x = robot.getX() + Math.sin( Math.toRadians( absBearingDeg ) )
                           * e.getDistance();
        // yes, you use the _cosine_ to get the Y value because 0 deg is North
        y = robot.getY() + Math.cos( Math.toRadians( absBearingDeg ) )
                           * e.getDistance();

    }

    /**
     * This method returns the future x value.
     * 
     * @param when
     *          this is the the long value that describes when to return x.
     * @return x + Math.sin( Math.toRadians( getHeading() ) )
                   * getVelocity() * when;
     *          the x-value of the enemy robot.
     */
    public double getFutureX( long when )
    {
        return x + Math.sin( Math.toRadians( getHeading() ) )
                   * getVelocity() * when;
    }

    /**
     * This method returns the future y value.
     * 
     * @param when
     *          this is the the long value that describes when to return y.
     * @return y + Math.cos( Math.toRadians( getHeading() ) )
                    * getVelocity() * when;
     *          the y-value of the enemy robot.
     */
    public double getFutureY( long when )
    {
        return y + Math.cos( Math.toRadians( getHeading() ) )
                    * getVelocity() * when;
    }

    /**
     * This method resets all the variables of the EnemyBot.
     */
    public void reset()
    {
        super.reset();
        x = 0.0;
        y = 0.0;
    }

}