/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.pg.api.IPostgresConnection;

import org.topicquests.es.api.IClient;
import org.topicquests.es.ProviderEnvironment;

import org.topicquests.ks.api.IExtendedCoreOntology;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;

import org.topicquests.ks.tm.ProxyModel;

import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.support.util.DateUtil;

import java.sql.*;

public class ProxyModelTest {
  public static SystemEnvironment proxy_environment;
  public static SystemEnvironment user_environment;
  public static DataProvider database;
  public static DataProvider user_database;
  public static IProxyModel proxyModel;
  public static ITicket credentials;
  public static PostgresConnectionFactory dbProvider;
  public static PostgresConnectionFactory user_dbProvider;
  public static IPostgresConnection dbConn;
  public static IPostgresConnection userConn;
  public static IClient esProvider;

  private static String userName = "joeuser";

  @BeforeAll
  static void setUp() {
    System.out.println("--- Calling setUp ---");
    proxy_environment = new SystemEnvironment("tq_contents");
    database = proxy_environment.getDataProvider();
    esProvider = proxy_environment.getESProvider().getProvider();

    dbProvider = database.getDBProvider();
    try {
      dbConn = dbProvider.getConnection();
    } catch(SQLException e) {
      System.out.println("ERROR in setUp: " + e.getMessage());
      return;
    }
    proxyModel = new ProxyModel(proxy_environment, database);

    user_environment = new SystemEnvironment("tq_authentication");
    user_database = user_environment.getDataProvider();
    user_dbProvider = user_database.getDBProvider();
    setupUser();
  }

  @Test
  @DisplayName("Put Proxy Test")
  void TestPutProxy() {
    System.out.println("--- Test Put Proxy ---");
    String select_sql = "SELECT lox, crtr from tq_contents.proxy where crtr = ?";

    String INDEX = "topics";
    String LABEL = "nodeLabel";
    String DETAIL = "node description";
    String LANGUAGE = "en";
    
    IProxy node1 = proxyModel.newInstanceNode(
        database.getUUID(),
        ITQCoreOntology.TYPE_TYPE,
        LABEL, DETAIL, LANGUAGE,
        userName, IExtendedCoreOntology.PROVENANCE_TYPE,
        "smallImagePath.png",
        "largeImagePath.png",
        false);

    String proxy_locator = node1.getLocator();

    //
    // Insert the Proxy node in the Postgres database.
    //
    IResult rslt = dbConn.setProxyRole();
    if (rslt.hasError()) {
      fail("Execute putNode error 1: " + rslt.getErrorString());
    }
    rslt = database.putNodeNoMerge(node1);
    if (rslt.hasError()) {
      fail("Execute putNode error 2: " + rslt.getErrorString());
    }

    //
    // Select proxy information from the Postgres database.
    //
    String [] crtr_value = new String[1];
    crtr_value[0] = userName;
    rslt = dbConn.executeSelect(select_sql, crtr_value);
    if (rslt.hasError()) {
      fail("Execute select error: " + rslt.getErrorString());
    }

    Object o = rslt.getResultObject();
    if (o != null) {
      ResultSet rs = (ResultSet)o;

      try {
        if (rs.next()) {
          assertEquals("joeuser", rs.getString("crtr"));
          assertEquals(node1.getLocator(), rs.getString("lox"));
        }
      } catch (Exception e) {
        fail(e.getMessage());
      }
    } else {
      fail("Postgres object is null");
    }

    // Retrieve the label and detail information from Elasticsearch.
    rslt = esProvider.get(proxy_locator, INDEX);
    o = rslt.getResultObject();
    if (o != null) {
      JSONObject rs = (JSONObject)o;
      try {
        assertEquals(node1.getLocator(), rs.get(ITQCoreOntology.LOCATOR_PROPERTY));
        JSONObject label_jo = (JSONObject)rs.get(ITQCoreOntology.LABEL_PROPERTY);
        assertEquals(LABEL, ((JSONArray)label_jo.get(LANGUAGE)).get(0));
        JSONObject detail_jo = (JSONObject)rs.get(ITQCoreOntology.DETAILS_PROPERTY);
        assertEquals(DETAIL, ((JSONArray)detail_jo.get(LANGUAGE)).get(0));
      } catch (Exception e) {
        fail(e.getMessage());
      }
    } else {
      fail("Elasticsearch object is null");
    }

    System.out.println("--- Test Retrieve JSON ---");
    IResult json_rslt = database.getNodeAsJSONObject(proxy_locator, INDEX);

    o = json_rslt.getResultObject();
    if (o != null) {
      JSONObject jo = (JSONObject)o;
      assertEquals(false, jo.get("isVrt"));
      assertEquals(false, jo.get("isPrv"));
      assertEquals(ITQCoreOntology.TYPE_TYPE, jo.get("node_type"));
      assertEquals(true, jo.get("isLiv"));
      assertEquals("joeuser", jo.get("crtr"));
      assertEquals(null, jo.get("url"));
      assertEquals(proxy_locator, jo.get("lox"));
      assertEquals(node1.getVersion(), jo.get("_ver"));
      // assertEquals(node1.getDateString(),
      //              jo.get(ITQCoreOntology.CREATED_DATE_PROPERTY));
      // assertEquals(node1.getLastEditDateString(),
      //              jo.get(ITQCoreOntology.LAST_EDIT_DATE_PROPERTY));
    } else {
      fail("Postgres JSON object is null");
    }
  }

  @Test
  @DisplayName("Get Proxy Test")
  void TestGetProxy() {
    System.out.println("--- Test Get Proxy ---");
    String select_sql = "SELECT lox, crtr from tq_contents.proxy where lox = ?";

    String INDEX = "topics";
    String LABEL = "nodeLabel2";
    String DETAIL = "node description 2";
    String LANGUAGE = "en";
    
    IProxy node1 = proxyModel.newInstanceNode(
        database.getUUID(),
        ITQCoreOntology.TYPE_TYPE,
        LABEL, DETAIL, LANGUAGE,
        userName, IExtendedCoreOntology.PROVENANCE_TYPE,
        "smallImagePath2.png",
        "largeImagePath2.png",
        false);

    String proxy_locator = node1.getLocator();

    //
    // Insert the Proxy node in the Postgres database.
    //
    IResult rslt = dbConn.setProxyRole();
    rslt = database.putNodeNoMerge(node1);
    if (rslt.hasError()) {
      fail("Execute putNode error: " + rslt.getErrorString());
    }

    //
    // Select proxy information from the Postgres database.
    //
    rslt = dbConn.executeSelect(select_sql, node1.getLocator());
    if (rslt.hasError()) {
      fail("Execute select error: " + rslt.getErrorString());
    }

    Object o = rslt.getResultObject();
    if (o != null) {
      ResultSet rs = (ResultSet)o;

      try {
        if (rs.next()) {
          assertEquals("joeuser", rs.getString("crtr"));
          assertEquals(node1.getLocator(), rs.getString("lox"));
        }
      } catch (Exception e) {
        fail(e.getMessage());
      }
    } else {
      fail("Postgres object is null");
    }

    // Retrieve the label and detail information from Elasticsearch.
    rslt = esProvider.get(proxy_locator, INDEX);
    o = rslt.getResultObject();
    if (o != null) {
      JSONObject rs = (JSONObject)o;

      try {
        assertEquals(node1.getLocator(), rs.get(ITQCoreOntology.LOCATOR_PROPERTY));
        JSONObject label_jo = (JSONObject)rs.get(ITQCoreOntology.LABEL_PROPERTY);
        assertEquals(LABEL, ((JSONArray)label_jo.get(LANGUAGE)).get(0));
        JSONObject detail_jo = (JSONObject)rs.get(ITQCoreOntology.DETAILS_PROPERTY);
        assertEquals(DETAIL, ((JSONArray)detail_jo.get(LANGUAGE)).get(0));
      } catch (Exception e) {
        fail(e.getMessage());
      }
    } else {
      fail("Elasticsearch object is null");
    }

    System.out.println("--- Test Retrieve JSON ---");
    IResult json_rslt = database.getNodeAsJSONObject(proxy_locator, INDEX);

    o = json_rslt.getResultObject();
    if (o != null) {
      JSONObject jo = (JSONObject)o;
      assertEquals(false, jo.get("isVrt"));
      assertEquals(false, jo.get("isPrv"));
      assertEquals(ITQCoreOntology.TYPE_TYPE, jo.get("node_type"));
      assertEquals(true, jo.get("isLiv"));
      assertEquals("joeuser", jo.get("crtr"));
      assertEquals(null, jo.get("url"));
      assertEquals(proxy_locator, jo.get("lox"));
      assertEquals(node1.getVersion(), jo.get("_ver"));
      // assertEquals(node1.getDateString(),
      //              jo.get(ITQCoreOntology.CREATED_DATE_PROPERTY));
      // assertEquals(node1.getLastEditDateString(),
      //              jo.get(ITQCoreOntology.LAST_EDIT_DATE_PROPERTY));
    } else {
      fail("Postgres JSON object is null");
    }
  }

  @Test
  @DisplayName("Is Live Test")
  void TestIsLiveProxy() {
    System.out.println("--- Test Is Live ---");
    String select_sql = "SELECT lox, crtr from tq_contents.proxy where crtr = ? and lox = ?";

    String INDEX = "topics";
    String LABEL = "nodeLabelDead";
    String DETAIL = "dead node description";
    String LANGUAGE = "en";
    
    IProxy node1 = proxyModel.newInstanceNode(
        database.getUUID(),
        ITQCoreOntology.TYPE_TYPE,
        LABEL, DETAIL, LANGUAGE,
        userName, IExtendedCoreOntology.PROVENANCE_TYPE,
        "smallImagePath.png",
        "largeImagePath.png",
        false);

    String proxy_locator = node1.getLocator();

    //
    // Insert the Proxy node in the Postgres database.
    //
    IResult rslt = dbConn.setProxyRole();
    if (rslt.hasError()) {
      fail("Execute putNode error 1: " + rslt.getErrorString());
    }
    rslt = database.putNodeNoMerge(node1);
    if (rslt.hasError()) {
      fail("Execute putNode error 2: " + rslt.getErrorString());
    }

    //
    // Select proxy information from the Postgres database.
    //
    String [] values = new String[2];
    values[0] = userName;
    values[1] = proxy_locator;
    rslt = dbConn.executeSelect(select_sql, values);
    if (rslt.hasError()) {
      fail("Execute select error: " + rslt.getErrorString());
    }

    Object o = rslt.getResultObject();
    if (o != null) {
      ResultSet rs = (ResultSet)o;

      try {
        if (rs.next()) {
          assertEquals("joeuser", rs.getString("crtr"));
          assertEquals(node1.getLocator(), rs.getString("lox"));
        }
      } catch (Exception e) {
        fail(e.getMessage());
      }
    } else {
      fail("Postgres object is null");
    }

    // Set the isLive attribute to false
    IResult proxy_result = database.getNode(proxy_locator, credentials);
    IProxy proxy = (IProxy)proxy_result.getResultObject();
    proxy.setIsLive(false);

    System.out.println("--- Test Retrieve Proxy ---");
    proxy_result = database.getNode(proxy_locator, credentials);

    assertEquals(null, proxy_result);
  }

  @AfterAll
  static void tearDown() {
    deleteProxies();
    dropUser();
    proxy_environment.shutDown();
    user_environment.shutDown();
  }

  private static void setupUser() {
    String user_sql = "INSERT INTO users " +
        "(userid, email, password, handle, full_name) " +
        "VALUES (?, ?, ?, ?, ?)";

    String [] userValues = new String [5];
    userValues[0] = "joeuser";
    userValues[1] = "joeuser@email.org";
    userValues[2] = "joeuser-pwd";
    userValues[3] = "joeuser";
    userValues[4] = "Joe User";

    try {
      userConn = user_dbProvider.getConnection();
    } catch (SQLException e) {
      System.out.println("Error in user setup: " + e.getMessage());
    }

    IResult r = userConn.setUsersRole();
    r = userConn.beginTransaction();
    userConn.executeSQL(user_sql, r, userValues);
    userConn.endTransaction(r);
    userConn.closeConnection(r);
    if (r.hasError()) {
      System.out.println("Error in setupUser: " + r.getErrorString());
    }
    credentials = new TicketPojo(userValues[0]);
  }

  private static void dropUser() {
    String user_sql = "DELETE FROM tq_authentication.users WHERE userid = ?";

    String [] userValues = new String [1];
    userValues[0] = "joeuser";

    try {
      userConn = user_dbProvider.getConnection();
    } catch (SQLException e) {
      System.out.println("Error in user setup: " + e.getMessage());
    }

    IResult r = userConn.setUsersRole();
    r = userConn.beginTransaction();
    userConn.executeSQL(user_sql, r, userValues);
    userConn.endTransaction(r);
    userConn.closeConnection(r);
    if (r.hasError()) {
      System.out.println("Error in dropUser: " + r.getErrorString());
    }
  }

  private static void deleteProxies() {
    String select_sql = "SELECT lox FROM tq_contents.proxy WHERE crtr = ?";
    String proxy_sql = "DELETE FROM tq_contents.proxy WHERE crtr = ?";
    String props_sql = "DELETE FROM tq_contents.properties WHERE proxyid = ?";

    String [] userValues = new String[1];
    userValues[0] = userName;

    dbConn.setProxyRole();
    IResult rslt = dbConn.executeSelect(select_sql, userValues);

    Object o = rslt.getResultObject();

    dbConn.beginTransaction();

    if (o != null) {
      ResultSet rs = (ResultSet)o;

      try {
        while (rs.next()) {
          userValues[0] = rs.getString(1);
          dbConn.executeSQL(props_sql, userValues);
        }
      } catch (Exception e) {
        fail(e.getMessage());
      }
    } else {
      fail("Postgres object is null");
    }
    
    userValues[0] = userName;
    dbConn.executeSQL(proxy_sql, userValues);
    dbConn.endTransaction();
  }
}
