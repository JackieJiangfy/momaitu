package com.novelgraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novelgraph.entity.NovelNarrator;
import com.novelgraph.mapper.NovelNarratorMapper;
import com.novelgraph.service.NovelNarratorService;
import org.springframework.stereotype.Service;

/**
 * 叙述者服务实现
 *
 * @author novelgraph
 */
@Service
public class NovelNarratorServiceImpl extends ServiceImpl<NovelNarratorMapper, NovelNarrator>
        implements NovelNarratorService {
}
