/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.merge;

import java.io.File;
import java.util.*;
import org.topicquests.ks.SystemEnvironment;
import org.topicquests.ks.util.JSONFileUtil;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class SameLabelFileReader {
  private SystemEnvironment environment;

  /**
   * 
   */
  public SameLabelFileReader(SystemEnvironment env) {
    environment = env;
  }
	
  public IResult processFile(File jsonFile) {
    IResult result = new ResultPojo();
    IResult r = JSONFileUtil.fileToJSON(jsonFile);
    
    if (r.hasError())
      result.addErrorString(r.getErrorString());
    
    JSONObject jo = (JSONObject)r.getResultObject();
    if (jo != null) {
      JSONObject hits = scanForHits(jo);
      System.out.println("HITS " + hits.size());
      
      if (hits.size() > 1) {
        r = JSONFileUtil.jsonToFile(jo, "HitList.json");
        if (r.hasError())
          result.addErrorString(r.getErrorString());
      }
			
    }
    return result;
  }
	
  private JSONObject scanForHits(JSONObject jo) {
    JSONObject result = new JSONObject();
    String label;
    List<String>locators;
    Iterator<String>itr = jo.keySet().iterator();
    
    while (itr.hasNext()) {
      label = itr.next();
      locators = (List<String>)jo.get(label);
      if (locators != null && locators.size() > 1)
        result.put(label, locators);
    }
    return result;
  }

}
