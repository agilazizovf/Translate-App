package com.translate.app.dto.response;

import lombok.Data;

@Data
public class ClientUpdateResponse {

    private Integer id;
    private String name;
    private String surname;
    private String username;
    private String authority;
}
