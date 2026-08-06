package com.novelgraph.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.novelgraph.dto.NovelQueryDTO;
import com.novelgraph.dto.NovelSaveDTO;
import com.novelgraph.dto.NovelVO;
import com.novelgraph.entity.Novel;

/**
 * 小说服务接口
 *
 * @author novelgraph
 */
public interface NovelService {

    /**
     * 分页查询当前用户的小说列表
     */
    IPage<NovelVO> page(NovelQueryDTO query);

    /**
     * 创建小说
     *
     * @return 新建的小说 VO
     */
    NovelVO create(NovelSaveDTO dto);

    /**
     * 获取小说详情（校验归属）
     */
    NovelVO detail(String id);

    /**
     * 编辑小说信息（校验归属）
     */
    NovelVO update(String id, NovelSaveDTO dto);

    /**
     * 删除小说（软删除 → 归档，状态置为 ARCHIVED）
     */
    void delete(String id);

    /**
     * 校验当前用户对小说的访问权限，返回小说实体
     * 不通过则抛 BusinessException
     */
    Novel checkOwnership(String novelId);
}
