package com.lemusee.lemusee_prj.util.baseUtil;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class BaseException extends Exception {
    private BaseResponseStatus status;
}