package com.WorksIn.controller;

import com.WorksIn.constant.ItemSellStatus;
import com.WorksIn.dto.ItemDto;
import com.WorksIn.entity.Item;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 요거 필요없는 페이지임.
@Controller
@RequestMapping(value = "/thymeleaf")
public class ThymeleafExController {
    // localhost/thymeleaf/ex01 -> thymeleafEx01.html
    // ${data} -> Hello World
    @RequestMapping("/ex01")
    public  String thymeleafEx01(Model model){
        model.addAttribute("data","Hello World");
        return "/thymeleafEx/thymeleafEX01";
    }

    @GetMapping("/ex02")
    public String thymeleafEx02(Model model) {
        ItemDto itemDto = new ItemDto();
        itemDto.setItemDetail("상품 상세 설명");
        itemDto.setItemNm("테스트 상품1");
        itemDto.setPrice(10000);
        itemDto.setRegTime(LocalDateTime.now());
        model.addAttribute("itemDto", itemDto);
        return "/thymeleafEx/thymeleafEX02";
    }
    @GetMapping("/ex03")
    public String thymeleafEx03(Model model) {
        List<ItemDto> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ItemDto itemDto = new ItemDto();
            itemDto.setItemNm("테스트 상품"+i);
            itemDto.setItemDetail("상품 상세 설명");
            itemDto.setPrice(10000*i);
            itemDto.setRegTime(LocalDateTime.now());
            list.add(itemDto);
        }
        model.addAttribute("itemDtoList", list);

        return "/thymeleafEx/thymeleafEX03";
    }
@GetMapping("/ex04")
    public String thymeleafEx04(Model model) {
        List<ItemDto> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ItemDto itemDto = new ItemDto();
            itemDto.setItemNm("테스트 상품"+i);
            itemDto.setItemDetail("상품 상세 설명");
            itemDto.setPrice(10000*i);
            itemDto.setRegTime(LocalDateTime.now());
            list.add(itemDto);
        }
        model.addAttribute("itemDtoList", list);

        return "/thymeleafEx/thymeleafEx04";
    }

    @GetMapping(value = "/ex05")
    public String thymeleafExample05(Model model) {
        return "thymeleafEx/thymeleafEx05";
    }
    @GetMapping(value = "/ex06")
    public String thymeleafExample06(@RequestParam("param1") String param1,
            @RequestParam("param2") String param2,
            Model model) {
        model.addAttribute("param1", param1);
        model.addAttribute("param2", param2);
        return "thymeleafEx/thymeleafEx06";
    }

    @RequestMapping("/ex07")
    public String thymeleafExample07() {
        return "thymeleafEx/ThymeleafEx07";
    }

}
