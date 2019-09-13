package de.ingrid.mdek.upload.storage.validate;

import de.ingrid.mdek.upload.ValidationException;

/**
 * Exception signaling an illegal file name. The HTTP status code is 418.
 */
public class IllegalNameException extends ValidationException {

    private static final int STATUS_CODE = 418;

    private static final long serialVersionUID = 1L;

    public IllegalNameException(final String message, final String file) {
        super(message, file, STATUS_CODE);
    }
}
