/**
 * Copyright 2013...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.tm.api.IDataProvider;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.ITuple;
import org.topicquests.ks.tm.api.ITupleQuery;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.pg.api.IPostgresConnection;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * @author park
 *
 */
public class TupleQuery implements ITupleQuery {
	private SystemEnvironment environment;
	private IDataProvider database;
	  private PostgresConnectionFactory provider = null;

	/**
	 * 
	 */
	public TupleQuery(SystemEnvironment env, IDataProvider d) {
		environment = env;
		database = d;
		provider = database.getDBProvider();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listObjectNodesByRelationAndObjectRole(java.lang.String, java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listObjectNodesByRelationAndObjectRole(String relationLocator, String objectRoleLocator, int start, int count, String sortBy, String sortDir, ITicket credentials) {
		environment.logDebug("TupleQuery.listObjectNodesByRelationAndObjectRole- "+relationLocator+" "+objectRoleLocator);
		IResult result = new ResultPojo();
		//TODO
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listObjectNodesByRelationAndSubjectRole(java.lang.String, java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listObjectNodesByRelationAndSubjectRole(String relationLocator, String subjectRoleLocator, int start, int count, String sortBy, String sortDir, ITicket credentials) {
		environment.logDebug("TupleQuery.listObjectNodesByRelationAndSubjectRole- "+relationLocator+" "+subjectRoleLocator);
		IResult result = new ResultPojo();
		IPostgresConnection conn = null;
		try {
			conn = provider.getConnection();
			result = listObjectNodesByRelationAndSubjectRole(conn, relationLocator, subjectRoleLocator, start, count, sortBy, sortDir, credentials);
		} catch (SQLException e) {
			result.addErrorString(e.getMessage());
		}
		conn.closeConnection(result);

		return result;
	}

	@Override
	public IResult listObjectNodesByRelationAndSubjectRole(IPostgresConnection conn, String relationLocator,
			String subjectRoleLocator, int start, int count, String sortBy, String sortDir, ITicket credentials) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listObjectNodesBySubjectAndRelation(java.lang.String, java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listObjectNodesBySubjectAndRelation(String subjectLocator, String relationLocator, int start, int count, String sortBy, String sortDir, ITicket credentials) {
		environment.logDebug("TupleQuery.listObjectNodesBySubjectAndRelation- "+subjectLocator+" "+relationLocator);
		IResult result =  new ResultPojo();
		//TODO
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listObjectNodesBySubjectAndRelationAndScope(java.lang.String, java.lang.String, java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listObjectNodesBySubjectAndRelationAndScope(String subjectLocator, String relationLocator, String scopeLocator, int start, int count, String sortBy, String sortDir, ITicket credentials) {
		environment.logDebug("TupleQuery.listObjectNodesBySubjectAndRelationAndScope- "+subjectLocator+" "+relationLocator+" "+scopeLocator);
		IResult result =  new ResultPojo();
		//
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listSubjectNodesByObjectAndRelation(java.lang.String, java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listSubjectNodesByObjectAndRelation(String objectLocator, String relationLocator, int start, int count, String sortBy, String sortDir, ITicket credentials) {
		environment.logDebug("TupleQuery.listSubjectNodesByObjectAndRelation- "+objectLocator+" "+relationLocator);
		IResult result = new ResultPojo();
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listSubjectNodesByObjectAndRelationAndScope(java.lang.String, java.lang.String, java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listSubjectNodesByObjectAndRelationAndScope(String objectLocator, String relationLocator, String scopeLocator, int start, int count, String sortBy, String sortDir, ITicket credentials) {
		environment.logDebug("TupleQuery.listSubjectNodesByObjectAndRelationAndScope- "+objectLocator+" "+relationLocator+" "+scopeLocator);
		IResult result =  new ResultPojo();
		//TODO
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listSubjectNodesByRelationAndObjectRole(java.lang.String, java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listSubjectNodesByRelationAndObjectRole(String relationLocator, String objectRoleLocator, int start, int count, String sortBy, String sortDir, ITicket credentials) {
		environment.logDebug("TupleQuery.listSubjectNodesByRelationAndObjectRole- "+relationLocator+" "+objectRoleLocator);
		IResult result =  new ResultPojo();
		//TODO
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listSubjectNodesByRelationAndSubjectRole(java.lang.String, java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listSubjectNodesByRelationAndSubjectRole(String relationLocator, String subjectRoleLocator, int start, int count, String sortBy, String sortDir, ITicket credentials) {
		environment.logDebug("TupleQuery.listSubjectNodesByRelationAndSubjectRole- "+relationLocator+" "+subjectRoleLocator);
		IResult result =  new ResultPojo();
		//TODO
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listTuplesByObjectLocator(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listTuplesByObjectLocator(String objectLocator, int start, int count,
			String sortBy, String sortDir, ITicket  credentials) {
		IResult result = new ResultPojo();
		IPostgresConnection conn = null;
		try {
			conn = provider.getConnection();
			result = listTuplesByObjectLocator(conn, objectLocator, start, count, sortBy, sortDir, credentials);
		} catch (SQLException e) {
			result.addErrorString(e.getMessage());
		}
		conn.closeConnection(result);
		return result;	
	}

	@Override
	public IResult listTuplesByObjectLocator(IPostgresConnection conn, String objectLocator, int start, int count,
			String sortBy, String sortDir, ITicket credentials) {
		IResult result = new ResultPojo();
		List<Object> l = new ArrayList<Object>();
		result.setResultObject(l);
		String sql = 
		     "SELECT lox, row_to_json(proxy) FROM tq_contents.proxy WHERE lox IN "+
		    		 "(SELECT proxyid AS lox FROM tq_contents.properties WHERE " +
		"( key = '"+ITQCoreOntology.TUPLE_OBJECT_PROPERTY+"' AND property_val = ? ) ORDER BY lox OFFSET ? LIMIT ?";
		
		Object [] vals = new Object[3];
		vals[0] = objectLocator;
		vals[1] = start;
		if (count > 0)
			vals[2]  = count;
		else
			vals[2] = "ALL";
		IResult r = new ResultPojo();
		conn.executeSelect(sql, r, vals);
		ResultSet rs = (ResultSet)r.getResultObject();
		if (r.hasError()) 
			result.addErrorString(r.getErrorString());
		if (rs != null) {
			try {
				JSONObject jo;
				String json;
				while (rs.next()) {
					json = rs.getString(2);
		            jo = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(json);

		            database.finishNodeFetch(conn, jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, r);
		            if (r.hasError()) {
		            	result.addErrorString(r.getErrorString());
		            	r = new ResultPojo();
		            }
		        	database.jsonToProxy(jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, credentials, r);
		        	if (r.getResultObject() != null)
		        		l.add(r.getResultObject());
		            if (r.hasError()) {
		            	result.addErrorString(r.getErrorString());
		            	r = new ResultPojo();
		            }
				}
				conn.closeResultSet(rs, r);
			} catch (Exception e) {
				environment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listTuplesByPredTypeAndObject(java.lang.String, java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listTuplesByPredTypeAndObject(String predType, String obj, int start, int count, String sortBy, String sortDir, ITicket  credentials) {
		environment.logDebug("TupleQuery.listTuplesByPredTypeAndObject- "+predType+" "+obj);
		IResult result =  new ResultPojo();
		//TODO
		return result;	
	}



	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listTuplesBySubject(java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listTuplesBySubject(String subjectLocator, int start, int count,
			String sortBy, String sortDir, ITicket  credentials) {
		IResult result = new ResultPojo();
		IPostgresConnection conn = null;
		try {
			conn = provider.getConnection();
			result = listTuplesBySubject(conn, subjectLocator, start, count, sortBy, sortDir, credentials);
		} catch (SQLException e) {
			result.addErrorString(e.getMessage());
		}
		conn.closeConnection(result);
		return result;	
	}

	@Override
	public IResult listTuplesBySubject(IPostgresConnection conn, String subjectLocator, int start, int count,
			String sortBy, String sortDir, ITicket credentials) {
		IResult result = new ResultPojo();
		List<Object> l = new ArrayList<Object>();
		result.setResultObject(l);
		String sql = 
		     "SELECT lox, row_to_json(proxy) FROM tq_contents.proxy WHERE lox IN "+
		    		 "(SELECT proxyid AS lox FROM tq_contents.properties WHERE " +
		"( key = '"+ITQCoreOntology.TUPLE_SUBJECT_PROPERTY+"' AND property_val = ? ) ORDER BY lox OFFSET ? LIMIT ?";
		
		Object [] vals = new Object[3];
		vals[0] = subjectLocator;
		vals[1] = start;
		if (count > 0)
			vals[2]  = count;
		else
			vals[2] = "ALL";
		IResult r = new ResultPojo();
		conn.executeSelect(sql, r, vals);
		ResultSet rs = (ResultSet)r.getResultObject();
		if (r.hasError()) 
			result.addErrorString(r.getErrorString());
		if (rs != null) {
			try {
				JSONObject jo;
				String json;
				while (rs.next()) {
					json = rs.getString(2);
		            jo = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(json);

		            database.finishNodeFetch(conn, jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, r);
		            if (r.hasError()) {
		            	result.addErrorString(r.getErrorString());
		            	r = new ResultPojo();
		            }
		        	database.jsonToProxy(jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, credentials, r);
		        	if (r.getResultObject() != null)
		        		l.add(r.getResultObject());
		            if (r.hasError()) {
		            	result.addErrorString(r.getErrorString());
		            	r = new ResultPojo();
		            }
				}
				conn.closeResultSet(rs, r);
			} catch (Exception e) {
				environment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}		
		return result;	
	}

	/* (non-Javadoc)
	 * @see org.topicquests.model.api.ITupleQuery#listTuplesBySubjectAndPredType(java.lang.String, java.lang.String, int, int, java.util.Set)
	 */
	@Override
	public IResult listTuplesBySubjectAndPredType(String subjectLocator, String predType, int start, int count, String sortBy, String sortDir, ITicket  credentials) {
		environment.logDebug("TupleQuery.listTuplesBySubjectAndPredType- "+subjectLocator+" "+predType);
		IResult result  = new ResultPojo();
		//TODO
		return result;	
	}

	@Override
	public IResult listTuplesByLabel(String [] labels, int start, int count, String sortBy, String sortDir, ITicket  credentials) {
		IResult result = new ResultPojo();
		//TODO
		return result;
	}

	@Override
	public IResult listTuplesByPredTypeAndObjectOrSubject(String predType,
			String obj, int start, int count, String sortBy, String sortDir, ITicket  credentials) {
		environment.logDebug("TupleQuery.listTuplesByPredTypeAndObjectOrSubject- "+predType+" "+obj);
		IResult result = new ResultPojo();
		IPostgresConnection conn = null;
		try {
			conn = provider.getConnection();
			result = listTuplesByPredTypeAndObjectOrSubject(conn, predType, obj, start, count, sortBy, sortDir, credentials);
		} catch (SQLException e) {
			result.addErrorString(e.getMessage());
		}
		conn.closeConnection(result);
		return result;	
	 }

	@Override
	public IResult listTuplesByPredTypeAndObjectOrSubject(IPostgresConnection conn, String predType, String obj,
			int start, int count, String sortBy, String sortDir, ITicket credentials) {
		IResult result = new ResultPojo();
		List<Object> l = new ArrayList<Object>();
		result.setResultObject(l);
		String sql = 
		     "SELECT lox, row_to_json(proxy) FROM tq_contents.proxy WHERE nodeType = ? AND lox IN "+
		    		 "(SELECT proxyid AS lox FROM tq_contents.properties WHERE " +
		"( key = '"+ITQCoreOntology.TUPLE_SUBJECT_PROPERTY+"' AND property_val = ? ) OR "+
		"( key = '"+ITQCoreOntology.TUPLE_OBJECT_PROPERTY+"' AND property_val = ? ) ORDER BY lox OFFSET ? LIMIT ?";
		
		Object [] vals = new Object[5];
		vals[0] = predType;
		vals[1] = obj;
		vals[2] = obj;
		vals[3] = start;
		if (count > 0)
			vals[4]  = count;
		else
			vals[4] = "ALL";
		IResult r = new ResultPojo();
		conn.executeSelect(sql, r, vals);
		ResultSet rs = (ResultSet)r.getResultObject();
		if (r.hasError()) 
			result.addErrorString(r.getErrorString());
		if (rs != null) {
			try {
				JSONObject jo;
				String json;
				while (rs.next()) {
					json = rs.getString(2);
		            jo = (JSONObject)new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(json);

		            database.finishNodeFetch(conn, jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, r);
		            if (r.hasError()) {
		            	result.addErrorString(r.getErrorString());
		            	r = new ResultPojo();
		            }
		        	database.jsonToProxy(jo.getAsString(ITQCoreOntology.LOCATOR_PROPERTY), jo, credentials, r);
		        	if (r.getResultObject() != null)
		        		l.add(r.getResultObject());
		            if (r.hasError()) {
		            	result.addErrorString(r.getErrorString());
		            	r = new ResultPojo();
		            }
				}
				conn.closeResultSet(rs, r);
			} catch (Exception e) {
				environment.logError(e.getMessage(), e);
				result.addErrorString(e.getMessage());
			}
		}
		return result;
	}



}
