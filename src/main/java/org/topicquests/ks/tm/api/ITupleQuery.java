/**
 * Copyright 2012...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import org.topicquests.support.api.IResult;
import org.topicquests.ks.api.ITicket;
import org.topicquests.pg.api.IPostgresConnection;

/**
 * @author park
 * <p>Common Queries to be implemented for different databases</p>
 */
public interface ITupleQuery {
	
  /**
   * Find a {@link ITuple} by its unique <code>signature</code>
   * @param signature
   * @param credentials
   * @return can return <code>null</code> inside {@link IResult}
   */
  //replaced with getTuple since the signature IS the locator
  //IResult getTupleBySignature(String signature, ITicket credentials);
	
  /**
   * Starts and ends a transaction
   * <p>List all available {@link ITuple}s which are instances of <code>predType</code>
   * and have <code>obj</code> as either the subject or object</p>
   * @param predType
   * @param obj
   * @param start
   * @param count
   * @param sortBy can be <code>null</code>
   * @param sortDir can be <code>null</code>
   * @param credentials
   * @return
   */
  IResult listTuplesByPredTypeAndObjectOrSubject(String predType, String obj, int start, int count,
                                                 String sortBy, String sortDir, ITicket credentials);
	
  /**
   * Runs inside a transaction
   * @param conn
   * @param predType
   * @param obj
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  IResult listTuplesByPredTypeAndObjectOrSubject(IPostgresConnection conn, String predType, String obj,
                                                 int start, int count, String sortBy, String sortDir,
                                                 ITicket credentials);
	
  /**
   * <p>List locators of all available {@link ITuple}s which have any of the <code>labels</code></p>
   * @param labels
   * @param start
   * @param count
   * @param sortBy can be <code>null</code>
   * @param sortDir can be <code>null</code>
   * @param credentials 
   * @return
   */
  IResult listTuplesByLabel(String [] labels, int start, int count, String sortBy, String sortDir, ITicket credentials);
	
  /**
   * Starts and ends a transaction
   * @param subjectLocator
   * @param start
   * @param count
   * @param sortBy can be <code>null</code>
   * @param sortDir can be <code>null</code>
   * @param credentials
   * @return
   */
  IResult listTuplesBySubject(String subjectLocator, int start, int count, String sortBy,
                              String sortDir, ITicket credentials);

  /**
   * Runs inside a transaction
   * @param conn
   * @param subjectLocator
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  IResult listTuplesBySubject(IPostgresConnection conn, String subjectLocator, int start, int count,
                              String sortBy, String sortDir, ITicket credentials);

  /**
   * Starts and ends a transaction
   * For tuples where the object is a nodeLocator, list all of them
   * @param objectLocator
   * @param start 
   * @param count 
   * @param sortBy can be <code>null</code>
   * @param sortDir can be <code>null</code>
   * @param credentials
   * @return
   */
  IResult listTuplesByObjectLocator(String objectLocator, int start, int count, String sortBy,
                                    String sortDir, ITicket credentials);

  /**
   * Runs inside a transaction
   * @param conn
   * @param objectLocator
   * @param start
   * @param count
   * @param sortBy
   * @param sortDir
   * @param credentials
   * @return
   */
  IResult listTuplesByObjectLocator(IPostgresConnection conn, String objectLocator, int start, int count,
                                    String sortBy, String sortDir, ITicket credentials);

	
  /**
   * <p>Return a list of <code>ITuple</code> objects inside an {@link IResult} object</p>
   * <p>This is the core way to fetch an list of <code>ITuple</code> object when
   * the desired result is to learn all the <code>subjectId</code> values that contain that
   * key/value pair.</p>
   * @param predType
   * @param obj
   * @param start
   * @param count
   * @param sortBy can be <code>null</code>
   * @param sortDir can be <code>null</code>
   * @param credentials
   * @return -- an IResult object that contains List[ITuple] or an error message
   */
  IResult listTuplesByPredTypeAndObject(String predType, String obj, int start, int count,
                                        String sortBy, String sortDir, ITicket credentials);
	
  /**
   * <p>Return a list of <code>ITuple</code> objects inside an {@link IResult} object</p>
   * <p>This is the core way to fetch an list of <code>ITuple</code> objects</p>
   * @param subjectLocator
   * @param predType
   * @param start
   * @param count
   * @param sortBy can be <code>null</code>
   * @param sortDir can be <code>null</code>
   * @param credentials
   * @return
   */
  IResult listTuplesBySubjectAndPredType(String subjectLocator, String predType, int start, int count,
                                         String sortBy, String sortDir, ITicket credentials);
	
  /**
   * <p>Return a possibly empty list of {@link INode} objects which correspond
   * with <code>objectLocator</code> by relation <code>relationLocator</code></p>
   * @param objectLocator
   * @param relationLocator
   * @param start 
   * @param count 
   * @param sortBy can be <code>null</code>
   * @param sortDir can be <code>null</code>
   * @param credentials
   * @return <code>List<INode></code>
   */
  IResult listSubjectNodesByObjectAndRelation(String objectLocator, String relationLocator,
                                              int start, int count, String sortBy, String sortDir,
                                              ITicket credentials);
	
  /**
   * <p>Return a possibly empty list of {@link INode} objects which correspond with
   * <code>subjectLocator</code> by relation <code>relationLocator</code></p>
   * @param subjectLocator
   * @param relationLocator
   * @param start 
   * @param count 
   * @param sortBy can be <code>null</code>
   * @param sortDir can be <code>null</code>
   * @param credentials
   * @return <code>List<INode></code>
   */
  IResult listObjectNodesBySubjectAndRelation(String subjectLocator, String relationLocator,
                                              int start, int count, String sortBy, String sortDir,
                                              ITicket credentials);
	
	
  /**
   * <p>Return a possibly empty list of {@link INode} objects which correspond with
   * <code>subjectLocator</code> by relation <code>relationLocator</code>,
   * and includes the scope <code>scopeLocator</code></p>
   * @param subjectLocator
   * @param relationLocator
   * @param start 
   * @param count 
   * @param sortBy can be <code>null</code>
   * @param sortDir can be <code>null</code>
   * @param credentials
   * @return <code>List<INode></code>
   */
  IResult listObjectNodesBySubjectAndRelationAndScope(String subjectLocator, String relationLocator,
                                                      String scopeLocator, int start, int count,
                                                      String sortBy, String sortDir, ITicket credentials);
	
  /**
   * <p>Return a possibly empty list of {@link INode} objects which correspond
   * with <code>objectLocator</code> by relation <code>relationLocator</code></p>
   * @param objectLocator
   * @param relationLocator
   * @param start 
   * @param count 
   * @param sortBy can be <code>null</code>
   * @param sortDir can be <code>null</code>
   * @param credentials
   * @return <code>List<INode></code>
   */
  IResult listSubjectNodesByObjectAndRelationAndScope(String objectLocator, String relationLocator,
                                                      String scopeLocator, int start, int count,
                                                      String sortBy, String sortDir, ITicket credentials);
	
  IResult listSubjectNodesByRelationAndObjectRole(String relationLocator, String objectRoleLocator,
                                                  int start, int count, String sortBy, String sortDir,
                                                  ITicket credentials);
	
  IResult listSubjectNodesByRelationAndSubjectRole(String relationLocator, String subjectRoleLocator,
                                                   int start, int count, String sortBy, String sortDir,
                                                   ITicket credentials);
	
  IResult listObjectNodesByRelationAndSubjectRole(String relationLocator, String subjectRoleLocator,
                                                  int start, int count, String sortBy, String sortDir,
                                                  ITicket credentials);

  IResult listObjectNodesByRelationAndSubjectRole(IPostgresConnection conn, String relationLocator,
                                                  String subjectRoleLocator, int start, int count,
                                                  String sortBy, String sortDir, ITicket credentials);

  IResult listObjectNodesByRelationAndObjectRole(String relationLocator, String objectRoleLocator,
                                                 int start, int count, String sortBy, String sortDir,
                                                 ITicket credentials);
  //TODO fetching tuples which weight criteria
}
