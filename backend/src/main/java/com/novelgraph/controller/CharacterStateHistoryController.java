package com.novelgraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novelgraph.common.Result;
import com.novelgraph.entity.NovelCharacterStateHistory;
import com.novelgraph.service.NovelCharacterStateHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色状态历史控制器（每章一快照）
 * 路径前缀：/novel/{novelId}/characters/{characterId}/history
 *
 * @author novelgraph
 */
@RestController
@RequestMapping("/novel/{novelId}/characters/{characterId}/history")
@RequiredArgsConstructor
public class CharacterStateHistoryController {

    private final NovelCharacterStateHistoryService historyService;

    /**
     * 角色状态轨迹（按章节范围查询）
     * GET /novel/{novelId}/characters/{characterId}/history?fromChapter=1&toChapter=20
     */
    @GetMapping
    public Result<List<NovelCharacterStateHistory>> list(@PathVariable String novelId,
                                                         @PathVariable String characterId,
                                                         @RequestParam(required = false) Integer fromChapter,
                                                         @RequestParam(required = false) Integer toChapter) {
        List<NovelCharacterStateHistory> list = historyService.list(
                new LambdaQueryWrapper<NovelCharacterStateHistory>()
                        .eq(NovelCharacterStateHistory::getNovelId, novelId)
                        .eq(NovelCharacterStateHistory::getCharacterId, characterId)
                        .ge(fromChapter != null, NovelCharacterStateHistory::getChapterNum, fromChapter)
                        .le(toChapter != null, NovelCharacterStateHistory::getChapterNum, toChapter)
                        .orderByAsc(NovelCharacterStateHistory::getChapterNum)
        );
        return Result.success(list);
    }
}
