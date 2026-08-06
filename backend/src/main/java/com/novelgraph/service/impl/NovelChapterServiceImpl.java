package com.novelgraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novelgraph.entity.NovelChapter;
import com.novelgraph.mapper.NovelChapterMapper;
import com.novelgraph.service.NovelChapterService;
import org.springframework.stereotype.Service;

/**
 * 章节服务实现
 *
 * @author novelgraph
 */
@Service
public class NovelChapterServiceImpl extends ServiceImpl<NovelChapterMapper, NovelChapter>
        implements NovelChapterService {
}
