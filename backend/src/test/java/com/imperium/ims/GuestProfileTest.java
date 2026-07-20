package com.imperium.ims;

import com.imperium.ims.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.UUID;

@SpringBootTest
public class GuestProfileTest {

    @Autowired
    private ApplicationService applicationService;

    @Test
    public void testGetGuestProfile() {
        try {
            // Using the same publicId from the logs
            applicationService.getGuestProfile(UUID.fromString("c90284c8-67f9-4c00-890e-b4321cbf40a0"));
            System.out.println("SUCCESSFULLY FETCHED PROFILE!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
