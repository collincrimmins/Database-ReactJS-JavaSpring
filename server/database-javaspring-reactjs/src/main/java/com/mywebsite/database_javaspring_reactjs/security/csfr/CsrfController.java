package com.mywebsite.database_javaspring_reactjs.security.csfr;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class CsrfController {
    // Get CSFR Token on Session Start
    // @GetMapping("/csrf")
    // public CsrfToken csrf(CsrfToken csrfToken) {
    //     return csrfToken;
    // }
}
