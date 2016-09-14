/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.mdek.beans;

import java.util.Map;

import de.ingrid.mdek.EnumUtil;
import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils.SearchtermType;

public class SNSTopicUpdateResult {
	private String title;
	private String alternateTitle;
	private String type;
	private String action;
	private int objects;
	private int addresses;

	public SNSTopicUpdateResult() {
	}

	// Construct a result for an update message returned by the backend
	public SNSTopicUpdateResult(Map<String, Object> updateMessage) {
		if (updateMessage.get(MdekKeys.JOBINFO_NUM_ADDRESSES) != null) {
			addresses = (Integer) updateMessage
					.get(MdekKeys.JOBINFO_NUM_ADDRESSES);
		}
		if (updateMessage.get(MdekKeys.JOBINFO_NUM_OBJECTS) != null) {
			objects = (Integer) updateMessage.get(MdekKeys.JOBINFO_NUM_OBJECTS);
		}
		SearchtermType searchTermType = EnumUtil.mapDatabaseToEnumConst(
				SearchtermType.class, (String) updateMessage
						.get(MdekKeys.TERM_TYPE));
		type = searchTermType.toString();
		action = (String) updateMessage.get(MdekKeys.JOBINFO_MESSAGES);
		title = (String) updateMessage.get(MdekKeys.TERM_NAME);
		alternateTitle = (String) updateMessage
				.get(MdekKeys.TERM_ALTERNATE_NAME);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAlternateTitle() {
		return alternateTitle;
	}

	public void setAlternateTitle(String alternateTitle) {
		this.alternateTitle = alternateTitle;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getObjects() {
		return objects;
	}

	public void setObjects(int objects) {
		this.objects = objects;
	}

	public int getAddresses() {
		return addresses;
	}

	public void setAddresses(int addresses) {
		this.addresses = addresses;
	}

	public String[] toStringArray() {
		return new String[] { title, alternateTitle, type, action,
				String.valueOf(objects), String.valueOf(addresses) };
	}
}
