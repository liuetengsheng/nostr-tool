package nostr.json.unmarshaller.impl;

import lombok.extern.java.Log;
import nostr.json.parser.impl.JsonNumberParser;
import nostr.json.unmarshaller.BaseUnmarshaller;
import nostr.types.values.impl.NumberValue;

/**
 *
 * @author squirrel
 */
@Log
public class JsonNumberUnmarshaller extends BaseUnmarshaller {

    public JsonNumberUnmarshaller(String json) {
        super(json);
    }

    @Override
    public NumberValue unmarshall() {
        String jsonStr = getJson();
        return new JsonNumberParser(jsonStr).parse();
    }

}
