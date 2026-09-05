<template>
  <el-card>
    <el-alert title="以下商品库存已低于预警阈值，请及时补货" type="warning" :closable="false" style="margin-bottom:16px" />
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="stock" label="当前库存" width="120" />
      <el-table-column prop="warnThreshold" label="预警阈值" width="120" />
      <el-table-column label="缺口" width="120">
        <template #default="{ row }">
          <el-tag type="danger">{{ row.warnThreshold - row.stock }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLowStock } from '../api'

const list = ref([])
onMounted(async () => { list.value = (await getLowStock()).data })
</script>