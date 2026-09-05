import request from '../utils/request'

export const login = (username, password) =>
    request.post('/api/auth/login', `username=${username}&password=${password}`,
        { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } })

export const getProductPage = params => request.get('/api/products/page', { params })
export const addProduct = data => request.post('/api/products', data)
export const updateProduct = (id, data) => request.put(`/api/products/${id}`, data)
export const deleteProduct = id => request.delete(`/api/products/${id}`)
export const getLowStock = () => request.get('/api/products/low-stock')

export const getOrderPage = params => request.get('/api/stock-orders/page', { params })
export const inbound = data => request.post('/api/stock-orders/inbound', data)
export const outbound = data => request.post('/api/stock-orders/outbound', data)

export const getTakePage = params => request.get('/api/stock-takes/page', { params })
export const getTakeDetail = id => request.get(`/api/stock-takes/${id}`)
export const createTake = remark => request.post('/api/stock-takes', null, { params: { remark } })
export const submitTake = (id, data) => request.put(`/api/stock-takes/${id}/submit`, data)
export const applyTake = id => request.post(`/api/stock-takes/${id}/apply`)