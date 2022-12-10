package com.example.springsecurityapplication.repositories;

import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.models.Person;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByPerson(Person person);

    Optional<Order> findById(int id);

    //вывод всех заказов
//    @Query(value = "select * from orders", nativeQuery = true)
//    List<Order> findAll(Order order);

    //поиск по номеру заказа, где считывается конец строки
//    @Query(value = "select * from orders where orders.number like '%2'", nativeQuery = true)
    List<Order> findByNumberEndingWithIgnoreCase(String ending_with);



}
