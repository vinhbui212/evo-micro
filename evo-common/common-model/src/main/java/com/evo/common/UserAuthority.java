package com.evo.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthority {
    private UUID userId;
    private String email;
    private String password;
    private boolean enabled;
    private boolean deleted;
    private boolean verified;
    private Boolean isRoot;
    private List<String> grantedPermissions;

}
