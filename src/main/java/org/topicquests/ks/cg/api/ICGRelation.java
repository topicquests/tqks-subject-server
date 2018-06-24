/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg.api;
import org.topicquests.ks.api.ITicket;

/**
 * @author jackpark
 *
 */
public interface ICGRelation extends ICGNode {

	////////////////////
	// TODO
	// Notio refers to Arguments -- in and out
	//  but it refers to collections of those
	//     higher cardinality than 1
	////////////////////
	
	void setRelationType(String typeLocator);
	
	String getRelationTypeLocator();
	
	void setSourceNodeLocator(String sourceLocator);
	
	ICGNode getSourceNode(ITicket credentials);
	
	void setTargetNodeLocator(String targetLocator);
	
	ICGNode getTargetNode(ITicket credentials);
	
	/**
	 * Returns <code>true</code> if <code>con</code>
	 * is an argument in this relation
	 * @param con
	 * @return
	 */
	boolean relatesConcept(ICGConcept con);
}
