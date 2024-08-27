package com.example.demo.review;


import com.example.demo.question.Question;
import com.example.demo.user.SiteUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.demo.question.QuestionRepository;
import java.security.Principal;
import java.util.List;

import com.example.demo.user.UserService;

@RequiredArgsConstructor
@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final QuestionRepository questionRepository;

    @GetMapping(value = "/review/list/{username}")
    public String list(Model model, @PathVariable("username") String username) {
        SiteUser siteUser = userService.getUser(username);
        model.addAttribute("siteUser", siteUser);
        List<Question> questions = questionRepository.findByAuthor(siteUser);
        model.addAttribute("questions",questions);
        Double grades = siteUser.getReview().stream().mapToInt(Review::getGrade).average().orElse(0.0);
        model.addAttribute("grades",(Math.round(grades * 100) / 100.0));
        return "review_list";
    }

    @GetMapping(value = "/{username}/create")
    public String reviewCreate(ReviewForm reviewForm, @PathVariable("username") String username, Model model, SiteUser siteUser) {
        model.addAttribute("siteUser", siteUser);
        return "review_form";
    }

    @PostMapping(value = "/{username}/create")
    public String reviewCreate(Principal principal, Model model, @PathVariable("username") String username, @Valid ReviewForm reviewForm, BindingResult bindingResult) {
        SiteUser siteUser =  userService.getUser(username);
        model.addAttribute("siteUser", siteUser);
        if (bindingResult.hasErrors()) {
            return "review_form";
        }
        this.reviewService.create(reviewForm.getContent(), siteUser, reviewForm.getGrade());
        return "redirect:/review/list/{username}"; // 질문 저장후 질문목록으로 이동
    }
}

