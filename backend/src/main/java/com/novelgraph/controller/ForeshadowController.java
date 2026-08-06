package com.novelgraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novelgraph.common.Result;
import com.novelgraph.entity.NovelForeshadow;
import com.novelgraph.service.NovelForeshadowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 伏笔控制器（来自墨流同步）
 * 路径前缀：/novel/{novelId}/foreshadows
 *
 * @author novelgraph
 */
@RestController
@RequestMapping("/novel/{novelId}/foreshadows")
@RequiredArgsConstructor
public class ForeshadowController {

    private final NovelForeshadowService foreshadowService;

    /**
     * 伏笔列表（可按状态过滤）
     * GET /novel/{novelId}/foreshadows?status=planted
     */
    @GetMapping
    public Result<List<NovelForeshadow>> list(@PathVariable String novelId,
                                                @RequestParam(required = false) String status) {
        List<NovelForeshadow> list = foreshadowService.list(
                new LambdaQueryWrapper<NovelForeshadow>()
                        .eq(NovelForeshadow::getNovelId, novelId)
                        .eq(status != null, NovelForeshadow::getStatus, status)
                        .orderByAsc(NovelForeshadow::getPlantedChapter)
        );
        return Result.success(list);
    }

    /**
     * 伏笔详情
     * GET /novel/{novelId}/foreshadows/{id}
     */
    @GetMapping("/{id}")
    public Result<NovelForeshadow> detail(@PathVariable String novelId,
                                          @PathVariable String id) {
        NovelForeshadow f = foreshadowService.getOne(
                new LambdaQueryWrapper<NovelForeshadow>()
                        .eq(NovelForeshadow::getId, id)
                        .eq(NovelForeshadow::getNovelId, novelId)
        );
        return Result.success(f);
    }
}
