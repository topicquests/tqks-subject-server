/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks;
import java.util.*;

import net.minidev.json.JSONObject;

import java.io.*;
import java.net.*;

public class ProxyEventHandler {
  private SystemEnvironment environment;
  private List<JSONObject> events;
  private int port = 0;
  private InetAddress host = null;
  private boolean isRunning = true;
  private Worker worker;
  private ServerSocket socket = null;
  
  /**
   * 
   */
  public ProxyEventHandler(SystemEnvironment environment) throws UnknownHostException {
    this.environment = environment;
    events = new ArrayList<JSONObject>();
    port = Integer.parseInt(this.environment.getStringProperty("EventHandlerPort"));
    host = InetAddress.getByName(this.environment.getStringProperty("EventHandlerServer"));
    isRunning = true;
    worker = new Worker();
    worker.start();
  }
	
  public void shutDown() throws IOException, InterruptedException {
    synchronized(events) {
      isRunning = false;
      events.notify();
    }
    if (socket != null) socket.close();
    worker.join();
  }
	
  /**
   * Event Structure:
   * { "type":<new or update>,
   *   //"userId": <some userId>,
   *   //NOTE: can add other metadata as desired
   *   "cargo": { <the proxy itself> }
   * }
   * @param event
   */

  public void acceptEvent(JSONObject event) {
    synchronized(events) {
      events.add(event);
      events.notify();
    }
  }
	
  void serveData(String data) {
    try {
      socket = new ServerSocket(port, 0, host);
      Socket closeableSocket = socket.accept();
      PrintWriter writer = new PrintWriter(closeableSocket.getOutputStream(), true);
      writer.print(data);
      writer.flush();
      writer.close();
    } catch (BindException e) {
      System.out.println(String.format("Address already in use: %s:%s", port, host));
    } catch(SocketException e) {
      // This happens if we buffer events on the socket but no client ever subscribes
      // since that is a routine occurrence let's ignore the error
      // System.out.println(String.format("Unhandled Chunk: %s", data));
    } catch (IOException e) {
      System.out.println("Broken Pipe");
      e.printStackTrace();
    } finally {
      try {
        if (socket != null) socket.close();
      } catch (IOException e) {
        System.out.println("Broken Pipe");
        e.printStackTrace();
      }
    }
  }
	
  class Worker extends Thread {

    public void run() {
      JSONObject jsonObject = null;
      
      while (isRunning) {
        synchronized(events) {
          if (events.isEmpty()) {
            try {
              events.wait();
            } catch (Exception e) {}
          } else {
            jsonObject = events.remove(0);
          }
          events.notify();
        }
        if (jsonObject != null) {
          serveData(jsonObject.toJSONString());
          jsonObject = null;
        }
      }
    }
  }
}
