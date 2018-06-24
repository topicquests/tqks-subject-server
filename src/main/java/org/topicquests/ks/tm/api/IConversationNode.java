/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import java.util.List;

import net.minidev.json.JSONObject;

public interface IConversationNode {

  public static final String
  	CONTEXT_LOCATOR	= "contextLocator",
    LOCATOR			= "locator",
    NODE_TYPE		= "type",
    ICON			= "smallImagePath",
    SUBJECT			= "subject",
    DETAILS			= "details",
    TRANSCLUDER_ID	= "transcluder",
    CHILD_LIST		= "childList",
    PARENT_LOCATOR	= "parentLocator";

  void setContextLocator(String context);
  String getContextLocator();
  
  void setLocator(String locator);
  String getLocator();
  
  void setNodeType(String type);
  String getNodeType();

  void setParentLocator(String locator);
  String getParentLocator();

  void addChild(JSONObject kid);
  
  /**
   * Can return an empty list
   * @return does not return <code>null</code>
   */
  List<JSONObject> listChildren();
  
  void setSmallIcon(String iconPath);
  String getSmallIcon();
  
  void setSubject(String subject);
  String getSubject();
  
  void setDetails(String details);
  
  /**
   * 
   * @return can return <code>null</code>
   */
  String getDetails();
  
  void setTranscluderLocator(String transcluderId);
  
  /**
   * 
   * @return can return <code>null</code>
   */
  String getTranscluderId();
 
  String toJSON();

  JSONObject getData();
}
