package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import com.hsp302.shared_english_e_learning_path.domain.enums.AgeGroup;
import com.hsp302.shared_english_e_learning_path.domain.enums.BlogStatus;
import com.hsp302.shared_english_e_learning_path.domain.enums.BlogType;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogResponse {

    UUID blogID;
    String blogName;
    Integer rate;
    String image;
    String description;
    String content;
    Integer readingTime;
    BlogType blogType;
    BlogStatus blogStatus;
    AgeGroup ageGroup;
    String createdAt;
    String updatedAt;
    UserResponse member;
}
