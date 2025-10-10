package com.example.model_project.controller;

import com.example.model_project.dto.TransactionDto;
import com.example.model_project.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller

public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/allUsers")
    public String viewAll(Model model) {
        List<TransactionDto> list = transactionService.getAll();
        double availableQuantity = transactionService.getAvailableQuantity();


        model.addAttribute("users", list);
        model.addAttribute("availableQuantity", availableQuantity);

        return "view";


    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new TransactionDto());
        return "view";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("user") TransactionDto dto) {
        transactionService.save(dto);
        return "redirect:/allUsers";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new TransactionDto());
        return "add";
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable Long id, Model model) {
        TransactionDto dto = transactionService.findById(id);
        model.addAttribute("user", dto);
        return "edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("user") TransactionDto dto) {
        transactionService.update(id, dto);
        return "redirect:/allUsers";
    }

    @GetMapping("/delete")
    public String showDeletePage() {

        return "delete";
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        transactionService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Transaction deleted successfully!");
        return "redirect:/allUsers";
    }

    @PostMapping("/delete/specific")
    public String deleteSpecific(@RequestParam("id") Long id,
                                 @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 RedirectAttributes redirectAttributes) {
        boolean deleted = transactionService.deleteByIdAndDate(id, date);
        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "Transaction deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Transaction not found or date mismatch!");
        }
        return "redirect:/allUsers";
    }

    @PostMapping("/delete/search")
    public String searchByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               Model model) {
        List<TransactionDto> transactions = transactionService.findByDate(date);
        model.addAttribute("transactions", transactions);
        model.addAttribute("selectedDate", date);
        return "deleteByDate";
    }
    @GetMapping("/search")
    public String searchByType(@RequestParam(name = "type", required = false) String type, Model model) {
        List<TransactionDto> users = transactionService.findByType(type);
        double availableQuantity = transactionService.getAvailableQuantity(); // add this

        model.addAttribute("users", users);
        model.addAttribute("type", type);
        model.addAttribute("availableQuantity",availableQuantity);
        return "search";
    }
}
