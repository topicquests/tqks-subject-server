/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONArray;

import org.topicquests.support.util.DateUtil;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.ks.SystemEnvironment;

import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.IDataProvider;
import org.topicquests.ks.tm.api.IParentChildContainer;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.ITuple;

import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.pg.api.IPostgresConnection;

public class Proxy implements IProxy, ITuple, IParentChildContainer {

  protected JSONObject data;
  protected SystemEnvironment environment;
  protected IDataProvider database;
  protected PostgresConnectionFactory factory = null;
  
  public Proxy(JSONObject jo) {
    data = jo;
    environment = SystemEnvironment.getInstance();
    database = environment.getDataProvider();
    factory = database.getDBProvider();
  }

  //
  // The following getters and setters access proxy data that
  // is stored in the cache and does not require database access.
  //
  @Override
  public void setLocator(String locator) {
    data.put(ITQCoreOntology.LOCATOR_PROPERTY, locator);
  }

  @Override
  public String getLocator() {
    return (String)data.get(ITQCoreOntology.LOCATOR_PROPERTY);
  }

  @Override
  public void setCreatorId(String id) {
    data.put(ITQCoreOntology.CREATOR_ID_PROPERTY, id);
  }

  @Override
  public String getCreatorId() {
    return (String)data.get(ITQCoreOntology.CREATOR_ID_PROPERTY);
  }

  @Override
  public void setNodeType(String typeLocator) {
    data.put("node_type", typeLocator);
  }

  @Override
  public String getNodeType() {
    return (String)data.get("node_type");
  }

  @Override
  public void setURL(String url) {
    data.put(ITQCoreOntology.RESOURCE_URL_PROPERTY, url);
  }

  @Override
  public String getURL() {
    return (String)data.get(ITQCoreOntology.RESOURCE_URL_PROPERTY);
  }

  @Override
  public void setIsVirtualProxy(boolean t) {
    data.put(ITQCoreOntology.IS_VIRTUAL_PROXY, t);
  }

  @Override
  public void setIsVirtualProxy(IPostgresConnection conn, boolean t) {
    data.put(ITQCoreOntology.IS_VIRTUAL_PROXY, t);
    String sql =
        "INSERT INTO tq_contents.proxy SET \"isVrt\" = ? WHERE "+
        "proxyid = ?  ON CONFLICT DO UPDATE";
    Object [] vals = new Object[2];
    vals[0] = t;
    vals[1] = this.getLocator();
    IResult r = conn.executeSQL(sql, vals);
  }

  @Override
  public boolean getIsVirtualProxy() {
    Object x = data.get(ITQCoreOntology.IS_VIRTUAL_PROXY);
    if (x != null) {
      if (x instanceof String) {
        if (x != null) {
          return Boolean.parseBoolean((String)x);
        }
      } else {
        return ((Boolean)x).booleanValue();
      }
    }
    return false;
  }

  @Override
  public void setIsPrivate(boolean isPrivate) {
    data.put(ITQCoreOntology.IS_PRIVATE_PROPERTY, isPrivate);
  }

  @Override
  public void setIsPrivate(IPostgresConnection conn, boolean isPrivate) {
    setIsPrivate(isPrivate);
    String sql = "UPDATE tq_contents.proxy SET \"isPrv\" = ? where lox = ?";
    Object [] vals = new Object[2];
    vals[0] = isPrivate;
    vals[1] = this.getLocator();
    IResult r = conn.executeSQL(sql, vals);
  }

  @Override
  public void setIsPrivate(String t) {
    setIsPrivate(t.equalsIgnoreCase("true"));
  }

  @Override
  public boolean getIsPrivate() {
    Object x = data.get(ITQCoreOntology.IS_PRIVATE_PROPERTY);

    if (x != null) {
      if (x instanceof String) {
        if (x != null) {
          return Boolean.parseBoolean((String)x);
        }
      } else {
        return ((Boolean)x).booleanValue();
      }
    }
    return false;
  }

  @Override
  public boolean getIsLive() {
    Boolean o = (Boolean)data.get(ITQCoreOntology.IS_LIVE);
    if (o != null)
      return o.booleanValue();
    return true;
  }
  
  @Override
  public void setIsLive(boolean t) {
    data.put(ITQCoreOntology.IS_LIVE, t);
  }

  @Override
  public void setDate(Date date) {
    data.put(ITQCoreOntology.CREATED_DATE_PROPERTY, DateUtil.formatIso8601(date));
  }

  @Override
  public void setDate(String date) {
    data.put(ITQCoreOntology.CREATED_DATE_PROPERTY, date);
  }

  @Override
  public Date getDate() {
    String dx = data.getAsString(ITQCoreOntology.CREATED_DATE_PROPERTY);
    return DateUtil.fromIso8601(dx);
  }

  @Override
  public String getDateString() {
    return data.getAsString(ITQCoreOntology.CREATED_DATE_PROPERTY);
  }

  @Override
  public String getLastEditDateString() {
    return data.getAsString(ITQCoreOntology.LAST_EDIT_DATE_PROPERTY);
  }
  
  @Override
  public void setLastEditDate(Date date) {
    data.put(ITQCoreOntology.LAST_EDIT_DATE_PROPERTY, DateUtil.formatIso8601(date));
    setVersion(Long.toString(System.currentTimeMillis()));
  }

  @Override
  public void setLastEditDate(String date) {
    data.put(ITQCoreOntology.LAST_EDIT_DATE_PROPERTY, date);
    setVersion(Long.toString(System.currentTimeMillis()));
  }

  @Override
  public void setLastEditDate(IPostgresConnection conn, String date, IResult r) {
    // TODO Auto-generated method stub
  }
  
  @Override
  public Date getLastEditDate() {
    String dx = (String)data.get(ITQCoreOntology.LAST_EDIT_DATE_PROPERTY);
    return DateUtil.fromIso8601(dx);
  }

  @Override
  public void setVersion(String version) {
    data.put(ITQCoreOntology.VERSION, version);
  }

  @Override
  public String getVersion() {
    return (String)data.get(ITQCoreOntology.VERSION);
  }

  //
  // The following getters and setters access proxy data that
  // is solely accessed from a data store.
  //
  @Override
  public void setRelationWeight(double weight) {
    data.put(ITQCoreOntology.RELATION_WEIGHT, new Double(weight));
  }

  @Override
  public double getRelationWeight() {
    Double d = (Double)data.get(ITQCoreOntology.RELATION_WEIGHT);
    if (d == null)
      return -9999;
    return d.doubleValue();
  }

  @Override
  public void setObject(String value) {
    data.put(ITQCoreOntology.TUPLE_OBJECT_PROPERTY, value);
  }

  @Override
  public void setObjectType(String typeLocator) {
    data.put(ITQCoreOntology.TUPLE_OBJECT_TYPE_PROPERTY, typeLocator);
  }

  @Override
  public String getObject() {
    return (String)data.get(ITQCoreOntology.TUPLE_OBJECT_PROPERTY);
  }

  @Override
  public String getObjectType() {
    return (String)data.get(ITQCoreOntology.TUPLE_OBJECT_TYPE_PROPERTY);
  }

  @Override
  public void setObjectRole(String roleLocator) {
    data.put(ITQCoreOntology.TUPLE_OBJECT_ROLE_PROPERTY, roleLocator);
  }

  @Override
  public String getObjectRole() {
    return (String)data.get(ITQCoreOntology.TUPLE_OBJECT_ROLE_PROPERTY);
  }

  @Override
  public void setSubjectLocator(String locator) {
    data.put(ITQCoreOntology.TUPLE_SUBJECT_PROPERTY, locator);
  }

  @Override
  public String getSubjectLocator() {
    return (String)data.get(ITQCoreOntology.TUPLE_SUBJECT_PROPERTY);
  }

  @Override
  public void setSubjectType(String subjectType) {
    data.put(ITQCoreOntology.TUPLE_SUBJECT_TYPE_PROPERTY, subjectType);
  }

  @Override
  public String getSubjectType() {
    return (String)data.get(ITQCoreOntology.TUPLE_SUBJECT_TYPE_PROPERTY);
  }

  @Override
  public void setSubjectRole(String roleLocator) {
    data.put(ITQCoreOntology.TUPLE_SUBJECT_ROLE_PROPERTY, roleLocator);
  }

  @Override
  public String getSubjectRole() {
    return (String)data.get(ITQCoreOntology.TUPLE_SUBJECT_ROLE_PROPERTY);
  }

  @Override
  public void setIsTransclude(boolean isT) {
    String x = (isT ? "true":"false");
    data.put(ITQCoreOntology.TUPLE_IS_TRANSCLUDE_PROPERTY, x);
  }

  @Override
  public void setIsTransclude(String t) {
    setIsTransclude(t.equalsIgnoreCase("true"));
  }

  @Override
  public boolean getIsTransclude() {
    Object x = data.get(ITQCoreOntology.TUPLE_IS_TRANSCLUDE_PROPERTY);
    
    if (x != null) {
      if (x instanceof String) {
        if (x != null) {
          return Boolean.parseBoolean((String)x);
        }
      } else {
        return ((Boolean)x).booleanValue();
      }
    }
    return false;
  }

  @Override
  public void addScope(String scopeLocator) {
    this.addPropertyValue(ITQCoreOntology.SCOPE_LIST_PROPERTY_TYPE, scopeLocator);
  }

  @Override
  public void addMergeReason(String reason) {
    // this.addMultivaluedSetStringProperty(ITQCoreOntology.MERGE_REASON_RULES_PROPERTY, reason);
  }

  @Override
  public List<String> listMergeReasons() {
    return this.getMultivaluedProperty(ITQCoreOntology.MERGE_REASON_RULES_PROPERTY);
  }

  @Override
  public List<String> listScopes() {
    return this.getMultivaluedProperty(ITQCoreOntology.SCOPE_LIST_PROPERTY_TYPE);
  }

  @Override
  public void setThemeLocator(String themeLocator) {
    data.put(ITQCoreOntology.TUPLE_THEME_PROPERTY, themeLocator);

  }

  @Override
  public String getThemeLocator() {
    return (String)data.get(ITQCoreOntology.TUPLE_THEME_PROPERTY);
  }

  @Override
  public IResult doUpdate() {
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;
    
    try {
      conn = factory.getConnection();
      doUpdate(conn, result);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(result);

    setLastEditDate(new Date());
    return result;
  }

  @Override
  public void doUpdate(IPostgresConnection conn, IResult r) {
    String newVersion = Long.toString(System.currentTimeMillis());
    this.setVersion(newVersion);
    String sql = "UPDATE tq_contents.properties SET property_val = ? WHERE proxyid = ? "+
        "AND property_key = ?";
    Object [] vals = new Object[3];
    
    vals[0] = newVersion;
    vals[1] = this.getLocator();
    vals[2] = ITQCoreOntology.VERSION;
    conn.executeSQL(sql, r, vals);
    vals[2] = ITQCoreOntology.LAST_EDIT_DATE_PROPERTY;
    conn.executeSQL(sql, r, vals);
    database.removeFromCache(this.toString());
  }

  @Override
  public List<String> listMergeTupleLocators() {
    List<String> result = new ArrayList<String>();
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();

    try {
      conn = factory.getConnection();
      result = listMergeTupleLocators(conn, r);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);

    return result;
  }
  
  @Override
  public List<String> listMergeTupleLocators(IPostgresConnection conn, IResult r) {
    List<String> result = new ArrayList<String>();
    String sql =
        "SELECT property_val FROM tq_contents.properties WHERE " +
  	"property_key = '" + ITQCoreOntology.MERGE_TUPLE_PROPERTY + "' AND " +
  	"proxyid = ?";

    IResult x = conn.executeSelect(sql, r, this.getLocator());
    ResultSet rs = (ResultSet)r.getResultObject();

    if (rs != null) {
      try {
        while (rs.next()) {
          result.add(rs.getString("property_val"));
        }
      } catch (SQLException e) {
        environment.logError(e.getMessage(), e);
      }
    }
    return result;
  }

  @Override
  public boolean isTuple() {
    String test = getSubjectLocator();
    
    // Is this a node or a tuple?
    if (test != null && ! test.equals(""))
      return true;
    return false;
  }

  @Override
  public JSONObject getData() {
    return data;
  }

  @Override
  public String toJSONString() {
    return data.toJSONString();
  }

  @Override
  public void setIsFederated(boolean t) {
    data.put(ITQCoreOntology.IS_FEDERATED, new Boolean(t));
  }

  @Override
  public boolean getIsFederated() {
    Boolean o = (Boolean)data.get(ITQCoreOntology.IS_FEDERATED);
    if (o != null)
      return o.booleanValue();
    return false;
  }

  @Override
  public void addLabel(String label, String language) {
    JSONObject labels = database.addLabel(this.getLocator(), label, language);
     data.put(ITQCoreOntology.LABEL_PROPERTY, labels);
  }

  @Override
  public JSONObject getLabel() {
    // TODO this is wrong
    return (JSONObject)data.get(ITQCoreOntology.LABEL_PROPERTY);
  }
  
  private JSONObject getLabels() {
    return (JSONObject)data.get(ITQCoreOntology.LABEL_PROPERTY);
  }

  @Override
  public String getLabel(String language) {
    String result = null;
    JSONObject labels = getLabels();
    if (labels != null) {
      List<String>l = (List<String>)labels.get(language.toLowerCase());
      if (l != null)
        result = l.get(0);
    }
    return result;
  }

  

  @Override
  public List<String> listLabels() {
    List<String>result = new ArrayList<String>();
    JSONObject labels = getLabels();
    
    if (labels != null && !labels.isEmpty()) {
      Iterator<String>itr = labels.keySet().iterator();
      List<String>lx;
      
      while (itr.hasNext()) {
        lx = (List<String>)labels.get(itr.next());
        if (lx != null)
          result.addAll(lx);
      }
    }
	  
    return result;
  }

  @Override
  public List<String> listLabels(String language) {
    List<String>result = null;
    JSONObject labels = getLabels();
    
    if (labels != null && !labels.isEmpty()) {
      result = (List<String>)labels.get(language.toLowerCase());
    }
    
    return result;
  }
 
  @Override
  public void addDetails(String detail, String language) {
	  
    JSONObject details = database.addDetails(this.getLocator(), detail, language);
    data.put(ITQCoreOntology.DETAILS_PROPERTY, details);
  }

  @Override
  public JSONObject getDetails() {
	  JSONObject jo = (JSONObject)data.get(ITQCoreOntology.DETAILS_PROPERTY);
	  if (jo == null) {
		  jo = database.fetchDetails(this.getLocator());
		  if (jo != null)
			  data.put(ITQCoreOntology.DETAILS_PROPERTY, jo);
	  }
    return jo;
  }

  @Override
  public String getDetails(String language) {
	  String result = null;
	  JSONObject det = getDetails();
	  environment.logDebug("Proxy.getDetails "+det);
	  if (det != null) {
		  List<String> x = (List<String>)det.get(language);
		  if (x != null && !x.isEmpty())
			  result = x.get(0);
	  }
		  
    return result;
  }

  @Override
  public List<String> listDetails() {
    List<String> result = new ArrayList<String>();
    JSONObject det = getDetails();
    
    if (det != null && !det.isEmpty()) {
      List<String>x;
      Iterator<String>itr = det.keySet().iterator();
      
      while (itr.hasNext()) {
        x = (List<String>)det.get(itr.next());
        if (x != null)
          result.addAll(x);
      }
    }
    return result;
  }

  @Override
  public List<String> listDetails(String language) {
    List<String>result= null;
    JSONObject det = getDetails();
    
    if (det != null && !det.isEmpty()) {
      result = (List<String>)det.get(language.toLowerCase());
    }
    return result;
  }

  @Override
  public void setSmallImage(String img, boolean isBootstrap) {
	  if (isBootstrap)
		  data.put(ITQCoreOntology.SMALL_IMAGE_PATH, img);
	  else 
		  this.setProperty(ITQCoreOntology.SMALL_IMAGE_PATH, img);
  }

  @Override
  public void setImage(String img, boolean isBootstrap) {
	  if (isBootstrap)
		  data.put(ITQCoreOntology.LARGE_IMAGE_PATH, img);
	  else 
		  this.setProperty(ITQCoreOntology.LARGE_IMAGE_PATH, img);
	  
  }

  @Override
  public String getSmallImage(boolean isBootstrap) {
	  if (isBootstrap)
		  return data.getAsString(ITQCoreOntology.SMALL_IMAGE_PATH);
    Object o = this.getProperty(ITQCoreOntology.SMALL_IMAGE_PATH);
    return (String)o;
	
  }

  @Override
  public String getImage(boolean isBootstrap) {
	  if (isBootstrap)
		  return data.getAsString(ITQCoreOntology.LARGE_IMAGE_PATH);
	  Object o = this.getProperty(ITQCoreOntology.LARGE_IMAGE_PATH);
	  return (String)o;
  }

  @Override
  public void addSuperclassId(String superclassLocator, boolean isBootstrap) {
    List<String> ids = listSuperclassIds(false);
    if (ids == null || ids.size() == 0) {
      ids = new ArrayList<String>();
      data.put(ITQCoreOntology.SUBCLASS_OF_PROPERTY_TYPE, ids);
    }
    if (!ids.contains(superclassLocator))
      ids.add(superclassLocator);
  }

  @Override
  public void addSuperclassId(IPostgresConnection conn, String superclassLocator, boolean isBootstrap, IResult r) {
    String sql = 
        "INSERT INTO tq_contents.superclasses (proxyid, superclass) VALUES (?, ?)";
    Object [] vals = new Object[2];
    vals[0] = this.getLocator();
    vals[1] = superclassLocator;
    conn.executeSQL(sql, r, vals);
  }

  @Override
  public void setProperty(String key, Object value) {
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
    
    try {
      conn = factory.getConnection();
      setProperty(conn, key, value, r);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);
  }

  @Override
  public void setProperty(IPostgresConnection conn, String key, Object value, IResult r) {
    environment.logDebug("Proxy.setProperty- "+key+" "+value);
    String sql = "INSERT INTO tq_contents.properties (proxyid, property_key, property_val) VALUES (?, ?, ?)";

    if (value instanceof String)  {
      Object[] vals = new Object[3];
      vals[0] = this.getLocator();
      vals[1] = key;
      vals[2] = value;
      conn.executeSQL(sql, r, vals);
    } else if (value instanceof JSONObject) {
        Object[] vals = new Object[3];
        vals[0] = this.getLocator();
        vals[1] = key;
        vals[2] = ((JSONObject) value).toJSONString();
        conn.executeSQL(sql, r, vals);
    } else {
      List<String> vx = (List<String>)value;
      Object[] values = new Object[3 * vx.size()];
      int index = 0;
      
      Iterator<String>itr = vx.iterator();
      while (itr.hasNext()) {
        values[index++] = this.getLocator();
        values[index++] = key;
        values[index++] = itr.next();
      }
      conn.executeBatch(sql, r, values);
    }
  }

  @Override
  public void addPropertyValue(String key, String value) {
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
    
    try {
      conn = factory.getConnection();
      addPropertyValue(conn, key, value, r);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);
  }

  @Override
  public void addPropertyValue(IPostgresConnection conn, String key, String value, IResult r) {
    String sql = 
        "INSERT INTO tq_contents.properties (proxyid, property_key, property_val) VALUES (?, ?, ?) ON CONFLICT DO NOTHING";
    Object [] vals = new Object[3];
    vals[0] = this.getLocator();
    vals[1] = key;
    vals[2] = value;
    conn.executeSQL(sql, r, vals);
    environment.logDebug("Proxy.addPropertyValue "+key+" "+value+" "+r.getErrorString());
  }

  @Override
  public Object getProperty(String key) {
    Object result = null;
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
    
    try {
      conn = factory.getConnection();
      if (r.hasError())
    	  environment.logError("S "+r.getErrorString(),null);
      result = getProperty(conn, key, r);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);
    return result;
  }

  @Override
  public Object getProperty(IPostgresConnection conn, String key, IResult r) {
	environment.logDebug("Proxy.getProperty- "+key+" "+this.getLocator());
    Object result = null;
    String sql = "SELECT property_val FROM tq_contents.properties WHERE proxyid = ? AND property_key = ?";

    Object [] _vals = new Object[2];
    _vals[0] = this.getLocator();
    _vals[1] = key;
    
    conn.executeSelect(sql, r, _vals);
    ResultSet rs = (ResultSet)r.getResultObject();
    environment.logDebug("Proxy.getProperty "+r.getErrorString()+" "+rs);
    if (rs != null) {
      try {
        String val = null;
        List<String> vals= null;
        //We are either seeing a singleton or a collection
        while (rs.next()) {
          if (val == null)
            val = rs.getString(1);
          else if (vals == null) {
            //more than one pass through while loop
            vals = new ArrayList<String>();
            vals.add(val);
            vals.add(rs.getString(1));
          } else {
            vals.add(rs.getString(1));
          }
        }
//        environment.logDebug("Proxy.getProperty-2 "+val+" "+vals);
        if (vals != null)
          result = vals;
        else
          result = val;
      } catch (SQLException e) {
        environment.logError(e.getMessage(), e);
        r.addErrorString(e.getMessage());
      }
      conn.closeResultSet(rs, r);

    }
    return result;
  }

  @Override
  public void removeProperty(String key) {
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
    
    try {
      conn = factory.getConnection();
      removeProperty(conn, key, r);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);
  }

  @Override
  public void removeProperty(IPostgresConnection conn, String key, IResult r) {
    String sql = "DELETE FROM tq_contents.properties WHERE proxyid = ? AND property_key = ?";
    Object [] vals = new Object[2];
    vals[0] = this.getLocator();
    vals[1] = key;
    conn.executeSQL(sql, r, vals);
  }

  @Override
  public void removePropertyValue(String key, String value) {
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
    
    try {
      conn = factory.getConnection();
      removePropertyValue(conn, key, value, r);
      if (r.hasError())
        environment.logError(r.getErrorString(), null);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);
  }

  @Override
  public void removePropertyValue(IPostgresConnection conn, String key, String value, IResult r) {
    String sql = "DELETE FROM tq_contents.properties WHERE proxyid = ? AND property_key = ? AND property_val = ?";
    Object [] vals = new Object[3];
    vals[0] = this.getLocator();
    vals[1] = key;
    vals[2] = value;
    conn.executeSQL(sql, r, vals);
  }

  @Override
  public void replacePropertyValue(String key, String oldValue, String newValue) {
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
    
    try {
      conn = factory.getConnection();
      replacePropertyValue(conn, key, oldValue, newValue, r);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);
  }

  @Override
  public void replacePropertyValue(IPostgresConnection conn, String key, String oldValue, String newValue, IResult r) {
    String sql = "UPDATE tq_contents.properties SET property_val = ? " +
        "WHERE proxyid = ? AND property_key = ? AND property_val = ?";
    Object [] vals = new Object[4];
    vals[0] = newValue;
    vals[1] = this.getLocator();
    vals[2] = key;
    vals[3] = oldValue;

    // try update
    IResult x = conn.executeUpdate(sql, r, vals);
    Integer rowcount = (Integer)x.getResultObject();
    if (rowcount.intValue() == 0) {
      sql = "INSERT INTO tq_contents.properties VALUES (?, ?, ?)";
      vals = new Object[3];
      vals[0] = this.getLocator();
      vals[1] = key;
      vals[2] = newValue;
      conn.executeSelect(sql, r, vals);
    }
  }

  @Override
  public List<String> listSuperclassIds(boolean isBootstrap) {
    List<String> result = null;
    
    if (isBootstrap) {
      result = (List<String>)data.get(ITQCoreOntology.SUBCLASS_OF_PROPERTY_TYPE);
    } else {
      IPostgresConnection conn = null;
      IResult r = new ResultPojo();
      
      try {
        conn = factory.getConnection();
        result = listSuperclassIds(conn, r);
      } catch (SQLException e) {
        environment.logError(e.getMessage(), e);
      }
      conn.closeConnection(r);
    }
    return result;
  }

  @Override
  public List<String> listSuperclassIds(IPostgresConnection conn, IResult r) {
    List<String> result = new ArrayList<String>();
    String sql = "SELECT superclass FROM tq_contents.superclasses WHERE proxyid = ?";
    IResult rx = conn.executeSelect(sql, r, this.getLocator());
    ResultSet rs = (ResultSet)rx.getResultObject();
    
    if (rs != null) {
      try {
        while (rs.next())
          result.add(rs.getString("superclass"));
      } catch (SQLException e) {
        environment.logError(e.getMessage(), e);
        r.addErrorString(e.getMessage());
      }
      conn.closeResultSet(rs, r);
    }
    return result;
  }

  @Override
  public boolean isA(String typeLocator) {
    if (this.getLocator().equals(typeLocator))
      return true;
    if (this.getNodeType() != null && this.getNodeType().equals(typeLocator))
      return true;
    List<String>sups = this.listTransitiveClosure();
    if (sups != null && !sups.isEmpty())
      return sups.contains(typeLocator);
    return false;
  }

  @Override
  public void addPSI(String psi) {
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
    
    try {
      conn = factory.getConnection();
      addPSI(conn, psi, r);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);
  }

  @Override
  public void addPSI(IPostgresConnection conn, String psi, IResult r) {
    String sql = 
        "INSERT INTO tq_contents.psi (proxyid, psi) VALUES (?, ?) ON CONFLICT DO NOTHING";
  	
    Object [] vals = new Object[2];
    vals[0] = this.getLocator();
    vals[1] = psi;
    conn.executeSQL(sql, r, vals);
  }

  @Override
  public void removePSI(String psi) {
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
    try {
      conn = factory.getConnection();
      removePSI(conn, psi, r);
      if (r.hasError())
        environment.logError(r.getErrorString(), null);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);
  }

  @Override
  public void removePSI(IPostgresConnection conn, String psi, IResult r) {
    String sql = "DELETE FROM tq_contents.psi WHERE proxyid = ? AND psi = ?";
    Object [] vals = new Object[2];
    vals[0] = this.getLocator();
    vals[1] = psi;
    conn.executeSQL(sql, r, vals);
  }

  @Override
  public List<String> listPSIValues() {
    List<String> result = new ArrayList<String>();
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
    
    try {
      conn = factory.getConnection();
      List<String>temp = listPSIValues(conn, r);
      if (temp != null)
        result = temp;
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);
    return result;
  }

  @Override
  public List<String> listPSIValues(IPostgresConnection conn, IResult r) {
    List<String> result = new ArrayList<String>();
    String sql = "SELECT psi FROM tq_contents.psi WHERE proxyid = ?";
    IResult rx = conn.executeSelect(sql, r, this.getLocator());
    ResultSet rs = (ResultSet)rx.getResultObject();
    
    if (rs != null) {
      try {
        while (rs.next()) {
          result.add(rs.getString("psi"));
        }
      } catch (SQLException e) {
        environment.logError(e.getMessage(), e);
        r.addErrorString(e.getMessage());
      }
      conn.closeResultSet(rs, r);

    }
    return result;
  }

  @Override
  public List<String> listTransitiveClosure() {
    List<String>result = (List<String>)data.get(ITQCoreOntology.TRANSITIVE_CLOSURE_PROPERTY_TYPE);
    return result;
  }

  @Override
  public void setTransitiveClosure(List<String> tc) {
    System.out.println("Proxy.setTransitiveClosure "+tc);
    data.put(ITQCoreOntology.TRANSITIVE_CLOSURE_PROPERTY_TYPE, tc);
  }

  @Override
  public void addTransitiveClosureLocator(String locator) {
    if (locator == null)
      return;

    List<String> result = (List<String>)data.get(ITQCoreOntology.TRANSITIVE_CLOSURE_PROPERTY_TYPE);
    // should never be null, but must look
    if (result == null)
      result = new ArrayList<String>();
    if (!result.contains(locator)) {
      result.add(locator);
      data.put(ITQCoreOntology.TRANSITIVE_CLOSURE_PROPERTY_TYPE, result);
    }
  }

  @Override
  public void addACLValue(String value, boolean isTransaction) {
    if (!isTransaction) {
      List<String> l = getMultivaluedProperty(ITQCoreOntology.RESTRICTION_PROPERTY_TYPE);
      if (!l.contains(value)) {
        l.add(value);
        data.put(ITQCoreOntology.RESTRICTION_PROPERTY_TYPE, l);
      }
    } else {
      IPostgresConnection conn = null;
      IResult r = new ResultPojo();
      
      try {
        conn = factory.getConnection();
        addACLValue(conn, value, r);
      } catch (SQLException e) {
        environment.logError(e.getMessage(), e);
      }
      conn.closeConnection(r);
    }
  }

  @Override
  public void addACLValue(IPostgresConnection conn, String value, IResult r) {
    String sql = 
        "INSERT INTO tq_contents.acls (proxyid, acl) VALUES (?, ?) ON CONFLICT DO NOTHING";
    Object [] vals = new Object[2];
    vals[0] = this.getLocator();
    vals[1] = value;
    conn.executeSQL(sql, r, vals);
    List<String> l = (List<String>)data.get(ITQCoreOntology.RESTRICTION_PROPERTY_TYPE);
    if (l == null)
      l = new ArrayList<String>();
    if (!l.contains(value))
      l.add(value);
    data.put(ITQCoreOntology.RESTRICTION_PROPERTY_TYPE, l);

  }

  @Override
  public void removeACLValue(String value) {
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
    
    try {
      conn = factory.getConnection();
      removeACLValue(conn, value, r);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);
  }

  @Override
  public void removeACLValue(IPostgresConnection conn, String value, IResult r) {
    String sql = "DELETE FROM tq_contents.acls WHERE proxyid = ? AND acl = ?";

    Object [] vals = new Object[2];
    vals[0] = this.getLocator();
    vals[1] = value;
    conn.executeSQL(sql, r, vals);
    
    List<String> l = (List<String>)data.get(ITQCoreOntology.RESTRICTION_PROPERTY_TYPE);
    if (l != null)
      l.remove(value);
  }

  @Override
  public List<String> listACLValues() {
    List<String> result = getMultivaluedProperty(ITQCoreOntology.RESTRICTION_PROPERTY_TYPE);
    return result;
  }

  @Override
  public boolean containsACL(String value) {
    List<String> creds = listACLValues();
    return creds.contains(value);
  } 

  /**
   * <p>Utility method for multivalued properties which may return a String</p>
   * <p>If this turns a string value into a list value, it installs that list in properties</p>
   * @param key
   * @return does not return <code>null</code>
   */
  private List<String> getMultivaluedProperty(String key) {
    List<String> result = null;
    Object op = data.get(key);
    
    if (op != null) {
      if (op instanceof String) {
        result = new ArrayList<String>();
        result.add((String)op);
      } else {
        result = (List<String>)op;
      } 
    } else {
      result = new ArrayList<String>();
      data.put(key, result);
    }
    return result;
  }
	
  private List<JSONObject> getMultivaluedJSONproperty(String key) {
    List<JSONObject> result = null;
    Object op = data.get(key);
    
    if (op != null) {
      if (op instanceof JSONObject) {
        result = new ArrayList<JSONObject>();
        result.add((JSONObject)op);
      } else {
        result = (List<JSONObject>)op;
      } 
    } else {
      result = new ArrayList<JSONObject>();
      data.put(key, result);
    }
    return result;
  }

  /**
   * Concatinate all the List<String> values indexed by <code>keys</code>
   * @param keys
   * @return
   */
  private List<String> concatinateStringLists(List<String>keys) {
    List<String>result = new ArrayList<String>();
    int len = keys.size();
    
    String key;
    for (int i = 0; i < len; i++) {
      key = keys.get(i);
      result.addAll(this.getMultivaluedProperty(key));
    }
    return result;
  }

  @Override
  public void addChildNode(String contextLocator, String childLocator, String transcludeLocator) {
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
      
    try {
      conn = factory.getConnection();
      //conn.beginTransaction(r);
      //conn.setConvRole(r);
      if (r.hasError()) {
        environment.logError("addChildNode ERROR: " + r.getErrorString(), null);
      }
      addChildNode(conn, contextLocator, childLocator, transcludeLocator);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    //conn.endTransaction(r);
    conn.closeConnection(r);
  }

  public void addChildNode(IPostgresConnection conn, String contextLocator,
                           String childLocator, String transcludeLocator) {
    String sql = "INSERT INTO tq_tree.conv (context, lox, parent_lox) VALUES (?, ?, ?)";

    Object [] vals = new Object[3];
    vals[0] = contextLocator;
    vals[1] = childLocator;
    vals[2] = this.getLocator();
    environment.logDebug("Proxy.addChildNode " + contextLocator + " " + childLocator+ " " + this.getLocator());

    IResult r = conn.beginTransaction();
    if (r.hasError()) {
        environment.logError("addChildNode-1 ERROR: " + r.getErrorString(), null);
      }
    conn.setConvRole(r);
    if (r.hasError()) {
        environment.logError("addChildNode-2 ERROR: " + r.getErrorString(), null);
      }

    conn.executeSQL(sql, r, vals);
    if (r.hasError()) {
        environment.logError("addChildNode-3 ERROR: " + r.getErrorString(), null);
      }

    conn.endTransaction(r);
    if (r.hasError()) {
        environment.logError("addChildNode-4 ERROR: " + r.getErrorString(), null);
      }
  }
  
  @Override
  public List<IProxy> listChildNodes(String contextLocator, ITicket credentials) {
    List<IProxy> result = new ArrayList<IProxy>();
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();

    try {
      conn = factory.getConnection();
      conn.setProxyRORole();
      result = listChildNodes(conn, contextLocator, credentials);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);

    return result;
  }
	
  @Override
  public List<IProxy> listChildNodes(IPostgresConnection conn, String contextLocator, ITicket credentials) {
    List<IProxy> result = new ArrayList<IProxy>();
    String lox_sql =
        "SELECT lox FROM tq_contents.proxy WHERE lox IN " +
  	"(SELECT lox FROM tq_tree.conv WHERE context = ? AND parent_lox = ?)";

    IResult r = conn.executeSelect(lox_sql, contextLocator, this.getLocator());
    if (r.hasError()) {
      System.out.println("listChildNodes ERROR: " + r.getErrorString());
      environment.logError("listChildNodes ERROR: " + r.getErrorString(), null);
    }
    ResultSet rs = (ResultSet)r.getResultObject();

    if (rs != null) {
      try {
        while (rs.next()) {
          IResult proxy = database.getNode(rs.getString(1), credentials);

          if (!proxy.hasError())
            result.add((IProxy)proxy.getResultObject());
          else
            environment.logError("listChildNodes (2) ERROR: " + proxy.getErrorString(), null);
        }
      } catch (SQLException e) {
        environment.logError(e.getMessage(), e);
      }
    }
    return result;
  }
	
  @Override
  public List<IProxy> listParentNodes(String contextLocator, ITicket credentials) {
    List<IProxy> result = new ArrayList<IProxy>();
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();

    try {
      conn = factory.getConnection();
      conn.setProxyRORole();
      result = listParentNodes(conn, contextLocator, credentials);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);

    return result;
  }
	
  @Override
  public List<IProxy> listParentNodes(IPostgresConnection conn, String contextLocator, ITicket credentials) {
    List<IProxy> result = new ArrayList<IProxy>();
    String lox_sql =
        "SELECT lox FROM tq_contents.proxy WHERE lox IN " +
  	"(SELECT parent_lox FROM tq_tree.conv WHERE context = ? AND lox = ?)";

    IResult r = conn.executeSelect(lox_sql, contextLocator, this.getLocator());
    ResultSet rs = (ResultSet)r.getResultObject();

    if (rs != null) {
      try {
        while (rs.next()) {
          IResult proxy = database.getNode(rs.getString(1), credentials);

          if (!proxy.hasError())
            result.add((IProxy)proxy.getResultObject());
        }
      } catch (SQLException e) {
        environment.logError(e.getMessage(), e);
      }
    }
    return result;
  }

  @Override
  public List<IProxy> listAncestorNodes(String contextLocator, ITicket credentials) {
    List<IProxy> result = new ArrayList<IProxy>();
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();

    try {
      conn = factory.getConnection();
      conn.setProxyRORole();
      result = listAncestorNodes(conn, contextLocator, credentials);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.closeConnection(r);

    return result;
  }
	
  @Override
  public List<IProxy> listAncestorNodes(IPostgresConnection conn, String contextLocator, ITicket credentials) {
    List<IProxy> result = new ArrayList<IProxy>();
    String lox_sql = "SELECT * from tq_tree.getAncestors(?, ?)";

    Object [] vals = new Object[2];
    vals[0] = contextLocator;
    vals[1] = this.getLocator();
    IResult r = conn.executeSelect(lox_sql, vals);
    ResultSet rs = (ResultSet)r.getResultObject();

    if (rs != null) {
      try {
        while (rs.next()) {
          String lox = rs.getString(1);

          if (!lox.equals(this.getLocator())) {
            IResult proxy = database.getNode(lox, credentials);

            if (!proxy.hasError())
              result.add((IProxy)proxy.getResultObject());
          }
        }
      } catch (SQLException e) {
        environment.logError(e.getMessage(), e);
      }
    } else {
      System.out.println("Result set is null");
    }
    return result;
  }
  
  @Override
  public void addParentNode(String contextLocator, String parentLocator) {
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();

    try {
      conn = factory.getConnection();
      conn.beginTransaction(r);
      conn.setConvRole(r);
      if (r.hasError()) {
          environment.logError("addParentNode-1 ERROR: " + r.getErrorString(), null);
        }

      addParentNode(conn, contextLocator, parentLocator);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
    }
    conn.endTransaction(r);
    conn.closeConnection(r);
  }

  public void addParentNode(IPostgresConnection conn, String contextLocator, String parentLocator) {
    String sql = "INSERT INTO tq_tree.conv (context, lox, parent_lox) VALUES (?, ?, ?)";

    Object [] vals = new Object[3];
    vals[0] = contextLocator;
    vals[1] = this.getLocator();
    vals[2] = parentLocator;
    IResult r = conn.executeSQL(sql, vals);
    environment.logDebug("Proxy.addParentNode " + contextLocator + " " + parentLocator + " " + this.getLocator());

    if (r.hasError()) {
        environment.logError("addParentNode-2 ERROR: " + r.getErrorString(), null);
      }
  }

  @Override
  public void setPSIList(IPostgresConnection conn, List<String> psis) {
    if (psis != null && !psis.isEmpty()) {
      IResult r = new ResultPojo();
      Iterator<String>itr = psis.iterator();
      
      while (itr.hasNext())
        this.addPSI(conn, itr.next(), r);
    }
  }
	
  @Override
  public void setParents(JSONObject parents) {
  	data.put(IParentChildContainer.PARENTS_KEY, parents);
  }

  @Override
  public JSONObject getParents() {
  	return (JSONObject)data.get(IParentChildContainer.PARENTS_KEY);
  }

  @Override
  public void setChildren(JSONObject children) {
	  	data.put(IParentChildContainer.CHILDREN_KEY, children);
  }

  @Override
  public JSONObject getChildren() {
	  	return (JSONObject)data.get(IParentChildContainer.CHILDREN_KEY);
  }
  @Override
  public void setScopeList(IPostgresConnection conn, List<String> scopes) {
    if (scopes != null && !scopes.isEmpty()) {
      Iterator<String>itr = scopes.iterator();
      IResult r = new ResultPojo();
      Object [] vals = new Object[3];
      String sql = 
          "INSERT INTO tq_contents.properties (proxyid, property_key, property_val) " +
          "VALUES (?, ?, ?) ON CONFLICT DO NOTHING";
      
      vals[0] = this.getLocator();
      vals[1] = ITQCoreOntology.SCOPE_LIST_PROPERTY_TYPE;
      
      while (itr.hasNext()) {
        vals[2] = itr.next();
        conn.executeSQL(sql, r, vals);
      }
    }
  }

  @Override
  public void addMergeReason(IPostgresConnection conn, String reason) {
    // TODO Auto-generated method stub
  }

	@Override
	public void stashProperty(String key, Object value) {
		data.put(key, value);
	}
	
	@Override
	public Object getStashedProperty(String key) {
		return data.get(key);
	}


}
