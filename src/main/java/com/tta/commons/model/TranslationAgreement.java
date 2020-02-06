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


import java.util.Date;

import com.tta.commons.cte.Status;


/**
 * Agreement class of model for TTA api of dao module
 * 
 * @param name_ name of the Agreement
 * @param source_ origin of trust in the agreement
 * @param target_ target of trust in the agreement
 * @param creationDate_ date of creation of the object
 * @param leavingDate_ date of expiration of the agreement
 * @param activationDate_ date when the agreement becomes active
 * @param duration_ duration in days of the validity period of the agreement
 * @param status_ current status of the agreement
 */
public class TranslationAgreement {

  private String name;
  private TrustScheme source;
  private TrustScheme target;
  private Date creationDate;
  private Date leavingDate;
  private Date activationDate;
  private int duration; //in days, trim by approximation
  private Status status;
  private String tplFileFullName;
  private String xmlFileFullName;
  private String dateNote = null;
  
  /**
   * Constructor
   * @param name
   * @param creationDate
   * @param leavingDate
   * @param activationDate
   * @param duration
   * @param status
   */
  public TranslationAgreement(String name, Date creationDate, Date leavingDate,
    Date activationDate, int duration, Status status, String dateNote) {
    super();
    this.name = name;
    this.creationDate = creationDate;
    this.leavingDate = leavingDate;
    this.activationDate = activationDate;
    this.duration = duration;
    this.status = status;
    this.dateNote = dateNote;
  }

  /**
   * @return name 
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name_
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return source or origin of trust
   */
  public TrustScheme getSource() {
    return this.source;
  }

  /**
   * @param source_
   */
  public void setSource(TrustScheme source) {
    this.source = source;
  }

  /**
   * @return target of trust
   */
  public TrustScheme getTarget() {
    return this.target;
  }

  /**
   * @param target_
   */
  public void setTarget(TrustScheme target) {
    this.target = target;
  }

  /**
   * @return creation date
   */
  public Date getCreationDate() {
    return this.creationDate;
  }

  /**
   * @param creationDate_
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * @return leaving date
   */
  public Date getLeavingDate() {
    return this.leavingDate;
  }

  /**
   * @param leavingDate_
   */
  public void setLeavingDate(Date leavingDate) {
    this.leavingDate = leavingDate;
  }

  /**
   * @return activation date
   */
  public Date getActivationDate() {
    return this.activationDate;
  }

  /**
   * @param activationDate_
   */
  public void setActivationDate(Date activationDate) {
    this.activationDate = activationDate;
  }

  /**
   * @return duration in days
   */
  public int getDuration() {
    return this.duration;
  }

  /**
   * @param duration_
   */
  public void setDuration(int duration) {
    this.duration = duration;
  }

  /**
   * @return status
   */
  public Status getStatus() {
    return this.status;
  }

  /**
   * @param status_
   */
  public void setStatus(Status status) {
    this.status = status;
  }
  
  /**
   * @return dateNote
   */
  public String getDateNote() {
	  return dateNote;
  }
  
  /**
 * @return the name of the translation declaration file when TTA module is set to produce a file per each translation agreement.
 */
public String getTplFileFullName () {
	  return tplFileFullName;
  }
  
  /**
 * @param name of the translation declaration file
 */
public void setTplFileFullName (String name) {
	  this.tplFileFullName = name;
  }

  /**
 * @return the name of the translation declaration file when TTA module is set to produce a file per each translation agreement.
 */
public String getXmlFileFullName () {
	  return xmlFileFullName;
  }

  /**
 * @param name of the translation declaration file
 */
public void setXmlFileFullName (String name) {
	  this.xmlFileFullName = name;
  }
  
  /**
   * @param agr another agreement object
   * @return true if both agreements has same values for all properties, false in any other case
   */
  public boolean isEqualTo (TranslationAgreement agr){
    boolean r = false;
    
    if (this.name.equals(agr.name) && 
    	this.creationDate.equals(agr.creationDate) && 
    	this.leavingDate.equals(agr.leavingDate) &&
    	this.activationDate.equals(agr.activationDate) &&
    	(this.duration == agr.duration) &&
    	this.source.isEqualTo(agr.source) &&
    	this.target.isEqualTo(agr.target)){
            
    	r = true;
      }
    
    return r;
  }
  
}
