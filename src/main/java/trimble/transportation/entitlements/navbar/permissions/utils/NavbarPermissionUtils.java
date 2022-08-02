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
	    //headers.put("Authorization", "Bearer eyJraWQiOiJmYjgyNzZhYi1jMzU5LTQ1MDctYjIxNS00OWZhMzVhMjRkODQiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI4ZGM1YTI3ZS03MjYxLTQ3NjktODI5My1hYTEwNTkyMzc3MWYiLCJ2ZXIiOiIxLjAiLCJpc3MiOiJodHRwczovL2FwaS5kZXYudHJpbWJsZS10cmFuc3BvcnRhdGlvbi5jb20iLCJhY2NvdW50X2lkIjoiYjY0MTc0M2MtNDY5Zi00YmUzLWI1MDMtYTZmYTgxMGI0MWVlIiwibmJmIjoxNjU5NDYwNTk5LCJhenAiOiIzNTQyMzU3ZTI0YzQ1OTcyMTlEODk1RUFBRmU5OGY3Iiwic2t1X2lkcyI6W10sImFjY291bnRfbmFtZSI6Ikt1ZWJpeCBTaGlwbWVudCBFbnRyeSIsIm5hbWUiOiJTaGlwbWVudCBFbnRyeSBGbG93IiwiaWRlbnRpdHlfdHlwZSI6ImFwcGxpY2F0aW9uIiwiZXhwIjoxNjU5NDY0MTk5LCJpYXQiOjE2NTk0NjA1OTksImp0aSI6ImY2NzU2M2Y3LTI3NzQtNGY3My1hMTJhLWVjZDNmNjk2MTljYyJ9.KiN_45WrtptyUojXYflEspWAz-u2ZsnQUhleTpFE_ETUewHHWBc-phL7NqaGPCBokDqmsLtM29Z3vocCQRAfKRdsgG4FsSqXmT-xtMB-bVNAOc7QRrMaGaUsHT_d2ZS5QOEQ4zC7FNrK6GPg531mD_boG0pMDbrwNJIW0gmL3PxmEgrkNTOnwrFGTqbOVZiheW1meHFVQB_49buCE9209jIjS4Ufsvy6LZjJCCWdHTOnHz2B-joGHkL_HJAJWNcuD6B1ouElNWnYV87Fwi1wWCM8wvB3UjxCXJ4vu9LEPynXb2HW-PhAw9rAeQG137q5-R5P1gDVLW_NQPtKjUsKlg");
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
