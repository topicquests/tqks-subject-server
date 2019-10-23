/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

/**
 * @author jackpark
 *
 */
public class TestHarness {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
          new BootTest();
          //new ProxyFetchTest();
          //new TextQueryTest_1();
          //new SubclassTest();
          //new InstanceTest();
          //new LivePropertyTest_1();
          //new LivePropertyTest_2();
          //new LivePropertyTest_3();
          //new LivePropertyTest_4();
          //new PSITest();
          //new ACLTest();
          //new RelationTest_1();
          //new ClientTest_1();
          //new TreeTest();
          //new ClientTest_2();
          // new MergeRunner();
          //new TextTest();
          //new TreeTest();
		//new ConversationTest();
		//new AddTextFieldsTest();
		//new FirstCGTest();
		//new SecondCGTest();
	}

}
/*
46b1a307-747d-43d1-9a87-f99010a39b72
46b1a307-747d-43d1-9a87-f99010a39b72
SELECT row_to_json(p1) FROM tq_contents.proxy p1 
  WHERE p1.node_type = 'SubclassRelationType' 
    AND p1.lox in 
    (SELECT p2.proxyid FROM tq_contents.properties p2 
      WHERE p1.lox = p2.proxyid 
        AND (p2.property_key = 'tupS' OR p2.property_key = 'tupO') 
        AND p2.property_val = 'PropertyType')
    ORDER BY lox OFFSET 0 LIMIT 5

*/
