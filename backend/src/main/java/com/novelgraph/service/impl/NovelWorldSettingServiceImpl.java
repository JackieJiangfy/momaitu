package com.novelgraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novelgraph.entity.NovelWorldSetting;
import com.novelgraph.mapper.NovelWorldSettingMapper;
import com.novelgraph.service.NovelWorldSettingService;
import org.springframework.stereotype.Service;

/**
 * 世界观服务实现
 *
 * @author novelgraph
 */
@Service
public class NovelWorldSettingServiceImpl extends ServiceImpl<NovelWorldSettingMapper, NovelWorldSetting>
        implements NovelWorldSettingService {
}
