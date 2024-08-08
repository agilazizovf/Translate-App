package com.translate.app.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientUpdateRequest {

    @NotEmpty(message = "Name must not be empty")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Column(columnDefinition = "VARCHAR(50)")
    private String name;

    @NotEmpty(message = "Surname must not be empty")
    @Size(min = 3, max = 50, message = "Surname must be between 3 and 50 characters")
    @Column(columnDefinition = "VARCHAR(50)")
    private String surname;

    @Size(min = 1, message = "Username must be at least 1 character")
    @Size(max = 30, message = "Username must be maximum 30 characters")
    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @NotEmpty(message = "Phone must not be empty")
    @Pattern(regexp = "^(\\+994|0)(50|51|55|70|77|99)\\d{7}$", message = "Phone number must be valid. Example: +994000000000 or 0000000000")
    @Column(columnDefinition = "VARCHAR(20)")
    @Size(min = 10, max = 20, message = "Phone must be between 10 and 20 characters")
    private String phone;

    @NotEmpty(message = "Email must not be empty")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Email must be valid.Example: firstname-lastname@example.com  ")
    @Column(columnDefinition = "VARCHAR(70)")
    @Size(min = 6, max = 70, message = "Email must be between 3 and 70 characters")
    private String email;

    @NotEmpty(message = "Address must not be empty")
    @Size(min = 3, max = 200, message = "Address must be between 3 and 200 characters")
    @Column(columnDefinition = "VARCHAR(200)")
    private String address;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birthday;

}
