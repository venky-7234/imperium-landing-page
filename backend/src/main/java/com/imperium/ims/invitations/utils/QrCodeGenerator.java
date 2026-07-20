package com.imperium.ims.invitations.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * Utility for generating QR Codes.
 */
@Slf4j
@UtilityClass
public class QrCodeGenerator {

    /**
     * Generates a QR Code for the given text.
     *
     * @param text   the content to encode (e.g. an Invitation URL)
     * @param width  width of the QR code
     * @param height height of the QR code
     * @return Base64 encoded PNG image string, or empty string on error
     */
    public static String generateQrCodeBase64(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            log.error("Failed to generate QR code for text: {}", text, e);
            return "";
        }
    }

    /**
     * Generates a QR Code as raw bytes.
     */
    public static byte[] generateQrCodeBytes(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate QR code bytes for text: {}", text, e);
            return new byte[0];
        }
    }
}
