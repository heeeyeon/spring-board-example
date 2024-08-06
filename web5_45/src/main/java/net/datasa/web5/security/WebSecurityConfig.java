package net.datasa.web5.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 시큐리티 환경설정
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    //로그인 없이 접근 가능 경로
    private static final String[] PUBLIC_URLS = {
            "/"                     //root
            , "/images/**"          //이미지 경로
            , "/css/**"             //CSS파일들
            , "/js/**"              //JavaSCript 파일들
            , "/member/join"        //회원가입
            , "/member/idCheck"     //ID중복확인
            , "/member/info"        //개인정보 수정
            , "/board/listAll"      //게시판 전체 목록 보기
            , "/board/list"         //게시판 목록
            , "/board/read"         //게시판 글 읽기

    };

    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(author -> author
                .requestMatchers(PUBLIC_URLS).permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(formLogin -> formLogin
                    .loginPage("/member/loginForm")
                    .usernameParameter("id")
                    .passwordParameter("password")
                    .loginProcessingUrl("/member/login")
                    .defaultSuccessUrl("/", true)
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/member/logout")
                    .logoutSuccessUrl("/")
            );

        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
