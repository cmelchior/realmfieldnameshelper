package dk.ilios.realmfieldnames;

import java.util.Locale;

/**
 * Class for encapsulating the rules for converting between the field name in the Realm model class
 * and the matching name in the "&lt;class&gt;Fields" class.
 */
public class FieldNameFormatter {

    public String format(String fieldName) {
        return format(fieldName, Locale.US);
    }

    public String format(String fieldName, Locale locale) {
        if (fieldName == null || fieldName.equals("")) {
            return "";
        }

        // Normalize word separator chars
        fieldName = fieldName.replace('-', '_');

        // Iterate field name using the following rules
        // lowerCase m followed by upperCase anything is considered hungarian notation
        // lowercase char followed by uppercase char is considered camel case
        // Two uppercase chars following each other is considered non-standard camelcase
        // _ and - are treated as word separators
        StringBuilder result = new StringBuilder(fieldName.length());

        if (fieldName.codePointCount(0, fieldName.length()) == 1) {
            result.append(fieldName);
        } else {
            Integer previousCodepoint = null;
            Integer currentCodepoint = null;
            final int length = fieldName.length();
            for (int offset = 0; offset < length; ) {
                previousCodepoint = currentCodepoint;
                currentCodepoint = fieldName.codePointAt(offset);

                if (previousCodepoint != null) {
                    if (Character.isUpperCase(currentCodepoint) && !Character.isUpperCase(previousCodepoint) && previousCodepoint == 'm' && result.length() == 1) {
                        // Hungarian notation starting with: mX
                        result.delete(0, 1);
                        result.appendCodePoint(currentCodepoint);

                    } else if (Character.isUpperCase(currentCodepoint) && Character.isUpperCase(previousCodepoint)) {
                        // InvalidCamelCase: XXYx (should have been xxYx)
                        if (offset + Character.charCount(currentCodepoint) < fieldName.length()) {
                            int nextCodePoint = fieldName.codePointAt(offset + Character.charCount(currentCodepoint));
                            if (Character.isLowerCase(nextCodePoint)) {
                                result.append("_");
                            }
                        }
                        result.appendCodePoint(currentCodepoint);

                    } else if (currentCodepoint == '-' || currentCodepoint == '_') {
                        // Word-separator: x-x or x_x
                        result.append("_");

                    } else if (Character.isUpperCase(currentCodepoint) && !Character.isUpperCase(previousCodepoint) && Character.isLetterOrDigit(previousCodepoint)) {
                        // camelCase: xX
                        result.append("_");
                        result.appendCodePoint(currentCodepoint);
                    } else {
                        // Unknown type
                        result.appendCodePoint(currentCodepoint);
                    }
                } else {
                    // Only triggered for first code point
                    result.appendCodePoint(currentCodepoint);
                }
                offset += Character.charCount(currentCodepoint);
            }
        }

        return result.toString().toUpperCase(locale);
    }
}
