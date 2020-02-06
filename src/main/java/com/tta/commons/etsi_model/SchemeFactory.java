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

package com.tta.commons.etsi_model;


import java.io.File;

import com.tta.commons.etsi_model.mapping.ETSIextractor;



/**
* SchemeFactory class to parse TSL accordign to the model for testing feeding api of dao module
*/
public class SchemeFactory {

private SchemeFactory (){
  
}

/**
 * @param f File containing the TSL
 * @param type Type or specification of the TSL
 * @return TSL object
 */
public static TrustServiceList getScheme(File f, String type){
  TrustServiceList sch = null;
  if (type !=null) {
	  sch = ETSIextractor.extract(f);
  }
  return sch;
}
}
