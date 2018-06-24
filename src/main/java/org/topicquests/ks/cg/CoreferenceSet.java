/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg;

import java.util.List;
import java.util.Map;

import org.topicquests.ks.cg.api.ICGGraph;
import org.topicquests.ks.cg.api.ICGNode;
import org.topicquests.ks.cg.api.ICoreferenceSet;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class CoreferenceSet extends JSONObject implements ICoreferenceSet {

	/**
	 * 
	 */
	public CoreferenceSet() {
	}

	/**
	 * @param map
	 */
	public CoreferenceSet(Map<String, ?> map) {
		super(map);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#setDefiningLabel(java.lang.String)
	 */
	@Override
	public void setDefiningLabel(String label) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#getDefiningLabel()
	 */
	@Override
	public String getDefiningLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#setDefiningConcept(org.topicquests.ks.cg.api.ICGNode)
	 */
	@Override
	public void setDefiningConcept(ICGNode con) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#getDefiningConcept()
	 */
	@Override
	public ICGNode getDefiningConcept() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#getDominantGraph()
	 */
	@Override
	public ICGGraph getDominantGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#addConcept(org.topicquests.ks.cg.api.ICGNode)
	 */
	@Override
	public void addConcept(ICGNode con) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#removeConcept(org.topicquests.ks.cg.api.ICGNode)
	 */
	@Override
	public void removeConcept(ICGNode con) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#hasConcept(org.topicquests.ks.cg.api.ICGNode)
	 */
	@Override
	public boolean hasConcept(ICGNode con) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#hasDominantConcept(org.topicquests.ks.cg.api.ICGNode)
	 */
	@Override
	public boolean hasDominantConcept(ICGNode con) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#listDominantConcepts()
	 */
	@Override
	public List<ICGNode> listDominantConcepts() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#listSubordinateConcepts()
	 */
	@Override
	public List<ICGNode> listSubordinateConcepts() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICoreferenceSet#listConcepts()
	 */
	@Override
	public List<ICGNode> listConcepts() {
		// TODO Auto-generated method stub
		return null;
	}

}
