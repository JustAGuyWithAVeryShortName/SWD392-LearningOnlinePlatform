package com.hsp302.shared_english_e_learning_path.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Video {

    @Id
    UUID videoId;

    String title;
    String videoUrl;
    int duration;

    @ManyToOne
    Lesson lesson;
}