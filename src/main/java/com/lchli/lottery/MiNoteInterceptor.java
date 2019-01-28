package com.lchli.lottery;

import com.lchli.lottery.util.Constants;
import com.lchli.lottery.util.EncryptUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MiNoteInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String sign = request.getHeader("sign");
        String ts = request.getHeader("ts");

        if (StringUtils.isEmpty(sign) || StringUtils.isEmpty(ts)) {
            System.err.println("preHandle intercepted--------");
            return false;
        }

        String serverSign = EncryptUtils.encryptMD5ToString(Constants.APP_KEY + ts);
        if (!sign.equals(serverSign)) {
            System.err.println("preHandle intercepted=========");
            return false;
        }


        return true;
    }
}
