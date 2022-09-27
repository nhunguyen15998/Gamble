package com.futech.entertainment.packages.users.controllers.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.core.models.EmailDetails;
import com.futech.entertainment.packages.core.services.interfaces.EmailServiceInterface;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.users.modelMappers.ForgotCodeMappper;
import com.futech.entertainment.packages.users.modelMappers.ForgotPhoneMappper;
import com.futech.entertainment.packages.users.modelMappers.ForgotResetMapper;
import com.futech.entertainment.packages.users.modelMappers.SignInMapper;
import com.futech.entertainment.packages.users.modelMappers.SignUpMapper;
import com.futech.entertainment.packages.users.models.User;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;

@Controller
public class AuthController {
    
    @Autowired
    private UserServiceInterface userServiceInterface;
    @Autowired 
    private EmailServiceInterface emailServiceInterface;
    @Autowired
    private Timer timer;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/user/sign-up")
    public String getRegister(Model model, HttpSession session){
        if(session.getAttribute("phone") != null){
            return "home";
        }
        model.addAttribute("signUpMapper", new SignUpMapper());
        return "users/authentication/register";
    }

    @PostMapping("/user/sign-up")
    public String postSignUp(@Valid @ModelAttribute("signUpMapper") SignUpMapper signUpMapper, BindingResult bindingResult,
                              RedirectAttributes redirAttrs, Model model){
        try {
            if(!signUpMapper.getplain_password().trim().equals(signUpMapper.getconfirm_password().trim())){
                bindingResult.addError(new FieldError("signUpMapper", "confirm_password", "Passwords do not match"));
            } 
            if(bindingResult.hasErrors()){
                model.addAttribute("signUpMapper", signUpMapper);
                return "users/authentication/register";
            }
            boolean sent = this.userServiceInterface.createUserSignUp(signUpMapper);
            if(sent){
                redirAttrs.addFlashAttribute("successMsg", String.format("You've successfully signed up. We've just sent verification link to email %s. Please check it out to activate your account.", Helpers.hiddenEmail(signUpMapper.getEmail())));
                return "redirect:/user/sign-in";
            }
            redirAttrs.addFlashAttribute("errorMsg", "Oops! Something went wrong!");
            return "redirect:/user/sign-up";
        } catch (Exception e) {
            e.printStackTrace();
            return "users/authentication/register";
        }
    }

    @GetMapping("/user/activate-account/{activateCode}")
    public String getActivate(@PathVariable String activateCode, RedirectAttributes redirAttrs){
        try {
            var user = this.userServiceInterface.activateAccount(activateCode);
            var code = Integer.parseInt(user.get("code").toString());
            if(code == 200){
                redirAttrs.addFlashAttribute("successMsg", "Activated");
            } else {
                redirAttrs.addFlashAttribute("errorMsg", "User not found.");
            }
            return "redirect:/user/sign-in";
        } catch (Exception e) {
            e.printStackTrace();
            return "users/authentication/register";
        }
    }

    @GetMapping("/user/sign-in")
    public String getSignIn(Model model, HttpSession session){
        if(session.getAttribute("phone") != null){
            return "home";
        }
        model.addAttribute("signInMapper", new SignInMapper());
        return "users/authentication/sign_in";
    }

    @PostMapping("/user/sign-in")
    public String postSignIn(@Valid @ModelAttribute("signInMapper") SignInMapper signInMapper, BindingResult bindingResult,
                              RedirectAttributes redirAttrs, Model model, HttpSession session){
        try {
            var phone = signInMapper.getPhone();
            String[] selects = {"users.id as user_id, users.*, "+"user_configs.id as user_config_id, user_configs.user_id, user_configs.type, user_configs.config_string,"+
            "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
            "user_profiles.birth, user_profiles.gender"};
            var user = this.userServiceInterface.getUserByPhone(selects, phone);
            if(user.size() > 0){
                if(signInMapper.getplain_password().trim() != ""){
                    if(!passwordEncoder.matches(signInMapper.getplain_password(), user.get("hash_password").toString())){
                        bindingResult.addError(new FieldError("signInMapper", "plain_password", "Invalid password"));
                    } else {
                        session.setAttribute("firstName", user.get("first_name"));
                        session.setAttribute("lastName", user.get("last_name"));
                        if(user.get("thumbnail") != null){
                            session.setAttribute("thumbnail", user.get("thumbnail"));
                        }
                        session.setAttribute("phone", user.get("phone"));
                        session.setAttribute("user_id", user.get("user_id"));
                        session.setAttribute("is_admin", user.get("is_admin"));
                        session.setAttribute("user_profile_id", user.get("user_profile_id"));
                        var date = user.get("created_at").toString().substring(0, 10);
                        session.setAttribute("createdAt", date);
                        session.setAttribute("user_config_id", user.get("user_config_id"));
                        session.setAttribute("user_config_string", user.get("config_string"));
                        return "redirect:/";
                    }
                }
            }
            if(bindingResult.hasErrors()){
                model.addAttribute("signInMapper", signInMapper);
                return "users/authentication/sign_in";
            }
            redirAttrs.addFlashAttribute("errorMsg", "Oops! Something went wrong!");
            return "redirect:/user/sign-in";
        } catch (Exception e) {
            e.printStackTrace();
            return "users/authentication/sign_in";
        }
    }

    @PostMapping("/user/sign-out")
    public String postSignOut(HttpSession session){
        try {
            session.removeAttribute("firstName");
            session.removeAttribute("lastName");
            session.removeAttribute("thumbnail");
            session.removeAttribute("phone");
            session.removeAttribute("email");
            return "redirect:/user/sign-in";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
    }

    //forgot password
    //phone
    @GetMapping("/user/forgot-password-phone")
    public String getPhone(Model model, HttpSession session){
        if(session.getAttribute("forgotPasswordPhone") != null){
            session.removeAttribute("forgotPasswordPhone");
        }
        model.addAttribute("phoneMapper", new ForgotPhoneMappper());
        return "users/forgot-password/phone";
    }

    @PostMapping("/user/forgot-password-phone")
    public String postPhone(@Valid @ModelAttribute("phoneMapper") ForgotPhoneMappper phoneMapper, BindingResult bindingResult,
                             RedirectAttributes redirAttrs, Model model, HttpSession session){
        try {
            if(bindingResult.hasErrors()){
                model.addAttribute("phoneMapper", phoneMapper);
                return "users/forgot-password/phone";
            }
            var phone = phoneMapper.getPhone();
            String[] selects = {"users.id as user_id, users.*, "+
            "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
            "user_profiles.birth, user_profiles.gender"};
            var user = this.userServiceInterface.getUserByPhone(selects, phone);
            if(user != null){
                String code = Helpers.randomStringWithLength(10);
                session.setAttribute("forgotPasswordPhone", phone);
                User updatedUser = new User();
                updatedUser.setId(Integer.parseInt(user.get("user_id").toString()));
                updatedUser.setforgot_password_code(code);
                updatedUser.setCode_lifespan(LocalDateTime.now());
                this.userServiceInterface.updateUser(updatedUser);
                Thread sendingThread = new Thread(() -> {
                    this.emailServiceInterface.sendMailWithAttachment(new EmailDetails(
                        user.get("email").toString(),
                        String.format("<p>Dear our beloved user,</p>"+
                        "<br/>"+
                        "<p>Here is your password recovery code : %s which will be expired in 5 minutes. Please do not share it with anyone.</p>", code),
                        "Reset Password Code"
                    ), true);
                });
                sendingThread.start();
                redirAttrs.addFlashAttribute("successMsg", String.format("We've just sent reset password code to email %s. Please check it out to reset your password.", Helpers.hiddenEmail(user.get("email").toString())));
                return "redirect:/user/forgot-password-code";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "users/forgot-password/phone";
    }

    //code
    @GetMapping("/user/forgot-password-code")
    public String getCode(Model model, HttpSession session, RedirectAttributes redirAttrs){
        //no phone -> phone
        if(session.getAttribute("forgotPasswordPhone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Phone is required");
            return "redirect:/user/forgot-password-phone";
        }
        model.addAttribute("codeMapper", new ForgotCodeMappper());
        return "users/forgot-password/code";
    }

    @PostMapping("/user/forgot-password-code")
    public String postCode(@Valid @ModelAttribute("codeMapper") ForgotCodeMappper codeMapper, BindingResult bindingResult,
                            RedirectAttributes redirAttrs, Model model, HttpSession session){
        try {
            String code = codeMapper.getCode();
            if(code.trim() != ""){
                var user = this.userServiceInterface.getUserByForgotPasswordCode(code);
                if(user == null){
                    bindingResult.addError(new FieldError("codeMapper", "code", "Invalid code"));
                }
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                var codeLifespan = LocalDateTime.parse(user.get("code_lifespan").toString(), df);
                var expiredTime = codeLifespan.plusMinutes(5);
                if(LocalDateTime.now().compareTo(expiredTime) > 0){
                    bindingResult.addError(new FieldError("codeMapper", "code", "Invalid code"));
                }
            }
            if(bindingResult.hasErrors()){
                model.addAttribute("codeMapper", codeMapper);
                return "users/forgot-password/code";
            }
            session.setAttribute("forgotPasswordCode", code);
            return "redirect:/user/forgot-password-reset";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "users/forgot-password/code";
    }

    //reset
    @GetMapping("/user/forgot-password-reset")
    public String getReset(Model model, HttpSession session, RedirectAttributes redirAttrs){
        //no phone -> phone
        if(session.getAttribute("forgotPasswordPhone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Phone is required");
            return "redirect:/user/forgot-password-phone";
        }
        //no code -> code
        if(session.getAttribute("forgotPasswordCode") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Code is required");
            return "redirect:/user/forgot-password-code";
        }
        model.addAttribute("resetMapper", new ForgotResetMapper());
        return "users/forgot-password/reset";
    }

    @PostMapping("/user/forgot-password-reset")
    public String postReset(@Valid @ModelAttribute("resetMapper") ForgotResetMapper resetMapper, BindingResult bindingResult,
                             RedirectAttributes redirAttrs, Model model, HttpSession session){
        try {
            var plainPassword = resetMapper.getplain_password().trim();
            var confirmPassword = resetMapper.getconfirm_password().trim();
            if(plainPassword != "" && confirmPassword != "" && !plainPassword.equals(confirmPassword)){
                bindingResult.addError(new FieldError("resetMapper", "confirm_password", "Passwords do not match"));
            } 
            if(bindingResult.hasErrors()){
                model.addAttribute("codeMapper", resetMapper);
                return "users/forgot-password/reset";
            }
            String[] selects = {"users.id as user_id, users.*, "+
            "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
            "user_profiles.birth, user_profiles.gender"};
            var user = this.userServiceInterface.getUserByPhone(selects, session.getAttribute("forgotPasswordPhone").toString());
            if(user != null){
                User updatedUser = new User();
                updatedUser.setId(Integer.parseInt(user.get("user_id").toString()));
                updatedUser.setplain_password(plainPassword);
                updatedUser.sethash_password(passwordEncoder.encode(confirmPassword));
                updatedUser.setforgot_password_code("");
                this.userServiceInterface.updateUser(updatedUser);
                session.removeAttribute("forgotPasswordPhone");
                session.removeAttribute("forgotPasswordCode");
                redirAttrs.addFlashAttribute("successMsg", "Password changed.");
            } else {
                redirAttrs.addFlashAttribute("errorMsg", "Whoops! Something went wrong.");   
            }
            return "redirect:/user/sign-in";
        } catch (Exception e) {
            e.printStackTrace();
            return "users/forgot-password/reset";
        }
    }

}
