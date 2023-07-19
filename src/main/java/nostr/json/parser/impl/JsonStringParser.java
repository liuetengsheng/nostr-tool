package nostr.json.parser.impl;

import lombok.extern.java.Log;
import nostr.json.parser.BaseParser;
import nostr.json.parser.JsonParseException;
import nostr.types.values.impl.StringValue;

import java.util.logging.Level;

/**
 *
 * @author squirrel
 */
@Log
public class JsonStringParser extends BaseParser<StringValue> {

    public JsonStringParser(String json) {
        super(json.trim().substring(1, json.length()-1));
        log.log(Level.FINE, "Parsing string {0}", json.trim());
    }

    @Override
    public StringValue parse() throws JsonParseException {
        return new StringValue(json);
    }

}
