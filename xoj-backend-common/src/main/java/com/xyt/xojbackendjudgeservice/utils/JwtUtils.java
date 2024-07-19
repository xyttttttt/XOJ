package com.xyt.xojbackendjudgeservice.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    //设置token过期时间
    public static final long TOKEN_TIME_OUT = 1000 * 60 * 60 * 24 * 15;
    //密钥 现在的密钥是随便写的，实际中公司会提供
    public static final String TOKEN_SECRET = "dsTankljdhiTT123us2jZWLPHO";
    // 生成token字符串的方法
    public static String getJwtToken(Map params){
        String JwtToken = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() +
                        TOKEN_TIME_OUT))
                .signWith(SignatureAlgorithm.HS256, TOKEN_SECRET)//签名哈希
                .addClaims(params)
                .compact();
        return JwtToken;
    }
    /**
     * 判断token是否存在与有效
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 获取Token中的claims信息
     */
    public static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(TOKEN_SECRET)
                .parseClaimsJws(token).getBody();
    }

    /**
     * 判断token是否存在与有效
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");
            if(StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
