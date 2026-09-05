<template>
  <el-card>
    <el-space style="margin-bottom:16px">
      <el-button type="success" @click="openDialog(1)">新建入库单</el-button>
      <el-button type="warning" @click="openDialog(2)">新建出库单</el-button>
      <el-select v-model="typeFilter" clearable placeholder="按类型筛选" style="width:140px" @change="load">
        <el-option label="入库" :value="1" />
        <el-option label="出库" :value="2" />
      </el-select>
    </el-space>

    <el-table :data="list" border stripe>
      <el-table-column prop="orderNo" label="单号" width="240" />
      <el-table-column label="类型" width="90">
        <template #default="{ row }">
          <el-tag :type="row.type === 1 ? 'success' : 'warning'">{{ row.type === 1 ? '入库' : '出库' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operator" label="操作人" width="120" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="createdAt" label="创建时间" width="190" />
    </el-table>

    <el-pagination style="margin-top:16px" background layout="total, prev, pager, next"
                   :total="total" :page-size="pageSize" :current-page="pageNum"
                   @current-change="p => { pageNum = p; load() }" />

    <el-dialog v-model="dialogVisible" :title="form.type === 1 ? '新建入库单' : '新建出库单'" width="420px">
      <el-form label-width="80px">
        <el-form-item label="商品ID"><el-input-number v-model="form.productId" :min="1" /></el-form-item>
        <el-form-item label="数量"><el-input-number v-model="form.quantity" :min="1" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderPage, inbound, outbound } from '../api'

const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = 10
const typeFilter = ref(undefined)
const dialogVisible = ref(false)
const form = reactive({ type: 1, productId: 1, quantity: 1, remark: '' })

async function load() {
  const res = await getOrderPage({ pageNum: pageNum.value, pageSize, type: typeFilter.value })
  list.value = res.data.list
  total.value = res.data.total
}

function openDialog(type) {
  form.type = type
  dialogVisible.value = true
}

async function submit() {
  const body = { remark: form.remark, items: [{ productId: form.productId, quantity: form.quantity }] }
  form.type === 1 ? await inbound(body) : await outbound(body)
  ElMessage.success('提交成功')
  dialogVisible.value = false
  load()
}

onMounted(load)
</script>