package com.novelgraph.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novelgraph.dto.GraphDataVO;
import com.novelgraph.dto.GraphEdgeVO;
import com.novelgraph.dto.GraphNodeVO;
import com.novelgraph.entity.Novel;
import com.novelgraph.entity.NovelCharacter;
import com.novelgraph.entity.NovelRelationship;
import com.novelgraph.mapper.NovelCharacterMapper;
import com.novelgraph.mapper.NovelRelationshipMapper;
import com.novelgraph.service.GraphService;
import com.novelgraph.service.NovelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图谱服务实现
 * - 一次查询所有角色 → 构建节点
 * - 一次查询所有关系 → 构建边
 * - 在内存中统计每个节点的 relationCount（作为源或目标的次数）
 *
 * 性能说明：小说角色通常 20-200 个，单次查询 + 内存聚合完全足够。
 *
 * @author novelgraph
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GraphServiceImpl implements GraphService {

    private final NovelCharacterMapper characterMapper;
    private final NovelRelationshipMapper relationshipMapper;
    private final NovelService novelService;

    @Override
    public GraphDataVO getGraph(String novelId) {
        Novel novel = novelService.checkOwnership(novelId);

        // 1. 查询所有角色（节点）
        List<NovelCharacter> characters = characterMapper.selectList(
                new LambdaQueryWrapper<NovelCharacter>()
                        .eq(NovelCharacter::getNovelId, novelId)
                        .orderByAsc(NovelCharacter::getSortOrder));

        // 2. 查询所有关系（边）
        List<NovelRelationship> relationships = relationshipMapper.selectList(
                new LambdaQueryWrapper<NovelRelationship>()
                        .eq(NovelRelationship::getNovelId, novelId));

        // 3. 统计每个角色的关联数（用 Map 加速，避免 N²）
        Map<String, Integer> relationCountMap = new HashMap<>();
        for (NovelRelationship rel : relationships) {
            relationCountMap.merge(rel.getSourceId(), 1, Integer::sum);
            relationCountMap.merge(rel.getTargetId(), 1, Integer::sum);
        }

        // 4. 构建节点 VO
        List<GraphNodeVO> nodes = new ArrayList<>(characters.size());
        for (NovelCharacter c : characters) {
            GraphNodeVO node = new GraphNodeVO();
            node.setId(c.getId());
            node.setName(c.getName());
            node.setAlias(c.getAlias());
            node.setFaction(c.getFaction());
            node.setRoleType(c.getRoleType());
            node.setSpecies(c.getSpecies());
            node.setAvatarUrl(c.getAvatarUrl());
            node.setRelationCount(relationCountMap.getOrDefault(c.getId(), 0));
            nodes.add(node);
        }

        // 5. 构建边 VO
        List<GraphEdgeVO> edges = new ArrayList<>(relationships.size());
        for (NovelRelationship rel : relationships) {
            GraphEdgeVO edge = new GraphEdgeVO();
            edge.setId(rel.getId());
            edge.setSource(rel.getSourceId());
            edge.setTarget(rel.getTargetId());
            edge.setRelType(rel.getRelType());
            edge.setCategory(rel.getCategory());
            edge.setDirected(rel.getDirected() != null && rel.getDirected() == 1);
            edge.setIntensity(rel.getIntensity());
            edges.add(edge);
        }

        // 6. 组装响应
        GraphDataVO vo = new GraphDataVO();
        vo.setNovelTitle(novel.getTitle());
        vo.setNodes(nodes);
        vo.setEdges(edges);
        log.info("图谱数据查询: novelId={}, nodes={}, edges={}", novelId, nodes.size(), edges.size());
        return vo;
    }
}
