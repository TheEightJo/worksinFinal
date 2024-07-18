package com.WorksIn.repository;

import com.WorksIn.constant.ItemCategoryStatus;
import com.WorksIn.constant.ItemSellStatus;
import com.WorksIn.dto.ItemSearchDto;
import com.WorksIn.dto.MainItemDto;
import com.WorksIn.entity.Item;
import com.WorksIn.entity.QItem;
import com.WorksIn.entity.QItemImg;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.*;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory; // 동적쿼리 사용하기 위해 JPAQueryFactory 변수 선언
    // 생성자
    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em); // JPAQueryFactory 실질적인 객체가 만들어진다
    }
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ?
                null : QItem.item.itemSellStatus.eq(searchSellStatus);
        //ItemSellStatus null 이면 null 리턴 null 아니면 SELL, SOLD 둘중 하나 리턴
    }
    private BooleanExpression regDtsAfter(String searchDateType){ // all, id, 1w, im , 6m
        LocalDateTime dateTime = LocalDateTime.now(); // 현재시간을 추출해서 변수에 대입

        if (StringUtils.equals("all",searchDateType) || searchDateType == null){
            return null;
        }
        else if (StringUtils.equals("1d",searchDateType)){
            dateTime = dateTime.minusDays(1);
        }
        else if (StringUtils.equals("1w",searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        }
        else if (StringUtils.equals("1m",searchDateType)){
            dateTime = dateTime.minusMonths(1);
        }
        else if (StringUtils.equals("6m",searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
        // dateTime을 시간에 맞게 세팅 후 시간에 맞는 등록된 상품이 조회하도록 조건값 반환
    }
    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if (StringUtils.equals("itemNm",searchBy)){ // 상품명
            return QItem.item.itemNm.like("%"+searchQuery+"%");
        }
        else if (StringUtils.equals("createdBy",searchBy)){ // 작성자
            return QItem.item.createdBy.like("%"+searchQuery+"%");
        }
        return null;
    }


    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        QueryResults<Item> results = queryFactory.selectFrom(QItem.item).
                where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content,pageable,total);
    }

    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%"+searchQuery+"%");
    }
    /*
    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        //QMainItemDto @QueryProjection을 허용하면 DTO로 바로 조회 가능
        QueryResults<MainItemDto> results = queryFactory.select(new QMainItemDto(item.id, item.itemNm,
                        item.itemDetail,item.itemSellStatus,itemImg.imgUrl,item.price))
                // Join 내부조인 .repImgYn.eq("Y") 대표이미지만 가져온다.
                .from(itemImg).join(itemImg.item, item).where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();
        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable,total);
    }

     */




    //무한 스크롤링


    @Override
    public Slice<MainItemDto> getMainItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> results = queryFactory
                .select(Projections.fields(MainItemDto.class,
                        item.id,
                        item.itemNm,
                        item.itemDetail,
                        item.itemSellStatus,
                        itemImg.imgUrl,
                        item.price,
                        item.itemCategoryStatus))
                .from(itemImg).join(itemImg.item, item).where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize() + 1) // limit보다 데이터를 1개 더 들고와서, 해당 데이터가 있다면 hasNext 변수에 true를 넣어 알림
                .fetch();

        List<MainItemDto> content = new ArrayList<>();
        for (MainItemDto mainItemDto: results) {
            content.add(new MainItemDto(mainItemDto.getId(), mainItemDto.getItemNm(), mainItemDto.getItemDetail(),
                                        mainItemDto.getItemSellStatus(), mainItemDto.getImgUrl(),
                                        mainItemDto.getPrice(), mainItemDto.getItemCategoryStatus()));
        }

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }


    // 카테고리 나누기

    //남성
    @Override
    public Slice<MainItemDto> getManItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> results = queryFactory
                .select(Projections.fields(MainItemDto.class,
                        item.id,
                        item.itemNm,
                        item.itemDetail,
                        item.itemSellStatus,
                        itemImg.imgUrl,
                        item.price,
                        item.itemCategoryStatus))
                .from(itemImg).join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y")
                        .and(item.itemCategoryStatus.eq(ItemCategoryStatus.MAN))
                        .and(itemNmLike(itemSearchDto.getSearchQuery())))
                .orderBy(item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize() + 1) // limit보다 데이터를 1개 더 들고와서, 해당 데이터가 있다면 hasNext 변수에 true를 넣어 알림
                .fetch();

        List<MainItemDto> content = new ArrayList<>();
        for (MainItemDto mainItemDto: results) {
            content.add(new MainItemDto(mainItemDto.getId(), mainItemDto.getItemNm(), mainItemDto.getItemDetail(),
                                        mainItemDto.getItemSellStatus(), mainItemDto.getImgUrl(),
                                        mainItemDto.getPrice(), mainItemDto.getItemCategoryStatus()));
        }

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    //여성
    @Override
    public Slice<MainItemDto> getWomanItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> results = queryFactory
                .select(Projections.fields(MainItemDto.class,
                        item.id,
                        item.itemNm,
                        item.itemDetail,
                        item.itemSellStatus,
                        itemImg.imgUrl,
                        item.price,
                        item.itemCategoryStatus))
                .from(itemImg).join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y")
                        .and(item.itemCategoryStatus.eq(ItemCategoryStatus.WOMAN))
                        .and(itemNmLike(itemSearchDto.getSearchQuery())))
                .orderBy(item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize() + 1) // limit보다 데이터를 1개 더 들고와서, 해당 데이터가 있다면 hasNext 변수에 true를 넣어 알림
                .fetch();

        List<MainItemDto> content = new ArrayList<>();
        for (MainItemDto mainItemDto: results) {
            content.add(new MainItemDto(mainItemDto.getId(), mainItemDto.getItemNm(), mainItemDto.getItemDetail(),
                    mainItemDto.getItemSellStatus(), mainItemDto.getImgUrl(),
                    mainItemDto.getPrice(), mainItemDto.getItemCategoryStatus()));
        }

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    // 생활
    @Override
    public Slice<MainItemDto> getLifeItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> results = queryFactory
                .select(Projections.fields(MainItemDto.class,
                        item.id,
                        item.itemNm,
                        item.itemDetail,
                        item.itemSellStatus,
                        itemImg.imgUrl,
                        item.price,
                        item.itemCategoryStatus))
                .from(itemImg).join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y")
                        .and(item.itemCategoryStatus.eq(ItemCategoryStatus.LIFE))
                        .and(itemNmLike(itemSearchDto.getSearchQuery())))
                .orderBy(item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize() + 1) // limit보다 데이터를 1개 더 들고와서, 해당 데이터가 있다면 hasNext 변수에 true를 넣어 알림
                .fetch();

        List<MainItemDto> content = new ArrayList<>();
        for (MainItemDto mainItemDto: results) {
            content.add(new MainItemDto(mainItemDto.getId(), mainItemDto.getItemNm(), mainItemDto.getItemDetail(),
                    mainItemDto.getItemSellStatus(), mainItemDto.getImgUrl(),
                    mainItemDto.getPrice(), mainItemDto.getItemCategoryStatus()));
        }

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    // 스케이트
    @Override
    public Slice<MainItemDto> getSkateItemPageInfinity(ItemSearchDto itemSearchDto, Pageable pageable){
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> results = queryFactory
                .select(Projections.fields(MainItemDto.class,
                        item.id,
                        item.itemNm,
                        item.itemDetail,
                        item.itemSellStatus,
                        itemImg.imgUrl,
                        item.price,
                        item.itemCategoryStatus))
                .from(itemImg).join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y")
                        .and(item.itemCategoryStatus.eq(ItemCategoryStatus.SKATE))
                        .and(itemNmLike(itemSearchDto.getSearchQuery())))
                .orderBy(item.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize() + 1) // limit보다 데이터를 1개 더 들고와서, 해당 데이터가 있다면 hasNext 변수에 true를 넣어 알림
                .fetch();

        List<MainItemDto> content = new ArrayList<>();
        for (MainItemDto mainItemDto: results) {
            content.add(new MainItemDto(mainItemDto.getId(), mainItemDto.getItemNm(), mainItemDto.getItemDetail(),
                    mainItemDto.getItemSellStatus(), mainItemDto.getImgUrl(),
                    mainItemDto.getPrice(), mainItemDto.getItemCategoryStatus()));
        }

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
