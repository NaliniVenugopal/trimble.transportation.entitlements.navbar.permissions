package trimble.transportation.entitlements.navbar.permissions.dto.enums;

import org.apache.commons.lang3.StringUtils;

public enum AccountType {

    BROKER("BROKER"),
    SHIPPER("SHIPPER"),
    CONTRACT("CONTRACT");

    private final String value;

    AccountType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static boolean ifValueExist(String value) {
        for (AccountType objectType : values()) {
            if (StringUtils.isNotBlank(value) && objectType.getValue().equals(value))
                return true;
        }
        return false;
    }

}
