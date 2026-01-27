package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateBlogRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateBlogRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.BlogResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Blog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlogMapper {

    Blog toEntity(CreateBlogRequest request);
    Blog toEntity(UpdateBlogRequest request);
    Blog toEntity(BlogResponse response);
    BlogResponse toDto(Blog blog);
}
