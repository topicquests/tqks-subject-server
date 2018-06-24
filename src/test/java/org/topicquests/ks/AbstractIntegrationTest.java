/**
 * Copyright 2017, 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.topicquests.ks.tm.DataProvider;
// import org.topicquests.pg.PostgreSqlProvider;
import org.topicquests.backside.kafka.producer.MessageProducer;

import org.topicquests.ks.api.ITQCoreOntology;
// import org.topicquests.ks.api.ITQDataProvider;
import org.topicquests.ks.api.ITicket;
// import org.topicquests.ks.tm.api.IProxyModel;
// import org.topicquests.node.provider.Client;

/**
 *
 */
public class AbstractIntegrationTest {
  public static SystemEnvironment environment;
  // public static Client client;
  public static DataProvider database;
  public static MessageProducer kProducer;
  // public static IProxyModel proxyModel;
  public static ITicket credentials;

  @BeforeAll
  static void setUp() {
    environment = new SystemEnvironment("abstractIntegrationTest");
    database = environment.getDataProvider();
    kProducer = environment.getkafkaProducer();
    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
  }

  @Test
  @DisplayName("Test Database Connection")
  void TestDBConnection() {
    assertEquals("tq_admin", database.getUser());
  }

  @AfterAll
  static void tearDown() {
    environment.shutDown();
  }
}
