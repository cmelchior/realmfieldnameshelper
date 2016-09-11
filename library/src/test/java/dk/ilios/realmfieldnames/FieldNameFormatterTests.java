package dk.ilios.realmfieldnames;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldNameFormatterTests {

    private FieldNameFormatter formatter;

    @Before
    public void setUp() {
        this.formatter = new FieldNameFormatter();
    }

    @Test
    public void simple() {
        String result = formatter.format("foo");
        assertEquals("FOO", result);
    }

    @Test
    public void shortSimple() {
        String result = formatter.format("i");
        assertEquals("I", result);
    }

    @Test
    public void camelCase() {
        String result = formatter.format("fooBar");
        assertEquals("FOO_BAR", result);
    }

    @Test
    public void withUnderscore() {
        String result = formatter.format("foo_bar");
        assertEquals("FOO_BAR", result);
    }

    @Test
    public void withHyphen() {
        String result = formatter.format("foo-bar");
        assertEquals("FOO_BAR", result);
    }

    @Test
    public void camelCaseWithUnderscore() {
        String result = formatter.format("foo_barBaz");
        assertEquals("FOO_BAR_BAZ", result);
    }

    @Test
    public void uppercase() {
        String result = formatter.format("FOO");
        assertEquals("FOO", result);
    }

    @Test
    public void uppercaseWithUnderscore() {
        String result = formatter.format("FOO_BAR");
        assertEquals("FOO_BAR", result);
    }

    @Test
    public void nonStandardCamelCase() {
        String result = formatter.format("FOOBar");
        assertEquals("FOO_BAR", result);
    }

    @Test
    public void hungarianNotation() {
        String result = formatter.format("mFoo");
        assertEquals("FOO", result);
    }

    /**
     * @see <a href="https://github.com/cmelchior/realmfieldnameshelper/issues/6">Some Field names are missing leading Character</a>
     */
    @Test
    public void issue6() {
        String result = formatter.format("itemQuantityDelta");
        assertEquals("ITEM_QUANTITY_DELTA", result);
    }

    @Test
    public void useUSLocaleForConversion() {
        // Right now we use the US locale to do the conversion.
        // If field names are written in other languages, upper casing them might do the wrong
        // thing unless the proper locale is used. This will have to be user provided. Punting on
        // finding a good solution for now, but at least this unit test captures the problem.
        String result = formatter.format("Iııİii"); // Taken from http://www.i18nguy.com/unicode/turkish-i18n.html
        assertEquals("III_İII", result); // Wrong, it should have been: "III_İİİ"
    }

    @Test
    public void emojii() {
        String result = formatter.format("\uD83D\uDE00"); // Smiley
        assertEquals("\uD83D\uDE00", result);
    }

}
