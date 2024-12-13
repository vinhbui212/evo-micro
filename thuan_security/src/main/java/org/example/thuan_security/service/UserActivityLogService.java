package org.example.thuan_security.service;

import org.example.thuan_security.model.Log;
import org.example.thuan_security.repository.LogRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserActivityLogService {

    private final LogRepository activityLogRepository;

    public UserActivityLogService(LogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void logActivity(String email, String action, String ipAddress, LocalDateTime localDateTime) {
        if (email == null || action == null || ipAddress == null || localDateTime == null) {
            throw new IllegalArgumentException("Invalid parameters for logging activity");
        }
        Log log = new Log(email, action, ipAddress, localDateTime);
        activityLogRepository.save(log);
    }
}
