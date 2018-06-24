/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

/**
 * @author jackpark
 * Modeled after
 *  https://github.com/tinkerpop/blueprints/blob/master/blueprints-core/src/main/java/com/tinkerpop/blueprints/Query.java
 */
public interface IQuery {

  /**
   * Filter out elements that do not have a property with provided key.
   *
   * @param key the key of the property
   * @return the modified query object
   */
  IQuery has(String key);
    
  /**
   * Filter out elements that have a property with provided key.
   *
   * @param key the key of the property
   * @return the modified query object
   */
  IQuery hasNot(String key);
    
  /**
   * Filter out elements that do not have a property value equal to provided value.
   *
   * @param key   the key of the property
   * @param value the value to check against
   * @return the modified query object
   */
  IQuery has(String key, Object value);
    
  /**
   * Filter out elements that have a property value equal to provided value.
   *
   * @param key   the key of the property
   * @param value the value to check against
   * @return the modified query object
   */
  IQuery hasNot(String key, Object value);
    
  /////////////////
  //TODO left out some that use predicates and comparable
  /////////////////
    
  /**
   * Filter out the element if the take number of incident/adjacent elements to retrieve has already been reached.
   *
   * @param limit the take number of elements to return
   * @return the modified query object
   */
  IQuery limit(int limit);
    
  /**
   * Execute the query and return the matching edges.
   *
   * @return the unfiltered incident edges
   */
  Iterable<IEdge> edges();
    
  /**
   * Execute the query and return the vertices on the other end of the matching edges.
   *
   * @return the unfiltered adjacent vertices
   */
  Iterable<IVertex> vertices();
}
