package io.wasupu.boinet.financial;

import java.math.BigDecimal;
import java.util.Map;

public class Money {

    public static Map<String, Object> convertMoneyToJson(BigDecimal amount) {
        return Map.of(
            "value", amount,
            "currency", "EUR");
    }

}
