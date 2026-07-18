package com.murk.serviceagent.Service.impl;

import com.murk.serviceagent.Mapper.AiChatLogMapper;
import com.murk.serviceagent.Service.AiChatLogService;
import com.murk.serviceagent.pojo.AiChatLog;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AiChatLogServiceImpl implements AiChatLogService {

    @Autowired
    private AiChatLogMapper aiChatLogMapper;
    @Override
    public List<AiChatLog> getAiChatLogById(int userId) {
        if(userId==0){
            return Collections.emptyList();
        }else{

            return aiChatLogMapper.selectAiChatById(userId);
        }
    }

    @Override
    public void saveAiResponse(int userId, String text) {
        aiChatLogMapper.insertText(userId,text);
    }

}
