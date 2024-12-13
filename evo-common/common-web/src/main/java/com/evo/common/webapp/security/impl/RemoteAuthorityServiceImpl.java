package com.evo.common.webapp.security.impl;

import com.evo.common.UserAuthority;
import com.evo.common.client.iam.IamClient;
import com.evo.common.webapp.security.AuthorityService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoteAuthorityServiceImpl implements AuthorityService {
    private final IamClient iamClient;

    public RemoteAuthorityServiceImpl(IamClient iamClient) {
        this.iamClient = iamClient;
    }

    @Override
    public UserAuthority getUserAuthority(UUID userId) {
        return null;
    }

    @Override
    public UserAuthority getUserAuthority(String username) {
        return null;
    }

    @Override
    public UserAuthority getClientAuthority(UUID clientId) {
        return null;
    }
}
