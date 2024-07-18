package com.WorksIn.controller;

import com.WorksIn.dto.ItemSearchDto;
import com.WorksIn.dto.MainItemDto;
import com.WorksIn.service.ItemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final ItemService itemService;

    // 남성
    @GetMapping(value = "/category/man")
    public String man(ItemSearchDto itemSearchDto, Model model,
                       @RequestParam(defaultValue = "0") int pageNumber,
                       @RequestParam(defaultValue = "20") int pageSize,
                       @RequestParam(required = false) String searchQuery){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Slice<MainItemDto> items = itemService.getManItemPageInfinity(itemSearchDto, pageable);
        model.addAttribute("items",items);
        model.addAttribute("itemSearchDto",itemSearchDto);
        model.addAttribute("maxPage",5);
        return "category/man";
    }

    @RequestMapping(value="/infinity/man", method= RequestMethod.GET)
    @ResponseBody
    public String manInfinity(ItemSearchDto itemSearchDto,Model model,
                                @RequestParam(defaultValue = "0") int pageNumber,
                                @RequestParam(defaultValue = "20") int pageSize,
                                @RequestParam(required = false) String searchQuery) throws JsonProcessingException {
        itemSearchDto.setSearchQuery(searchQuery); // 검색어 설정

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Slice<MainItemDto> items = itemService.getManItemPageInfinity(itemSearchDto, pageable);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(items);

        return json;
    }

    //여성
    @GetMapping(value = "/category/woman")
    public String womanInfinity(ItemSearchDto itemSearchDto, Model model,
                       @RequestParam(defaultValue = "0") int pageNumber,
                       @RequestParam(defaultValue = "20") int pageSize,
                       @RequestParam(required = false) String searchQuery){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Slice<MainItemDto> items = itemService.getWomanItemPageInfinity(itemSearchDto, pageable);
        model.addAttribute("items",items);
        model.addAttribute("itemSearchDto",itemSearchDto);
        model.addAttribute("maxPage",5);
        return "category/woman";
    }

    @RequestMapping(value="/infinity/woman", method= RequestMethod.GET)
    @ResponseBody
    public String infinity_test(ItemSearchDto itemSearchDto,Model model,
                                @RequestParam(defaultValue = "0") int pageNumber,
                                @RequestParam(defaultValue = "20") int pageSize,
                                @RequestParam(required = false) String searchQuery) throws JsonProcessingException {
        itemSearchDto.setSearchQuery(searchQuery); // 검색어 설정

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Slice<MainItemDto> items = itemService.getWomanItemPageInfinity(itemSearchDto, pageable);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(items);

        return json;
    }
    //생활
    @GetMapping(value = "/category/life")
    public String life(ItemSearchDto itemSearchDto, Model model,
                      @RequestParam(defaultValue = "0") int pageNumber,
                      @RequestParam(defaultValue = "20") int pageSize,
                      @RequestParam(required = false) String searchQuery){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Slice<MainItemDto> items = itemService.getLifeItemPageInfinity(itemSearchDto, pageable);
        model.addAttribute("items",items);
        model.addAttribute("itemSearchDto",itemSearchDto);
        model.addAttribute("maxPage",5);
        return "category/life";
    }

    @RequestMapping(value="/infinity/life", method= RequestMethod.GET)
    @ResponseBody
    public String lifeInfinity(ItemSearchDto itemSearchDto,Model model,
                              @RequestParam(defaultValue = "0") int pageNumber,
                              @RequestParam(defaultValue = "20") int pageSize,
                              @RequestParam(required = false) String searchQuery) throws JsonProcessingException {
        itemSearchDto.setSearchQuery(searchQuery); // 검색어 설정

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Slice<MainItemDto> items = itemService.getLifeItemPageInfinity(itemSearchDto, pageable);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(items);

        return json;
    }
    //스케이트
    @GetMapping(value = "/category/skate")
    public String skate(ItemSearchDto itemSearchDto, Model model,
                      @RequestParam(defaultValue = "0") int pageNumber,
                      @RequestParam(defaultValue = "20") int pageSize,
                      @RequestParam(required = false) String searchQuery){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Slice<MainItemDto> items = itemService.getSkateItemPageInfinity(itemSearchDto, pageable);
        model.addAttribute("items",items);
        model.addAttribute("itemSearchDto",itemSearchDto);
        model.addAttribute("maxPage",5);
        return "category/skate";
    }

    @RequestMapping(value="/infinity/skate", method= RequestMethod.GET)
    @ResponseBody
    public String skateInfinity(ItemSearchDto itemSearchDto,Model model,
                              @RequestParam(defaultValue = "0") int pageNumber,
                              @RequestParam(defaultValue = "20") int pageSize,
                              @RequestParam(required = false) String searchQuery) throws JsonProcessingException {
        itemSearchDto.setSearchQuery(searchQuery); // 검색어 설정

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Slice<MainItemDto> items = itemService.getSkateItemPageInfinity(itemSearchDto, pageable);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(items);

        return json;
    }
}
