package com.example.springsecurityapplication.config;

import com.example.springsecurityapplication.security.PersonDetails;
import com.example.springsecurityapplication.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//основной конфиг для конфигурации безопасности в приложении
@EnableWebSecurity
//аннотация сообщает, что в приложении доступно разграничение ролей на основе аннотаций
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private  final PersonDetailsService personDetailsService;
    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    //метод по настройке аутентификации
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        //производим аутентификацию с помощью сервиса, сопоставляем введенный пароль хешированием
        authenticationManagerBuilder.userDetailsService(personDetailsService)
        .passwordEncoder(getPasswordEncoder());
    }
    //конфигурирем сам спринг секьюрити, конфиг авторизацию
    @Override
    protected void configure(HttpSecurity http) throws Exception{

        //отключить защиту от межсайтовой подделки запросов .csrf().disable()
        http
        //указываем, что все страницы должны быть защищены аутентификацией
        .authorizeRequests()
                //указываем, что /admin доступен пользователю с ролью админа
        .antMatchers("/admin/**").hasAnyRole("ADMIN")

        //не аутентиф пользователям доступны эти две страницы
        //с помощью permitAll() указываем, что данные страницы по умолчанию доступны всем пользователям
        .antMatchers("/auth/login", "/error", "/auth/registration", "/product", "/product/info/{id}", "/img/**", "/product/search").permitAll()
        //все остальные страницы доступны юзеру и админу
//        .anyRequest().hasAnyRole("USER", "ADMIN")
        //для всех остальных страниц необходимо вызывать метод authenticated, который открывает форму аутентификации
        .anyRequest().authenticated()
        //дальше конфигурироваться будет аутентификация и соединяем аутентификацию с настройкой доступа
        .and()
        .formLogin().loginPage("/auth/login")
//        Указываем на какой url адрес будут отправляться данные с формы. Нам уже не нужно будет     создавать метод в контроллере и
        //обрабатывать данные с формы. Мы задали url по умолчанию, который позволяет обрабатывать форму аутентификации в спринг секьюрити.
        //Спринг секьюрити будет ждать логин и пароль с формы и затем сверить их с данными в БД
        .loginProcessingUrl("/process_login")
//        Указываем на какой url необходимо направить пользователя после успешной аутентификации
        // Вторым аргументом ставим true чтобы перенаправление на данную страницу шло в любом случае при успешной аутентификации
        .defaultSuccessUrl("/index", true)
//        Указываем куда необходимо перенаправить пользователя при проваленной аутентификации
        // В url будет передан объект. Данный объект мы будем проверять на форме и если он есть будет выводить сообщение "Неправильный логин или пароль"
        .failureUrl("/auth/login?error")
        .and()
        //указываем, что при переходе на /logout будет очищена сессия пользователя и перенаправление на страницу аутентификации
        .logout().logoutUrl("/logout").logoutSuccessUrl("/auth/login");
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers( "/static/**", "/css/**", "/js/**", "/img/**", "/icon/**", "/media/**");
    }
}
