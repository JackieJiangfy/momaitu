package com.novelgraph;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 小说角色关系图谱系统 - 主启动类
 *
 * @author novelgraph
 */
@SpringBootApplication
@MapperScan("com.novelgraph.mapper")
public class NovelGraphApplication {

    public static void main(String[] args) {
        SpringApplication.run(NovelGraphApplication.class, args);
        System.out.println("\n" +
                "========================================\n" +
                "  小说角色关系图谱系统启动成功\n" +
                "  地址: http://127.0.0.1:8080/api\n" +
                "========================================");
    }
}
