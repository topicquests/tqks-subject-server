/**
 * Copyright 2015...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

/**
 * @author park
 *
 */
public interface IMergeResultsListener {

  /**
   * <p>Returned only if a merge was performed on
   * <code>originalLocator</code> and any other node</p>
   * <p>This method is premised on the idea that one and only one
   * topic is put up for merge; the {@link IMergeEngine} does that.</p>
   * @param virtualNodeLocator
   * @param originalLocator
   * @param errorMessages
   */
  void acceptMergeResults(String virtualNodeLocator,
                          String originalLocator,
                          String errorMessages);
	
  /**
   * Returned only if <em>no</em> merge was performed on the proxy
   * identified by <code>topicLocator</code>
   * @param topicLocator
   * @param errorMessages
   */
  void acceptMergeResults(String topicLocator, String errorMessages);
}
