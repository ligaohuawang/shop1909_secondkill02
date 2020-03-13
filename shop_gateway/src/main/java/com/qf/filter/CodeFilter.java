package com.qf.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class CodeFilter implements GatewayFilter, Ordered {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获得cookie中的codeToken
        //先拿到请求体
        ServerHttpRequest request = exchange.getRequest();
        HttpCookie codeToken = request.getCookies().getFirst("codeToken");
        //获得参数code
        String code = request.getQueryParams().getFirst("code");
        //判断codeToken是否有值
        if (null != codeToken) {
            //获得验证码的cookie
            String token = codeToken.getValue();
            //获得服务器端保存的验证码
            /* String rediscode  = (String)redisTemplate.opsForValue().get(token);*/
            //获得服务端存储的验证码
            String redisCode = redisTemplate.opsForValue().get(token);
            //判断rediscode及其和code是否一致
            if (redisCode != null && redisCode.equalsIgnoreCase(code)) {
                //放行
                return chain.filter(exchange);
            }
        }
        //执行到这里证明验证码不通过
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SEE_OTHER);//设置响应码
        //设置重定向的位置
        String msg = null;
        try {
            msg = URLEncoder.encode("验证码错误", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.getHeaders().set("Location", "info/error?msg=" + msg);
        return response.setComplete();
    }

    /***
     * 标记当前过滤器的优先级，值越小，优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 100;
    }
}
