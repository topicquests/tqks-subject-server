/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import java.util.Date;
import java.util.List;
//import java.util.Map;

import net.minidev.json.JSONObject;

import org.topicquests.support.api.IResult;
import org.topicquests.ks.api.IVersionable;
import org.topicquests.pg.api.IPostgresConnection;

public interface IProxy extends IVersionable {

  /**
   * Starts and ends a transaction
   * <p>Do a single update event when changes have been made as compared to
   * individual updates. This method would update LastEditDate and Version</p>
   * <p>Removes this proxy from the cache</p>
   * @return
   */
  IResult doUpdate();
  
  /**
   * Update from inside a transaction
   * @param conn
   * @param r TODO
   */
  void doUpdate(IPostgresConnection conn, IResult r);
		
  /**
   * <p>Perform a simple test on nodeType and superClasses</p>
   * <em>Note: this is a live event</em>
   * @param typeLocator
   * @return
   */
  // boolean localIsA(String typeLocator);
	
  /**
   * Locator is the identifier for this tuple
   * @param tupleLocator
   */
  void setLocator(String tupleLocator);
	
  /**
   * Return this node's locator
   * @return
   */
  String getLocator();

  /**
   * Set creatorId
   * @param id
   */
  void setCreatorId(String id);
	
  /**
   * Return creatorId
   * @return
   */
  String getCreatorId();
	
	
  /**
   * For VirtualNodes
   * Starts and ends a transaction
   * @return can return an empty list
   */
  List<String> listMergeTupleLocators();

  /**
   * Runs inside a transaction
   * @param conn
   * @param r
   * @return
   */
  List<String> listMergeTupleLocators(IPostgresConnection conn, IResult r);

  /**
   * Return <code>true</code> if this {@link IProxy} is a {@link ITuple}
   * @return
   */
  boolean isTuple();

	
  /**
   *  YYYY-MM-DDThh:mm:ssZ; this is the createdDate
   * @param date
   */
  void setDate(Date date);
	
  /**
   * Internally, we need to set two dates when creating a node.
   * One is the creation date, the other is lastEdit date. By
   * allowing for a single {@link Date} to {@link String} conversion
   * we save cycles.
   * @param date
   */
  void setDate(String date);
	
  /**
   * Return createdDate
   * @return
   */
  Date getDate();
  
  String getDateString();
	
  /**
   * <p>Used while building, not while modifying after stored.</p>
   * <p>To use after node is stored, must pay attention to update methods</p>
   * @param date
   */
  void setLastEditDate(Date date);
	
  void setLastEditDate(String date);
  
  /**
   * Runs inside a transaction
   * @param conn
   * @param date
   * @param r
   */
  void setLastEditDate(IPostgresConnection conn, String date, IResult r);
  /**
   * Return the lastEditDate
   * @return
   */
  Date getLastEditDate();
  
  String getLastEditDateString();
	
  /**
   * @return
   */
  JSONObject getData();
	
  /**
   * Return this node expressed in JSON
   * @return
   */
  String toJSONString();
	
  //////////////////////////////////////////////
  //At the moment, it's not clear what "federated" means:
  // We can easily tell if the node has been merged by 
  // testing its mergeTuple property
  //////////////////////////////////////////////
  /**
   * If a node has been federated, set to <code>true</code>; default return if <code>false</code>
   * @param t
   */
  void setIsFederated(boolean t);
	
  boolean getIsFederated();
	
  /**
   * <p>Used while building, not while modifying after stored.</p>
   * <p>To use after node is stored, must pay attention to update methods</p>
   * Labels can come in many languages, or also be synonyms, acronyms, etc
   * @param label
   * @param language
   */
  void addLabel(String label, String language);
	  	  
  /**
   * Will return the <em>first</em>label found.
   * @return can return null by default
   */
  JSONObject getLabel();
  

  /**
   * Will return the <em>first</em>label for the given <code>language</code>
   * but if that <code>language</code> does not exist, returns the first label
   * @param language
   * @return can return "" empty string by default
   */
  String getLabel(String language);
	  
  /**
   * List all labels
   * @return does not return <code>null</code>
   */
  List<String> listLabels();
	  
  /**
   * List labels for given <code>language</code>
   * @param language
   * @return can return <code>null</code>
   */
  List<String> listLabels(String language);
	  
  /**
   * Details can come in many languages
   * @param details
   * @param language
   */
  void addDetails(String details, String language);

  /**
   * Will return the <em>first</em>details found.
   * @return can return null by default
   */
  JSONObject getDetails();

  /**
   * Will return the <em>first</em>details for the given <code>language</code>
   * but if that <code>language</code> does not exist, returns the first details
   * @param language
   * @return can return "" empty string by default
   */
  String getDetails(String language);

  /**
   * List all details; ignores language codes
   * @return
   */
  List<String> listDetails();
	  
  /**
   * List details for <code>language</code>; returns first entry if many
   * @param language
   * @return
   */
  List<String> listDetails(String language);
	  	  
  /**
   * <p>Used while building, bootstrap = <code>true</code>.</p>
   * <p>To use after node is stored, bootstrap = <code>false</code></p>
   * Images are really icons, not pictures
   * @param img can be <code>null</code>
   * <em>should not be an empty string</em>
   * @param isBootstrap TODO
   */
  void setSmallImage(String img, boolean isBootstrap);
	  
  /**
   * <p>Used while building, bootstrap = <code>true</code>.</p>
   * <p>To use after node is stored, bootstrap = <code>false</code></p>
   * @param img can be <code>null</code>
   * <em>should not be an empty string</em>
   * @param isBootstrap 
   */
  void setImage(String img, boolean isBootstrap);
	  
  /**
   * Starts and ends a transaction if bootstrap is <code>false</code>
   * @param isBootstrap 
   * @return can return <code>null</code>
   */
  String getSmallImage(boolean isBootstrap);
	    
  /**
   * Starts and ends a transaction if bootstrap is <code>false</code>
   * @param isBootstrap 
   * @return can return <code>null</code>
   */
  String getImage(boolean isBootstrap);
  
  /**
   * <p>Used while building, not while modifying after stored.</p>
   * <p>To use after node is stored, must pay attention to update methods</p>
   * @param typeLocator
   */
  void setNodeType(String typeLocator);
	  
  /**
   * 
   * @return can return <code>null</code>
   */
  String getNodeType();

  /**
   * A <em>VirtualProxy</em> is an {@link IProxy} which serves as a <em>hub</em>
   * in a collection of nodes which have been merged.
   * @param t
   */
  void setIsVirtualProxy(boolean t);
  
  void setIsVirtualProxy(IPostgresConnection conn, boolean t);
	  
  /**
   * <p>Return <code>true</code> if this is a "virtual proxy"</p>
   * <p>Virtual proxies are created when two nodes are merged: the virtual proxy
   * is created to represent both, or all so-merged nodes; each merged node is then
   * linked with a "merge assertion" to the virtual proxy.</p>
   * @return
   */
  boolean getIsVirtualProxy();
	  
  /**
   * <p>Used while building, not while modifying after stored.
   * 	when <code>isBootstrap</code> is <code>true</code></p>
   * <p>To use after node is stored, must pay attention to update methods
   *  will start and end a transaction.</p>
   * @param superclassLocator
   * @param isBootstrap
   */
  void addSuperclassId(String superclassLocator, boolean isBootstrap);

  /**
   * Runs inside a transaction
   * @param conn
   * @param superclassLocator
   * @param r
   * @param isBootstrap
   */
  void addSuperclassId(IPostgresConnection conn, String superclassLocator, boolean isBootstrap, IResult r);

  /**
   * Stashed properties are not persisted
   * @param key
   * @param value
   */
  void stashProperty(String key, Object value);
  
  Object getStashedProperty(String key);
  
  /**
   * Starts and ends a transaction
   * <p>A generic method<p>
   * <em>Risks overwriting a property</em>
   * <p>Used while building, not while modifying after stored.</p>
   * <p>To use after node is stored, must pay attention to update methods</p>
   * @param key
   * @param value one of <code>String</code> or <code>List<String></code>
   */
  void setProperty(String key, Object value);
  
  /**
   * Inside a transaction
   * @param conn
 * @param key
 * @param value
 * @param r TODO
   */
  void setProperty(IPostgresConnection conn, String key, Object value, IResult r);
	  
  /**
   * Starts and ends a transation
   * <p>add a value to a collection</p>
   * <p>Does so in a set-related (no duplicates) fashion</p>
   * @param key
   * @param value
   */
  void addPropertyValue(String key, String value);
  
  /**
   * Inside a transaction
   * @param conn
 * @param key
 * @param value
 * @param r TODO
   */
  void addPropertyValue(IPostgresConnection conn, String key, String value, IResult r);
	  
  /**
   * Starts and ends a transaction
   * Returns one of <code>String</code> or <code>List<String></code>
   * @param key
   * @return can return <code>null</code>
   */
  Object getProperty(String key);
  
  /**
   * Inside a transaction
   * @param conn
 * @param key
 * @param r TODO
   * @return
   */
  Object getProperty(IPostgresConnection conn, String key, IResult r);
	  
  /**
   * Starts and ends a transaction
   * Remove that key from this proxy's records
   * @param key
   */
  void removeProperty(String key);
  
  /**
   * Inside transaction
   * @param conn
   * @param key
   * @param r
   */
  void removeProperty(IPostgresConnection conn, String key, IResult r);
  
  /**
   * Starts and ends a transaction
   * @param key
   * @param value
   */
  void removePropertyValue(String key, String value);
  
  /**
   * Inside transaction
   * @param conn
   * @param key
   * @param value
   * @param r
   */
  void removePropertyValue(IPostgresConnection conn, String key, String value, IResult r);
  
  /**
   * Starts and ends a transaction.
   * If <code>oldValue</code> does not exist, <code>newValue</code> is inserted
   * @param key
   * @param oldValue
   * @param newValue
   */
  void replacePropertyValue(String key, String oldValue, String newValue);
 
  /**
   * Inside transaction
   * @param conn
   * @param key
   * @param oldValue
   * @param newValue
   * @param r
   */
  void replacePropertyValue(IPostgresConnection conn, String key, String oldValue, String newValue, IResult r);

  /**
   * Starts and ends a transaction if <code>isBootstrap</code>=</code>false</code>
 * @param isBootstrap TODO
   * @return does not return <code>null</code>
   */
  List<String> listSuperclassIds(boolean isBootstrap);
  
  /**
   * Inside a transaction
   * @param conn
 * @param r TODO
   * @return
   */
  List<String> listSuperclassIds(IPostgresConnection conn, IResult r);
	  
  /**
   * <p>Used while building, not while modifying after stored.</p>
   * <p>To use after node is stored, must pay attention to update methods</p>
   * @param isPrivate
   */
  void setIsPrivate(boolean isPrivate);

  /**
   * For updating an existing node
   * @param conn
   * @param isPrivate
   */
  void setIsPrivate(IPostgresConnection conn, boolean isPrivate);

  /**
   * Utility
   * <p>Used while building, not while modifying after stored.</p>
   * <p>To use after node is stored, must pay attention to update methods</p>
   * @param t
   */
  void setIsPrivate(String t);
	  
  /**
   * Defaults to <code>false</code>
   * @return
   */
  boolean getIsPrivate();
	
  /**
   * <p>Intended thus:
   * <li><code>t</code> = <code>true</code> only in the case where
   * a node was set to <code>false</code> before</li>
   * <li><code>t</code> = <code>false</code> to mean that this node
   * has been <em>deleted</code></li>
   * </p>
   * @param t
   */
  void setIsLive(boolean t);
	
  /**
   * Defaults <code>true</code> if the value has not been set.
   * @return
   */
  boolean getIsLive();
	
  /**
   * <p>Return <code>true</code> if <code>typeLocator</code> is found in 
   * this node's <em>transitive closure</em></p>
   * @param typeLocator
   * @return
   */
  boolean isA(String typeLocator);
	  
   /**
   * <p>Used while building, not while modifying after stored.</p>
   * <p>To use after node is stored, must pay attention to update methods</p>
   * @param url
   */
  void setURL(String url);
	  
  /**
   * Return the URL if this node represents a WebResource
   * @return can return <code>null</code>
   */
  String getURL();
	  
  /**
   * Starts and ends a transaction
   * @param psi
   */
  void addPSI(String psi);
  
  /**
   * Add PSI from inside a transaction
   * @param conn
   * @param psi
   * @param r
   */
  void addPSI(IPostgresConnection conn, String psi, IResult r);
	  
  /**
   * Starts and ends a transaction
   * @return does not return <code>null</code>
   */
  List<String> listPSIValues();
  
  /**
   * List PSIs from inside a transaction
   * @param conn
   * @param r
   * @return
   */
  List<String> listPSIValues(IPostgresConnection conn, IResult r);
  
  /**
   * Used mostly for merge
   * @param conn
   * @param psis
   */
  void setPSIList(IPostgresConnection conn, List<String>psis);
  /**
   * Starts and ends a transaction
   * @param psi
   */
  void removePSI(String psi);
  
  /**
   * Inside a transaction
   * @param conn
   * @param psi
   * @param r
   */
  void removePSI(IPostgresConnection conn, String psi, IResult r);
  /////////////////////////////
  // Transitive Closure
  // Transitive Closure is created when a proxy is created.
  // It is a collection of the locators *above* this proxy in its
  // inheritence tree, including type if available, and all superclasses
  /////////////////////////////
  /**
   * <p>Does not return <code>null</code>. Can return an empty list
   * for the root node</p>
   * <em>NOTE: this is a live event</em>
   * @return
   */
  List<String> listTransitiveClosure();
  
  /**
   * List from inside a transaction
   * @param conn
   * @return
   */
  //List<String> listTransitiveClosure(IPostgresConnection conn);

  /**
   * Used during bootstrapping -- not live
   * @param tc
   */
  void setTransitiveClosure(List<String>tc);
  
  /**
   * Used during bootstrapping -- not live
   * @param locator
   */
  void addTransitiveClosureLocator(String locator);

  /////////////////////////////
  //ACL
  /////////////////////////////

  /**
   * Starts and ends a transaction if <code>isTransaction</code> is <code>true</code>
   * @param value
   * @param isTransaction
   */
  void addACLValue(String value, boolean isTransaction);
  
  /**
   * Inside a transaction
   * @param conn
   * @param value
   * @param r
   */
  void addACLValue(IPostgresConnection conn, String value, IResult r);

  /**
   * Starts and ends a transaction
   * @param value
   */
  void removeACLValue(String value);
  
  void removeACLValue(IPostgresConnection conn, String value, IResult r);
	
  List<String>listACLValues();
	
  
  boolean containsACL(String value);
}
