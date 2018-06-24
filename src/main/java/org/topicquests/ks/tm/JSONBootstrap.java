/**
 * Copyright 2015...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm;

import java.io.*;
import java.sql.ResultSet;
import java.util.*;

import org.topicquests.support.util.TextFileHandler;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.IExtendedCoreOntology;
import org.topicquests.ks.api.ITQCoreOntology;
// import org.topicquests.ks.api.ITQDataProvider;
import org.topicquests.ks.api.ITicket;
// import org.topicquests.ks.tm.api.ISubjectProxy;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.pg.PostgresConnection;
import org.topicquests.pg.api.IPostgresConnection;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;


/**
 * @author Park
 * A class to replace hard-coded bootstrapping with
 * file loading from JSON files
 */
public class JSONBootstrap {
  private SystemEnvironment environment;
  private DataProvider dataPovider;
  private IProxyModel proxyModel;
  private String userId = ITQCoreOntology.SYSTEM_USER;
  private ITicket credentials;
  private final String path = "data/bootstrap/";
  private TextFileHandler handler;
  private final String USER_ID = "SystemUser",
    BOOTSTRAP_PROVENANCE = "BootstrapProvenance";

  /**
   * 
   */
  public JSONBootstrap(SystemEnvironment env) {
    this.environment = env;
    this.dataPovider = environment.getDataProvider();
    this.proxyModel = environment.getProxyModel();
    this.credentials = new TicketPojo();
    credentials.setUserLocator(userId);
    handler = new TextFileHandler();
    validateSystemUser();
    System.out.println("JSONBOOTSTRAP+");
  }
  
  void validateSystemUser() {
    try {
      IPostgresConnection conn = dataPovider.getDBProvider().getConnection();
      conn.beginTransaction();
      
      String sql = "SELECT userid FROM tq_authentication.users where userid=?";
      Object [] vals = new Object[1];
      vals[0] =ITQCoreOntology.SYSTEM_USER;
      
      IResult r = conn.executeSelect(sql, vals);
      ResultSet rs = (ResultSet)r.getResultObject();
      environment.logDebug("JSONBootstrap.validateSystemUser- " + r.getErrorString() + " | " + r.getResultObject());
      
      if (!rs.next()) {
        sql = "INSERT INTO tq_authentication.users VALUES (?, ?, ?, ?)";
        vals = new Object[4];
        vals[0] = ITQCoreOntology.SYSTEM_USER;
        vals[1] = "sysusr@topicquests.org";
        vals[2] = "SysPwd!";
        vals[3] = "SystemUser";
        r = conn.setUsersRole();
        environment.logDebug("JSONBootstrap.validateSystemUser-1a " + r.getErrorString() + " | " + r.getResultObject());
        conn.executeSQL(sql, r, vals);
        environment.logDebug("JSONBootstrap.validateSystemUser-1b " + r.getErrorString() + " | " + r.getResultObject());
      }
      conn.endTransaction();
      conn.closeConnection(r);
    } catch (Exception e) {
      environment.logError(e.getMessage(), e);
      e.printStackTrace();
    }
  }

  private void showUserInfo() {
    String sql = "SELECT CURRENT_USER, SESSION_USER";

    try {
      IPostgresConnection conn = dataPovider.getDBProvider().getConnection();

      IResult rslt = conn.executeSelect(sql);
      ResultSet rs = (ResultSet)rslt.getResultObject();
      if (rs.next()) {
        environment.logDebug("JSONBootstrap.validateSystemUser- current_user: " + rs.getString(1));
        environment.logDebug("JSONBootstrap.validateSystemUser- session_user: " + rs.getString(2));
        conn.closeResultSet(rs, rslt);
      }
      conn.closeConnection(rslt);
    } catch (Exception e) {
      environment.logError(e.getMessage(), e);
      e.printStackTrace();
    }
  }

  public IResult bootstrap() {
    environment.logDebug("JSONBootstrap- ");
    showUserInfo();
    
    IResult result = new ResultPojo();
    IResult r = dataPovider.getNode(ITQCoreOntology.TYPE_TYPE, credentials);
    if (r.hasError())
      result.addErrorString(r.getErrorString());
    
    environment.logDebug("JSONBootstrap-1 "+r.getErrorString()+" | "+r.getResultObject());
    
    if (r.getResultObject() == null) {
      File dir = new File(path);
      System.out.println("JSONBOOTSTRAP.bootstrap "+dir.getAbsolutePath());
      File files [] = dir.listFiles();
      int len = files.length;
      File f;
      
      r = null;
      for (int i = 0; i < len; i++) {
        f = files[i];
        System.out.println(f.getAbsolutePath());
        if (f.getName().endsWith(".json")) {
          r = importJSONFile(f);
          if (r.hasError())
            result.addErrorString(r.getErrorString());
        }
      }
    } else {
      IProxy p = (IProxy)r.getResultObject();
      System.out.println("FOO " + p.toJSONString());
    }
    return result;
  }
	
  /**
   * Testing the first line of each file. Return <code>true</code>
   * if this class already exists.
   * @param p
   * @return
   */
  private boolean seenThis(JSONObject p) {
    boolean result = false;
    String lox = (String)p.get("lox");
    IResult r = dataPovider.getNode(lox, credentials);
    
    result = (r.getResultObject() != null);
    System.out.println("SEEN THIS "+lox+" "+r.getResultObject());
    return result;
  }
	
  private IResult importJSONFile(File f) {
    environment.logDebug(f.getName());
    IResult result = new ResultPojo();
    String json = handler.readFile(f);
    JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    
    try {
      JSONObject jo = (JSONObject)p.parse(json);
      List<JSONObject> o = (List<JSONObject>)jo.get("nodes");
      
      if (o != null) {
        IResult r;
        Iterator<JSONObject>itr = o.iterator();
        JSONObject x;
        boolean isFirst = true;
        
        while (itr.hasNext()) {
          x = itr.next();
          if (x.get("lox") != null) {
            if (isFirst) {
              if (seenThis(x)) {
                return result;
              }
              isFirst = false;
            }
            r = buildProxy(x);
            if (r.hasError())
              result.addErrorString(r.getErrorString());
          }
        }
      } else {
        result.addErrorString(f.getName()+" MISSING Data");
      }
    } catch (Exception e) {
      environment.logError("JSONBootstrap1 "+e.getMessage(), e);
      result.addErrorString(e.getMessage());
    }
    return result;
  }
	
  private IResult buildProxy(JSONObject jo)  {
    environment.logDebug("Bootstrap " + jo.toJSONString());
    IResult result = new ResultPojo();
    environment.logDebug("Bootstrap-1 " + result.getErrorString());
    String subOf = jo.getAsString(ITQCoreOntology.SUBCLASS_OF_PROPERTY_TYPE);
    IProxy n = null;
    
    if (subOf != null) {
      n = this.proxyModel.newSubclassNode( 
          jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), 
          subOf,
          jo.getAsString(ITQCoreOntology.LABEL_PROPERTY), 
          jo.getAsString(ITQCoreOntology.DETAILS_PROPERTY), 
          "en", 
          USER_ID, 
          IExtendedCoreOntology.BOOTSTRAP_PROVENANCE_TYPE,
          jo.getAsString(ITQCoreOntology.SMALL_IMAGE_PATH), 
          jo.getAsString(ITQCoreOntology.LARGE_IMAGE_PATH), false);
    } else {
      n = this.proxyModel.newNode( 
          jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), 
          jo.getAsString(ITQCoreOntology.LABEL_PROPERTY), 
          jo.getAsString(ITQCoreOntology.DETAILS_PROPERTY), 
          "en", 
          USER_ID, 
          jo.getAsString(ITQCoreOntology.SMALL_IMAGE_PATH), 
          jo.getAsString(ITQCoreOntology.LARGE_IMAGE_PATH), false);
    }
    List<String> tc = (List<String>)jo.get(ITQCoreOntology.TRANSITIVE_CLOSURE_PROPERTY_TYPE);
    environment.logDebug("Bootstrap-2 "+n.listTransitiveClosure());

    if (tc != null)
      n.setTransitiveClosure(tc);
    if (subOf != null)
      n.addTransitiveClosureLocator(subOf);
    environment.logDebug("Bootstrap-3 "+n.toJSONString());
    dataPovider.putNode(n);
    environment.logDebug("Bootstrap+ "+result.getErrorString());
    return result;
  }
}
