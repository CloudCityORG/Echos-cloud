package com.murk.serviceaudit.Service;

import com.murk.serviceaudit.dto.ApplySongVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminSongService {
    List<ApplySongVo> selectapplySong();

    void uploadadmin(MultipartFile file);
}
