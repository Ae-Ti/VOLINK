package com.example.demo.review;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {

    @NotNull(message = "평점은 필수 입력 값입니다.")
    @Min(value = 1, message = "평점은 최소 1점이어야 합니다.")
    @Max(value = 5, message = "평점은 최대 5점이어야 합니다.")
    private Integer grade;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;
}
