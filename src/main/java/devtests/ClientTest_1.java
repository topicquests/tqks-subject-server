/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITQRelationsOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.support.api.IResult;

import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.pg.api.IPostgresConnection;

import java.sql.*;

/**
 * @author jackpark
 *
 */
public class ClientTest_1 {
  private SystemEnvironment environment;
  private DataProvider database;
  private ITicket credentials;
  private IProxyModel model;
  private static final String 
  SUPERCLASS	= ITQCoreOntology.TYPE_TYPE,
    //USERID = "joesixpack", <-- must be registered user
    USERID	= ITQCoreOntology.SYSTEM_USER,
    LABEL_1 = "Carbon Dioxide",
    LABEL_2	= "Climate Change",
    RELN	= ITQRelationsOntology.CAUSAL;
  String LOCATOR = null;
  String LOCATOR2 = null;
		
  private IPostgresConnection dbConn;

  /**
   * 
   */
  public ClientTest_1() {
    environment = new SystemEnvironment();
    database = environment.getDataProvider();
    model = environment.getProxyModel();
    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);

    try {
      dbConn = database.getDBProvider().getConnection();
    } catch(SQLException e) {
      System.out.println("ERROR in setUp: " + e.getMessage());
      return;
    }

    System.out.println("\n-------------------\nStarting ClientTest_1");
    LOCATOR = database.getUUID();
    LOCATOR2 = database.getUUID();
    System.out.println("ClientTest_1: model: " + model);
    IProxy p = model.newInstanceNode(LOCATOR, SUPERCLASS, LABEL_1, "", "en", USERID, null, null, null, false);
    IResult r = database.putNode(p);
    System.out.println("SUBJLOX "+LOCATOR);
    System.out.println("OBJLOX "+LOCATOR2);
    System.out.println("AAA "+r.getErrorString()+" "+r.getResultObject());
    IProxy p2 = model.newInstanceNode(LOCATOR2, SUPERCLASS, LABEL_1, "", "en", USERID, null, null, null, false);
    System.out.println("BBB "+r.getErrorString()+" "+r.getResultObject());
    r = model.relateExistingNodes(p, p2, RELN, null, null, USERID, null, null, null, false, false);
    System.out.println("CCC "+r.getErrorString()+" "+r.getResultObject());

    deleteProxies();
    dbConn.closeConnection(r);
    
    environment.shutDown();
    System.out.println("Finished ClientTest_1\n-------------------\n");
  }
  //SUBJLOX 949accd8-9b93-4a96-8cf2-33538c73827d
  //	OBJLOX 39695bc8-8707-4f72-9431-0c84004005bb
  //	AAA  null
  //BBB  null
  //CCC  org.topicquests.ks.tm.Proxy@3370f42

  private void deleteProxies() {
    String proxy_sql = "DELETE FROM tq_contents.proxy WHERE lox IN (?, ?)";
    String acls_sql = "DELETE FROM tq_contents.acls WHERE proxyid IN (?, ?)";
    String super_sql = "DELETE FROM tq_contents.superclasses where proxyid IN (?, ?)";
    String trans_sql = "DELETE FROM tq_contents.transitive_closure where proxyid IN (?, ?)";
    String psi_sql = "DELETE FROM tq_contents.psi where proxyid IN (?, ?)";
    String props_sql = "DELETE FROM tq_contents.properties where proxyid IN (?, ?)";

    String [] proxy_vals = new String[2];
    proxy_vals[0] = LOCATOR;
    proxy_vals[0] = LOCATOR2;

    dbConn.setProxyRole();
    IResult r = dbConn.beginTransaction();
    dbConn.executeSQL(acls_sql, r, proxy_vals);
    dbConn.executeSQL(psi_sql, r, proxy_vals);
    dbConn.executeSQL(super_sql, r, proxy_vals);
    dbConn.executeSQL(trans_sql, r, proxy_vals);
    dbConn.executeSQL(props_sql, r, proxy_vals);
    dbConn.executeSQL(proxy_sql, r, proxy_vals);
    dbConn.endTransaction();

    if (r.hasError()) {
      System.out.println("Error in deleteProxies: " + r.getErrorString());
    }
  }
}
