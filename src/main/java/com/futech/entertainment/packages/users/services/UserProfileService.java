package com.futech.entertainment.packages.users.services;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.users.modelMappers.SignUpMapper;
import com.futech.entertainment.packages.users.models.User;
import com.futech.entertainment.packages.users.models.UserProfile;
import com.futech.entertainment.packages.users.services.interfaces.UserProfileServiceInterface;

@Service
public class UserProfileService extends BaseService<UserProfile> implements UserProfileServiceInterface{

    //crud
    public UserProfile createUserProfile(User user, SignUpMapper signUpMapper){
        try {
            UserProfile userProfile = new UserProfile();
            userProfile.setUserId(user.getId());
            userProfile.setFirstName(signUpMapper.getfirst_name());
            userProfile.setLastName(signUpMapper.getlast_name());
            userProfile.setThumbnail("/images/defaults/no-user.jpeg");
            userProfile.setWallpaper("/images/defaults/section-bg.jpg");
            userProfile.setBirth(LocalDate.parse("1998-02-10"));
            userProfile.setGender(Helpers.MALE);
            return this.create(userProfile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateUserProfile(UserProfile userProfile){
        try {
            return this.update(userProfile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //end crud

    public Map<String, Object> updateProfilePhoto(MultipartFile photo, int userProfileId, String userId){
        Map<String, Object> obj = new HashMap<String,Object>();
        try {
            if(!photo.isEmpty()){
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/users";
                File folderPath = new File(uploadDir+"/user"+userId);
                String photoName = Helpers.changePhotoName(photo.getOriginalFilename());
                if(!folderPath.exists()){
                    folderPath.mkdir();
                }
                String uploadFilePath = folderPath+"/"+photoName;
                byte[] bytes = photo.getBytes();
                Path path = Paths.get(uploadFilePath);
                Files.write(path, bytes);
                UserProfile userProfile = new UserProfile();
                userProfile.setId(userProfileId);
                userProfile.setThumbnail("/images/users/user"+userId+"/"+photoName);
                var updated = this.update(userProfile);
                if(updated){
                    obj.put("code", 200);
                    obj.put("data", photoName);
                } else {
                    obj.put("code", 500);
                    obj.put("data", "Cannot update photo");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("code", 500);
            obj.put("data", e.getMessage());
        }
        return obj;
    }
}
