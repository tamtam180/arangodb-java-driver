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
 *
 */
public class AdminStatusEntity extends BaseEntity {
	
	long minorPageFaults;
	long majorPageFaults;
	double userTime;
	double systemTime;
	int numberThreads;
	long residentSize;
	long virtualSize;
	
	public long getMinorPageFaults() {
		return minorPageFaults;
	}
	public long getMajorPageFaults() {
		return majorPageFaults;
	}
	public double getUserTime() {
		return userTime;
	}
	public double getSystemTime() {
		return systemTime;
	}
	public int getNumberThreads() {
		return numberThreads;
	}
	public long getResidentSize() {
		return residentSize;
	}
	public long getVirtualSize() {
		return virtualSize;
	}
	public void setMinorPageFaults(long minorPageFaults) {
		this.minorPageFaults = minorPageFaults;
	}
	public void setMajorPageFaults(long majorPageFaults) {
		this.majorPageFaults = majorPageFaults;
	}
	public void setUserTime(double userTime) {
		this.userTime = userTime;
	}
	public void setSystemTime(double systemTime) {
		this.systemTime = systemTime;
	}
	public void setNumberThreads(int numberThreads) {
		this.numberThreads = numberThreads;
	}
	public void setResidentSize(long residentSize) {
		this.residentSize = residentSize;
	}
	public void setVirtualSize(long virtualSize) {
		this.virtualSize = virtualSize;
	}
	
}
