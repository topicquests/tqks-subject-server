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
public class SubclassTest {
  private SystemEnvironment environment;
  private DataProvider database;
  private ITicket credentials;

  /**
   * 
   */
  public SubclassTest() {
    environment = new SystemEnvironment();
    database = environment.getDataProvider();
    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);

    System.out.println("\n-------------------\nStarting SubclassTest");
    IResult r = database.listSubclassNodes(ITQCoreOntology.PROPERTY_TYPE,
                                           0, 5, credentials);
	    
		
    System.out.println("AAA "+r.getErrorString()+" \n "+r.getResultObject());
    environment.shutDown();
    System.out.println("Finished SubclassTest\n-------------------\n");
  }

}
//AAA  
//[org.topicquests.ks.tm.Proxy@6e01f9b0, org.topicquests.ks.tm.Proxy@2b9ed6da, org.topicquests.ks.tm.Proxy@6c61a903, org.topicquests.ks.tm.Proxy@658c5a19, org.topicquests.ks.tm.Proxy@421e361]

