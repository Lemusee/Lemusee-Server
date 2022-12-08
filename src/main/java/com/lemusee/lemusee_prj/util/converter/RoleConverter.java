package com.lemusee.lemusee_prj.util.converter;

import com.lemusee.lemusee_prj.util.type.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;

@Converter
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role attribute) {
        /* 객체에서 DB column으로 변경 */
        return attribute.getType();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        /* DB column에서 객체로 변경 */
        return Arrays.stream(Role.values())
                .filter(v -> v.getType().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("ROLE에 dbData=[%s]가 존재하지 않습니다.",dbData)));
    }
}
