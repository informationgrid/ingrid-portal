/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.interfaces.ChronicleInterface;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author martin@wemove.com
 */
public class SNSChronicleInterfaceImpl implements ChronicleInterface {

    private final static Logger log = LoggerFactory.getLogger(SNSChronicleInterfaceImpl.class);

    private static ChronicleInterface instance = null;

    public static synchronized ChronicleInterface getInstance() {
        if (instance == null) {
            try {
                instance = new SNSChronicleInterfaceImpl();
            } catch (Exception e) {
                log.error("Error initiating the SNSChronicle Interface");
                e.printStackTrace();
            }
        }
        return instance;
    }

    private SNSChronicleInterfaceImpl() throws Exception {
        super();
    }
}
