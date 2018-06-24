/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.topicquests.ks.api.ICoreIcons;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.cg.api.ICGConcept;
import org.topicquests.ks.cg.api.ICGGraph;
import org.topicquests.ks.cg.api.ICGNode;
import org.topicquests.ks.cg.api.ICGOntology;
import org.topicquests.ks.cg.api.ICGRelation;
import org.topicquests.ks.cg.api.IReferent;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * @author jackpark
 *
 */
public class CGGraph extends CGNode implements ICGGraph {

	/**
	 * @param jo
	 */
	public CGGraph(JSONObject jo) {
		super(jo);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGGraph#addConcept(org.topicquests.ks.cg.api.ICGConcept)
	 */
	@Override
	public void addConcept(ICGNode newConcept) throws Exception {
		//TODO run some tests
		super.addPropertyValue(ICGOntology.CG_CONCEPT_LIST, newConcept.getLocator());
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGGraph#removeConcept(java.lang.String)
	 */
	@Override
	public void removeConcept(String conceptLocator) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGGraph#replaceConcept(org.topicquests.ks.cg.api.ICGConcept, org.topicquests.ks.cg.api.ICGConcept)
	 */
	@Override
	public void replaceConcept(ICGConcept oldConcept, ICGConcept newConcept) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGGraph#listConcepts()
	 */
	@Override
	public List<ICGConcept> listConcepts(ITicket credentials) {
		List<ICGConcept> result = null;
		Object o = super.getProperty(ICGOntology.CG_CONCEPT_LIST);
		IResult r;
		IProxy p;
		ICGConcept cr;
		if (o != null && o instanceof List) {
			List<String> l = (List<String>)o; 

			environment.logDebug("CGGraph.listConcepts "+l);
			// TODO use a database connection -- this is slow as is
			if (l != null && !l.isEmpty()) {
				result = new ArrayList<ICGConcept>();
				Iterator<String> itr = l.iterator();
				while (itr.hasNext()) {
					r = database.getFullNode(itr.next(), credentials);
					p = (IProxy)r.getResultObject();
					if (p != null) {
						cr = new CGConcept(p.getData());
						result.add(cr);
					}
				}
			}
		} else if (o != null) {
			String lox = (String)o;
			result = new ArrayList<ICGConcept>();
			r = database.getFullNode(lox, credentials);
			p = (IProxy)r.getResultObject();
			if (p != null) {
				cr = new CGConcept(p.getData());
				result.add(cr);
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGGraph#addRelation(org.topicquests.ks.cg.api.ICGRelation)
	 */
	@Override
	public void addRelation(ICGRelation newRelation) throws Exception {
		//TODO run some tests
		super.addPropertyValue(ICGOntology.CG_RELATION_LIST, newRelation.getLocator());
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGGraph#removeRelation(java.lang.String)
	 */
	@Override
	public void removeRelation(String relationLocator) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.cg.api.ICGGraph#listRelations()
	 */
	@Override
	public List<ICGRelation> listRelations(ITicket credentials) {
		List<ICGRelation> result = null;
		Object o = super.getProperty(ICGOntology.CG_RELATION_LIST);
		IResult r;
		IProxy p;
		ICGRelation cr;
		if (o != null && o instanceof List) {
			List<String> l = (List<String>)o; 
			environment.logDebug("CGGraph.listRelations "+l);
			// TODO use a database connection -- this is slow as is
			if (l != null && !l.isEmpty()) {
				result = new ArrayList<ICGRelation>();

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
		} else if (o != null) {
			String lox = (String)o;
			result = new ArrayList<ICGRelation>();
			r = database.getFullNode(lox, credentials);
			p = (IProxy)r.getResultObject();
			if (p != null) {
				cr = new CGRelation(p.getData());
				result.add(cr);
			}
			
		}
		return result;
	}

	/////////////////
	// Graph DSL
	/////////////////
	@Override
	public IResult newGraph(String graphLocator, String label, String details, String language, String userLocator,
			String provenanceLocator) {
		IResult result = new ResultPojo();
		ICGGraph g = environment.getCGModel().newGraphInstance(graphLocator, label, details, language, userLocator, provenanceLocator);
		result.setResultObject(g);
		try {
			//Graphs are added to concepts
			this.addConcept(g);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		return result;
	}

	@Override
	public IResult newConcept(String conceptLocator, String topicLocator, String latticeTypeLocator, String label,
			String details, String language, String userLocator, String provenanceLocator, IReferent ref) {
		IResult result = new ResultPojo();
		try {
			ICGConcept c = environment.getCGModel().newConceptInstance(conceptLocator, topicLocator, latticeTypeLocator, label, details, language, userLocator, provenanceLocator, ref);
			environment.logDebug("CGGraph.newConcept "+c);
			result.setResultObject(c);
			result.setResultObjectA(this);
			this.addConcept(c);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}
		environment.logDebug("CGGraph.newConcept+ "+result.getResultObject()+" "+result.getResultObjectA());
		return result;
	}

	@Override
	public IResult newRelation(String relationType, String label, String details, String language,
			String userLocator, String provenanceLocator, String sourceNodeLocator, String targetNodeLocator) {
		IResult result = new ResultPojo();
		ICGRelation r = environment.getCGModel().newRelationInstance(relationType, label, details, language, userLocator, provenanceLocator);
		result.setResultObject(r);
		result.setResultObjectA(this);
		try {
			this.addRelation(r);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}		
		return result;
	}

	@Override
	public IResult relateGraphToTopic(IProxy topic, String userLocator, String provenanceLocator) {
		IResult result = environment.getProxyModel().relateExistingNodes(this, topic, ICGOntology.GRAPH_TOPIC_RELATION, 
				null, null, userLocator, provenanceLocator, ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, false, false);
		return result;
	}

	@Override
	public void addCoreference(String topicLocator, String conceptLocator) {
		List<String> result = null;
		JSONObject corefs = getCoreferences();
		if (corefs != null) {
			String oldVal = corefs.toJSONString();
				result = (List<String>)corefs.get(topicLocator);
				if (result != null) {
					if (!result.contains(conceptLocator))
						result.add(conceptLocator);
				} else {
					result = new ArrayList<String>();
					result.add(conceptLocator);
					corefs.put(topicLocator, result);
				}
				environment.logDebug("CGGraph.addCoreference "+oldVal);
				environment.logDebug("CGGraph.addCoreference-1 "+corefs);
				super.replacePropertyValue(ICGOntology.COREFERENCE_MAP, oldVal, corefs.toJSONString());
				super.replacePropertyValue(ICGOntology.STASH_COREF_MAP, oldVal, corefs.toJSONString());

		} else {
			corefs = new JSONObject();
			result = new ArrayList<String>();
			result.add(conceptLocator);
			corefs.put(topicLocator, result);
			super.setProperty(ICGOntology.COREFERENCE_MAP, corefs.toJSONString());
			super.setProperty(ICGOntology.STASH_COREF_MAP, corefs.toJSONString());			
		}
		environment.logDebug("CGGraph.addCoreference+ "+corefs);
	}

	@Override
	public List<String> getCoreferences(String topicLocator) {
		List<String> result = null;
		JSONObject corefs = getCoreferences();
		if (corefs != null) {
					result = (List<String>)corefs.get(topicLocator);
		}
		return result;
	}

	@Override
	public JSONObject getCoreferences() {
		JSONObject result = null;
		Object o = super.getStashedProperty(ICGOntology.STASH_COREF_MAP);
		environment.logDebug("CGGraph.getCoreferences "+this.getLocator()+" "+o);
		String json;
		if (o != null)
			json = (String)o;
		else
			json =  (String)super.getProperty(ICGOntology.COREFERENCE_MAP);
		environment.logDebug("CGGraph.getCoreferences-1 "+o);
		if (json != null) {
			JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
			try {
				result = (JSONObject)p.parse(json);
				super.setProperty(ICGOntology.STASH_COREF_MAP, result.toJSONString());
			} catch (Exception e) {
				environment.logError(e.getMessage(), e);
			}
		}
		return result;
	}

}
