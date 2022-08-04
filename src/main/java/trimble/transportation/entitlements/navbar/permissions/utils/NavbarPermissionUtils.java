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
    
    
    public static Map<String,String> constructHeaders(String apiCase, String jwtToken, String accountId) {
    	Map<String, String> headers = new HashMap<>();
    	headers.put("x-credential-jwt", jwtToken);
	    headers.put("Content-Type", "application/json");
		//comment
	    headers.put("Authorization", "Bearer eyJraWQiOiJmYjgyNzZhYi1jMzU5LTQ1MDctYjIxNS00OWZhMzVhMjRkODQiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI4ZGM1YTI3ZS03MjYxLTQ3NjktODI5My1hYTEwNTkyMzc3MWYiLCJ2ZXIiOiIxLjAiLCJpc3MiOiJodHRwczovL2FwaS5kZXYudHJpbWJsZS10cmFuc3BvcnRhdGlvbi5jb20iLCJhY2NvdW50X2lkIjoiYjY0MTc0M2MtNDY5Zi00YmUzLWI1MDMtYTZmYTgxMGI0MWVlIiwibmJmIjoxNjU5NTk5NTc4LCJhenAiOiIzNTQyMzU3ZTI0YzQ1OTcyMTlEODk1RUFBRmU5OGY3Iiwic2t1X2lkcyI6W10sImFjY291bnRfbmFtZSI6Ikt1ZWJpeCBTaGlwbWVudCBFbnRyeSIsIm5hbWUiOiJTaGlwbWVudCBFbnRyeSBGbG93IiwiaWRlbnRpdHlfdHlwZSI6ImFwcGxpY2F0aW9uIiwiZXhwIjoxNjU5NjAzMTc4LCJpYXQiOjE2NTk1OTk1NzgsImp0aSI6IjEyYTc3MTAzLWUzNDgtNDVhYi1hOTJmLTdhNDhhMmQ1NDcxNyJ9.u5aoWUfJ4mteHHPXIeBuK98muNFg1v1EHiNQmHTwkjkyoTKHwhTPPhKPyaJZtNyQ43wwy4zSS7IyTArl_Qp8qivxnNsUl1jqxU93fzc7LVAV42B5t-8jKQQZC2vem12uZVqi_mlBkHxh99TE0fXnLtIqUsG_-mmnYaWAfqGmSUXIecYwv7vG42yrw7CUJA3FrJ9QUCA5Esq5veWmG9iAg7lpuH1ZbMp9aFBWwFbBk6BEG07RmSjg0aR81_PVF1aScHIpzqPgSKhv-dwDUDx4hleEULajt2EXcyXKEBCiHittWbhrAaWrNJVPz086383Y7mcPIeImtXk7lavBgMGRgA");
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
