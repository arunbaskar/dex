package model;

/**
 * Created by arun.bhaskar on 1/11/2017.
 */
public class Weather {
    public Place place;
    public String iconData;
    public CurrentCondition currentCondition = new CurrentCondition();
    public Temperature temperature = new Temperature();
    public Wind wind = new Wind();
    public Snow snow = new Snow();
    public Cloud clouds = new Cloud();
}
