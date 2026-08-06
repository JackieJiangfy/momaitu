<template>
  <div class="graph-view">
    <!-- 顶部操作栏 -->
    <div class="graph-view__toolbar">
      <div class="graph-view__title">
        <a-button type="text" @click="$router.push('/novels')">
          <template #icon><icon-left /></template>
          返回
        </a-button>
        <h2 v-if="data?.novelTitle">{{ data.novelTitle }} · 关系图谱</h2>
        <h2 v-else>关系图谱</h2>
      </div>
      <div class="graph-view__actions">
        <a-tooltip content="居中">
          <a-button @click="resetView"><template #icon><icon-fullscreen /></template></a-button>
        </a-tooltip>
        <a-tooltip content="刷新">
          <a-button @click="fetchData" :loading="loading">
            <template #icon><icon-refresh /></template>
          </a-button>
        </a-tooltip>
      </div>
    </div>

    <!-- 图例 -->
    <div class="graph-view__legend">
      <span class="graph-view__legend-title">图例：</span>
      <span class="legend-item"><span class="dot dot-positive"></span>友好</span>
      <span class="legend-item"><span class="dot dot-neutral"></span>中立</span>
      <span class="legend-item"><span class="dot dot-negative"></span>敌对</span>
      <span class="legend-divider"></span>
      <span class="legend-item"><span class="role role-main">主</span>主角</span>
      <span class="legend-item"><span class="role role-villain">反</span>反派</span>
      <span class="legend-item"><span class="role role-support">配</span>配角</span>
    </div>

    <!-- 图谱画布 -->
    <div ref="chartRef" class="graph-view__canvas" />

    <!-- 空状态 -->
    <a-empty
      v-if="!loading && data && data.nodes.length === 0"
      description="暂无角色和关系数据"
      class="graph-view__empty"
    >
      <template #action>
        <a-button type="primary" @click="$router.push(`/novel/${novelId}/characters`)">
          前往添加角色
        </a-button>
      </template>
    </a-empty>

    <!-- 详情侧边栏 -->
    <transition name="slide">
      <div v-if="selectedNode" class="graph-view__detail">
        <div class="graph-view__detail-header">
          <h3>{{ selectedNode.name }}</h3>
          <a-button type="text" size="small" @click="selectedNode = null">×</a-button>
        </div>
        <a-descriptions :column="1" size="small" bordered>
          <a-descriptions-item label="别名">{{ selectedNode.alias || '-' }}</a-descriptions-item>
          <a-descriptions-item label="势力">{{ selectedNode.faction || '-' }}</a-descriptions-item>
          <a-descriptions-item label="定位">{{ roleLabel(selectedNode.roleType) }}</a-descriptions-item>
          <a-descriptions-item label="种族">{{ selectedNode.species || '-' }}</a-descriptions-item>
          <a-descriptions-item label="关系数">{{ selectedNode.relationCount }}</a-descriptions-item>
        </a-descriptions>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import * as echarts from 'echarts/core'
import { GraphChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getGraph } from '@/api/graph'
import type { GraphDataVO, GraphNodeVO } from '@/types'

echarts.use([GraphChart, TooltipComponent, LegendComponent, TitleComponent, CanvasRenderer])

const route = useRoute()
const novelId = computed(() => route.params.novelId as string)

const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null
const loading = ref(false)
const data = ref<GraphDataVO | null>(null)
const selectedNode = ref<GraphNodeVO | null>(null)

const roleColorMap: Record<string, string> = {
  main: '#f53f3f',
  support: '#ff7d00',
  villain: '#722ed1',
  minor: '#86909c',
  neutral: '#16599e'
}

const categoryColorMap: Record<string, string> = {
  positive: '#00b42a',
  neutral: '#86909c',
  negative: '#f53f3f'
}

function roleLabel(t: string): string {
  const m: Record<string, string> = {
    main: '主角', support: '重要配角', minor: '次要角色', villain: '反派', neutral: '中立'
  }
  return m[t] || t || '-'
}

async function fetchData() {
  loading.value = true
  try {
    data.value = await getGraph(novelId.value)
    renderChart()
  } finally {
    loading.value = false
  }
}

function renderChart() {
  if (!chart || !data.value) return
  const { nodes, edges } = data.value

  const chartNodes = nodes.map((n) => ({
    id: n.id,
    name: n.name,
    symbolSize: 30 + Math.min(n.relationCount * 6, 50),
    category: n.roleType || 'neutral',
    itemStyle: {
      color: roleColorMap[n.roleType] || '#16599e',
      borderColor: '#fff',
      borderWidth: 2
    },
    label: { show: true, position: 'bottom' },
    rawData: n
  }))

  const chartEdges = edges.map((e) => ({
    source: e.source,
    target: e.target,
    label: {
      show: false
    },
    lineStyle: {
      color: categoryColorMap[e.category] || '#86909c',
      width: 1 + e.intensity / 4,
      curveness: e.directed ? 0.2 : 0
    },
    symbol: e.directed ? ['none', 'arrow'] : ['none', 'none'],
    rawData: e
  }))

  const categories = Object.keys(roleColorMap).map((k) => ({ name: roleLabel(k) }))

  chart.setOption({
    tooltip: {
      formatter: (p: any) => {
        if (p.dataType === 'node') {
          const n = p.data.rawData as GraphNodeVO
          return `<b>${n.name}</b><br/>势力: ${n.faction || '-'}<br/>定位: ${roleLabel(n.roleType)}<br/>关系数: ${n.relationCount}`
        } else if (p.dataType === 'edge') {
          const e = p.data.rawData
          return `${e.relType}<br/>强度: ${e.intensity}`
        }
        return ''
      }
    },
    legend: {
      data: categories.map((c) => c.name),
      bottom: 10
    },
    series: [
      {
        type: 'graph',
        layout: 'force',
        data: chartNodes,
        links: chartEdges,
        categories,
        roam: true,
        draggable: true,
        focusNodeAdjacency: true,
        force: {
          repulsion: 220,
          edgeLength: [80, 180],
          gravity: 0.08
        },
        emphasis: {
          focus: 'adjacency',
          lineStyle: { width: 4 }
        },
        lineStyle: { opacity: 0.7 }
      }
    ]
  })

  // 节点点击事件
  chart.off('click')
  chart.on('click', (params: any) => {
    if (params.dataType === 'node') {
      selectedNode.value = params.data.rawData as GraphNodeVO
    }
  })
}

function resetView() {
  if (!chart) return
  chart.setOption({
    series: [{ type: 'graph', zoom: 1, center: ['50%', '50%'] }]
  } as any)
}

function handleResize() {
  chart?.resize()
}

onMounted(async () => {
  await nextTick()
  if (chartRef.value) {
    chart = echarts.init(chartRef.value)
    window.addEventListener('resize', handleResize)
  }
  fetchData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
  chart = null
})

watch(novelId, fetchData)
</script>

<style scoped lang="scss">
.graph-view {
  position: relative;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: $color-bg-page;

  &__toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 24px;
    background: $color-bg-card;
    border-bottom: 1px solid $color-border;
  }

  &__title {
    display: flex;
    align-items: center;
    gap: 8px;

    h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
    }
  }

  &__actions {
    display: flex;
    gap: 8px;
  }

  &__legend {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 8px 24px;
    background: $color-bg-card;
    border-bottom: 1px solid $color-border;
    font-size: 13px;

    .legend-item {
      display: inline-flex;
      align-items: center;
      gap: 6px;
    }

    .legend-divider {
      width: 1px;
      height: 14px;
      background: $color-border;
      margin: 0 4px;
    }

    .dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;

      &-positive { background: #00b42a; }
      &-neutral { background: #86909c; }
      &-negative { background: #f53f3f; }
    }

    .role {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 18px;
      height: 18px;
      border-radius: 50%;
      color: #fff;
      font-size: 11px;

      &-main { background: #f53f3f; }
      &-villain { background: #722ed1; }
      &-support { background: #ff7d00; }
    }
  }

  &__canvas {
    flex: 1;
    min-height: 0;
  }

  &__empty {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }

  &__detail {
    position: absolute;
    top: 130px;
    right: 24px;
    width: 320px;
    background: $color-bg-card;
    border: 1px solid $color-border;
    border-radius: $radius-md;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    padding: 16px;
  }

  &__detail-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
    }
  }
}

.slide-enter-active, .slide-leave-active {
  transition: transform 0.25s ease, opacity 0.25s ease;
}
.slide-enter-from, .slide-leave-to {
  transform: translateX(20px);
  opacity: 0;
}
</style>
