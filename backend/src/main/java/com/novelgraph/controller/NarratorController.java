package com.novelgraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novelgraph.common.Result;
import com.novelgraph.entity.NovelNarrator;
import com.novelgraph.service.NovelNarratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 叙述者控制器（1:1 关联小说，来自墨流同步）
 * 路径前缀：/novel/{novelId}/narrator
 *
 * @author novelgraph
 */
@RestController
@RequestMapping("/novel/{novelId}/narrator")
@RequiredArgsConstructor
public class NarratorController {

    private final NovelNarratorService narratorService;

    /**
     * 查询小说叙述者
     * GET /novel/{novelId}/narrator
     */
    @GetMapping
    public Result<NovelNarrator> detail(@PathVariable String novelId) {
        NovelNarrator n = narratorService.getOne(
                new LambdaQueryWrapper<NovelNarrator>().eq(NovelNarrator::getNovelId, novelId)
        );
        return Result.success(n);
    }
}
