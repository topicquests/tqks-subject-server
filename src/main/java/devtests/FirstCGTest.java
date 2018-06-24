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
 *
 */
public class FirstCGTest {
	  private SystemEnvironment environment;
	  private DataProvider database;
	  private ITicket credentials;
	  private IProxyModel proxyModel;
	  private ICGModel cgModel;
	  private static final String 
	  	SUPERCLASS	= ITQCoreOntology.CLASS_TYPE,
	    USERID	= ITQCoreOntology.SYSTEM_USER,
	    RELN_LOX = "CHLD"; // child
	/**
	 * 
	 */
	public FirstCGTest() {
	    environment = new SystemEnvironment();
	    database = environment.getDataProvider();
	    proxyModel = environment.getProxyModel();
	    cgModel = environment.getCGModel();
	    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
	    System.out.println("\n-------------------\nStarting FirstCGTest");
	    String joeLox = UUID.randomUUID().toString();
	    String sueLox = UUID.randomUUID().toString();
	    //Make two "people concepts"
	    IProxy joe = proxyModel.newInstanceNode(joeLox, SUPERCLASS, "Joe", "Sue's Dad", "en", USERID, null, null, null, false);
	    IResult r = database.putNode(joe);
	    System.out.println("\n-------------------\nJOE "+r.getErrorString());
	    IProxy sue = proxyModel.newInstanceNode(sueLox, SUPERCLASS, "SUE", "Joe's Daughter", "en", USERID, null, null, null, false);
	    r = database.putNode(sue);
	    System.out.println("\n-------------------\nSUE "+r.getErrorString());

	    secondPass(joe, sue);
	    /**
	    String latticeTypeLocator = null; //TODO

	    // NOW, make the two CG concepts
	    String joeConLox = UUID.randomUUID().toString();
	    ICGConcept joeCon = null;
	    try {
	    	joeCon = cgModel.newConcept(joeConLox, joeLox, latticeTypeLocator, "Joe's Concept", "Testing cgs", "en", USERID, null, null);
	    } catch (Exception e) {
	    	throw new RuntimeException(e);
	    }
	    r = cgModel.getConcept(joeConLox, credentials);
	    System.out.println("\n-------------------\nJOECon "+joeCon.toJSONString());
	    String sueConLoc = UUID.randomUUID().toString();
	    ICGConcept sueCon = null;
	    try {
	    	sueCon = cgModel.newConcept(sueConLoc, sueLox, latticeTypeLocator, "Sue's Concept", "Testing cgs", "en", USERID, null, null);
	    } catch (Exception e) {
	    	throw new RuntimeException(e);
	    }
	    r = cgModel.getConcept(sueConLoc, credentials);
	    System.out.println("\n-------------------\nSUECon "+sueCon.toJSONString());
	    
	    //Now relate them
	    String rlox = cgModel.relationToLocator(RELN_LOX);
	    ICGRelation joesueReln = cgModel.newRelationInstance(rlox, "Joe Sue Relation", "Joe is the father of Sue", "en", 
	    		USERID, null, joeLox, sueLox);
	    
	    //Now make a graph
	    String gLox = UUID.randomUUID().toString();
	    ICGGraph g = cgModel.newGraph(gLox, "The Joe Father of Sue Graph", "Hope this works", "en", USERID, null);
	    try {
	    	g.addConcept(joeCon);
	    	g.addConcept(sueCon);
	    	g.addRelation(joesueReln);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    r = cgModel.getGraph(gLox, credentials);
	    System.out.println("\n-------------------\nGA "+r.getErrorString()+" "+r.getResultObject());
	    g = (ICGGraph)r.getResultObject();
	    if (g != null)
		    System.out.println("\n-------------------\nDID "+g.toJSONString());
	    environment.shutDown();
	    System.exit(0); */
	}
	
	/**
	 * This pass makes use of the new ICGGraph DSL methods
	 * to greatly simplify crafting a graph
	 * @param joeLox
	 * @param sueLox
	 */
	void secondPass(IProxy joe, IProxy sue) {
		String joeLox = joe.getLocator();
		String sueLox = sue.getLocator();
	    String rlox = cgModel.relationToLocator(RELN_LOX);
	    String gLox = UUID.randomUUID().toString();
	    //Make the graph
	    ICGGraph g = cgModel.newGraphInstance(gLox, "The Joe Father of Sue Graph", "Hope this works", "en", USERID, null);
	    System.out.println("\n-------------------\nG "+g.toJSONString());
	    //Relate graph to topics
	    IResult r = g.relateGraphToTopic(joe, USERID, null);
	    r = g.relateGraphToTopic(sue, USERID, null);
	    //Make some concepts
	    r = g.newConcept(null, joeLox, null, "Joe's Concept", "Testing cgs", "en", USERID, null, null);
	    System.out.println("\n-------------------\nJOE "+r.getErrorString());
	    ICGConcept cgc = (ICGConcept)r.getResultObject();
	    environment.logDebug("FirstCGTest A"+cgc.toJSONString());
	    r = g.newConcept(null, sueLox, null, "Sue's Concept", "Testing cgs", "en", USERID, null, null);	
	    System.out.println("\n-------------------\nJOE "+r.getErrorString());
	    cgc = (ICGConcept)r.getResultObject();
	    environment.logDebug("FirstCGTest B"+cgc.toJSONString());
	    //Make the relation
	    r = g.newRelation(rlox, "Joe Sue Relation", "Joe is the father of Sue", "en", 
	    		USERID, null, joeLox, sueLox);
	    System.out.println("\n-------------------\nREL "+r.getErrorString());
	    r = cgModel.getGraph(gLox, credentials);
	    System.out.println("\n-------------------\nGA "+r.getErrorString()+" "+r.getResultObject());
	    g = (ICGGraph)r.getResultObject();
	    if (g != null)
		    System.out.println("\n-------------------\nDID "+g.toJSONString());
	    environment.shutDown();
	    System.exit(0);

	}

}
/**
{
	"relList": [{
		"crtr": "SystemUser",
		"_ver": "1525467564810",
		"lEdDt": "2018-05-04T13:59:24-07:00",
		"label": {
			"en": ["Joe Sue Relation"]
		},
		"url": null,
		"crDt": "2018-05-04T13:59:24-07:00",
		"trCl": ["TypeType", "RelationType", "CGRelationType", "CG_CHLD_RELATION"],
		"tpL": ["60312a77-99b1-44e7-91b3-b52f0f4d1e3bInstanceRelationTypeCG_CHLD_RELATION"],
		"isLiv": true,
		"node_type": "CG_CHLD_RELATION",
		"isVrt": false,
		"lox": "60312a77-99b1-44e7-91b3-b52f0f4d1e3b",
		"isPrv": false,
		"sIco": "\/images\/cogwheels_sm.png",
		"details": {
			"en": ["Joe is the father of Sue"]
		},
		"lIco": "\/images\/cogwheels.png"
	}],
	"crtr": "SystemUser",
	"conList": [{
		"crtr": "SystemUser",
		"_ver": "1525467564552",
		"lEdDt": "2018-05-04T13:59:24-07:00",
		"label": {
			"en": ["Sue's Concept"]
		},
		"topicRefLox": "235b30a6-7bd6-4063-ba8f-2c76b44eca87",
		"url": null,
		"crDt": "2018-05-04T13:59:24-07:00",
		"trCl": ["TypeType", "RelationType", "CGRelationType"],
		"tpL": ["00eab299-d6b3-48dd-8c0e-9675b59dc72eInstanceRelationTypeCGRelationType"],
		"isLiv": true,
		"node_type": "CGRelationType",
		"isVrt": false,
		"lox": "00eab299-d6b3-48dd-8c0e-9675b59dc72e",
		"isPrv": false,
		"sIco": "\/images\/cogwheel_sm.png",
		"details": {
			"en": ["Testing cgs"]
		},
		"lIco": "\/images\/cogwheel.png"
	}, {
		"crtr": "SystemUser",
		"_ver": "1525467564291",
		"lEdDt": "2018-05-04T13:59:24-07:00",
		"label": {
			"en": ["Joe's Concept"]
		},
		"topicRefLox": "831ef383-27c5-449c-a6ea-a571c6aa2cc3",
		"url": null,
		"crDt": "2018-05-04T13:59:24-07:00",
		"trCl": ["TypeType", "RelationType", "CGRelationType"],
		"tpL": ["ad0c1fac-5056-4cc1-a788-33204ab4e87aInstanceRelationTypeCGRelationType"],
		"isLiv": true,
		"node_type": "CGRelationType",
		"isVrt": false,
		"lox": "ad0c1fac-5056-4cc1-a788-33204ab4e87a",
		"isPrv": false,
		"sIco": "\/images\/cogwheel_sm.png",
		"details": {
			"en": ["Testing cgs"]
		},
		"lIco": "\/images\/cogwheel.png"
	}],
	"_ver": "1525467563501",
	"lEdDt": "1525467564023",
	"label": {
		"en": ["The Joe Father of Sue Graph"]
	},
	"url": null,
	"crDt": ["1525467564023", "2018-05-04T13:59:23-07:00"],
	"trCl": ["TypeType", "ClassType", "NodeType", "CGGraphNodeType"],
	"tpL": ["53998cca-9c8a-4cd7-9d30-ad49dc44b292InstanceRelationTypeCGGraphNodeType", "53998cca-9c8a-4cd7-9d30-ad49dc44b292CG_Topic_Relation831ef383-27c5-449c-a6ea-a571c6aa2cc3", "53998cca-9c8a-4cd7-9d30-ad49dc44b292CG_Topic_Relation235b30a6-7bd6-4063-ba8f-2c76b44eca87"],
	"isLiv": true,
	"node_type": "CGGraphNodeType",
	"isVrt": false,
	"lox": "53998cca-9c8a-4cd7-9d30-ad49dc44b292",
	"isPrv": false,
	"sIco": "\/images\/ibis\/map_sm.png",
	"details": {
		"en": ["Hope this works"]
	},
	"lIco": "\/images\/ibis\/map.png",
	"cgCList": ["ad0c1fac-5056-4cc1-a788-33204ab4e87a", "00eab299-d6b3-48dd-8c0e-9675b59dc72e"],
	"cgRList": "60312a77-99b1-44e7-91b3-b52f0f4d1e3b"
}
 */
