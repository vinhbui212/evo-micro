package org.example.thuan_security.mapper;

import org.example.thuan_security.model.Users;
import org.example.thuan_security.request.RegisterRequestKCL;
import org.example.thuan_security.response.UserKCLResponse;
import org.example.thuan_security.response.UserResponse;
import org.mapstruct.Mapper;

public interface UserMapper {
    Users toUsers(RegisterRequestKCL requestKCL);
    UserKCLResponse toUserKCLResponse(Users users);
}
