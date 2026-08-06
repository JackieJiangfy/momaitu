<template>
  <a-modal
    :visible="visible"
    :title="isEdit ? '编辑小说' : '创建小说'"
    :width="560"
    :mask-closable="false"
    :ok-loading="loading"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-form-item field="title" label="标题">
        <a-input v-model="form.title" placeholder="请输入小说标题" allow-clear :max-length="128" />
      </a-form-item>
      <a-form-item field="author" label="作者">
        <a-input v-model="form.author" placeholder="请输入作者" allow-clear :max-length="64" />
      </a-form-item>
      <a-form-item field="description" label="简介">
        <a-textarea
          v-model="form.description"
          placeholder="请输入小说简介"
          allow-clear
          :max-length="500"
          show-word-limit
          :auto-size="{ minRows: 3, maxRows: 6 }"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from 'vue'
import { type FieldRule, type FormInstance, Message } from '@arco-design/web-vue'
import { createNovel, updateNovel } from '@/api/novel'
import type { NovelSaveDTO, NovelVO } from '@/types'

const props = defineProps<{
  visible: boolean
  novel?: NovelVO | null  // 传入则为编辑模式
}>()

const emit = defineEmits<{
  (e: 'update:visible', v: boolean): void
  (e: 'success', novel: NovelVO): void
}>()

const formRef = ref<FormInstance>()
const loading = ref(false)
const isEdit = computed(() => !!props.novel)

const form = reactive<NovelSaveDTO>({
  title: '',
  author: '',
  description: ''
})

const rules: Record<string, FieldRule[]> = {
  title: [
    { required: true, message: '请输入小说标题' },
    { maxLength: 128, message: '标题长度不能超过 128 个字符' }
  ]
}

// 弹窗打开时同步表单数据
watch(
  () => props.visible,
  (v) => {
    if (v) {
      if (props.novel) {
        form.title = props.novel.title
        form.author = props.novel.author
        form.description = props.novel.description
      } else {
        form.title = ''
        form.author = ''
        form.description = ''
      }
    }
  }
)

async function handleOk() {
  const valid = await formRef.value?.validate()
  if (valid) return // 有错误不继续

  loading.value = true
  try {
    let result: NovelVO
    if (props.novel) {
      result = await updateNovel(props.novel.id, form)
      Message.success('小说已更新')
    } else {
      result = await createNovel(form)
      Message.success('小说已创建')
    }
    emit('success', result)
    emit('update:visible', false)
  } catch {
    // 错误已由 request.ts 处理
  } finally {
    loading.value = false
  }
}

function handleCancel() {
  emit('update:visible', false)
}
</script>
