/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg;

import java.io.File;
import java.util.*;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.TicketPojo;
import org.topicquests.ks.api.IExtendedCoreOntology;
import org.topicquests.ks.api.ITQCoreOntology;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.cg.api.ICGModel;
import org.topicquests.ks.cg.api.ICGRelation;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.ks.tm.api.IProxy;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.support.util.TextFileHandler;

/**
 * @author jackpark
 *
 */
public class CGImporter {
	  private SystemEnvironment environment;
	  private DataProvider dataProvider;
	  private IProxyModel proxyModel;
	  private ICGModel model;
	  private ITicket credentials;
	  private final String path = "data/bootstrap/";
	  private TextFileHandler handler;
	  private String provenanceLocator = IExtendedCoreOntology.BOOTSTRAP_PROVENANCE_TYPE; //default
	  private final String 
	  		USER_ID = ITQCoreOntology.SYSTEM_USER,
	  		LANGUAGE = "en",
			BOOTSTRAP_PROVENANCE = "BootstrapProvenance";

	/**
	 * 
	 */
	public CGImporter(SystemEnvironment env) {
		environment = env;
	    this.dataProvider = environment.getDataProvider();
	    proxyModel = environment.getProxyModel();
	    model = environment.getCGModel();
	    this.credentials = new TicketPojo();
	    credentials.setUserLocator(USER_ID);
	    handler = new TextFileHandler();
	}

	public IResult bootstrap() {
		IResult result = new ResultPojo();
		//see if relations are booted
		String lox = "CG_ACCM_RELATION";
	    IResult r = dataProvider.getNode(lox, credentials);
	    List<String> fnames = new ArrayList<String>();
	    if (r.hasError())
	      result.addErrorString(r.getErrorString());
	    environment.logDebug("CGImporter-1 "+r.getErrorString()+" | "+r.getResultObject());
	    if (r.getResultObject() == null) {
	        File dir = new File(path);
	        System.out.println("JSONBOOTSTRAP.bootstrap "+dir.getAbsolutePath());
	        File files [] = dir.listFiles();
	        int len = files.length;
	        File f;
	        
	        r = null;
	        for (int i = 0; i < len; i++) {
	          f = files[i];
	          System.out.println(f.getAbsolutePath());
	          if (!fnames.contains(f.getName())) {
		          if (f.getName().endsWith(".cgr")) {
		            r = importRelationFile(f);
		            if (r.hasError())
		              result.addErrorString(r.getErrorString());
		          } else if (f.getName().endsWith(".cg")) {
		        	//  r = importCGFile(f);
		      	    //        if (r.hasError())
		      	    //          result.addErrorString(r.getErrorString());
		          } else if (f.getName().endsWith(".cgo")) {
		        	  //TODO
		          }
	          }
	        }
	      } else {
	        IProxy p = (IProxy)r.getResultObject();
	        System.out.println("FOO " + p.toJSONString());
	      }		
		return result;
	}
	
	//////////////////////////
	// A lattice type comes in as
	// ROBOT, MACHINE, MOBILE_ENTITY
	//  where the first term is the class
	//  following terms are the isA's of that class
	// 1- see if it already exists
	// if so
	//	2- add to it
	// else
	//	create new
	//////////////////////////
	/**
	 * Process lattice types
	 * @param f
	 * @return
	 */
	IResult processLatticeTypes(File f) {
		IResult result = new ResultPojo();
		
		return result;
	}
	
	/**
	 * Process a CGRelation
	 * @param f
	 * @return
	 */
	IResult importRelationFile(File f) {
		IResult result = new ResultPojo();
		String line = handler.readFirstLine(f);
		while (line != null) {
			line = line.trim();
			if (line.startsWith("$BOOTSTRAP")) {
				line = line.substring(0, ("$BOOTSTRAP".length()));
				provenanceLocator = line.trim();
			}
			else if (!line.equals("") && !line.startsWith("#"))
				processRelation(line.trim(), result);
			line = handler.readNextLine();
		}
		
		return result;		
	}
	
	String [] splitOnCommas(String line) {
		String [] result = new String[3];
		StringTokenizer tok = new StringTokenizer(line, ",");
		int i = 0;
		while (tok.hasMoreTokens() && i < 3) {
			result[i++] = tok.nextToken();
		}
		return result;
	}
	/**
	 * Each line takes the form RELN, label, details
	 * @param line
	 * @param r
	 */
	void processRelation(String line, IResult r) {
		environment.logDebug("CGIImporter.processRelation- "+line);
		String [] words = splitOnCommas(line);
		String locator = model.relationToLocator(words[0]);
		String label = words[1].trim();
		String details = words[2].trim();
		ICGRelation p = model.newRelation(locator, label, details, LANGUAGE,
				USER_ID, BOOTSTRAP_PROVENANCE);
		environment.logDebug("CGImporter.processRelation "+line+" "+p.toJSONString());
		IResult x = dataProvider.putNode((IProxy)p);
		//WE are building parent classes; that should be enough for now.
		//These will be used as parents for when we are making Conceptual graphs
		
	}

	/**
	 * Process CG concepts
	 * @param f
	 * @return
	 */
	IResult importCGFile(File f) {
		IResult result = new ResultPojo();
		String line = handler.readFirstLine(f);
		while (line != null) {
			if (line.startsWith("$BOOTSTRAP")) {
				line = line.substring(0, ("$BOOTSTRAP".length()));
				provenanceLocator = line.trim();
			}
			else if (!line.equals("") && !line.startsWith("#"))
				processLine(line.trim(), result);
			line = handler.readNextLine();
		}
		
		return result;
	}
	
	void processLine(String line, IResult r) {
		
	}
}
