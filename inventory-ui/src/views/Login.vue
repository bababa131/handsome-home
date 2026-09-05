<template>
  <div style="height:100vh;display:flex;align-items:center;justify-content:center;background:#f0f2f5">
    <el-card style="width:360px">
      <h2 style="text-align:center;margin-top:0">库存管理系统</h2>
      <el-form @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: 'admin', password: '123456' })

async function handleLogin() {
  loading.value = true
  try {
    const res = await login(form.username, form.password)
    localStorage.setItem('token', res.data.token)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>