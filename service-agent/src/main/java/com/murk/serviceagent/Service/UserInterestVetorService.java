package com.murk.serviceagent.Service;

import com.murk.serviceagent.pojo.UserInterestVetor;

import java.util.List;

public interface UserInterestVetorService {
    List<UserInterestVetor> getUserInterestVetor(int userId);
}
