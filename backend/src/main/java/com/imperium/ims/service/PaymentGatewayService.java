package com.imperium.ims.service;

/**
 * Extension Point: Future Payment Gateway Integration
 * 
 * This interface is prepared for upcoming payment processing (e.g., Stripe, Razorpay).
 * It will support paid ticket sales and VIP upgrade processing.
 */
public interface PaymentGatewayService {
    
    /**
     * Process a payment for an event application/ticket.
     */
    String processPayment(Long applicationId, Double amount, String currency);
    
    /**
     * Verify the payment status from webhook callback.
     */
    boolean verifyPaymentStatus(String transactionId);
}
