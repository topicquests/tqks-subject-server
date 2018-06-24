/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.api;

public interface IProxyEvent {
  public static final String
  NEW_EVENT		= "new",
    UPDATE_EVENT 	= "upd",
    //USER_ID			= "uid",
    EVENT_TYPE		= "typ",
    CARGO			= "crg",
    VERB			= "vrb";
}
