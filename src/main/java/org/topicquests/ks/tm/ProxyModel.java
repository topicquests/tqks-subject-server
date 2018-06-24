/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import net.minidev.json.JSONObject;

import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.IExtendedCoreOntology;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.DataProvider;
// import org.topicquests.ks.tm.api.IMergeImplementation;
import org.topicquests.ks.tm.api.IParentChildContainer;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.ks.tm.api.ITuple;
import org.topicquests.pg.api.IPostgresConnection;

public class ProxyModel implements IProxyModel {
  private SystemEnvironment environment;
  private DataProvider database;
  private ITicket credentials;
  private final String USER_ID = ITQCoreOntology.SYSTEM_USER;

  public ProxyModel(SystemEnvironment env, DataProvider db) {
    environment = env;
    database = db;
    credentials = new TicketPojo(ITQCoreOntology.SYSTEM_USER);
  }

  /* (non-Javadoc)
   * @see org.topicquests.node.api.INodeProviderModel#addSuperClass(org.topicquests.node.api.IProxy, java.lang.String)
   */
  @Override
  public IResult addSuperClass(IProxy node, String superClassLocator, String provenanceLocator) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.topicquests.node.api.INodeProviderModel#setNodeType(org.topicquests.node.api.IProxy, java.lang.String)
   */
  @Override
  public IResult setNodeType(IProxy node, String typeLocator, String provenanceLocator) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.topicquests.node.api.INodeProviderModel#newNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
   */
  @Override
  public IProxy newNode(String locator, String label, String description,
                        String lang, String userId, String smallImagePath,
                        String largeImagePath, boolean isPrivate) {
    IProxy result = new Proxy(new JSONObject());
    result.setLocator(locator);
    result.setCreatorId(userId);
    Date d = new Date();
    result.setDate(d); 
    result.setLastEditDate(d);
    if (label != null)
      result.addLabel(label, lang);
		
    if (smallImagePath != null)
      result.setSmallImage(smallImagePath, true);
    if (largeImagePath != null)
      result.setImage(largeImagePath, true);
    if (description != null)
      result.addDetails(description, lang);
    result.setIsPrivate(isPrivate);
    result.setVersion(Long.toString(System.currentTimeMillis()));
    return result;
  }

  /* (non-Javadoc)
   * @see org.topicquests.node.api.INodeProviderModel#newNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
   */
  @Override
  public IProxy newNode(String label, String description, String lang,
                        String userId, String smallImagePath, String largeImagePath,
                        boolean isPrivate) {
    IProxy result = newNode(newUUID(),label,description,lang,userId,smallImagePath,largeImagePath,isPrivate);
    return result;
  }

  /* (non-Javadoc)
   * @see org.topicquests.node.api.INodeProviderModel#newSubclassNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
   */
  @Override
  public IProxy newSubclassNode(String locator, String superclassLocator, String label,
                                String description, String lang, String userId,
                                String provenanceLocator, String smallImagePath,
                                String largeImagePath, boolean isPrivate) {
    IProxy result = newNode(locator, label, description, lang, userId,
                            smallImagePath, largeImagePath, isPrivate);
    
    if (!locator.equals(ITQCoreOntology.TYPE_TYPE)) {
      // "TypeType" is the root of the typology. No transitive closure
      List<String>tc = listTransitiveClosure(superclassLocator);
      if (!tc.isEmpty()) {
        tc.add(superclassLocator);
        result.setTransitiveClosure(tc);
      }
    }
    
    result.addSuperclassId(superclassLocator, true);
    IResult r = createSuperClassRelation(result, superclassLocator, provenanceLocator);
    return result;
  }

  /* (non-Javadoc)
   * @see org.topicquests.node.api.INodeProviderModel#newSubclassNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
   */
  @Override
  public IProxy newSubclassNode(String superclassLocator, String label, String description,
                                String lang, String userId, String provenanceLocator,
                                String smallImagePath, String largeImagePath, boolean isPrivate) {
    return newSubclassNode(newUUID(), superclassLocator, label, description, lang,
                           userId, smallImagePath, largeImagePath, isPrivate);
  }

  /* (non-Javadoc)
   * @see org.topicquests.node.api.INodeProviderModel#newInstanceNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
   */
  @Override
  public IProxy newInstanceNode(String locator, String typeLocator, String label,
                                String description, String lang, String userId,
                                String provenanceLocator, String smallImagePath,
                                String largeImagePath, boolean isPrivate) {
    IProxy result = newNode(locator, label, description, lang, userId,
                            smallImagePath, largeImagePath, isPrivate);
    List<String>tc = listTransitiveClosure(typeLocator);
    
    if (!tc.isEmpty()) {
      tc.add(typeLocator);
      result.setTransitiveClosure(tc);
    }
    
    result.setNodeType(typeLocator);
    createInstanceRelation(result, typeLocator, provenanceLocator);
    return result;
  }

  /* (non-Javadoc)
   * @see org.topicquests.node.api.INodeProviderModel#newInstanceNode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
   */
  @Override
  public IProxy newInstanceNode(String typeLocator, String label, String description,
                                String lang, String userId, String provenanceLocator,
                                String smallImagePath, String largeImagePath, boolean isPrivate) {
    return newInstanceNode(newUUID(), typeLocator, label, description, lang, userId,
                           provenanceLocator, smallImagePath, largeImagePath, isPrivate);
  }

  /**
   * Returns the new Relation node
   * @param subClass
   * @param superClassLocator
   * @param provenanceLocator
   * @return
   */
  IResult createSuperClassRelation(IProxy subClass, String superClassLocator, String provenanceLocator) {
    IResult result = database.getNode(superClassLocator, credentials);
    IProxy sup = (IProxy)result.getResultObject();
    result = relateExistingNodes(subClass, sup, IExtendedCoreOntology.SUBCLASS_RELATION_TYPE,
                                 null, null, USER_ID, provenanceLocator,
                                 "/images/snowflake_sm.png", "/images/snowflake.png", false, false
                                 );
    return result;
  }
	
  IResult createInstanceRelation(IProxy instanceClass, String typeClassLocator, String provenanceLocator) {
    IResult result = database.getNode(typeClassLocator, credentials);
    IProxy sup = (IProxy)result.getResultObject();
    environment.logDebug("ProxyModel.createInstanceRelation "+typeClassLocator+" "+sup);
    result = relateExistingNodes(instanceClass, sup, IExtendedCoreOntology.INSTANCE_RELATION_TYPE,
                                 null, null, USER_ID, provenanceLocator,
                                 "/images/snowflake_sm.png", "/images/snowflake.png", false, false
                                 );
    return result;
  }


  ITuple newTaxonomicRelation(String locator, String relnType, String sourceLabel,
                              String targetLabel, String userId, boolean isPrivate) {
    String label = relnType;
    String details = "Relate existing nodes:<br/>" + sourceLabel + "<br/> with</br> " +
        targetLabel + "<br/>with the relation: " + relnType;
    String language = "en";
    ITuple result = (ITuple)newNode(locator, label, details, language, userId,
                                    "/images/snowflake_sm.png", "/images/snowflake.png", isPrivate);
    result.setNodeType(relnType);
    return result;
  }
	
  /* (non-Javadoc)
   * @see org.topicquests.node.api.INodeProviderModel#relateExistingNodes(org.topicquests.node.api.IProxy, org.topicquests.node.api.IProxy, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
   */
  @Override
  public IResult relateExistingNodes(IProxy subjectNode, IProxy objectNode, String relationTypeLocator,
                                     String subjectRoleLocator, String objectRoleLocator,
                                     String userId, String provenanceLocator, String smallImagePath,
                                     String largeImagePath, boolean isTransclude, boolean isPrivate) {
    environment.logDebug("ProxyModel.relateExistingNodes- " + subjectNode.getLocator() + " " +
                         objectNode + " " + relationTypeLocator);
    IResult result = new ResultPojo();
    IResult r = database.getNode(relationTypeLocator, credentials);
    if (r.hasError()) result.addErrorString(r.getErrorString());

    IProxy reltyp = (IProxy)r.getResultObject();

    ////////////////////
    // Bootstrapping patch
    // A hack: professional programmer
    // Don't try this at home
    //
    environment.logDebug("ProxyModel.relateExistingNodes-1 " + objectNode);
    String relnLabel = relationTypeLocator; // default
    if (reltyp != null)
      relnLabel = reltyp.getLabel("en");
 
    String targetNodeLocator = relationTypeLocator; //default
    if (objectNode != null)
      targetNodeLocator = objectNode.getLocator();
    
    String tlab = relationTypeLocator; //default
    if (objectNode != null)
      tlab = objectNode.getLabel("en");

    //
    ////////////////////
    String signature = subjectNode.getLocator()+relationTypeLocator+targetNodeLocator;
    String slab = subjectNode.getLabel("en");
    environment.logDebug("ProxyModel.relateExistingNodes-2 "+objectNode);
    ITuple t = null;
    
    if (relationTypeLocator.equals(IExtendedCoreOntology.INSTANCE_RELATION_TYPE) ||
        relationTypeLocator.equals(IExtendedCoreOntology.SUBCLASS_RELATION_TYPE)) {
      t = newTaxonomicRelation(signature, relationTypeLocator, slab, tlab, userId, isPrivate);
    } else {
      t = (ITuple)this.newInstanceNode(signature, relationTypeLocator, relnLabel,
                                       "Relate existing nodes:<br/>" + slab + "<br/> with</br> " + tlab +
                                       "<br/>with the relation: " + relationTypeLocator, "en",
                                       userId, provenanceLocator, smallImagePath, largeImagePath, isPrivate);
    }

    environment.logDebug("ProxyModel.relateExistingNodes-3 " + t + " " +
                         ((objectNode != null) ? objectNode.getLocator() : "null"));
    environment.logDebug("ProxyModel.relateExistingNodes " + signature + " " + t.getLocator() +
                         " " + ((objectNode != null) ? objectNode.getLocator() : "null"));
    
    t.setIsTransclude(isTransclude);
    if (objectNode != null) {
      t.setObject(objectNode.getLocator());
    }
    t.setObjectType(ITQCoreOntology.NODE_TYPE);
    t.setSubjectLocator(subjectNode.getLocator());
    t.setSubjectType(ITQCoreOntology.NODE_TYPE);
    
    // NOTE: this is a live update
    subjectNode.doUpdate(); // update version
    // NOTE: this is a live update
    if (objectNode != null) {
      objectNode.doUpdate();
    }
    database.putNode(t);
    t.addScope(provenanceLocator);
    environment.logDebug("ProxyModel.relateNewNodes " + subjectNode.getLocator() + " " +
                         ((objectNode != null) ? objectNode.getLocator() : "null") + " " +
                         t.getLocator() + " | " + result.getErrorString());

    result.setResultObject(t);
    return result;
  }

  private String newUUID() {
    return UUID.randomUUID().toString();
  }
	
  /**
   * Returns transitive closure for a given node
   * @param parentLocator
   * @return does not return <code>null</code>
   */
  private List<String> listTransitiveClosure(String parentLocator) {
    IResult x = database.getNode(parentLocator, credentials);
    IProxy n = (IProxy)x.getResultObject();
    environment.logDebug("ProxyModel.listTransitiveClosure " + n);
    List<String>result = new ArrayList<String>();
    
    if (n != null) {
      // clone the list
      List<String> temp  = n.listTransitiveClosure();
      if (temp != null) {
        Iterator<String>itr = temp.iterator();
        while (itr.hasNext())
          result.add(itr.next());
      }
    }
    
    environment.logDebug("ProxyModel.listTransitiveClosure+ " + result);
    return result;
  }

  @Override // not implemented yet
  public IResult addParentNode(IProxy proxy, String contextLocator, String parentLocator) {
    IResult result = new ResultPojo();
    return result;
  }
  
  @Override
  public IResult addChildNode(IProxy proxy, String contextLocator,
                              String childLocator, String transcludeLocator) {
    IResult result = new ResultPojo();
    IResult r = null;

    // add child to proxy
    ((IParentChildContainer)proxy).addChildNode(contextLocator, childLocator, "");

    String lox = proxy.getLocator();
    IParentChildContainer root = null;
    
    if (lox.equals(contextLocator))
      root = (IParentChildContainer)proxy;
    else {
      r = database.getNode(contextLocator, credentials);
      if (r.hasError())
        result.addErrorString(r.getErrorString());
      root = (IParentChildContainer)r.getResultObject();
    }

    return result;
  }    
}
