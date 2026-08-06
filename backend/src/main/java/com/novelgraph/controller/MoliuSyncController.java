package com.novelgraph.controller;

import com.novelgraph.common.Result;
import com.novelgraph.dto.moliu.MoliuChapterSyncDTO;
import com.novelgraph.dto.moliu.MoliuCharacterSyncDTO;
import com.novelgraph.dto.moliu.MoliuForeshadowSyncDTO;
import com.novelgraph.dto.moliu.MoliuNarratorSyncDTO;
import com.novelgraph.dto.moliu.MoliuWorldSyncDTO;
import com.novelgraph.service.MoliuSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 墨流数据同步控制器
 * 路径前缀：/novel/{novelId}/sync
 * 所有接口均需登录（Sa-Token 校验），且必须是该小说的拥有者。
 *
 * @author novelgraph
 */
@Slf4j
@RestController
@RequestMapping("/novel/{novelId}/sync")
@RequiredArgsConstructor
public class MoliuSyncController {

    private final MoliuSyncService moliuSyncService;

    /**
     * 同步角色（按 name upsert）
     * POST /novel/{novelId}/sync/character
     */
    @PostMapping("/character")
    public Result<Map<String, Object>> syncCharacter(@PathVariable String novelId,
                                                     @Valid @RequestBody MoliuCharacterSyncDTO dto) {
        return Result.success(moliuSyncService.syncCharacter(novelId, dto));
    }

    /**
     * 同步世界观（1:1 覆盖）
     * POST /novel/{novelId}/sync/world
     */
    @PostMapping("/world")
    public Result<Map<String, Object>> syncWorld(@PathVariable String novelId,
                                                 @Valid @RequestBody MoliuWorldSyncDTO dto) {
        return Result.success(moliuSyncService.syncWorld(novelId, dto));
    }

    /**
     * 同步叙述者（1:1 覆盖）
     * POST /novel/{novelId}/sync/narrator
     */
    @PostMapping("/narrator")
    public Result<Map<String, Object>> syncNarrator(@PathVariable String novelId,
                                                     @Valid @RequestBody MoliuNarratorSyncDTO dto) {
        return Result.success(moliuSyncService.syncNarrator(novelId, dto));
    }

    /**
     * 同步章节（按 chapterNum upsert）
     * POST /novel/{novelId}/sync/chapter
     */
    @PostMapping("/chapter")
    public Result<Map<String, Object>> syncChapter(@PathVariable String novelId,
                                                   @Valid @RequestBody MoliuChapterSyncDTO dto) {
        return Result.success(moliuSyncService.syncChapter(novelId, dto));
    }

    /**
     * 同步伏笔（按 moliuId upsert）
     * POST /novel/{novelId}/sync/foreshadow
     */
    @PostMapping("/foreshadow")
    public Result<Map<String, Object>> syncForeshadow(@PathVariable String novelId,
                                                       @Valid @RequestBody MoliuForeshadowSyncDTO dto) {
        return Result.success(moliuSyncService.syncForeshadow(novelId, dto));
    }
}
