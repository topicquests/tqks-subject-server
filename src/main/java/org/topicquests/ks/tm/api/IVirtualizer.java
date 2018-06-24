/**
 * Copyright 2015...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import java.util.Map;

import org.topicquests.support.api.IResult;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.api.ITicket;
/**
 * @author park
 *
 */
public interface IVirtualizer {

  /**
   * <p>We are here because two proxies have been determined to be merged.
   * It may be that an appropriate <em>VirtualNode</em> already exists,
   * in which case that will be used</p>
   * <p>Create a <em>VirtualNode</em></p>
   * <p>Behavior is to first build a <code>MergeProvenanceType</code> node
   * instance given <code>mergeReason</code> and <code>mergeData</code>,
   * then pass that to <code>BaseVirtualizer.wireMerge</code></p>
   * @param primary
   * @param merge
   * @param mergeData
   * @param confidence
   * @param userLocator
   * @param mergeReason determined by merge agents, typically by merge rules
   * @param credentials TODO
   * @return returns the locator of the created VirtualNode
   */
  IResult createVirtualNode(IProxy primary, IProxy merge,
                            Map<String,Double> mergeData, double confidence,
                            String userLocator, String mergeReason, ITicket credentials);
	
  /**
   * Init allows us to use different implementations
   * @param env
   */
  void init(SystemEnvironment env);
}
