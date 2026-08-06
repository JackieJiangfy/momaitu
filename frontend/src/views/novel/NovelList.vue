<template>
  <div class="novel-list">
    <!-- 顶部操作栏 -->
    <div class="novel-list__toolbar">
      <div class="novel-list__title">
        <h2>我的小说</h2>
        <span class="text-secondary">共 {{ total }} 部</span>
      </div>
      <div class="novel-list__actions">
        <a-input-search
          v-model="keyword"
          placeholder="搜索标题/作者"
          allow-clear
          style="width: 240px"
          @search="handleSearch"
          @clear="handleSearch"
        />
        <a-button type="primary" @click="handleCreate">
          <template #icon><icon-plus /></template>
          新建小说
        </a-button>
      </div>
    </div>

    <!-- 卡片网格 -->
    <a-spin :loading="loading" dot>
      <div v-if="novels.length > 0" class="novel-list__grid">
        <div v-for="novel in novels" :key="novel.id" class="novel-card">
          <div class="novel-card__header">
            <div class="novel-card__cover">
              <icon-book :size="32" />
            </div>
            <div class="novel-card__info">
              <h3 class="novel-card__title" :title="novel.title">{{ novel.title }}</h3>
              <p class="novel-card__author" v-if="novel.author">{{ novel.author }}</p>
            </div>
          </div>
          <p class="novel-card__desc">{{ novel.description || '暂无简介' }}</p>
          <div class="novel-card__stats">
            <a-tag size="small">角色 {{ novel.characterCount }}</a-tag>
            <a-tag size="small">关系 {{ novel.relationCount }}</a-tag>
          </div>
          <div class="novel-card__footer">
            <span class="novel-card__time text-secondary mono">
              {{ formatDate(novel.createdAt) }}
            </span>
            <div class="novel-card__ops">
              <a-button type="text" size="small" @click="handleGraph(novel)">图谱</a-button>
              <a-button type="text" size="small" @click="handleManage(novel)">管理</a-button>
              <a-button type="text" size="small" @click="handleEdit(novel)">编辑</a-button>
              <a-popconfirm content="确认删除此小说？删除后不可恢复" @ok="handleDelete(novel)">
                <a-button type="text" size="small" status="danger">删除</a-button>
              </a-popconfirm>
            </div>
          </div>
        </div>
      </div>
      <a-empty v-else-if="!loading" description="暂无小说，点击右上角创建" />
    </a-spin>

    <!-- 分页 -->
    <div v-if="total > 0" class="novel-list__pagination">
      <a-pagination
        v-model:current="page"
        v-model:page-size="size"
        :total="total"
        show-total
        show-page-size
        @change="fetchData"
      />
    </div>

    <!-- 编辑/创建弹窗 -->
    <NovelEditModal
      v-model:visible="modalVisible"
      :novel="editingNovel"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { listNovels, deleteNovel } from '@/api/novel'
import type { NovelVO } from '@/types'
import NovelEditModal from './NovelEditModal.vue'

const router = useRouter()

const novels = ref<NovelVO[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(12)
const keyword = ref('')
const loading = ref(false)

// 弹窗
const modalVisible = ref(false)
const editingNovel = ref<NovelVO | null>(null)

async function fetchData() {
  loading.value = true
  try {
    const res = await listNovels({ keyword: keyword.value, page: page.value, size: size.value })
    novels.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

function handleCreate() {
  editingNovel.value = null
  modalVisible.value = true
}

function handleEdit(novel: NovelVO) {
  editingNovel.value = novel
  modalVisible.value = true
}

function handleSuccess() {
  fetchData()
}

async function handleDelete(novel: NovelVO) {
  await deleteNovel(novel.id)
  Message.success('已删除')
  // 如果当前页只剩 1 条且不是第一页，回到上一页
  if (novels.value.length === 1 && page.value > 1) {
    page.value--
  }
  fetchData()
}

function handleManage(novel: NovelVO) {
  router.push(`/novel/${novel.id}/characters`)
}

function handleGraph(novel: NovelVO) {
  router.push(`/novel/${novel.id}/graph`)
}

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString('zh-CN')
}

onMounted(fetchData)
</script>

<style scoped lang="scss">
.novel-list {
  max-width: 1400px;
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
    align-items: baseline;
    gap: 12px;

    h2 {
      margin: 0;
      font-size: 22px;
      font-weight: 600;
    }
  }

  &__actions {
    display: flex;
    gap: 12px;
  }

  &__grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 16px;
  }

  &__pagination {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
  }
}

.novel-card {
  background: $color-bg-card;
  border: 1px solid $color-border;
  border-radius: $radius-lg;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: all 0.2s;

  &:hover {
    border-color: $color-primary;
    box-shadow: $shadow-hover;
    transform: translateY(-2px);
  }

  &__header {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__cover {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 48px;
    height: 48px;
    border-radius: $radius-md;
    background: $color-bg-page;
    color: $color-primary;
    flex-shrink: 0;
  }

  &__info {
    flex: 1;
    min-width: 0;
  }

  &__title {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: $color-text-primary;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__author {
    margin: 4px 0 0;
    font-size: 13px;
    color: $color-text-secondary;
  }

  &__desc {
    margin: 0;
    font-size: 13px;
    color: $color-text-secondary;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    min-height: 39px;
  }

  &__stats {
    display: flex;
    gap: 8px;
  }

  &__footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-top: 12px;
    border-top: 1px solid $color-border;
  }

  &__time {
    font-size: 12px;
  }

  &__ops {
    display: flex;
    gap: 4px;
  }
}
</style>
