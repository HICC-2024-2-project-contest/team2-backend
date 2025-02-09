package com.hiccproject.moaram.service.relation;

import com.hiccproject.moaram.entity.Item.Item;
import com.hiccproject.moaram.entity.composite.ItemExhibitionId;
import com.hiccproject.moaram.entity.exhibition.Exhibition;
import com.hiccproject.moaram.entity.relation.ItemExhibition;
import com.hiccproject.moaram.repository.ItemExhibitionRepository;
import com.hiccproject.moaram.service.ExhibitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ItemExhibitionService {

    @Autowired
    private ItemExhibitionRepository itemExhibitionRepository;

    @Autowired
    private ExhibitionService exhibitionService;

    // ItemExhibition을 생성하고 저장하는 메서드
    public Exhibition createAndSaveItemExhibition(Item item, Long exhibitionId) {
        // exhibitionId로 Exhibition을 찾기 (조건을 만족하는지 확인)
        Exhibition exhibition = exhibitionService.getExhibition(exhibitionId);  // getExhibition 사용

        // ItemExhibitionId 생성
        ItemExhibitionId id = new ItemExhibitionId();
        id.setItemId(item.getId());
        id.setExhibitionId(exhibition.getId());

        // ItemExhibition 객체 생성
        ItemExhibition itemExhibition = new ItemExhibition();
        itemExhibition.setId(id);
        itemExhibition.setItem(item);
        itemExhibition.setExhibition(exhibition);
        itemExhibition.setCreatedTime(LocalDateTime.now());

        // ItemExhibition 저장
        itemExhibitionRepository.save(itemExhibition);

        // Exhibition 객체 반환
        return exhibition;  // Exhibition 반환
    }
}