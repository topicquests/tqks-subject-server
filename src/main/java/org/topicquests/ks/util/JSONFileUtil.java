/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.util;

import java.io.*;

import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import java.nio.charset.StandardCharsets;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
/**
 * @author jackpark
 *
 */
public class JSONFileUtil {
  /**
   * Read <code>jsonFile</code> and return a {@link JSONObject}
   * @param jsonFile
   * @return
   */
  public static IResult fileToJSON(File jsonFile) {
    IResult result = new ResultPojo();
    
    try {
      JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
      FileInputStream fis = new FileInputStream(jsonFile);
      BufferedInputStream bis = new BufferedInputStream(fis);
      JSONObject jo = (JSONObject)p.parse(bis);
      result.setResultObject(jo);
    } catch (Exception e) {
      result.addErrorString(e.getMessage());
    }
		
    return result;
  }

	
  /**
   * Write a {@link JSONObject} to a given file specified by <code>filePath</code>
   * @param jo
   * @param filePath
   * @return
   */
  public static IResult jsonToFile(JSONObject jo, String filePath) {
    IResult result = new ResultPojo();
    
    try {
      Writer fos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath)),
                                                             StandardCharsets.UTF_8));
      PrintWriter out = new PrintWriter(fos);
      out.print(jo.toJSONString());
      out.flush();
      out.close();
    } catch (Exception e) {
      result.addErrorString(e.getMessage());
    }
    return result;

  }
}
