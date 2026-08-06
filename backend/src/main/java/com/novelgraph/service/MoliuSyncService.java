package com.novelgraph.service;

import com.novelgraph.dto.moliu.MoliuChapterSyncDTO;
import com.novelgraph.dto.moliu.MoliuCharacterSyncDTO;
import com.novelgraph.dto.moliu.MoliuForeshadowSyncDTO;
import com.novelgraph.dto.moliu.MoliuNarratorSyncDTO;
import com.novelgraph.dto.moliu.MoliuWorldSyncDTO;

import java.util.Map;

/**
 * 墨流数据同步服务接口
 * 所有方法均校验 novelId 归属当前登录用户。
 *
 * @author novelgraph
 */
public interface MoliuSyncService {

    /**
     * 同步角色（按 name upsert，保留原基础字段如 description/avatar 等）
     *
     * @return 操作结果，含 id 和 isCreated 标志
     */
    Map<String, Object> syncCharacter(String novelId, MoliuCharacterSyncDTO dto);

    /**
     * 同步世界观（1:1 覆盖）
     */
    Map<String, Object> syncWorld(String novelId, MoliuWorldSyncDTO dto);

    /**
     * 同步叙述者（1:1 覆盖）
     */
    Map<String, Object> syncNarrator(String novelId, MoliuNarratorSyncDTO dto);

    /**
     * 同步章节（按 chapterNum upsert）
     */
    Map<String, Object> syncChapter(String novelId, MoliuChapterSyncDTO dto);

    /**
     * 同步伏笔（按 moliuId upsert）
     */
    Map<String, Object> syncForeshadow(String novelId, MoliuForeshadowSyncDTO dto);
}
