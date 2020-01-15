/*-
 * **************************************************-
 * InGrid Portal MDEK Application
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
package de.ingrid.mdek.i18n;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * A {@code ResourceBundle} wrapper for looking up localised string in multiple
 * resource bundles. This class looks sequentially in multiple
 * {@code PropertyResourceBundle}s and returs the first localised string that it
 * finds for the given key. If no such string is found, then it returns the
 * given key with "???" as both prefix and suffix.
 *
 * @author Vikram Notay
 */
public class MdekResourceBundle extends ResourceBundle {

    private static final Logger LOG = Logger.getLogger(MdekResourceBundle.class);

    private static final String PROFILE_BUNDLE = "de.ingrid.mdek.i18n.ProfileMessages";
    private static final String COMMON_BUNDLE = "de.ingrid.mdek.i18n.CommonMessages";

    private final Locale locale;
    private final List<PropertyResourceBundle> bundles;

    /**
     * Creates a fallback resource bundle.
     *
     * @see #MdekResourceBundle(Locale)
     */
    public MdekResourceBundle() {
        this(Locale.ENGLISH);
    }

    /**
     * Creates a resource bundle for the given locale. In the background, a
     * {@code PropertyResourceBundle} for the profile-specific localised strings
     * (if present) and another one for localised strings common to all profiles
     * for the given locale is initialised.
     *
     * @param locale the locale for this resource bundle
     */
    public MdekResourceBundle(Locale locale) {
        this.locale = locale;
        try {
            LOG.warn("Using resource bundle with locale: " + locale.getDisplayName());
        } catch(Exception ex) {
            LOG.warn("Stack trace: " + new RuntimeException());
        }
        bundles = new ArrayList<>(2);
        addBundle(PROFILE_BUNDLE, locale); // Profile has higher priority
        addBundle(COMMON_BUNDLE, locale);
    }

    private void addBundle(String bundle, Locale locale) {
        try {
            bundles.add((PropertyResourceBundle) PropertyResourceBundle.getBundle(bundle, locale));
        } catch (MissingResourceException ex) {
            LOG.warn("Resource bundle for localisation not found: " + bundle);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * Returns the localised string for the given key. If the profile-specific
     * localisation bundle contains the given key, then the associated value
     * is return. If not, then the localisation bundle common to all profiles
     * is searched and the associated value, if found, is returned. If neither
     * of the bundles contains the key, then the given key is returned after
     * surrounding it with "???".
     *
     * {@inheritDoc}
     *
     * @return the localised string associated with the given key, or the key
     * surrounded by "???" if no such association is found
     */
    @Override
    protected Object handleGetObject(String key) {
        for(PropertyResourceBundle rb : bundles) {
            try {
                /*
                 *If no value is found, then exception is thrown. We ignore this
                 * exception and search in the next available bundle.
                 */
                return rb.getString(key);
            } catch (MissingResourceException ignored) {}
        }
        LOG.warn("No localised string for key: " + key);
        return "???" + key + "???";
    }

    /**
     * Checks if the given key is present in either the profile-specific
     * resource bundle or the resource bundle common to all profiles.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(String key) {
        for (ResourceBundle rb: bundles) {
            if (rb.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Combines the keys from the profile-specific resource bundle and the
     * resource bundle common to all profiles.
     *
     * {@inheritDoc}
     */
    @Override
    protected Set<String> handleKeySet() {
        Set<String> keySet = new HashSet<>();
        bundles.forEach(e -> keySet.addAll(e.keySet()));
        return keySet;
    }

    /**
     * Fetches the combined keys from the profile-specific resource bundle and
     * the resource bundle common to all profiles.
     *
     * {@inheritDoc}
     */
    @Override
    public Enumeration<String> getKeys() {
        Vector<String> vector = new Vector<>(keySet());
        return vector.elements();
    }
}
