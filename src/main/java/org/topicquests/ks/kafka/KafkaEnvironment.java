/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.kafka;

import org.topicquests.ks.SystemEnvironment;
import org.topicquests.support.RootEnvironment;

/**
 * @author jackpark
 *
 */
public class KafkaEnvironment extends RootEnvironment {
	private SystemEnvironment topicMapEnvironment;
	private KafkaProducer producer;
	/**
	 * @param configPath
	 * @param logConfigPath
	 */
	public KafkaEnvironment(SystemEnvironment env) {
		super("kafka-topics.xml", "logger.properties");
		topicMapEnvironment  = env;
		String clientId = "tmProducer"+Long.toString(System.currentTimeMillis());
		producer = new KafkaProducer(this, clientId);
		
	}
	
	public KafkaProducer getProducer() {
		return producer;
	}

	public void shutDown() {
		producer.close();
	}
}
