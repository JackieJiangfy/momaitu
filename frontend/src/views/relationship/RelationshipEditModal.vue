<template>
  <a-modal
    :visible="visible"
    :title="isEdit ? '编辑关系' : '新增关系'"
    :width="560"
    :mask-closable="false"
    :ok-loading="loading"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-form-item field="sourceId" label="源角色">
        <a-select v-model="form.sourceId" placeholder="请选择源角色" allow-search>
          <a-option
            v-for="c in characters"
            :key="c.id"
            :value="c.id"
            :label="`${c.name}${c.faction ? '（' + c.faction + '）' : ''}`"
          />
        </a-select>
      </a-form-item>
      <a-form-item field="targetId" label="目标角色">
        <a-select v-model="form.targetId" placeholder="请选择目标角色" allow-search>
          <a-option
            v-for="c in characters"
            :key="c.id"
            :value="c.id"
            :label="`${c.name}${c.faction ? '（' + c.faction + '）' : ''}`"
          />
        </a-select>
      </a-form-item>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item field="relType" label="关系类型">
            <a-select v-model="form.relType" placeholder="请选择" allow-search allow-create>
              <a-option
                v-for="t in relTypes"
                :key="t.typeName"
                :value="t.typeName"
                :label="t.typeName"
              />
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item field="category" label="关系性质">
            <a-select v-model="form.category" placeholder="请选择">
              <a-option value="positive">友好（绿）</a-option>
              <a-option value="neutral">中立（灰）</a-option>
              <a-option value="negative">敌对（红）</a-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item field="directed" label="方向">
            <a-radio-group v-model="form.directed">
              <a-radio :value="0">双向</a-radio>
              <a-radio :value="1">单向</a-radio>
            </a-radio-group>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item field="intensity" label="强度（1-10）">
            <a-input-number v-model="form.intensity" :min="1" :max="10" style="width: 100%" />
          </a-form-item>
        </a-col>
      </a-row>
      <a-form-item field="description" label="关系说明">
        <a-textarea
          v-model="form.description"
          placeholder="可选"
          :max-length="256"
          show-word-limit
          :auto-size="{ minRows: 2, maxRows: 4 }"
        />
      </a-form-item>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item field="startChapter" label="起始章节">
            <a-input v-model="form.startChapter" placeholder="如：第5章" :max-length="32" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item field="endChapter" label="结束章节">
            <a-input v-model="form.endChapter" placeholder="如：第20章" :max-length="32" />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from 'vue'
import { type FieldRule, type FormInstance, Message } from '@arco-design/web-vue'
import { createRelationship, updateRelationship, listRelTypes } from '@/api/relationship'
import { listCharacters } from '@/api/character'
import type { RelationshipSaveDTO, RelationshipVO, CharacterVO, RelTypeConfigVO } from '@/types'

const props = defineProps<{
  visible: boolean
  novelId: string
  relationship?: RelationshipVO | null
}>()

const emit = defineEmits<{
  (e: 'update:visible', v: boolean): void
  (e: 'success'): void
}>()

const formRef = ref<FormInstance>()
const loading = ref(false)
const isEdit = computed(() => !!props.relationship)

// 角色列表（供选择）
const characters = ref<CharacterVO[]>([])
// 关系类型配置
const relTypes = ref<RelTypeConfigVO[]>([])

const form = reactive<RelationshipSaveDTO>({
  sourceId: '',
  targetId: '',
  relType: '',
  category: 'positive',
  directed: 0,
  intensity: 5,
  description: '',
  startChapter: '',
  endChapter: ''
})

const rules: Record<string, FieldRule[]> = {
  sourceId: [{ required: true, message: '请选择源角色' }],
  targetId: [{ required: true, message: '请选择目标角色' }],
  relType: [{ required: true, message: '请选择或输入关系类型' }],
  category: [{ required: true, message: '请选择关系性质' }]
}

// 弹窗打开时同步数据 + 拉取选项
watch(
  () => props.visible,
  async (v) => {
    if (v) {
      // 同步表单
      if (props.relationship) {
        Object.assign(form, {
          sourceId: props.relationship.sourceId,
          targetId: props.relationship.targetId,
          relType: props.relationship.relType,
          category: props.relationship.category,
          directed: props.relationship.directed,
          intensity: props.relationship.intensity,
          description: props.relationship.description,
          startChapter: props.relationship.startChapter,
          endChapter: props.relationship.endChapter
        })
      } else {
        Object.assign(form, {
          sourceId: '', targetId: '', relType: '', category: 'positive',
          directed: 0, intensity: 5, description: '', startChapter: '', endChapter: ''
        })
      }

      // 拉取角色列表和关系类型（一次拉取全部，避免分页）
      try {
        const [charRes, typeRes] = await Promise.all([
          listCharacters(props.novelId, { size: 999 }),
          listRelTypes(props.novelId)
        ])
        characters.value = charRes.records
        relTypes.value = typeRes
      } catch {
        // ignore
      }
    }
  }
)

async function handleOk() {
  const valid = await formRef.value?.validate()
  if (valid) return

  if (form.sourceId === form.targetId) {
    Message.warning('源角色和目标角色不能相同')
    return
  }

  loading.value = true
  try {
    if (props.relationship) {
      await updateRelationship(props.novelId, props.relationship.id, form)
      Message.success('关系已更新')
    } else {
      await createRelationship(props.novelId, form)
      Message.success('关系已创建')
    }
    emit('success')
    emit('update:visible', false)
  } catch {
    // 错误已处理
  } finally {
    loading.value = false
  }
}

function handleCancel() {
  emit('update:visible', false)
}
</script>
