/**
 * Copyright 2018, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package devtests;

/**
 * @author jackpark
 *
 */
public class MergeRunner {
	private MergeTest_1 merger = null;
	private Worker worker;
	private boolean isRunning = true;
	
	/**
	 * 
	 */
	public MergeRunner() {
		worker = new Worker();
		worker.start();
	}
	void startMerge() {
		merger = new MergeTest_1(this);
	}
	
	public void shutDown() {
		synchronized(merger) {
			isRunning = false;
			merger.notify();
		}
	}
	class Worker extends Thread {
		
		
		public void run() {
			while (isRunning) {
				if (merger == null )
					startMerge();
				else {
					synchronized(merger) {
						try {
							merger.wait(10);
						} catch (Exception e) {}
					}
				}
			}
			System.exit(0);
		}
		
	}

}
