/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import java.util.List;

/**
 * 
 * @author jackpark
 * Modeled after
 *  https://github.com/tinkerpop/blueprints/blob/master/blueprints-core/src/main/java/com/tinkerpop/blueprints/VertexQuery.java
 */
public interface IVertexQuery extends IQuery {

  /**
   * The direction of the edges to retrieve.
   *
   * @param direction whether to retrieve the incoming, outgoing, or both directions
   * @return the modified query object
   */
  IVertexQuery direction(String direction);
    
  /**
   * Filter out the edge if its label is not in set of provided labels.
   *
   * @param labels the labels to check against
   * @return the modified query object
   */
  IVertexQuery labels(String... labels);
    
  /**
   * Execute the query and return the number of edges that are unfiltered.
   *
   * @return the number of unfiltered edges
   */
  long count();
    
  /**
   * Return the raw ids of the vertices on the other end of the edges.
   *
   * @return the raw ids of the vertices on the other end of the edges
   */
  List<String> vertexIds();
    
  @Override
  IVertexQuery has(String key);

  @Override
  IVertexQuery hasNot(String key);

  @Override
  IVertexQuery has(String key, Object value);

  @Override
  IVertexQuery hasNot(String key, Object value);

  @Override
  IVertexQuery limit(int limit);
}
