package trimble.transportation.entitlements.navbar.permissions.dto.enums;

import org.apache.commons.lang3.StringUtils;

public enum MatchingIdentifier {

    ACCOUNT_TYPE("ACCOUNT_TYPE"),
    ROLE("ROLE"),
    USER_ID("USER_ID"),
    DEFAULT("DEFAULT");

    private final String value;

    MatchingIdentifier(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static boolean ifValueExist(String value) {
        for (MatchingIdentifier objectType : values()) {
            if (StringUtils.isNotBlank(value) && objectType.getValue().equals(value))
                return true;
        }
        return false;
    }

}
