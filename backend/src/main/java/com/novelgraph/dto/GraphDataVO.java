package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图谱数据 VO（对应前端 GraphData）
 * 设计文档 5.2 节定义的响应格式
 *
 * @author novelgraph
 */
@Data
public class GraphDataVO implements Serializable {

    private String novelTitle;
    private List<GraphNodeVO> nodes;
    private List<GraphEdgeVO> edges;
}
