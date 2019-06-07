/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

import java.util.UUID;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.IExtendedCoreOntology;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITQRelationsOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.ks.tm.api.IMergeResultsListener;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class MergeTest_1 implements IMergeResultsListener {
	  private SystemEnvironment environment;
	  private DataProvider database;
	  private ITicket credentials;
	  private IProxyModel model;
	  private static final String 
	  	SUPERCLASS	= ITQCoreOntology.CLASS_TYPE,
	  	RELN		= ITQRelationsOntology.ADDRESSES,
	    USERID		= ITQCoreOntology.SYSTEM_USER,
	    LOX_1		= UUID.randomUUID().toString(),
	    LOX_2		= UUID.randomUUID().toString(),
	    LOX_3		= UUID.randomUUID().toString(),
	    PSI			= "http://example.com/#foo",
	    KEY			= "myKey",
	    KEY_2		= "anotherKey",
	    VAL_1		= "Value 1",
	    VAL_2		= "Value 2",
	    VAL_3		= "Value 3";
	private MergeRunner host;

	/**
	 * 
	 */
	public MergeTest_1(MergeRunner mr) {
		host = mr;
	    environment = new SystemEnvironment();
	    database = environment.getDataProvider();
	    model = environment.getProxyModel();
	    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
	    runTest();
	}
	
	/**
	 * Build three nodes, two of them about the same subject
	 * Connect two of them with a relation
	 * Merge them
	 * Fetch and study resulting VirtualProxy
	 */
	void runTest() {
	    IProxy p1 = model.newInstanceNode(LOX_1, SUPERCLASS, "Test class 1", "Yup 1", "en", USERID, IExtendedCoreOntology.BOOTSTRAP_PROVENANCE_TYPE, null, null, false);
	    IResult r = database.putNodeNoMerge(p1);
		environment.logDebug("MergeTest_1 p1 "+LOX_1+" "+r.getErrorString());
	    p1.addPSI(PSI);
	    p1.setProperty(KEY, VAL_1);
	    p1.setProperty(KEY_2, VAL_3);
	    IProxy p2 = model.newInstanceNode(LOX_2, SUPERCLASS, "Test class 2", "Yup 2", "en", USERID, IExtendedCoreOntology.BOOTSTRAP_PROVENANCE_TYPE, null, null, false);
	    r = database.putNodeNoMerge(p2);
		environment.logDebug("MergeTest_1 p2 "+LOX_2+" "+r.getErrorString());
	    p2.addPSI(PSI);
	    p2.setProperty(KEY, VAL_2);
	    IProxy p3 = model.newInstanceNode(LOX_3, SUPERCLASS, "Test class 3", "Yup 3", "en", USERID, IExtendedCoreOntology.BOOTSTRAP_PROVENANCE_TYPE, null, null, false);
	    r = database.putNodeNoMerge(p3);
		environment.logDebug("MergeTest_1 p3 "+LOX_3+" "+r.getErrorString());
	    
		r = database.getFullNodeJSON(LOX_1);
		JSONObject jo = (JSONObject)r.getResultObject();
		System.out.println("XXX "+jo.toJSONString());
		r = database.getFullNodeJSON(LOX_2);
		jo = (JSONObject)r.getResultObject();
		System.out.println("YYY "+jo.toJSONString());
		
		r = model.relateExistingNodes(p1, p3, RELN, null, null, USERID, null, null, null, false, false);
	    System.out.println("A "+r.getErrorString()+" "+r.getResultObject());
	    if (r.getResultObject() != null)
	    	System.out.println("B "+((IProxy)r.getResultObject()).toJSONString());
	    database.mergeTwoProxies(p1, p2, "Same PSI", USERID, (IMergeResultsListener)this, credentials);
	    System.out.println("X ");
	}
//B {"crtr":"SystemUser","_ver":"1523219371641","lEdDt":"2018-04-08T13:29:31-07:00","tupST":"NodeType","label":{"en":["Addresses"]},"tupOT":"NodeType","crDt":"2018-04-08T13:29:31-07:00","trCl":["TypeType","RelationType","AddressesRelationType"],"tupS":"83724a26-592b-4165-b32e-8a5a3f16a2f3","node_type":"AddressesRelationType","isTrcld":"false","lox":"83724a26-592b-4165-b32e-8a5a3f16a2f3AddressesRelationTypeb40b1a76-24ee-4dfc-aff3-94b84063f91e","tupO":"b40b1a76-24ee-4dfc-aff3-94b84063f91e","isPrv":false,"details":{"en":["Relate existing nodes:<br\/>Test class 1<br\/> with<\/br> Test class 3<br\/>with the relation: AddressesRelationType"]}}

	@Override
	public void acceptMergeResults(String virtualNodeLocator,
            String originalLocator,
            String errorMessages) {
		System.out.println("C "+virtualNodeLocator+" | "+originalLocator+" | "+errorMessages);
	    if (virtualNodeLocator != null) {
	    	IResult r = database.getFullNodeJSON(virtualNodeLocator);
	    	JSONObject jo = (JSONObject)r.getResultObject();
	    	System.out.println("DID "+jo.toJSONString());
	    }
		environment.shutDown();
	    host.shutDown();
	}

	@Override // not used
	public void acceptMergeResults(String topicLocator, String errorMessages) {
		System.out.println("D "+topicLocator+" | "+errorMessages);
	}

}
/**
 * BAD details
XXX
{
	"crtr": "SystemUser",
	"psi": ["http:\/\/example.com\/#foo"],
	"_ver": "1523376113257",
	"lEdDt": "2018-04-10T09:01:53-07:00",
	"label": {
		"en": ["Test class 1"]
	},
	"myKey": "Value 1",
	"url": null,
	"crDt": "2018-04-10T09:01:53-07:00",
	"trCl": ["TypeType", "ClassType"],
	"anotherKey": "Value 3",
	"tpL": ["6d73a1e2-033e-4a66-b276-b33fee5790f5InstanceRelationTypeClassType"],
	"isLiv": true,
	"node_type": "ClassType",
	"isVrt": false,
	"lox": "6d73a1e2-033e-4a66-b276-b33fee5790f5",
	"isPrv": false,
	"details": null
}

YYY
{
	"crtr": "SystemUser",
	"psi": ["http:\/\/example.com\/#foo"],
	"_ver": "1523376113466",
	"lEdDt": "2018-04-10T09:01:53-07:00",
	"label": {
		"en": ["Test class 2"]
	},
	"myKey": "Value 2",
	"url": null,
	"crDt": "2018-04-10T09:01:53-07:00",
	"trCl": ["TypeType", "ClassType"],
	"tpL": ["4333ddcc-36cd-4bdc-b97b-e3ef9434434cInstanceRelationTypeClassType"],
	"isLiv": true,
	"node_type": "ClassType",
	"isVrt": false,
	"lox": "4333ddcc-36cd-4bdc-b97b-e3ef9434434c",
	"isPrv": false,
	"details": null
}
 */
/**
 * it seems to work
{
	"crtr": "SystemUser",
	"psi": ["http:\/\/example.com\/#foo"],
	"_ver": ["1523470531943", "1523470532520"],
	"lEdDt": "1523470532520",
	"label": {
		"en": ["Test class 1", "Test class 2"]
	},
	"myKey": ["Value 1", "Value 2"],
	"url": null,
	"crDt": ["1523470532520", "2018-04-11T11:15:31-07:00"],
	"trCl": ["TypeType", "ClassType"],
	"anotherKey": "Value 3",
	"tpL": ["4d9f55d7-be5b-4e60-8bfc-6c694f1d924dAddressesRelationTypeb59c8ff8-788a-4b6a-a08d-5ab722c4c796", "4d9f55d7-be5b-4e60-8bfc-6c694f1d924dMergeAssertionTypeb59c8ff8-788a-4b6a-a08d-5ab722c4c796", "d4dd50d2-9d23-4269-b439-c757492d6deaMergeAssertionTypeb59c8ff8-788a-4b6a-a08d-5ab722c4c796"],
	"isLiv": true,
	"node_type": "ClassType",
	"isVrt": true,
	"lox": "b59c8ff8-788a-4b6a-a08d-5ab722c4c796",
	"isPrv": false,
	"details": {
		"en": ["Yup 1", "Yup 2"]
	}
}
 */
