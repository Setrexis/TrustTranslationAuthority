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

package com.tta.commons.error;

/**
 * @author a101866
 * Call result class is used to store the result of an invocation to a method
 */
public class CallResult {
	private boolean ok=false;
	private String txt="";
	
	public CallResult () {}
	
	
	/**
	 * @param ok result of the call to a method
	 * @param txt description of the result
	 */
	public CallResult (boolean ok, String txt) {
		this.ok = ok;
		this.txt = txt;
	}
	
	/**
	 * @return result true/false
	 */
	public boolean isOK() {
		return ok;
	}
	
	/**
	 * @return description of the result
	 */
	public String getTxt() {
		return txt;
	}
}
