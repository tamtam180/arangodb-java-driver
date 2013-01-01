/*
 * Copyright (C) 2012 tamtam180
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.orz.arangodb.entity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * @see http://www.arangodb.org/manuals/current/HttpSystem.html#HttpSystemConnectionStatistics
 *
 */
public class ConnectionStatisticsEntity extends BaseEntity {

	public static class StatisticsEntity {
		int start;
		int httpConnectionsCount;
		double httpConnectionsPerSecond;
		int httpDurationCount;
		double httpDurationMean;
		double httpDurationMin;
		int[] httpDurationDistribution;
	}

	int resolution;
	int length;
	int totalLength;
	double[] httpDurationCuts;
	StatisticsEntity[] statistics;
	
	public int getResolution() {
		return resolution;
	}
	public int getLength() {
		return length;
	}
	public int getTotalLength() {
		return totalLength;
	}
	public double[] getHttpDurationCuts() {
		return httpDurationCuts;
	}
	public StatisticsEntity[] getStatistics() {
		return statistics;
	}
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public void setTotalLength(int totalLength) {
		this.totalLength = totalLength;
	}
	public void setHttpDurationCuts(double[] httpDurationCuts) {
		this.httpDurationCuts = httpDurationCuts;
	}
	public void setStatistics(StatisticsEntity[] statistics) {
		this.statistics = statistics;
	}
	
}
