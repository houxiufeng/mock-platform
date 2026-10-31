package com.mockplatform.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前端单页与静态资源显式路由
 * 确保不受 /{projectCode}/** 通配干扰
 *
 * @author mockplatform
 */
@RestController
public class WebPageController {

    private final Resource indexHtml = new ClassPathResource("static/index.html");
    private final Resource vueJs = new ClassPathResource("static/vue.global.prod.js");

    @GetMapping(value = {"/", "/index.html"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Resource> index() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                .body(indexHtml);
    }

    @GetMapping(value = "/vue.global.prod.js", produces = "application/javascript")
    public ResponseEntity<Resource> vueJs() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/javascript;charset=UTF-8"))
                .body(vueJs);
    }
}
