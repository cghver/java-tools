package com.tools.validate;

import com.google.common.base.Strings;
import com.tools.string.StringHelper;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * Created by Darren at 2018-05-24 13:04
 */

public class ValidateHelper {
    public static <T> void validate(T t) throws ValidateException {
        ValidatorFactory vFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = vFactory.getValidator();
        Set<ConstraintViolation<T>> set = validator.validate(t);
        if (set.size() > 0) {
            StringBuilder validateError = new StringBuilder();
            for (ConstraintViolation<T> val : set) {
                validateError.append(val.getMessage());
            }
            throw new ValidateException(validateError.toString());
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static void isPhone(String userName) throws ValidateException {
        if (Strings.isNullOrEmpty(userName)) {
            throw new ValidateException("手机不能为空");
        }
        if (!StringHelper.regex("0?(13|14|15|17|18)[0-9]{9}", userName)) {
            throw new ValidateException("手机格式不对");
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static void isEmail(String userName) throws ValidateException {
        if (Strings.isNullOrEmpty(userName)) {
            throw new ValidateException("邮箱不能为空");
        }
        if (!StringHelper.regex("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}", userName)) {
            throw new ValidateException("邮箱格式不对");
        }
    }


    @SuppressWarnings("unused")
    public static void isPhoneOrEmail(String username) throws ValidateException {
        try {
            isEmail(username);
            isPhone(username);
        } catch (ValidateException e) {
            throw new ValidateException("手机或邮箱格式不对");
        }

    }

    @SuppressWarnings("unused")
    public static void isPassword(String password) throws ValidateException {
        if (Strings.isNullOrEmpty(password)) {
            throw new ValidateException("密码不合法");
        }
    }

    @SuppressWarnings("unused")
    public static void isIp(String ip) throws ValidateException {
        if (Strings.isNullOrEmpty(ip)) {
            throw new ValidateException("ip不合法");
        }
        String reg = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
        if (!StringHelper.regex(reg, ip)) {
            throw new ValidateException("ip不合法");
        }
    }

}
