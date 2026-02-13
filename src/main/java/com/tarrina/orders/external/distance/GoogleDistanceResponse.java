package com.tarrina.orders.external.distance;
import java.math.BigDecimal;
import java.util.List;

public class GoogleDistanceResponse {

    private List<Row> rows;

    public boolean isValid() {
        return rows != null
                && !rows.isEmpty()
                && rows.get(0).elements != null
                && !rows.get(0).elements.isEmpty()
                && "OK".equals(rows.get(0).elements.get(0).status);
    }

    public BigDecimal getDistanceInKm() {
        long meters = rows.get(0).elements.get(0).distance.value;
        return BigDecimal.valueOf(meters).divide(BigDecimal.valueOf(1000));
    }

    static class Row {
        public List<Element> elements;
    }

    static class Element {
        public Distance distance;
        public String status;
    }

    static class Distance {
        public long value; // meters
    }
}
