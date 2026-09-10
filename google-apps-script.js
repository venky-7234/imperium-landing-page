// ── VIORA ELITE - GOOGLE APPS SCRIPT AUTOMATION & EMAIL INTEGRATION ──────────
// Paste this code into your Google Apps Script editor (Extensions > Apps Script in Google Sheets)

function doPost(e) {
  try {
    var data = JSON.parse(e.postData.contents);
    var sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
    
    // Append entry row to Google Sheet
    sheet.appendRow([
      new Date(),
      data.name || "",
      data.email || "",
      (data.phoneCountryCode || "") + " " + (data.contactNumber || ""),
      (data.whatsappCountryCode || "") + " " + (data.whatsapp || ""),
      data.company || "",
      data.annualTurnover || "",
      data.journey || "",
      data.linkedinUrl || "",
      data.instagramUrl || "",
      data.facebookUrl || "",
      data.websiteUrl || "",
      data.notes || ""
    ]);

    // Send Automated Luxury Email Confirmation to the guest
    if (data.email) {
      sendConfirmationEmail(data);
    }

    return ContentService.createTextOutput(JSON.stringify({ status: "success" }))
      .setMimeType(ContentService.MimeType.JSON);
  } catch (error) {
    return ContentService.createTextOutput(JSON.stringify({ status: "error", message: error.toString() }))
      .setMimeType(ContentService.MimeType.JSON);
  }
}

function sendConfirmationEmail(data) {
  var recipient = data.email;
  var subject = "Application Received | Viora Elite";
  var guestName = data.name || "Valued Guest";
  
  var htmlBody = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Viora Elite Application Confirmation</title>
</head>
<body style="margin: 0; padding: 0; background-color: #0A0A0A; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; color: #F5F5F5;">
  <table role="presentation" width="100%" border="0" cellspacing="0" cellpadding="0" style="background-color: #0A0A0A; padding: 40px 10px;">
    <tr>
      <td align="center">
        <table role="presentation" width="100%" border="0" cellspacing="0" cellpadding="0" style="max-width: 600px; background-color: #121212; border: 1px solid #D4AF37; border-radius: 12px; overflow: hidden; padding: 40px 30px;">
          
          <!-- Header Logo / Brand -->
          <tr>
            <td align="center" style="padding-bottom: 25px;">
              <div style="font-family: Georgia, serif; font-size: 24px; font-weight: 300; letter-spacing: 4px; color: #D4AF37; text-transform: uppercase;">
                VIORA ELITE
              </div>
              <div style="font-size: 9px; letter-spacing: 3px; color: #BDBDBD; text-transform: uppercase; margin-top: 6px;">
                INVITE ONLY
              </div>
              <div style="width: 60px; height: 1px; background-color: #D4AF37; margin: 20px auto 0 auto;"></div>
            </td>
          </tr>

          <!-- Main Title -->
          <tr>
            <td align="center" style="padding-bottom: 20px;">
              <h1 style="font-family: Georgia, serif; font-size: 20px; font-weight: 400; color: #F5F5F5; letter-spacing: 2px; text-transform: uppercase; margin: 0;">
                Application Received
              </h1>
            </td>
          </tr>

          <!-- Message Body -->
          <tr>
            <td style="font-size: 14px; line-height: 1.8; color: #CCCCCC; padding-bottom: 25px;">
              <p style="margin-top: 0;">Dear <strong style="color: #D4AF37;">${guestName}</strong>,</p>
              <p>Thank you for expressing your interest in joining <strong>Viora Elite</strong>. We have successfully received your application.</p>
              <p>Our admissions committee carefully reviews each submission to ensure a curated experience for every visionary founder and leader in our community.</p>
              <p>We will evaluate your details and reach out personally regarding your invitation status.</p>
            </td>
          </tr>

          <!-- Submitted Details Summary Box -->
          <tr>
            <td style="padding-bottom: 30px;">
              <table role="presentation" width="100%" border="0" cellspacing="0" cellpadding="15" style="background-color: #1A1A1A; border: 1px solid #333333; border-radius: 8px;">
                <tr>
                  <td style="font-size: 12px; color: #BDBDBD; line-height: 1.8;">
                    <strong style="color: #D4AF37; font-size: 11px; text-transform: uppercase; letter-spacing: 1px; display: block; margin-bottom: 10px;">Submitted Application Summary:</strong>
                    <strong>Name:</strong> ${guestName}<br>
                    <strong>Email:</strong> ${data.email || 'N/A'}<br>
                    ${data.contactNumber ? '<strong>Phone:</strong> ' + (data.phoneCountryCode || '') + ' ' + data.contactNumber + '<br>' : ''}
                    ${data.company ? '<strong>Company:</strong> ' + data.company + '<br>' : ''}
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <!-- Footer -->
          <tr>
            <td align="center" style="border-top: 1px solid #222222; padding-top: 20px; font-size: 11px; color: #777777; line-height: 1.6;">
              <p style="margin: 0;">&copy; VIORA ELITE. Curated exclusively for visionary founders and leaders.</p>
            </td>
          </tr>

        </table>
      </td>
    </tr>
  </table>
</body>
</html>
  `;

  // Use GmailApp to send from contact@vioraelite.com
  GmailApp.sendEmail(recipient, subject, "", {
    htmlBody: htmlBody,
    name: "Viora Elite",
    from: "contact@vioraelite.com",
    replyTo: "contact@vioraelite.com"
  });
}
