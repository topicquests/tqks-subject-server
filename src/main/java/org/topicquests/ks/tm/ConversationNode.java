/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm;

import java.util.ArrayList;
import java.util.List;

import org.topicquests.ks.tm.api.IConversationNode;
import org.topicquests.ks.tm.api.IProxy;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class ConversationNode implements IConversationNode {
  private JSONObject data;

  public ConversationNode() {
    data = new JSONObject();
  }

  public ConversationNode(String json) throws Exception {
    JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    data = (JSONObject)p.parse(json);
  }
  
  /**
   * Construct a node from its proxy
   * All other factors, e.g. context, must be set by other means
   * @param p
   * @param language
   */
  public ConversationNode(IProxy p, String language) {
	  data = new JSONObject();
	  this.setLocator(p.getLocator());
	  this.setSubject(p.getLabel(language));
	  this.setDetails(p.getDetails(language));
	  this.setSmallIcon(p.getSmallImage(false));
	  this.setNodeType(p.getNodeType());
  }

  @Override
  public void setContextLocator(String context) {
    data.put(IConversationNode.CONTEXT_LOCATOR, context);
  }

  @Override
  public String getContextLocator() {
    return data.getAsString(IConversationNode.CONTEXT_LOCATOR);
  }

  @Override
  public void setLocator(String locator) {
    data.put(IConversationNode.LOCATOR, locator);
  }

  @Override
  public String getLocator() {
    return data.getAsString(IConversationNode.LOCATOR);
  }

  @Override
  public void setParentLocator(String locator) {
	    data.put(IConversationNode.PARENT_LOCATOR, locator);
  }

  @Override
  public String getParentLocator() {
	  return data.getAsString(IConversationNode.PARENT_LOCATOR);
  }

  @Override
  public void setSmallIcon(String iconPath) {
    data.put(IConversationNode.ICON, iconPath);
  }

  @Override
  public String getSmallIcon() {
    return data.getAsString(IConversationNode.ICON);
  }

  @Override
  public void setTranscluderLocator(String transcluderId) {
    data.put(IConversationNode.TRANSCLUDER_ID, transcluderId);
  }

  @Override
  public String getTranscluderId() {
    return data.getAsString(IConversationNode.TRANSCLUDER_ID);
  }

  @Override
  public String toJSON() {
    return data.toJSONString();
  }

  @Override
  public void setSubject(String subject) {
    data.put(IConversationNode.SUBJECT, subject);
  }

  @Override
  public String getSubject() {
    return data.getAsString(IConversationNode.SUBJECT);
  }

	@Override
	public void setDetails(String details) {
	    data.put(IConversationNode.DETAILS, details);
	}

	@Override
	public String getDetails() {
	    return data.getAsString(IConversationNode.DETAILS);
	}

  @Override
  public JSONObject getData() {
    return data;
  }


	@Override
	public void addChild(JSONObject kid) {
		List<JSONObject> l = listChildren();
		l.add(kid);
		data.put(IConversationNode.CHILD_LIST, l);
	}
	
	@Override
	public List<JSONObject> listChildren() {
		List<JSONObject> result = (List<JSONObject>)data.get(IConversationNode.CHILD_LIST);
		if (result == null)
			result = new ArrayList<JSONObject>();
		return result;
	}

	@Override
	public void setNodeType(String type) {
	    data.put(IConversationNode.NODE_TYPE, type);
	}

	@Override
	public String getNodeType() {
		return data.getAsString(IConversationNode.NODE_TYPE);
	}

}
