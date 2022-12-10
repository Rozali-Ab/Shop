package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.enumm.Status;
import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.repositories.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    //вернуть весь лист заказов
    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    //поиск по id заказа
    public Order getOrderById(int id){
        Optional<Order> optionalOrder = orderRepository.findById(id);
        return optionalOrder.orElse(null);
    }


    //изменить статус заказа
    @Transactional
    public void updateOrderStatus(Order order){
        orderRepository.save(order);
    }

    //поиск заказа по 4 последним символам в номере
    public List<Order> getOrderByNumberEndingWithIgnoreCase(String ending_with){
        List<Order> orderList = orderRepository.findByNumberEndingWithIgnoreCase(ending_with);
        return orderList;
    }

}
