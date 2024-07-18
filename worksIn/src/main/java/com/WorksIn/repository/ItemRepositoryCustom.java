package com.WorksIn.repository;

import com.WorksIn.dto.ItemSearchDto;
import com.WorksIn.dto.MainItemDto;
import com.WorksIn.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    //Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Slice<MainItemDto> getMainItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable);

    Slice<MainItemDto> getManItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable);

    Slice<MainItemDto> getWomanItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable);

    Slice<MainItemDto> getLifeItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable);

    Slice<MainItemDto> getSkateItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable);
}
