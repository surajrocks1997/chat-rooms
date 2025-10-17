package com.chat_rooms.auth_handler.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "user_info", uniqueConstraints = {
        @UniqueConstraint(name = "Email_Unique", columnNames = "email"),
        @UniqueConstraint(name = "Username_Unique", columnNames = "username")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    @NotNull
    @Email
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = true)
    private String username;

    @Column(nullable = true)
    private String salt;

    @Column(nullable = false)
    private String authProvider;

    @Column(nullable = false)
    private boolean isEmailVerified;

    @Column(nullable = false)
    private boolean isSocialLogin;

    @Column(name= "profile_picture_mongo_id", nullable = true)
    private String profilePictureMongoId;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.username = this.email.substring(0, email.indexOf('@'));
        if (this.authProvider == null) this.authProvider = AuthProvider.LOCAL.getValue();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

}
