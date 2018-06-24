/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;
import org.topicquests.pg.api.IPostgresConnection;

/**
 * @author jackpark
 * Modeled after
 *  https://github.com/tinkerpop/blueprints/blob/master/blueprints-core/src/main/java/com/tinkerpop/blueprints/Vertex.java
 */
public interface IVertex extends IProxy {
	
  //////////////////
  // TODO
  // Since edges are a part of proxies now, perhaps
  // edge stuff should migrate to IProxy?
  /////////////////
  /**
   * Return the edges incident to the vertex according to the provided direction and edge labels.
   * @param conn TODO
   * @param direction the direction of the edges to retrieve
   * @param labels    the labels of the edges to retrieve
   *
   * @return an iterable of incident edges
   */
  Iterable<IEdge> getEdges(IPostgresConnection conn, String direction, String... labels);
	
  /**
   * Return the vertices adjacent to the vertex according to the provided direction and edge labels.  This
   * method does not remove duplicate vertices (i.e. those vertices that are connected by more than one edge).
   * @param conn TODO
   * @param direction the direction of the edges of the adjacent vertices
   * @param labels    the labels of the edges of the adjacent vertices
   *
   * @return an iterable of adjacent vertices
   */
  Iterable<IVertex> getVertices(IPostgresConnection conn, String direction, String... labels);

  IVertexQuery vertexQuery();
    
  IVertexQuery vertexQuery(IPostgresConnection conn);
    
  /**
   * Add a new outgoing edge from this vertex to the parameter vertex with provided edge label.
   * @param conn TODO
   * @param id
   * @param label    the label of the edge
   * @param language
   * @param inVertex the vertex to connect to with an incoming edge
   * @return the newly created edge
   */
  IEdge addEdge(IPostgresConnection conn, String id, String label, String language, IVertex inVertex);
    
}
