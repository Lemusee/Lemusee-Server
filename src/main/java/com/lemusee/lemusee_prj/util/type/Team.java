package com.lemusee.lemusee_prj.util.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Team {

    CURATOR("CURATOR"), CONTENT("CONTENT"), ADMIN("ADMIN"), CULTURE("CULTURE"), INACTIVE("INACTIVE");
    private final String type;
}
