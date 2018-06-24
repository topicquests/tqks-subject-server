/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg.api;

import java.util.List;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public interface ICGConcept extends ICGNode {

	////////////////////
	// TODO
	// CoreferenceSets
	//   This is an important aspect of concepts:
	//     they can refer to others in a graph
	////////////////////
	
	/**
	 * <p>Can also be set in the constructor</p>
	 * <p>When set, this concept becomes the <code>newReferent</code>'s 
	 * enclosing concept</p>
	 * @param newReferent
	 */
	void setReferent(IReferent newReferent);
	/**
	 * 
	 * @return
	 */
	IReferent getReferent();
	
	void addCoreferenceSet(ICoreferenceSet set);
	
	void removeCoreferenceSet(ICoreferenceSet set);
	
	//ICoreferenceSet getCoreferenceSet();
	
	List<ICoreferenceSet> listCoreferenceSets();
	
	/**
	 * Returns a union of all concepts in all coreference sets
	 * @return
	 */
	List<ICGConcept> listCoreferenceConcepts();
	
	/**
	 * Returns <code>true</code> if this concept is a dominant
	 * concept in any coreference set
	 * @return
	 */
	boolean isDominantConcept();
	
	/**
	 * Returns <code>true</code> if this concept is a defining concept
	 * in any coreference set
	 * @return
	 */
	boolean isDefiningConcept();
	
	/**
	 * <p>Returns <p>true</p> if this concept is a context.  A context is a concept
     * whose designator is a non-blank conceptual graph.
     * This method will return true if this concept has a referent which returns
     * true when its isContext() method is called.</p>
	 * @return
	 */
	boolean isContext();
	
	/////////////////////
	// Adapted from notio
	//  appear to be related to graph manipulation
	/////////////////////
	
	/**
	 * <p>Isolates this concept by removing it from all coreference sets
     * to which it belongs and by isolating any and all concepts that may
     * be nested within it.</p>
     * <p>Throws exception if removing a concept throws an exception
	 * @throws Exception
	 */
	void isolate() throws Exception;
	
	/**
	 * <p>Returns an array (possibly empty) of the relations in the enclosing
     * graph that relate this concept.  This method will return null if the
     * concept does not belong to any graph (getEnclosingGraph() returns null).</p>
	 * @return
	 */
	List<ICGRelation> listRelators();
	
	/**
	 * <p>Performs a copy operation on this concept according to the
     * the specified CopyingScheme.
     * The result may be a new object of exactly the same class as the original or simply 
     * a reference to this concept depending on the copying scheme.
     * Coreference sets will be copied as needed depending on the copying scheme.</p>
	 * @param scheme
	 * @param substitutionMap
	 * @return
	 */
	ICGConcept copy(ICopyScheme scheme, JSONObject substitutionMap);

	/**
	 * <p>Returns a new concept identical to this but restricted to the new type</p>
	 * <p>Throws exception if if subType is not a real subtype of the current type
	 * @param newSubType
	 * @return
	 * @throws Exception
	 */
	ICGConcept restrictToType(String newSubType) throws Exception;

	/**
	 * <p>Returns a new concept identical to this but restricted to the given
     * referent.<p>
     * <p>Throws Exception if there is already an equally specific referent</p>
	 * @param newReferentLocator
	 * @return
	 * @throws Exception
	 */
	ICGConcept restrictToReferent(String newReferentLocator) throws Exception;

	/**
	 * <p>Returns a new concept identical to this but restricted to the given
     * referent and subtype.</p>
     * <p>
     * <p>Throws Exception if subType is not a real sub-type of the current type
     *  and/or if there is already an equally specific referent.</p>
	 * @param newSubType
	 * @param newReferentLocator
	 * @return
	 * @throws Exception
	 */
	ICGConcept restrictToTypeAndReferent(String newSubType, String newReferentLocator) throws Exception;
}
