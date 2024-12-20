package com.higtek.truckradarv2;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.util.ArrayList;

public class SerializationHelper {
    private static final SerializationHelper ourInstance = new SerializationHelper();

    public static SerializationHelper getInstance() {
        return ourInstance;
    }

    private SerializationHelper() {
    }

    public void Test(){
        Serializer serializer = new Persister();
        Events example = new Events();
        example.Events = new ArrayList<AVLEvent>();
        AVLEvent ev = new AVLEvent();
        ev.Uid = "1";
        example.Events.add(ev);
        ev = new AVLEvent();
        ev.Uid = "2";
        example.Events.add(ev);
        StringWriter writer = new StringWriter();

        try {
            serializer.write(example, writer);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        String s = writer.toString();

        Events obj = new Events();
        try {
            obj = serializer.read(Events.class, s);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Events DeserializeEvents(String data){

        Serializer serializer = new Persister();

        Events obj = new Events();
        try {
            obj = serializer.read(Events.class, data, false);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return obj;
    }
}
