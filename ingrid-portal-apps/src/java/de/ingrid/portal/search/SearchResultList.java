/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Created on 01.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search;

import java.util.Vector;

/**
 * @author joachim
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SearchResultList extends Vector {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2941647923169041473L;

	/**
	 * 
	 */
	private int numberOfHits = 0;

	/**
	 * @return
	 */
	public int getNumberOfHits() {
		return numberOfHits;
	}

	/**
	 * @param numberOfHits
	 */
	public void setNumberOfHits(int numberOfHits) {
		this.numberOfHits = numberOfHits;
	}

}
