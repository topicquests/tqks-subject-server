/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import net.minidev.json.JSONObject;

/**
 * @author Admin
 *
 */
public interface IMergeThread {
  void evaluateNewTopic(String topicLocator);
	
  void evaluateNewTopic(JSONObject newTopic);
	
  /**
   * <p>All methods lead here.</p>
   * <p>If the given <code>newTopic<code> can be merged, it 
   * will be merged.</p>
   * @param newTopic
   */
  void evaluateNewTopic(IProxy newTopic);
	
  void shutDown();
}
