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
public class ACLTest {
  private SystemEnvironment environment;
  private DataProvider database;
  private ITicket credentials;
  private IProxyModel model;
  private static final String 
  SUPERCLASS	= ITQCoreOntology.TYPE_TYPE,
    USERID	= ITQCoreOntology.SYSTEM_USER,
    VALUE1	= "ev56",
    VALUE2	= "h2343tgh",
    VALUE3	= "mygroup";
  String LOCATOR = null;

  private IPostgresConnection dbConn;
  
  /**
   * 
   */
  public ACLTest() {
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
    System.out.println("\n-------------------\nStarting ACLTest");

    LOCATOR = database.getUUID();
    IProxy p = model.newInstanceNode(LOCATOR, SUPERCLASS, "Test class", "Yup", "en", USERID, null, null, null, false);
    IResult r = database.putNode(p);
    System.out.println("STARTING "+p.getLocator());	    
    System.out.println("AAA "+r.getErrorString()+" "+r.getResultObject());
    p.addACLValue(VALUE1, true);
    System.out.println("BBB "+p.listACLValues());
    p.addACLValue(VALUE2, true);
    System.out.println("CCC "+p.listACLValues());
    p.removeACLValue(VALUE1);
    System.out.println("DDD "+p.listACLValues());
    p.addACLValue(VALUE3, true);
    System.out.println("EEE "+p.listACLValues());
    System.out.println("FFF "+p.containsACL(VALUE3));
    System.out.println("GGG "+p.containsACL(VALUE1));
    r = database.getNode(LOCATOR, credentials);
    p = (IProxy)r.getResultObject();
    System.out.println("EEE1 "+p.listACLValues());
    System.out.println("FFF1 "+p.containsACL(VALUE3));
    System.out.println("GGG1 "+p.containsACL(VALUE1));

    // Clean up
    deleteProxies();
    dbConn.closeConnection(r);
    
    environment.shutDown();
    System.out.println("Finished ACLTest\n-------------------\n");
  }
  //STARTING 974786e1-32ab-44ac-b9e1-d3bde746fb0d
  //	AAA  null
  //	BBB [ev56]
  //	CCC [ev56, h2343tgh]
  //	DDD [h2343tgh]
  //	EEE [h2343tgh, mygroup]
  //	FFF true
  //	GGG false
  // EEE1 [h2343tgh, mygroup] -- same after refetch
  //	FFF1 true
  //	GGG1 false

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
