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
 * <p>An API for a pojo for a JSONObject. That is, a coreference set
 *  is a collection of data (locators) stored in a concept, and
 *  this pojo returns live data.</p>
 * <p>NOTE a CoreferenceSet extends JSONObject so it can be passed around</p>
 * <p>Note, we are passing around {@link ICGNode} rather than
 * {@link ICGConcept} to keep this as generalized as possible</p>
 * Based on Notio
 */
public interface ICoreferenceSet {

	void setDefiningLabel(String label);
	
	String getDefiningLabel();
	
	///////////////////
	//TODO
	//  define "dominant concept"
	///////////////////
	/**
	 * <p>Set the defining concept associated with this coreference set, and related to the defining relation</p>
	 * <p>The primary use for this feature is related to parsing and generating,
     * particularly as regards constructing arcs for relations.  The specified
     * concept must already be a dominant member of the coreference set.<p>
	 * <p>if the specified concept is not a dominant
     * concept in this set.</p>
	 * @param con
	 * @throws Exception
	 */
	void setDefiningConcept(ICGNode con) throws Exception;
	
	ICGNode getDefiningConcept();
	
	/**
	 * Return the graph containing the dominant nodes
	 * @return
	 */
	ICGGraph getDominantGraph();
	
	/**
	 * Number of concepts in this set
	 * @return
	 */
	int size();
	
	/**
	 * <p>Add a concept to this set</p>
	 * <p>If concept exists in this set, do nothing</p>
	 * <p> Throws exception if the specified concept is dominant in another
     * coreference set,
     * or if the specified concept would be dominant in this set but is a member of other 
     * sets,
     * or if the specified concept is not in the correct scope for this set,
     * or if the specified concept is not enclosed by any graph.<p>
	 * @param con
	 * @throws Exception
	 */
	void addConcept(ICGNode con) throws Exception;
	
	/**
	 * <p>Removes the specified concept from this coreference set.  
     * Note that this method will automatically clear the current defining concept
     * if it is removed from the set.</p>
     * <p>Throws exception if removal of the specified concept would result in
     * an invalid coreference set.</p>
	 * @param con
	 * @throws Exception
	 */
	void removeConcept(ICGNode con) throws Exception;
	
	boolean hasConcept(ICGNode con);
	
	boolean hasDominantConcept(ICGNode con);
	
	List<ICGNode> listDominantConcepts();
	
	List<ICGNode> listSubordinateConcepts();
	
	List<ICGNode> listConcepts();
	
	/**
	 * This object is actually a JSONObject
	 * @return
	 */
	String toJSONString();
	
}
