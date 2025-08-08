package com.michelmaia.quickbite.security;

import com.michelmaia.quickbite.model.Role;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JWTObject {
    private String subject;
    private Date issuedAt;
    private Date expiration;
    private List<Role> roles;
}
