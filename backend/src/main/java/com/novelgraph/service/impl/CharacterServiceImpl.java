package com.novelgraph.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novelgraph.common.BusinessException;
import com.novelgraph.common.ResultCode;
import com.novelgraph.dto.BatchCharacterDTO;
import com.novelgraph.dto.CharacterQueryDTO;
import com.novelgraph.dto.CharacterSaveDTO;
import com.novelgraph.dto.CharacterVO;
import com.novelgraph.entity.NovelCharacter;
import com.novelgraph.mapper.NovelCharacterMapper;
import com.novelgraph.mapper.NovelRelationshipMapper;
import com.novelgraph.service.CharacterService;
import com.novelgraph.service.NovelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现
 * - 所有操作先校验 novel 归属当前用户（通过 NovelService.checkOwnership）
 * - 再校验 character 属于该 novel
 * - 列表/详情返回时附带 relationCount（用于图谱节点大小计算）
 *
 * @author novelgraph
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterServiceImpl implements CharacterService {

    private final NovelCharacterMapper characterMapper;
    private final NovelRelationshipMapper relationshipMapper;
    private final NovelService novelService;

    @Override
    public IPage<CharacterVO> page(String novelId, CharacterQueryDTO query) {
        novelService.checkOwnership(novelId);

        Page<NovelCharacter> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<NovelCharacter> wrapper = new LambdaQueryWrapper<NovelCharacter>()
                .eq(NovelCharacter::getNovelId, novelId)
                .orderByAsc(NovelCharacter::getSortOrder)
                .orderByDesc(NovelCharacter::getCreatedAt);

        if (StrUtil.isNotBlank(query.getKeyword())) {
            String kw = query.getKeyword().trim();
            wrapper.and(w -> w.like(NovelCharacter::getName, kw).or().like(NovelCharacter::getAlias, kw));
        }
        if (StrUtil.isNotBlank(query.getFaction())) {
            wrapper.eq(NovelCharacter::getFaction, query.getFaction());
        }
        if (StrUtil.isNotBlank(query.getRoleType())) {
            wrapper.eq(NovelCharacter::getRoleType, query.getRoleType());
        }
        if (StrUtil.isNotBlank(query.getSpecies())) {
            wrapper.eq(NovelCharacter::getSpecies, query.getSpecies());
        }

        IPage<NovelCharacter> charPage = characterMapper.selectPage(page, wrapper);
        return charPage.convert(this::toVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CharacterVO create(String novelId, CharacterSaveDTO dto) {
        novelService.checkOwnership(novelId);

        NovelCharacter character = new NovelCharacter();
        BeanUtil.copyProperties(dto, character);
        character.setNovelId(novelId);
        if (character.getSortOrder() == null) {
            character.setSortOrder(0);
        }
        characterMapper.insert(character);
        log.info("角色创建成功: id={}, name={}, novelId={}", character.getId(), character.getName(), novelId);
        return toVO(character);
    }

    @Override
    public CharacterVO detail(String novelId, String characterId) {
        NovelCharacter character = checkCharacterInNovel(novelId, characterId);
        return toVO(character);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CharacterVO update(String novelId, String characterId, CharacterSaveDTO dto) {
        NovelCharacter character = checkCharacterInNovel(novelId, characterId);
        BeanUtil.copyProperties(dto, character);
        characterMapper.updateById(character);
        log.info("角色更新成功: id={}", characterId);
        return toVO(character);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String novelId, String characterId) {
        NovelCharacter character = checkCharacterInNovel(novelId, characterId);
        characterMapper.deleteById(character.getId());
        log.info("角色删除成功: id={}, name={}", characterId, character.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchDelete(String novelId, List<String> characterIds) {
        if (characterIds == null || characterIds.isEmpty()) {
            return 0;
        }
        novelService.checkOwnership(novelId);
        // 一次性删除（MyBatis-Plus 逻辑删除会自动更新 deleted 字段）
        int count = characterMapper.deleteBatchIds(characterIds);
        log.info("批量删除角色成功: novelId={}, count={}", novelId, count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchImport(String novelId, BatchCharacterDTO dto) {
        novelService.checkOwnership(novelId);

        List<CharacterSaveDTO> list = dto.getCharacters();
        if (list == null || list.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "导入列表不能为空");
        }

        int success = 0;
        for (CharacterSaveDTO item : list) {
            NovelCharacter character = new NovelCharacter();
            BeanUtil.copyProperties(item, character);
            character.setNovelId(novelId);
            if (character.getSortOrder() == null) {
                character.setSortOrder(0);
            }
            characterMapper.insert(character);
            success++;
        }
        log.info("批量导入角色完成: novelId={}, 成功 {} 条", novelId, success);
        return success;
    }

    @Override
    public NovelCharacter checkCharacterInNovel(String novelId, String characterId) {
        novelService.checkOwnership(novelId);
        NovelCharacter character = characterMapper.selectById(characterId);
        if (character == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        if (!novelId.equals(character.getNovelId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "角色不属于该小说");
        }
        return character;
    }

    /**
     * 实体转 VO，填充 relationCount
     */
    private CharacterVO toVO(NovelCharacter character) {
        CharacterVO vo = new CharacterVO();
        BeanUtil.copyProperties(character, vo);
        // 统计该角色的关系数（作为源或目标）
        Long count = relationshipMapper.selectCount(
                new LambdaQueryWrapper<com.novelgraph.entity.NovelRelationship>()
                        .eq(com.novelgraph.entity.NovelRelationship::getSourceId, character.getId())
                        .or()
                        .eq(com.novelgraph.entity.NovelRelationship::getTargetId, character.getId()));
        vo.setRelationCount(count != null ? count.intValue() : 0);
        return vo;
    }
}
