package com.example.blog.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TemplateEmail {
    private String title;
    private String message;
    private String mainTitle;
}
