/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import java.lang.Exception;

import java.util.Map;

import org.topicquests.support.api.IResult;
import org.topicquests.ks.api.ITicket;

public interface IProxyModel {

  /**
   * Return a new {@link IProxy} with the given <code>locator</code>
   * @param locator
   * @param label
   * @param description
   * @param lang
   * @param userId
   * @param smallImagePath
   * @param largeImagePath
   * @param isPrivate
   * @return
   */
  IProxy newNode(String locator,String label, String description, String lang, 
                 String userId, String smallImagePath, String largeImagePath, boolean isPrivate);
	  
  /**
   * Return a new {@link IProxy} with a database-created <code>locator</code>
   * @param label
   * @param description
   * @param lang
   * @param userId
   * @param smallImagePath
   * @param largeImagePath
   * @param isPrivate
   * @return
   */
  IProxy newNode(String label, String description, String lang, String userId, 
                 String smallImagePath, String largeImagePath, boolean isPrivate);
	  
  /**
   * Return a new {@link IProxy} with the given <code>locator</code> and
   * <code>superclassLocator</code>
   * @param locator
   * @param superclassLocator
   * @param label
   * @param description
   * @param lang
   * @param userId
   * @param provenanceLocator can be <code>null</code>
   * @param smallImagePath
   * @param largeImagePath
   * @param isPrivate
   * @return
   */
  IProxy newSubclassNode(String locator,String superclassLocator,String label, 
                         String description, String lang, String userId, String provenanceLocator, 
                         String smallImagePath, String largeImagePath, boolean isPrivate);
	  
  /**
   * Return a new {@link IProxy} with the database-fabricated <code>locator</code>
   * and given <code>superclassLocator</code>
   * @param superclassLocator
   * @param label
   * @param description
   * @param lang
   * @param userId
   * @param provenanceLocator can be <code>null</code>
   * @param smallImagePath
   * @param largeImagePath
   * @param isPrivate
   * @return
   */
  IProxy newSubclassNode(String superclassLocator,String label, String description, 
                         String lang, String userId, String provenanceLocator, String smallImagePath,
                         String largeImagePath, boolean isPrivate);
	  
  /**
   * Return a new {@link IProxy} with the given <code>locator</code> and
   * given <code>typeLocator</code>
   * @param locator
   * @param typeLocator
   * @param label
   * @param description
   * @param lang
   * @param userId
   * @param provenanceLocator can be <code>null</code>
   * @param smallImagePath
   * @param largeImagePath
   * @param isPrivate
   * @return
   */
  IProxy newInstanceNode(String locator,String typeLocator,String label, String description, 
                         String lang, String userId, String provenanceLocator, String smallImagePath,
                         String largeImagePath, boolean isPrivate);
	  
  /**
   * Return a new {@link IProxy} with the database-fabricated <code>locator</code>
   * and given <code>typeLocator</code>
   * @param typeLocator
   * @param label
   * @param description
   * @param lang
   * @param userId
   * @param provenanceLocator can be <code>null</code>
   * @param smallImagePath
   * @param largeImagePath
   * @param isPrivate
   * @return
   */
  IProxy newInstanceNode(String typeLocator,String label, String description, String lang, 
                         String userId, String provenanceLocator, String smallImagePath,
                         String largeImagePath, boolean isPrivate);

  /**
   * This is the only sanctioned API for adding parent nodes
   * @param proxy
   * @param contextLocator
   * @param parentLocator
   * @return
   */
  IResult addParentNode(IProxy proxy, String contextLocator, String parentLocator);
	
  /**
   * This is the only sanctioned API for adding child nodes
   * @param proxy
   * @param contextLocator
   * @param childLocator
   * @param transcluderLocator
   * @return
   */
  IResult addChildNode(IProxy proxy, String contextLocator, String childLocator, String transcluderLocator);

  /**
   * Preferred way to add a <code>superClassLocator</code>
   * to an existing <code>node</code> to deal with
   * changes to transitive closure
   * @param node
   * @param superClassLocator
   * @param provenanceLocator can be <code>null</code>
   * @return
   */
  IResult addSuperClass(IProxy node, String superClassLocator, String provenanceLocator);
	
  /**
   * Preferred way to set a <code>typeLocator</code> to an
   * existing <code>node</code> to deal with changes to 
   * transitive closure
   * @param node
   * @param typeLocator
   * @param provenanceLocator can be <code>null</code>
   * @return
   */
  IResult setNodeType(IProxy node, String typeLocator, String provenanceLocator);
	
  /**
   * Return a new {@link IAddressableInformationResource}
   * @param sourceNode
   * @param targetNode
   * @param relationTypeLocator
   * @param sourceRole TODO
   * @param targetRole TODO
   * @param userId
   * @param userId
   * @param provenanceLocator can be <code>null<?code>
   * @param smallImagePath
   * @param largeImagePath
   * @param isTransclude
   * @param isPrivate
   * @param isPrivate
   * @param locator
   * @param subject can be <code>null</code>
   * @param body can be <code>null</code>
   * @param language can be <code>null</code>
   * @return
   * /
   IAddressableInformationResource newAIR(String locator, String subject, String body, String language,
   String userId, boolean isPrivate);
	    
   /**
   * <p>This method is appropriate <em>only</em> to nodes which are in the database</p>
   * @param subjectNode
   * @param objectNode
   * @param relationTypeLocator
   * @param subjectRoleLocator
   * @param objectRoleLocator
   * @param userId
   * @param provenanceLocator
   * @param smallImagePath
   * @param largeImagePath
   * @param isTransclude
   * @param isPrivate
   * @return the created {@link ITuple}
   */
  IResult relateExistingNodes(IProxy subjectNode, IProxy objectNode, String relationTypeLocator, 
                              String subjectRoleLocator, String objectRoleLocator, String userId,
                              String provenanceLocator, String smallImagePath, String largeImagePath,
                              boolean isTransclude, boolean isPrivate);
	  
  /**
   * <p>Assert a merge, which fires up a VirtualProxy, creates a MergeAssertion node (not a triple)
   * and adds the list of rule locators to the merge assertion proxy</p>
   * <p>The merge must be mindful of a nodes place in some graph. If the node has
   * a parent node of some time, then the VirtualProxy must substitute for that,
   * and both nodes must be removed as child nodes; the VirtualProxy always stands
   * for those nodes in that graph.</p>
   * @param sourceNodeLocator
   * @param targetNodeLocator
   * @param mergeData
   * @param mergeConfidence
   * @param userLocator
   * @return the locator of the created {@link ITuple}
   */
  //	  IResult assertMerge(String sourceNodeLocator, String targetNodeLocator, 
  //			  Map<String, Double> mergeData, double mergeConfidence, String userLocator);
	  
  /**
   * Assert that the two nodes <em>might need to be merged</em> based on the
   * collection of reasons and votes.
   * @param sourceNodeLocator
   * @param targetNodeLocator
   * @param mergeData
   * @param mergeConfidence
   * @param userLocator
   * @return
   * NOTE: not yet implemented
   */
  //	  IResult assertPossibleMerge(String sourceNodeLocator, String targetNodeLocator, 
  //			  Map<String, Double> mergeData, double mergeConfidence, String userLocator);

  /**
   * Assert that these two nodes must not be merged; they were before, but for reasons given,
   * they should not be merged now.
   * @param sourceNodeLocator
   * @param targetNodeLocator
   * @param mergeData
   * @param mergeConfidence
   * @param userLocator
   * @return
   *Note: not implemented yet
   */
  //	  IResult assertUnmerge(String sourceNodeLocator, IProxy targetNodeLocator, 
  //			  Map<String, Double> mergeData, double mergeConfidence, String userLocator);

}
