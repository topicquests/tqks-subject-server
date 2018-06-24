/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm.api;

import java.util.List;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 * Supports index documents which look like this:
 * <pre>
 * {
 *	 "lox": "locator",
 *	 "labels": [
 *   	{ "language": "en", "labels": [ "label 1", "label 2" ] },
 * 	    { "language": "fr", "labels": [ "étiquette 1", "étiquette 2" ] }
 *	 ],
 *	 "details": [
 *	    { "language": "en", "details": [ "now is the time", "another time" ] },
 *	    { "language": "fr", "details": [ "ce moment", "le moment suivant" ] }
 *	 ]
 * }
 * </pre>
 * Nope.
 * For now, Simple
 * { lox: locator, label: [...], language: en; details: [...] }
 */
public interface IESDocument {
  void setlanguage(String lang);
  String getLanguage();
	
  void setProxyLocatorField(String locator);
  String getProxyLocatorField();
	
  void setLabelField(String label);
  List<String> getLabelField();
	
  void setDetailsField(String details);
  List<String> getDetailsField();
		
  JSONObject getData();
}
