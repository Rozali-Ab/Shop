package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.enumm.Status;
import com.example.springsecurityapplication.models.Cart;
import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.CartRepository;
import com.example.springsecurityapplication.repositories.OrderRepository;
import com.example.springsecurityapplication.repositories.ProductRepository;
import com.example.springsecurityapplication.security.PersonDetails;
import com.example.springsecurityapplication.services.PersonService;
import com.example.springsecurityapplication.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    private final OrderRepository orderRepository;

    private final PersonService personService;

    @Autowired
    public UserController(ProductService productService, ProductRepository productRepository, CartRepository cartRepository, OrderRepository orderRepository, PersonService personService) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.personService = personService;
    }


    @GetMapping("/index")
    public String index(Model model){
        //получаем объект аутентификации -> с помощью SecurityContextHolder обращаемся к контексту и на нем вызываем метод аутентификации.
        // По сути из потока для текущего пользователя мы получаем объект, который был положен в сессию после аутентификации пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //преобразовываем объект аутентификации в специальный объект класса по работе с пользователями
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id = personDetails.getPerson().getId();
        System.out.println(personDetails.getPerson().getId());
        System.out.println(personDetails.getPerson().getLogin());
        System.out.println(personDetails.getPerson().getPassword());

        String role = personDetails.getPerson().getRole();

        if(role.equals("ROLE_ADMIN")){
            return "redirect:/admin";
        }
        model.addAttribute("username", personService.getPersonById(id).getLogin());
        model.addAttribute("products", productService.getAllProduct());
         return "user/index";
    }

    @GetMapping("/user/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model){

        model.addAttribute("product", productService.getProductId(id));
        return "user/infoProduct";
    }

    //Добавить товар в корзину
    @GetMapping("/cart/add/{id}")
    public String addProductInCart(@PathVariable("id") int id, Model model){
        Product product = productService.getProductId(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        int id_person = personDetails.getPerson().getId();
        Cart cart = new Cart(id_person, product.getId());
        cartRepository.save(cart);
        return "redirect:/cart";
    }

    //корзина
    @GetMapping("/cart")
    public String cart(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();
        for (Cart cart: cartList) {
            productList.add(productService.getProductId(cart.getProductId()));
        }
        float price = 0;
        for (Product product : productList){
            price += product.getPrice();
        }
        model.addAttribute("price", price);
        model.addAttribute("cart_product", productList);
        return "user/cart";
    }

    //удаление товара из корзины
    @GetMapping("/cart/delete/{id}")
    public String deleteProductFromCart(Model model, @PathVariable("id") int id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        cartRepository.deleteCartById(id, id_person);
        return "redirect:/cart";
    }

    //оформить заказ
    @GetMapping("/order/create")
    public String createOrder(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int id_person = personDetails.getPerson().getId();
        List<Cart> cartList = cartRepository.findByPersonId(id_person);
        List<Product> productList = new ArrayList<>();
        for (Cart cart: cartList) {
            productList.add(productService.getProductId(cart.getProductId()));
        }

        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0,8);



        for(Product product: productList){
          Order newOrder = new Order(uuid, 1, product.getPrice(), Status.Принято, product, personDetails.getPerson());
          orderRepository.save(newOrder);
          cartRepository.deleteCartById(product.getId(), id_person);
        }
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String ordersUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        List<Order> orderList = orderRepository.findByPerson(personDetails.getPerson());
        model.addAttribute("orders", orderList);
        return "/user/orders";
    }

    @PostMapping("/user/search")
    public String productSearch
            (
                    @RequestParam("search") String search,
                    @RequestParam("ot") String ot,
                    @RequestParam("Do") String Do,
                    @RequestParam(value = "price", required = false, defaultValue = "") String price,
                    @RequestParam(value = "category", required = false, defaultValue = "") String category, Model model
            )
    {
        System.out.println(search);
        System.out.println(ot);
        System.out.println(Do);
        System.out.println(price);
        System.out.println(category);

        // Если диапазон цен от и до не пустой
        if(!ot.isEmpty() & !Do.isEmpty()) {
            // Если сортировка по цене выбрана
            if (!price.isEmpty()) {
                // Если в качестве сортировки выбрана сортировкам по возрастанию
                if (price.equals("sorted_by_ascending_price")) {
                    // Если категория товара не пустая
                    if (!category.isEmpty()) {
                        // Если категория мебели
                        if (category.equals("furniture")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));

                        } // Если категория бытовой техники
                        else if (category.equals("appliances")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));

                        } // Если категория одежды
                        else if (category.equals("clothes")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        }
                    } // Если категория не выбрана
                    else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPrice(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }
                } // Если в качестве сортировки выбрана сортировкам по убыванию
                else if (price.equals("sorted_by_descending_price")) {
                    // Если категория не пустая
                    if (!category.isEmpty()) {
                        // Если категория равная мебели
                        if (category.equals("furniture")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                            // Если категория равная бытовой техники
                        } else if (category.equals("appliances")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                            // Если категория равная одежде
                        } else if (category.equals("clothes")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        }

                    }// Если категория не выбрана
                    else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }
                }
            }
            else {
                model.addAttribute("search_product", productRepository.findByTitleAndPriceGreaterThanEqualAndPriceLessThan(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
            }
        } //поиск выбрана только категория
        else if (!category.isEmpty()) {
            // Если категория лосины
            if (category.equals("furniture")) {
                model.addAttribute("search_product", productService.getProductByCategory(1));
                // Если категория комбинезоны
            } else if (category.equals("appliances")) {
                model.addAttribute("search_product", productService.getProductByCategory(2));
                // Если категория равная маечки и топы
            } else if (category.equals("clothes")) {
                model.addAttribute("search_product", productService.getProductByCategory(3));
            }

        }
        else {
            model.addAttribute("search_product",productRepository.findByTitleContainingIgnoreCase(search));
        }
        model.addAttribute("value_search", search);
        model.addAttribute("value_price_ot", ot);
        model.addAttribute("value_price_Do", Do);
        model.addAttribute("products", productService.getAllProduct());
        return "/user/index";
    }


//    @GetMapping("/changepassword")
//    public String changePassword(Model model){
//        model.addAttribute("editPerson");
//        return "/user/changepassword";
//    }
//
//    @PostMapping("/changepassword")
//    public String resultChangePassword(@ModelAttribute("editPerson") Person person)
//    {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
//        personService.updatePassword(person);


//        personValidator.passwordValidate(person, bindingResult);
//        if (bindingResult.hasErrors()){
//            return " ";
//        }
//        personService.updatePassword(person_changepassword, newPassword);
//
//       return "user/index";
//    }




}
