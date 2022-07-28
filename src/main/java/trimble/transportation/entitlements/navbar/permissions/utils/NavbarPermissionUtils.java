package trimble.transportation.entitlements.navbar.permissions.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermission;

public class NavbarPermissionUtils {

    public static String concatStrings(String... values) {
        var sb = new StringBuilder();
        if (Objects.nonNull(values))
            Arrays.stream(values).forEach(sb::append);
        return sb.toString();
    }
    
    
    public static Map<String,String> constructHeaders(String apiCase, String jwtToken, String authorization, String accountId) {
    	Map<String, String> headers = new HashMap<>();
    	headers.put("x-credential-jwt", jwtToken);
	    headers.put("Content-Type", "application/json");	
	    headers.put("Authorization", authorization);
        switch(apiCase) {
    	  case "Account":
    	  case "Users":	  
    		  break;
    	  case "Permissions":
    		  headers.put("accountId", accountId);
    		  break;
    	}
        return headers;
    }
    
    public static  void validateDefaultUrl(NavBarPermission navBarPermission) {
    	boolean isAppropriateDefaultUrl = false;
    	switch(navBarPermission.getDefaultURL()) {
			case "/#/planning":
				isAppropriateDefaultUrl = navBarPermission.getMapApplication().get("operations").contains("planning");
				break;
			case "/#/waterfall-tendering":
				isAppropriateDefaultUrl = navBarPermission.getMapApplication().get("tendering").contains("tendering");
				break;
			case "/#/carrier-tracking":
				isAppropriateDefaultUrl = navBarPermission.getMapApplication().get("operations").contains("carrier-tracking");
				break;
		}
    	if(!isAppropriateDefaultUrl)
    		navBarPermission.setDefaultURL("");
    }
    

}
