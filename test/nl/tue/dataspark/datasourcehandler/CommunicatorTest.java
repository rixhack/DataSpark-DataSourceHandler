/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.dataspark.datasourcehandler;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rixhack
 */
public class CommunicatorTest extends ReceiverAdapter {
    
    JChannel channel;
    Communicator communicator;
    Integer value;
    
    public CommunicatorTest() throws Exception {
        communicator = new Communicator();
        channel = new JChannel();
        channel.connect("TwitterDataCluster");
        channel.setReceiver(this);
        channel.setDiscardOwnMessages(true);  
        value = -1;
    }
    
    public void receive(Message msg){
        value = (int) msg.getObject();
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    /*
    Test receive, startRequesting and newData methods from Communicator class.
    */

    @Test
    public void testChannel() throws Exception {
        Object[] params = {"twitter", "eindhoven", "15"};
        Message msg = new Message(null, params);
        channel.send(msg);
        Thread.sleep(5000);
        assertTrue(value >= 0);
    }
}
