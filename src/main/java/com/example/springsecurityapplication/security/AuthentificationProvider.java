//package com.example.springsecurityapplication.security;
//
//import com.example.springsecurityapplication.services.PersonDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//
//@Component
//public class AuthentificationProvider implements AuthenticationProvider {
//
//    private final PersonDetailsService personDetailsService;
//
//    @Autowired
//    public AuthentificationProvider(PersonDetailsService personDetailsService) {
//        this.personDetailsService = personDetailsService;
//    }
//
//
//    //Логика по аутентификации в приложении
//    //за нас спринг секьюрити сам возьмет объект из формы и передаст его сюда
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        //Получаем логин с формы аутентификации
//        String login = authentication.getName();
//
//        //получаем пароль с формы аутентификации
//        String password = authentication.getCredentials().toString();
//
//        //т.к. даннй метод возвращает объект интерфейса UserDetails, то и объект мы создаем через интерфейс
//        UserDetails person = personDetailsService.loadUserByUsername(login);
//
//        //если праоль с формы не равен паролю пользователя из бд, который найден по логину,
//        if(!password.equals(person.getPassword())){
//            //выбрасываем исключение с сообщением
//            throw new BadCredentialsException("Некорретный пароль");
//        }
//
//        //возвращаем объект аутентификации. В данном объекте будет лежать пользователь, его пароль и права доступа
//        return new UsernamePasswordAuthenticationToken(person, password, Collections.emptyList());
//    }
//
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return true;
//    }
//}
