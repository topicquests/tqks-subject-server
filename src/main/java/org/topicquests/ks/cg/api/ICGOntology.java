/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg.api;

/**
 * @author jackpark
 *
 */
public interface ICGOntology {
	/** Node Types */
	public static final String
		CG_CONCEPT_NODE		= "CGConceptNodeType",
		//never should be used  WHAT? we're going to build a lattice
		CG_RELATION_NODE	= "CGRelationNodeType",
		CG_GRAPH_NODE		= "CGGraphNodeType",
		CG_LATTICE_TYPE_NODE = "CGLatticeType",
		CG_LATTICE_RELATION_NODE = "CGLatticeRelation";
	
	public static final String
		SOWA_PROVENANCE		= "SowaBootstrapProvenanceType";
	
	/** Relation Type */
	public static final String
		CG_RELATION_TYPE	= "CGRelationType",
		GRAPH_TOPIC_RELATION	= "CG_Topic_Relation";
	
	/** property types */
	public static final String
		// for locators
		CG_RELATION_LIST		= "cgRList",
		CG_CONCEPT_LIST			= "cgCList",
		CG_GRAPH_LIST			= "cgGList",
		// for JSON objects after populating
		RELN_LIST				= "relList",
		CON_LIST				= "conList",
		TOPIC_REFERENCE_LOCATOR = "topicRefLox",
		//SOME REASON TO BELIEVE LATTICE_TYPE is same as TOPIC_REFERENCE_LOCATOR
		LATTICE_TYPE			= "latticeType",
		ENCLOSING_GRAPH_LOCATOR	= "enclosGraphLox",
		OUT_EDGE_LOCATOR_LIST	= "outEList",
		IN_EDGE_LOCATOR_LIST	= "inEList",
		RELATION_SOURCE_LOCATOR	= "relSrcLox",
		RELATION_TARGET_LOCATOR	= "RelTrgLox",
		COREFERENCE_MAP			= "CorefMap",
		STASH_COREF_MAP			= "StashCoref";
}
