<template>
  <el-card>
    <el-button type="primary" style="margin-bottom:16px" @click="create">发起盘点</el-button>

    <el-table :data="list" border stripe>
      <el-table-column prop="takeNo" label="盘点单号" width="240" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="['warning','primary','success'][row.status]">{{ ['待盘点','已盘点','已调账'][row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operator" label="操作人" width="120" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">明细</el-button>
          <el-button size="small" type="warning" v-if="row.status === 0" @click="openSubmit(row)">录入实盘</el-button>
          <el-button size="small" type="success" v-if="row.status === 1" @click="apply(row)">调账</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 明细抽屉 -->
    <el-drawer v-model="detailVisible" title="盘点明细" size="50%">
      <el-table :data="detail.items" border size="small">
        <el-table-column prop="productName" label="商品" />
        <el-table-column prop="systemQty" label="系统库存" width="100" />
        <el-table-column prop="actualQty" label="实盘数量" width="100" />
        <el-table-column label="差异" width="100">
          <template #default="{ row }">
            <span v-if="row.diffQty == null">-</span>
            <el-tag v-else :type="row.diffQty === 0 ? 'info' : row.diffQty > 0 ? 'success' : 'danger'">
              {{ row.diffQty > 0 ? '+' : '' }}{{ row.diffQty }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>

    <!-- 录入实盘对话框 -->
    <el-dialog v-model="submitVisible" title="录入实盘数量" width="600px">
      <el-table :data="submitItems" border size="small">
        <el-table-column prop="productName" label="商品" />
        <el-table-column prop="systemQty" label="系统库存" width="100" />
        <el-table-column label="实盘数量" width="160">
          <template #default="{ row }">
            <el-input-number v-model="row.actualQty" :min="0" size="small" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="submitVisible = false">取消</el-button>
        <el-button type="primary" @click="doSubmit">提交盘点结果</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTakePage, getTakeDetail, createTake, submitTake, applyTake } from '../api'

const list = ref([])
const detailVisible = ref(false)
const submitVisible = ref(false)
const detail = ref({ items: [] })
const submitItems = ref([])
let currentTakeId = null

async function load() {
  list.value = (await getTakePage({ pageNum: 1, pageSize: 20 })).data.list
}

async function create() {
  const { value } = await ElMessageBox.prompt('请输入盘点备注', '发起盘点')
  await createTake(value || '例行盘点')
  ElMessage.success('盘点单已创建，系统库存已生成快照')
  load()
}

async function openDetail(row) {
  detail.value = (await getTakeDetail(row.id)).data
  detailVisible.value = true
}

async function openSubmit(row) {
  const d = (await getTakeDetail(row.id)).data
  currentTakeId = row.id
  submitItems.value = d.items.map(i => ({ ...i, actualQty: i.systemQty }))
  submitVisible.value = true
}

async function doSubmit() {
  await submitTake(currentTakeId, {
    items: submitItems.value.map(i => ({ productId: i.productId, actualQty: i.actualQty }))
  })
  ElMessage.success('盘点提交成功')
  submitVisible.value = false
  load()
}

async function apply(row) {
  await ElMessageBox.confirm('调账将按盘点差异修正库存，确定执行？', '提示', { type: 'warning' })
  await applyTake(row.id)
  ElMessage.success('调账完成')
  load()
}

onMounted(load)
</script>