/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.tm;

import java.util.ArrayList;
import java.util.List;

import org.topicquests.ks.tm.api.IESDocument;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class ElasticSearchDocument implements IESDocument {
	private JSONObject data;
	//fields are defined in tqks-elastic6-provider config/mappings.json
	private static final String
		LOCATOR		= "lox",
		LABEL		= "label",
		LANGUAGE	= "language",
		DETAILS		= "details";
		
	/**
	 * @param jo
	 */
	public ElasticSearchDocument(JSONObject jo) {
		data = jo;
	}
	
	public ElasticSearchDocument() {
		data = new JSONObject();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.tm.api.IESDocument#setLabelField(java.lang.String)
	 */
	@Override
	public void setLabelField(String label) {
		List<String> labels = new ArrayList<String>();
		labels.add(label);
		data.put(LABEL, labels);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.tm.api.IESDocument#getLabelField()
	 */
	@Override
	public List<String> getLabelField() {
		return (List<String>)data.get(LABEL);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.tm.api.IESDocument#setDetailsField(java.lang.String)
	 */
	@Override
	public void setDetailsField(String details) {
		List<String> dets = new ArrayList<String>();
		dets.add(details);
		data.put(DETAILS, dets);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.tm.api.IESDocument#getDetailsField()
	 */
	@Override
	public List<String> getDetailsField() {
		return (List<String>)data.get(DETAILS);
	}

	/* (non-Javadoc)
	 * @see org.topicquests.ks.tm.api.IESDocument#getData()
	 */
	@Override
	public JSONObject getData() {
		return data;
	}

	@Override
	public void setProxyLocatorField(String locator) {
		data.put(LOCATOR, locator);
	}

	@Override
	public String getProxyLocatorField() {
		return data.getAsString(LOCATOR);
	}

	@Override
	public void setlanguage(String lang) {
		data.put(LANGUAGE, lang);
	}

	@Override
	public String getLanguage() {
		return data.getAsString(LANGUAGE);
	}

}
