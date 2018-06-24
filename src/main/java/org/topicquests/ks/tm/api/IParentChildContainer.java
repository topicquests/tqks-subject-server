/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import java.util.List;

import org.topicquests.ks.api.ITicket;
import org.topicquests.pg.api.IPostgresConnection;

import net.minidev.json.JSONObject;

public interface IParentChildContainer {
	public static final String
		PARENTS_KEY		= "parents",
		CHILDREN_KEY	= "children";
	/**
	 * A map of parentNodeLocators for a given contextLocator
	 * @param parents
	 */
	void setParents(JSONObject parents);
	
	JSONObject getParents();
	
	/**
	 * A map of lists of childNodeLocators for a given contextLocator
	 * @param children
	 */
	void setChildren(JSONObject children);
	
	JSONObject getChildren();
  /**
   * DO NOT USE DIRECTLY, Only from {@link ISubjectProxyModel}
   * @param contextLocator
   * @param childLocator
   * @param transcluderLocator can be <code>null</code>
   */
  void addChildNode(String contextLocator, String childLocator, String transcludeLocator);
	
  /**
   * DO NOT USE DIRECTLY, Only from {@link ISubjectProxyModel}
   * @param contextLocator
   * @param parentLocator
   */
  void addParentNode(String contextLocator, String parentLocator);
	  
  /**
   * List all childNodes for <code>contextLocator</code>
   * Starts and ends a transaction
   * 
   * @param contextLocator if <code>null</code> lists all
   * @param credentials
   * @return
   */
  List<IProxy> listChildNodes(String contextLocator, ITicket credentials);
	  
  /**
   * Runs inside transaction
   * @param conn
   * @param contextLocator
   * @param credentials
   * @return
   */
  List<IProxy> listChildNodes(IPostgresConnection conn, String contextLocator, ITicket credentials);

  /**
   * List all parent Nodes for <code>contextLocator</code>
   * Starts and ends a transaction
   * 
   * @param contextLocator if <code>null</code> lists all
   * @param credentials
   * @return
   */
  List<IProxy> listParentNodes(String contextLocator, ITicket credentials);
	  
  /**
   * Runs inside transaction
   * @param conn
   * @param contextLocator
   * @param credentials
   * @return
   */
  List<IProxy> listParentNodes(IPostgresConnection conn, String contextLocator, ITicket credentials);

  /**
   * List all ancestor nodes for <code>contextLocator</code>
   * Starts and ends a transaction
   * 
   * @param contextLocator if <code>null</code> lists all
   * @param credentials
   * @return
   */
  List<IProxy> listAncestorNodes(String contextLocator, ITicket credentials);
	  
  /**
   * Runs inside transaction
   * @param conn
   * @param contextLocator
   * @param credentials
   * @return
   */
  List<IProxy> listAncestorNodes(IPostgresConnection conn, String contextLocator, ITicket credentials);
}
