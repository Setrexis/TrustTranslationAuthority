/**
 *
 *LIGHTest Trust Translation Authority
 *Copyright © 2018 Atos Spain SA
 *
 *This program is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.tta.commons.connectors.dns;

/**
 * 
 * Container for answers from DNS Server
 * 
 * class properties:
 *        result_: http code of the answer
 *        txt_: text of the response to be sent in the answer to tta client
 *
 */

public class DnsResponse {

	private boolean result_ = false;
	private String txt_ = null;
	
	/**
	 * @param resutl
	 * @param txt
	 */
	public DnsResponse (boolean resutl, String txt) {
		this.result_ = resutl;
		this.txt_ = txt;
	}

	/**
	 * @return True if the answer of DNS is 200OK, False any other case
	 */
	public boolean isResult() {
		return result_;
	}

	/**
	 * @return if result is true this method returns null or empty String, if result is false this field contains the description of the error.
	 */
	public String getTxt() {
		return txt_;
	}
	
}
