package com.novelgraph.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.novelgraph.common.Result;
import com.novelgraph.dto.RelationshipQueryDTO;
import com.novelgraph.dto.RelationshipSaveDTO;
import com.novelgraph.dto.RelationshipVO;
import com.novelgraph.service.RelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 关系控制器
 * 路径前缀：/novel/{novelId}/relationships
 *
 * @author novelgraph
 */
@RestController
@RequestMapping("/novel/{novelId}/relationships")
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;

    /**
     * 关系列表
     * GET /novel/{novelId}/relationships
     */
    @GetMapping
    public Result<IPage<RelationshipVO>> list(@PathVariable String novelId,
                                              RelationshipQueryDTO query) {
        return Result.success(relationshipService.page(novelId, query));
    }

    /**
     * 添加关系
     * POST /novel/{novelId}/relationships
     */
    @PostMapping
    public Result<RelationshipVO> create(@PathVariable String novelId,
                                         @Valid @RequestBody RelationshipSaveDTO dto) {
        return Result.success(relationshipService.create(novelId, dto));
    }

    /**
     * 编辑关系
     * PUT /novel/{novelId}/relationships/{relationshipId}
     */
    @PutMapping("/{relationshipId}")
    public Result<RelationshipVO> update(@PathVariable String novelId,
                                          @PathVariable String relationshipId,
                                          @Valid @RequestBody RelationshipSaveDTO dto) {
        return Result.success(relationshipService.update(novelId, relationshipId, dto));
    }

    /**
     * 删除关系
     * DELETE /novel/{novelId}/relationships/{relationshipId}
     */
    @DeleteMapping("/{relationshipId}")
    public Result<Void> delete(@PathVariable String novelId,
                               @PathVariable String relationshipId) {
        relationshipService.delete(novelId, relationshipId);
        return Result.success();
    }
}
