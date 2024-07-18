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
public class MainController {
    private final ItemService itemService;
    /*
    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,100);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
        System.out.println(items.getNumber()+"!!!!!!!!!!!!");
        System.out.println(items.getTotalPages()+"############");
        model.addAttribute("items",items);
        model.addAttribute("itemSearchDto",itemSearchDto);
        model.addAttribute("maxPage",5);
        return "main";
    }

     */

    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Model model,
                       @RequestParam(defaultValue = "0") int pageNumber,
                       @RequestParam(defaultValue = "20") int pageSize,
                       @RequestParam(required = false) String searchQuery){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Slice<MainItemDto> items = itemService.getMainItemPageInfinity(itemSearchDto, pageable);
        model.addAttribute("items",items);
        model.addAttribute("itemSearchDto",itemSearchDto);
        model.addAttribute("maxPage",5);
        return "main";
    }

    @RequestMapping(value="/infinity", method=RequestMethod.GET)
    @ResponseBody
    public String infinity_test(ItemSearchDto itemSearchDto,Model model,
                                @RequestParam(defaultValue = "0") int pageNumber,
                                @RequestParam(defaultValue = "20") int pageSize,
                                @RequestParam(required = false) String searchQuery) throws JsonProcessingException {
        itemSearchDto.setSearchQuery(searchQuery); // 검색어 설정

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Slice<MainItemDto> items = itemService.getMainItemPageInfinity(itemSearchDto, pageable);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(items);

        return json;
    }

}
