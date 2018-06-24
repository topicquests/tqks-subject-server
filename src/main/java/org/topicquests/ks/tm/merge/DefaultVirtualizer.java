/**
 * Copyright 2015...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.merge;

import java.sql.SQLException;
import java.util.*;

import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IVirtualizer;
import org.topicquests.pg.api.IPostgresConnection;

/**
 * @author park
 * <p>Decide what, if anything to merge:<br/>
 * <li>If no VirtualNode exists, create one</li>
 * <li>If VirtualNode exists, use it</li>
 * <li>Then use SetUnion tools in {@link BaseVirtualizer} to merge nodes</li>
 * <li>Craft relations from the setunion of relations between the nodes</li>
 * </p>
 * <p>This is just a tool for any {@link IMergeImplementation} MergeBean</p>
 */
public class DefaultVirtualizer extends BaseVirtualizer implements IVirtualizer {

  /* (non-Javadoc)
   * @see org.topicquests.topicmap.json.model.api.IVirtualizer#createVirtualNode(org.topicquests.model.api.INode, org.topicquests.model.api.INode, java.lang.String)
   * Must return the virtualNode's locator
   * <code>mergeData</code> is a key=reason string, value=double weight
   */
  @Override
  public IResult createVirtualNode(IProxy primary, IProxy merge,
                                   Map<String,Double> mergeData, double confidence,
                                   String userLocator, String mergeReason, ITicket credentials) {
    environment.logDebug("DefaultVirtualizer.createVirtualNode- " + primary.getLocator() +
                         " " + merge.getLocator() + " " + userLocator);
    IResult result = new ResultPojo();
    // Both are the same node?
    if (primary.getLocator().equals(merge.getLocator()))
      return result;
    
    String provenanceLocator = null;
    JSONObject vNodeStuff = null;
    JSONObject mNodeStuff = null;
    IPostgresConnection conn = null;
    
    try {
      conn = connectionFactory.getConnection();
      conn.setProxyRole();
 
      // virtualNode will hold a proxy if either of the pair
      // is found to be a virtual node
      // otherwise, if it's null, we build one
	      
      IProxy virtualNode = null;
      IProxy mergedNode = null;
      boolean primaryHadVirtual = false; // default
      IResult r = new ResultPojo();
      
      // using MergeTupleProperty as a proxy for a merge operation
      boolean primaryIsMerged = (primary.getProperty(conn, ITQCoreOntology.MERGE_TUPLE_PROPERTY, r) != null);
      boolean mergeIsMerged = (merge.getProperty(conn, ITQCoreOntology.MERGE_TUPLE_PROPERTY, r) != null);
      environment.logDebug("DefaultVirtualizer.createVirtualNode-a " + primaryIsMerged +" "+mergeIsMerged);
      System.out.println("DefaultVirtualizer.createVirtualNode-x");
      
      // Both already merged?
      if (primaryIsMerged && mergeIsMerged)
        return result;
      System.out.println("DefaultVirtualizer.createVirtualNode-y");
      
      //////////////////////
      // TODO
      // IF BOTH are already merged, then how the heck did we get here?
      // TODO rewrite this with primaryIsMerged and mergedIsMerged
      /////////////////////
      // Have these two, or either one of them, been merged before?
      // Look for a virtual node in this pair
      // environment.logDebug("DefaultVirtualizer.createVirtualNode-ab " + database);
      r = database.getVirtualNodeIfExists(conn, primary, credentials);
      
      if (r.hasError())
        result.addErrorString(r.getErrorString());
      // environment.logDebug("DefaultVirtualizer.createVirtualNode-1 "+r.getResultObject());
      
      if (r.getResultObject() != null) {
        virtualNode = (IProxy)r.getResultObject();
        environment.logDebug("DefaultVirtualizer.createVirtualNode-2 "+r.getResultObject());
        primaryHadVirtual = true;
        mergedNode = merge;
        // found one: primary is virtual
      } else {
        // environment.logDebug("DefaultVirtualizer.createVirtualNode-3 "+merge);
        r = database.getVirtualNodeIfExists(conn, merge, credentials);
        // environment.logDebug("DefaultVirtualizer.createVirtualNode-4 "+r.getResultObject());
        if (r.hasError())
          result.addErrorString(r.getErrorString());
        if (r.getResultObject() != null) {
          virtualNode = (IProxy)r.getResultObject();
          mergedNode = primary;
          // found one: primaryHadVirtual defaults false
          // merge is virtual
        } // otherwise, no virtual proxy
      }
      // Here with or without a virtual proxy
      // environment.logDebug("DefaultVirtualizer.createVirtualNode-5 "+virtualNode+" "+mergedNode);

      String theType;
      List<String>sups;
      String smallImagePath, largeImagePath;
      // isPrivate will be what the virtual will be
      boolean isPrivate = false;
      // boolean mlp = false;
      // If so, then get that virtual IProxy and use it
      
      if (virtualNode != null) {
        // environment.logDebug("DefaultVirtualizer.createVirtualNode-6 "+virtualNode+" "+mergedNode);
        // the case where we are updating the virtual node due to
        // another merge with it
        // CHECK PRIVACY
        if (mergedNode.getIsPrivate()) {
          if (!virtualNode.getIsPrivate())
            // was public, now must be private
            virtualNode.setIsPrivate(conn, true);
        }
        
        r = database.getFullNodeJSON(conn, virtualNode.getLocator());
        vNodeStuff = (JSONObject)r.getResultObject();
        r = database.getFullNodeJSON(conn, mergedNode.getLocator());
        mNodeStuff = (JSONObject)r.getResultObject();

        theType = mergedNode.getNodeType();
        sups = mergedNode.listSuperclassIds(conn, r);
        smallImagePath = mergedNode.getSmallImage(false);
        largeImagePath = mergedNode.getImage(false);
        
        // SET-UNION the properties
        // That takes care of everything
        //////////////////////////
        // In this context, one of the two nodes already had a VirtualProxy;
        // It was already merged
        // We only need to setUnion the virtual to the as-yet unmerged node
        //////////////////////////
        setUnionProperties(conn, virtualNode, mergedNode, vNodeStuff, mNodeStuff, credentials);

        if (r.hasError())
          result.addErrorString(r.getErrorString());
        JSONObject textStuff = mergeText(virtualNode, mergedNode);
        textStuff.put(ITQCoreOntology.LOCATOR_PROPERTY, virtualNode.getLocator());
        database.removeFromCache(virtualNode.getLocator());
        database.getESProvider().getProvider().updateFullNode(virtualNode.getLocator(), TEXT_INDEX, textStuff, false);
					
        // WIRE the merge assertion
        r = super.wireMerge(conn, virtualNode, mergedNode, mergeData, confidence, provenanceLocator, userLocator);
        if (r.hasError())
          result.addErrorString(r.getErrorString());
        String tupleLocator = (String)r.getResultObject();
        
        // Save the merge tuple
        r = database.putNode(virtualNode);
        if (r.hasError())
          result.addErrorString(r.getErrorString());
      } else {
        environment.logDebug("DefaultVirtualizer.createVirtualNode-7 "+virtualNode+" "+mergedNode);
        /////////////////////////////
        // Otherwise, create a new virtual IProxy
        // Make it from the Primary
        // Still have to merge Primary AND Merge with that new node
        //////////////////////////////
        r = database.getFullNodeJSON(conn, primary.getLocator());
        vNodeStuff = (JSONObject)r.getResultObject();
        r = database.getFullNodeJSON(conn, merge.getLocator());
        mNodeStuff = (JSONObject)r.getResultObject();
				
        theType = primary.getNodeType();
        sups = primary.listSuperclassIds(false);
        smallImagePath = primary.getSmallImage(false);
        largeImagePath = primary.getImage(false);
        isPrivate = primary.getIsPrivate();
        boolean mip = merge.getIsPrivate();
        isPrivate = isPrivate || mip;
        
        /////////////////////////////////////////////////
        // POLICY
        // IF either node is private, the VirtualNode is private
        ////////////////////////////////////////////////
        // Process:
        // Make an empty shell node
        // add its labels
        // then STORE IT
        // After that, surgically add properties and details
        //    Includes type, supers, acls, psis and whatever else
        // No update necessary
        // THEN do the setUnion merge
        // THEN wire the merge for primary
        // THEN wire the merge for merge
        // THEN perform topicmap surgery for primary
        // THEN perform topicmap surgery for merge
        /////////////////////////////////////////////////
        // make the shell node
        virtualNode = nodeModel.newNode(null, null, "", userLocator, smallImagePath, largeImagePath, isPrivate);
        virtualNode.setIsVirtualProxy(true);
        if (theType != null)
          virtualNode.setNodeType(theType);
        if (sups != null && !sups.isEmpty()) {
          Iterator<String>itr = sups.iterator();
          while (itr.hasNext())
            virtualNode.addSuperclassId(itr.next(), true);
        }
        
        // DEAL WITH TransitiveClosure
        List<String>vtcl = (List<String>)vNodeStuff.get(ITQCoreOntology.TRANSITIVE_CLOSURE_PROPERTY_TYPE);
        List<String>mtcl = (List<String>)mNodeStuff.get(ITQCoreOntology.TRANSITIVE_CLOSURE_PROPERTY_TYPE);
        vtcl = setUnionLists(vtcl, mtcl);
        virtualNode.setTransitiveClosure(vtcl);
        
        // Save virtualnode before doing surgery on it
        r = database.putNode(virtualNode);
        if (r.hasError())
          result.addErrorString(r.getErrorString());
        JSONObject textStuff = mergeText(primary, merge);
        textStuff.put(ITQCoreOntology.LOCATOR_PROPERTY, virtualNode.getLocator());
        // environment.logDebug("DefaultVirtualizerABC "+textStuff);
        // {"lox":"43dff617-906a-40b2-87ba-e9a35cf72f70","details":{"en":["Yup 1","Yup 2"]},"label":{"en":["Test class 1","Test class 2"]}}
        database.removeFromCache(virtualNode.getLocator());
        database.getESProvider().getProvider().put(virtualNode.getLocator(), TEXT_INDEX, textStuff);
				
        //SetUnion against BOTH proxies to the new VirtualProxy
        //////////////////////////
        // In this context, neither of the two nodes already had a VirtualProxy;
        //  We built a new VirtualProxy
        // We need to setUnion the virtual to both as-yet unmerged nodes
        //////////////////////////
        r = database.getFullNodeJSON(conn, virtualNode.getLocator());
        JSONObject xNodeStuff = (JSONObject)r.getResultObject();
        setUnionProperties(conn, virtualNode, primary, xNodeStuff, vNodeStuff, credentials);
        r = database.getFullNodeJSON(conn, virtualNode.getLocator());
        xNodeStuff = (JSONObject)r.getResultObject();
        setUnionProperties(conn, virtualNode, merge, xNodeStuff, mNodeStuff, credentials);
        
        // virtualNode is now constructed
        // Time to wire them together
        r = super.wireMerge(conn, virtualNode, primary, mergeData, confidence, provenanceLocator, userLocator);
        if (r.hasError())
          result.addErrorString(r.getErrorString());
        r = super.wireMerge(conn, virtualNode, merge, mergeData, confidence, provenanceLocator, userLocator);
        if (r.hasError())
          result.addErrorString(r.getErrorString());
      }
      result.setResultObject(virtualNode.getLocator());
    } catch (SQLException e) {
      // environment.logDebug("DefaultVirtualizer.createVirtualNode-EXCEPTION: " + e.getMessage());
      result.addErrorString(e.getMessage());
      environment.logError(e.getMessage(), e);
    }
    
    conn.closeConnection(result);
    // environment.logDebug("DefaultVirtualizer.createVirtualNode+ "+result.getResultObject());
    return result;
  }
}
