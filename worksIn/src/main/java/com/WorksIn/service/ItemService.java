package com.WorksIn.service;

import com.WorksIn.dto.ItemFormDto;
import com.WorksIn.dto.ItemImgDto;
import com.WorksIn.dto.ItemSearchDto;
import com.WorksIn.dto.MainItemDto;
import com.WorksIn.entity.Item;
import com.WorksIn.entity.ItemImg;
import com.WorksIn.repository.ItemImgRepository;
import com.WorksIn.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList)
            throws Exception{
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //상품등록
        Item item = itemFormDto.createItem();
        item.setCreatedBy(userDetails.getUsername());
        itemRepository.save(item);
        //이미지 등록
        for (int i =0; i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if (i==0)
                itemImg.setRepImgYn("Y");
            else
                itemImg.setRepImgYn("N");
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){
        //Entity
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        //DB에서 데이터를 가지고 옵니다.
        //DTO
        List<ItemImgDto> itemImgDtoList = new ArrayList<>(); // 왜 DTO 만들었나요?

        for (ItemImg itemImg : itemImgList){
            //Entity -> DTO
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        //Item -> ItemFormDto modelMapper
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList)
            throws Exception{
        //상품 변경
        Item item = itemRepository.findById(itemFormDto.getId()).
                orElseThrow(EntityNotFoundException::new);
        System.out.println(item.getItemDetail());
        item.updateItem(itemFormDto);
        System.out.println(item.getItemDetail());

        //상품 이미지 변경
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for (int i=0; i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true) // 쿼리문 실행 읽기만 한다.
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto,pageable);
    }

    /*
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

     */

    // 무한스크롤링
    @Transactional(readOnly = true)
    public Slice<MainItemDto> getMainItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPageInfinity(itemSearchDto, pageable);
    }

    // 카테고리 나누기
    // 남성
    @Transactional(readOnly = true)
    public Slice<MainItemDto> getManItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getManItemPageInfinity(itemSearchDto, pageable);
    }
    //여성
    @Transactional(readOnly = true)
    public Slice<MainItemDto> getWomanItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getWomanItemPageInfinity(itemSearchDto, pageable);
    }
    //생활
    @Transactional(readOnly = true)
    public Slice<MainItemDto> getLifeItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getLifeItemPageInfinity(itemSearchDto, pageable);
    }
    //스케이트
    @Transactional(readOnly = true)
    public Slice<MainItemDto> getSkateItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getSkateItemPageInfinity(itemSearchDto, pageable);
    }
}
