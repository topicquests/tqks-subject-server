/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

/**
 * @author jackpark
 * Modeled after
 *  https://github.com/tinkerpop/blueprints/blob/master/blueprints-core/src/main/java/com/tinkerpop/blueprints/GraphQuery.java
 */
public interface IGraphQuery extends IQuery {
  @Override
  IGraphQuery has(String key);

  @Override
  IGraphQuery hasNot(String key);

  @Override
  IGraphQuery has(String key, Object value);

  @Override
  IGraphQuery hasNot(String key, Object value);
    
  @Override
  IGraphQuery limit(int limit);
    
  ////////////////
  // TODO
  // Ignored some queries with Predicate or comparable
  ///////////////
}
