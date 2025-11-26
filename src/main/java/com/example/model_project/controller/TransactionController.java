package com.example.model_project.controller;

import com.example.model_project.dto.TransactionDto;
import com.example.model_project.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new TransactionDto());
        return "add";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("user") TransactionDto dto, Model model) {

        try {
            transactionService.save(dto);
            return "redirect:/allUsers";
        }
        catch (IllegalStateException | IllegalArgumentException ex) {

            model.addAttribute("error", ex.getMessage());
            model.addAttribute("user", dto);

            return "failedPage";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable Long id, Model model) {
        model.addAttribute("user", transactionService.findById(id));
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
    public String deleteById(@PathVariable Long id, RedirectAttributes ra) {
        transactionService.deleteById(id);
        ra.addFlashAttribute("message", "Deleted successfully!");
        return "redirect:/allUsers";
    }

  
    @PostMapping("/delete/search")
    public String searchByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        model.addAttribute("transactions", transactionService.findByDate(date));
        model.addAttribute("selectedDate", date);

        return "deleteByDate";
    }

 
@PostMapping("/delete/selected")
public String deleteSelected(
        @RequestParam(required = false) List<Long> ids,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        RedirectAttributes ra) {

    if (date == null) {
        ra.addFlashAttribute("error", "No date selected for delete!");
        return "redirect:/delete";
    }

    if (ids == null || ids.isEmpty()) {
        ra.addFlashAttribute("error", "No transactions selected to delete!");
        return "redirect:/delete";
    }

    int deletedCount = transactionService.deleteMultipleByDate(ids, date);
    ra.addFlashAttribute("message", deletedCount + " transaction(s) deleted!");

    return "redirect:/delete";
}



    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        boolean filtersApplied =
                (type != null && !type.isBlank()) ||
                        (location != null && !location.isBlank()) ||
                        date != null;

        List<TransactionDto> users = filtersApplied
                ? transactionService.search(type, location, date)
                : List.of();

        model.addAttribute("filtersApplied", filtersApplied);
        model.addAttribute("users", users);
        model.addAttribute("type", type);
        model.addAttribute("location", location);
        model.addAttribute("date", date);

        return "search";
    }
}
