/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.cg.api;

import java.util.List;

/**
 * @author jackpark
 * <p>A class for storing the referent of a concept.
 * A referent consists of a quantifier, a designator, and a descriptor (nested graph).
 * A null quantifier indicates that the enclosing concept is existentially quantified.
 * A null designator indicates that the enclosing concept is unspecified.
 * A null descriptor indicates that the enclosing concept is undescribed.
 * A combination of a null quantifier, null designator, and null descriptor indicates
 * that the enclosing concept is a generic concept.  A concept that has no
 * referent at all (null) is also considered to be a generic concept.</p>
 */
public interface IReferent {
	
	void setEnclosingConcept(ICGConcept con);
	
	ICGConcept getEnclosingConcept();
	
	List<ICoreferenceSet> listCoreferenceSets();

}
