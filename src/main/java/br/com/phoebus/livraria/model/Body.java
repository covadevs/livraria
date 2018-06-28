package br.com.phoebus.livraria.model;

import java.util.HashMap;
import java.util.Map;

public class Body {
    private Map<String, Object> body;

    public Body() {
        this.body = new HashMap<>();
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Body{" +
                "body=" + body +
                '}';
    }
}
