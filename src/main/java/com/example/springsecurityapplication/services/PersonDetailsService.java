package com.example.springsecurityapplication.services;


import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.repositories.PersonRepository;
import com.example.springsecurityapplication.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    @Autowired
    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    //вернуть лист всех юзеров
    public List<Person> getAllUsers(){
        return personRepository.findAll();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //получаем пользователя из таблицы Персон по логину с формы аутентификации
        Optional<Person> person = personRepository.findByLogin(username);
        //если пользователь не был найден
        if(person.isEmpty()){
            //выбрасываем исключение, что данный пользователь не найден
            //данное исключение будет поймано спринг секьюрити и сообщение выведется на страницу
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        //возвращаем объект пользователя
        return new PersonDetails(person.get());
    }
}
