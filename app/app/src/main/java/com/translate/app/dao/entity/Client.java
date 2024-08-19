package com.translate.app.dao.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;

@Table(name = "clients")
@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String surname;

    private String username;

    private LocalDate birthday;

    private String address;

    private String phone;

    @Email
    @Column(unique = true)
    private String email;

    @Getter
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            user.setClient(this);
        }
    }
}
