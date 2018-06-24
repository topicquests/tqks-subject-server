/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

import java.util.*;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class TextTest {
	  private SystemEnvironment environment;
	  private DataProvider database;
	  private final String
	  	LOX		= UUID.randomUUID().toString(),
	  	LBL		= "My Label",
	  	LBL2	= "Bonjour",
	  	DTLS	= "My Details",
	  	DTLS2	= "My other details";

	/**
	 * 
	 */
	public TextTest() {
	    environment = new SystemEnvironment();
	    database = environment.getDataProvider();
	    //Build some labels and details
	    JSONObject jo = new JSONObject();
	    jo.put(ITQCoreOntology.LOCATOR_PROPERTY, LOX);
	    JSONObject jx = new JSONObject();
	    List<String>l = new ArrayList<String>();
	    l.add(LBL);
	    jx.put("en", l);
	    l= new ArrayList<String>();
	    l.add(LBL2);
	    jx.put("fr", l);
	    jo.put(ITQCoreOntology.LABEL_PROPERTY, jx);
	    jx = new JSONObject();
	    l = new ArrayList<String>();
	    l.add(DTLS);
	    jx.put("en", l);
	    jo.put(ITQCoreOntology.DETAILS_PROPERTY, jx);
	    IResult r = database.getESProvider().getProvider().put(LOX, "topics", jo);
	    System.out.println("AAA "+r.getErrorString()+" "+jo);
	    jo = database.fetchDetails(LOX);
	    System.out.println("BBB "+jo);
	    jo = database.fetchLabels(LOX);
	    System.out.println("CCC "+jo);
	    //Now do some surgery
	    r = database.getESProvider().getProvider().get(LOX, "topics");
	    jo = (JSONObject)r.getResultObject();
	    System.out.println("DDD "+jo);
	    jx = (JSONObject)jo.get(ITQCoreOntology.DETAILS_PROPERTY);
	    l = (List<String>)jx.get("en");
	    l.add(DTLS2);
	    jx.put("en", l);
	    jo.put(ITQCoreOntology.DETAILS_PROPERTY, jx);
	    database.setDetails(jo);
	    jo = database.fetchDetails(LOX);
	    System.out.println("EEE "+jo);
	    jo = database.fetchLabels(LOX);
	    System.out.println("FFF "+jo);
	    
	    environment.shutDown();
	    System.exit(0);
	}

}
//AAA  {"lox":"ccb0402d-aa74-4c38-9840-8cfcbe7f2293","details":{"en":["My Details"]},"label":{"en":["My Label"]}}
//BBB {"en":["My Details"]}
//CCC {"en":["My Label"],"fr":["Bonjour"]}
//DDD {"lox":"94357e04-7c8d-48c4-8f2b-2b3df684e4a2","details":{"en":["My Details"]},"label":{"en":["My Label"],"fr":["Bonjour"]}}

//EEE {"en":["My Details","My other details"]}
//FFF {"en":["My Label"],"fr":["Bonjour"]}


