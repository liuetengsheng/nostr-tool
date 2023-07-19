
package nostr.json.unmarshaller.impl;

import lombok.extern.java.Log;
import nostr.json.parser.impl.JsonArrayParser;
import nostr.json.unmarshaller.BaseUnmarshaller;
import nostr.types.values.impl.ArrayValue;

/**
 *
 * @author squirrel
 */
@Log
public class JsonArrayUnmarshaller extends BaseUnmarshaller {

    public JsonArrayUnmarshaller(String json) {
        super(json);
    }

    @Override
    public ArrayValue unmarshall() {
        String jsonStr = getJson();

        return new JsonArrayParser(jsonStr).parse();        
    }
}
