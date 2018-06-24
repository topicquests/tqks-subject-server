/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
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
public class CGNode extends Proxy implements ICGNode {

	/**
	 * @param jo
	 */
	public CGNode(JSONObject jo) {
		super(jo);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#setTopicReferenceLocator(java.lang.String)
	 */
	@Override
	public void setTopicReferenceLocator(String locator) {
		super.setProperty(ICGOntology.TOPIC_REFERENCE_LOCATOR, locator);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#getTopicReferenceLocator()
	 */
	@Override
	public String getTopicReferenceLocator() {
		return (String)super.getProperty(ICGOntology.TOPIC_REFERENCE_LOCATOR);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#setLatticeType(java.lang.String)
	 */
	@Override
	public void setLatticeType(String typeLocator) {
		super.setProperty(ICGOntology.LATTICE_TYPE, typeLocator);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#getLatticeTypeLocator()
	 */
	@Override
	public String getLatticeTypeLocator() {
		return (String)super.getProperty(ICGOntology.LATTICE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#setEnclosingGraph(org.topicquests.ks.cg.api.ICGGraph)
	 */
	@Override
	public void setEnclosingGraph(ICGGraph graph) {
		super.setProperty(ICGOntology.ENCLOSING_GRAPH_LOCATOR, graph.getLocator());

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#getEnclosingGraph(org.topicquests.ks.api.ITicket)
	 */
	@Override
	public ICGGraph getEnclosingGraph(ITicket credentials) {
		// TODO Start a connection to save roundtrips
		ICGGraph result = null;
		String graphLox = (String)super.getProperty(ICGOntology.ENCLOSING_GRAPH_LOCATOR);
		if (graphLox != null) {
			IResult r = database.getFullNode(graphLox, credentials);
			IProxy p = (IProxy)r.getResultObject();
			if (p != null)
				result = new CGGraph(p.getData());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#isEnclosedByGraph(java.lang.String)
	 */
	@Override
	public boolean isEnclosedByGraph(String graphLocator) {
		String graphLox = (String)super.getProperty(ICGOntology.ENCLOSING_GRAPH_LOCATOR);
		if (graphLox != null)
			return graphLox.equals(graphLocator);
		return false;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#addOutEdge(org.topicquests.ks.cg.api.ICGRelation)
	 */
	@Override
	public void addOutEdge(ICGRelation outEdge) {
		super.addPropertyValue(ICGOntology.OUT_EDGE_LOCATOR_LIST, outEdge.getLocator());
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#removeOutEdge(java.lang.String)
	 */
	@Override
	public void removeOutEdge(String edgeLocator) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#listOutEdges(org.topicquests.ks.api.ITicket)
	 */
	@Override
	public List<ICGRelation> listOutEdges(ITicket credentials) {
		List<ICGRelation> result = null;
		List<String> l = (List<String>)super.getProperty(ICGOntology.OUT_EDGE_LOCATOR_LIST);
		// TODO use a database connection -- this is slow as is
		if (l != null && !l.isEmpty()) {
			result = new ArrayList<ICGRelation>();
			IResult r;
			IProxy p;
			ICGRelation cr;
			Iterator<String> itr = l.iterator();
			while (itr.hasNext()) {
				r = database.getFullNode(itr.next(), credentials);
				p = (IProxy)r.getResultObject();
				if (p != null) {
					cr = new CGRelation(p.getData());
					result.add(cr);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#addInEdge(org.topicquests.ks.cg.api.ICGRelation)
	 */
	@Override
	public void addInEdge(ICGRelation inEdge) {
		super.addPropertyValue(ICGOntology.IN_EDGE_LOCATOR_LIST, inEdge.getLocator());
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#removeInEdge(java.lang.String)
	 */
	@Override
	public void removeInEdge(String edgeLocator) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#listInEdges(org.topicquests.ks.api.ITicket)
	 */
	@Override
	public List<ICGRelation> listInEdges(ITicket credentials) {
		List<ICGRelation> result = null;
		List<String> l = (List<String>)super.getProperty(ICGOntology.IN_EDGE_LOCATOR_LIST);
		// TODO use a database connection -- this is slow as is
		if (l != null && !l.isEmpty()) {
			result = new ArrayList<ICGRelation>();
			IResult r;
			IProxy p;
			ICGRelation cr;
			Iterator<String> itr = l.iterator();
			while (itr.hasNext()) {
				r = database.getFullNode(itr.next(), credentials);
				p = (IProxy)r.getResultObject();
				if (p != null) {
					cr = new CGRelation(p.getData());
					result.add(cr);
				}
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#addProvenanceLocator(java.lang.String)
	 */
	@Override
	public void addProvenanceLocator(String locator) {
		super.addPropertyValue(ITQCoreOntology.SCOPE_LIST_PROPERTY_TYPE, locator);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGNode#listProvenanceLocators()
	 */
	@Override
	public List<String> listProvenanceLocators() {
		return (List<String>)super.getProperty(ITQCoreOntology.SCOPE_LIST_PROPERTY_TYPE);
	}

}
