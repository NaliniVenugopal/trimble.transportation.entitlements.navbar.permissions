package trimble.transportation.entitlements.navbar.permissions.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;

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
	    //headers.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IjEifQ.eyJpc3MiOiJodHRwczovL3N0YWdlLmlkLnRyaW1ibGVjbG91ZC5jb20iLCJleHAiOjE2NjAxMzA3ODEsIm5iZiI6MTY2MDEyNzE4MSwiaWF0IjoxNjYwMTI3MTgxLCJqdGkiOiJiMGI0MTZlMzBhN2U0YjIyYWI3MmIxNjNhNWFkYmIzMiIsImp3dF92ZXIiOjIsInN1YiI6ImI4OTM0MjA0LTdmN2EtNDk1Yy1iY2Q0LWQwYmEyMTI0NzE3MSIsImlkZW50aXR5X3R5cGUiOiJ1c2VyIiwiYW1yIjpbImZlZGVyYXRlZCIsIm9rdGFfdHJpbWJsZSIsIm1mYSJdLCJhdXRoX3RpbWUiOjE2NjAxMjcxNzksImF6cCI6ImY0OTBjZWMwLTIxNmItNDZhYi1iYjJkLTg2MTAwMjhhMzdjYyIsImF1ZCI6WyJmNDkwY2VjMC0yMTZiLTQ2YWItYmIyZC04NjEwMDI4YTM3Y2MiXSwic2NvcGUiOiJ0bXMtZGV2In0.EbB6GxbU-2KxUoI-3FnmFbHoi1zTlluTESQT26pEpjq4nKaiwoKhZdNirbyirDLkrOCCyrEJzEgyp_wxUT0sZtuGPBpq6Y6n9e4lebcgam_R6hKB7ejUqDoY1_LTkDeo2JFx6gqA0Ty-810G2fcT9KRMgWOa04gbNU6cyfrTTsML_KQagesfvU3cRems_XJpeUVMkgL5WqN0pMOaAutXFCvZ76Ea-0QyL1izCBuBKoOod1XDpa3xQfho8CCyi3nzkEceEFKHOY9RJilc-hXnVqE02PoHwEFhi-DePDrCjAPKMNhXYm4kcGG5E3IPzbnApYlBS4jFvf8aCmGstlXf0g");
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
    	if(ObjectUtils.isNotEmpty(navBarPermission.getMapApplication())){
	    	switch(navBarPermission.getDefaultURL()) {
				case "/#/planning":
					isAppropriateDefaultUrl = navBarPermission.getMapApplication().get("operations") != null ? navBarPermission.getMapApplication().get("operations").contains("planning") : false;
					break;
				case "/#/waterfall-tendering":
					isAppropriateDefaultUrl = navBarPermission.getMapApplication().get("tendering") != null ? navBarPermission.getMapApplication().get("tendering").contains("tendering") : false;
					break;
				case "/#/carrier-tracking":
					isAppropriateDefaultUrl = navBarPermission.getMapApplication().get("operations") != null ? navBarPermission.getMapApplication().get("operations").contains("carrier-tracking") : false;
					break;
			}
    	}
    	if(!isAppropriateDefaultUrl)
    		navBarPermission.setDefaultURL("");
    }
    

}
