package br.net.unicom.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonService {

    @Autowired
    ObjectMapper objectMapper;

    public HashMap<String, String> flatten(Object object, Class<?> view) {

        JsonNode node = objectMapper.createObjectNode();

        try {
            node = objectMapper.readTree(objectMapper.writerWithView(view).writeValueAsString(object));
        } catch (Exception e) {
        }

        return flatten(node, "");
    }

    public HashMap<String, String> flatten(JsonNode node, String prefix) {


        HashMap<String, String> r = new HashMap<>();
        Pattern pattern = Pattern.compile("(\\w+)\\.");


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

            String key = prefix.substring(0, prefix.length() - 1);
            Matcher matcher = pattern.matcher(key);
            key = matcher.replaceAll(
                m -> String.valueOf(m.group().charAt(0)) + ".");

            r.put(key, node.asText());


        }


        return r;
    }

    public String difference(HashMap<String, String> before, HashMap<String, String> after) {
        List<String> difference = new ArrayList<>();

        for (String key : before.keySet()) {
            if (!after.containsKey(key))
                difference.add("(-) %s: %s\n".formatted(key, before.get(key)));
            else if (!after.get(key).equals(before.get(key)))
                difference.add("%s: %s -> %s\n".formatted(key, before.get(key), after.get(key)));
        }

        for (String key : after.keySet()) {
            if (!before.containsKey(key))
                difference.add("(+) %s: %s\n".formatted(key, after.get(key)));
        }

        Collections.sort(difference);

        return difference.stream().collect(Collectors.joining(""));
    }
}
