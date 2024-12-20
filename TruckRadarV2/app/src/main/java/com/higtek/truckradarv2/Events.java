package com.higtek.truckradarv2;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Events", strict = false)
public class Events {

    @ElementList(name = "Events", inline = true)
    public List<AVLEvent> Events;

}

