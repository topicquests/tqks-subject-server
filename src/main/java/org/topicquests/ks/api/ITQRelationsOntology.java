/**
 * Copyright 2015...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.api;

/**
 * 
 * @author park
 * MOST of the relations in the data/bootstrap/2-relations.json file
 */
public interface ITQRelationsOntology {
  public static final String
    CAUSAL			= "CausesRelationType",
    EXPLAINS_WHAT	= "ExplainsWhatRelationType",
    EXPLAINS_WHY	= "ExplainsWhyRelationType",
    EXPLAINS_HOW	= "ExplainsHowRelationType",
    IS_SIMILAR		= "IsSimilarToRelationType",
    SHARES_ISSUES_WITH	= "SharesIssuesWithRelationType",
    IS_ANALOGOUS		= "IsAnalogousRelationType",
    IS_NOT_ANALOGOUS	= "IsNotAnalogousRelationType",
    IS_METAPHOR			= "IsMetaphorRelationType",
    AGREES_WITH			= "AgreesWithRelationType",
    DISAGREES_WITH		= "DisagreesWithRelationType",
    IS_DIFFERENT		= "IsDifferentToRelationType",
    IS_OPPOSITE_OF		= "IsOppositeOfRelationType",
    IS_SAME_AS			= "IsSameAsRelationType",
    HAS_NOTHING_TO_DO_WITH	= "HasNothingToDoWithRelationType",
    USES					= "UsesRelationType",
    IMPLIES					= "ImpliesRelationType",
    ENABLES					= "EnablesRelationType",
    IMPROVES_ON				= "ImprovesOnRelationType",
    ADDRESSES				= "AddressesRelationType",
    SOLVES					= "SolvesRelationType",
    IS_PREREQUISITE_FOR		= "IsPrerequisiteForRelationType",
    IMPAIRS					= "ImpairsRelationType",
    PREVENTS				= "PreventsRelationType",
    PROVES					= "ProvesRelationType",
    REFUTES					= "RefutesRelationType",
    IS_EVIDENCE_FOR			= "IsEvidenceForRelationType",
    IS_EVIDENCE_AGAINST		= "IsEvidenceAgainstRelationType",
    IS_CONSISTENT_WITH		= "IsConsistentWithRelationType",
    IS_INCONSISTENT_WITH	= "IsInconsistentWithRelationType",
    IS_EXAMPLE_OF			= "IsExampleRelationType",
    PREDICTS				= "PredictsRelationType",
    ENVISAGES				= "EnvisagesRelationType",
    UNLIKELY_TO_AFFECT		= "UnlikelyToAffectRelationType",
    RESPONDS_TO				= "RespondsToRelationType",
    IS_RELATED_TO			= "IsRelatedToRelationType",
    IS_PART_OF				= "IsPartOfRelationType",
    IS_CONTAINED_IN			= "IsContainedInRelationType",
    HAS_ROLE				= "HasRoleRelationType";
}
