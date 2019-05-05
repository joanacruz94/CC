/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joanacruz
 */
public class In extends Thread{
    private Resources connResources;
    private PDU packet;
    
    public In(Resources connection) throws UnknownHostException, SocketException{
        connResources = connection;
    }
    
    @Override
    public void run(){
        try {
            connResources.receive();
            sleep(5);
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
        }
    }
}
