package com.murk.serviceagent.Mapper;

import com.murk.serviceagent.pojo.UserInterestVetor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface UserInterestVetorMapper {
    List<UserInterestVetor> selectUserInterestById(int userId);
}
