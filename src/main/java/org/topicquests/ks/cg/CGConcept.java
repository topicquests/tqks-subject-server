/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg;

import java.util.List;

import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.cg.api.ICGConcept;
import org.topicquests.ks.cg.api.ICGGraph;
import org.topicquests.ks.cg.api.ICGRelation;
import org.topicquests.ks.cg.api.ICopyScheme;
import org.topicquests.ks.cg.api.ICoreferenceSet;
import org.topicquests.ks.cg.api.IReferent;
import org.topicquests.ks.tm.Proxy;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class CGConcept extends CGNode implements ICGConcept {

	/**
	 * @param jo
	 */
	public CGConcept(JSONObject jo) {
		super(jo);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#setReferent(org.topicquests.ks.cg.api.IReferent)
	 */
	@Override
	public void setReferent(IReferent newReferent) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#getReferent()
	 */
	@Override
	public IReferent getReferent() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#addCoreferenceSet(org.topicquests.ks.cg.api.ICoreferenceSet)
	 */
	@Override
	public void addCoreferenceSet(ICoreferenceSet set) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#removeCoreferenceSet(org.topicquests.ks.cg.api.ICoreferenceSet)
	 */
	@Override
	public void removeCoreferenceSet(ICoreferenceSet set) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#listCoreferenceSets()
	 */
	@Override
	public List<ICoreferenceSet> listCoreferenceSets() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#listCoreferenceConcepts()
	 */
	@Override
	public List<ICGConcept> listCoreferenceConcepts() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#isDominantConcept()
	 */
	@Override
	public boolean isDominantConcept() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#isDefiningConcept()
	 */
	@Override
	public boolean isDefiningConcept() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#isContext()
	 */
	@Override
	public boolean isContext() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#isolate()
	 */
	@Override
	public void isolate() throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#listRelators()
	 */
	@Override
	public List<ICGRelation> listRelators() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#copy(org.topicquests.ks.cg.api.ICopyScheme, net.minidev.json.JSONObject)
	 */
	@Override
	public ICGConcept copy(ICopyScheme scheme, JSONObject substitutionMap) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#restrictToType(java.lang.String)
	 */
	@Override
	public ICGConcept restrictToType(String newSubType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#restrictToReferent(java.lang.String)
	 */
	@Override
	public ICGConcept restrictToReferent(String newReferentLocator) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGConcept#restrictToTypeAndReferent(java.lang.String, java.lang.String)
	 */
	@Override
	public ICGConcept restrictToTypeAndReferent(String newSubType, String newReferentLocator) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
