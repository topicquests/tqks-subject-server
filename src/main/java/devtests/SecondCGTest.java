/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

import java.util.UUID;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.cg.api.ICGConcept;
import org.topicquests.ks.cg.api.ICGGraph;
import org.topicquests.ks.cg.api.ICGModel;
import org.topicquests.ks.cg.api.ICGRelation;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 * <p>"Joe believes that Mary wants to marry a sailor"</p>
 * <p>To do this, we must make two new relations:
 *  Believe (BLEV) and Want (WANT)
 * </p>
 * <p>This test should require a CoreferenceSet
 * owing to the nested use of Mary</p>
 * <p>But, we are transcluding topics so they serve as
 * their own reference</p>
 */
public class SecondCGTest {
	  private SystemEnvironment environment;
	  private DataProvider database;
	  private ITicket credentials;
	  private IProxyModel proxyModel;
	  private ICGModel cgModel;
	  private static final String 
	  	SUPERCLASS	= ITQCoreOntology.CLASS_TYPE,
	    USERID	= ITQCoreOntology.SYSTEM_USER,
	    EXPERIENCER = "EXPR",
	    OBJECT = "OBJ",
	    AGENT = "AGNT";
	  
	/**
	 * 
	 */
	public SecondCGTest() {
	    environment = new SystemEnvironment();
	    database = environment.getDataProvider();
	    proxyModel = environment.getProxyModel();
	    cgModel = environment.getCGModel();
	    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
	    System.out.println("\n-------------------\nStarting FirstCGTest");
	    String latticeTypeLocator = null; //TODO
	    // make the actors
	    String joeLox = UUID.randomUUID().toString();
	    IProxy joe = makeActor(joeLox, "Joe", "Joe the dude");
	    String maryLox = UUID.randomUUID().toString();
	    IProxy mary = makeActor(maryLox, "Mary", "Has desires");
	    String sailorLox = UUID.randomUUID().toString();
	    IProxy sailor = makeActor(sailorLox, "Sailor", "Generic type");
	    //Make the new Make the new topics
	    IProxy believe = makeSubclass(null, "Believe", "A belief topic");
	    IProxy want = makeSubclass(null, "Want", "A want topic");
	    IProxy marriage = makeSubclass(null, "Marry", "A Marriage Topic");
	    
	    // the graph
	    wireGraph(joe, mary, sailor, believe, want, marriage);
	  environment.shutDown();
	  System.exit(0);
	}
	
	void wireGraph(IProxy joe, IProxy mary, IProxy sailor, IProxy believe, IProxy want, IProxy marriage) {
		String joeLox = joe.getLocator();
		String maryLox = mary.getLocator();
		String sailorLox = sailor.getLocator();
	    String expLox = cgModel.relationToLocator(EXPERIENCER);
	    String objLox = cgModel.relationToLocator(OBJECT);
	    String agntLox = cgModel.relationToLocator(AGENT);
	    String gLox = UUID.randomUUID().toString();
	    //Make the graph
	    ICGGraph g = cgModel.newGraphInstance(gLox, "The Joe Mary Sailor Graph", "Hope this works", "en", USERID, null);
	    System.out.println("\n-------------------\nG "+g.toJSONString());
	    //Relate graph to topics
	    IResult r = g.relateGraphToTopic(joe, USERID, null);
	    r = g.relateGraphToTopic(mary, USERID, null);
	    r = g.relateGraphToTopic(sailor, USERID, null);
	    //Add Joe
	    ICGConcept joeCon = addAConcept(g, joeLox, "Joe's Concept", "Testing");
	    //Track coreferences
	    g.addCoreference(joeLox, joeCon.getLocator());

	    //Add believe
	    ICGConcept believeCon = addAConcept(g, believe.getLocator(), "Believe Concept", "Testing");
	    //Track coreferences
	    g.addCoreference(believe.getLocator(), believeCon.getLocator());

	    //Relate Joe's concept to Believe by way of Experiencer
	    ICGRelation belR = addARelation(g, expLox, "Joe experiences belief","Testing", believeCon.getLocator(), joeCon.getLocator() );
	    //Now add the proposition
	    ICGGraph propositionGraph = addAGraph(g, "Proposition", "The proposition that Mary wants to marry a sailor");
	    //Now, relate this proposition to the belief
	    ICGRelation propBelR = addARelation(g, expLox, "Belief in a proposition","Testing", believeCon.getLocator(), propositionGraph.getLocator() );
	    //Now, populate the proposition graph
	    //Add mary the person
	    ICGConcept maryCon = addAConcept(propositionGraph, maryLox, "Mary's concept", "Testing");
	    //Track coreferences
	    g.addCoreference(maryLox, maryCon.getLocator());

	    //Add wants
	    ICGConcept wantsCon = addAConcept(propositionGraph, want.getLocator(), "Want Concept", "Testing");
	    //Track coreferences
	    g.addCoreference(want.getLocator(), wantsCon.getLocator());
	   
	    //Wire mary wants
	    ICGRelation maryWantR = addARelation(propositionGraph, expLox, "Belief in a proposition","Testing", wantsCon.getLocator(), maryCon.getLocator() );
	    //Add the situation graph
	    ICGGraph sitGraph = addAGraph(propositionGraph, "Situation", "The situation that marry marries a sailor");
	    //Add mary to situation
	    ICGConcept marySitCon = addAConcept(sitGraph, maryLox, "Mary wanting...", "Testing");
	    //Track coreferences
	    g.addCoreference(maryLox, marySitCon.getLocator());
	    //Add marriage
	    ICGConcept marriageCon = addAConcept(sitGraph, marriage.getLocator(), "Marriage", "Testing");
	    //Track coreferences
	    g.addCoreference(marriage.getLocator(), marriageCon.getLocator());
	    //Wire marriage to mary
	    ICGRelation marriageR = addARelation(sitGraph, agntLox, "Belief in a proposition","Testing", marriageCon.getLocator(), marySitCon.getLocator() );
	    //Add sailor
	    ICGConcept sailorCon = addAConcept(sitGraph, sailor.getLocator(), "Sailor", "Testing");
	    //Track coreferences
	    g.addCoreference(sailor.getLocator(), sailorCon.getLocator());
	    //Wire marriage to sailor
	    ICGRelation sailorR = addARelation(sitGraph, agntLox, "Sailor in the marriage","Testing", marriageCon.getLocator(), sailorCon.getLocator() );
	    
	    //OK, it's wired
	    // Now, dump the result
	    r = cgModel.getGraph(gLox, credentials);
	    System.out.println("\n-------------------\nGA "+r.getErrorString()+" "+r.getResultObject());
	    g = (ICGGraph)r.getResultObject();
	    if (g != null)
		    System.out.println("\n-------------------\nDID "+g.toJSONString());
	    environment.shutDown();
	    System.exit(0);

	}
	
	//TODO add handling coreference linking to this to save more coding
	ICGConcept addAConcept(ICGGraph g, String conceptProxyLocator, String label, String details) {
		IResult r = g.newConcept(UUID.randomUUID().toString(), conceptProxyLocator, null, label, details, "en", USERID, null, null);
		ICGConcept result = (ICGConcept)r.getResultObject();
		return result;
	}
	
	ICGGraph addAGraph(ICGGraph g, String label, String details) {
		IResult r = g.newGraph(UUID.randomUUID().toString(), label, details, "en", USERID, null);
		ICGGraph result = (ICGGraph)r.getResultObject();
		return result;
	}
	
	ICGRelation addARelation(ICGGraph g, String relationType, String label, String details, String sourceNodeLocator, String targetNodeLocator) {
		IResult r = g.newRelation(relationType, label, details, "en", USERID, null, sourceNodeLocator, targetNodeLocator);
		ICGRelation result = (ICGRelation)r.getResultObject();
		return result;
	}
	
	IProxy makeActor(String lox, String label, String details) {
	    String l = lox;
	    if (l == null)
	    	l = UUID.randomUUID().toString();
		IProxy result = proxyModel.newInstanceNode(l, SUPERCLASS, label, details, "en", USERID, null, null, null, false);
	    IResult r = database.putNode(result);
	    System.out.println("\n-------------------\n "+label+" "+r.getErrorString());
	    return result;
	}
	
	IProxy makeSubclass(String lox, String label, String details) {
	    String l = lox;
	    if (l == null)
	    	l = UUID.randomUUID().toString();
		IProxy result = proxyModel.newSubclassNode(l, SUPERCLASS, label, details, "en", USERID, null, null, null, false);
	    IResult r = database.putNode(result);
	    System.out.println("\n-------------------\n "+label+" "+r.getErrorString());
	    return result;
	}

}

/**
{
	"relList": [{
		"crtr": "SystemUser",
		"_ver": "1525484868056",
		"lEdDt": "2018-05-04T18:47:48-07:00",
		"label": {
			"en": ["Belief in a proposition"]
		},
		"url": null,
		"crDt": ["2018-05-04T18:47:48-07:00", "2018-05-04T18:47:47-07:00"],
		"trCl": ["TypeType", "RelationType", "CGRelationType", "CG_EXPR_RELATION"],
		"tpL": ["ee98f802-5453-4ff5-856d-0ceb4819bd6aInstanceRelationTypeCG_EXPR_RELATION"],
		"isLiv": true,
		"node_type": "CG_EXPR_RELATION",
		"isVrt": false,
		"lox": "ee98f802-5453-4ff5-856d-0ceb4819bd6a",
		"isPrv": false,
		"sIco": "\/images\/cogwheels_sm.png",
		"details": {
			"en": ["Testing"]
		},
		"lIco": "\/images\/cogwheels.png"
	}, {
		"crtr": "SystemUser",
		"_ver": "1525484867545",
		"lEdDt": "2018-05-04T18:47:47-07:00",
		"label": {
			"en": ["Joe experiences belief"]
		},
		"url": null,
		"crDt": "2018-05-04T18:47:47-07:00",
		"trCl": ["TypeType", "RelationType", "CGRelationType", "CG_EXPR_RELATION"],
		"tpL": ["56e8e4fd-4bb7-45df-8967-6c0db326b861InstanceRelationTypeCG_EXPR_RELATION"],
		"isLiv": true,
		"node_type": "CG_EXPR_RELATION",
		"isVrt": false,
		"lox": "56e8e4fd-4bb7-45df-8967-6c0db326b861",
		"isPrv": false,
		"sIco": "\/images\/cogwheels_sm.png",
		"details": {
			"en": ["Testing"]
		},
		"lIco": "\/images\/cogwheels.png"
	}],
	"crtr": "SystemUser",
	"conList": [{
		"crtr": "SystemUser",
		"_ver": "1525484867802",
		"lEdDt": "2018-05-04T18:47:47-07:00",
		"label": {
			"en": ["Proposition"]
		},
		"url": null,
		"crDt": "2018-05-04T18:47:47-07:00",
		"trCl": ["ClassType", "NodeType", "TypeType", "CGGraphNodeType"],
		"tpL": ["5cebcc06-77ce-40f9-a6d8-42fac0031ae4InstanceRelationTypeCGGraphNodeType"],
		"isLiv": true,
		"node_type": "CGGraphNodeType",
		"isVrt": false,
		"lox": "5cebcc06-77ce-40f9-a6d8-42fac0031ae4",
		"isPrv": false,
		"sIco": "\/images\/ibis\/map_sm.png",
		"details": {
			"en": ["The proposition that Mary wants to marry a sailor"]
		},
		"lIco": "\/images\/ibis\/map.png",
		"cgCList": ["f470bf18-3d06-4547-9cad-91da192ca5dd", "b0559afe-e1d5-4f18-b9cd-9b422dea3025", "ae7031e2-9b10-4184-a390-d6ff180b9552"],
		"cgRList": "9a99a467-50b2-4e2d-9e93-db9ae73f7e44"
	}, {
		"crtr": "SystemUser",
		"_ver": "1525484867276",
		"lEdDt": "2018-05-04T18:47:47-07:00",
		"label": {
			"en": ["Believe Concept"]
		},
		"topicRefLox": "6114e3a0-623f-48a2-971c-5da6139462aa",
		"url": null,
		"crDt": "2018-05-04T18:47:47-07:00",
		"trCl": ["TypeType", "RelationType", "CGRelationType"],
		"tpL": ["88f2ad24-21ad-4897-b3ad-94269eb585f0InstanceRelationTypeCGRelationType"],
		"isLiv": true,
		"node_type": "CGRelationType",
		"isVrt": false,
		"lox": "88f2ad24-21ad-4897-b3ad-94269eb585f0",
		"isPrv": false,
		"sIco": "\/images\/cogwheel_sm.png",
		"details": {
			"en": ["Testing"]
		},
		"lIco": "\/images\/cogwheel.png"
	}, {
		"crtr": "SystemUser",
		"_ver": "1525484866987",
		"lEdDt": "2018-05-04T18:47:46-07:00",
		"label": {
			"en": ["Joe's Concept"]
		},
		"topicRefLox": "6528c240-dd3a-4c97-92dd-76d0d953464c",
		"url": null,
		"crDt": "2018-05-04T18:47:46-07:00",
		"trCl": ["TypeType", "RelationType", "CGRelationType"],
		"tpL": ["06c445b8-564e-471f-854a-1c69714ff46cInstanceRelationTypeCGRelationType"],
		"isLiv": true,
		"node_type": "CGRelationType",
		"isVrt": false,
		"lox": "06c445b8-564e-471f-854a-1c69714ff46c",
		"isPrv": false,
		"sIco": "\/images\/cogwheel_sm.png",
		"details": {
			"en": ["Testing"]
		},
		"lIco": "\/images\/cogwheel.png"
	}],
	"_ver": "1525484865718",
	"lEdDt": "1525484866744",
	"label": {
		"en": ["The Joe Mary Sailor Graph"]
	},
	"url": null,
	"crDt": ["1525484866744", "2018-05-04T18:47:45-07:00"],
	"trCl": ["ClassType", "NodeType", "TypeType", "CGGraphNodeType"],
	"tpL": ["8be35488-3f7d-463a-8600-763df3b5d4b4InstanceRelationTypeCGGraphNodeType", "8be35488-3f7d-463a-8600-763df3b5d4b4CG_Topic_Relation6528c240-dd3a-4c97-92dd-76d0d953464c", "8be35488-3f7d-463a-8600-763df3b5d4b4CG_Topic_Relationb7fda986-de13-44cf-8e53-78d98f63ca9f", "8be35488-3f7d-463a-8600-763df3b5d4b4CG_Topic_Relation39c18e9a-3d72-4d01-9517-7559d93d9e33"],
	"isLiv": true,
	"node_type": "CGGraphNodeType",
	"isVrt": false,
	"lox": "8be35488-3f7d-463a-8600-763df3b5d4b4",
	"isPrv": false,
	"sIco": "\/images\/ibis\/map_sm.png",
	"details": {
		"en": ["Hope this works"]
	},
	"lIco": "\/images\/ibis\/map.png",
	"CorefMap": "{\"6114e3a0-623f-48a2-971c-5da6139462aa\":[\"88f2ad24-21ad-4897-b3ad-94269eb585f0\"],\"39c18e9a-3d72-4d01-9517-7559d93d9e33\":[\"cd67a3b4-01bc-4939-8e8e-90fb8c409cf4\"],\"e7da4a4e-519a-4acf-985c-9115ef96e96f\":[\"b0559afe-e1d5-4f18-b9cd-9b422dea3025\"],\"b7fda986-de13-44cf-8e53-78d98f63ca9f\":[\"f470bf18-3d06-4547-9cad-91da192ca5dd\",\"e7d766c3-18a6-4458-8927-45e3c763563c\"],\"1b9941f1-2756-4307-8865-1fec63aa7f9e\":[\"3544e24a-7820-4de0-b3da-c787d4d08168\"],\"6528c240-dd3a-4c97-92dd-76d0d953464c\":[\"06c445b8-564e-471f-854a-1c69714ff46c\"]}",
	"cgCList": ["06c445b8-564e-471f-854a-1c69714ff46c", "88f2ad24-21ad-4897-b3ad-94269eb585f0", "5cebcc06-77ce-40f9-a6d8-42fac0031ae4"],
	"StashCoref": "{\"6114e3a0-623f-48a2-971c-5da6139462aa\":[\"88f2ad24-21ad-4897-b3ad-94269eb585f0\"],\"39c18e9a-3d72-4d01-9517-7559d93d9e33\":[\"cd67a3b4-01bc-4939-8e8e-90fb8c409cf4\"],\"e7da4a4e-519a-4acf-985c-9115ef96e96f\":[\"b0559afe-e1d5-4f18-b9cd-9b422dea3025\"],\"b7fda986-de13-44cf-8e53-78d98f63ca9f\":[\"f470bf18-3d06-4547-9cad-91da192ca5dd\",\"e7d766c3-18a6-4458-8927-45e3c763563c\"],\"1b9941f1-2756-4307-8865-1fec63aa7f9e\":[\"3544e24a-7820-4de0-b3da-c787d4d08168\"],\"6528c240-dd3a-4c97-92dd-76d0d953464c\":[\"06c445b8-564e-471f-854a-1c69714ff46c\"]}",
	"cgRList": ["56e8e4fd-4bb7-45df-8967-6c0db326b861", "ee98f802-5453-4ff5-856d-0ceb4819bd6a"]
}
 */
