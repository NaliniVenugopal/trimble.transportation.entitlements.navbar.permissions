package trimble.transportation.entitlements.navbar.permissions.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trimble.transportation.entitlements.navbar.permissions.constants.NavbarPermissionsConstants;
import trimble.transportation.entitlements.navbar.permissions.service.NavbarPermissionsService;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequestMapping("/core/v1/navbar-permissions")
@RequiredArgsConstructor
public class NavbarPermissionsController {

    private final NavbarPermissionsService navbarPermissionsService;


    @GetMapping("permissions")
    public ResponseEntity<Object> getNavigationBarValues(
            @RequestHeader(value = NavbarPermissionsConstants.X_CREDENTIAL_JWT, required = false) String jwtToken
            //,@RequestHeader(value = "Authorization", required = false) String authorization
    ) throws Exception {
        log.info(" JWTToken " + jwtToken);
        var navRefdto = navbarPermissionsService.constructNavigationMenu(jwtToken);
        return ResponseEntity.status(CREATED).body(navRefdto);
    }

}
