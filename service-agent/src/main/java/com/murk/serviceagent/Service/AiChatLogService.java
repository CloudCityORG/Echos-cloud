package com.murk.serviceagent.Service;

import com.murk.serviceagent.pojo.AiChatLog;

import java.util.List;

public interface AiChatLogService {
    List<AiChatLog> getAiChatLogById(int userId);

    void saveAiResponse(int userId, String text);


}
