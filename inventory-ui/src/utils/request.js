import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
    baseURL: '',
    timeout: 10000
})

// 请求拦截：自动携带 Token
request.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) config.headers.Authorization = 'Bearer ' + token
    return config
})

// 响应拦截：统一处理业务码
request.interceptors.response.use(
    res => {
        const r = res.data
        if (r.code !== 200) {
            if (r.code === 401) {
                localStorage.removeItem('token')
                router.push('/login')
            }
            ElMessage.error(r.message || '请求失败')
            return Promise.reject(r)
        }
        return r
    },
    err => {
        ElMessage.error(err.message || '网络异常')
        return Promise.reject(err)
    }
)
export default request