/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class ProxyFetchTest {
  private SystemEnvironment environment;
  private DataProvider database;
  private ITicket credentials;

  /**
   * 
   */
  public ProxyFetchTest() {
    environment = new SystemEnvironment();
    database = environment.getDataProvider();
    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
    System.out.println("\n-------------------\nStarting ProxyFetchTest");

    IResult r = database.getNodeAsJSONObject("UserType", DataProvider._INDEX);
	    
		
    System.out.println("AAA "+r.getErrorString()+" | "+r.getResultObject());
    environment.shutDown();
    System.out.println("Finished ProxyFetchTest\n-------------------\n");
  }
  //	AAA  | {"crDt":"2018-03-20T16:07:54-07:00","isliv":true,"crtr":"SystemUser","node_type":null,"isvrt":false,"lox":"UserType","isprv":false,"_ver":"1521587274743","lEdDt":"2018-03-20T16:07:54-07:00","label":{"en":["UserType type"]},"url":null}

}
