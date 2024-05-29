package br.net.unicom.backend.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class JsonService {

    public HashMap<String, String> flatten(JsonNode node, String prefix) {
        HashMap<String, String> r = new HashMap<>();
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                r.putAll(flatten(field.getValue(), prefix + field.getKey() + "."));
            }
        } else if (node.isArray()) {
            for (int i=0; i<node.size(); i++) {
                r.putAll(flatten(node.get(i), prefix + (i + 1) + "."));
            }
        } else {
            r.put(prefix, node.asText());
        }
        return r;
    }
}
