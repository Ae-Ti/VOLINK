package com.example.demo.user;

import com.example.demo.question.Question;
import com.example.demo.question.QuestionRepository;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import org.springframework.ui.Model;

import org.springframework.http.HttpStatus;
import com.example.demo.user.UserForm;

import com.example.demo.question.QuestionService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final QuestionService questionService;
    private final QuestionRepository questionRepository;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value="/MyPage/{username}")
    public String MyPage(@PathVariable("username") String username, Principal principal, Model model) {
        SiteUser siteUser = userService.getUser(username);
        if (siteUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이용자 정보를 찾을 수 없습니다.");
        }
        if (!siteUser.getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        model.addAttribute("siteUser", siteUser);
        return "my_page";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/userModify/{username}")
    public String userModify(UserForm userForm, @PathVariable("username") String username, Principal principal, Model model) {
        SiteUser siteUser = userService.getUser(username);
        if (siteUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이용자 정보를 찾을 수 없습니다.");
        }
        if (!siteUser.getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정권한이 없습니다.");
        }
        model.addAttribute("siteUser", siteUser);
        userForm.setAge(siteUser.getAge());
        userForm.setEmail(siteUser.getEmail());
        userForm.setPhone(siteUser.getPhone());
        return "user_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/userModify/{username}")
    public String userModify(@Valid UserForm userForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("username") String username) {
        if (bindingResult.hasErrors()) {
            return "user_form";
        }
        SiteUser siteUser = this.userService.getUser(username);
        if (!siteUser.getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.userService.modify(siteUser, userForm.getAge(), userForm.getEmail(), userForm.getPhone());
        return String.format("redirect:/user/MyPage/%s", username);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/requestList/{username}")
    public String requestList(Principal principal, @PathVariable("username") String username, Model model) {
        SiteUser siteUser = this.userService.getUser(username);
        if (siteUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이용자 정보를 찾을 수 없습니다.");
        }
        if (!siteUser.getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        model.addAttribute("siteUser", siteUser);
        return "request_page";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/acceptList/{username}")
    public String acceptList(Principal principal, @PathVariable("username") String username, Model model) {
        SiteUser siteUser = this.userService.getUser(username);
        if (siteUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이용자 정보를 찾을 수 없습니다.");
        }
        if (!siteUser.getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        model.addAttribute("siteUser", siteUser);
        return "accept_page";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/completeList/{username}")
    public String completeList(Principal principal, @PathVariable("username") String username, Model model) {
        SiteUser siteUser = this.userService.getUser(username);
        if (siteUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이용자 정보를 찾을 수 없습니다.");
        }
        if (!siteUser.getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        model.addAttribute("siteUser", siteUser);
        return "complete_page";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/registrationList/{username}")
    public String registrationList(Principal principal, @PathVariable("username") String username, Model model) {
        SiteUser siteUser = this.userService.getUser(username);
        if (siteUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이용자 정보를 찾을 수 없습니다.");
        }
        if (!siteUser.getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        List<Question> questionList = this.questionRepository.findAll();
        model.addAttribute("questionList", questionList);
        model.addAttribute("siteUser", siteUser);
        return "registration_page";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/management/{id}")
    public String management(Principal principal, @PathVariable("id") Integer id, Model model) {
        Question question = questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "관리 권한이 없습니다.");
        }
        model.addAttribute("question", question);
        return "management_page";
    }
}

