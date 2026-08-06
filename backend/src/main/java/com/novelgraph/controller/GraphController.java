package com.novelgraph.controller;

import com.novelgraph.common.Result;
import com.novelgraph.dto.GraphDataVO;
import com.novelgraph.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图谱控制器
 * 路径前缀：/novel/{novelId}/graph
 *
 * @author novelgraph
 */
@RestController
@RequestMapping("/novel/{novelId}/graph")
@RequiredArgsConstructor
public class GraphController {

    private final GraphService graphService;

    /**
     * 获取图谱数据
     * GET /novel/{novelId}/graph
     * 返回 { novelTitle, nodes, edges } 供前端 G6 直接渲染
     */
    @GetMapping
    public Result<GraphDataVO> getGraph(@PathVariable String novelId) {
        return Result.success(graphService.getGraph(novelId));
    }
}
