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
        } catch (Exception ex) {
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

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, RedirectAttributes ra) {
        try {
            transactionService.deleteById(id);
            ra.addFlashAttribute("message", "Deleted successfully!");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/allUsers";
    }

    @GetMapping("/deleteRangePage")
    public String showDeleteRangePage(Model model) {
        model.addAttribute("transactions", null);
        return "deleteByRange";
    }

    @PostMapping("/searchByRange")
    public String searchTransactions(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model, RedirectAttributes redirectAttributes) {

        List<TransactionDto> list = transactionService.findByDateRange(startDate, endDate);

        if (list.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No records found between selected dates.");
            return "redirect:/deleteRangePage";
        }

        model.addAttribute("transactions", list);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "deleteByRange";
    }

    @PostMapping("/delete/selected")
    public String deleteSelected(@RequestParam(required = false) List<Long> ids,
                                 @RequestParam String startDate,
                                 @RequestParam String endDate,
                                 RedirectAttributes redirectAttributes) {

        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No transactions selected for deletion!");
            redirectAttributes.addFlashAttribute("startDate", startDate);
            redirectAttributes.addFlashAttribute("endDate", endDate);
            return "redirect:/searchByRange";
        }

        int deletedCount = transactionService.deleteMultiple(ids);
        redirectAttributes.addFlashAttribute("message", deletedCount + " transactions deleted successfully!");
        redirectAttributes.addFlashAttribute("startDate", startDate);
        redirectAttributes.addFlashAttribute("endDate", endDate);

        return "redirect:/searchByRange";
    }


    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        boolean filtersApplied = (type != null && !type.isBlank()) ||
                (location != null && !location.isBlank()) || date != null;

        List<TransactionDto> users = filtersApplied ?
                transactionService.search(type, location, date) : List.of();

        model.addAttribute("filtersApplied", filtersApplied);
        model.addAttribute("users", users);
        model.addAttribute("type", type);
        model.addAttribute("location", location);
        model.addAttribute("date", date);

        return "search";
    }

    @GetMapping("/delete")
    public String showDeletePage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        if (date != null) {
            List<TransactionDto> transactions = transactionService.findByDate(date);
            model.addAttribute("transactions", transactions);
            model.addAttribute("selectedDate", date);
        } else {
            model.addAttribute("transactions", null);
        }
        return "delete";
    }


    @PostMapping("/delete/search")
    public String searchByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        List<TransactionDto> transactions = transactionService.findByDate(date);

        model.addAttribute("transactions", transactions);
        model.addAttribute("selectedDate", date);

        return "delete";
    }

    @GetMapping("/searchByRange")
    public String loadSearchPage(Model model,
                                 @ModelAttribute("startDate") String startDate,
                                 @ModelAttribute("endDate") String endDate) {

        if (startDate != null && !startDate.isBlank() && endDate != null && !endDate.isBlank()) {
            LocalDate sDate = LocalDate.parse(startDate);
            LocalDate eDate = LocalDate.parse(endDate);

            List<TransactionDto> list = transactionService.findByDateRange(sDate, eDate);
            model.addAttribute("transactions", list);
        } else {
            model.addAttribute("transactions", null);
        }

        return "deleteByRange";
    }



}
