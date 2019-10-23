/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks;

import org.topicquests.support.RootEnvironment;
import org.topicquests.support.api.IResult;
import org.topicquests.backside.kafka.producer.MessageProducer;
import org.topicquests.ks.cg.CGImporter;
import org.topicquests.ks.cg.CGModel;
import org.topicquests.ks.cg.api.ICGModel;
import org.topicquests.ks.kafka.KafkaProducer;
import org.topicquests.ks.tm.DataProvider;
import org.topicquests.ks.tm.JSONBootstrap;
import org.topicquests.ks.tm.ProxyModel;
import org.topicquests.ks.tm.TupleQuery;
import org.topicquests.ks.tm.api.IProxyModel;
import org.topicquests.ks.tm.api.ITupleQuery;
import org.topicquests.ks.tm.api.IVirtualizer;
import org.topicquests.ks.tm.merge.DefaultVirtualizer;
import org.topicquests.ks.tm.merge.VirtualizerHandler;
import org.topicquests.os.asr.StatisticsHttpClient;
import org.topicquests.os.asr.api.IStatisticsClient;
import org.topicquests.es.ProviderEnvironment;
import org.topicquests.support.api.IEnvironment;

public class SystemEnvironment extends RootEnvironment  {
  private static SystemEnvironment instance;
  private DataProvider dataProvider = null;
  private VirtualizerHandler virtualizerHandler = null;

  private ProviderEnvironment esProvider;  // Elasticsearch provider
  private IProxyModel proxyModel;
  // private VirtualizerHandler virtualizerHandler = null;
  private IStatisticsClient stats;
  private JSONBootstrap jsonBootstrapper;
  // private IExtendedConsoleDisplay console;
  // private SearchEnvironment searchEnvironment;
  // private ElasticQueryDSL queryDSL;
  //private ProxyEventHandler eventHandler;
  // private IMergeThread mergeThread;
  protected ITupleQuery tupleQuery;
  private IVirtualizer virtualizer;
  private ICGModel cgModel;
  private KafkaProducer kProducer;
  
  public SystemEnvironment() {
    this("ttq_contents");
  }
  
  public SystemEnvironment(String schemaName) {
    super("topicmap-props.xml", "logger.properties");
    
    try {
      esProvider = new ProviderEnvironment();
      String hs = getStringProperty("HaveRemoteStatistics");
      boolean hasRemoteStats = hs.equalsIgnoreCase("T");
      if (hasRemoteStats)
  		 stats = new StatisticsHttpClient(this);
      else
    	  stats = StatisticsUtility.getInstance();
	  String clientId = "tmProducer"+Long.toString(System.currentTimeMillis());

      kProducer = new KafkaProducer(this, clientId);
      //eventHandler = new ProxyEventHandler(this);
      // queryDSL = new ElasticQueryDSL(this);
      dataProvider = new DataProvider(this, schemaName);
      tupleQuery = new TupleQuery(this, dataProvider);
      proxyModel = new ProxyModel(this, dataProvider);
      virtualizer = new DefaultVirtualizer();
      virtualizer.init(this);
      virtualizerHandler = new VirtualizerHandler(this);
      dataProvider.setVirtualizer(virtualizerHandler);
      cgModel = new CGModel(this);
      // mergeThread = new MergeThread(this);
    } catch (Exception e) {
      logError(e.getMessage(), e);
      e.printStackTrace();
      shutDown();
      System.exit(1);
    }
    
    instance = this;
    init();
    logDebug("Environment Started");
  }
  
  public static SystemEnvironment getInstance() {
    return instance;
  }
	
  public ICGModel getCGModel() {
    return cgModel;
  }
	
  public IVirtualizer getVirtualizer() {
    return virtualizer;
  }
  
  public VirtualizerHandler getVirtualizerHandler() {
    return virtualizerHandler;
  }
	
  public ITupleQuery getTupleQuery() {
    return tupleQuery;
  }
  
  /**
   * @return Kafka producer
   */
  public KafkaProducer getkafkaProducer() {
    return kProducer;
  }
	
 // public ProxyEventHandler getProxyEventHandler() {
 //   return eventHandler;
 // }
	
  public DataProvider getDataProvider() {
    return dataProvider;
  }
  
  public ProviderEnvironment getESProvider() {
    return esProvider;
  }
	
  public IProxyModel getProxyModel() {
    return proxyModel;
  }
	
  // public VirtualizerHandler getVirtualizerHandler() {
  //   return virtualizerHandler;
  // }
	
  public IStatisticsClient getStats() {
    return stats;
  }
		
  /**
   * Available to extensions if needed by way of additional JSON files
   */
  public void bootstrap() {
    JSONBootstrap bs = new JSONBootstrap(this);
    IResult r = bs.bootstrap();
    CGImporter cgi = new CGImporter(this);
    r = cgi.bootstrap();
  }
  
  @Override
  public void shutDown() {
    virtualizerHandler.shutDown();
    try {
      //eventHandler.shutDown();
      kProducer.close();
      esProvider.shutDown();
      
      if (dataProvider != null)
        dataProvider.shutDown();
    } catch (Exception e) {
      logError(e.getMessage(),e);
    }
  }
	
  private void init() {
    String bs = getStringProperty("ShouldBootstrap");
    boolean shouldBootstrap = false; // default value
    
    if (bs != null)
      shouldBootstrap = bs.equalsIgnoreCase("Yes");
    if (shouldBootstrap)
      bootstrap();
  }
}
