package com.example.demo.review;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.demo.DataNotFoundException;
import com.example.demo.user.SiteUser;
import com.example.demo.user.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;


    public List<Review> getList() {
        return this.reviewRepository.findAll();
    }

    public Review getReview(Integer id) {
        Optional<Review> review = this.reviewRepository.findById(id);
        if (review.isPresent()) {
            return review.get();
        } else {
            throw new DataNotFoundException("review not found");
        }
    }

    public void create(String content, SiteUser siteUser, Integer grade) {
        Review r = new Review();
        r.setContent(content);
        r.setSiteUser(siteUser);
        r.setGrade(grade);
        r.setCreateDate(LocalDateTime.now());
        siteUser.getReview().add(r); //태일이와 웅식이의 눈물 햔재시각 오전 1:46
        this.reviewRepository.save(r);
    }
}

