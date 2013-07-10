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

package at.orz.arangodb.util;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class IOUtils {

	public static String toString(InputStream input) throws IOException {
		return toString(input, "utf-8");
	}
	
	public static String toString(InputStream input, String encode) throws IOException {
		
		InputStreamReader in;
		try {
			
			StringBuilder buffer = new StringBuilder(8012);
			
			in = new InputStreamReader(new BufferedInputStream(input), encode);
			char[] cbuf = new char[8012];
			int len;
			while ((len = in.read(cbuf)) != -1) {
				buffer.append(cbuf, 0, len);
			}
			
			return buffer.toString();
			
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} finally {
			close(input);
		}
	}
	
	public static void close(Closeable input) {
		try {
			input.close();
		} catch (IOException e) {
		}
	}
	
}
