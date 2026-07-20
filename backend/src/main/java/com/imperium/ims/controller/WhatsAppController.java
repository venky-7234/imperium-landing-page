package com.imperium.ims.controller;

import com.imperium.ims.dto.ApiResponse;
import com.imperium.ims.service.WhatsAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for WhatsApp operations and provider webhooks.
 *
 * <p>Base URL: {@code /api/whatsapp}
 */
@RestController
@RequestMapping("/whatsapp")
@RequiredArgsConstructor
@Tag(name = "WhatsApp", description = "WhatsApp dispatch and webhook APIs")
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    @PostMapping("/webhook")
    @Operation(summary = "Handle incoming delivery status webhook from WhatsApp provider")
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> payload) {
        // TODO: Parse provider-specific payload, extract messageId and status
        String providerMessageId = (String) payload.get("MessageSid");
        String status = (String) payload.get("MessageStatus");
        whatsAppService.handleDeliveryStatusWebhook(providerMessageId, status);
        return ResponseEntity.ok("OK");
    }
}
