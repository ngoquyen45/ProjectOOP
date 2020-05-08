package com.viettel.dmsplus.sdk.network;

import com.viettel.dmsplus.sdk.utils.ObjectMapperHelper;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Hỗ trợ việc truy xuất các RESTful Webservices thông qua RestTemplate của thư viện Spring Android
 * @author ThanhNV60
 */
public final class RestTemplate extends org.springframework.web.client.RestTemplate {

    private static RestTemplate instance;

    public static RestTemplate get() {
        if (instance == null) {
            synchronized (RestTemplate.class) {
                if (instance == null) {
                    /*
                    // Nếu cần sử dụng self-signed certificate thì thay thế bằng
                    // TravelOneHttpComponentsClientHttpRequestFactory
                    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
                    clientHttpRequestFactory.setConnectTimeout(20000);
                    clientHttpRequestFactory.setReadTimeout(20000);
                    instance = new RestTemplate(clientHttpRequestFactory);
                     */

                    // Từ 2015-05-20 thí điểm chuyển sang sử dụng OkHttpClient
                    // Để trust một self-signed certificate thì tham khảo link sau
                    // https://github.com/square/okhttp/issues/1336
                    OkHttpRequestFactory okHttpRequestFactory = new OkHttpRequestFactory();
                    okHttpRequestFactory.setConnectTimeout(20000);
                    okHttpRequestFactory.setReadTimeout(20000);
                    instance = new RestTemplate(okHttpRequestFactory);
                }
            }
        }
        return instance;
    }

    private RestTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        super(clientHttpRequestFactory);
        List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>(2);
        MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();
        jacksonMessageConverter.setObjectMapper(ObjectMapperHelper.singletonObjectMapper());
        converters.add(new FormHttpMessageConverter());
        converters.add(jacksonMessageConverter);
        this.setMessageConverters(converters);
        this.setErrorHandler(new CustomResponseErrorHandler());
    }
}
