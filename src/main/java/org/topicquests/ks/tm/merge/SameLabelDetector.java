/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.merge;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.util.JSONFileUtil;
import org.topicquests.support.api.IResult;
import org.topicquests.support.util.TextFileHandler;

/**
 * @author park
 * <p>We are reading the JSON file SameLabels.json which is made while importing or dealing
 * with proxies</p>
 */
public class SameLabelDetector {
  private SystemEnvironment environment;
  private JSONObject data;
  private final String fileName;
  private List<IProxy>proxies;
  private boolean isRunning = true;
  private Worker worker;
  private boolean isDirty = false;

  /**
   * 
   */
  public SameLabelDetector(SystemEnvironment env) {
    environment = env;
    fileName = environment.getStringProperty("SameLabelPath");
    proxies = new ArrayList<IProxy>();
    isRunning = true;
    isDirty = false;
    bootData();
    if (data == null)
      throw new RuntimeException("SameLabelDetector Boot null");
    worker = new Worker();
    worker.start();
  }
	
  /**
   * Examine proxy for its labels to add to SameLabels file
   * @param p
   */
  public void acceptProxy(IProxy p) {
    synchronized(proxies) {
      isDirty = true;
      proxies.add(p);
      proxies.notify();
    }
  }
	
  private void _acceptProxy(IProxy p) {
    environment.logDebug("SameLabel " + p.getLocator() + " " + p.listLabels());
    synchronized(data) {
      List<String> labels = p.listLabels();
      List<String> hits;
      
      if (labels != null) {
        String label, locator = p.getLocator();
        Iterator<String>itr = labels.iterator();
        
        while (itr.hasNext()) {
          label = itr.next();
          label = label.toLowerCase(); //TODO study this
          hits = (List<String>)data.get(label);
          if (hits == null)
            hits = new ArrayList<String>();
          if (!hits.contains(locator)) {
            hits.add(locator);
            data.put(label, hits);
          }
        }
      }
    }
  }
	
  class Worker extends Thread {
		
    public void run() {
      IProxy p = null;
      
      while (isRunning) {
        synchronized(proxies) {
          if (proxies.isEmpty()) {
            try {
              proxies.wait();
            } catch (Exception e) {}
          } else {
            p = proxies.remove(0);
          }
        }
        if (isRunning && p != null) {
          _acceptProxy(p);
          p = null;
        }
      }
    }
  }
  void bootData() {
    System.out.println("SameLabelBoot");
    data = null;
    
    try {
      data = null;
      TextFileHandler h = new TextFileHandler();
      File f = new File(fileName);
      if (f.exists()) {
        String dx = h.readFile(f);
        JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        data = (JSONObject)p.parse(dx);
      } else
        data = new JSONObject();
    } catch (Exception e) {
      environment.logError(e.getMessage(), e);
      e.printStackTrace();
    }
  }
  
  public void shutDown() {
    synchronized(proxies) {
      isRunning = false;
      proxies.notify();
    }
    
    if (!isDirty)
      return;
    
    synchronized(data) {
      System.out.println("SameLabelShutDown "+data.size());
      try {
        File f = new File("SameLabels.json");
        FileOutputStream fos = new FileOutputStream(f);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        OutputStreamWriter w = new OutputStreamWriter(bos, StandardCharsets.UTF_8);
        PrintWriter out = new PrintWriter(w);
        
        out.print(data.toJSONString());
        out.flush();
        out.close();
      } catch (Exception e) {
        environment.logError(e.getMessage(), e);
        e.printStackTrace();
      }
    }
  }
}
