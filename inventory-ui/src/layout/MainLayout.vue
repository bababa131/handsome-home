<template>
  <el-container style="height:100vh">
    <el-aside width="220px" style="background:#001529">
      <div style="color:#fff;text-align:center;line-height:60px;font-size:16px">库存管理系统</div>
      <el-menu :default-active="$route.path" router background-color="#001529"
               text-color="#aaa" active-text-color="#fff">
        <el-menu-item index="/products">商品管理</el-menu-item>
        <el-menu-item index="/low-stock">低库存预警</el-menu-item>
        <el-menu-item index="/orders">出入库单</el-menu-item>
        <el-menu-item index="/takes">库存盘点</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex;align-items:center;justify-content:flex-end;border-bottom:1px solid #eee">
        <el-button text type="danger" @click="logout">退出登录</el-button>
      </el-header>
      <el-main style="background:#f5f5f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function logout() {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>