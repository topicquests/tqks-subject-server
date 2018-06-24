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
public interface ISameLabelListener {

  /**
   * When <em>same label</em> study results in a merge,
   * the <code>virtualNodeLocator</code> plus the original
   * locators are returned
   * @param virtualNodeLocator
   * @param nodeALocator
   * @param nodeBLocator
   * @param errorMessages
   */
  void acceptSameLabelResults(String virtualNodeLocator, 
                              String nodeALocator,
                              String nodeBLocator,
                              String errorMessages);
}
