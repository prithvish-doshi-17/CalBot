package com.se21.calbot.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Database schema
 */
@Entity
@Table(name = "Tokens")
@JsonSerialize
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

    @Id
    private String discordId;
    @Column(name = "token")
    private String token;
    @Column(name = "code")
    private String code;
    @Column(name = "expires")
    private LocalDateTime expirydatetime;
    @Column(name = "refresh")
    private String refreshToken;
    @Column(name = "scope")
    private String scope;
    @Column(name = "calType")
    private String calType;
    @Column(name = "calId")
    private String calId;

}
