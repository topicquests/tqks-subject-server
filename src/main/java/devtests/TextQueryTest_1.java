/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

import org.topicquests.es.util.TextQueryUtil;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class TextQueryTest_1 {
  private SystemEnvironment environment;
  private DataProvider database;
  private TextQueryUtil textQueryUtil;
  private final String
  INDEX 	= "topics",
    Q1 		= "ResourceType type",
    Q2		= "Map upper SystemUser",
    Q3		= "Topic Map"; // should get lots of hits (5, to be exact because query is set to that count)

  /**
   * 
   */
  public TextQueryTest_1() {
    environment = new SystemEnvironment();
    database = environment.getDataProvider();
    textQueryUtil = environment.getESProvider().getTextQueryUtil();
    System.out.println("\n-------------------\nStarting TextQueryTest_1");
    
    String [] indices = new String [1];
    indices[0]=INDEX;
    String [] fields = new String[2];
    fields[0]="label.en"; //"label" does not work
    fields[1]="details.en"; //"details" does not work
    //fields[2]="lox"; //CANNOT put that in a text query
    IResult r = textQueryUtil.queryText(Q1, 0, 5, INDEX, indices, fields);
    System.out.println("AAA "+r.getErrorString()+" | "+r.getResultObject());
    //AAA  | [{"lox":"ResourceType","details":{"en":["Topic Map upper Resource type"]},"label":{"en":["ResourceType type"]}}]
    r = textQueryUtil.queryText(Q2, 0, 5, INDEX, indices, fields);
    System.out.println("BBB "+r.getErrorString()+" | "+r.getResultObject());
    // BBB  | [{"lox":"SystemUser","details":{"en":["Topic Map upper SystemUser type"]},"label":{"en":["SystemUser"]}}]
    r = textQueryUtil.queryText(Q3, 0, 5, INDEX, indices, fields);
    System.out.println("CCC "+r.getErrorString()+" | "+r.getResultObject());
    //CCC  | [{"lox":"ProvenanceType","details":{"en":["Topic Map provenance class"]},"label":{"en":["Provenance type"]}}, {"lox":"RelationType","details":{"en":["Topic Map upper Relation type"]},"label":{"en":["Relation type"]}}, {"lox":"StashedResourceNodeType","details":{"en":["Topic Map StashedResource type"]},"label":{"en":["StashedResourceNodeType"]}}, {"lox":"ClassType","details":{"en":["Topic Map upper Class type"]},"label":{"en":["Class type"]}}, {"lox":"TypeType","details":{"en":["Topic Map root type"]},"label":{"en":["Type type"]}}]
    environment.shutDown();
    System.out.println("Finished TextQueryTest_1\n-------------------\n");
  }

}
