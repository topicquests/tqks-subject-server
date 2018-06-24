/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import java.util.List;
import org.topicquests.pg.api.IPostgresConnection;


public interface ITuple extends IProxy {
  /**
   * Set relational weight
   * @param weight
   */
  void setRelationWeight(double weight);
	
  /**
   * Can return <code>-9999</code> if this {@link ITuple} is not weighted
   * @return
   */
  double getRelationWeight();
	
  /**
   * <p>Object refers to the object of a {subject,predicate,object} triple.</p>
   * <p>An object could be one of
   * <li>A literal (typed) value</li>
   * <li>A symbol, typically an identifier of another entity which could be one of
   * <li>Another tuple</li>
   * <li>A node</li></li></p>
   * @param value
   */
  void setObject(String value);
	
  /**
   * Set the object's type
   * @param typeLocator
   */
  void setObjectType(String typeLocator);
	
  /**
   * Return the object
   * @return
   */
  String getObject();
	
  /**
   * Return the object's type
   * @return
   */
  String getObjectType();
	
  /**
   * Set the object's role
   * @param roleLocator
   */
  void setObjectRole(String roleLocator);
	
  /**
   * Return the object's role
   * @return can return <code>null</code>
   */
  String getObjectRole();
	
  /**
   * <p>A subject is the subject in a {subject,predicate,object} triple</p>
   * <p>A subject is always the locator (identifier) for another entity, which could be one of
   * <li>A node</li>
   * <li>A tuple</li></p>
   * @param locator
   */
  void setSubjectLocator(String locator);
	
  /**
   * Return the subject locator
   * @return
   */
  String getSubjectLocator();
	
  /**
   * SubjectType refers to whether this subject is an ITuple type or some other
   * type in the typology
   * @param subjectType
   */
  void setSubjectType(String subjectType);
	
  /**
   * Return the subject's type
   * @return
   */
  String getSubjectType();
	
  /**
   * Roles are appropriate to relations among role-playing actors
   * @param roleLocator
   */
  void setSubjectRole(String roleLocator);
	
  /**
   * Return the subject/s role
   * @return can return <code>null</code>
   */
  String getSubjectRole();
	
  /**
   * Transclude means this tuple is transcluded from another source
   * @param isT
   */
  void setIsTransclude(boolean isT);
	
  /**
   * <p>A <em>signature</em> is a large string composed of:
   * subjectLocator + tupleType + objectLocator</p>
   * <p>It is intended to be used on tuples which link nodes</p>
   * @param signature
   */
  //	void setSignature(String signature);
	
  /**
   * Return this tuple's signature
   * @return can return <code>null</code>
   */
  //	String getSignature();
	
  /**
   * Utility
   * @param t
   */
  void setIsTransclude(String t);
	
  /**
   * Return <code>true</code> if this tuple represents a transcluded node
   * @return
   */
  boolean getIsTransclude();
	
  /**
   * Used only during bootstrap
   * Scopes are topics
   * @param scopeLocator
   */
  void addScope(String scopeLocator);
	
  /**
   * Add a merge reason given by fired merge rules
   * @param reason
   */
  void addMergeReason(String reason);
	
  void addMergeReason(IPostgresConnection conn, String reason);
  /**
   * Return merge reasons
   * @return can return <code>null</code>
   */
  List<String> listMergeReasons();
	
  /**
   * 
   * @return does not return <code>null</code>
   */
  List<String> listScopes();
	
  /**
   * Used mostly in merge
   * @param conn
   * @param scopes
   */
  void setScopeList(IPostgresConnection conn, List<String> scopes);
  
  /**
   * <p>A tuple can be used to represent a scene in which something occurs in a transaction.</p>
   * <p>Example: Mary gave a ball to Joe, where the <em>ball</em> is the theme in a <em>give</em>
   * transaction between two parties, Mary and Joe</p>
   * @param themeLocator
   */
  void setThemeLocator(String themeLocator);
	
  /**
   * 
   * @return can return <code>null</code>
   */
  String getThemeLocator();
}
