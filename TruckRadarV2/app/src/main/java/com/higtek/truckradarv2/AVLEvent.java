package com.higtek.truckradarv2;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Date;

@Root(strict = false)
public class AVLEvent {
    @Attribute(name = "Uid")
    public String Uid;

    @Attribute(name = "DateTime")
    public String DateTime;

    @Attribute(name = "LatitudeFloat", required = false)
    public double LatitudeFloat;

    @Attribute(name = "LongitudeFloat", required = false)
    public double LongitudeFloat;

    @Attribute(name = "Speed", required = false)
    public double Speed;

    @Attribute(name = "Course", required = false)
    public int Course;

    public AVLEvent(){
        LatitudeFloat = Double.NaN;
        LongitudeFloat = Double.NaN;
    }
}