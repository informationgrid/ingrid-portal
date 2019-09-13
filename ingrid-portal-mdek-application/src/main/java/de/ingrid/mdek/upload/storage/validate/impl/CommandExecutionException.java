package de.ingrid.mdek.upload.storage.validate.impl;

public class CommandExecutionException extends Exception {
    private static final long serialVersionUID = 1L;

    public CommandExecutionException(final String msg) {
        super(msg);
    }

    public CommandExecutionException(final String msg, final Exception e) {
        super(msg, e);
    }
}
