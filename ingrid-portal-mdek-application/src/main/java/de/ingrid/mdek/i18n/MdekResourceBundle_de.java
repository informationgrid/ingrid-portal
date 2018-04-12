package de.ingrid.mdek.i18n;

import java.util.Locale;

/**
 * Specialised {@code MdekResourceBundle} for the "de" locale. This class is
 * needed to properly load the localised resource bundles in the German
 * language.
 *
 * @author Vikram Notay
 */
public final class MdekResourceBundle_de extends MdekResourceBundle {
    public MdekResourceBundle_de() {
        super(Locale.GERMAN);
    }
}
