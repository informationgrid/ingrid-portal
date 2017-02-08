package de.ingrid.mdek.upload;

/**
 * Documents gives access to the database entries containing file uploads.
 */
public class Documents {

    /**
     * Check if a file is referenced in the database.
     * @param path
     * @return boolean
     */
    public static boolean isReferenced(String path) {
        // SELECT data AS link FROM additional_field_data WHERE field_key = 'link' AND data = {path}
        return true;
    }

    /**
     * Check if a file is expired.
     * NOTE: This will only return true if the file is referenced and not expired.
     * @param path
     * @return boolean
     */
    public static boolean isExpired(String path) {
        // SELECT a1.data AS expireDate, a2.data AS link FROM additional_field_data a1, additional_field_data a2
        // WHERE a1.parent_field_id = a2.parent_field_id AND a1.sort = a2.sort AND a1.field_key = 'expires' AND a2.field_key = 'link'
        // AND a2.data = {path}
        return false;
    }
}
