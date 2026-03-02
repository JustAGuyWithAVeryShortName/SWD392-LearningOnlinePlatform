package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SubmitQuizRequest {
    UUID optionId;
}
