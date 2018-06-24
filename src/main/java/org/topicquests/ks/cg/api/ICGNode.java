/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg.api;

import java.util.List;

import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.api.ITicket;

/**
 * @author jackpark
 * A Base Object for all of the ConceptualGraph objects
 */
public interface ICGNode extends IProxy {

	/**
	 * <p>CG objects are <em>anchored</em> in topics in the topic map</p>
	 * <p>Those topics are always <em>in</em> a <em>type lattice</em></p>
	 * @param locator
	 */
	void setTopicReferenceLocator(String locator);
	
	String getTopicReferenceLocator();
	
	/**
	 * While the referenced topic is in a type lattice, it might also be
	 * a subclass or instance of some other ontological entity. So, we
	 * are obliged to specify which type serves this context.
	 * @param typeLocator
	 */
	void setLatticeType(String typeLocator);
	
	String getLatticeTypeLocator();
	
	/**
	 * An ICGNode can be enclosed in one and only one ICGGraph
	 * @param graph
	 */
	void setEnclosingGraph(ICGGraph graph);
	
	ICGGraph getEnclosingGraph(ITicket credentials);
	
	/**
	 * Return <code>true</code> if this object is enclosed by <code>graph</code>
	 * @param graphLocator
	 * @return
	 */
	boolean isEnclosedByGraph(String graphLocator);
	
	/////////////////////////
	// ICGConcept and ICGGraph can have edges
	// They are put here for convenience.
	/////////////////////////
	
	void addOutEdge(ICGRelation outEdge);
	
	void removeOutEdge(String edgeLocator);
	
	List<ICGRelation> listOutEdges(ITicket credentials);
	
	void addInEdge(ICGRelation inEdge);
	
	void removeInEdge(String edgeLocator);
	
	List<ICGRelation> listInEdges(ITicket credentials);
	
	/////////////////////////
	// Provenance and scope
	/////////////////////////
	
	void addProvenanceLocator(String locator);
	
	List<String> listProvenanceLocators();
}
