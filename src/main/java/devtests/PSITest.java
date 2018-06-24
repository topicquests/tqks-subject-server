/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

import java.util.ArrayList;
import java.util.List;

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
public class PSITest {
  private SystemEnvironment environment;
  private DataProvider database;
  private ITicket credentials;
  private IProxyModel model;
  private static final String 
  SUPERCLASS	= ITQCoreOntology.TYPE_TYPE,
    USERID	= ITQCoreOntology.SYSTEM_USER,
    VALUE1	= "http://foo.org/bar#39",
    VALUE2	= "http://bar.org/foo#59",
    VALUE3	= "http://example.com/ontology#foo";
  String LOCATOR = null;

  private IPostgresConnection dbConn;

  /**
   * 
   */
  public PSITest() {
    environment = new SystemEnvironment();
    database = environment.getDataProvider();
    model = environment.getProxyModel();
    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
    System.out.println("\n-------------------\nStarting PSITest");

    try {
      dbConn = database.getDBProvider().getConnection();
    } catch(SQLException e) {
      System.out.println("ERROR in setUp: " + e.getMessage());
      return;
    }

    LOCATOR = database.getUUID();
    IProxy p = model.newInstanceNode(LOCATOR, SUPERCLASS, "Test class", "Yup", "en", USERID, null, null, null, false);
    IResult r = database.putNode(p);
    System.out.println("STARTING "+p.getLocator());	    
    System.out.println("AAA "+r.getErrorString()+" "+r.getResultObject());
    p.addPSI(VALUE1);
    System.out.println("BBB "+p.listPSIValues());
    p.addPSI(VALUE2);
    System.out.println("CCC "+p.listPSIValues());
    p.removePSI(VALUE1);
    System.out.println("DDD "+p.listPSIValues());
    p.addPSI(VALUE3);
    System.out.println("EEE "+p.listPSIValues());

    // Clean up
    deleteProxies();
    dbConn.closeConnection(r);
    
    environment.shutDown();
    System.out.println("Finished PSITest\n-------------------\n");
  }
  //STARTING 2cbb8044-b321-4a52-b95c-8f09db515fed
  //	AAA  null
  //	BBB [http://foo.org/bar#39]
  //	CCC [http://foo.org/bar#39, http://bar.org/foo#59]
  //	DDD [http://bar.org/foo#59]
  //	EEE [http://bar.org/foo#59, http://example.com/ontology#foo]

  private void deleteProxies() {
    String proxy_sql = "DELETE FROM tq_contents.proxy WHERE lox = ?";
    String acls_sql = "DELETE FROM tq_contents.acls WHERE proxyid = ?";
    String super_sql = "DELETE FROM tq_contents.superclasses where proxyid = ?";
    String trans_sql = "DELETE FROM tq_contents.transitive_closure where proxyid = ?";
    String psi_sql = "DELETE FROM tq_contents.psi where proxyid = ?";
    String props_sql = "DELETE FROM tq_contents.properties where proxyid = ?";

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
