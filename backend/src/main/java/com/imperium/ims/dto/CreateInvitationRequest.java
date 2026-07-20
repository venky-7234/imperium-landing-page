package com.imperium.ims.dto;

import com.imperium.ims.common.enums.Channel;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request DTO for creating a new invitation.
 */
@Data
public class CreateInvitationRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotBlank(message = "Guest name is required")
    @Size(max = 200)
    private String guestName;

    @Email(message = "Guest email must be valid")
    @Size(max = 150)
    private String guestEmail;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Guest phone is invalid")
    private String guestPhone;

    @Size(max = 500)
    private String guestNote;

    @Size(max = 150)
    private String company;

    @Size(max = 150)
    private String designation;

    private Channel channel = Channel.EMAIL;

    private LocalDateTime expiresAt;
}
