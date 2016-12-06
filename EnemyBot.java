package rkrb;

import robocode.*;


/**
 * Record the state of an enemy bot.
 * 
 * @author Ritik Batra and Rohit Kulkarni
 * @version May 6, 2016
 * 
 * @author Period - 1
 * @author Assignment - EnemyBot
 * 
 * @author Sources - None
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
     * This method has a constructor and resets the robot created.
     */
    public EnemyBot()
    {
        reset();
    }


    /**
     * This method returns the bearing.
     * 
     * @return bearing This is the double that describes the bearing to enemy.
     */
    public double getBearing()
    {
        return bearing;
    }


    /**
     * This method returns the distance between the robot and enemy.
     * 
     * @return distance This is the double that describes the distance to enemy.
     */
    public double getDistance()
    {
        return distance;
    }


    /**
     * This method returns the energy of the enemy.
     * 
     * @return energy This is the double that describes the energy of enemy.
     */
    public double getEnergy()
    {
        return energy;
    }


    /**
     * This method returns the heading to the enemy.
     * 
     * @return heading This is the double that describes the heading to enemy.
     */
    public double getHeading()
    {
        return heading;
    }


    /**
     * This method returns the velocity of the robot.
     * 
     * @return velocity This is the double that describes the velocity of enemy.
     */
    public double getVelocity()
    {
        return velocity;
    }


    /**
     * This method returns the name of the enemy.
     * 
     * @return name This is the String that describes the name of enemy.
     */
    public String getName()
    {
        return name;
    }


    /**
     * If the robot scans another robot, it updates its information.
     * 
     * @param srEvt
     *            This is the event of the robot scanning an enemy.
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
     * This resets the robot's information to 0 in order to update later.
     */
    public void reset()
    {
        bearing = 0.0;
        distance = 0.0;
        energy = 0.0;
        heading = 0.0;
        velocity = 0.0;
        name = "";
    }


    /**
     * Implement a (state-reporting) accessor method called none which will
     * return true if name is "" or false otherwise. (Remember to use the
     * equals() method of the String class.) Basically, this method will return
     * true if the reset method was just called.
     * 
     * @return true/false This boolean describes whether the string name is
     *         empty or not.
     */
    public boolean none()
    {
        return ( name.length() == 0 );
    }
}