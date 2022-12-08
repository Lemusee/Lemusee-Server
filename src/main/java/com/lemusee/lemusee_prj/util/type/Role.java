package com.lemusee.lemusee_prj.util.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER("USER"), ADMIN("ADMIN");

    private final String type;
}
