package com.novelgraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novelgraph.common.Result;
import com.novelgraph.entity.NovelWorldSetting;
import com.novelgraph.service.NovelWorldSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 世界观控制器（1:1 关联小说，来自墨流同步）
 * 路径前缀：/novel/{novelId}/world
 *
 * @author novelgraph
 */
@RestController
@RequestMapping("/novel/{novelId}/world")
@RequiredArgsConstructor
public class WorldSettingController {

    private final NovelWorldSettingService worldSettingService;

    /**
     * 查询小说世界观
     * GET /novel/{novelId}/world
     */
    @GetMapping
    public Result<NovelWorldSetting> detail(@PathVariable String novelId) {
        NovelWorldSetting ws = worldSettingService.getOne(
                new LambdaQueryWrapper<NovelWorldSetting>().eq(NovelWorldSetting::getNovelId, novelId)
        );
        return Result.success(ws);
    }
}
