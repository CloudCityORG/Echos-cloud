package com.murk.serviceagent.Mapper;

import com.murk.serviceagent.pojo.AiChatLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface AiChatLogMapper {
    List<AiChatLog> selectAiChatById(int userId);

    void insertText(int userId, String text);
}
