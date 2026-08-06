<template>
  <a-modal
    :visible="visible"
    :title="isEdit ? '编辑角色' : '新增角色'"
    :width="640"
    :mask-closable="false"
    :ok-loading="loading"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-form ref="formRef" :model="form" :rules="rules" layout="vertical">
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item field="name" label="姓名">
            <a-input v-model="form.name" placeholder="请输入角色姓名" allow-clear :max-length="64" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item field="alias" label="别名/外号">
            <a-input v-model="form.alias" placeholder="多个用逗号分隔" allow-clear :max-length="256" />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item field="faction" label="势力/门派">
            <a-input v-model="form.faction" placeholder="如：青云门" allow-clear :max-length="64" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item field="roleType" label="定位">
            <a-select v-model="form.roleType" placeholder="请选择" allow-clear>
              <a-option value="main">主角</a-option>
              <a-option value="support">重要配角</a-option>
              <a-option value="minor">次要角色</a-option>
              <a-option value="villain">反派</a-option>
              <a-option value="neutral">中立</a-option>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item field="species" label="种族">
            <a-input v-model="form.species" placeholder="如：人/妖/仙" allow-clear :max-length="32" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item field="powerLevel" label="战力等级">
            <a-input v-model="form.powerLevel" placeholder="如：筑基/金丹" allow-clear :max-length="32" />
          </a-form-item>
        </a-col>
      </a-row>
      <a-form-item field="description" label="简介">
        <a-textarea
          v-model="form.description"
          placeholder="请输入角色简介"
          allow-clear
          :max-length="2000"
          show-word-limit
          :auto-size="{ minRows: 3, maxRows: 6 }"
        />
      </a-form-item>
      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item field="firstChapter" label="首次出场章节">
            <a-input v-model="form.firstChapter" placeholder="如：第1章" allow-clear :max-length="32" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item field="sortOrder" label="排序权重">
            <a-input-number
              v-model="form.sortOrder"
              :min="0"
              :max="9999"
              placeholder="数字越小越靠前"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from 'vue'
import { type FieldRule, type FormInstance, Message } from '@arco-design/web-vue'
import { createCharacter, updateCharacter } from '@/api/character'
import type { CharacterSaveDTO, CharacterVO } from '@/types'

const props = defineProps<{
  visible: boolean
  novelId: string
  character?: CharacterVO | null
}>()

const emit = defineEmits<{
  (e: 'update:visible', v: boolean): void
  (e: 'success'): void
}>()

const formRef = ref<FormInstance>()
const loading = ref(false)
const isEdit = computed(() => !!props.character)

const form = reactive<CharacterSaveDTO>({
  name: '',
  alias: '',
  faction: '',
  roleType: '',
  species: '',
  powerLevel: '',
  description: '',
  firstChapter: '',
  sortOrder: 0
})

const rules: Record<string, FieldRule[]> = {
  name: [
    { required: true, message: '请输入角色姓名' },
    { maxLength: 64, message: '姓名长度不能超过 64 个字符' }
  ]
}

watch(
  () => props.visible,
  (v) => {
    if (v) {
      if (props.character) {
        Object.assign(form, {
          name: props.character.name,
          alias: props.character.alias,
          faction: props.character.faction,
          roleType: props.character.roleType,
          species: props.character.species,
          powerLevel: props.character.powerLevel,
          description: props.character.description,
          firstChapter: props.character.firstChapter,
          sortOrder: props.character.sortOrder
        })
      } else {
        Object.assign(form, {
          name: '', alias: '', faction: '', roleType: '', species: '',
          powerLevel: '', description: '', firstChapter: '', sortOrder: 0
        })
      }
    }
  }
)

async function handleOk() {
  const valid = await formRef.value?.validate()
  if (valid) return

  loading.value = true
  try {
    if (props.character) {
      await updateCharacter(props.novelId, props.character.id, form)
      Message.success('角色已更新')
    } else {
      await createCharacter(props.novelId, form)
      Message.success('角色已创建')
    }
    emit('success')
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
