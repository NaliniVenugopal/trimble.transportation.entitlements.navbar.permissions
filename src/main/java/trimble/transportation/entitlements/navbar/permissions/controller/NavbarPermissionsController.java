package trimble.transportation.entitlements.navbar.permissions.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trimble.transportation.entitlements.navbar.permissions.constants.NavbarPermissionsConstants;
import trimble.transportation.entitlements.navbar.permissions.dto.NavBarPermission;
import trimble.transportation.entitlements.navbar.permissions.service.NavbarPermissionsService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/entitlements/v1/permissions")
@RequiredArgsConstructor
public class NavbarPermissionsController {

    private final NavbarPermissionsService navbarPermissionsService;


    @PostMapping
    public ResponseEntity<Object> postNavigationBarValues(@RequestBody NavBarPermission navBarPermission) {
        var response = navbarPermissionsService.postNavigationBarValues(navBarPermission);
        return ResponseEntity.status(CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<Object> updateNavigationBarValues(@RequestBody NavBarPermission navBarPermission) {
        var response = navbarPermissionsService.updateNavigationBarValues(navBarPermission);
        return ResponseEntity.status(CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Object> getNavigationBarValues(
            @RequestHeader(value = NavbarPermissionsConstants.X_CREDENTIAL_JWT, required = false) String jwtToken
            //,@RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        log.info(" JWTToken " + jwtToken);
        var navRefdto = navbarPermissionsService.constructNavigationMenu(jwtToken);
        return ResponseEntity.status(OK).body(navRefdto);
    }


    @GetMapping("identifier/{identifier}")
    public ResponseEntity<Object> getApplicationList(@PathVariable("identifier") String identifier,
                                                     @RequestParam(value = "matcher") String matcher) {
        var response = navbarPermissionsService.getApplicationList(identifier, matcher);
        return ResponseEntity.status(OK).body(response);
    }

}
