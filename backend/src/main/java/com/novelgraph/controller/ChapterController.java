package com.novelgraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novelgraph.common.BusinessException;
import com.novelgraph.common.Result;
import com.novelgraph.common.ResultCode;
import com.novelgraph.entity.NovelChapter;
import com.novelgraph.service.NovelChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 章节控制器（来自墨流同步）
 * 路径前缀：/novel/{novelId}/chapters
 *
 * @author novelgraph
 */
@RestController
@RequestMapping("/novel/{novelId}/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final NovelChapterService chapterService;

    /**
     * 章节列表（分页，仅元数据，不含正文）
     * GET /novel/{novelId}/chapters?page=1&size=20
     */
    @GetMapping
    public Result<IPage<NovelChapter>> list(@PathVariable String novelId,
                                            @RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "20") Integer size) {
        Page<NovelChapter> p = new Page<>(page, size);
        // 列表只查元数据，不返回 content 大字段
        IPage<NovelChapter> result = chapterService.page(p,
                new LambdaQueryWrapper<NovelChapter>()
                        .eq(NovelChapter::getNovelId, novelId)
                        .orderByAsc(NovelChapter::getChapterNum)
                        .select(NovelChapter::getId, NovelChapter::getNovelId,
                                NovelChapter::getChapterNum, NovelChapter::getTitle,
                                NovelChapter::getWordCount, NovelChapter::getTokensUsed,
                                NovelChapter::getModelUsed, NovelChapter::getEmotion,
                                NovelChapter::getSummary, NovelChapter::getKeyCharacters,
                                NovelChapter::getKeyEvents, NovelChapter::getChapterType,
                                NovelChapter::getTensionScore, NovelChapter::getMoliuSyncedAt,
                                NovelChapter::getCreatedAt, NovelChapter::getUpdatedAt)
        );
        return Result.success(result);
    }

    /**
     * 章节详情（含正文）
     * GET /novel/{novelId}/chapters/{chapterNum}
     */
    @GetMapping("/{chapterNum}")
    public Result<NovelChapter> detail(@PathVariable String novelId,
                                        @PathVariable Integer chapterNum) {
        NovelChapter ch = chapterService.getOne(
                new LambdaQueryWrapper<NovelChapter>()
                        .eq(NovelChapter::getNovelId, novelId)
                        .eq(NovelChapter::getChapterNum, chapterNum)
        );
        if (ch == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "章节不存在");
        }
        return Result.success(ch);
    }
}
