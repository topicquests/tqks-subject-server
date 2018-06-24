/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import java.util.List;

import org.topicquests.es.ProviderEnvironment;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.ITreeNode;
import org.topicquests.ks.tm.merge.VirtualizerHandler;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.pg.api.IPostgresConnection;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public interface IDataProvider {
	
  /**
   * Return the PostgreSQL provider
   * @return
   */
  PostgresConnectionFactory getDBProvider();
	
  /**
   * Return the ElasticSearch provider
   * @return
   */
  ProviderEnvironment getESProvider();

  void setVirtualizer(VirtualizerHandler h);
	
  /**
   * Remove a node from the cache
   * @param nodeLocator
   */
  void removeFromCache(String nodeLocator);
	
  /**
   * Return a new UUID
   * @return
   */
  String getUUID();
	
  /**
   * Return a new UUID with a prefix
   * @param prefix
   * @return
   */
  String getUUID_Pre(String prefix);
	
  /**
   * Return a new UUID with a suffix
   * @param suffix
   * @return
   */
  String getUUID_Post(String suffix);
	
  /**
   * Used typically in merge operations
   * @param labels
   */
  void setLabels(JSONObject labels);
  
  JSONObject addLabel(String locator, String newLabel, String language);
  
  //JSONObject removeLabel(String locator, String oldLabel, String language);

  /**
   * Used typically in merge operations
   * @param details
   */
  void setDetails(JSONObject details);
  
  JSONObject addDetails(String locator, String newDetails, String language);
  
  //JSONObject removeDetails(String locator, String oldDetails, String language);

  /**
   * Starts and ends a transaction
   * Return a node's <em>Taxonomic Instance (Type) Relation</em>
   * @param locator
   * @return
   */
  IResult getNodeInstanceRelation(String locator);

  /**
   * Runs inside a transaction
   * @param conn
   * @param locator
   * @return
   */
  IResult getNodeInstanceRelation(IPostgresConnection conn, String locator);

  /**
   * Starts and ends a transaction
   * Return a list of a node's <em>Taxonomic Subclass Relations</em>
   * @param locator
   * @return
   */
  IResult listNodeSubclassRelations(String locator);

  /**
   * Runs inside a transaction
   * @param conn
   * @param locator
   * @return
   */
  IResult listNodeSubclassRelations(IPostgresConnection conn, String locator);

  /**
   * Starts and ends a transaction
   * <p>Return a node identified by <code>locator</code></p>
   * <p>There are conditions in which the node will not be returned:<br/>
   * <ul><li>The node is <em>not live</em> meaning it was deleted</li>
   * <li>The node is <code>private</code> and the caller's <code>credentials</code>
   * are insufficient<li></ul><p>
   * Note: if <code>credentials</code> == <code>null</code> if the node is live
   * it will be returned.
   * @param locator
   * @param credentials
   * @return
   */
  IResult getNode(String locator, ITicket credentials);

  /**
   * Runs inside a transaction
   * @param conn
   * @param locator
   * @param credentials
   * @return
   */
  IResult getNode(IPostgresConnection conn, String locator, ITicket credentials);
	
  /**
   * Used in Merge and applications which need all of the node's properties
   * Runs inside a transaction
   * Includes all superclass locators
   * Includes all tuple locators
   * @param conn
   * @param locator
   * @param jo can be <code>null</code>
   * @param credentials
   * @return returns a {@link JSONObject}
   */
  IResult getNodeProperties(IPostgresConnection conn, String locator, JSONObject jo);

  /**
   * Remove a node -- actually, mark it isLive=false
   * Starts and ends a transaction
   * @param locator
   * @param credentials
   * @return
   */
  IResult removeNode(String locator, ITicket credentials);

  /**
   * Runs inside transaction
   * @param conn
   * @param locator
   * @param credentials
   * @return
   */
  IResult removeNode(IPostgresConnection conn, String locator, ITicket credentials);

	
	
  /**
   * Starts and ends a transaction
   * <p>Return the raw {@link JSONObject} for a given node</p>
   * <p>Note: no credential checking</p>
   * @param locator
   * @param topic -- the ElasticSearch index
   * @return
   */
  IResult getNodeAsJSONObject(String locator, String topic);
	
  /**
   * Runs inside a transaction
   * @param conn
   * @param locator
   * @param topic
   * @return
   */
  IResult getNodeAsJSONObject(IPostgresConnection conn, String locator, String topic);
	
  /**
   * Starts a transaction, returns a populated node
   * @param locator
   * @param credentials
   * @return
   */
  IResult getFullNode(String locator, ITicket credentials);

  /**
   * Runs inside a transaction
   * @param conn
   * @param locator
   * @param credentials
   * @return
   */
  IResult getFullNode(IPostgresConnection conn, String locator, ITicket credentials);

  
  /**
   * Starts and ends a transaction
   * @param locator
   * @return
   */
  IResult getFullNodeJSON(String locator);

  /**
   * Runs inside a transaction
   * Fetch the full measure of properties for this node
   * Will also contain labels and details
   * @param conn
   * @param locator
   * @return
   */
  IResult getFullNodeJSON(IPostgresConnection conn, String locator);
	
  /**
   * Utility: runs inside a transaction
   * @param conn
   * @param locator
   * @param jo
   * @param r
   */
  void finishNodeFetch(IPostgresConnection conn, String locator, JSONObject jo, IResult r);
	
  /**
   * Utility: runs inside a transaction
   * @param locator
   * @param jo
   * @param credentials
   * @param result
   */
  void jsonToProxy(String locator, JSONObject jo, ITicket credentials, IResult result);
  /**
   * Utility method: fetch the label fields for a proxy from ElasticSearch
   * @param locator
   * @return
   */
  JSONObject fetchLabels(String locator);
	
  /**
   * Utility method: fetch the details fields for a proxy from ElasticSearch
   * @param locator
   * @return
   */
  JSONObject fetchDetails(String locator);
	
  /**
   * Fetch a node which is identified by <code>url</code>.<br/>
   * Note that a <code>url</code> is considered an <em>identity property</em>
   * which means there can be one and only one proxy in the topic map
   * which has that attribute.
   * @param url
   * @param credentials
   * @return
   */
  IResult getNodeByURL(String url, ITicket credentials);
	
  /**
   * Starts and ends a transaction
   * @param psi
   * @param credentials
   * @return
   */
  IResult getNodeByPSI(String psi, ITicket credentials);

  IResult getNodeByPSI(IPostgresConnection conn, String psi, ITicket credentials);

  /**
   * Store a node and submit it for merge study.
   * Starts and ends a transaction
   * @param node
   * @return
   */
  IResult putNode(IProxy node);

  /**
   * Runs inside a transaction
   * @param conn
   * @param node
   * @return
   */
  IResult putNode(IPostgresConnection conn, IProxy node);

  /**
   * Store a node
   * @param node
   * @return
   */
  IResult putNodeNoMerge(IProxy node);
	
  /**
   * Starts and ends a transaction
   * @param locator
   * @return a Boolean result
   */
  IResult existsNode(String locator);
	
  /**
   * Runs inside transaction
   * @param conn
   * @param locator
   * @return
   */
  IResult existsNode(IPostgresConnection conn, String locator);
	
  /**
   * Fetch a virtual proxy if it exists
   * Starts and ends a transaction
   * @param node
   * @param credentials
   * @return
   */
  IResult getVirtualNodeIfExists(IProxy node, ITicket credentials);

  /**
   * Runs inside a transaction
   * @param conn
   * @param node
   * @param credentials
   * @return
   */
  IResult getVirtualNodeIfExists(IPostgresConnection conn, IProxy node, ITicket credentials);

  /**
   * <p>Starts a merge process</p>
   * <p>Does not need to start a transction; that happens
   * when the merge process, itself, happens.</p>
   * @param leftNode
   * @param rightNode
   * @param reason
   * @param userLocator
   * @param mergeListener
   * @param credentials
   */
  void mergeTwoProxies(IProxy leftNode,
                       IProxy rightNode, String reason, String userLocator,
                       IMergeResultsListener mergeListener, ITicket credentials);
	
  ///////////////////
  // Tuples are Proxies: may not need these
  IResult putTuple(ITuple tuple, boolean checkVersion);
	
  IResult getTuple(String tupleLocator, ITicket credentials);
  //
  //////////////////
  /////////////////////////////
  //Tuples
  /////////////////////////////
		  
  /**
   * Starts and ends a transaction
   * List tuples linked to this node of given type
   * @param locator
   * @param relationType if <code>null</code>, returns all relations as JSON strings
   * @param start 
   * @param count <code>-1</code> means all
   * @param credentials
   * @return can return <code>null</code>
   */
  IResult listRelationsByRelationType(String locator, String relationType, int start, int count, ITicket credentials);

  /**
   * Runs inside a transaction
   * @param conn
   * @param locator
   * @param relationType
   * @param start
   * @param count
   * @param credentials
   * @return
   */
  IResult listRelationsByRelationType(IPostgresConnection conn, String locator, String relationType,
                                      int start, int count, ITicket credentials);

  /////////////////////////////
  //TREE
  // This is for displaying and navigating taxonomies
  /////////////////////////////	
  /**
   * Starts and ends a transaction
   * Load a {@link ITreeNode} starting from <code>rootNodeLocator</code>
   * with all its child nodes (<em>subs</em> and <em>instances</em>)
   * to a depth defined by <code>maxDepth</code>
   * @param rootNodeLocator
   * @param maxDepth  -1 means no limit
   * @param start
   * @param count
   * @param credentials
   * @return
   */
  IResult loadTree(String rootNodeLocator, int maxDepth, int start, int count, ITicket credentials);

  /**
   * Runs inside a transaction
   * @param conn
   * @param rootNodeLocator
   * @param maxDepth
   * @param start
   * @param count
   * @param credentials
   * @return
   */
  IResult loadTree(IPostgresConnection conn, String rootNodeLocator, int maxDepth,
                   int start, int count, ITicket credentials);

  //////////////////////////
  // Conversation Tree
  /**
   * <p>Returns list of childNode objects.<p>
   * <p>If <code>contextLocator</code> = <code>null</code>, returns
   * all objects</p>
   * <p>JSONObjects returned behave according to {@link IConversationNode} keys
 * @param rootLocator
 * @param language TODO
 * @param credentials
 * @param credentials
 * @param contextLocator <code>null</code> means list all child nodes
 * @param locator
 * @param contextLocator <code>null</code> means return all parent nodes
   * @return a list of JSON strings which are representations of {@link IConversationNode}
   * /
   List<JSONObject> listChildNodes(String contextLocator);
		
		
		
   /**
   * Return list of parentNode objects
   * @return a list of JSON objects which are representations of {@link IConversationNode}
   * /
   IResult listParentNodes(String locator, String contextLocator, ITicket credentials);
		
   /**
    * Result will contain an instance of IConversationNode which is the conversation
    * tree with <code>rootLocator</code> as its root. ; starts a transaction
    * @return
    */
   IResult fetchConversation(String rootLocator, String language, ITicket credentials);

   /**
    * Runs inside a transaction
    * @param conn
 * @param rootLocator
 * @param language TODO
 * @param credentials
    * @return
    */
   IResult fetchConversation(IPostgresConnection conn, String rootLocator, String language, ITicket credentials);
   ///////////////////
	  
  /**
   * Perform a client-provided query.
   * Starts and ends a transaction
   * @param query
   * @param credentials
   * @return
   */
  IResult runTextQuery(String query, ITicket credentials);
	
  /**
   * Runs inside a transaction
   * @param conn
   * @param query
   * @param credentials
   * @return
   */
  IResult runTextQuery(IPostgresConnection conn, String query, ITicket credentials);

  /**
   * Run a query created by a client
   * @param query
   * @param vals
   * @param credentials
   * @return
   */
  IResult runSQLQuery(String query, Object [] vals, ITicket credentials);

  /**
   * Run inside a transaction
   * @param conn
   * @param query
   * @param vals
   * @param credentials
   * @return
   */
  IResult runSQLQuery(IPostgresConnection conn, String query, Object [] vals, ITicket credentials);

  /**
   * Starts and ends a transaction
   * <p>List nodes by the intersection of their <code>label</code> and 
   * 	<code>typeLocator</code></p>
   * <p>Note: <code>typeLocator</code> can also be a <code>superClassLocator</code></p>
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
  IResult listNodesByLabelAndType(String label, String typeLocator, String language, int start, int count,
                                  String sortBy, String sortDir, ITicket credentials);

  /**
   * Runs inside transaction
   * @param conn
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
  IResult listNodesByLabelAndType(IPostgresConnection conn, String label, String typeLocator,
                                  String language, int start, int count, String sortBy,
                                  String sortDir, ITicket credentials);

  /**
   * Utility method: return a list of nodes identified by <code>locators</code>
   * @param conn
   * @param locators
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  IResult listProxiesByLocators(IPostgresConnection conn, List<String>locators, int start,
                                int count, String sortBy, String sortDir, ITicket credentials);

  /**
   * <p>Utility method: return a list of nodes identified by the <code>textBodies</code> objects.</p>
   * <p>This method follows an ElasticSearch fetch which produces the <code>textBodies</code></p>
   * <p>Any sorting or list size constraint is presumed to have been handled before this</p>
   * @param conn
   * @param textBodies
   * @param credentials
   * @return
   */
  IResult listProxiesByTextBodies(IPostgresConnection conn, List<JSONObject>textBodies, ITicket credentials);


  /**
   * Starts and ends a transaction
   * Returns a list of nodes which contain <code>label</code> for <code>language</code>
   * @param label
   * @param language
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  IResult listNodesByLabel(String label, String language, int start,
                           int count, String sortBy, String sortDir, ITicket credentials);

  /**
   * Runs inside transaction
   * @param conn
   * @param label
   * @param language
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  IResult listNodesByLabel(IPostgresConnection conn, String label, String language, int start,
                           int count, String sortBy, String sortDir, ITicket credentials);


  IResult listNodesByLabelLike(String labelFragment, String language, int start, int count,
                               String sortBy, String sortDir, ITicket credentials);
	
  IResult listNodesByDetailsLike(String detailsFragment, String language, int start, int count,
                                 String sortBy, String sortDir, ITicket credentials);

  IResult listNodesByCreatorId(String creatorId, int start, int count, ITicket credentials);
	
  /**
   * Starts and ends a transaction
   * @param typeLocator
   * @param start
   * @param count
   * @param credentials
   * @return
   */
  IResult listInstanceNodes(String typeLocator, int start, int count, ITicket credentials);
	
  /**
   * Inside a transaction
   * @param conn
   * @param typeLocator
   * @param start
   * @param count
   * @param credentials
   * @return
   */
  IResult listInstanceNodes(IPostgresConnection conn, String typeLocator, int start, int count,
                            ITicket credentials);
	
  /**
   * Starts and ends a transaction
   * @param superclassLocator
   * @param start
   * @param count
   * @param credentials
   * @return
   */
  IResult listSubclassNodes(String superclassLocator, int start, int count, ITicket credentials);

  /**
   * Inside transaction
   * @param conn
   * @param superclassLocator
   * @param start
   * @param count
   * @param credentials
   * @return
   */
  IResult listSubclassNodes(IPostgresConnection conn, String superclassLocator, int start,
                            int count, ITicket credentials);

  /**
   * Utility method
   * @param key
   * @param value
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  IResult listNodesByKeyValuePair(String key, String value, int start, int count,
                                  String sortBy, String sortDir, ITicket credentials);

  IResult listNodesByKeyValuePair(IPostgresConnection conn, String key, String value, int start, int count,
                                  String sortBy, String sortDir, ITicket credentials);


  void shutDown();
}
