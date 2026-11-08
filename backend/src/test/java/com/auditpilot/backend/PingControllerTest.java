package com.auditpilot.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.auditpilot.backend.api.PingController;

class PingControllerTest {

    @Test
    void pingReturnsOk() {
        var body = new PingController().ping();
        assertEquals("ok", body.get("status"));
    }
} 