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
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class AddTextFieldsTest {
	  private SystemEnvironment environment;
	  private DataProvider database;
	  private ITicket credentials;
	  private IProxyModel model;
	  private IProxy p1 = null;;

	  private final String 
	    SUPERCLASS = ITQCoreOntology.CLASS_TYPE,
	    USERID     = ITQCoreOntology.SYSTEM_USER,
	    LOX		   = UUID.randomUUID().toString(),
	    LANG       = "en",
	    LAB		   = "My New Label",
	    DET			= "Yada Yada Yada...";

	/**
	 * 
	 */
	public AddTextFieldsTest() {
	    environment = new SystemEnvironment();
	    database = environment.getDataProvider();
	    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
	    model = environment.getProxyModel();
	    buildNode();
	    showNode("A ");
	    addLabel(LAB, LANG);
	    showNode("B ");
	    addDetails(DET, LANG);
	    showNode("C ");
	    environment.shutDown();
	    System.exit(0);
	}
	
	void buildNode() {
	    p1 = model.newInstanceNode(LOX, SUPERCLASS, "Root conversation A", "Something Wonderful is going to happen!", LANG, USERID,
                null, null, null, false);
	    IResult r = database.putNodeNoMerge(p1);
	}
	
	void showNode(String prompt) {
		IResult r = database.getFullNode(LOX, credentials);
		p1 = (IProxy)r.getResultObject();
		System.out.println(prompt+p1.toJSONString());
	}
	
	void addLabel(String newLabel, String language) {
		p1.addLabel(newLabel, language);
	}
	
	void addDetails(String newDetails, String language) {
		p1.addDetails(newDetails, language);
	}

}
//A {"crtr":"SystemUser","_ver":"1524867793019","lEdDt":"2018-04-27T15:23:13-07:00","label":{"en":["Root conversation A"]},"url":null,"crDt":["2018-04-27T15:23:13-07:00","2018-04-27T15:23:12-07:00"],"trCl":["TypeType","ClassType"],"tpL":["2dc5bc88-8e6a-48f9-bc58-e3692dfc7769InstanceRelationTypeClassType"],"isLiv":true,"node_type":"ClassType","isVrt":false,"lox":"2dc5bc88-8e6a-48f9-bc58-e3692dfc7769","isPrv":false,"details":{"en":["Something Wonderful is going to happen!"]}}
//B {"crtr":"SystemUser","_ver":"1524867793019","lEdDt":"2018-04-27T15:23:13-07:00","label":{"en":["Root conversation A","My New Label"]},"url":null,"crDt":["2018-04-27T15:23:13-07:00","2018-04-27T15:23:12-07:00"],"trCl":["TypeType","ClassType"],"tpL":["2dc5bc88-8e6a-48f9-bc58-e3692dfc7769InstanceRelationTypeClassType"],"isLiv":true,"node_type":"ClassType","isVrt":false,"lox":"2dc5bc88-8e6a-48f9-bc58-e3692dfc7769","isPrv":false,"details":{"en":["Something Wonderful is going to happen!"]}}
//C {"crtr":"SystemUser","_ver":"1524867793019","lEdDt":"2018-04-27T15:23:13-07:00","label":{"en":["Root conversation A","My New Label"]},"url":null,"crDt":["2018-04-27T15:23:13-07:00","2018-04-27T15:23:12-07:00"],"trCl":["TypeType","ClassType"],"tpL":["2dc5bc88-8e6a-48f9-bc58-e3692dfc7769InstanceRelationTypeClassType"],"isLiv":true,"node_type":"ClassType","isVrt":false,"lox":"2dc5bc88-8e6a-48f9-bc58-e3692dfc7769","isPrv":false,"details":{"en":["Something Wonderful is going to happen!","Yada Yada Yada..."]}}

