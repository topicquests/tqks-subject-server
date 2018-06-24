/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import org.topicquests.pg.api.IPostgresConnection;

/**
 * 
 * @author jackpark
 * Modeled after
 *  https://github.com/tinkerpop/blueprints/blob/master/blueprints-core/src/main/java/com/tinkerpop/blueprints/Edge.java
 */
public interface IEdge extends IProxy {
  public static final String DIRECTION_IN = "InD",
    DIRECTION_OUT	= "OutD",
    DIRECTION_BOTH	= "BothD";
	
  IVertex getVertex(IPostgresConnection conn, String direction);
}
