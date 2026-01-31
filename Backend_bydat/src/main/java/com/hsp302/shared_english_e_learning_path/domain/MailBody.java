package com.hsp302.shared_english_e_learning_path.domain;

import lombok.Builder;

@Builder
public record MailBody(String[] to, String subject, String content) {
}
