/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
package de.ingrid.portal.interfaces.impl;

import de.ingrid.portal.interfaces.ChronicleInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author martin@wemove.com
 */
public class SNSChronicleInterfaceImpl implements ChronicleInterface {

    private static final Logger log = LoggerFactory.getLogger(SNSChronicleInterfaceImpl.class);

    private static ChronicleInterface instance = null;

    public static synchronized ChronicleInterface getInstance() {
        if (instance == null) {
            try {
                instance = new SNSChronicleInterfaceImpl();
            } catch (Exception e) {
                log.error("Error initiating the SNSChronicle Interface", e);
            }
        }
        return instance;
    }

    private SNSChronicleInterfaceImpl() {
        super();
    }
}
