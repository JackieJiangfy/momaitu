package com.novelgraph.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.novelgraph.common.Result;
import com.novelgraph.dto.NovelQueryDTO;
import com.novelgraph.dto.NovelSaveDTO;
import com.novelgraph.dto.NovelVO;
import com.novelgraph.service.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 小说控制器
 * 路径前缀：/novels
 * 所有接口均需登录（Sa-Token 拦截器统一校验）
 *
 * @author novelgraph
 */
@RestController
@RequestMapping("/novels")
@RequiredArgsConstructor
public class NovelController {

    private final NovelService novelService;

    /**
     * 我的小说列表（支持搜索/状态过滤/分页）
     * GET /novels
     */
    @GetMapping
    public Result<IPage<NovelVO>> list(NovelQueryDTO query) {
        return Result.success(novelService.page(query));
    }

    /**
     * 创建小说
     * POST /novels
     */
    @PostMapping
    public Result<NovelVO> create(@Valid @RequestBody NovelSaveDTO dto) {
        return Result.success(novelService.create(dto));
    }

    /**
     * 小说详情
     * GET /novels/{id}
     */
    @GetMapping("/{id}")
    public Result<NovelVO> detail(@PathVariable String id) {
        return Result.success(novelService.detail(id));
    }

    /**
     * 编辑小说信息
     * PUT /novels/{id}
     */
    @PutMapping("/{id}")
    public Result<NovelVO> update(@PathVariable String id,
                                  @Valid @RequestBody NovelSaveDTO dto) {
        return Result.success(novelService.update(id, dto));
    }

    /**
     * 删除小说（软删除 → 归档，status 置为 ARCHIVED）
     * DELETE /novels/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        novelService.delete(id);
        return Result.success();
    }
}
