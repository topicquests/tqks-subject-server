/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

/**
 * @author park
 *
 */
public interface IMergeEngine extends IMergeResultsListener {
  /**
   * <p>If the given <code>newTopic<code> can be merged, it 
   * will be merged.</p>
   * @param newTopic
   */
  void evaluateNewTopic(IProxy newTopic);
	
  void shutDown();
}
