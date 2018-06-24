/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

//import org.topicquests.ks.ProxyEventHandler;
import org.topicquests.ks.SystemEnvironment;

import org.topicquests.ks.api.IErrorMessages;
//import org.topicquests.ks.api.IExtendedCoreOntology;
import org.topicquests.ks.api.IProxyEvent;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.kafka.KafkaProducer;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.tm.api.IConversationNode;
import org.topicquests.ks.tm.api.IDataProvider;
import org.topicquests.ks.tm.api.IMergeResultsListener;
import org.topicquests.ks.tm.api.IParentChildContainer;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.ITreeNode;
import org.topicquests.ks.tm.api.ITuple;
import org.topicquests.ks.tm.merge.VirtualizerHandler;

import org.topicquests.support.RootEnvironment;
import org.topicquests.support.util.LRUCache;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import org.topicquests.es.api.IClient;
import org.topicquests.es.util.TextQueryUtil;
import org.topicquests.es.ProviderEnvironment;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.pg.api.IPostgresConnection;

public class DataProvider extends RootEnvironment implements IDataProvider {
  private SystemEnvironment environment;
  private LRUCache nodeCache;
  private VirtualizerHandler mergePerformer;

  private CredentialUtility credentialUtility;
  private PostgresConnectionFactory database = null;
  private ProviderEnvironment esProvider = null;
  private TextQueryUtil textQueryUtil;
  //private ProxyEventHandler eventHandler;
  private KafkaProducer kafkaProducer;
  public static final String _INDEX = "topics";

  private final String PROXY_TABLE  = "tq_contents.proxy";
  private final String PROPS_TABLE  = "tq_contents.properties";
  private final String TCL_TABLE    = "tq_contents.transitive_closure";  
  
  public DataProvider(SystemEnvironment env) throws Exception {
    this(env, null);
  }
  
  /**
   * 
   * @param env
   * @param schemaName
   * @throws Exception
   */
  public DataProvider(SystemEnvironment env, String schemaName) throws Exception {
    super("data-props.xml", "logger.properties");

    environment = env;
    kafkaProducer = environment.getkafkaProducer();
    if (schemaName == null) {
      schemaName = getStringProperty("DatabaseSchema");
    }
    
    database = new PostgresConnectionFactory(getStringProperty("DatabaseName"),
                                             schemaName);
    esProvider = env.getESProvider();
    textQueryUtil = esProvider.getTextQueryUtil();

    int clientCacheSize = Integer.parseInt(getStringProperty("ClientCacheSize"));
    nodeCache = new LRUCache(clientCacheSize);
    credentialUtility = new CredentialUtility(env);
  }

  public void setVirtualizer(VirtualizerHandler h) {
    this.mergePerformer = h;
  }

  public String getUser() {
    return database.getUser();
  }

  @Override
  public PostgresConnectionFactory getDBProvider() {
    return database;
  }

  @Override
  public ProviderEnvironment getESProvider() {
    return esProvider;
  }

  @Override
  public void removeFromCache(String nodeLocator) {
    nodeCache.remove(nodeLocator);
  }

  @Override
  public String getUUID() {
    return UUID.randomUUID().toString();
  }

  @Override
  public String getUUID_Pre(String prefix) {
    return prefix+getUUID();
  }

  @Override
  public String getUUID_Post(String suffix) {
    return getUUID()+suffix;
  }

  @Override
  public IResult loadTree(String rootNodeLocator, int maxDepth, int start,
                          int count, ITicket credentials) {
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;
    
    try {
      conn = database.getConnection();
      result = loadTree(conn, rootNodeLocator, maxDepth, start, count, credentials);
    } catch (SQLException e) {
      result.addErrorString(e.getMessage());
    }
    conn.closeConnection(result);

    return result;
  }

  @Override
  public IResult loadTree(IPostgresConnection conn, String rootNodeLocator, int maxDepth,
                          int start, int count, ITicket credentials) {
    IResult result = new ResultPojo();
    List<String> loopStopper = new ArrayList<String>();
    
    // Get the root node
    IResult r = this.getNode(conn, rootNodeLocator, credentials);
    if (r.hasError()) {
      result.addErrorString(r.getErrorString());
    }
    
    ITreeNode root = new TreeNode(rootNodeLocator);
    IProxy n = (IProxy)r.getResultObject();
    environment.logDebug("DataProvider.loadTree "+n.getData().toJSONString());
    
    String label = n.getLabel("en");
    root.setNodeLabel(label);
    result.setResultObject(root);
    
    // now populate its child nodes
    recursiveWalkDownTree(conn, result, root, maxDepth, maxDepth, start, count, credentials, loopStopper);
    environment.logDebug("DataProvider.loadTree+ " + rootNodeLocator + " " +
                         root.getSubclassCount() + " " + root.getInstanceCount());
    return result;
  }
  
  @Override
  public IResult fetchConversation(String rootLocator, String language, ITicket credentials) {
	    IResult result = new ResultPojo();
	    IPostgresConnection conn = null;
	    IResult r = null;
	    try {
	      conn = database.getConnection();
	      r = conn.beginTransaction();
	      conn.setConvRole(r);
	      if (r.hasError())
	    	  environment.logError("DataProvider.fetchConversation: "+r.getErrorString(), null);

	      result = fetchConversation(conn, rootLocator, language, credentials);
	    } catch (SQLException e) {
	      result.addErrorString(e.getMessage());
	    }
	    conn.endTransaction(r);
	    conn.closeConnection(r);

	    return result;
  }

  @Override
  public IResult fetchConversation(IPostgresConnection conn, String rootLocator, String language, ITicket credentials) {
	    IResult result = new ResultPojo();
	    String lang = language;
	    if (lang == null)
	    	lang = "en";
		environment.logDebug("DataProvider.fetchConversation "+rootLocator);
	    // Get the root node
	    IResult r = this.getNode(rootLocator, credentials);
	    if (r.hasError()) {
	      result.addErrorString(r.getErrorString());
	    }
		environment.logDebug("DataProvider.fetchConversation-1 "+r.getErrorString()+" "+r.getResultObject());
	    IProxy p = (IProxy) r.getResultObject();
	    if (p != null) {
	    	IConversationNode root = new ConversationNode(p, lang);
	    	populateConversation(conn, rootLocator, root, language, result, credentials);
                result.setResultObject(root);
	    }  
	    return result;
  }
  
  /**
   * Recursive walk down tree
   * @param conn
   * @param contextLocator
   * @param root
   * @param language
   * @param result
   * @param credentials
   */
  void populateConversation(IPostgresConnection conn, String contextLocator, IConversationNode root, String language, IResult result, ITicket credentials) {
	  environment.logDebug("DataProvider.populateConversation "+root.getLocator());
	  String lox = root.getLocator();
	  String sql = "SELECT lox FROM tq_tree.conv WHERE context = ? AND parent_lox = ?";
	  Object [] vals = new Object[2];
	  vals[0] = contextLocator;
	  vals[1] = lox;
	  IResult r = conn.executeSelect(sql, vals);
	  ResultSet rs = (ResultSet)r.getResultObject();
	  environment.logDebug("DataProvider.populateConversation-1 "+r.getErrorString()+" "+rs);
	  if (r.hasError())
		  result.addErrorString(r.getErrorString());
	  if (rs != null) {
		  try {
			  String kidLox;
			  IProxy kid;
			  IConversationNode child;
			  while (rs.next()) {
				  kidLox = rs.getString(1);
                                  environment.logDebug("DataProvider.populateConversation-2:  " + kidLox);
				  r = this.getNode(kidLox, credentials);
				  if (r.hasError())
					  result.addErrorString(r.getErrorString());
				  kid = (IProxy)r.getResultObject();
				  if (kid != null) {
					  child = new ConversationNode(kid, language);
					  child.setParentLocator(lox);
					  root.addChild(child.getData());
					  //recurse
					  populateConversation(conn, contextLocator, child, language, result, credentials);
				  }
			  }
			  
		  } catch (Exception e) {
			  environment.logError(e.getMessage(), e);
			  result.addErrorString(e.getMessage());
		  }
	  }
  }


  public IResult getNodeInstanceRelation(String locator) {
	    IResult result = new ResultPojo();
	    result.addErrorString("DataProvider.getNodeInstanceRelation not implemented");
	    //TODO
	    return result;
  }

  public IResult getNodeInstanceRelation(IPostgresConnection conn, String locator) {
	    IResult result = new ResultPojo();
	    result.addErrorString("DataProvider.getNodeInstanceRelation not implemented");
	    //TODO
	    return result;
  }
  
  public IResult listNodeSubclassRelations(String locator) {
    IResult result = new ResultPojo();
    //TODO
    return result;
  }

  public IResult listNodeSubclassRelations(IPostgresConnection conn, String locator)  {
    IResult result = new ResultPojo();
    //TODO
    return result;
  }
  
  @Override
  public IResult getNode(String locator, ITicket credentials) {
    Object nx = nodeCache.get(locator);
    IResult result = null;

    if (nx != null) {
      if (((IProxy)nx).getIsLive()) {
        result = new ResultPojo();
        result.setResultObject(nx);
      }
    } else {
      result = this.getNodeAsJSONObject(locator, _INDEX);
      JSONObject jo = (JSONObject)result.getResultObject();

      result.setResultObject(null); // default in case bad credentials
      if (jo != null) {
        this.jsonToProxy(locator, jo, credentials, result);
      }
    }
    return result;
  }

  @Override
  public IResult getNode(IPostgresConnection conn, String locator, ITicket credentials) {
    Object nx = nodeCache.get(locator);
    IResult result = null;
    
    if (nx != null) {
      if (((IProxy)nx).getIsLive()) {
        result = new ResultPojo();
        result.setResultObject(nx);
      }
    } else {
      result = this.getNodeAsJSONObject(conn, locator, _INDEX);
      JSONObject jo = (JSONObject)result.getResultObject();

      result.setResultObject(null); // default in case bad credentials
      if (jo != null) {
        this.jsonToProxy(locator, jo, credentials, result);
      }
    }
    return result;
  }

  /**
   * Returns an {@link IProxy} or <code>null</code> in <code>result</code>
   * @param locator
   * @param jo
   * @param credentials
   * @param result
   */
  @Override
  public void jsonToProxy(String locator, JSONObject jo, ITicket credentials, IResult result) {
    IProxy n = new Proxy(jo);
    int what = this.checkCredentials(n, credentials);
    
    if (what == 1) {
      result.setResultObject(n);
      if (!n.getIsPrivate())
        nodeCache.add(locator, n);
    } else if (what == 0){
      result.addErrorString(IErrorMessages.INSUFFICIENT_CREDENTIALS);
    } else {
      result.addErrorString(IErrorMessages.NODE_REMOVED);
    }

  }

  @Override
  public IResult getNodeAsJSONObject(String locator, String topic) {
    IPostgresConnection conn = null;
    IResult r = null;

    try {
      conn = database.getConnection();
      conn.setProxyRole();
      r = getNodeAsJSONObject(conn, locator, topic);
    } catch (SQLException e) {
      r.addErrorString(e.getMessage());
      System.out.println("DataProvider.getNodeAsJSON-1 "+r.getErrorString()+" "+r.getResultObject());
      r.setResultObject(null);
      return r;
    }
    conn.closeConnection(r);
    return r;
  }

  @Override
  public IResult getNodeAsJSONObject(IPostgresConnection conn, String locator, String topic) {
    // Construct the SQL query to retrieve the node.
    String metadata_query =
        "SELECT row_to_json(proxy) FROM tq_contents.proxy WHERE lox = ?";
	    
    IResult r = new ResultPojo();
    JSONObject jo = null;
    Object o1;
    
    r = conn.setProxyRole();
    conn.executeSelect(metadata_query, r, locator);
    o1 = r.getResultObject();

    if (r.hasError()) {
      r.setResultObject(null);
      return r;
    }

    if (o1 != null) {
      ResultSet rs = (ResultSet)o1;

      try {
        if (rs.next()) {
          jo = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(rs.getString(1));
          r.setResultObject(jo);
        } else {
          r.setResultObject(null);
          environment.logError("DataProvider.getNodeAsJSON-2bb " + r.getErrorString() +
                               " - result is null - locator: " + locator, null);
        }
      } catch (Exception e) {
        r.addErrorString(e.getMessage());
        r.setResultObject(null);
        environment.logError("DataProvider.getNodeAsJSON-3 " + r.getErrorString() + " " + locator +
                             " " + r.getResultObject(), null);
        return r;
      }

      conn.closeResultSet(rs, r);
      if (r.hasError() || r.getResultObject() == null) {
        r.setResultObject(null);
        environment.logError("DataProvider.getNodeAsJSON-4 " + r.getErrorString() + " " + locator +
                             " " + r.getResultObject(), null);
        return r;
      }
    }

    Boolean o = (Boolean)jo.get(ITQCoreOntology.IS_LIVE);
    if (o.booleanValue()) {
      finishNodeFetch(conn, locator, jo, r);
      r.setResultObject(jo);
    }
    return r; 
  }
  
  @Override
  public void finishNodeFetch(IPostgresConnection conn, String locator, JSONObject jo, IResult r) {
    environment.logDebug("DataProvider.finishNodeFetch- "+locator+" "+jo.toJSONString());
    String properties_query =
        "SELECT row_to_json(t) FROM (" +
        "SELECT property_key, property_val FROM tq_contents.properties " +
        "WHERE proxyid = ? AND property_key IN ('" + ITQCoreOntology.CREATED_DATE_PROPERTY +
        "', '" + ITQCoreOntology.LAST_EDIT_DATE_PROPERTY + "')) t";
    String acl_query =
        "SELECT property_val FROM tq_contents.properties WHERE proxyid = ? " +
        "AND property_key = '" + ITQCoreOntology.RESTRICTION_PROPERTY_TYPE + "'";
    String tcl_query =
        "SELECT tc_lox FROM tq_contents.transitive_closure WHERE proxyid = ?";
	  	
    String genprops_query = 
        "SELECT property_val FROM tq_contents.properties WHERE proxyid = ? AND property_key = ?";
    Object o2, o3, o4, o5, o6, o7, o8, o9, o10;
    
    conn.executeSelect(properties_query, r, locator);
    o2 = r.getResultObject();
    conn.executeSelect(acl_query, r, locator);
    o3 = r.getResultObject();
    conn.executeSelect(tcl_query, r, locator);
    o4 = r.getResultObject();
    // environment.logDebug("DataProvider.finishNodeFetch-a ");
      
    Object [] pvals = new Object[2];
    pvals[0] = locator;
    pvals[1] = ITQCoreOntology.TUPLE_SUBJECT_PROPERTY;
    conn.executeSelect(genprops_query, r, pvals);
    // environment.logDebug("DataProvider.finishNodeFetch-b ");
   
    // Tuple subject locator
    o5 = r.getResultObject();
    pvals[1] = ITQCoreOntology.TUPLE_OBJECT_PROPERTY;
    conn.executeSelect(genprops_query, r, pvals);
    // environment.logDebug("DataProvider.finishNodeFetch-c ");

    // Tuple object value
    o6 = r.getResultObject();
    pvals[1] = ITQCoreOntology.TUPLE_SUBJECT_TYPE_PROPERTY;
    conn.executeSelect(genprops_query, r, pvals);
    // environment.logDebug("DataProvider.finishNodeFetch-d ");

    // Tuple subject type
    o7 = r.getResultObject();
    pvals[1] = ITQCoreOntology.TUPLE_OBJECT_TYPE_PROPERTY;
    conn.executeSelect(genprops_query, r, pvals);
    // environment.logDebug("DataProvider.finishNodeFetch-e ");

    // Tuple object type
    o8 = r.getResultObject();
    pvals[1] = ITQCoreOntology.TUPLE_SUBJECT_ROLE_PROPERTY;
    conn.executeSelect(genprops_query, r, pvals);
    // environment.logDebug("DataProvider.finishNodeFetch-f ");

    // Tuple subject role
    o9 = r.getResultObject();
    pvals[1] = ITQCoreOntology.TUPLE_OBJECT_ROLE_PROPERTY;
    conn.executeSelect(genprops_query, r, pvals);
    // environment.logDebug("DataProvider.finishNodeFetch-g ");

    // Tuple object role
    o10 = r.getResultObject();
        
    if (o2 != null) {
      ResultSet rs = (ResultSet)o2;

      try {
        while (rs.next()) {
          JSONObject jo2 = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(rs.getString(1));
          JSONObject dateobj = new JSONObject();
          jo.put(ITQCoreOntology.CREATED_DATE_PROPERTY, jo2.get("property_val"));
          jo.put(ITQCoreOntology.LAST_EDIT_DATE_PROPERTY, jo2.get("property_val"));
          // environment.logDebug("DataProvider.finishNodeFetch-1 "+jo+" | "+dateobj);
        }
      } catch (Exception e) {
        r.addErrorString(e.getMessage());
        r.setResultObject(null);
        environment.logError("DataProvider.finishNodeFetch-5 "+r.getErrorString()+" "+r.getResultObject(), null);
        return;
      }
      // environment.logDebug("DataProvider.finishNodeFetch-h ");

      conn.closeResultSet(rs, r);
      if (r.hasError()) {
        r.setResultObject(null);
        environment.logError("DataProvider.finishNodeFetch-6 "+r.getErrorString()+" "+r.getResultObject(), null);
        return;
      }
    }
    // environment.logDebug("DataProvider.finishNodeFetch-i ");
	      
    String loc;
    if (o3 != null) {
      ResultSet rs = (ResultSet)o3;
      List<String>acls = new ArrayList<String>();
      try {
        while (rs.next()) {
          loc = rs.getString("property_val");
          acls.add(loc);
        } 
        if (!acls.isEmpty())
          jo.put(ITQCoreOntology.RESTRICTION_PROPERTY_TYPE, acls);
      } catch (Exception e) {
        r.addErrorString(e.getMessage());
        r.setResultObject(null);
        environment.logError("DataProvider.finishNodeFetch-7 "+r.getErrorString()+" "+r.getResultObject(), null);
        return;
      }
      // environment.logDebug("DataProvider.finishNodeFetch-j ");

      conn.closeResultSet(rs, r);
      if (r.hasError() || r.getResultObject() == null) {
        r.setResultObject(null);
        environment.logError("DataProvider.finishNodeFetch-8 "+r.getErrorString()+" "+r.getResultObject(), null);
        return;
      }
    }
    // environment.logDebug("DataProvider.finishNodeFetch-k ");

    if (o4 != null) {
      ResultSet rs = (ResultSet)o4;
      List<String>tcls = new ArrayList<String>();
      try {
        while (rs.next()) {
          loc = rs.getString("tc_lox");
          tcls.add(loc);
        } 
        if (!tcls.isEmpty())
          jo.put(ITQCoreOntology.TRANSITIVE_CLOSURE_PROPERTY_TYPE, tcls);
      } catch (Exception e) {
        r.addErrorString(e.getMessage());
        r.setResultObject(null);
        environment.logError("DataProvider.finishNodeFetch-9 "+r.getErrorString()+" "+r.getResultObject(), null);
        return;
      }
      // environment.logDebug("DataProvider.finishNodeFetch-l ");

      conn.closeResultSet(rs, r);
      if (r.hasError() || r.getResultObject() == null) {
        r.setResultObject(null);
        environment.logError("DataProvider.finishNodeFetch-10 "+r.getErrorString()+" "+r.getResultObject(), null);
        return;
      }
    }
    // environment.logDebug("DataProvider.finishNodeFetch-m ");

    // tuples
    if (o5 != null) {
      ResultSet rs = (ResultSet)o5;

      try {
        if (rs.next()) {
          jo.put(ITQCoreOntology.TUPLE_SUBJECT_PROPERTY, rs.getString("property_val"));
        } 
      } catch (Exception e) {
      }
      conn.closeResultSet(rs, r);
    }
    // environment.logDebug("DataProvider.finishNodeFetch-n ");

    if (o6 != null) {
      ResultSet rs = (ResultSet)o6;

      try {
        if (rs.next()) {
          jo.put(ITQCoreOntology.TUPLE_OBJECT_PROPERTY, rs.getString("property_val"));
        } 
      } catch (Exception e) {
      }
      conn.closeResultSet(rs, r);
    }
    // environment.logDebug("DataProvider.finishNodeFetch-o ");

    if (o7 != null) {
      ResultSet rs = (ResultSet)o7;

      try {
        if (rs.next()) {
          jo.put(ITQCoreOntology.TUPLE_SUBJECT_TYPE_PROPERTY, rs.getString("property_val"));
        } 
      } catch (Exception e) {
      }
      conn.closeResultSet(rs, r);
    }	      
    // environment.logDebug("DataProvider.finishNodeFetch-p ");

    if (o8 != null) {
      ResultSet rs = (ResultSet)o8;

      try {
        if (rs.next()) {
          jo.put(ITQCoreOntology.TUPLE_OBJECT_TYPE_PROPERTY, rs.getString("property_val"));
        } 
      } catch (Exception e) {
      }
      conn.closeResultSet(rs, r);
    }	      
    // environment.logDebug("DataProvider.finishNodeFetch-q ");

    if (o9 != null) {
      ResultSet rs = (ResultSet)o9;

      try {
        if (rs.next()) {
          jo.put(ITQCoreOntology.TUPLE_SUBJECT_ROLE_PROPERTY, rs.getString("property_val"));
        } 
      } catch (Exception e) {
      }
      conn.closeResultSet(rs, r);
    }
    // environment.logDebug("DataProvider.finishNodeFetch-r ");


    if (o10 != null) {
      ResultSet rs = (ResultSet)o10;

      try {
        if (rs.next()) {
          jo.put(ITQCoreOntology.TUPLE_OBJECT_ROLE_PROPERTY, rs.getString("property_val"));
        } 
      } catch (Exception e) {
      }
      conn.closeResultSet(rs, r);
    }
    r.setResultObject(jo);
    // environment.logDebug("DataProvider.finishNodeFetch-s ");

    // FETCH THE LABELS
    JSONObject labels = fetchLabels(locator);
    if (labels != null)
      jo.put(ITQCoreOntology.LABEL_PROPERTY, labels);
    // environment.logDebug("DataProvider.finishNodeFetch+ "+jo);
  }

  @Override
  public JSONObject fetchLabels(String locator) {
    JSONObject result = null;
    IResult r = esProvider.getProvider().get(locator, _INDEX);
    JSONObject jo = (JSONObject)r.getResultObject();

    if (jo != null) {
      environment.logDebug("DataProvider.fetchLabels "+locator+" "+jo.toJSONString());
      result = (JSONObject)jo.get(ITQCoreOntology.LABEL_PROPERTY);
    } else {
      result = new JSONObject();
    }

    return result;
  }

  @Override
  public JSONObject fetchDetails(String locator) {
    JSONObject result = null;
    IResult r = esProvider.getProvider().get(locator, _INDEX);
    JSONObject jo = (JSONObject)r.getResultObject();

    if (jo != null) {
      // environment.logDebug("DataProvider.fetchDetails "+locator+" "+jo.toJSONString());
      result = (JSONObject)jo.get(ITQCoreOntology.DETAILS_PROPERTY);
    } else {
      result = new JSONObject();
    }
    return result;
  }

  @Override
  public JSONObject addLabel(String locator, String newLabel, String language) {
	    this.removeFromCache(locator);
	    // get the labels
  	JSONObject jo = this.fetchLabels(locator);
  	environment.logDebug("DataProvider.addLabel "+locator+" "+jo);
  	if (jo == null)
  		jo = new JSONObject();
  	//add new label if unique
  	List<String> x = (List<String>)jo.get(language);
  	if (x == null)
  		x = new ArrayList<String>();
  	if (!x.contains(newLabel))
  		x.add(newLabel);
  	jo.put(language, x);
  	//get the text object from the index
    IResult r = esProvider.getProvider().get(locator, _INDEX);
    JSONObject jx = (JSONObject)r.getResultObject();
    if (jx != null) {
    	//overwrite
        jx.put(ITQCoreOntology.LABEL_PROPERTY, jo);
    	if (jx.get(ITQCoreOntology.LOCATOR_PROPERTY) == null)
    		throw new RuntimeException("B "+jx.toJSONString());

        // environment.logDebug("DataProvider.setDetails-2 "+jo);
        r = esProvider.getProvider().updateFullNode(locator, _INDEX, jx, false);
      } else {
    	  jx = new JSONObject();
    	  jx.put(ITQCoreOntology.LABEL_PROPERTY, jo);
    	  jx.put(ITQCoreOntology.LOCATOR_PROPERTY, locator);
    		if (jx.get(ITQCoreOntology.LOCATOR_PROPERTY) == null)
    			throw new RuntimeException("C "+jx.toJSONString());

        r = esProvider.getProvider().put(locator, _INDEX, jx);
      }
    return jo;
  }
/*
  @Override
  public JSONObject removeLabel(String locator, String oldLabel, String language) {
  	// TODO Auto-generated method stub
  	return null;
  }
*/
  @Override
  public JSONObject addDetails(String locator, String newDetails, String language) {
	    // get the details
	JSONObject jo = this.fetchDetails(locator);
	if (jo == null)
		jo = new JSONObject();
	//add new details if unique
	List<String> x = (List<String>)jo.get(language);
	if (x == null)
		x = new ArrayList<String>();
	if (!x.contains(newDetails))
		x.add(newDetails);
	jo.put(language, x);
	//get the text object from the index
  IResult r = esProvider.getProvider().get(locator, _INDEX);
  JSONObject jx = (JSONObject)r.getResultObject();
  if (jx != null) {
  	//overwrite
      jx.put(ITQCoreOntology.DETAILS_PROPERTY, jo);
  	if (jx.get(ITQCoreOntology.LOCATOR_PROPERTY) == null)
		throw new RuntimeException("E "+jx.toJSONString());
      // environment.logDebug("DataProvider.setDetails-2 "+jo);
      r = esProvider.getProvider().updateFullNode(locator, _INDEX, jx, false);
    } else {
  	  jx = new JSONObject();
  	  jx.put(ITQCoreOntology.DETAILS_PROPERTY, jo);
  	  jx.put(ITQCoreOntology.LOCATOR_PROPERTY, locator);
    	if (jx.get(ITQCoreOntology.LOCATOR_PROPERTY) == null)
    		throw new RuntimeException("F "+jx.toJSONString());
      r = esProvider.getProvider().put(locator, _INDEX, jx);
    }
  	return jo;
  }

  /*
  @Override
  public JSONObject removeDetails(String locator, String oldDetails, String language) {
  	// TODO Auto-generated method stub
  	return null;
  }
 */
  
  @Override
  public void setLabels(JSONObject labels) {
    // environment.logDebug("DataProvider.setLabels "+labels);
    String locator = labels.getAsString(ITQCoreOntology.LOCATOR_PROPERTY);
    this.removeFromCache(locator);
    IResult r = esProvider.getProvider().get(locator, _INDEX);
    JSONObject jo = (JSONObject)r.getResultObject();
    environment.logDebug("DataProvider.setLabels-1 "+jo);
    
    if (jo != null) {
      // overwrite the labels
      jo.put("labels", labels.get("label"));
      environment.logDebug("DataProvider.setLabels-3 "+jo.toJSONString());
      r = esProvider.getProvider().updateFullNode(locator, _INDEX, jo, false);
    } else
      r = esProvider.getProvider().put(locator, _INDEX, labels);
  }

  @Override
  public void setDetails(JSONObject details) {
    // environment.logDebug("DataProvider.setDetails "+details.toJSONString());
    String locator = details.getAsString(ITQCoreOntology.LOCATOR_PROPERTY);
    this.removeFromCache(locator);
    IResult r = esProvider.getProvider().get(locator, _INDEX);
    JSONObject jo = (JSONObject)r.getResultObject();
    // environment.logDebug("DataProvider.setDetails-1 "+jo);
    
    if (jo != null) {
      jo.put("details", details.get("details"));
      // environment.logDebug("DataProvider.setDetails-2 "+jo);
      r = esProvider.getProvider().updateFullNode(locator, _INDEX, jo, false);
    } else
      r = esProvider.getProvider().put(locator, _INDEX, details);
  }

  @Override
  public IResult getNodeByURL(String url, ITicket credentials) {
    IResult result = new ResultPojo();
    String sql = "SELECT lox FROM tq_contents.proxy WHERE url = ?";
    IPostgresConnection conn = null;
    Object o1;
    
    try {
      conn = database.getConnection();
      result = conn.setProxyRole();
      conn.executeSelect(sql, result, url);
      o1 = result.getResultObject();
    } catch (SQLException e) {
      result.addErrorString(e.getMessage());
      System.out.println("DataProvider.getNodeAsJSON-1 "+result.getErrorString()+" "+result.getResultObject());
      result.setResultObject(null);
      return result;
    }
    
    String loc = null;
    if (o1 != null) {
      ResultSet rs = (ResultSet)o1;
      try {
        if (rs.next()) {
          loc = rs.getString("lox");
        } 
      } catch (Exception e) {
        result.addErrorString(e.getMessage());
        result.setResultObject(null);
        environment.logError("DataProvider.getNodeAsJSON-7 " + result.getErrorString() +
                             " " + result.getResultObject(), null);
        return result;
      }

      conn.closeResultSet(rs, result);
      if (result.hasError() || result.getResultObject() == null) {
        environment.logError("DataProvider.getNodeAsJSON-8 " + result.getErrorString() + " " +
                             result.getResultObject(), null);
        return result;
      }
    }
    if (loc != null) {
      // found it
      result = getNodeAsJSONObject(conn, loc, _INDEX);
      JSONObject jo = (JSONObject)result.getResultObject();
      
      if (jo != null) {
        this.jsonToProxy(loc, jo, credentials, result);
      }
    }
    conn.closeConnection(result);
    return result;
  }

  @Override
  public IResult putTuple(ITuple tuple, boolean checkVersion) {
    return putNode(tuple);
  }

  @Override
  public IResult getTuple(String tupleLocator, ITicket credentials) {
    return getNode(tupleLocator, credentials);
  }

  @Override
  public IResult listRelationsByRelationType(String locator, String relationType,
                                             int start, int count, ITicket credentials) {
    IResult result = null;
    IPostgresConnection conn = null;
    IResult r = new ResultPojo();
    
    try {
      conn = database.getConnection();
      result = listRelationsByRelationType(conn, locator, relationType, start, count, credentials);
    } catch (SQLException e) {
      SystemEnvironment.getInstance().logError(e.getMessage(), e);
    }
    conn.closeConnection(r);
    return result;
  }

  @Override
  public IResult listRelationsByRelationType(IPostgresConnection conn, String locator,
                                             String relationType, int start,
                                             int count, ITicket credentials) {
    IResult result = new ResultPojo();
    List<ITuple> tups = new ArrayList<ITuple>();
    result.setResultObject(tups);
    String sql = "SELECT lox, row_to_json(proxy) " +
        "FROM tq_contents.proxy " +
        "WHERE node_type = ? AND lox IN " +
        "(SELECT proxyid AS lox " +
        "FROM tq_contents.properties " +
        "WHERE property_key IN ('" + ITQCoreOntology.TUPLE_SUBJECT_PROPERTY +
        "','" +  ITQCoreOntology.TUPLE_OBJECT_PROPERTY + "') AND property_val = ?) " +
        "ORDER BY lox OFFSET ?";
    int ct = 3;
    if (count > 0)
      ct++;
    
    Object [] vals = new Object[ct];
    vals[0] = relationType;
    vals[1] = locator;
    vals[2] = start;
    if (ct > 0) {
      vals[3] = count;
      sql += " LIMIT ?";
    }
    
    IResult r = conn.executeSelect(sql, vals);
    ResultSet rs = (ResultSet)r.getResultObject();
    environment.logDebug("DataProvider.listRelationsByRelationType " + sql + " \n " +
                         r.getErrorString() + " " + rs);
    
    r = nodesFromJSONResultSet(conn, rs, credentials, 2);
    if (r.hasError())
      result.addErrorString(r.getErrorString());
    result.setResultObject(r.getResultObject());

    return result;
  }
  
  /**
   * Reusable
   * @param conn
   * @param rs
   * @param credentials
   * @return
   */
  IResult nodesFromJSONResultSet(IPostgresConnection conn, ResultSet rs, ITicket credentials,
                                 int json_pos) {
    IResult result = new ResultPojo();
    List<Object>lp = new ArrayList<Object>();

    if (rs != null) {
      IResult r = new ResultPojo();

      try {
        JSONObject jo;
        String json;

        while (rs.next()) {
          json = rs.getString(json_pos);
          environment.logDebug("DataProvider.nodesFromJSONResultSet-1 " + json);
          jo = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(json);

          this.finishNodeFetch(conn, jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, r);
          if (r.hasError()) {
            result.addErrorString(r.getErrorString());
            r = new ResultPojo();
          }
		            
          jo = (JSONObject)r.getResultObject();
          this.jsonToProxy(jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, credentials, r);
          if (r.getResultObject() != null)
            lp.add(r.getResultObject());
          if (r.hasError()) {
            result.addErrorString(r.getErrorString());
            r = new ResultPojo();
          }
        } 
      } catch (Exception e) {
        r.addErrorString(e.getMessage());
        environment.logError("DataProvider.nodesFromJSONResultSet-3 " + r.getErrorString() +
                             " " + r.getResultObject(), null);
      }

      result.setResultObject(lp);
    }
    return result;
  }

  @Override
  public IResult getFullNodeJSON(IPostgresConnection conn, String locator) {
    IResult result = getNodeAsJSONObject(conn, locator, _INDEX);
    JSONObject jo = (JSONObject)result.getResultObject();

    environment.logDebug("DataProvider.getFullNodeJSON- "+locator+" "+jo);
    result.setResultObject(jo);

    if (jo != null) {
      // Merge all the properties
      IResult r = this.getNodeProperties(conn, locator, jo);
      if (r.hasError())
        result.addErrorString(r.getErrorString());
      // environment.logDebug("DataProvider.getFullNodeJSON (1)- "+locator+" "+jo);

      JSONObject jx = this.fetchDetails(locator);
      if (jx != null)
        jo.put(ITQCoreOntology.DETAILS_PROPERTY, jx);
      // environment.logDebug("DataProvider.getFullNodeJSON+ "+jo.toJSONString());
    }
    
    return result;
  }

  @Override
  public IResult getVirtualNodeIfExists(IProxy node,
                                        ITicket credentials) {
    // environment.logDebug("DataProvider.getVirtualNodeIfExists- "+node.toJSONString());
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;
    
    try {
      conn = database.getConnection();
      result = getVirtualNodeIfExists(conn, node, credentials);
    } catch (SQLException e) {
      result.addErrorString(e.getMessage());
    }
    conn.closeConnection(result);
    return result;
  }

  @Override
  public IResult getVirtualNodeIfExists(IPostgresConnection conn, IProxy node, ITicket credentials) {
    IResult result = new ResultPojo();
    IResult r = new ResultPojo();
    // environment.logDebug("DataProvider.getVirtualNodeIfExists-2 "+node);
    List<String> tups = node.listMergeTupleLocators(conn, r);
    // environment.logDebug("DataProvider.getVirtualNodeIfExists-3 "+tups);
    
    // a merged node should have just one-- that is tups.size() == 1
    // a virtualnode could have many
    if (node.getIsVirtualProxy() ) {
      result.setResultObject(node);
    } else if (tups != null && !tups.isEmpty()) {
      // Since it's not a VP, then there should be just one entry
      String tuplox = tups.get(0);
      r = this.getNode(tuplox, credentials);
      ITuple tup = (ITuple)r.getResultObject();
      
      if (r.hasError())
        result.addErrorString(r.getErrorString());
      
      // this ITuple has this node as object, virtualnode as subject
      // see BaseVirtualizer.relateNodes
      // {virtualProxy, relation, mergedProxy}
      tuplox = tup.getSubjectLocator();
      r = this.getNode(tuplox, credentials);
      result.setResultObject(r.getResultObject());
      if (r.hasError()) result.addErrorString(r.getErrorString());
    }
    
    // environment.logDebug("DataProvider.getVirtualNodeIfExists+ "+result.getResultObject());
    return result;
  }

  public IResult existsNode(String locator) {
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;
    
    try {
      conn = database.getConnection();
      conn.setProxyRole(result);
      result = existsNode(conn, locator);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
      result.addErrorString(e.getMessage());
    } finally {
      if (conn != null)
        conn.closeConnection(result);
    }
    return result;
  }

  @Override
  public IResult existsNode(IPostgresConnection conn, String locator) {
    IResult result = new ResultPojo();
    String sql = "SELECT lox FROM tq_contents.proxy WHERE lox = '" + locator + "'";
    IResult r = conn.executeSQL(sql);
    ResultSet rs = null;
    
    try {
      boolean t = false;
      rs = (ResultSet)r.getResultObject();
      if (rs != null && rs.next())
        t = true;
      result.setResultObject(new Boolean(t));
      rs.close();
    } catch (Exception e) {
      result.addErrorString(e.getMessage());
      environment.logError(e.getMessage(), e);
    }
    return result;
  }

  public IResult nodeIsA(String nodeLocator, String targetTypeLocator,
                         ITicket credentials) {
    IResult result = getNode(nodeLocator, credentials);
    IProxy n = (IProxy)result.getResultObject();
    boolean t = false;
    
    if (n != null) {
      List<String>tcls = n.listTransitiveClosure();
      
      if (tcls != null && tcls.contains(targetTypeLocator))
        t = true;
    } 
    result.setResultObject(new Boolean(t));
    return result;
  }

  @Override
  public IResult removeNode(String locator, ITicket credentials) {
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;

    try {
      conn = database.getConnection();
      conn.setProxyRole(result);
      result = removeNode(conn, locator, credentials);
    } catch (SQLException e) {
      environment.logError(e.getMessage(), e);
      result.addErrorString(e.getMessage());
    } finally {
      if (conn != null)
        conn.closeConnection(result);
    }

    return result;
  }

  @Override
  public IResult removeNode(IPostgresConnection conn, String locator, ITicket credentials) {
    IResult result = new ResultPojo();
    // TODO Auto-generated method stub
    // Fetch node
    // If credentials allow that, then node.setIsLive(false)
    return result;
  }

  /**
   * Store the given <code>node</code> and submit it for
   * merge study
   * @param node
   * @return
   */
  @Override
  public IResult putNode(IProxy node) {
    IResult result = putNodeNoMerge(node);

    if (!result.hasError()) {
      nodeCache.add(node.getLocator(), node);
    }
    return result;
  } 

  @Override
  public IResult putNode(IPostgresConnection conn, IProxy node) {
	  
    // Construct the SQL query for insertion.
    String proxy_sql = "INSERT INTO " + PROXY_TABLE +
        " (lox, crtr, node_type, _ver, \"isPrv\", \"isVrt\") VALUES (?, ?, ?, ?, ?, ?)";

    String prop_sql = "INSERT INTO " + PROPS_TABLE +
        " (proxyid, property_key, property_val) VALUES (?, ?, ?)";

    String tcl_sql = "INSERT INTO " + TCL_TABLE +
        " (proxyid, tc_lox) VALUES (?, ?)";

    String subclass_sql = 
        "INSERT INTO tq_contents.superclasses (proxyid, superclass) VALUES (?, ?)";
    IResult result = _putNodeNoMerge(conn, node,
                                     proxy_sql, prop_sql, tcl_sql, subclass_sql );
    // environment.logDebug("DataProvider.putNode-A " + node.getLocator() +
    //                      " " +result.getErrorString());

    if (!result.hasError()) {
      nodeCache.add(node.getLocator(), node);
      JSONObject event = newProxyEvent(IProxyEvent.NEW_EVENT, node.getData());
      kafkaProducer.sendMessage(event.toJSONString());
    }
    return result;
  }
  
  IResult _putNodeNoMerge(IPostgresConnection conn, IProxy node, 
                          String proxy_sql, String prop_sql, String tcl_sql, String subclass_sql) {
    List<String>tcl = node.listTransitiveClosure();
    environment.logDebug("DataProvider.putNodeNoMerge-x "+node.toJSONString());

    IResult result = conn.beginTransaction();
    conn.setProxyRole(result);

    if (node.getLocator() == "SubclassRelationType")
      System.out.println("--- putNodeNoMerge: node_type = " + node.getNodeType());

    Object [] proxy_vals = new Object[6];
    proxy_vals[0] = node.getLocator();
    proxy_vals[1] = node.getCreatorId();
    proxy_vals[2] = node.getNodeType();
    proxy_vals[3] = node.getVersion();
    proxy_vals[4] = node.getIsPrivate();
    proxy_vals[5] = node.getIsVirtualProxy();
    conn.executeSQL(proxy_sql, result, proxy_vals);

    Object [] prop_vals = new Object[3];
    prop_vals[0] = node.getLocator();
    prop_vals[1] = ITQCoreOntology.CREATED_DATE_PROPERTY;
    prop_vals[2] = node.getDateString();
    conn.executeSQL(prop_sql, result, prop_vals);

    prop_vals[1] = ITQCoreOntology.LAST_EDIT_DATE_PROPERTY;
    prop_vals[2] = node.getLastEditDateString();
    conn.executeSQL(prop_sql, result, prop_vals);
    //environment.logDebug("DataProvider.putNodeNoMerge-y "+node.getSmallImage(true)+" "+prop_vals[0]);
  
    if (node.getSmallImage(true) != null) {
    	prop_vals[1] = ITQCoreOntology.SMALL_IMAGE_PATH;
    	prop_vals[2] = node.getSmallImage(true);
        conn.executeSQL(prop_sql, result, prop_vals);
     //   environment.logDebug("DataProvider.putNodeNoMerge-z "+node.getSmallImage(true)+" "+result.getErrorString());
    }
    
    if (node.getImage(true) != null) {
    	prop_vals[1] = ITQCoreOntology.LARGE_IMAGE_PATH;
    	prop_vals[2] = node.getImage(true);  	
        conn.executeSQL(prop_sql, result, prop_vals);
    }

    if (tcl != null && !tcl.isEmpty()) {
      Iterator<String>itr = tcl.iterator();
      String lox;
      
      while (itr.hasNext()) {
        lox = itr.next();
        prop_vals = new Object[2];
        prop_vals[0] = node.getLocator();
        prop_vals[1] = lox;
        conn.executeSQL(tcl_sql, result, prop_vals);
      }
    }

    // subclasses if any
    List<String>sups = node.listSuperclassIds(true);
    // environment.logDebug("DataProvider.putNodeNoMerge-1c "+tcl);
    Object [] svals = new Object[2];
    
    if (sups != null && !sups.isEmpty()) {
      svals[0] = node.getLocator();
      Iterator<String>itr = sups.iterator();
      while (itr.hasNext()) {
        svals[1] = itr.next();
        conn.executeSQL(subclass_sql, result, svals);
      }
    }

    // tuple properties subject, subjecttype, subjectrole, object object type, objectrole
    String x = ((ITuple)node).getSubjectLocator();
    if (x != null && !x.equals("")) {
      svals = new Object[3];
      svals[0] = node.getLocator();
      svals[1] = ITQCoreOntology.TUPLE_SUBJECT_PROPERTY;
      svals[2] = x;
      conn.executeSQL(prop_sql, result, svals);
    }

    x = ((ITuple)node).getObject();
    if (x != null && !x.equals("")) {
      svals[1] = ITQCoreOntology.TUPLE_OBJECT_PROPERTY;
      svals[2] = x;
      conn.executeSQL(prop_sql, result, svals);
    }

    x = ((ITuple)node).getSubjectType();
    if (x != null && !x.equals("")) {
      svals[1] = ITQCoreOntology.TUPLE_SUBJECT_TYPE_PROPERTY;
      svals[2] = x;
      conn.executeSQL(prop_sql, result, svals);
    }

    x = ((ITuple)node).getObjectType();
    if (x != null && !x.equals("")) {
      svals[1] = ITQCoreOntology.TUPLE_OBJECT_TYPE_PROPERTY;
      svals[2] = x;
      conn.executeSQL(prop_sql, result, svals);
    }

    x = ((ITuple)node).getSubjectRole();
    if (x != null && !x.equals("")) {
      svals[1] = ITQCoreOntology.TUPLE_SUBJECT_ROLE_PROPERTY;
      svals[2] = x;
      conn.executeSQL(prop_sql, result, svals);
    }

    x = ((ITuple)node).getObjectRole();
    if (x != null && !x.equals("")) {
      svals[1] = ITQCoreOntology.TUPLE_OBJECT_ROLE_PROPERTY;
      svals[2] = x;
      conn.executeSQL(prop_sql, result, svals);
    }

    conn.endTransaction();

    conn.closeConnection(result);
    if (result.hasError()) {
      return result;
    }

    //
    // Insert the labels and details into Elasticsearch.
    //
    JSONObject stuff = new JSONObject();
    stuff.put(ITQCoreOntology.LOCATOR_PROPERTY, node.getLocator());

    JSONObject jo = node.getLabel();
    if (jo != null)
      stuff.put(ITQCoreOntology.LABEL_PROPERTY, jo);

    JSONObject j = node.getDetails();
    if (j != null) {
      stuff.put(ITQCoreOntology.DETAILS_PROPERTY, j);
    }
	if (stuff.get(ITQCoreOntology.LOCATOR_PROPERTY) == null)
		throw new RuntimeException("A "+stuff.toJSONString());
    // environment.logDebug("DataProvider.putNodeNoMerge-xx "+stuff.toJSONString());

    IClient client = esProvider.getProvider();
    IResult r = client.put(node.getLocator(), _INDEX, stuff);
/* this is putProxyNoMerge which means NO EVENT    
    JSONObject event = newProxyEvent(IProxyEvent.NEW_EVENT, node.getData());

    // detector.acceptProxy(node);
    if (eventHandler != null)
      eventHandler.acceptEvent(event);	 
     */ 
    return result;
  }
  /**
   * Store the given <code>node</code>
   * @param node
   * @return
   */
  @Override
  public IResult putNodeNoMerge(IProxy node) {
    // environment.logDebug("DataProvider.putNodeNoMerge "+node.getData().toJSONString());
    // Construct the SQL query for insertion.
    String proxy_sql = "INSERT INTO " + PROXY_TABLE +
        " (lox, crtr, node_type, _ver, \"isPrv\", \"isVrt\") VALUES (?, ?, ?, ?, ?, ?)";

    String prop_sql = "INSERT INTO " + PROPS_TABLE +
        " (proxyid, property_key, property_val) VALUES (?, ?, ?)";

    String tcl_sql = "INSERT INTO " + TCL_TABLE +
        " (proxyid, tc_lox) VALUES (?, ?)";

    String subclass_sql = 
        "INSERT INTO tq_contents.superclasses (proxyid, superclass) VALUES (?, ?)";

    // environment.logDebug("DataProvider.putNodeNoMerge-1 "+node.getVersion());
    
    IPostgresConnection conn = null;
    try {
      conn = database.getConnection();
    } catch (SQLException e) {
      IResult r = new ResultPojo();
      r.addErrorString(e.getMessage());
      environment.logError(e.getMessage(), e);
      return r;
    }

    IResult result = _putNodeNoMerge(conn, node, proxy_sql, prop_sql, tcl_sql, subclass_sql);
    // environment.logDebug("DataProvider.putNodeNoMerge+ "+result.getErrorString());
    return result;
  }
  
  ///////////////////////
  // Observation: PSIs are in their own table
  //////////////////////
  @Override
  public IResult getNodeByPSI(String psi, ITicket credentials) {
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;
    
    try {
      conn = database.getConnection();
      result = getNodeByPSI(conn, psi, credentials);
    } catch (SQLException e) {
      result.addErrorString(e.getMessage());
    }
    conn.closeConnection(result);

    return result;
  }

  @Override
  public IResult getNodeByPSI(IPostgresConnection conn, String psi, ITicket credentials) {
    IResult result = new ResultPojo();
    String sql =
        "SELECT row_to_json(proxy) FROM tq_contents.proxy WHERE lox IN "+
        "(SELECT lox FROM tq_contents.psi WHERE psi = ?";
    
    IResult r = new ResultPojo();
    JSONObject jo = null;
    
    r = conn.setProxyRole();
    conn.executeSelect(sql, r, psi);
    ResultSet rs = (ResultSet) r.getResultObject();
    if (r.hasError())
      result.addErrorString(r.getErrorString());
    
    try {
      if (rs != null) {
        if (rs.next()) {
          jo = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(rs.getString(1));
          if (jo != null) {
            finishNodeFetch(conn, jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, r);
            this.jsonToProxy(jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, credentials, r);
            result.setResultObject(r.getResultObject());
          }
        }
				   
      }
    } catch (Exception e) {
      environment.logError(e.getMessage(), e);
      result.addErrorString(e.getMessage());
    }
    
    if (rs != null)
      conn.closeConnection(r);
    return result;
  }
  
  /**
   * <p>Fetch all possible <em>textBodies</em> from ElasticSearch</p>
   * <p>If any with that label, then intersect that list of locators
   *  with a <em>type fetch</em> from Postgres</p>
   * @param label
   * @param typeLocator
   * @param language
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  @Override
  public IResult listNodesByLabelAndType(String label, String typeLocator,
                                         String language, int start, int count,
                                         String sortBy, String sortDir, ITicket credentials) {
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;
    
    try {
      conn = database.getConnection();
      result = listNodesByLabelAndType(conn, label, typeLocator, language, start,
                                       count, sortBy, sortDir, credentials);
    } catch (SQLException e) {
      result.addErrorString(e.getMessage());
    }
    conn.closeConnection(result);

    return result;
  }

  @Override
  public IResult listNodesByLabelAndType(IPostgresConnection conn, String label, String typeLocator,
                                         String language, int start, int count, String sortBy,
                                         String sortDir, ITicket credentials) {
    IResult result = new ResultPojo();
    List<IProxy>lp = new ArrayList<IProxy>();
    result.setResultObject(lp);
    
    IResult r;
    String [] indices = new String [1];
    indices[0] = _INDEX;
    String [] fields = new String[1];
    fields[0] = "label." + language.toLowerCase();

    r = textQueryUtil.queryText(label, start, count, _INDEX, indices, fields);
    if (r.hasError())
      result.addErrorString(r.getErrorString());
    
    if (r.getResultObject() != null) {
      List<JSONObject>l = (List<JSONObject>)r.getResultObject();
      String sql =
          "SELECT lox FROM tq_contents.proxy WHERE lox = ? AND node_type= '" + typeLocator + "'";
      JSONObject jo;
      Iterator<JSONObject> itr = l.iterator();
      String lox;
      ResultSet rs;
      
      try {
        while (itr.hasNext()) {
          jo = itr.next();
          lox = jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY);
          r = conn.executeSelect(sql, r, lox);
          if (r.hasError())
            result.addErrorString(r.getErrorString());
          rs = (ResultSet)r.getResultObject();
          
          if (rs != null) {
            if (rs.next()) {
              r = this.getNode(lox, credentials);
              if (r.hasError())
                result.addErrorString(r.getErrorString());
              if (r.getResultObject() != null)
                lp.add((IProxy)r.getResultObject());
            }
          }
          conn.closeResultSet(rs, r);
        }
      } catch (SQLException e) {
        result.addErrorString(e.getMessage());
        environment.logError(e.getMessage(), e);
      }
    }
    return result;
  }

  /**
   * Fetch an list of proxies identified by <code>locators</code>
   * @param conn
   * @param locators
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  @Override
  public IResult listProxiesByLocators(IPostgresConnection conn, List<String>locators, int start,
                                       int count, String sortBy, String sortDir, ITicket credentials) {
    IResult result = new ResultPojo();
    List<IProxy> l = new ArrayList<IProxy>();

    result.setResultObject(l);
    if (locators != null && !locators.isEmpty()) {
      IResult r;
      Iterator<String> itr = locators.iterator();

      while (itr.hasNext()) {
        r = this.getNode(conn, itr.next(), credentials);
        if (r.hasError())
          result.addErrorString(r.getErrorString());
        if (r.getResultObject() != null)
          l.add((IProxy)r.getResultObject());
      }
    }

    return result;
  }

  /**
   * <p>Fetch a list of proxies identified by the <code>lox</code> property in each <code>textBody</code></p>
   * <p>This means you don't have to fetch the labels and details for each since they are provided</p>
   * @param conn
   * @param textBodies
   * @param credentials
   * @return
   */
  @Override
  public IResult listProxiesByTextBodies(IPostgresConnection conn, List<JSONObject>textBodies, ITicket credentials) {
    IResult result = new ResultPojo();
    List<IProxy> l = new ArrayList<IProxy>();
    result.setResultObject(l);
    
    if (textBodies != null && !textBodies.isEmpty()) {
      JSONObject jo;
      IResult r;
      Iterator<JSONObject> itr = textBodies.iterator();
      
      while (itr.hasNext()) {
        jo = itr.next();
        r = this.getNode(conn, jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), credentials);
        if (r.hasError())
          result.addErrorString(r.getErrorString());
        if (r.getResultObject() != null)
          l.add((IProxy)r.getResultObject());
      }
    }
    return result;
  }
  
  /**
   * Query ElasticSearch
   * @param label
   * @param language
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  @Override
  public IResult listNodesByLabel(String label, String language, int start,
                                  int count, String sortBy, String sortDir,
                                  ITicket credentials) {
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;
    
    try {
      conn = database.getConnection();
      result = listNodesByLabel(conn, label, language, start, count, sortBy,
                                sortDir, credentials);
    } catch (SQLException e) {
      result.addErrorString(e.getMessage());
    }
    conn.closeConnection(result);

    return result;
  }

  @Override
  public IResult listNodesByLabel(IPostgresConnection conn, String label, String language,
                                  int start, int count, String sortBy, String sortDir,
                                  ITicket credentials) {
    IResult result = new ResultPojo();
		
    IResult r;
    String [] indices = new String [1];
    indices[0] = _INDEX;

    String [] fields = new String[1];
    fields[0] = "label." + language.toLowerCase();

    r = textQueryUtil.queryText(label, start, count, _INDEX, indices, fields);
    if (r.hasError())
      result.addErrorString(r.getErrorString());
    
    if (r.getResultObject() != null) {
      List<JSONObject> l = (List<JSONObject>)r.getResultObject();
      r = this.listProxiesByTextBodies(conn, l, credentials);
      if (r.hasError())
        result.addErrorString(r.getErrorString());
      result.setResultObject(r.getResultObject());
    }
    return result;
  }

  /**
   * Uses a fuzzy query on ElasticSearch to get locators, then fetch from Postgres
   * @param labelFragment
   * @param language
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  public IResult listNodesByLabelLike(String labelFragment, String language,
                                      int start, int count, String sortBy, String sortDir,
                                      ITicket credentials) {
    IResult result = new ResultPojo();
    //TODO
    return result;
  }

  /**
   * Query ElasticSearch for locators, then fetch from Postgres
   * @param detailsFragment
   * @param language
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  @Override
  public IResult listNodesByDetailsLike(String detailsFragment,
                                        String language, int start, int count,
                                        String sortBy, String sortDir, ITicket credentials) {
    IResult result = new ResultPojo();
    result.addErrorString("DataProvider.listNodesByDetailsLike not implemented");
    //TODO
    return result;
  }

  @Override
  public IResult listNodesByCreatorId(String creatorId, int start, int count, ITicket credentials) {
    return this.listNodesByKeyValuePair(ITQCoreOntology.CREATOR_ID_PROPERTY, creatorId,
                                        start, count, null, null, credentials);
  }

  @Override
  public IResult listInstanceNodes(String typeLocator, int start, int count, ITicket credentials) {
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;
    
    try {
      conn = database.getConnection();
      result = listInstanceNodes(conn, typeLocator, start, count, credentials);
    } catch (SQLException e) {
      result.addErrorString(e.getMessage());
      result.setResultObject(null);
    }
    conn.closeConnection(result);

    return result;
  }

  @Override
  public IResult listInstanceNodes(IPostgresConnection conn, String typeLocator,
                                   int start, int count, ITicket credentials) {
    IResult result = new ResultPojo();
    List<IProxy>l = new ArrayList<IProxy>();
    result.setResultObject(l);
    StringBuilder buf = new StringBuilder();
    int ct = 0;
    String sql = 
        "SELECT row_to_json(proxy) FROM tq_contents.proxy WHERE node_type = ? " +
        "ORDER BY lox OFFSET ? ";
    
    buf.append(sql);
    ct = 2;
    if (count > 0) {
      buf.append("LIMIT ? ");
      ct++;
    }
    
    Object [] vals = new Object[ct];
    vals[0] = typeLocator;
    vals[1] = start;
    if (count > 0) {
      vals[2] = count;
    }
    sql = buf.toString();
    
    IResult r = new ResultPojo();
    JSONObject jo = null;
    Object o1;

    r = conn.setProxyRole();
    conn.executeSelect(sql, r, vals);
    o1 = r.getResultObject();

    if (r.hasError()) {
      result.addErrorString(r.getErrorString());
    }

    if (o1 != null) {
      ResultSet rs = (ResultSet)o1;

      r = nodesFromJSONResultSet(conn, rs, credentials, 1);
      if (r.hasError())
        result.addErrorString(r.getErrorString());
      result.setResultObject(r.getResultObject());
    }
		    
    return result;
  }

  @Override
  public IResult listSubclassNodes(String superclassLocator, int start,
                                   int count, ITicket credentials) {
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;
    
    try {
      conn = database.getConnection();
      result = listSubclassNodes(conn, superclassLocator, start, count, credentials);
    } catch (SQLException e) {
      result.addErrorString(e.getMessage());
      System.out.println("DataProvider.listSubclassNodes-1 " + result.getErrorString() +
                         " " + result.getResultObject());
      result.setResultObject(null);
    }
    conn.closeConnection(result);

    return result;
  }
   
  @Override
  public IResult listSubclassNodes(IPostgresConnection conn, String superclassLocator,
                                   int start, int count, ITicket credentials) {
    IResult result = new ResultPojo();
    StringBuilder buf = new StringBuilder();
    int ct = 0;
    String sql = "SELECT proxyid " +
        "FROM tq_contents.superclasses " +
        "WHERE superclass = ? " +
        "ORDER BY proxyid OFFSET ?";
    
    buf.append(sql);
    ct = 2;
    if (count > 0) {
      buf.append("LIMIT ? ");
      ct++;
    }
    
    Object [] vals = new Object[ct];
    vals[0] = superclassLocator;
    vals[1] = start;
    if (count > 0) {
      vals[2] = count;
    }
    
    sql = buf.toString();
    IResult x = fetchSomeLocatorsFromSelectQuery(conn, sql, "proxyid", vals);
    List<String>locators = (List<String>)x.getResultObject();
    
    if (x.hasError())
      result.addErrorString(x.getErrorString());
    
    x = fetchSomeNodes(conn, locators, credentials);
    result.setResultObject(x.getResultObject());
    if (x.hasError())
      result.addErrorString(x.getErrorString());
    
    return result;
  }
   
  IResult fetchSomeLocatorsFromSelectQuery(IPostgresConnection conn, String sql,
                                           String field, Object [] vals) {
    IResult result = new ResultPojo();
    List<String>l = new ArrayList<String>();
    result.setResultObject(l);
    
    IResult r = new ResultPojo();
    conn.executeSelect(sql, r, vals);
    
    ResultSet rs = (ResultSet)r.getResultObject();
    if (r.hasError())
      result.addErrorString(r.getErrorString());
    
    if (rs != null) {
      try {
        while (rs.next()) {
          l.add(rs.getString(field));
        }
      } catch (SQLException e) {
        environment.logError(e.getMessage(), e);
        result.addErrorString(e.getMessage());
      } 
      conn.closeResultSet(rs, result);
    }
    return result;
  }
 
  IResult fetchSomeNodes(IPostgresConnection conn, List<String> locators, ITicket credentials) {
    IResult result = new ResultPojo();
    List<IProxy> l = new ArrayList<IProxy>();
    
    if (locators.isEmpty())
      return result;
    
    result.setResultObject(l);
    Iterator<String>itr = locators.iterator();
    IResult r;
    
    while (itr.hasNext()) {
      r = this.getNode(conn, itr.next(), credentials);
      if (r.getResultObject() != null)
        l.add((IProxy)r.getResultObject());
      if (r.hasError())
        result.addErrorString(r.getErrorString());
    }	   
    return result;
  }

  IResult fetchSomeJSONNodes(IPostgresConnection conn, List<String> locators) {
    IResult result = new ResultPojo();
    List<JSONObject> l = new ArrayList<JSONObject>();
    
    if (locators.isEmpty())
      return result;
    result.setResultObject(l);
    
    String sql = "SELECT row_to_json(proxy) FROM tq_contents.proxy WHERE lox = ?";
    Iterator<String>itr = locators.iterator();
    IResult r = new ResultPojo();
    ResultSet rs;
    JSONObject jo;
    
    while (itr.hasNext()) {
      conn.executeSelect(sql, r, itr.next());
      rs = (ResultSet)r.getResultObject();
      try {
        if (rs != null) {
          if (rs.next()) {
            jo = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(rs.getString(1));
            if (jo != null) {
              finishNodeFetch(conn, jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, r);
              l.add(jo);
              environment.logDebug("DataProvider.fetchSomeJSONNodes "+jo.toJSONString());
            }
          }
        }
      } catch (Exception e) {
        environment.logError(e.getMessage(), e);
        result.addErrorString(e.getMessage());
      }
    }
    return result;
  }

  /**
   * <p>Returns a <code>List<JSONObject</code> in <code>r</code></p>
   * <p>Requires that the <code>SELECT</code> takes the form
   * 	<code>SELECT row_to_json(proxy) FROM tq_contents.proxy WHERE</code></p>
   * @param conn
   * @param sql
   * @param vals
   * @param r
   */
  void listNodesBySelectQuery(IPostgresConnection conn, String sql, Object [] vals, IResult r) {
    environment.logDebug("DataProvider.listNodesBySelectQuery- "+vals.length+" "+sql);
    conn.executeSelect(sql, r, vals);
    ResultSet rs = (ResultSet)r.getResultObject();
    environment.logDebug("DataProvider.listNodesBySelectQuery-1 "+r.getErrorString()+" "+r.getResultObject());
    
    List<JSONObject> l = new ArrayList<JSONObject>();
    r.setResultObject(l);
    
    if (rs != null) {
      JSONObject jo;
      try {
        while (rs.next()) {
          jo = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(rs.getString(1));
          environment.logDebug("DataProvider.listNodesBySelectQuery-2 "+jo.toJSONString());
          if (jo != null) {
            finishNodeFetch(conn, jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, r);
            l.add(jo);
          }
        }
      } catch (Exception e) {
        environment.logError(e.getMessage(), e);
        r.addErrorString(e.getMessage());
      }
      conn.closeResultSet(rs, r);
    }
  }
  
  @Override
  public void mergeTwoProxies(IProxy leftNode,
                              IProxy rightNode, String reason, String userLocator,
                              IMergeResultsListener mergeListener, ITicket credentials) {
    // environment.logDebug("DataProvider.mergeTwoProxies "+leftNode+" "+rightNode);
    Map<String,Double> mergeData = new HashMap<String,Double>();
    String rx = reason;

    if (rx == null || rx.equals(""))
      rx = "No reason given: user-suggested";
    mergeData.put(rx, 1.0);
    mergePerformer.performMerge(leftNode, rightNode, mergeData, 1.0,
                                userLocator, reason, mergeListener, credentials);
  }

  private void recursiveWalkDownTree(IPostgresConnection conn, IResult result, ITreeNode root, 
                                     int maxDepth, int curDepth, int start, int count,
                                     ITicket credentials, List<String> loopStopper) {
    // stopping rule
    if (curDepth == 0)
      return;
    
    // Given this root, grab its children, then recurse on them
    String lox = root.getNodeLocator();
    if (loopStopper.contains(lox))
      return;
    
    loopStopper.add(lox);
    // Note: the day will come when -1 will bite us in the butt due to huge
    // collections
    
    IResult r = this.listSubclassNodes(conn, lox, start, count, credentials);
    if (r.hasError())
      result.addErrorString(r.getErrorString());
    
    List<IProxy>kids = null;
    Iterator<IProxy>itr;
    IProxy snapper;
    ITreeNode child;
    
    if (r.getResultObject() != null) {
      kids = (List<IProxy>)r.getResultObject();
      itr = kids.iterator();

      while (itr.hasNext()) {
        // get the kid
        snapper = itr.next();
        child = new TreeNode(snapper.getLocator(), snapper.getLabel("en"));
        root.addSubclassChild(child);
        // now populate it
        recursiveWalkDownTree(conn, result,child,maxDepth, --curDepth,
                              start, count, credentials, loopStopper);
      }
    }
    
    r = this.listInstanceNodes(conn, lox, 0, 200, credentials);
    if (r.hasError())
      result.addErrorString(r.getErrorString());
    
    if (r.getResultObject() != null) {
      kids = (List<IProxy>)r.getResultObject();
      itr = kids.iterator();

      while (itr.hasNext()) {
        // get the kid
        snapper = itr.next();
        child = new TreeNode(snapper.getLocator(), snapper.getLabel("en"));
        root.addInstanceChild(child);
        // now populate it
        recursiveWalkDownTree(conn, result,child, maxDepth, --curDepth,
                              start, count, credentials, loopStopper);
      }
    }
  }

  /**
   * A workhorse that supports fetching proxies which have
   * property key/value pairs which match
   * @param key
   * @param value
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  @Override
  public IResult listNodesByKeyValuePair(String key, String value, int start, int count,
                                         String sortBy, String sortDir, ITicket credentials) {
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;
    
    try {
      conn = database.getConnection();
      result = listNodesByKeyValuePair(conn, key, value, start, count,
                                       sortBy, sortDir, credentials);
    } catch (SQLException e) {
      result.addErrorString(e.getMessage());
    }
    conn.closeConnection(result);

    return result;
  }

  @Override
  public IResult listNodesByKeyValuePair(IPostgresConnection conn, String key, String value,
                                         int start, int count, String sortBy, String sortDir,
                                         ITicket credentials) {
    IResult result = new ResultPojo();
    String sql =
        "SELECT lox, row_to_json(proxy) FROM tq_contents.proxy WHERE lox IN "+
        "(SELECT proxyid FROM tq_contents.properties WHERE "+
        "property_key = ? AND property_val = ?) OFFSET ? ORDER BY ?";
	   	
    int ct = 4;
    String sortby = "lox";
    if (sortBy != null)
      sortby = sortBy;
    if (count > 0)
      ct++;

    Object [] vals = new Object[4];
    vals[0] = key;
    vals[1] = value;
    vals[2] = start;
    vals[3] = sortby;
    if (count > 0) {
      vals[4] = count;
      sql += " LIMIT ?";
    }
    
    IResult r = conn.executeSelect(sql, vals);
    ResultSet rs = (ResultSet)r.getResultObject();
    environment.logDebug("DataProvider.listNodesByKeyValuePair " + sql + " \n " +
                         r.getErrorString() + " " + rs);
    
    r  = nodesFromJSONResultSet(conn, rs, credentials, 2);
    if (r.hasError())
      result.addErrorString(r.getErrorString());
    result.setResultObject(r.getResultObject());

    return result;
  }

  /**
   * <p>If node is <code>not live</code> returns <code>-1</code></p>
   * <p>If <code>credentials</code> == <code>null</code>, returns <code>-1</code></p>
   * <p>Return <code>1</code> if sufficient <code>credentials</code>
   * to allow viewing this <code>node></p>
   * <p>Return <code>0</code> if not sufficient <code>credentials</code></p>
   * <p>Return <code>-1</code> if node has been removed: (isAlive = false)</p>
   * @param node
   * @param credentials
   * @return
   */
  private int checkCredentials(IProxy node, ITicket credentials) {
    return credentialUtility.checkCredentials(node, credentials);
  }

  @Override
  public void shutDown() {
    try {
      // interceptor.shutDown();
      // detector.shutDown();
      database.shutDown();
    } catch (Exception e) {
      environment.logError(e.getMessage(), e);
    }
  }
	
  private JSONObject newProxyEvent(String type, JSONObject proxy) {
    JSONObject result = new JSONObject();
    result.put(IProxyEvent.EVENT_TYPE, type);
    result.put(IProxyEvent.CARGO, proxy);
    return result;
  }

  @Override
  public IResult runTextQuery(String query, ITicket credentials) {
	    IResult result = new ResultPojo();
	    result.addErrorString("DataProvider.runTextQuery not implemented");
	    //TODO
	    return result;
  }
	
  @Override
  public IResult runTextQuery(IPostgresConnection conn, String query, ITicket credentials) {
	    IResult result = new ResultPojo();
	    result.addErrorString("DataProvider.runTextQuery not implemented");
	    //TODO
	    return result;
  }
	
  @Override
  public IResult runSQLQuery(String query, Object[] vals, ITicket credentials) {
	    IResult result = new ResultPojo();
	    result.addErrorString("DataProvider.runSQLQuery not implemented");
	    //TODO
	    return result;
  }
	
  @Override
  public IResult runSQLQuery(IPostgresConnection conn, String query, Object[] vals, ITicket credentials) {
	    IResult result = new ResultPojo();
	    result.addErrorString("DataProvider.runSQLQuery not implemented");
	    //TODO
	    return result;
  }

  @Override
  public IResult getNodeProperties(IPostgresConnection conn, String locator, JSONObject jo) {
    IResult result = new ResultPojo();
    JSONObject jx = jo;
    if (jx == null)
      jx = new JSONObject();
    
    result.setResultObject(jo);
    // Fetch core properties
    IResult r = new ResultPojo();
    String sql = "SELECT property_key, property_val FROM tq_contents.properties WHERE proxyid = ?";
    conn.executeSelect(sql, r, locator);
    
    ResultSet rs = (ResultSet)r.getResultObject();
    if (rs != null) {
      try {
        String k, v;
        while (rs.next()) {
          //JSONObject jp = new JSONObject();
          k = rs.getString(1);
          v = rs.getString(2);

          environment.logDebug("DataProviderFOO " + k + " | " + v);
          
          ///////////////////////
          // This is complex
          // If the key exists, it may be a collection
          // But, if it's a Date or Boolean, etc, then you
          // Don't want to allow this merge
          ///////////////////////
          Object vx = jo.get(k);
          if  (vx == null)
            jo.put(k, v);
          else if (!v.equals(vx)) {
            List<String> l;
            Object o = jo.get(k);
            if (o instanceof List)
              l = (List<String>)o;
            else {
              l = new ArrayList<String>();
              l.add(jo.getAsString(k));
            }
            if (!l.contains(v))
              l.add(v);
            jo.put(k, l);
          } 
        }
      } catch (Exception e) {
        environment.logError(e.getMessage(), e);
        result.setResultObject(e.getMessage());
      }
    }
    List<String> foo = new ArrayList<String>();
    if (jo != null) {
      // Fetch supers
      sql = "SELECT superclass FROM tq_contents.superclasses WHERE proxyId = ?";
      conn.executeSelect(sql, r, locator);
      rs = (ResultSet)r.getResultObject();
      if (rs != null) {
        try {
          while (rs.next())
            foo.add(rs.getString("superclass"));
        } catch (SQLException e) {
          environment.logError(e.getMessage(), e);
          result.setResultObject(e.getMessage());
        }
        if (!foo.isEmpty()) {
          jo.put(ITQCoreOntology.SUBCLASS_OF_PROPERTY_TYPE, foo);
          foo = new ArrayList<String>();
        }
      }

      // Fetch acls
      sql = "SELECT acl FROM tq_contents.acls WHERE proxyid = ?";
      conn.executeSelect(sql, r, locator);
      rs = (ResultSet)r.getResultObject();
      if (rs != null) {
        try {
          while (rs.next())
            foo.add(rs.getString("acl"));
        } catch (SQLException e) {
          environment.logError(e.getMessage(), e);
          result.setResultObject(e.getMessage());
        }
        if (!foo.isEmpty()) {
          jo.put(ITQCoreOntology.RESTRICTION_PROPERTY_TYPE, foo);
          foo = new ArrayList<String>();
        }
      }

      // Fetch psis
      sql = "SELECT psi FROM tq_contents.psi WHERE proxyid = ?";
      conn.executeSelect(sql, r, locator);
      rs = (ResultSet)r.getResultObject();
      if (rs != null) {
        try {
          while (rs.next())
            foo.add(rs.getString("psi"));
        } catch (SQLException e) {
          environment.logError(e.getMessage(), e);
          result.setResultObject(e.getMessage());
        }
        
        // environment.logDebug("DataProviderPSI "+locator+" "+foo);
        if (!foo.isEmpty()) {
          jo.put(ITQCoreOntology.PSI_PROPERTY_TYPE, foo);
          foo = new ArrayList<String>();
        }
      }
			
      // fetch transitive closure
      sql = "SELECT tc_lox FROM tq_contents.transitive_closure WHERE proxyid = ?";
      conn.executeSelect(sql, r, locator);
      rs = (ResultSet)r.getResultObject();
      if (rs != null) {
        try {
          while (rs.next())
            foo.add(rs.getString("tc_lox"));
        } catch (SQLException e) {
          environment.logError(e.getMessage(), e);
          result.setResultObject(e.getMessage());
        }
        
        // environment.logDebug("DataProvider.full "+locator+" "+foo);
        if (!foo.isEmpty()) {
          jo.put(ITQCoreOntology.TRANSITIVE_CLOSURE_PROPERTY_TYPE, foo);
          foo = new ArrayList<String>();
        }
      }

      // ADD ALL THE tuple locators
      jo.put(ITQCoreOntology.TUPLE_LIST_PROPERTY, listAllTupleLocators(conn,  locator));
    
      //PARENT-CHILD
      sql = "SELECT context, lox, parent_lox FROM tq_tree.conv WHERE id = ?";
      conn.executeSelect(sql, r, locator);
      rs = (ResultSet)r.getResultObject();
      if (rs != null) {
    	  String ctx, lx, plx;
    	  JSONObject children = new JSONObject();
    	  JSONObject parents = new JSONObject();
    	  List<String>xxx;
        try {
          while (rs.next()) {
            ctx = rs.getString("context");
            lx = rs.getString("lox"); //the child
            plx = rs.getString("parent_lox");
            xxx = (List<String>)children.get(ctx);
            if (xxx == null) xxx = new ArrayList<String>();
            xxx.add(lx);
            children.put(ctx, xxx);
            if (plx != null)
            	parents.put(ctx, plx);
          }
          if (!children.isEmpty())
        	  jo.put(IParentChildContainer.CHILDREN_KEY, children);
          if (!parents.isEmpty())
        	  jo.put(IParentChildContainer.PARENTS_KEY, parents);
        } catch (SQLException e) {
          environment.logError(e.getMessage(), e);
          result.setResultObject(e.getMessage());
        }
      }
    
    }
    // environment.logDebug("DataProvider.getProperties " + result.getErrorString() + " | " +
    //                      result.getResultObject());
	
    return result;
  }

  List<String> listAllTupleLocators(IPostgresConnection conn, String locator) {
    List<String>result = new ArrayList<String>();
    String sql =
        "SELECT proxyid FROM tq_contents.properties WHERE "+
        "( property_key = ? AND property_val = ? ) OR "+
        "( property_key = ? AND property_val = ? )";
    
    // TODO will this fetch ALL of them?
    Object [] vals = new Object[4];
    vals[0] = ITQCoreOntology.TUPLE_SUBJECT_PROPERTY;
    vals[1] = locator;
    vals[2] = ITQCoreOntology.TUPLE_OBJECT_PROPERTY;
    vals[3] = locator;
    
    IResult r = conn.executeSelect(sql, vals);
    ResultSet rs = (ResultSet)r.getResultObject();
    
    if (rs != null) {
      try {
        String loc;
        while (rs.next()) {
          loc = rs.getString("proxyid");
          result.add(loc);
        }
      } catch (Exception e) {
        environment.logError(e.getMessage(), e);
      }
    }
    return result;
  }

  @Override
  public IResult getFullNodeJSON(String locator) {
    IResult result = new ResultPojo();
    IPostgresConnection conn = null;
	    
    try {
      conn = database.getConnection();
      result = getFullNodeJSON(conn, locator);
    } catch (SQLException e) {
      result.addErrorString(e.getMessage());
    }
    conn.closeConnection(result);
    return result;
  }
  @Override
  public IResult getFullNode(String locator, ITicket credentials) {
	    IResult result = new ResultPojo();
	    IPostgresConnection conn = null;
		    
	    try {
	      conn = database.getConnection();
	      result = getFullNode(conn, locator, credentials);
	    } catch (SQLException e) {
	      result.addErrorString(e.getMessage());
	    }
	    conn.closeConnection(result);
	    return result;
  }

  @Override
  public IResult getFullNode(IPostgresConnection conn, String locator, ITicket credentials) {
  	IResult result = getFullNodeJSON(conn, locator);
  	JSONObject jo = (JSONObject)result.getResultObject();
  	if (jo != null) {
  		jsonToProxy(locator, jo, credentials, result);
  	}
  	return result;
  }




}
