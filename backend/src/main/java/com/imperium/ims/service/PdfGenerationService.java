package com.imperium.ims.service;

import com.imperium.ims.entity.Invitation;
import com.imperium.ims.invitations.utils.QrCodeGenerator;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class PdfGenerationService {

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public byte[] generateInvitationPdf(Invitation invitation) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Title
            Paragraph title = new Paragraph("YOU ARE INVITED")
                    .setBold()
                    .setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            // Guest Name
            document.add(new Paragraph("Guest: " + invitation.getGuestName())
                    .setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER));

            // Company & Designation
            if (invitation.getCompany() != null || invitation.getDesignation() != null) {
                String companyDesc = (invitation.getDesignation() != null ? invitation.getDesignation() + " " : "")
                        + (invitation.getCompany() != null ? "at " + invitation.getCompany() : "");
                document.add(new Paragraph(companyDesc)
                        .setFontSize(12).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10));
            }

            // Event Details
            document.add(new Paragraph("Event: " + invitation.getEvent().getTitle())
                    .setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            
            document.add(new Paragraph("Date: " + invitation.getEvent().getStartDateTime().format(dateFormatter))
                    .setFontSize(12).setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("Time: " + invitation.getEvent().getStartDateTime().format(timeFormatter))
                    .setFontSize(12).setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("Venue: " + invitation.getEvent().getVenue())
                    .setFontSize(12).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

            // QR Code
            String invitationUrl = frontendUrl + "/rsvp/" + invitation.getToken();
            byte[] qrBytes = QrCodeGenerator.generateQrCodeBytes(invitationUrl, 250, 250);
            if (qrBytes.length > 0) {
                ImageData imageData = ImageDataFactory.create(qrBytes);
                Image qrImage = new Image(imageData).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                document.add(qrImage);
            }

            // Invitation Number
            document.add(new Paragraph("Ticket No: " + invitation.getInvitationNumber())
                    .setFontSize(10).setFontColor(ColorConstants.GRAY).setTextAlignment(TextAlignment.CENTER).setMarginTop(10));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF for invitation ID: {}", invitation.getId(), e);
            throw new RuntimeException("Failed to generate PDF ticket", e);
        }
    }
}
