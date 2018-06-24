/**
 * Copyright 2015...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.merge;

import java.util.*;

import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.tm.api.IDataProvider;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.ISameLabelListener;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;

/**
 * @author park
 *
 */
public class SameLabelMergeHandler {
  private SystemEnvironment environment;
  private IDataProvider database;
  private IProxyModel nodeModel;
  private ITicket credentials;
  private Worker worker;
  private VirtualizerHandler virtualizerHandler;
	
  /**
   * 
   */
  public SameLabelMergeHandler(SystemEnvironment env) {
    environment = env;
    database = environment.getDataProvider();
    nodeModel = environment.getProxyModel();
    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
    virtualizerHandler = environment.getVirtualizerHandler();
    worker = new Worker();
  }
	
  public void studySameLabel(String nodeLocatorA, String nodeLocatorB, ISameLabelListener l) {
    worker.addHit(new WorkerObject(nodeLocatorA, nodeLocatorB, l));
  }
	
  public void shutDown() {
    if (worker != null)
      worker.shutDown();
  }
  class WorkerObject {
    public String locatorA;
    public String locatorB;
    public ISameLabelListener listener;
		
    public WorkerObject(String a, String b, ISameLabelListener l) {
      locatorA = a;
      locatorB = b;
      listener = l;
    }
  }
  class Worker extends Thread {
    private List<WorkerObject>hits;
    private boolean isRunning = true;
		
    public Worker() {
      hits = new ArrayList<WorkerObject>();
      this.start();
    }
		
    public void addHit(WorkerObject o) {
      synchronized(hits) {
        hits.add(o);
        hits.notify();
      }
    }
    public void shutDown() {
      synchronized(hits) {
        isRunning = false;
        hits.notify();
      }
    }
    public void run() {
      WorkerObject o = null;
      while(isRunning) {
        synchronized(hits) {
          if (hits.isEmpty()) {
            try {
              hits.wait();
            } catch (Exception e) {}
          }
          if (isRunning & !hits.isEmpty())
            o = hits.remove(0);
        }
        if (isRunning && o != null) {
          studySameLableNodes(o);
          o = null;
        }
      }
    }
		
  }
	
  /**
   * Study two {@link INode} objects with same labels. If a new
   * <em>VirtualNode</em> is created, return its locator in the
   * output PLUS the two original locators space-separated in resultA
   * @param nodeLocatorA
   * @param nodeLocatorB
   * @return
   */
  public void studySameLableNodes(WorkerObject o) {
    String nodeLocatorA = o.locatorA;
    String nodeLocatorB = o.locatorA;
    String virtualNodeLocator = null;
    IResult result = new ResultPojo();
    IResult rA = database.getNode(nodeLocatorA, credentials);
    
    if (rA.hasError())
      result.addErrorString(rA.getErrorString());
    
    IResult rB = database.getNode(nodeLocatorB, credentials);
    if (rB.hasError())
      result.addErrorString(rB.getErrorString());
    
    IProxy nodeA = (IProxy)rA.getResultObject();
    IProxy nodeB = (IProxy)rB.getResultObject();
    String typeA, typeB;
    
    // sanity
    if (nodeA != null && nodeB != null) {
      IResult rC;
      
      // NOW, sort out if these are somehow the same subject
      typeA = nodeA.getNodeType();
      typeB = nodeB.getNodeType();
      
      if (typeA.equals(typeB)) {
        rC = studySameTypeNodes(nodeA, nodeB);
        if (rC.hasError())
          result.addErrorString(rC.getErrorString());
        virtualNodeLocator = (String)rC.getResultObject();
      } else {
        //TODO
      }
    }
    
    if (virtualNodeLocator != null)
      o.listener.acceptSameLabelResults(virtualNodeLocator, nodeLocatorA, nodeLocatorB, result.getErrorString());
  }
	
  //////////////////////////////////////
  // NOTES
  // If instanceOf OntologyClassType
  //   If memberOf same Ontology
  //   Then less likely
  //   Else...
  //
  //////////////////////////////////////
  /**
   * Returns a virtual node locator if created
   * @param nodeA
   * @param nodeB
   * @return
   */
  IResult studySameTypeNodes(IProxy nodeA, IProxy nodeB) {
    IResult result = new ResultPojo();
    //TODO
    return result;
  }
}
