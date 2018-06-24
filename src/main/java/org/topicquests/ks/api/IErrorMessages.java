/**
 * Copyright 2015...2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.api;

/**
 * @author park
 *
 */
public interface IErrorMessages {
  public static final String
    INSUFFICIENT_CREDENTIALS   = "InsufficientCredentials",
    OPTIMISTIC_LOCK_EXCEPTION  = "OptimisticLockException",
    NODE_REMOVED               = "NodeRemoved",
    NODE_MISSING               = "NodeMissing",
    MISSING_VERSION_PROPERTY   = "MissingVersionProperty",
    //ITQDataProvider.updateProxyFromJSON
    BAD_JSON_UPDATE_NODE       = "BadJSONUpdateNode";

}
