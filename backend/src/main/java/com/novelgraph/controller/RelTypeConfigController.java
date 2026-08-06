package com.novelgraph.controller;

import com.novelgraph.common.Result;
import com.novelgraph.dto.RelTypeConfigDTO;
import com.novelgraph.dto.RelTypeConfigVO;
import com.novelgraph.service.RelTypeConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 关系类型配置控制器
 * 路径前缀：/novel/{novelId}/rel-types
 *
 * @author novelgraph
 */
@RestController
@RequestMapping("/novel/{novelId}/rel-types")
@RequiredArgsConstructor
public class RelTypeConfigController {

    private final RelTypeConfigService relTypeConfigService;

    /**
     * 关系类型列表
     * GET /novel/{novelId}/rel-types
     */
    @GetMapping
    public Result<List<RelTypeConfigVO>> list(@PathVariable String novelId) {
        return Result.success(relTypeConfigService.listByNovel(novelId));
    }

    /**
     * 自定义关系类型
     * POST /novel/{novelId}/rel-types
     */
    @PostMapping
    public Result<RelTypeConfigVO> create(@PathVariable String novelId,
                                          @Valid @RequestBody RelTypeConfigDTO dto) {
        return Result.success(relTypeConfigService.create(novelId, dto));
    }
}
