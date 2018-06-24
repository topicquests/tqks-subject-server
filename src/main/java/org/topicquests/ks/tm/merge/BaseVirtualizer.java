/**
 * Copyright 2015...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.merge;

import java.util.*;

import net.minidev.json.JSONObject;

import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.api.ICoreIcons;
import org.topicquests.ks.api.IExtendedCoreOntology;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.tm.api.IDataProvider;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.ks.tm.api.ITuple;
import org.topicquests.ks.tm.api.ITupleQuery;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.pg.api.IPostgresConnection;

/**
 * @author park
 * <p>SetUnion functions for all VirtualNode creation
 */
public class BaseVirtualizer {
  protected SystemEnvironment environment;
  protected IDataProvider database;
  protected PostgresConnectionFactory connectionFactory = null;
  protected IProxyModel nodeModel;
  protected ITupleQuery tupleQuery;
  protected final String TEXT_INDEX = "topics";

  public void init(SystemEnvironment env) {
    environment = env;
    database = environment.getDataProvider();
    connectionFactory = database.getDBProvider();
    nodeModel = environment.getProxyModel();
    tupleQuery = environment.getTupleQuery();
  }

  /**
   * <p>Perform a <em>Set Union</em> on various key/value pairs</p>
   * <p>This is ONLY appropriate for creation of a virtual proxy</p> //????
   * <p><em>stuff</em> includes all tuple locators</p>
   * @param virtualNode
   * @param mergedNode
   * @param vNodeStuff TODO
   * @param mNodeStuff TODO
   * @param credentials TODO
   * @param isTuple
   * @return
   */
  protected boolean setUnionProperties(IPostgresConnection conn, IProxy virtualNode,
                                       IProxy mergedNode, JSONObject vNodeStuff,
                                       JSONObject mNodeStuff, ITicket credentials) {
    // if any surgery is performed, result = true
    ////////////////////
    // A given node is mostly metadata
    // We have to fetch the rest of its properties
    // for this merge
    ///////////////////
		
    boolean result = false;
    // Copy over all labels and details for both nodes
    //	installLabelsAndDetails(virtualNode,mergedNode);
    // grab unrestricted tuples first
    // environment.logDebug("BaseVirtualizer.setUnionProperties- "+virtualNode.getLocator()+" "+mergedNode.getLocator());
    // environment.logDebug("BaseVirtualizer.setUnionProperties-a "+vNodeStuff);
    // environment.logDebug("BaseVirtualizer.setUnionProperties-b "+mNodeStuff);

    //other properties -- doing surgery on the map
    JSONObject sourceMap = mNodeStuff;
    JSONObject virtMap = vNodeStuff;
    Iterator<String>keys = sourceMap.keySet().iterator();
    String key;
    Object os;
    Object ov;
    List<String>sxx = null;
    List<String>vxx = null;
    IResult r = new ResultPojo();
    
    while (keys.hasNext()) {
      key = keys.next();
      // environment.logDebug("BaseVirtualizer.setUnionProperties-1 "+key);
      
      ///////////////////////////////
      // TODO
      // WE DO NOT *BUILD* THE VIRTMAP
      // INSTEAD, IF WE ARE TO ADD SOMETHING TO IT, WE USE IDataProvider.setProperty or addPropertyValue
      if (okToUse(key)) {
        os = sourceMap.get(key);
        ov = virtMap.get(key);
        // environment.logDebug("BaseVirtualizer.setUnionProperties-2 "+key+" "+ov+" "+os);

        //////////////////////////////
        // TODO
        // WE MAY NOT CARE ABOUT THIS
        // especially if we use List<Object>
        //////////////////////////////
        if (os instanceof String ||
            os instanceof Date ||
            os instanceof Long ||
            os instanceof Double ||
            os instanceof Integer ||
            os instanceof Float ||
            os instanceof Boolean) {
          ///////////////////////////////////////
          // This may be where we are creating dates as strings
          // and messing things up
          ///////////////////////////////////////
          if (ov == null && !(key.equals(ITQCoreOntology.CREATED_DATE_PROPERTY) ||
                              key.equals(ITQCoreOntology.LAST_EDIT_DATE_PROPERTY))) {
            // environment.logDebug("BaseVirtualizer.setUnionProperties-2a ");
            virtualNode.setProperty(conn, key, os, r);
            result = true;
          } else if (ov == null) {
            // environment.logDebug("BaseVirtualizer.setUnionProperties-2b ");
            virtualNode.setProperty(conn, key, (String)os, r);
          } else if (ov instanceof String && os instanceof String) {
            // environment.logDebug("BaseVirtualizer.setUnionProperties-2c ");
            
            // ov and os are strings: same key; make a list
            if (!ov.equals(os)) {
              virtualNode.setProperty(conn, key, os, r);
            }
          } else if (ov instanceof List) {
            // environment.logDebug("BaseVirtualizer.setUnionProperties-2d ");
            vxx = (List<String>)ov;
            if (!vxx.isEmpty() && !vxx.contains((String)os)) {
              virtualNode.addPropertyValue(conn, key, (String)os, r);
            }
          } else {
            environment.logDebug("WIERD "+key+" "+ov+" | "+os);
            // this fired on isLiv and isVrt
          }
        } else { //os must be a list
          // Source got a list
          sxx = (List<String>)os;
          // environment.logDebug("BaseVirtualizer.setUnionProperties-5 "+key+" "+sxx);
          if (sxx != null) {
            int len = sxx.size();
            for (int i=0;i<len;i++) {
              if (vxx == null || (vxx != null &&!vxx.contains(sxx.get(i)))) {
                virtualNode.addPropertyValue(conn, key, sxx.get(i), r);
              }
            }
          }
        }
      }
    }
    
    // now deal with PSIs
    setUnionPSIs(conn, virtualNode, vNodeStuff, mNodeStuff);
    // now the tuples
    setUnionTuples(conn, virtualNode, mergedNode, vNodeStuff, mNodeStuff, credentials);
    return result;
  }
  
  /**
   * "label":{"en":["ResourceType type"]}
   * @param vLabels
   * @param mLabels
   * @return
   */
  JSONObject mergeLabels(JSONObject vLabels, JSONObject mLabels) {
    JSONObject result = vLabels;
    
    if (result == null)
      result = mLabels;
    
    if (result == null)
      return new JSONObject();
    else {
      JSONObject jo = new JSONObject();
      JSONObject vlbl = vLabels;
      JSONObject mlbl = mLabels;
      Set<String>vs = vlbl.keySet();
      Set<String>vx = new HashSet<String>();
      
      vx.addAll(vs);
      environment.logDebug("BaseVirtualizerAA " + vx);
      Set<String>ms = mlbl.keySet();
      environment.logDebug("BaseVirtualizerBB " + ms);
      vx.addAll(ms);
      Iterator<String>itr = vx.iterator();
      String lang;
      List<String>vl, ml;
      
      while (itr.hasNext()) {
        lang = itr.next();
        vl = (List<String>)vlbl.get(lang);
        ml = (List<String>)mlbl.get(lang);
        if (vl != null && ml == null)
          jo.put(lang, vl);
        else if (vl == null && ml != null)
          jo.put(lang, ml);
        else
          jo.put(lang, setUnionLists(vl, ml));
      }
      result = jo;
    }
    return result;
  }

  /**
   * Tries to be order preserving
   * @param a
   * @param b
   * @return can return <code>null</code>
   */
  List<String> setUnionLists(List<String> a, List<String> b) {
    List<String>result = a;
    
    if (a != null && b != null) {
      Iterator<String>itr = b.iterator();
      String x;
      while (itr.hasNext()) {
        x = itr.next();
        if (!result.contains(x))
          result.add(x);
      }
    } else if (b != null) {
      result = b;
    }
    return result;
  }
	
  /**
   * "details":{"en":["Topic Map upper Resource type"]}
   * @param vDetails
   * @param mDetails
   * 
   * @return
   */
  JSONObject mergeDetails(JSONObject vDetails, JSONObject mDetails) {
    JSONObject result = vDetails;
    
    if (result == null)
      result = mDetails;
    
    if (result == null)
      return new JSONObject();
    else {
      JSONObject jo = new JSONObject();
      JSONObject vdtl = vDetails;
      JSONObject mdtl = mDetails;
      environment.logDebug("BaseVirtualizer.mergeDetails-1 " + vdtl.toJSONString());
      environment.logDebug("BaseVirtualizer.mergeDetails-2 " + mdtl.toJSONString());
      Set<String>vs = vdtl.keySet();
      Set<String>vx = new HashSet<String>();
      vx.addAll(vs);
      Set<String>ms = mdtl.keySet();
      vx.addAll(ms);
      Iterator<String>itr = vx.iterator();
      String lang;
      List<String>vl, ml;
      
      while (itr.hasNext()) {
        lang = itr.next();
        vl = (List<String>)vdtl.get(lang);
        ml = (List<String>)mdtl.get(lang);
        if (vl != null && ml == null)
          jo.put(lang, vl);
        else if (vl == null && ml != null)
          jo.put(lang, ml);
        else 
          jo.put(lang, setUnionLists(vl, ml));
      }
      result = jo;
    }
    return result;
  }

  void setUnionPSIs(IPostgresConnection conn, IProxy virtualNode, JSONObject vNodeStuff, JSONObject mNodeStuff) {
    List<String>vp = (List<String>)vNodeStuff.get(ITQCoreOntology.PSI_PROPERTY_TYPE);
    List<String>mp = (List<String>)mNodeStuff.get(ITQCoreOntology.PSI_PROPERTY_TYPE);
    List<String>  xp = setUnionLists(vp, mp);
    
    if (xp != null && !xp.equals(vp)) {
      xp = subtractElements(xp, vp);
      if (!xp.isEmpty())
        virtualNode.setPSIList(conn, xp);
    }
  }
	
  /**
   * Add only new values
   * @param target
   * @param src
   * @return
   */
  List<String> subtractElements(List<String> target, List<String>src) {
    List<String>result = src;
    
    if (result == null)
      result = target;
    else if (target != null) {
      Iterator<String>itr = target.iterator();
      String x;
      
      while (itr.hasNext()) {
        x = itr.next();
        if (result.contains(x))
          result.remove(x);
      }
    }
    return result;
  }
	
  boolean mergedIsSubject(JSONObject tup, String mergedLocator) {
    String subj = tup.getAsString(ITQCoreOntology.TUPLE_SUBJECT_PROPERTY);
    return subj.equals(mergedLocator);
  }
  
  boolean mergedIsObject(JSONObject tup, String mergedLocator) {
    String subj = tup.getAsString(ITQCoreOntology.TUPLE_OBJECT_PROPERTY);
    return subj.equals(mergedLocator);
  }
	
  boolean isTaxonomic(String lox) {
    return (lox.indexOf(IExtendedCoreOntology.INSTANCE_RELATION_TYPE) > 0 ||
            lox.indexOf(IExtendedCoreOntology.SUBCLASS_RELATION_TYPE) > 0);
  }

  /**
   * 
   * @param conn
   * @param virtualNode
   * @param mergedNode
   * @param vNodeStuff
   * @param mNodeStuff
   * @param credentials TODO
   * @throws RuntimeException
   */
  void setUnionTuples(IPostgresConnection conn, IProxy virtualNode, IProxy mergedNode,
                      JSONObject vNodeStuff, JSONObject mNodeStuff, ITicket credentials) {
    // Get all the tuple locators; some will be taxonomic
    List<String>vp = (List<String>)vNodeStuff.get(ITQCoreOntology.TUPLE_LIST_PROPERTY);
    List<String>mp = (List<String>)mNodeStuff.get(ITQCoreOntology.TUPLE_LIST_PROPERTY);
    
    String mergedNodeLocator = mergedNode.getLocator();
    String lox;
    Iterator<String> itr = mp.iterator();
    IResult r;
    JSONObject tup;
    IProxy other;
    
    ///////////////////////////
    // For each relation
    //  
    // 1- Make sure it's not already in the vp
    // 2- Get the relation from the merged node
    // 3- Get the *other* node
    // 4- Relate other node to virtual the same way (subject or object) it was
    // 5- Include other properties -- roles, provenance, etc
    //////////////////////////
    String otherLox;
    String relationType;
    String userId;
    boolean isTransclude = false;
    boolean isPrivate = false;
    IProxy src, tgt;
    ITuple theTup;
    List<String>scopes;
    
    while (itr.hasNext()) {
      lox = itr.next();
      if (!isTaxonomic(lox)) {
        if (!vp.contains(lox)) {
          r = database.getFullNodeJSON(conn, lox);
          tup = (JSONObject)r.getResultObject();
          environment.logDebug("BaseVirtualizer.setUnionTuples "+mergedNode.getLocator()+" "+tup);
          relationType = tup.getAsString("node_type");
          isPrivate = (Boolean)tup.get(ITQCoreOntology.IS_PRIVATE_PROPERTY);
          
          if (tup.get(ITQCoreOntology.TUPLE_IS_TRANSCLUDE_PROPERTY) != null)
            isTransclude = (Boolean)tup.get(ITQCoreOntology.TUPLE_IS_TRANSCLUDE_PROPERTY);
          
          userId = tup.getAsString(ITQCoreOntology.CREATOR_ID_PROPERTY);
          boolean mergedIsSubject = mergedIsSubject(tup, mergedNodeLocator);
          scopes = (List<String>)tup.get(ITQCoreOntology.SCOPE_LIST_PROPERTY_TYPE);
          
          if (!mergedIsSubject) {
            otherLox = tup.getAsString(ITQCoreOntology.TUPLE_OBJECT_PROPERTY);
            //sanity
            if (!mergedIsObject(tup, mergedNodeLocator))
              throw new RuntimeException("BaseVirtulizer.setUnionTuples bad tuple "+tup.toJSONString());
						
          } else
            otherLox = tup.getAsString(ITQCoreOntology.TUPLE_SUBJECT_PROPERTY);
          
          r = database.getNode(otherLox, credentials);
          other = (IProxy)r.getResultObject();
          src = virtualNode;
          tgt = other;
          
          if (mergedIsSubject) {
            src = other;
            tgt = virtualNode;
          }
          
          r = nodeModel.relateExistingNodes(src, tgt, relationType, null, null, userId, null, 
                                            tup.getAsString(ITQCoreOntology.SMALL_IMAGE_PATH),
                                            tup.getAsString(ITQCoreOntology.LARGE_IMAGE_PATH),
                                            isTransclude, isPrivate);
          
          theTup = (ITuple)r.getResultObject();
          if (scopes != null && !scopes.isEmpty()) {
            theTup.setScopeList(conn, scopes);
          }
        }
      }
    }
  }
  
  //////////////////////////////////////
  // WE must filter some keys which do not apply to
  // SetUnion of properties operations
  // PSI properties go in their own table
  // Label and Details go to ElasticSearch
  // ACLS are not included in a VirtualProxy
  //	They are the subject of an SQL query if someone tries
  //   to access a private VirtualProxy
  // Tuples are subject to the creation of new tuples which
  //   are the set-union of existing tuples between the
  //   merged and virtual proxy
  // Transitive closure is in a different table
  // Subclass list is about superclasses which will be set-unioned
  //  for that table, but also picked up for the taxonomic tuples
  /////////////////////////////////////
  /**
   * Filter out certain keys
   * @param key
   * @return
   */
  boolean okToUse(String key) {
    if (key.equals(ITQCoreOntology.LOCATOR_PROPERTY) ||
        key.equals(ITQCoreOntology.CREATED_DATE_PROPERTY) ||
        key.equals(ITQCoreOntology.LAST_EDIT_DATE_PROPERTY) ||
        key.equals(ITQCoreOntology.CREATOR_ID_PROPERTY) ||
        key.equals(ITQCoreOntology.IS_PRIVATE_PROPERTY) ||
        key.equals(ITQCoreOntology.LABEL_PROPERTY) ||
        key.equals(ITQCoreOntology.DETAILS_PROPERTY) ||
        key.equals(ITQCoreOntology.PSI_PROPERTY_TYPE) ||
        key.equals(ITQCoreOntology.TRANSITIVE_CLOSURE_PROPERTY_TYPE) ||
        key.equals(ITQCoreOntology.RESTRICTION_PROPERTY_TYPE) ||
        key.equals(ITQCoreOntology.SUBCLASS_OF_PROPERTY_TYPE) ||
        key.equals(ITQCoreOntology.TUPLE_LIST_PROPERTY))
      return false;
    return true;
  }
  /**
   * Adding to a singleValued field
   * @param key
   * @return false if not ok to add
   */
  boolean okToAdd(String key) {
    if (key.equals(ITQCoreOntology.LOCATOR_PROPERTY) ||
        key.equals(ITQCoreOntology.CREATED_DATE_PROPERTY) ||
        key.equals(ITQCoreOntology.LAST_EDIT_DATE_PROPERTY) ||
        key.equals(ITQCoreOntology.CREATOR_ID_PROPERTY) ||
        key.equals(ITQCoreOntology.PSI_PROPERTY_TYPE) ||
        key.equals(ITQCoreOntology.RESOURCE_URL_PROPERTY) ||
        key.equals(ITQCoreOntology.INSTANCE_OF_PROPERTY_TYPE) ||
        key.equals(ITQCoreOntology.IS_PRIVATE_PROPERTY))
      return false;
    return true;
  }
	
  /**
   * Wire up a MergeAssertion
   * @param conn TODO
   * @param virtualProxy
   * @param targetProxy
   * @param mergeData key = reason, value = vote
   * @param mergeConfidence not used at the moment
   * @param provenanceLocator
   * @param userLocator
   * @return tupleLocator is included
   */
  IResult wireMerge(IPostgresConnection conn, IProxy virtualProxy, IProxy targetProxy,
                    Map<String, Double> mergeData, double mergeConfidence,
                    String provenanceLocator, String userLocator) {
    IResult result = relateNodes(conn, virtualProxy, targetProxy, userLocator, provenanceLocator,
                                 ICoreIcons.RELATION_ICON_SM, ICoreIcons.RELATION_ICON, false,
                                 targetProxy.getIsPrivate(), mergeData);
    
    // result contains the tuple's locator
    // use that to fetch it and wire up scopes from mergeData	
    return result;
  }
	
  /**
   * <p>Form a tuple of this triple pattern:<br/>
   *  { virtualProxy, MergeRelation, mergedProxy }
   *  </p>
   * <p>Interpret that: a VirtualProxy has radiating outward from it
   * a collection of merged proxies</p>
   * Forge a merge relation--not using the version in INodeModel
   * @param conn TODO
   * @param virtualNode
   * @param targetNode
   * @param userId
   * @param smallImagePath
   * @param largeImagePath
   * @param isTransclude
   * @param isPrivate
   * @param mergeData
   * @return locator of the created tuple
   */
  private IResult relateNodes(IPostgresConnection conn, IProxy virtualNode, IProxy targetNode,
                              String userId, String provenanceLocator, String smallImagePath,
                              String largeImagePath, boolean isTransclude,
                              boolean isPrivate, Map<String, Double> mergeData) {
    String relationTypeLocator = ITQCoreOntology.MERGE_ASSERTION_TYPE;
    
    database.removeFromCache(virtualNode.getLocator());
    database.removeFromCache(targetNode.getLocator());
    
    IResult result = new ResultPojo();

    // String signature = virtualNode.getLocator()+ITQCoreOntology.MERGE_ASSERTION_TYPE+targetNode.getLocator();
    // NOTE that we make the tuple an instance of the relation type, not of TUPLE_TYPE
    IResult r = nodeModel.relateExistingNodes(targetNode, virtualNode, relationTypeLocator, 
                                              null, null, userId, provenanceLocator, smallImagePath,
                                              largeImagePath, isTransclude, isPrivate);
    ITuple t = (ITuple)r.getResultObject();
    environment.logDebug("BaseVirtualizer.relateNodes " + r.getErrorString() + " " + t);
    
    Iterator<String>itx = mergeData.keySet().iterator();
    String reason;
    while (itx.hasNext()) {
      reason = itx.next();
      t.addMergeReason(conn, reason+" "+mergeData.get(reason));
    }
    environment.logDebug("MergeBean.relateNodes " + virtualNode.getLocator() + " " + targetNode.getLocator()
                         + " " + t.getLocator());
    
    String tLoc = t.getLocator();

    // save the tuple's locator in the output
    result.setResultObject(tLoc);
    addPropertyValue(virtualNode,ITQCoreOntology.MERGE_TUPLE_PROPERTY,tLoc);
    changePropertyValue(targetNode,ITQCoreOntology.MERGE_TUPLE_PROPERTY,tLoc);
    environment.logDebug("MergeBean.relateNodes+ " + result.getErrorString());
    return result;
  }
	
  /**
   * Surgically change a property value (NOT a list value)
   * @param node
   * @param key
   * @param newValue
   * @return
   */
  void changePropertyValue(IProxy node, String key, String newValue) {
    environment.logDebug("MergeBean.changePropertyValue- " + node.getLocator() + " " + key + " " + newValue);
    JSONObject p = node.getData();
    p.put(key, newValue);
    database.removeFromCache(node.getLocator());
  }
	
  void addPropertyValue(IProxy node, String key, String newValue) {
    environment.logDebug("MergeBean.addPropertyValue- " + node.getLocator() + " " + key + " " + newValue);
    String sourceNodeLocator = node.getLocator();
    JSONObject propMap = node.getData();
    Object ox = propMap.get(key);
    environment.logDebug("MergeBean.addPropertyValue-1 " + ox);
    
    if (ox == null) {
      propMap.put(key, newValue);
    } else {
      if (ox instanceof String)
        propMap.put(key, newValue);
      else {
        List<String> l = (List<String>)ox;
        l.add(newValue);
      }
    }
    database.removeFromCache(node.getLocator());
  }
	
  /**
   * Craft a complete JSON document with labels and details
   * @param src
   * @param mrg
   * @return
   */
  JSONObject mergeText(IProxy src, IProxy mrg) {
    IResult r = database.getESProvider().getProvider().get(src.getLocator(), TEXT_INDEX);
    JSONObject vtxt = (JSONObject)r.getResultObject();
    r = database.getESProvider().getProvider().get(mrg.getLocator(), TEXT_INDEX);
    
    JSONObject mtxt = (JSONObject)r.getResultObject();
    JSONObject result = null;
    
    if (vtxt == null && mtxt == null)
      return result;
    else if (vtxt != null && mtxt == null)
      return vtxt;
    else if (vtxt == null && mtxt != null)
      return mtxt;

    result = new JSONObject();
    JSONObject vlbl = (JSONObject)vtxt.get(ITQCoreOntology.LABEL_PROPERTY);
    JSONObject vdtl = (JSONObject)vtxt.get(ITQCoreOntology.DETAILS_PROPERTY);
    JSONObject mlbl = (JSONObject)mtxt.get(ITQCoreOntology.LABEL_PROPERTY);
    JSONObject mdtl = (JSONObject)mtxt.get(ITQCoreOntology.DETAILS_PROPERTY);
    
    mlbl = this.mergeLabels(vlbl, mlbl);
    mdtl = this.mergeDetails(vdtl, mdtl);
    environment.logDebug("BaseVirtualizer.mergeText " + mdtl);
    result.put(ITQCoreOntology.LABEL_PROPERTY, mlbl);
    result.put(ITQCoreOntology.DETAILS_PROPERTY, mdtl);
    return result;
  }
}
