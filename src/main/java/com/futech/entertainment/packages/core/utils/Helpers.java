package com.futech.entertainment.packages.core.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.servlet.http.Cookie;

public class Helpers {
    public static final int INCOMPLETE = 0;
    public static final int COMPLETED = 1;

    public static final int DEACTIVATED = 0;
    public static final int ACTIVATED = 1; 
    public static final int BANNED = 2; 
    
    public static final int MALE = 0;
    public static final int FEMALE = 1; 
    public static final int OTHER = 2;

    public static String formatDateTime(LocalDateTime dateTime){
        return DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss").format(dateTime);
    }

    public static String formatDate(LocalDate date){
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static LocalDateTime toLocalDateTime(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); 
        LocalDateTime dateTime = LocalDateTime.parse(date.replace("T", " "), formatter);
        return dateTime;
    }

    public static String randomString(){
        return UUID.randomUUID().toString().replaceAll("_", "").replace("-", "");
    }
    public static String randomForImageName()
    {
        return DateTimeFormatter.ofPattern("ddMMyyyyHHmmss").format(LocalDateTime.now());
    }
    /* <= 32 */
    public static String randomStringWithLength(int length){
        return UUID.randomUUID().toString().replaceAll("_", "").replace("-", "").substring(0, length > 32 || length < 0 ? 32 : length);
    }

    public static String hiddenEmail(String email){
        String hiddenStr = "";
        String firstCharacter = email.substring(0, 1);
        for(int i = 0; i < email.substring(1, email.indexOf('@')).length(); i++){
            hiddenStr += '*';
        }
        System.out.println(firstCharacter+hiddenStr+email.substring(email.indexOf('@'), email.length()));
        return firstCharacter+hiddenStr+email.substring(email.indexOf('@'), email.length());
    }

    public static void countDown(int count){
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = count;
            public void run() {
                System.out.println(i--);
                if (i< 0)
                    timer.cancel();
            }
        }, 0, 300000);
    }

    public static String getCookieValue(Cookie[] cookies, String name){
        String value = "";
        if(cookies != null){
            for (int i = 0; i < cookies.length; i++) {
                if(cookies[i].getName().equals(name)){
                    value = cookies[i].getValue();
                }
            }
        }
        return value;
    }

    //change photo name
    public static String changePhotoName(String original){
        return UUID.randomUUID().toString()
                   .replaceAll("_", "")
                   .replace("-", "")
                   .substring(0, 15)+"_"+original;
    }


}
