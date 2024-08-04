package com.mywebsite.database_javaspring_reactjs.security.csfr;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class CsfrControllerAdvice {
    // @ModelAttribute
	// public void getCsrfToken(HttpServletResponse response, CsrfToken csrfToken) {
	// 	System.out.println(csrfToken.getHeaderName() + " " + csrfToken.getToken());
	// 	response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
	// }
}
