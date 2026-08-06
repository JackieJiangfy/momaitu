package com.novelgraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novelgraph.entity.NovelCharacterStateHistory;
import com.novelgraph.mapper.NovelCharacterStateHistoryMapper;
import com.novelgraph.service.NovelCharacterStateHistoryService;
import org.springframework.stereotype.Service;

/**
 * 角色状态历史服务实现
 *
 * @author novelgraph
 */
@Service
public class NovelCharacterStateHistoryServiceImpl
        extends ServiceImpl<NovelCharacterStateHistoryMapper, NovelCharacterStateHistory>
        implements NovelCharacterStateHistoryService {
}
