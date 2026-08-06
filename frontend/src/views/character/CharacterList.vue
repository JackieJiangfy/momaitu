<template>
  <div class="char-list">
    <!-- 顶部操作栏 -->
    <div class="char-list__toolbar">
      <div class="char-list__title">
        <a-button type="text" @click="$router.push('/novels')">
          <template #icon><icon-left /></template>
          返回
        </a-button>
        <h2 v-if="novelTitle">{{ novelTitle }} · 角色管理</h2>
        <h2 v-else>角色管理</h2>
      </div>
      <div class="char-list__actions">
        <a-input-search
          v-model="keyword"
          placeholder="搜索角色名/别名"
          allow-clear
          style="width: 220px"
          @search="handleSearch"
          @clear="handleSearch"
        />
        <a-select
          v-model="factionFilter"
          placeholder="势力筛选"
          allow-clear
          style="width: 140px"
          @change="handleSearch"
        >
          <a-option v-for="f in factionOptions" :key="f" :value="f">{{ f }}</a-option>
        </a-select>
        <a-button @click="batchModalVisible = true">
          <template #icon><icon-import /></template>
          批量导入
        </a-button>
        <a-popconfirm
          :content="`确认删除选中的 ${selectedKeys.length} 个角色？`"
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
          新增角色
        </a-button>
      </div>
    </div>

    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data="characters"
      :loading="loading"
      :pagination="pagination"
      row-key="id"
      :row-selection="{ type: 'checkbox', showCheckedAll, onlyCurrent }"
      v-model:selectedKeys="selectedKeys"
      :scroll="{ y: 'calc(100vh - 280px)' }"
      @page-change="onPageChange"
      @page-size-change="onPageSizeChange"
    >
      <template #name="{ record }">
        <span class="char-list__name">{{ record.name }}</span>
        <span v-if="record.alias" class="char-list__alias">（{{ record.alias }}）</span>
      </template>
      <template #faction="{ record }">
        <a-tag v-if="record.faction" size="small">{{ record.faction }}</a-tag>
        <span v-else class="text-secondary">-</span>
      </template>
      <template #roleType="{ record }">
        <a-tag v-if="record.roleType" :color="roleColor(record.roleType)" size="small">
          {{ roleLabel(record.roleType) }}
        </a-tag>
        <span v-else class="text-secondary">-</span>
      </template>
      <template #relationCount="{ record }">
        <span class="mono">{{ record.relationCount }}</span>
      </template>
      <template #operations="{ record }">
        <a-button type="text" size="small" @click="handleEdit(record)">编辑</a-button>
        <a-popconfirm content="确认删除此角色？关联关系也会被删除" @ok="handleDelete(record)">
          <a-button type="text" size="small" status="danger">删除</a-button>
        </a-popconfirm>
      </template>
    </a-table>

    <!-- 编辑弹窗 -->
    <CharacterEditModal
      v-model:visible="modalVisible"
      :novel-id="novelId"
      :character="editingChar"
      @success="fetchData"
    />

    <!-- 批量导入弹窗 -->
    <a-modal
      v-model:visible="batchModalVisible"
      title="批量导入角色"
      :width="640"
      :ok-loading="batchLoading"
      @ok="handleBatchImport"
    >
      <a-alert type="info" style="margin-bottom: 12px">
        每行一个角色，格式：<code class="mono">姓名,势力,定位</code>（仅姓名必填）
        <br />定位可选：main / support / minor / villain / neutral
      </a-alert>
      <a-textarea
        v-model="batchText"
        placeholder="张三,青云门,main&#10;李四,青云门,support"
        :auto-size="{ minRows: 8, maxRows: 16 }"
      />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { listCharacters, deleteCharacter, batchCreateCharacters } from '@/api/character'
import { getNovel } from '@/api/novel'
import type { CharacterVO } from '@/types'
import CharacterEditModal from './CharacterEditModal.vue'

const route = useRoute()
const novelId = computed(() => route.params.novelId as string)

const characters = ref<CharacterVO[]>([])
const novelTitle = ref('')
const loading = ref(false)
const keyword = ref('')
const factionFilter = ref<string | undefined>()

// 收集势力选项（从已加载的数据中）
const factionOptions = computed(() => {
  const set = new Set<string>()
  characters.value.forEach((c) => c.faction && set.add(c.faction))
  return Array.from(set)
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: true,
  showPageSize: true
})

// 行选择
const selectedKeys = ref<string[]>([])
const showCheckedAll = true
const onlyCurrent = false

const columns = [
  { title: '姓名', slotName: 'name', width: 200 },
  { title: '势力', slotName: 'faction', width: 120 },
  { title: '定位', slotName: 'roleType', width: 100 },
  { title: '种族', dataIndex: 'species', width: 80, ellipsis: true },
  { title: '战力', dataIndex: 'powerLevel', width: 100, ellipsis: true },
  { title: '关系数', slotName: 'relationCount', width: 80, align: 'right' as const },
  { title: '操作', slotName: 'operations', width: 140, fixed: 'right' as const }
]

async function fetchData() {
  loading.value = true
  try {
    const res = await listCharacters(novelId.value, {
      keyword: keyword.value,
      faction: factionFilter.value,
      page: pagination.current,
      size: pagination.pageSize
    })
    characters.value = res.records
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

// ---- 编辑 ----
const modalVisible = ref(false)
const editingChar = ref<CharacterVO | null>(null)

function handleCreate() {
  editingChar.value = null
  modalVisible.value = true
}

function handleEdit(record: CharacterVO) {
  editingChar.value = record
  modalVisible.value = true
}

async function handleDelete(record: CharacterVO) {
  await deleteCharacter(novelId.value, record.id)
  Message.success('已删除')
  if (characters.value.length === 1 && pagination.current > 1) {
    pagination.current--
  }
  fetchData()
}

async function handleBatchDelete() {
  if (selectedKeys.value.length === 0) return
  try {
    await Promise.all(selectedKeys.value.map((id) => deleteCharacter(novelId.value, id)))
    Message.success(`已删除 ${selectedKeys.value.length} 个角色`)
    selectedKeys.value = []
    if (characters.value.length === selectedKeys.value.length && pagination.current > 1) {
      pagination.current--
    }
    fetchData()
  } catch {
    // 错误已处理
  }
}

// ---- 批量导入 ----
const batchModalVisible = ref(false)
const batchLoading = ref(false)
const batchText = ref('')

async function handleBatchImport() {
  if (!batchText.value.trim()) {
    Message.warning('请输入角色数据')
    return
  }
  const lines = batchText.value.split('\n').filter((l) => l.trim())
  const characters = lines.map((line) => {
    const [name, faction, roleType] = line.split(',').map((s) => s.trim())
    return { name: name || '', faction: faction || '', roleType: roleType || '' }
  }).filter((c) => c.name)

  if (characters.length === 0) {
    Message.warning('未解析到有效角色')
    return
  }

  batchLoading.value = true
  try {
    await batchCreateCharacters(novelId.value, { characters })
    Message.success(`已导入 ${characters.length} 个角色`)
    batchText.value = ''
    batchModalVisible.value = false
    pagination.current = 1
    fetchData()
  } catch {
    // 错误已处理
  } finally {
    batchLoading.value = false
  }
}

// ---- 工具 ----
const roleTypeMap: Record<string, { label: string; color: string }> = {
  main: { label: '主角', color: 'red' },
  support: { label: '重要配角', color: 'orange' },
  minor: { label: '次要角色', color: 'gray' },
  villain: { label: '反派', color: 'purple' },
  neutral: { label: '中立', color: 'blue' }
}
function roleLabel(t: string) {
  return roleTypeMap[t]?.label || t
}
function roleColor(t: string) {
  return roleTypeMap[t]?.color || 'gray'
}

onMounted(async () => {
  // 拉小说标题
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
.char-list {
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

  &__alias {
    color: $color-text-secondary;
    font-size: 13px;
  }
}
</style>
