package com.novelgraph.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.novelgraph.common.Result;
import com.novelgraph.dto.BatchCharacterDTO;
import com.novelgraph.dto.CharacterQueryDTO;
import com.novelgraph.dto.CharacterSaveDTO;
import com.novelgraph.dto.CharacterVO;
import com.novelgraph.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 角色控制器
 * 路径前缀：/novel/{novelId}/characters
 * 所有接口均需登录，Service 层会校验 novel 归属
 *
 * @author novelgraph
 */
@RestController
@RequestMapping("/novel/{novelId}/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    /**
     * 角色列表（支持搜索/过滤/分页）
     * GET /novel/{novelId}/characters
     */
    @GetMapping
    public Result<IPage<CharacterVO>> list(@PathVariable String novelId,
                                           CharacterQueryDTO query) {
        return Result.success(characterService.page(novelId, query));
    }

    /**
     * 新增角色
     * POST /novel/{novelId}/characters
     */
    @PostMapping
    public Result<CharacterVO> create(@PathVariable String novelId,
                                      @Valid @RequestBody CharacterSaveDTO dto) {
        return Result.success(characterService.create(novelId, dto));
    }

    /**
     * 角色详情
     * GET /novel/{novelId}/characters/{characterId}
     */
    @GetMapping("/{characterId}")
    public Result<CharacterVO> detail(@PathVariable String novelId,
                                      @PathVariable String characterId) {
        return Result.success(characterService.detail(novelId, characterId));
    }

    /**
     * 编辑角色
     * PUT /novel/{novelId}/characters/{characterId}
     */
    @PutMapping("/{characterId}")
    public Result<CharacterVO> update(@PathVariable String novelId,
                                      @PathVariable String characterId,
                                      @Valid @RequestBody CharacterSaveDTO dto) {
        return Result.success(characterService.update(novelId, characterId, dto));
    }

    /**
     * 删除角色（逻辑删除）
     * DELETE /novel/{novelId}/characters/{characterId}
     */
    @DeleteMapping("/{characterId}")
    public Result<Void> delete(@PathVariable String novelId,
                               @PathVariable String characterId) {
        characterService.delete(novelId, characterId);
        return Result.success();
    }

    /**
     * 批量导入角色
     * POST /novel/{novelId}/characters/batch
     */
    @PostMapping("/batch")
    public Result<Map<String, Object>> batchImport(@PathVariable String novelId,
                                                   @Valid @RequestBody BatchCharacterDTO dto) {
        Integer count = characterService.batchImport(novelId, dto);
        Map<String, Object> data = new HashMap<>(1);
        data.put("successCount", count);
        return Result.success(data);
    }
}
