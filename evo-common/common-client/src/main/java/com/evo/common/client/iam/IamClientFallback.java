package com.evo.common.client.iam;

import com.evo.common.UserAuthority;
import com.evo.common.dto.response.Response;
import com.evo.common.enums.ServiceUnavailableError;
import com.evo.common.exception.ForwardInnerAlertException;
import com.evo.common.exception.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IamClientFallback implements FallbackFactory<IamClient> {
    @Override
    public IamClient create(Throwable cause) {
        return new FallbackWithFactory(cause);
    }

    @Slf4j
    static class FallbackWithFactory implements IamClient {
        private final Throwable cause;

        FallbackWithFactory(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public Response<UserAuthority> getUserAuthority(UUID userId) {
            if (cause instanceof ForwardInnerAlertException) {
                return Response.fail((RuntimeException) cause);
            }
            return Response.fail(
                    new ResponseException(ServiceUnavailableError.IAM_SERVICE_UNAVAILABLE_ERROR));
        }

        @Override
        public Response<UserAuthority> getUserAuthority(String username) {
            if (cause instanceof ForwardInnerAlertException) {
                return Response.fail((RuntimeException) cause);
            }
            return Response.fail(
                    new ResponseException(ServiceUnavailableError.IAM_SERVICE_UNAVAILABLE_ERROR));
        }

        @Override
        public Response<String> getClientToken(String clientId, String clientSecret) {
            if (cause instanceof ForwardInnerAlertException) {
                return Response.fail((RuntimeException) cause);
            }
            return Response.fail(
                    new ResponseException(ServiceUnavailableError.IAM_SERVICE_UNAVAILABLE_ERROR));
        }

        @Override
        public Response<Boolean> isTokenBlacklisted(String token) {
            if (cause instanceof ForwardInnerAlertException) {
                return Response.fail((RuntimeException) cause);
            }
            return Response.fail(
                    new ResponseException(ServiceUnavailableError.IAM_SERVICE_UNAVAILABLE_ERROR));
        }
    }
}
