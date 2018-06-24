/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm;

import java.util.*;

import org.topicquests.ks.tm.api.ITreeNode;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONArray;


/**
 * @author park
 *
 */
public class TreeNode implements ITreeNode {
  private String locator = "";
  private String label = "no label";
  private List<ITreeNode>subs = null;
  private List<ITreeNode>instances = null;
  /**
   * 
   */
  public TreeNode() {
  }
	
  public TreeNode(String locator) {
    setNodeLocator(locator);
  }
	
  public TreeNode(String locator, String label) {
    setNodeLocator(locator);
    setNodeLabel(label);
  }

  /* (non-Javadoc)
   * @see org.topicquests.topicmap.json.model.api.ITreeNode#setNodeLocator(java.lang.String)
   */
  @Override
  public void setNodeLocator(String locator) {
    this.locator = locator;
  }

  /* (non-Javadoc)
   * @see org.topicquests.topicmap.json.model.api.ITreeNode#getNodeLocator()
   */
  @Override
  public String getNodeLocator() {
    return locator;
  }

  /* (non-Javadoc)
   * @see org.topicquests.topicmap.json.model.api.ITreeNode#setNodeLabel(java.lang.String)
   */
  @Override
  public void setNodeLabel(String label) {
    this.label = label;
  }

  /* (non-Javadoc)
   * @see org.topicquests.topicmap.json.model.api.ITreeNode#getNodeLabel()
   */
  @Override
  public String getNodeLabel() {
    return label;
  }

  /* (non-Javadoc)
   * @see org.topicquests.topicmap.json.model.api.ITreeNode#addSubclassChild(org.topicquests.topicmap.json.model.api.ITreeNode)
   */
  @Override
  public void addSubclassChild(ITreeNode c) {
    if (subs == null)
      subs = new ArrayList<ITreeNode>();
    subs.add(c);
  }

  /* (non-Javadoc)
   * @see org.topicquests.topicmap.json.model.api.ITreeNode#addInstanceChild(org.topicquests.topicmap.json.model.api.ITreeNode)
   */
  @Override
  public void addInstanceChild(ITreeNode c) {
    if (instances == null)
      instances = new ArrayList<ITreeNode>();
    instances.add(c);
  }

  /* (non-Javadoc)
   * @see org.topicquests.topicmap.json.model.api.ITreeNode#listSubclassChildNodes()
   */
  @Override
  public List<ITreeNode> listSubclassChildNodes() {
    return subs;
  }

  /* (non-Javadoc)
   * @see org.topicquests.topicmap.json.model.api.ITreeNode#listInstanceChildNodes()
   */
  @Override
  public List<ITreeNode> listInstanceChildNodes() {
    return instances;
  }

  @Override
  public JSONObject simpleToJSON() {
    JSONObject jo = new JSONObject();
    ITreeNode k;
    Iterator<ITreeNode>itr;

    jo.put("locator", this.locator);
    jo.put("label", label);
    if (subs != null) {
      JSONArray subs_array = new JSONArray();
      itr = subs.iterator();
      while (itr.hasNext()) {
        k = itr.next();
        subs_array.add(k.simpleToJSON());
      }
      jo.put("subs", subs_array);
    }

    if (instances != null) {
      JSONArray instance_array = new JSONArray();
      itr = instances.iterator();
      while (itr.hasNext()) {
        k = itr.next();
        instance_array.add(k.simpleToJSON());
      }
      jo.put("instances", instance_array);
    }

    return jo;
  }

  @Override
  public String simpleToXML() {
    StringBuilder buf = new StringBuilder();
    buf.append("<node locator=\""+locator+"\">\n");
    buf.append("<label>"+label+"</label>\n");
    ITreeNode k;
    Iterator<ITreeNode>itr;
    if (subs != null) {
      buf.append("<subs>\n");
      itr = subs.iterator();
      while (itr.hasNext()) {
        k = itr.next();
        buf.append(k.simpleToXML());
      }
      buf.append("</subs>\n");
    }
    if (instances != null) {
      buf.append("<instances>\n");
      itr = instances.iterator();
      while (itr.hasNext()) {
        k = itr.next();
        buf.append(k.simpleToXML());
      }
      buf.append("</instances>\n");
    }
    buf.append("</node>");
    return buf.toString();
  }

  @Override
  public int getSubclassCount() {
    if (this.subs != null)
      return subs.size();
    return 0;
  }

  @Override
  public int getInstanceCount() {
    if (this.instances != null)
      return instances.size();
    return 0;
  }

}
