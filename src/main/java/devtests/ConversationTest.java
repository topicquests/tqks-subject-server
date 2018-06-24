/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

import java.util.UUID;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.ICoreIcons;
import org.topicquests.ks.api.INodeTypes;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.ks.tm.api.IConversationNode;
import org.topicquests.ks.tm.api.IParentChildContainer;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class ConversationTest {
	  private SystemEnvironment environment;
	  private DataProvider database;
	  private ITicket credentials;
	  private IProxyModel model;
	  private final String 
	    USERID     = ITQCoreOntology.SYSTEM_USER,
	    ROOT_LOX   = UUID.randomUUID().toString(),
	    LANG       = "en";

	/**
	 * 
	 */
	public ConversationTest() {
	    environment = new SystemEnvironment();
	    database = environment.getDataProvider();
	    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
	    model = environment.getProxyModel();
	    buildTree();
	    fetchTree();
	    environment.shutDown();
	    System.exit(0);
	}
	
	void buildTree() {
		System.out.println("BUILDTREE-");
		String provenanceLocator = null;

	    IProxy p1 = model.newInstanceNode(ROOT_LOX, INodeTypes.ISSUE_TYPE, "Why is the sky blue?",
                                              "Inquiring minds want to know", LANG, USERID,
                                              provenanceLocator, ICoreIcons.ISSUE_SM, ICoreIcons.ISSUE, false);
	    environment.logDebug("ConversationTest-1");
	    ((IParentChildContainer)p1).addParentNode(ROOT_LOX, "");  // root node
	    environment.logDebug("ConversationTest-2");
	    IResult r = database.putNodeNoMerge(p1);

	    IProxy p2 = model.newInstanceNode(INodeTypes.ISSUE_TYPE, "Who wants to know?",
                                              "Inquiring minds want to know", LANG, USERID,
                                              provenanceLocator, ICoreIcons.ISSUE_SM, ICoreIcons.ISSUE, false);
	    r = database.putNodeNoMerge(p2);
	    environment.logDebug("ConversationTest-3");
	    ((IParentChildContainer)p1).addChildNode(ROOT_LOX, p2.getLocator(), ""); 
	    environment.logDebug("ConversationTest-2");
            
	    IProxy p3 = model.newInstanceNode(INodeTypes.POSITION_TYPE, "Has to do with light refraction",
                                              "Physics. Hard stuff", LANG, USERID,
                                              provenanceLocator, ICoreIcons.POSITION_SM, ICoreIcons.POSITION, false);
	    environment.logDebug("ConversationTest-4");
	    r = database.putNodeNoMerge(p3);
	    ((IParentChildContainer)p1).addChildNode(ROOT_LOX, p3.getLocator(), ""); 
            
	    IProxy p4 = model.newInstanceNode(INodeTypes.ISSUE_TYPE, "Why did you ask?",
                                              "Inquiring minds want to know", LANG, USERID,
                                              provenanceLocator, ICoreIcons.ISSUE_SM, ICoreIcons.ISSUE, false);
	    r = database.putNodeNoMerge(p4);
	    ((IParentChildContainer)p2).addChildNode(ROOT_LOX, p4.getLocator(), ""); 
            System.out.println("BUILDTREE+");
	}
	
	void fetchTree() {
		IResult r = database.fetchConversation(ROOT_LOX, LANG, credentials);
		System.out.println("A "+r.getErrorString());
		System.out.println("B "+r.getResultObject());
		if (r.getResultObject() != null) {
			IConversationNode x = (IConversationNode)r.getResultObject();
			/*System.out.println("C getContextLocator: "+x.getContextLocator());
			System.out.println("C getLocator: "+x.getLocator());
			System.out.println("C getParentLocator: "+x.getParentLocator());
			System.out.println("C getSmallIcon: "+x.getSmallIcon());
			System.out.println("C getSubject: "+x.getSubject());
			System.out.println("C getDetails: "+x.getDetails());
			System.out.println("C getTranscluderId: "+x.getTranscluderId());*/
			System.out.println("C toJSON: "+x.toJSON());
			//System.out.println("C getData: "+x.getData());
		}

	}

}
//C toJSON: {"smallImagePath":"\/images\/ibis\/issue_sm.png","subject":"Why is the sky blue?","details":"Inquiring minds want to know","childList":[{"smallImagePath":"\/images\/ibis\/issue_sm.png","parentLocator":"0196fd36-f2f3-4a01-91a2-10fdd10dbc56","subject":"Who wants to know?","details":"Inquiring minds want to know","childList":[{"smallImagePath":"\/images\/ibis\/issue_sm.png","parentLocator":"30d1e61e-f233-4a5e-ac2d-791e07b78942","subject":"Why did you ask?","details":"Inquiring minds want to know","locator":"fdd2ee93-75d9-48b6-bdbd-a6f3ba7f3353"}],"locator":"30d1e61e-f233-4a5e-ac2d-791e07b78942"},{"smallImagePath":"\/images\/ibis\/position_sm.png","parentLocator":"0196fd36-f2f3-4a01-91a2-10fdd10dbc56","subject":"Has to do with light refraction","details":"Physics. Hard stuff","locator":"25635f44-2bf4-4752-b0a3-014082b1c5fe"}],"locator":"0196fd36-f2f3-4a01-91a2-10fdd10dbc56"}

/**
{
	"smallImagePath": "\/images\/ibis\/issue_sm.png",
	"subject": "Why is the sky blue?",
	"details": "Inquiring minds want to know",
	"childList": [{
		"smallImagePath": "\/images\/ibis\/issue_sm.png",
		"parentLocator": "0196fd36-f2f3-4a01-91a2-10fdd10dbc56",
		"subject": "Who wants to know?",
		"details": "Inquiring minds want to know",
		"childList": [{
			"smallImagePath": "\/images\/ibis\/issue_sm.png",
			"parentLocator": "30d1e61e-f233-4a5e-ac2d-791e07b78942",
			"subject": "Why did you ask?",
			"details": "Inquiring minds want to know",
			"locator": "fdd2ee93-75d9-48b6-bdbd-a6f3ba7f3353"
		}],
		"locator": "30d1e61e-f233-4a5e-ac2d-791e07b78942"
	}, {
		"smallImagePath": "\/images\/ibis\/position_sm.png",
		"parentLocator": "0196fd36-f2f3-4a01-91a2-10fdd10dbc56",
		"subject": "Has to do with light refraction",
		"details": "Physics. Hard stuff",
		"locator": "25635f44-2bf4-4752-b0a3-014082b1c5fe"
	}],
	"locator": "0196fd36-f2f3-4a01-91a2-10fdd10dbc56"
}
*/