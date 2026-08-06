package com.novelgraph.dto;

import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * 批量导入角色请求 DTO
 *
 * @author novelgraph
 */
@Data
public class BatchCharacterDTO implements Serializable {

    @Valid
    private List<CharacterSaveDTO> characters;
}
