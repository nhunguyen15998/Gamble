package com.futech.entertainment.packages.users.services.interfaces;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.users.models.UserProfile;

public interface UserProfileServiceInterface extends BaseServiceInterface<UserProfile> {
    public boolean updateUserProfile(UserProfile userProfile);
    public Map<String, Object> updateProfilePhoto(MultipartFile photo, int userProfileId, String userId);
}
