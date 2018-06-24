/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.ks.tm.api.IParentChildContainer;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.ks.tm.api.ITreeNode;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class TreeTest {
  private SystemEnvironment environment;
  private DataProvider database;
  private ITicket credentials;
  private IProxyModel model;

  private final String 
    SUPERCLASS = ITQCoreOntology.CLASS_TYPE,
    USERID     = ITQCoreOntology.SYSTEM_USER,
    LANG       = "en";
  
  private void TreeTest1() {
    System.out.println("\n-------------------\nStarting TreeTest1");
    IResult r = database.loadTree(ITQCoreOntology.TYPE_TYPE,
                                  3, 0, 5, credentials);
    
    // That returns a single node.
    ITreeNode n = (ITreeNode)r.getResultObject();
    String json = null;
    if (n != null) {
      json = n.simpleToJSON().toJSONString();
    }
    System.out.println("AAA "+r.getErrorString()+" \n "+json);
    System.out.println("Finished TreeTest1\n-------------------\n");
  }

  private void ConvTreeTest() {
    System.out.println("\n-------------------\nStarting ConvTreeTest");

    Conv1Setup();
    Conv2Setup();

    System.out.println("Finished ConvTreeTest\n-------------------\n");
  }

  private void Conv1Setup() {
    String context = "context1";
    
    IProxy p1 = model.newInstanceNode("lox1", SUPERCLASS, "Root conversation", "conv1", LANG, USERID,
                                      ITQCoreOntology.CONVERSATION_ROOT, null, null, false);
    IResult r = database.putNodeNoMerge(p1);
    ((IParentChildContainer)p1).addParentNode(context, "");  // root node
    
    IProxy p2 = model.newInstanceNode("lox2", SUPERCLASS, "Child conversation 1", "conv2", LANG, USERID,
                                      ITQCoreOntology.CONVERSATION_NODE_TYPE, null, null, false);
    r = database.putNodeNoMerge(p2);
    ((IParentChildContainer)p1).addChildNode(context, "lox2", ""); 
   
    IProxy p3 = model.newInstanceNode("lox3", SUPERCLASS, "Child conversation 2", "conv3", LANG, USERID,
                                      ITQCoreOntology.CONVERSATION_NODE_TYPE, null, null, false);
    r = database.putNodeNoMerge(p3);
    ((IParentChildContainer)p2).addChildNode(context, "lox3", "");
    
    IProxy p4 = model.newInstanceNode("lox4", SUPERCLASS, "Child conversation 3", "conv4", LANG, USERID,
                                      ITQCoreOntology.CONVERSATION_NODE_TYPE, null, null, false);
    r = database.putNodeNoMerge(p4);
    ((IParentChildContainer)p2).addChildNode(context, "lox4", "");
    
    IProxy p5 = model.newInstanceNode("lox5", SUPERCLASS, "Child conversation 4", "conv5", LANG, USERID,
                                      ITQCoreOntology.CONVERSATION_NODE_TYPE, null, null, false);
    r = database.putNodeNoMerge(p5);
    ((IParentChildContainer)p3).addChildNode(context, "lox5", "");

    // Get children of root for context1
    System.out.println("--------------\nChildren of lox2 in context1");
    outputProxyList(context, ((IParentChildContainer)p2).listChildNodes(context, credentials));
    System.out.println("--------------\nParent of lox4 in context1");
    outputProxyList(context, ((IParentChildContainer)p4).listParentNodes(context, credentials));
    System.out.println("--------------\nAncestors of lox4 in context1");
    outputProxyList(context, ((IParentChildContainer)p4).listAncestorNodes(context, credentials));
    System.out.println("--------------");
  }

  private void Conv2Setup() {
    String context = "context2";
    
    IProxy p1 = model.newInstanceNode("lox1a", SUPERCLASS, "Root conversation A", "conv1a", LANG, USERID,
                                      ITQCoreOntology.CONVERSATION_ROOT, null, null, false);
    IResult r = database.putNodeNoMerge(p1);
    ((IParentChildContainer)p1).addParentNode(context, "");  // root node
    
    IProxy p2 = model.newInstanceNode("lox2a", SUPERCLASS, "Child conversation 1", "conv2a", LANG, USERID,
                                      ITQCoreOntology.CONVERSATION_NODE_TYPE, null, null, false);
    r = database.putNodeNoMerge(p2);
    ((IParentChildContainer)p1).addChildNode(context, "lox2a", ""); 
   
    IProxy p3 = model.newInstanceNode("lox3a", SUPERCLASS, "Child conversation 2", "conv3a", LANG, USERID,
                                      ITQCoreOntology.CONVERSATION_NODE_TYPE, null, null, false);
    r = database.putNodeNoMerge(p3);
    ((IParentChildContainer)p1).addChildNode(context, "lox3a", "");
    
    IProxy p4 = model.newInstanceNode("lox4a", SUPERCLASS, "Child conversation 3", "conv4a", LANG, USERID,
                                      ITQCoreOntology.CONVERSATION_NODE_TYPE, null, null, false);
    r = database.putNodeNoMerge(p4);
    ((IParentChildContainer)p1).addChildNode(context, "lox4a", "");
    
    IProxy p5 = model.newInstanceNode("lox5a", SUPERCLASS, "Child conversation 4", "conv5a", LANG, USERID,
                                      ITQCoreOntology.CONVERSATION_NODE_TYPE, null, null, false);
    r = database.putNodeNoMerge(p5);
    ((IParentChildContainer)p3).addChildNode(context, "lox5a", "");

    // Get children of root for context1
    System.out.println("--------------\n\nChildren of lox1a in context2");
    outputProxyList(context, ((IParentChildContainer)p1).listChildNodes(context, credentials));
    System.out.println("--------------\nParent of lox4a in context2");
    outputProxyList(context, ((IParentChildContainer)p4).listParentNodes(context, credentials));
    System.out.println("--------------\nAncestors of lox4a in context2");
    outputProxyList(context, ((IParentChildContainer)p4).listAncestorNodes(context, credentials));
    System.out.println("--------------");
  }

  private void outputProxyList(String context, List<IProxy> children) {
    Iterator<IProxy> itr = children.iterator();

    while (itr.hasNext()) {
      System.out.println("context: " + context + ", child: " + itr.next().getLocator());
    }
  }
  
  /**
   * 
   */
  public TreeTest() {
    environment = new SystemEnvironment();
    database = environment.getDataProvider();
    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
    model = environment.getProxyModel();

    TreeTest1();
    ConvTreeTest();

    environment.shutDown();
  }
}
/*
  {
  "subs": [{
  "subs": [{
  "label": "Bootstrap Provenance type",
  "locator": "BootstrapProvenanceType"
  }],
  "label": "Provenance type",
  "locator": "ProvenanceType"
  }, {
  "subs": [{
  "label": "Harvested Class type",
  "locator": "HarvestedClassType"
  }, {
  "label": "ClusterType",
  "locator": "ClusterType"
  }, {
  "subs": [{
  "label": "OrganizationNodeType",
  "locator": "OrganizationNodeType"
  }, {
  "label": "PerspectiveNodeType",
  "locator": "PerspectiveNodeType"
  }, {
  "label": "ProjectType",
  "locator": "ProjectType"
  }, {
  "label": "SolutionNodeType",
  "locator": "SolutionNodeType"
  }, {
  "label": "SummaryNodeType",
  "locator": "SummaryNodeType"
  }],
  "label": "ClassType",
  "locator": "NodeType"
  }],
  "label": "Class type",
  "locator": "ClassType"
  }, {
  "label": "Relation type",
  "locator": "RelationType"
  }, {
  "label": "Ontology type",
  "locator": "OntologyType"
  }, {
  "subs": [{
  "label": "LocatorProperty",
  "locator": "LocatorProperty"
  }, {
  "label": "CreatedDateProperty",
  "locator": "CreatedDateProperty"
  }, {
  "label": "LastEditDateProperty",
  "locator": "LastEditDateProperty"
  }, {
  "label": "CreatorIdProperty",
  "locator": "CreatorIdProperty"
  }, {
  "label": "LabelProperty",
  "locator": "LabelProperty"
  }],
  "label": "Property type",
  "locator": "PropertyType"
  }],
  "instances": [{
  "label": "Test class",
  "locator": "b7d71a74-c46f-40ce-935e-c44225ba25f7"
  }, {
  "label": "Test class",
  "locator": "9868a45f-7e29-4ee4-8f4c-a553d383887c"
  }, {
  "label": "Test class",
  "locator": "372ad49e-bdf9-45c4-aabb-f3816e72e7ee"
  }, {
  "label": "Test class",
  "locator": "a1127116-ab41-4ecf-80eb-105cf5850df8"
  }, {
  "label": "Test class",
  "locator": "4ba0c76c-e2d0-4530-9cd9-d9fbfe89b73c"
  }, {
  "label": "Test class",
  "locator": "9d3af948-2a10-4463-8d8a-e4ad7a4b6f87"
  }, {
  "label": "Test class",
  "locator": "4a361ced-cb8d-4173-bded-83e00852105c"
  }, {
  "label": "Test class",
  "locator": "4cedc102-cb86-4e6f-a32a-5d826b0ac71c"
  }, {
  "label": "Test class",
  "locator": "e3ad096d-5ec5-4639-b384-71c81716c5ea"
  }, {
  "label": "Test class",
  "locator": "2b7422b1-040e-4a49-ad10-3701eb7e1590"
  }, {
  "label": "Test class",
  "locator": "30c31920-55a3-499a-a9ac-7deda9aa340a"
  }, {
  "label": "Test class",
  "locator": "587c844b-ed5c-4fda-915e-8f63f9754c64"
  }, {
  "label": "Test class",
  "locator": "e25dbe2f-cd42-4206-9a62-8fd2c403f7b1"
  }, {
  "label": "Test class",
  "locator": "2cbb8044-b321-4a52-b95c-8f09db515fed"
  }, {
  "label": "Test class",
  "locator": "4de76814-d20a-4267-9084-83c1b072eb52"
  }, {
  "label": "Test class",
  "locator": "823af311-8ef7-4e40-a7d3-096ed57fcfc8"
  }, {
  "label": "Test class",
  "locator": "974786e1-32ab-44ac-b9e1-d3bde746fb0d"
  }, {
  "label": "Test class",
  "locator": "4366d682-67a7-4384-9621-c83520fe4be9"
  }, {
  "label": "Carbon Dioxide",
  "locator": "949accd8-9b93-4a96-8cf2-33538c73827d"
  }],
  "label": "Type type",
  "locator": "TypeType"
  }
*/
