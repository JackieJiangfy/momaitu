<template>
  <div class="rel-type">
    <div class="rel-type__toolbar">
      <div class="rel-type__title">
        <a-button type="text" @click="$router.back()">
          <template #icon><icon-left /></template>
          返回
        </a-button>
        <h2>关系类型配置</h2>
        <a-tag color="blue" size="small">共 {{ types.length }} 种</a-tag>
      </div>
      <a-button type="primary" @click="handleCreate">
        <template #icon><icon-plus /></template>
        新增类型
      </a-button>
    </div>

    <a-spin :loading="loading" dot>
      <div class="rel-type__grid">
        <div v-for="t in types" :key="t.id" class="rel-type__card">
          <div class="rel-type__card-info">
            <a-tag :color="categoryColor(t.category)" size="small">{{ categoryLabel(t.category) }}</a-tag>
            <span class="rel-type__name">{{ t.typeName }}</span>
            <span v-if="t.icon" class="text-secondary">{{ t.icon }}</span>
          </div>
          <a-tag size="small" color="gray">预置</a-tag>
        </div>
      </div>
      <a-empty v-if="!loading && types.length === 0" description="暂无关系类型" />
    </a-spin>

    <!-- 新增弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      title="新增关系类型"
      :ok-loading="modalLoading"
      @ok="handleCreateSubmit"
    >
      <a-form :model="form" layout="vertical">
        <a-form-item field="typeName" label="类型名称" :rules="[{ required: true, message: '请输入类型名称' }]">
          <a-input v-model="form.typeName" placeholder="如：挚友" :max-length="32" />
        </a-form-item>
        <a-form-item field="category" label="关系性质" :rules="[{ required: true, message: '请选择' }]">
          <a-select v-model="form.category">
            <a-option value="positive">友好</a-option>
            <a-option value="neutral">中立</a-option>
            <a-option value="negative">敌对</a-option>
          </a-select>
        </a-form-item>
        <a-form-item field="icon" label="图标（可选）">
          <a-input v-model="form.icon" placeholder="emoji 或文字" :max-length="16" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { listRelTypes, createRelType } from '@/api/relationship'
import type { RelTypeConfigVO, RelTypeConfigDTO } from '@/types'

const route = useRoute()
const novelId = computed(() => route.params.novelId as string)

const types = ref<RelTypeConfigVO[]>([])
const loading = ref(false)

const modalVisible = ref(false)
const modalLoading = ref(false)
const form = reactive<RelTypeConfigDTO>({ typeName: '', category: 'positive', icon: '' })

async function fetchData() {
  loading.value = true
  try {
    types.value = await listRelTypes(novelId.value)
  } finally {
    loading.value = false
  }
}

function handleCreate() {
  form.typeName = ''
  form.category = 'positive'
  form.icon = ''
  modalVisible.value = true
}

async function handleCreateSubmit() {
  if (!form.typeName) {
    Message.warning('请输入类型名称')
    return
  }
  modalLoading.value = true
  try {
    await createRelType(novelId.value, form)
    Message.success('已添加')
    modalVisible.value = false
    fetchData()
  } catch {
    // 错误已处理
  } finally {
    modalLoading.value = false
  }
}

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

onMounted(fetchData)
</script>

<style scoped lang="scss">
.rel-type {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;

  &__toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
  }

  &__title {
    display: flex;
    align-items: center;
    gap: 12px;

    h2 {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
    }
  }

  &__grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 12px;
  }

  &__card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    background: $color-bg-card;
    border: 1px solid $color-border;
    border-radius: $radius-md;
  }

  &__card-info {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  &__name {
    font-weight: 500;
  }
}
</style>
