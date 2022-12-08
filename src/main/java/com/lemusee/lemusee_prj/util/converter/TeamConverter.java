package com.lemusee.lemusee_prj.util.converter;

import com.lemusee.lemusee_prj.util.type.Team;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;

@Converter
public class TeamConverter implements AttributeConverter<Team, String> {

    @Override
    public String convertToDatabaseColumn(Team attribute) {
        /* 객체에서 DB column으로 변경 */
        return attribute.getType();
    }

    @Override
    public Team convertToEntityAttribute(String dbData) {
        /* DB column에서 객체로 변경 */
        return Arrays.stream(Team.values())
                .filter(v -> v.getType().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Team에 dbData=[%s]가 존재하지 않습니다.",dbData)));
    }
}
