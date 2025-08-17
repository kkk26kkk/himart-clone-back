package com.himartclone.goods.controller;

import com.himartclone.common.file.FileStore;
import com.himartclone.common.file.UploadFile;
import com.himartclone.goods.controller.dto.GoodsBoCreateDto;
import com.himartclone.goods.controller.dto.GoodsBoDetailDto;
import com.himartclone.goods.domain.GoodsBase;
import com.himartclone.goods.service.GoodsBoService;
import com.himartclone.goods.service.GoodsService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsBoController {

    private final GoodsBoService service;

    private final FileStore fileStore;

    private static final Map<String, String> DELIVERY_TYPES = new LinkedHashMap<>();
    private final GoodsService goodsService;

    @PostConstruct
    public void init() {
        DELIVERY_TYPES.put("01", "하이마트배송");
        DELIVERY_TYPES.put("02", "업체배송");
    }

    @GetMapping("/list")
    public String list(Model model) {
        List<GoodsBoDetailDto> list = service.goodsList().stream()
                .map(GoodsBoDetailDto::goodsBaseToDto)
                .toList();
        model.addAttribute("list", list);
        return "/goods/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("goodsNo") String goodsNo, Model model) {
        GoodsBase goodsBase = service.goodsDetail(goodsNo);
        GoodsBoDetailDto dto = GoodsBoDetailDto.goodsBaseToDto(goodsBase);
        model.addAttribute("goods", dto);
        return "/goods/detail";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("dlvTypes", DELIVERY_TYPES);
        model.addAttribute("goods", new GoodsBoCreateDto());
        return "/goods/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("goods") GoodsBoCreateDto goods,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes) throws IOException {

        /* NOTE : 필드 오류 예시
        if (!StringUtils.hasText(goods.getGoodsNo())) {
            bindingResult.addError(new FieldError("goods", "goodsNo", goods.getGoodsNo(), false, new String[]{"NotBlank.goods.goodsNo"}, null, null));
            bindingResult.rejectValue("goodsNo", "NotBlank");
            ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "goodsNo", "NotBlank");
        }
        */

        // NOTE : 객체 오류 예시
        if (goods.getImageFile() == null) {
//            bindingResult.addError(new ObjectError("required.goods.image", new String[]{"required.goods.description"}, null, null));
            bindingResult.reject("required.goods.image", null, null);
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "/goods/create";
        }

        fileStore.storeFile(goods.getImageFile());

        // TODO : 상품 등록 서비스 로직
//        GoodsBase goodsBase = goods.transferToGoodsBase();
//        service.createGoods(goodsBase);

        redirectAttributes.addAttribute("goodsNo", goods.getGoodsNo());
        return "redirect:/goods/detail?goodsNo=${goodsNo}";
    }
}
