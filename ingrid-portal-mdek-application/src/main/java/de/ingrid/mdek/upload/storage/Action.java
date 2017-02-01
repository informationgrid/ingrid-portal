package de.ingrid.mdek.upload.storage;

/**
 * Enumeration of file actions.
 */
public enum Action {
    CREATE("create"),
    READ("read"),
    DELETE("delete");

    private final String name;

    private Action(String s) {
        this.name = s;
    }

    public static Action lookup(String name) {
        for (Action action : values()) {
            if (action.toString().equalsIgnoreCase(name)) {
                return action;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
