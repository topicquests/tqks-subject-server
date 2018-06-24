/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.IExtendedCoreOntology;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class InstanceTest {
  private SystemEnvironment environment;
  private DataProvider database;
  private ITicket credentials;

  /**
   * 
   */
  public InstanceTest() {
    environment = new SystemEnvironment();
    database = environment.getDataProvider();
    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
    System.out.println("\n-------------------\nStarting InstanceTest");
    IResult r = database.listInstanceNodes(IExtendedCoreOntology.SUBCLASS_RELATION_TYPE,
                                           0, 5, credentials);
	    
		
    System.out.println("AAA "+r.getErrorString()+" \n "+r.getResultObject());
    environment.shutDown();
    System.out.println("Finished InstanceTest\n-------------------\n");
  }
  //AAA  
  //	 [org.topicquests.ks.tm.Proxy@59d4cd39, org.topicquests.ks.tm.Proxy@389c4eb1, org.topicquests.ks.tm.Proxy@3fc79729, org.topicquests.ks.tm.Proxy@34f6515b, org.topicquests.ks.tm.Proxy@4b34fff9]

}
