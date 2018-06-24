/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.api;

/**
 * @author jackpark
 *	defined in data/bootstrap/core.json
 */
public interface IExtendedCoreOntology extends ITQCoreOntology {
	
  /** relations */
  public static final String
    TAXONOMIC_RELATION_TYPE		= "TaxonomicRelationType",
    SUBCLASS_RELATION_TYPE		= "SubclassRelationType",
    INSTANCE_RELATION_TYPE		= "InstanceRelationType";
	
  /** Roles */
  public static final String
    INSTANCE_ROLE_TYPE			= "InstanceRoleType",
    TYPE_ROLE_TYPE			= "TypeRoleType",
    SUBCLASS_ROLE_TYPE			= "SubclassRoleType",
    SUPERCLASS_ROLE_TYPE		= "SuperclassRoleType";
	
  public static final String
    PROVENANCE_TYPE			= "ProvenanceType",
    BOOTSTRAP_PROVENANCE_TYPE		= "BootstrapProvenanceType",
    MERGE_PROVENANCE_TYPE		= "MergeProvenanceType";
}
