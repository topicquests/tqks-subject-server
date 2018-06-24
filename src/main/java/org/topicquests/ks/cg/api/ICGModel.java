/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg.api;

import org.topicquests.ks.api.ITicket;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public interface ICGModel {
	
	/**
	 * Deterministic way to make locators from Relation types
	 * @param reln
	 * @return
	 */
	String relationToLocator(String reln);

	/**
	 * Deterministic way to make locators from LatticeTypes
	 * @param type
	 * @return
	 */
	String latticeTypeToLocator(String type);

	/**
	 * <code>topicLocator</code> is the topic map topic for which
	 * this concept is a representative in a graph
	 * @param conceptLocator can be <code>null</code>
	 * @param topicLocator must NOT be <code>null</code> -- not true for cannonical graphs
	 * @param latticeTypeLocator
	 * @param label
	 * @param details
	 * @param language
	 * @param userLocator
	 * @param provenanceLocator
	 * @param ref
	 * @return
	 * @throws Exception
	 */
	ICGConcept newConceptInstance(String conceptLocator, String topicLocator, String latticeTypeLocator, String label, String details, String language, String userLocator, String provenanceLocator, IReferent ref) throws Exception;
	
	IResult getConcept(String conceptLocator, ITicket credentials);
	
	IResult removeConcept(String conceptLocator, ITicket credentials);
	
	//TODO what is this?
	IResult listConceptsForProxy(String proxyLocator, ITicket credentials);
	
	/**
	 * 
	 * @param graphLocator can be <code>null</code>
	 * @param label
	 * @param details
	 * @param language
	 * @param userLocator
	 * @param provenanceLocator
	 * @return
	 */
	ICGGraph newGraphInstance(String graphLocator, String label, String details, String language, String userLocator, String provenanceLocator);
	
	IResult getGraph(String graphLocator, ITicket credentials);
	
	/**
	 * Used for making <em>Relation Classes</em>
	 * @param relationLocator can NOT be <code>null</code>
	 * @param label
	 * @param details
	 * @param language
	 * @param userLocator
	 * @param provenanceLocator
	 * @return
	 */
	ICGRelation newRelation(String relationLocator, String label, String details, String language, String userLocator, String provenanceLocator);
	
	/**
	 * Used for making <em>Relation Instances</em> based on <code>relationType</code>
	 * @param relationType
	 * @param label
	 * @param details
	 * @param language
	 * @param userLocator
	 * @param provenanceLocator  can but should not be <code>null</code>
	 * @return
	 */
	ICGRelation newRelationInstance(String relationType, String label, String details, String language, String userLocator, String provenanceLocator);

	/**
	 * Relation instance with <code>sourceNodeLocator</code>
	 * and <code>targetNodeLocator</p>
	 * @param relationType
	 * @param label
	 * @param details
	 * @param language
	 * @param userLocator
	 * @param provenanceLocator  can but should not be <code>null</code>
	 * @param sourceNodeLocator
	 * @param targetNodeLocator
	 * @return
	 */
	ICGRelation newRelationInstance(String relationType, String label, String details, String language, String userLocator, String provenanceLocator,
			String sourceNodeLocator, String targetNodeLocator);

	
	
	IResult getRelation(String relationLocator, ITicket credentials);
	
	///////////////////
	// CG manipulation methods
	// 	Copy and Simplify
	// 	Restrict and Unrestrict
	// 	Join and Detach
	//
	//////////////////
	
	/**
	 * Return an exact copy of <code>node</code>
	 * @param node
	 * @return
	 */
	IResult copy(ICGGraph node);
	
	/**
	 * If conceptual relations r and s in the graph 
	 * <code>node</code are duplicates, then one of them may 
	 * be deleted together with all its arcs.
	 * @param node
	 * @return
	 */
	IResult simplify(ICGGraph node);
	
	/**
	 * Specialization: For any concept c in <code>node</code, 
	 * type(c) may be replaced by a subtype; 
	 * if c is generic, its referent may be changed to an individual marker. 
	 * These changes are permitted only if referent(c) 
	 * conforms to type(c) before and after the change.
	 * @param node
	 * @return
	 */
	IResult restrict(ICGConcept node);
	
	/**
	 * Generalization: reverse of Restrict
	 * @param node
	 * @return
	 */
	IResult unRestrict(ICGConcept node);
	
	/**
	 * Specialization: If a concept c in <code>nodeA</code> 
	 * is identical to a concept d in <code>nodeB</code>, 
	 * then return the graph obtained by deleting d and 
	 * linking to c all arcs of conceptual relations that had been linked to d.
	 * @param nodeA
	 * @param nodeB
	 * @return
	 */
	IResult join (ICGGraph nodeA, ICGGraph nodeB);
	
	/**
	 * No clue what this is, but it's in the literature
	 * @param nodeA
	 * @param nodeB
	 * @return
	 */
	IResult maximalJoin(ICGGraph nodeA, ICGGraph nodeB);
	
	/**
	 * Generalization: reverse of Join
	 * @param nodeA
	 * @param nodeB
	 * @return
	 */
	IResult detach(ICGGraph nodeA, ICGGraph nodeB);
}
