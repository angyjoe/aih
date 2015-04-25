package info.sarihh.antiinferencehub;

import java.text.ParseException;
import java.util.StringTokenizer;
import javax.swing.text.DefaultFormatter;
/*
 * Author: Sari Haj Hussein
 */
public class IPAddressFormatter extends DefaultFormatter {

    @Override
    public final Object stringToValue(String text) throws ParseException {
        StringTokenizer tokenizer = new StringTokenizer(text, ".");
        short[] a = new short[4];
        for (int i = 0; i < 4; i++) {
            int b = 0;
            if (!tokenizer.hasMoreTokens()) {
                throw new ParseException("Too few bytes", 0);
            }
            try {
                b = Integer.parseInt(tokenizer.nextToken());
            } catch (NumberFormatException e) {
                throw new ParseException("Not an integer", 0);
            }
            if (b < 0 || b >= 256) {
                throw new ParseException("Byte out of range", 0);
            }
            a[i] = (short) b;
        }
        if (tokenizer.hasMoreTokens()) {
            throw new ParseException("Too many bytes", 0);
        }
        return a;
    }

    @Override
    public final String valueToString(Object value) throws ParseException {
        if (!(value instanceof short[])) {
            throw new ParseException("Not a byte[]", 0);
        }
        short[] a = (short[]) value;
        if (a.length != 4) {
            throw new ParseException("Length != 4", 0);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int b = a[i];
            builder.append(String.valueOf(b));
            if (i < 3) {
                builder.append('.');
            }
        }
        return builder.toString();
    }
}
