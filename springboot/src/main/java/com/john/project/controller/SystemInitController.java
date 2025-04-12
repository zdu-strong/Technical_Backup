package com.john.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.scheduled.SystemInitScheduled;

@RestController
public class SystemInitController extends BaseController {

    @Autowired
    private SystemInitScheduled systemInitScheduled;

    @GetMapping("/system-init")
    public ResponseEntity<?> getSystemHasInit() {
        var hasInit = this.systemInitScheduled.getHasInit();
        return ResponseEntity.ok(hasInit);
    }

}
