
package nostr.json.unmarshaller.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import nostr.base.IUnmarshaller;
import nostr.json.parser.impl.JsonExpressionParser;
import nostr.types.values.impl.ExpressionValue;

/**
 *
 * @author squirrel
 */
@Log
@Data
@AllArgsConstructor
public class JsonExpressionUnmarshaller implements IUnmarshaller<ExpressionValue> {

    private final String json;

    @Override
    public ExpressionValue unmarshall() {
        return new JsonExpressionParser(json).parse();
    }
}
