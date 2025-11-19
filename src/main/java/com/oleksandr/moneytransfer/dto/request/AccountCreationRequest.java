package com.oleksandr.moneytransfer.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountCreationRequest(

        @NotNull(message = "First name is required")
        @Size(min = 3, max = 30, message = "First name must be between 3 and 30 chars")
        String firstName,

        @NotNull(message = "Last name is required")
        @Size(min = 3, max = 30)
        String lastName
) { }
