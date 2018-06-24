/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Iterator;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.ICoreIcons;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.cg.api.ICGConcept;
import org.topicquests.ks.cg.api.ICGGraph;
import org.topicquests.ks.cg.api.ICGModel;
import org.topicquests.ks.cg.api.ICGOntology;
import org.topicquests.ks.cg.api.ICGRelation;
import org.topicquests.ks.cg.api.IReferent;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class CGModel implements ICGModel {
  private SystemEnvironment environment;
  private DataProvider dataProvider;
  private IProxyModel proxyModel;
  private ICGModel model;
  private ITicket credentials;
  private final String USER_ID = ITQCoreOntology.SYSTEM_USER;

  /**
   * 
   */
  public CGModel(SystemEnvironment env) {
    environment = env;
    this.dataProvider = environment.getDataProvider();
    proxyModel = environment.getProxyModel();
    model = environment.getCGModel();
    this.credentials = new TicketPojo();
    credentials.setUserLocator(USER_ID);
  }
  
  @Override
  public String relationToLocator(String reln) {
	  String x = reln.toUpperCase();
	  String result = "CG_"+x+"_RELATION";
	  return result;
  }

	@Override
	public String latticeTypeToLocator(String type) {
		  String x = type.toUpperCase();
		  String result = "CG_"+x+"_LatticeType";
		  return result;
	}

	@Override
	public ICGConcept newConceptInstance(String conceptLocator, String topicLocator, String latticeTypeLocator, String label, String details, String language, String userLocator, String provenanceLocator, IReferent ref) throws Exception {
		if (topicLocator == null)
			throw new Exception("Missing topicLocator");
		String lox = UUID.randomUUID().toString();
		IProxy p = proxyModel.newInstanceNode(lox, ICGOntology.CG_RELATION_TYPE, label, details, language,
				userLocator, provenanceLocator, 
				ICoreIcons.CLASS_ICON_SM, ICoreIcons.CLASS_ICON, false);
		ICGConcept result = new CGConcept(p.getData());
		IResult r = dataProvider.putNode(p);
		result.setTopicReferenceLocator(topicLocator);
		if (latticeTypeLocator != null && !latticeTypeLocator.equals(""))
			result.setLatticeType(latticeTypeLocator);
		//TODO deal with ref
		return result;
	}
	
	@Override
	public IResult getConcept(String conceptLocator, ITicket credentials) {
		IResult result = dataProvider.getFullNode(conceptLocator, credentials);
		IProxy p = (IProxy)result.getResultObject();
		if (p != null) 
			result.setResultObject(new CGConcept(p.getData()));
		return result;

	}
	
	@Override
	public IResult removeConcept(String conceptLocator, ITicket credentials) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IResult listConceptsForProxy(String proxyLocator, ITicket credentials) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ICGGraph newGraphInstance(String graphLocator, String label, String details, String language, String userLocator, String provenanceLocator) {
		String lox = graphLocator;
		if (lox == null)
			lox = UUID.randomUUID().toString();
		IProxy p = proxyModel.newInstanceNode(lox, ICGOntology.CG_GRAPH_NODE, label, details, language,
				userLocator, provenanceLocator, 
				ICoreIcons.MAP_SM, ICoreIcons.MAP, false);
		ICGGraph result = new CGGraph(p.getData());
		IResult r = dataProvider.putNode(p);
		return result;
	}
	
	@Override
	public ICGRelation newRelation(String relationLocator, String label, String details, String language, String userLocator, String provenanceLocator) {
		IProxy p = proxyModel.newSubclassNode(relationLocator, ICGOntology.CG_RELATION_TYPE, label, details, language,
				userLocator, provenanceLocator, 
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, false);
		ICGRelation result = new CGRelation(p.getData());
		IResult r = dataProvider.putNode(p);
		return result;
	}

	@Override
	public IResult getGraph(String graphLocator, ITicket credentials) {
		IResult result =
				dataProvider.getFullNode(graphLocator, credentials);
		IProxy p = (IProxy)result.getResultObject();
		if (p != null) {
			List<JSONObject> ljo;
			ICGGraph g = new CGGraph(p.getData());
			List<ICGConcept> cons = g.listConcepts(credentials);
			environment.logDebug("CGModel.getGraph-1 "+cons);
			if (cons != null) {
				ljo = new ArrayList<JSONObject>();
				ICGConcept c;
				Iterator<ICGConcept> itr = cons.iterator();
				while (itr.hasNext()) {
					c = itr.next();
					ljo.add(c.getData());
				}
				g.stashProperty(ICGOntology.CON_LIST, ljo);
			}
			List<ICGRelation> relns = g.listRelations(credentials);
			environment.logDebug("CGModel.getGraph-2 "+relns);
			if (relns != null) {
				ljo = new ArrayList<JSONObject>();
				ICGRelation c;
				Iterator<ICGRelation> itr = relns.iterator();
				while (itr.hasNext()) {
					c = itr.next();
					ljo.add(c.getData());
				}
				g.stashProperty(ICGOntology.RELN_LIST, ljo);
			}
			result.setResultObject(g);
		}
			
		return result;
	
	}

	@Override
	public IResult getRelation(String relationLocator, ITicket credentials) {
		IResult result =
				dataProvider.getFullNode(relationLocator, credentials);
		IProxy p = (IProxy)result.getResultObject();
		if (p != null) 
			result.setResultObject(new CGRelation(p.getData()));
		return result;
	}

	@Override
	public ICGRelation newRelationInstance(String relationType, String label, String details, String language,
			String userLocator, String provenanceLocator) {
		String lox = UUID.randomUUID().toString();
		IProxy p = proxyModel.newInstanceNode(lox, relationType, label, details, language,
				userLocator, provenanceLocator, 
				ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, false);
		ICGRelation result = new CGRelation(p.getData());
		IResult r = dataProvider.putNode(p);
		return result;
	}

	@Override
	public ICGRelation newRelationInstance(String relationType, String label, String details, String language,
			String userLocator, String provenanceLocator, String sourceNodeLocator, String targetNodeLocator) {
		ICGRelation result = newRelationInstance(relationType, label, details, language,
				userLocator, provenanceLocator);
		result.setSourceNodeLocator(sourceNodeLocator);
		result.setTargetNodeLocator(targetNodeLocator);
		return result;
	}

	@Override
	public IResult copy(ICGGraph node) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public IResult simplify(ICGGraph node) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public IResult restrict(ICGConcept node) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public IResult unRestrict(ICGConcept node) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public IResult join(ICGGraph nodeA, ICGGraph nodeB) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public IResult maximalJoin(ICGGraph nodeA, ICGGraph nodeB) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public IResult detach(ICGGraph nodeA, ICGGraph nodeB) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}



}
