package com.translate.app.model.dto.response;

import com.translate.app.model.enums.Status;
import lombok.Data;

@Data
public class ClientAddResponse {

    private String username;
    private Status status;
}
