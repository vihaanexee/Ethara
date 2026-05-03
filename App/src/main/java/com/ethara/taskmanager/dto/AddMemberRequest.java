package com.ethara.taskmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AddMemberRequest {
    @NotBlank(message = "Member email is required") @Email(message = "Invalid email format") private String email;
    @NotBlank(message = "Role is required") private String role;
}
