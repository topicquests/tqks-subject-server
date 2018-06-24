/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg.api;

import java.util.List;

import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;


/**
 * @author jackpark
 * Many of the ideas here came from Notio's Graph object
 */
public interface ICGGraph extends ICGNode {

	/**
	 * <p>Add a concept to this graph</p
	 * <p>If the concept is in the graph, do nothing</p>
	 * <p>If the concept is already in another graph, throw an Exception</p>
	 * @param newConcept
	 * @throws Exception
	 */
	void addConcept(ICGNode newConcept) throws Exception;
	
	void removeConcept(String conceptLocator);
	
	void replaceConcept(ICGConcept oldConcept, ICGConcept newConcept) throws Exception;
	
	List<ICGConcept> listConcepts(ITicket credentials);
	
	/**
	 * <p>Add a relation to this graph</p>
	 * <p>Add the relation's source and target concepts to the graph</p>
	 * <p>If the relation is in the graph, do nothing</p>
	 * <p>If the relation is already in another graph, throw an Exception</p>
	 * <p>If either of the relation's concepts are in another graph, throw an Exception</p>
	 * @param newRelation
	 * @throws Exception
	 */
	void addRelation(ICGRelation newRelation) throws Exception;
	
	void removeRelation(String relationLocator);
	
	List<ICGRelation> listRelations(ITicket credentials);
	
	//////////////////////////
	// Coreferences
	/////////////////////////
	
	/**
	 * <p>In a given graph, it is possible that a given topic
	 * can be represented by several different concepts throughout the map
	 * even in nested graphs.</p>
	 * <p>Here, we are tracking, for each nested graph, if any,
	 * the <code>conceptLocator</code>-<code>topicLocator</code> collection pairs
	 * </p>
	 * <p>Use this on the outer containing graph</p>
	 * @param topicLocator
	 * @param conceptLocator
	 */
	void addCoreference(String topicLocator, String conceptLocator);
	
	/**
	 * 
	 * @param topicLocator
	 * @return can return <code>null</code>
	 */
	List<String> getCoreferences(String topicLocator);
	
	/**
	 * Can return <code>null</code>
	 * @return
	 */
	JSONObject getCoreferences();
	
	/////////////////////////
	// TODO
	// Notio's Graph.java talks about
	//   getContext which returns a Concept
	//   getEnclosingGraph we already do that
	////////////////////////
	
	/////////////////////
	// Turn a graph into its own DSL
	/////////////////////
	
	/**
	 * Create a new {@link ICGGraph} to nest within this graph
	 * @param graphLocator can be <code>null</code>
	 * @param label
	 * @param details
	 * @param language
	 * @param userLocator
	 * @param provenanceLocator  can but should not be <code>null</code>
	 * @return returns the new graph
	 */
	IResult newGraph(String graphLocator, String label, String details, String language, String userLocator, String provenanceLocator);

	/**
	 * Create a new {@link ICGConcept} to be nested within this graph
	 * @param conceptLocator	can be <code>null</code>
	 * @param topicLocator must NOT be <code>null</code>
	 * @param latticeTypeLocator
	 * @param label
	 * @param details
	 * @param language
	 * @param userLocator
	 * @param provenanceLocator  can but should not be <code>null</code>
	 * @param ref
	 * @return returns both the concept and the graph (in getResultObjectA)
	 */
	IResult newConcept(String conceptLocator, String topicLocator, String latticeTypeLocator, String label, String details, String language, String userLocator, String provenanceLocator, IReferent ref);

	/**
	 * Create a new {@link ICGRelation} to be nested in this graph
	 * @param relationType
	 * @param label
	 * @param details
	 * @param language
	 * @param userLocator
	 * @param provenanceLocator can but should not be <code>null</code>
	 * @param sourceNodeLocator
	 * @param targetNodeLocator
	 * @return returns both the relation and the graph (in getResultObjectA)
	 */
	IResult newRelation(String relationType, String label, String details, String language, String userLocator, String provenanceLocator, String sourceNodeLocator, String targetNodeLocator);

	/**
	 * Relate this graph to a <code>topic</code>
	 * @param topic
	 * @param userLocator 
	 * @param provenanceLocator can but should not be <code>null</code>
	 * @return
	 */
	IResult relateGraphToTopic(IProxy topic, String userLocator, String provenanceLocator);
}
