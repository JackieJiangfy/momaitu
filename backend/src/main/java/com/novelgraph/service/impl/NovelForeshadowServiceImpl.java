package com.novelgraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novelgraph.entity.NovelForeshadow;
import com.novelgraph.mapper.NovelForeshadowMapper;
import com.novelgraph.service.NovelForeshadowService;
import org.springframework.stereotype.Service;

/**
 * 伏笔服务实现
 *
 * @author novelgraph
 */
@Service
public class NovelForeshadowServiceImpl extends ServiceImpl<NovelForeshadowMapper, NovelForeshadow>
        implements NovelForeshadowService {
}
