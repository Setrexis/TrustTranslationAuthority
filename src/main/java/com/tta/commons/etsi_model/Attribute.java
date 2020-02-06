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


import org.apache.log4j.Logger;
import org.json.JSONObject;



/**
 * Attribute class of model for testing feeding api of dao module
 */
public class Attribute {

  private static Logger logger = Logger.getLogger("Attribute");

  String name;
  String value;

  
  /**
   * Constructor of attribute
   * @param name name of the attribute
   * @param value value of the attribute
   */
  private Attribute(String name, String value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Constructor of attribute
   * @param jo description of the attribute in JSON format
   */
  public static Attribute createAttribute(JSONObject jo) {
    try {
      String name = jo.getString("name");
      String value = jo.getString("value");
      
      return new Attribute(name,value);

    } catch (Exception e) {
      logger.error(e, e);
      return null;
      
    }
  }

  /**
   * Compare this attribute with the one passed as parameter
   * @param att
   * @return true if name and values are the same, false any other case
   */
  public boolean compare(Attribute att) {
    return (att.name.compareTo(this.name) + att.value.compareTo(this.value) == 0);
    
  }

  /**
   * @return attribute name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return attribute value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * @return the attribute in JSON format
   */
  public JSONObject toJSON() {
    JSONObject jo = new JSONObject();
    jo.put("name", name);
    jo.put("value", value);

    return jo;

  }
}
