/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

import java.util.List;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITQRelationsOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.ks.tm.api.ITuple;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 * A goal is to navigate a relation from a starting node
 */
public class ClientTest_2 {
  private SystemEnvironment environment;
  private DataProvider database;
  private ITicket credentials;
  private IProxyModel model;
  private static final String 
  USERID	= ITQCoreOntology.SYSTEM_USER,
    SUBJLOX	= "661b4d7f-c232-4297-a370-6ccd08c903ab",
    OBJLOX	= "5b5ed3eb-d825-4497-a34b-c9c343638bf4",
    RELN	= ITQRelationsOntology.CAUSAL;

  /**
   * 
   */
  public ClientTest_2() {
    environment = new SystemEnvironment();
    database = environment.getDataProvider();
    model = environment.getProxyModel();
    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);

    System.out.println("\n-------------------\nStarting ClientTest_2");
    // ask for a relation's Subject
    IResult r = database.getNode(SUBJLOX, credentials);
    System.out.println("AAA "+r.getErrorString()+" "+r.getResultObject());
    IProxy p = (IProxy)r.getResultObject();
    
    if (p != null) {
      System.out.println("BBB "+p.toJSONString());
      r = database.listRelationsByRelationType(SUBJLOX, RELN, 0, -1, credentials);
      System.out.println("CCC "+r.getErrorString()+" "+r.getResultObject());
      List<ITuple> l = (List<ITuple>)r.getResultObject();
      
      if (l != null && !l.isEmpty()) {
        ITuple t = l.get(0);
        System.out.println("DDD "+t.toJSONString());
				
        String lox = t.getObject();
        System.out.println("EEE "+lox.equals(OBJLOX));
      }
    }
    environment.shutDown();
    System.out.println("Finished ClientTest_2\n-------------------\n");
  }
  //AAA  org.topicquests.ks.tm.Proxy@421e361
  //BBB {"crDt":"1522202601639","isliv":true,"crtr":"SystemUser","node_type":"TypeType","isvrt":false,"lox":"661b4d7f-c232-4297-a370-6ccd08c903ab","isprv":false,"_ver":"1522202601377","lEdDt":"1522202601639","label":{"en":["Carbon Dioxide"]},"url":null}
  //CCC  [org.topicquests.ks.tm.Proxy@57c03d88]
  //DDD {"crtr":"SystemUser","_ver":"1522202601580","lEdDt":"2018-03-27T19:03:21-07:00","tupST":"NodeType","label":{"en":["Causes"]},"tupOT":"NodeType","url":null,"crDt":"2018-03-27T19:03:21-07:00","trCl":["TypeType","RelationType","CausesRelationType"],"isliv":true,"tupS":"661b4d7f-c232-4297-a370-6ccd08c903ab","node_type":"CausesRelationType","isvrt":false,"lox":"661b4d7f-c232-4297-a370-6ccd08c903abCausesRelationType5b5ed3eb-d825-4497-a34b-c9c343638bf4","tupO":"5b5ed3eb-d825-4497-a34b-c9c343638bf4","isprv":false}
  //	EEE true

}
