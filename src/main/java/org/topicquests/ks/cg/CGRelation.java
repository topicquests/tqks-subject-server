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
import org.topicquests.ks.cg.api.ICGNode;
import org.topicquests.ks.cg.api.ICGOntology;
import org.topicquests.ks.cg.api.ICGRelation;
import org.topicquests.ks.tm.Proxy;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class CGRelation extends CGNode implements ICGRelation {

	/**
	 * @param jo
	 */
	public CGRelation(JSONObject jo) {
		super(jo);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGRelation#setRelationType(java.lang.String)
	 */
	@Override
	public void setRelationType(String typeLocator) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGRelation#getRelationTypeLocator()
	 */
	@Override
	public String getRelationTypeLocator() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGRelation#setSourceNode(org.topicquests.ks.cg.api.ICGNode)
	 */
	@Override
	public void setSourceNodeLocator(String sourceLocator) {
		super.setProperty(ICGOntology.RELATION_SOURCE_LOCATOR, sourceLocator);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGRelation#getSourceNode()
	 */
	@Override
	public ICGNode getSourceNode(ITicket credentials) {
		ICGNode result = null;
		String lox = (String)super.getProperty(ICGOntology.RELATION_SOURCE_LOCATOR);
		if (lox != null) {
			IResult r = database.getFullNode(lox, credentials);
			IProxy p = (IProxy)r.getResultObject();
			if (p != null)
				result = new CGNode(p.getData());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGRelation#setTargetNode(org.topicquests.ks.cg.api.ICGNode)
	 */
	@Override
	public void setTargetNodeLocator(String targetLocator) {
		super.setProperty(ICGOntology.RELATION_TARGET_LOCATOR, targetLocator);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGRelation#getTargetNode()
	 */
	@Override
	public ICGNode getTargetNode(ITicket credentials) {
		ICGNode result = null;
		String lox = (String)super.getProperty(ICGOntology.RELATION_TARGET_LOCATOR);
		if (lox != null) {
			IResult r = database.getFullNode(lox, credentials);
			IProxy p = (IProxy)r.getResultObject();
			if (p != null)
				result = new CGNode(p.getData());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGRelation#relatesConcept(org.topicquests.ks.cg.api.ICGConcept)
	 */
	@Override
	public boolean relatesConcept(ICGConcept con) {
		// TODO Auto-generated method stub
		return false;
	}

}
