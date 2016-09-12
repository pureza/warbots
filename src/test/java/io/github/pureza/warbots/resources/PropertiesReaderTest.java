package io.github.pureza.warbots.resources;

import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PropertiesReaderTest {

    private PropertiesReader reader;


    @Before
    public void setUp() {
        Properties props = new Properties();
        props.put("true", "true");
        props.put("true-with-whitespace", "  true  ");
        props.put("false", "false");
        props.put("illegal-true", "illegal-true");
        props.put("false-mixed-case", "fAlSE");

        props.put("int", "123");
        props.put("int-with-whitespace", "    123   ");
        props.put("not-a-valid-int", "not a valid number");

        props.put("double", "123.0");
        props.put("double-with-whitespace", "    123.0   ");
        props.put("not-a-valid-double", "not a valid number");

        props.put("long", "123");
        props.put("long-with-whitespace", "    123   ");
        props.put("not-a-valid-long", "not a valid number");

        props.put("string", "some text");
        props.put("string-with-whitespace-around", "   trim this   ");

        reader = new PropertiesReader(props);
    }


    /*
     * boolean getBoolean(String propertyName)
     */


    @Test
    public void getBooleanAcceptsTrue() {
        assertThat(reader.getBoolean("true"), is(true));
    }


    @Test
    public void getBooleanAcceptsFalse() {
        assertThat(reader.getBoolean("false"), is(false));
    }


    @Test(expected=IllegalArgumentException.class)
    public void getBooleanFailsForIllegalBoolean() {
        reader.getBoolean("illegal-true");
    }


    @Test(expected=NoSuchElementException.class)
    public void getBooleanFailsForNonExistentProperty() {
        reader.getBoolean("non-existent");
    }


    @Test
    public void getBooleanIsCaseInsensitive() {
        assertThat(reader.getBoolean("false-mixed-case"), is(false));
    }


    @Test(expected=IllegalArgumentException.class)
    public void getBooleanDoesNotTrimValue() {
        reader.getBoolean("true-with-whitespace");
    }

    
    /*
     * int getInt(String propertyName)
     */

    @Test
    public void getIntAcceptsInteger() {
        assertThat(reader.getInt("int"), is(123));
    }


    @Test(expected=NumberFormatException.class)
    public void getIntDoesNotTrimValue() {
        reader.getInt("int-with-whitespace");
    }


    @Test(expected=NumberFormatException.class)
    public void getIntFailsForInvalidInteger() {
        reader.getInt("not-a-valid-int");
    }


    @Test(expected=NoSuchElementException.class)
    public void getIntFailsForNonExistentProperty() {
        reader.getInt("non-existent");
    }
    
    
    /*
     * int getDouble(String propertyName)
     */

    @Test
    public void getDoubleAcceptsDouble() {
        assertThat(reader.getDouble("double"), is(123.0));
    }


    @Test
    public void getDoubleTrimsValue() {
        // XXX It is weird that getDouble() trims the value, while getInt() and
        // XXX getLong() don't. This is because we use Double.parseValue()
        // XXX internally, which trims the value, while Integer.parseInt()
        // XXX and Long.parseLong() don't.
        assertThat(reader.getDouble("double-with-whitespace"), is(123.0));
    }


    @Test(expected=NumberFormatException.class)
    public void getDoubleFailsForInvalidDouble() {
        reader.getDouble("not-a-valid-double");
    }


    @Test(expected=NoSuchElementException.class)
    public void getDoubleFailsForNonExistentProperty() {
        reader.getDouble("non-existent");
    }
    
    
    /*
     * int getLong(String propertyName)
     */

    @Test
    public void getLongAcceptsLong() {
        assertThat(reader.getLong("long"), is(123L));
    }


    @Test(expected=NumberFormatException.class)
    public void getLongDoesNotTrimValue() {
        reader.getLong("long-with-whitespace");
    }


    @Test(expected=NumberFormatException.class)
    public void getLongFailsForInvalidLong() {
        reader.getLong("not-a-valid-long");
    }


    @Test(expected=NoSuchElementException.class)
    public void getLongFailsForNonExistentProperty() {
        reader.getLong("non-existent");
    }


    /*
     * String getString(String propertyName)
     */

    @Test
    public void getStringReadsString() {
        assertThat(reader.getString("string"), is("some text"));
    }


    @Test
    public void getStringDoesNotTrimValue() {
        assertThat(reader.getString("string-with-whitespace-around"), is("   trim this   "));
    }


    @Test(expected=NoSuchElementException.class)
    public void getStringFailsForNonExistentProperty() {
        reader.getString("non-existent");
    }

}
