/**
 * Copyright (c) 2017, The Glowsticks.
 * All rights reserved.
 */
package nl.tue.dataspark.datasourcehandler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.Message;

/**
 * 
 * @author rixhack
 */
public class Communicator extends ReceiverAdapter {

    private static QueryHandler queryHandler;
    private JChannel channel;
    private String dataSource;
    private Map<String, String> params;
    private int value;
    private boolean requesting;
    private int rate = 12 * 60; // Number of times to request new data per hour.
    
    
     public Communicator() throws Exception {
        queryHandler = new QueryHandler();
        requesting = false;
        params = new HashMap<>();
        createChannel();
        
    }
    public static void main(String[] args) throws Exception {
        new Communicator();
    }

    private void createChannel() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("TwitterDataCluster");
        channel.setDiscardOwnMessages(true);
    }

    public void receive(Message msg) {
        try {
            Object[] params = (Object[]) msg.getObject();
            this.dataSource = (String) params[0];
            this.params.put("query", (String) params[1]);
            this.params.put("timeWindow", (String) params[2]);
            if (!requesting){
                try {
                    startRequesting();
                    requesting = true;
                } catch (Exception e) {
                 Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        } catch (ClassCastException e) {

        }
    }
    
    // Create a separate thread to request data with a frequency defined by the rate variable.
    public void startRequesting() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        newData();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    try {
                        Thread.sleep(3600 / rate * 1000);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
    }

    private void newData() {
        try {
            value = queryHandler.getDataValue(dataSource, params).getValue().intValue();
        } catch (Exception e) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, null, e);
        }
        Message msg = new Message(null, value);
        try {
            channel.send(msg);
        } catch (Exception ex) {
            Logger.getLogger(Communicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
