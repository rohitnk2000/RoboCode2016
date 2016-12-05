package rkrb;

import robocode.*;

/**
 * Record the state of an enemy bot.
 * 
 * @author Rohit Kulkarni and Ritik Batra
 * @version 5/6/16
 * 
 * @author Period - 1
 * @author Assignment - EnemyBot
 * 
 * @author Sources - none
 */
public class EnemyBot
{
    private double bearing;
    private double distance;
    private double energy;
    private double heading;
    private double velocity;
    private String name;

    
    /**
     * resets the methods
     */
    public EnemyBot()
    {
        reset();
    }

    /**
     * gets bearing
     * @return bearing of robot
     */
    public double getBearing()
    {
        return bearing; // Fix this!!
    }

    /**
     * gets distance
     * @return distance
     */
    public double getDistance()
    {
        
        return distance; // Fix this!!
    }

    /**
     * gets energy
     * @return energy
     */
    public double getEnergy()
    {
        return energy; // Fix this!!
    }

    /**
     * gets heading
     * @return heading
     */
    public double getHeading()
    {
        // TODO Your code here
        return heading; // Fix this!!
    }

    /**
     * gets velocity
     * @return velocity
     */
    public double getVelocity()
    {
        // TODO Your code here
        return velocity; // Fix this!!
    }

    /**
     * gets name
     * @return name
     */
    public String getName()
    {
        return name; // Fix this!!
    }

    /**
     * updates each parameter
     * @param srEvt - scanned event
     */
    public void update( ScannedRobotEvent srEvt )
    {
        bearing = srEvt.getBearing();
        distance = srEvt.getDistance();
        energy = srEvt.getEnergy();
        heading = srEvt.getHeading();
        velocity = srEvt.getVelocity();
        name = srEvt.getName();
    }

    /**
     * resets each parameter
     */
    public void reset()
    {
        bearing = 0;
        distance = 0;
        energy = 0; 
        heading = 0;
        velocity = 0;
        name = "";
    }

    /**
     * checks to see if robot has a name
     * @return if the name has no characters
     */
    public boolean none()
    {
        return name.length() == 0;
    }
}
