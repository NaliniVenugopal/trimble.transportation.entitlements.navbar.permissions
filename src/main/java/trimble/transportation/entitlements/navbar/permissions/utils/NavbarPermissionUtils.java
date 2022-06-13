package trimble.transportation.entitlements.navbar.permissions.utils;

import java.util.Arrays;
import java.util.Objects;

public class NavbarPermissionUtils {

    public static String concatStrings(String... values) {
        var sb = new StringBuilder();
        if (Objects.nonNull(values))
            Arrays.stream(values).forEach(sb::append);
        return sb.toString();
    }

}
