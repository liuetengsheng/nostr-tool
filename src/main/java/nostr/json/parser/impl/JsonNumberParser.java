package nostr.json.parser.impl;

import lombok.extern.java.Log;
import nostr.json.parser.BaseParser;
import nostr.json.parser.JsonParseException;
import nostr.types.values.impl.NumberValue;

import java.util.logging.Level;

/**
 *
 * @author squirrel
 */
@Log
public class JsonNumberParser extends BaseParser<NumberValue> {

    public JsonNumberParser(String json) {
        super(json.trim());
        log.log(Level.FINE, "Parsing number {0}", json.trim());
    }

    @Override
    public NumberValue parse() throws JsonParseException {
        try {
            return new NumberValue(Double.valueOf(json));
        } catch (NumberFormatException ex) {
            throw new JsonParseException(ex);
        }
    }

}
