<template>
  <div class="rel-list">
    <!-- 顶部操作栏 -->
    <div class="rel-list__toolbar">
      <div class="rel-list__title">
        <a-button type="text" @click="$router.push('/novels')">
          <template #icon><icon-left /></template>
          返回
        </a-button>
        <h2 v-if="novelTitle">{{ novelTitle }} · 关系管理</h2>
        <h2 v-else>关系管理</h2>
      </div>
      <div class="rel-list__actions">
        <a-input-search
          v-model="keyword"
          placeholder="搜索角色名/关系类型"
          allow-clear
          style="width: 240px"
          @search="handleSearch"
          @clear="handleSearch"
        />
        <a-select
          v-model="categoryFilter"
          placeholder="关系性质"
          allow-clear
          style="width: 120px"
          @change="handleSearch"
        >
          <a-option value="positive">友好</a-option>
          <a-option value="neutral">中立</a-option>
          <a-option value="negative">敌对</a-option>
        </a-select>
        <a-popconfirm
          :content="`确认删除选中的 ${selectedKeys.length} 条关系？`"
          :disabled="selectedKeys.length === 0"
          @ok="handleBatchDelete"
        >
          <a-button status="danger" :disabled="selectedKeys.length === 0">
            <template #icon><icon-delete /></template>
            批量删除 ({{ selectedKeys.length }})
          </a-button>
        </a-popconfirm>
        <a-button type="primary" @click="handleCreate">
          <template #icon><icon-plus /></template>
          新增关系
        </a-button>
      </div>
    </div>

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data="relationships"
      :loading="loading"
      :pagination="pagination"
      row-key="id"
      :row-selection="{ type: 'checkbox', showCheckedAll: true, onlyCurrent: false }"
      v-model:selectedKeys="selectedKeys"
      :scroll="{ y: 'calc(100vh - 280px)' }"
      @page-change="onPageChange"
      @page-size-change="onPageSizeChange"
    >
      <template #source="{ record }">
        <span class="rel-list__name">{{ record.sourceName }}</span>
        <span class="rel-list__arrow">→</span>
        <span class="rel-list__name">{{ record.targetName }}</span>
      </template>
      <template #relType="{ record }">
        <a-tag size="small">{{ record.relType }}</a-tag>
      </template>
      <template #category="{ record }">
        <a-tag :color="categoryColor(record.category)" size="small">
          {{ categoryLabel(record.category) }}
        </a-tag>
      </template>
      <template #directed="{ record }">
        <span v-if="record.directed === 1" class="text-secondary">单向</span>
        <span v-else>双向</span>
      </template>
      <template #intensity="{ record }">
        <span class="mono">{{ record.intensity }}</span>
      </template>
      <template #operations="{ record }">
        <a-button type="text" size="small" @click="handleEdit(record)">编辑</a-button>
        <a-popconfirm content="确认删除此关系？" @ok="handleDelete(record)">
          <a-button type="text" size="small" status="danger">删除</a-button>
        </a-popconfirm>
      </template>
    </a-table>

    <!-- 编辑弹窗 -->
    <RelationshipEditModal
      v-model:visible="modalVisible"
      :novel-id="novelId"
      :relationship="editingRel"
      @success="fetchData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { listRelationships, deleteRelationship, batchDeleteRelationships } from '@/api/relationship'
import { getNovel } from '@/api/novel'
import type { RelationshipVO } from '@/types'
import RelationshipEditModal from './RelationshipEditModal.vue'

const route = useRoute()
const novelId = computed(() => route.params.novelId as string)

const relationships = ref<RelationshipVO[]>([])
const novelTitle = ref('')
const loading = ref(false)
const keyword = ref('')
const categoryFilter = ref<string | undefined>()

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: true,
  showPageSize: true
})

const selectedKeys = ref<string[]>([])

const columns = [
  { title: '关系', slotName: 'source', width: 240 },
  { title: '类型', slotName: 'relType', width: 120 },
  { title: '性质', slotName: 'category', width: 100 },
  { title: '方向', slotName: 'directed', width: 80 },
  { title: '强度', slotName: 'intensity', width: 80, align: 'right' as const },
  { title: '说明', dataIndex: 'description', ellipsis: true, tooltip: true },
  { title: '操作', slotName: 'operations', width: 140, fixed: 'right' as const }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await listRelationships(novelId.value, {
      keyword: keyword.value,
      category: categoryFilter.value,
      page: pagination.current,
      size: pagination.pageSize
    })
    relationships.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.current = 1
  fetchData()
}

function onPageChange(page: number) {
  pagination.current = page
  fetchData()
}

function onPageSizeChange(size: number) {
  pagination.pageSize = size
  pagination.current = 1
  fetchData()
}

const modalVisible = ref(false)
const editingRel = ref<RelationshipVO | null>(null)

function handleCreate() {
  editingRel.value = null
  modalVisible.value = true
}

function handleEdit(record: RelationshipVO) {
  editingRel.value = record
  modalVisible.value = true
}

async function handleDelete(record: RelationshipVO) {
  await deleteRelationship(novelId.value, record.id)
  Message.success('已删除')
  if (relationships.value.length === 1 && pagination.current > 1) {
    pagination.current--
  }
  fetchData()
}

async function handleBatchDelete() {
  if (selectedKeys.value.length === 0) return
  try {
    const res = await batchDeleteRelationships(novelId.value, selectedKeys.value)
    Message.success(`已删除 ${res.successCount} 条关系`)
    selectedKeys.value = []
    if (relationships.value.length === 0 && pagination.current > 1) {
      pagination.current--
    }
    fetchData()
  } catch {
    // 错误已处理
  }
}

// ---- 工具 ----
function categoryColor(c: string): string {
  if (c === 'positive') return 'green'
  if (c === 'negative') return 'red'
  return 'gray'
}
function categoryLabel(c: string): string {
  if (c === 'positive') return '友好'
  if (c === 'negative') return '敌对'
  return '中立'
}

onMounted(async () => {
  try {
    const novel = await getNovel(novelId.value)
    novelTitle.value = novel.title
  } catch {
    // ignore
  }
  fetchData()
})
</script>

<style scoped lang="scss">
.rel-list {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;

  &__toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
    flex-wrap: wrap;
    gap: 12px;
  }

  &__title {
    display: flex;
    align-items: center;
    gap: 8px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }
  }

  &__actions {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }

  &__name {
    font-weight: 500;
  }

  &__arrow {
    margin: 0 8px;
    color: $color-text-secondary;
  }
}
</style>
