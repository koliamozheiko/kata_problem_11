package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String userList(Model model, Authentication authentication) {
        if (userService.findByUsername(authentication.getName()) != null) {
            if (!userService.findByUsername(authentication.getName()).getRoles().toString().contains("ROLE_ADMIN")) {
                return "redirect:user?id=" + userService.findByUsername(authentication.getName()).getId();
            }
        }
        model.addAttribute("user", userService.findByUsername(authentication.getName()));
        model.addAttribute("allUsers", userService.allUsers());
        return "admin";
    }

    @PostMapping("/remove")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

    @GetMapping("/userAdmin")
    public String getUserAdminPage(@RequestParam("id") Long id, Model model) {
        model.addAttribute("userAdmin", userService.findUserById(id));
        return "userAdmin";
    }

    @PatchMapping("/edit")
    public String updateUser(@ModelAttribute("userForEdit") User user) {
       if (!userService.updateUser(user)) {
           return "admin";
       }
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/new")
    public String addNewUser(Model model) {
        model.addAttribute("newUser", new User());
        return "admin";
    }

    @PostMapping("/new")
    public String createNewUser(@ModelAttribute("newUser") User user) {
        if(!userService.saveDefaultUser(user)) {
            return "redirect:/admin";
        }
        return "redirect:/admin";
    }
}
