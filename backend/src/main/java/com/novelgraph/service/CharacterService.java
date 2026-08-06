package com.novelgraph.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.novelgraph.dto.BatchCharacterDTO;
import com.novelgraph.dto.CharacterQueryDTO;
import com.novelgraph.dto.CharacterSaveDTO;
import com.novelgraph.dto.CharacterVO;
import com.novelgraph.entity.NovelCharacter;

/**
 * 角色服务接口
 *
 * @author novelgraph
 */
public interface CharacterService {

    /**
     * 分页查询角色列表（校验小说归属）
     */
    IPage<CharacterVO> page(String novelId, CharacterQueryDTO query);

    /**
     * 新增角色（校验小说归属）
     */
    CharacterVO create(String novelId, CharacterSaveDTO dto);

    /**
     * 角色详情（校验小说归属）
     */
    CharacterVO detail(String novelId, String characterId);

    /**
     * 编辑角色（校验小说归属）
     */
    CharacterVO update(String novelId, String characterId, CharacterSaveDTO dto);

    /**
     * 删除角色（校验小说归属）
     */
    void delete(String novelId, String characterId);

    /**
     * 批量导入角色（校验小说归属）
     *
     * @return 成功导入数量
     */
    Integer batchImport(String novelId, BatchCharacterDTO dto);

    /**
     * 校验角色属于指定小说（内部使用）
     */
    NovelCharacter checkCharacterInNovel(String novelId, String characterId);
}
