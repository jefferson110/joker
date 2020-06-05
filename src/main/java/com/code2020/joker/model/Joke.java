package com.code2020.joker.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Joke {
    private Long id;
    private String title;
    private String publishDate;
    private Date dealDate;
    private String content;
}
