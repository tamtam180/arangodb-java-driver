/*
 * Copyright (C) 2012,2013 tamtam180
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

package at.orz.arangodb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.orz.arangodb.Station;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class TestUtils {
	
	public static List<Station> readStations() throws IOException {

		ArrayList<Station> stations = new ArrayList<Station>(1000);
		BufferedReader br = new BufferedReader(new InputStreamReader(TestUtils.class.getResourceAsStream("/test-data/jp-tokyo-station.tsv"), "utf-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}
			Station station = new Station(line.split("\t", -1));
			stations.add(station);
		}
		br.close();
		
		return stations;
		
	}
	
}
