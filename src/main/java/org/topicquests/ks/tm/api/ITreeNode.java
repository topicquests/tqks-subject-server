/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import java.util.List;
import net.minidev.json.JSONObject;

/**
 * @author park
 *
 */
public interface ITreeNode {

  void setNodeLocator(String locator);
  String getNodeLocator();
	
  void setNodeLabel(String label);
  String getNodeLabel();
	
  void addSubclassChild(ITreeNode c);
	
  void addInstanceChild(ITreeNode c);
	
  int getSubclassCount();
	
  int getInstanceCount();
	
  /**
   * Can return <code>null</code>
   * @return
   */
  List<ITreeNode> listSubclassChildNodes();
	
  /**
   * Can return <code>null</code>
   * @return
   */
  List<ITreeNode> listInstanceChildNodes();
	
  JSONObject simpleToJSON();
  String simpleToXML();
}
