package com.lemusee.lemusee_prj.util.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Team {

    CURATOR("큐레이터 팀"), CONTENT("컨텐츠 팀"), ADMIN("어드민 팀"), CULTURE("컬쳐 팀");

    private final String type;
}
