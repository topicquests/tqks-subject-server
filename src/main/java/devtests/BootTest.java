/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;
import org.topicquests.ks.SystemEnvironment;
/**
 * @author jackpark
 *
 */
public class BootTest {
  private SystemEnvironment environment;
  /**
   * 
   */
  public BootTest() {
    environment = new SystemEnvironment();
    System.out.println("\n-------------------\nStarting BootTest");
    System.out.println("AAA "+environment.getProperties());
    environment.shutDown();
    System.out.println("Finished BootTest\n-------------------\n");
  }
}
