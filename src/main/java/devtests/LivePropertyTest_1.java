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
public class LivePropertyTest_1 {
  private SystemEnvironment environment;
  private DataProvider database;
  private ITicket credentials;
  private IProxyModel model;
  private static final String 
  SUPERCLASS	= ITQCoreOntology.TYPE_TYPE,
    //USERID = "joesixpack", <-- must be registered user
    USERID	= ITQCoreOntology.SYSTEM_USER,
    KEY		= "MyTestProperty",
    VALUE	= "SomethingElse";
  String LOCATOR = null;
  private IPostgresConnection dbConn;

  /**
   * 
   */
  public LivePropertyTest_1() {
    environment = new SystemEnvironment();
    database = environment.getDataProvider();
    model = environment.getProxyModel();
    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
    System.out.println("\n-------------------\nStarting LivePropertyTest_1");

    try {
      dbConn = database.getDBProvider().getConnection();
    } catch(SQLException e) {
      System.out.println("ERROR in setUp: " + e.getMessage());
      return;
    }
    
    LOCATOR = database.getUUID();
    IProxy p = model.newInstanceNode(LOCATOR, SUPERCLASS, "Test class", "Yup", "en", USERID, null, null, null, false);
    IResult r = database.putNode(p);
    System.out.println("AAA "+r.getErrorString()+" "+r.getResultObject());
    p.setProperty(KEY, VALUE);
    System.out.println("BBB "+p.getProperty(KEY));

    // Clean up
    deleteProxies();
    dbConn.closeConnection(r);
		
    environment.shutDown();
    System.out.println("Finished LivePropertyTest_1\n-------------------\n");
  }
  //AAA  null
  //BBB SomethingElse

  private void deleteProxies() {
    String proxy_sql = "DELETE FROM tq_contents.proxy WHERE lox = ?";
    String acls_sql = "DELETE FROM tq_contents.acls WHERE proxyid = ?";
    String super_sql = "DELETE FROM tq_contents.superclasses WHERE proxyid = ?";
    String trans_sql = "DELETE FROM tq_contents.transitive_closure WHERE proxyid = ?";
    String psi_sql = "DELETE FROM tq_contents.psi WHERE proxyid = ?";
    String props_sql = "DELETE FROM tq_contents.properties WHERE proxyid = ?";

    String [] proxy_vals = new String[1];
    proxy_vals[0] = LOCATOR;

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
