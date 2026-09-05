import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    { path: '/login', component: () => import('../views/Login.vue') },
    {
        path: '/',
        component: () => import('../layout/MainLayout.vue'),
        redirect: '/products',
        children: [
            { path: 'products', component: () => import('../views/ProductList.vue') },
            { path: 'low-stock', component: () => import('../views/LowStock.vue') },
            { path: 'orders', component: () => import('../views/StockOrder.vue') },
            { path: 'takes', component: () => import('../views/StockTake.vue') }
        ]
    }
]

const router = createRouter({ history: createWebHistory(), routes })

// 路由守卫：没登录只能看登录页
router.beforeEach((to) => {
    if (to.path !== '/login' && !localStorage.getItem('token')) {
        return '/login'
    }
})

export default router