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


package com.tta.commons.cte;



/**
 * Constants use to define the status of an agreement
 */
public enum Status {
  CREATED, // the entity has been totally created but it has not been activated
           // yet
  ACTIVE, // the entity is active
  ONHOLD, // an active entity that has been put on-hold due to... so, it is
          // active but should not be considered for evaluation
  ENDED, // the entity has finished its active time but it cannot be removed as
         // far as it can be considered for situtations which happened during
         // its active time.
  REMOVED; // the entity cannot be considered any more.
  
 
}
