package org.example.thuan_security.service;

import lombok.RequiredArgsConstructor;
import org.example.thuan_security.model.History;
import org.example.thuan_security.repository.HistoryRepository;
import org.example.thuan_security.request.HistoryRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    public String createNewHistory(String filename,String action) {
        History history = new History();
        history.setAction(action);
        history.setFileName(filename);
        historyRepository.save(history);
        return "Luu thanh cong";
    }
}
