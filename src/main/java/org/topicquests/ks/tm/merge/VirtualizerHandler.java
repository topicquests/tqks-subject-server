/**
 * Copyright 2015...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.merge;

import java.util.*;

import org.topicquests.support.api.IResult;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.IMergeResultsListener;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IVirtualizer;

/**
 * @author park
 *
 */
public class VirtualizerHandler {
  private SystemEnvironment environment;
  private IVirtualizer virtualizer;
  private Worker worker;
	
  /**
   * 
   */
  public VirtualizerHandler(SystemEnvironment env) {
    environment = env;
    virtualizer = environment.getVirtualizer();
    worker = new Worker();
    worker.start();
  }
	
  public void performMerge(IProxy primary, IProxy merge, Map<String,Double> mergeData,
                           double confidence, String userLocator, String mergeReason,
                           IMergeResultsListener listener, ITicket credentials) {
    environment.logDebug("VirtualizerHandler.performMerge-");
    worker.addWorkerObject(new WorkerObject(primary,merge,mergeData,
                                            confidence,userLocator, mergeReason, listener, credentials));
  }
	
  public void shutDown() {
    if (worker != null)
      worker.shutDown();
  }
  
  class WorkerObject {
    public IProxy primary, merge;
    public Map<String,Double>mergeData;
    public double confidence;
    public String userLocator;
    public String mergeReason;
    public IMergeResultsListener listener;
    public ITicket credentials;
		
    public WorkerObject(IProxy primary, IProxy merge, Map<String,Double> mergeData,
                        double confidence, String userLocator, String mergeReason,
                        IMergeResultsListener listener, ITicket credentials) {
      this.primary = primary;
      this.merge = merge;
      this.mergeData = mergeData;
      this.confidence = confidence;
      this.userLocator = userLocator;
      this.mergeReason = mergeReason;
      this.listener = listener;
      this.credentials = credentials;
    }
  }
	
  class Worker extends Thread {
    private List<WorkerObject>objects = new ArrayList<WorkerObject>();
    private boolean isRunning = true;
		
    public void addWorkerObject(WorkerObject o) {
      synchronized(objects) {
        objects.add(o);
        objects.notify();
      }
    }
		
    public void shutDown() {
      synchronized(objects) {
        isRunning = false;
        objects.notify();
      }
    }
		
    public void run() {
      WorkerObject theO = null;
      
      while (isRunning) {
        synchronized(objects) {
          if (objects.isEmpty()) {
            try {
              objects.wait();
            } catch (Exception e) {}
          }
          if (isRunning && !objects.isEmpty())
            theO = objects.remove(0);
        }
        if (isRunning && theO != null) {
          doIt(theO);
          theO = null;
        }
      }
    }
		
    void doIt(WorkerObject wo) {
      environment.logDebug("VirtualizerHandler.doIt- " + wo);
      IResult r = null;
      
      try {
        // System.out.println("VirtualizerHandler.doIt 1");
        r = virtualizer.createVirtualNode(wo.primary, wo.merge, wo.mergeData, wo.confidence,
                                          wo.userLocator, wo.mergeReason, wo.credentials);
        // System.out.println("VirtualizerHandler.doIt 1a");
      } catch (Exception e) {
        e.printStackTrace();
      }
      // System.out.println("VirtualizerHandler.doIt 2");

      if (wo.listener != null) {
        // System.out.println("VirtualizerHandler.doIt 3");
        if (r.getResultObject() != null) {
          // System.out.println("VirtualizerHandler.doIt 4");
          wo.listener.acceptMergeResults((String)r.getResultObject(), wo.primary.getLocator(),
                                         r.getErrorString());
        }
      }
      // System.out.println("VirtualizerHandler.doIt 5");
    }
  }

}
