/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm;

// import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.api.ITicket;
import org.topicquests.ks.tm.api.IProxy;

public class CredentialUtility {
  private SystemEnvironment environment;

  public CredentialUtility(SystemEnvironment env) {
    environment = env;
  }

  /**
   * <p>Return <code>1</code> if sufficient <code>credentials</code>
   * to allow viewing this <code>node</code></p>
   * <p>Return <code>0</code> if not sufficient <code>credentials</code></p>
   * <p>Return <code>-1</code> if node has been removed: (isAlive = false)</p>
   * <p>Return <code>-1</code> if <code>credentials</code>==<code>null</code></p>
   * @param node
   * @param credentials
   * @return
   */
  public int checkCredentials(IProxy node, ITicket credentials) {
    int what = 1; //default
    if (!node.getIsLive() || credentials == null)
      return -1;
    if (node.getIsPrivate()) {
      //node created by this dude?
      String cid = node.getCreatorId();
      if (!cid.equals(credentials.getUserLocator())) {
        //Same avatar?
        boolean found = false;
        List<String>l = credentials.listAvatars();
        if (l != null) {
          if (l.contains(cid))
            found = true;
        }
        if (!found) {
          //check goup
          Set<String> groupLocators = Sets.newHashSet(credentials.listRoles());
          Set<String> aclValues = Sets.newHashSet(node.listACLValues());
          SetView<String> intersection = Sets.intersection(groupLocators, aclValues);
          found = !intersection.isEmpty();
        }
        if (!found)
          return 0;
      }
    }
    return what; 
  }

}
