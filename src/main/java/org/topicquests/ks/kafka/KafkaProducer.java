/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.kafka;

import org.topicquests.backside.kafka.producer.MessageProducer;
import org.topicquests.support.api.IEnvironment;

/**
 * @author Admin
 *
 */
public class KafkaProducer extends MessageProducer {
	private final String MY_TOPIC;

  /**
   * @param env
   */
  public KafkaProducer(IEnvironment env, String clientId) {
    super(env, clientId);
    System.out.println("KafkaProducer "+env+" | "+environment);
	MY_TOPIC = env.getStringProperty("TopicMapProducerTopic");
  }

  /**
   * 
   * @param message
   * 
   */
  public void sendMessage(String message) {
	  sendMessage(MY_TOPIC, message, MY_TOPIC, new Integer(0));
  }
  
  /**
   * This will be a JSON string of the form {verb:<verb>,cargo:<cargo>)
   * @param msg
   * @param partition
   */
  public void sendMessage(String topic, String message, String key, Integer partition) {
    environment.logDebug("TQElasticKSProducer "+message);
    super.sendMessage(topic, message, key, partition);
  }

  /* (non-Javadoc)
   * @see org.topicquests.backside.kafka.apps.AbstractKafkaApp#close()
   */
  @Override
  public void close() {
    super.close();
  }

}
