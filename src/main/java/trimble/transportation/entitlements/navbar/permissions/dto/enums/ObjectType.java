package trimble.transportation.entitlements.navbar.permissions.dto.enums;

import org.apache.commons.lang3.StringUtils;

public enum ObjectType {

    SHIPMENT("Shipment"),
    LOAD("Load"),
    ORDER("Order");

    private final String value;

    ObjectType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static boolean ifValueExist(String value) {
        for (ObjectType objectType : values()) {
            if (StringUtils.isNotBlank(value) && objectType.getValue().equals(value))
                return true;
        }
        return false;
    }

}
