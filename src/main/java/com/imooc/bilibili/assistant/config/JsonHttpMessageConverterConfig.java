package com.imooc.bilibili.assistant.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * JSON信息转换器配置
 */
@Configuration
public class JsonHttpMessageConverterConfig {

    @Bean
    @Primary // 为了处理所有返回
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastConfig = new FastJsonConfig();
        fastConfig.setDateFormat("yyyy-MM-dd HH:mm:ss"); // 日期时间格式
        fastConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat, // 格式化输出
                SerializerFeature.WriteNullStringAsEmpty, // 把null转换为空串
                SerializerFeature.WriteNullListAsEmpty, // 把null转换为空列表
                SerializerFeature.WriteMapNullValue, // 把null转换为空Map
                SerializerFeature.MapSortField, // 把Map按key排序输出
                SerializerFeature.DisableCircularReferenceDetect // 禁止循环引用
        );
        fastConverter.setFastJsonConfig(fastConfig);
        return new HttpMessageConverters(fastConverter);
    }

}
