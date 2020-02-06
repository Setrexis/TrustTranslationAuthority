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

package com.tta.commons.model;

import java.util.HashMap;


/**
 * Trust Relations class of model for TTA api of dao module.
 * 
 * @param trustTo_ subjects of trust it trust
 * @param trustedBy_ subject of trust that trust in me
 */
public class TrustRelations {
  private HashMap<String,String> agrIamTarget;
  private HashMap<String,String> agrIamOrigin;

  /**
   * Constructor
   */
  public TrustRelations (){
	agrIamTarget = new HashMap<>();
	agrIamOrigin = new HashMap<>();
  }

  /**
   * @param name of an agreement
   * @return true if I am origing
   */
  public boolean amIOrigin (String name){
    return agrIamOrigin.containsKey(name);
  }
  
  /**
   * @param name of an agreement
   * @return true if I am target
   */
  public boolean amITarget (String name){
    return agrIamTarget.containsKey(name);
  }
  
  /**
   * @param name  
   */
  public void addmeAsOrigin(String name){
	  agrIamOrigin.put(name, name);
  }
  
  /**
   * @param name
   */
  public void removemeAsOrigin(String name){
	  agrIamOrigin.remove(name);
  }
  
  /**
   * @param name
   */
  public void addmeAsTarget (String name){
	  agrIamTarget.put(name, name);
  }
  
  /**
   * @param name
   */
  public void removemeAsTarget (String name){
	  agrIamTarget.remove(name);
  }
  
  /**
   * @return string array with list of names of trusting schemes
   */
  public String[] getListwhereIamTarget(){
	  return  agrIamTarget.keySet().toArray(new String[0]);
  }
  
  /**
   * @return string array with list of names of trusted schemes
   */
  public String[] getListwhereIamOrigin() {
	  return  agrIamOrigin.keySet().toArray(new String[0]);
  }


}
