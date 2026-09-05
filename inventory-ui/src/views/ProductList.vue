<template>
  <el-card>
    <el-form inline>
      <el-form-item label="分类ID">
        <el-input v-model="query.categoryId" clearable style="width:120px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable style="width:120px">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="success" @click="openDialog()">新增商品</el-button>
    </el-form>

    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="categoryId" label="分类" width="80" />
      <el-table-column prop="price" label="单价" width="100" />
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination style="margin-top:16px" background layout="total, prev, pager, next"
                   :total="total" :page-size="query.pageSize" :current-page="query.pageNum"
                   @current-change="p => { query.pageNum = p; load() }" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商品' : '新增商品'" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类ID"><el-input-number v-model="form.categoryId" :min="1" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存" v-if="!form.id"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="预警阈值"><el-input-number v-model="form.warnThreshold" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductPage, addProduct, updateProduct, deleteProduct } from '../api'

const list = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const query = reactive({ pageNum: 1, pageSize: 10, categoryId: undefined, status: undefined })
const form = ref({})

async function load() {
  const res = await getProductPage(query)
  list.value = res.data.list
  total.value = res.data.total
}

function openDialog(row) {
  form.value = row ? { ...row } : { status: 1, warnThreshold: 10, stock: 0, price: 0, categoryId: 1 }
  dialogVisible.value = true
}

async function save() {
  form.value.id
      ? await updateProduct(form.value.id, form.value)
      : await addProduct(form.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除「${row.name}」？`, '提示', { type: 'warning' })
  await deleteProduct(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>