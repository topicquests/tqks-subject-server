/**
 * Copyright 2013...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import java.util.Map;

import org.topicquests.support.api.IResult;
import org.topicquests.ks.SystemEnvironment;

/**
 * @author park
 * <p>This <em>merge agent</em> is passed a topic, which will be
 * examined for need to merge.</p>
 * <p>If a merge is detected, that merge is performed</p>
 */
public interface IMergeImplementation {

  /**
   * Initialize the engine
   * @param environment
   */
  void init(SystemEnvironment environment);

	
  /**
   * Set the INodeModel
   * @param m
   */
  void setNodeModel(IProxyModel m);
	
  /**
   * <p>Assert a merge, which fires up a VirtualProxy, creates a MergeAssertion node (not a triple)
   * and adds the list of rule locators to the merge assertion proxy</p>
   * <p>The merge must be mindful of a nodes place in some graph. If the node has
   * a parent node of some time, then the VirtualProxy must substitute for that,
   * and both nodes must be removed as child nodes; the VirtualProxy always stands
   * for those nodes in that graph.</p>
   * @param sourceNodeLocator
   * @param targetNodeLocator
   * @param mergeData
   * @param mergeConfidence
   * @param virtualizer TODO
   * @param userLocator
   * @return
   */
  IResult assertMerge(String sourceNodeLocator, String targetNodeLocator, Map<String, Double> mergeData,
                      double mergeConfidence, IVirtualizer virtualizer, String userLocator);
	
}
