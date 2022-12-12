package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.enumm.Status;
import com.example.springsecurityapplication.models.Image;
import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.CategoryRepository;
import com.example.springsecurityapplication.security.PersonDetails;
import com.example.springsecurityapplication.services.OrderService;
import com.example.springsecurityapplication.services.PersonService;
import com.example.springsecurityapplication.services.ProductService;
import com.example.springsecurityapplication.util.ProductValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
//@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')") для всего контроллера
public class AdminController {
    @Value("${upload.path}")
    private String uploadPath;

    private final ProductValidator productValidator;

    private final ProductService productService;

    private final CategoryRepository categoryRepository;
    private final OrderService orderService;
    private final PersonService personService;


    public AdminController(ProductValidator productValidator, ProductService productService, CategoryRepository categoryRepository, OrderService orderService, PersonService personService) {
        this.productValidator = productValidator;
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.orderService = orderService;
        this.personService = personService;
    }

    //  @PreAuthorize("hasRole('ROLE_ADMIN')") для каждого метода

    //метод по отображению главной страницы админа с выводом товаров
    @GetMapping()
    public String admin(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        String role = personDetails.getPerson().getRole();
        if(role.equals("ROLE_USER")){
            return "redirect:/index";
        }

        model.addAttribute("products", productService.getAllProduct());
        return "admin/admin";
    }

    //метод по отображению формы добавления нового товара
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/product/add")
    public String addFormProduct(Model model){
        model.addAttribute("product", new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/addProduct";
    }

    //добавление нового товара по кнопке
    @PostMapping("/product/add")
    public String addProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult,
                             @RequestParam("file_one") MultipartFile file_one,
                             @RequestParam("file_two") MultipartFile file_two,
                             @RequestParam("file_three") MultipartFile file_three,
                             @RequestParam("file_four") MultipartFile file_four,
                             @RequestParam("file_five") MultipartFile file_five) throws IOException
        {
            productValidator.validate(product, bindingResult);
            if (bindingResult.hasErrors()){
                return "product/addProduct";
            }

            //проверка на пустоту файла, если пустой, то не сохраняем
            if(file_one != null){
                //объект по хранению пути сохранения (директория)
                File uploadDir = new File(uploadPath);
                //если данный путь не существует,
                if(!uploadDir.exists()){
                    //то его создаем
                    uploadDir.mkdir();
                }
                //создаем уникальное имя файла (универсальный уникальный идентификатор)
                String uuidFile = UUID.randomUUID().toString();
                String resultFileName = uuidFile + "." + file_one.getOriginalFilename();
                //загружаем файл по указанному пути
                file_one.transferTo(new File(uploadPath + "/" + resultFileName));
                Image image = new Image();
                image.setProduct(product);
                image.setFileName(resultFileName);
                product.addImageToProduct(image);}

            //проверка на пустоту второго файла, если пустой, то не сохраняем и так для остальных файлов
                    if (!file_two.isEmpty()) {
                        File uploadDir = new File(uploadPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdir();
                        }
                        String uuidFile = UUID.randomUUID().toString();
                        String resultFileName = uuidFile + "." + file_two.getOriginalFilename();
                        file_two.transferTo(new File(uploadPath + "/" + resultFileName));
                        Image image = new Image();
                        image.setProduct(product);
                        image.setFileName(resultFileName);
                        product.addImageToProduct(image);}

                    //третий
                    if (!file_three.isEmpty()) {
                        File uploadDir = new File(uploadPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdir();
                        }
                        String uuidFile = UUID.randomUUID().toString();
                        String resultFileName = uuidFile + "." + file_three.getOriginalFilename();
                        file_three.transferTo(new File(uploadPath + "/" + resultFileName));
                        Image image = new Image();
                        image.setProduct(product);
                        image.setFileName(resultFileName);
                        product.addImageToProduct(image);}

                    //четвертый
                    if (!file_four.isEmpty()) {
                        File uploadDir = new File(uploadPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdir();
                        }
                        String uuidFile = UUID.randomUUID().toString();
                        String resultFileName = uuidFile + "." + file_four.getOriginalFilename();
                        file_four.transferTo(new File(uploadPath + "/" + resultFileName));
                        Image image = new Image();
                        image.setProduct(product);
                        image.setFileName(resultFileName);
                        product.addImageToProduct(image);}

                    //пятый
                    if (!file_five.isEmpty()) {
                        File uploadDir = new File(uploadPath);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdir();
                        }
                        String uuidFile = UUID.randomUUID().toString();
                        String resultFileName = uuidFile + "." + file_five.getOriginalFilename();
                        file_five.transferTo(new File(uploadPath + "/" + resultFileName));
                        Image image = new Image();
                        image.setProduct(product);
                        image.setFileName(resultFileName);
                        product.addImageToProduct(image);}

        productService.saveProduct(product);
        return "redirect:/admin";
    }

    //удаление товара по id
    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id){
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    //метод по получению товара по id и передача его на форму редактирования
    @GetMapping("/product/edit/{id}")
    public String editFormProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("editProduct", productService.getProductId(id));
        model.addAttribute("category", categoryRepository.findAll());
        return "product/editProduct";
    }

    //редактирование товара
    @PostMapping("/product/edit/{id}")
    public String editProduct(@ModelAttribute("editProduct") Product product, @PathVariable("id") int id){
        productService.updateProduct(id, product);
        return "redirect:/admin";
    }


    //лист (вернее таблица с заказами)
    @GetMapping("/orders")
    public String getOrders(Model model){
        model.addAttribute("orders", orderService.getAllOrders());
        return "/admin/orders";
    }

    //поиск по последним СИМВОЛАМ заказа, не забыть сделать так, чтобы таблица не пропадала
    @PostMapping("/orders/search")
    public String getOrderByNumber(@RequestParam("value") String value, Model model){
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("search_order", orderService.getOrderByNumberEndingWithIgnoreCase(value));
        model.addAttribute("value", value);
        System.out.println(value);
        System.out.println(model.toString());
        return "/admin/orders";
    }

    //информация о конкретном товаре
    @GetMapping("/orders/{id}")
    public String getOrderInfo(@PathVariable("id") int id, Model model){
        model.addAttribute("info_order", orderService.getOrderById(id));
        return "/admin/infoOrder";
    }

    //изменить статус заказа в инфоордер
    @PostMapping("/orders/{id}")
    public String changeOrderStatus(@PathVariable("id") int id, @RequestParam("status") Status status){
        Order order_status = orderService.getOrderById(id);
        order_status.setStatus(status);
        orderService.updateOrderStatus(order_status);
        return "redirect:/admin/orders/{id}";
    }

    //изменение статуса заказа на странице с листом всех заказов
    @PostMapping("/orders/create/{id}")
    public String createOrder(@PathVariable("id") int id, @RequestParam("status") Status status){
        System.out.println(id);
        System.out.println(status);
        Order order = orderService.getOrderById(id);
        order.setStatus(status);
        orderService.updateOrderStatus(order);
        return "redirect:/admin/orders";
    }

    //вывести список юзеров для админа
    @GetMapping("/users")
    public String getUsers(Model model){
        model.addAttribute("users", personService.gefAllPersons());
        return "/admin/users";
    }

    //изменить роль пользователю на странице с листом всех пользователей
    @PostMapping ("/users/{id}")
    public String getPersonInfo(@PathVariable("id") int id, @RequestParam("role") String role){
        System.out.println(id);
        System.out.println(role);
        Person person_role = personService.getPersonById(id);
        person_role.setRole(role);
        personService.updateRole(person_role);
        return "redirect:/admin/users";
    }

}
