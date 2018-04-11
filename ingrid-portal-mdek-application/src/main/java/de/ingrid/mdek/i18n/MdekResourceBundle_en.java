package de.ingrid.mdek.i18n;

import java.util.Locale;

/**
 * Specialised {@code MdekResourceBundle} for the "en" locale. This class is
 * needed to properly load the localised resource bundles in the English
 * language.
 *
 * @author Vikram Notay
 */
public class MdekResourceBundle_en extends MdekResourceBundle {
    public MdekResourceBundle_en(){
        super(Locale.ENGLISH);
    }
}
