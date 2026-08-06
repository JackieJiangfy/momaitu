package com.novelgraph.service;

import com.novelgraph.dto.GraphDataVO;

/**
 * 图谱服务接口
 *
 * @author novelgraph
 */
public interface GraphService {

    /**
     * 获取某小说的图谱数据（校验归属）
     * 返回 { novelTitle, nodes, edges }
     */
    GraphDataVO getGraph(String novelId);
}
