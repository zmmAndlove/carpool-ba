import axios from 'axios'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求缓存
const cache: Record<string, { data: any; timestamp: number }> = {}
const CACHE_DURATION = 5 * 60 * 1000 // 5分钟缓存

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 检查是否需要缓存
    if (config.method === 'get') {
      const cacheKey = config.url + JSON.stringify(config.params)
      const cachedItem = cache[cacheKey]

      if (cachedItem && Date.now() - cachedItem.timestamp < CACHE_DURATION) {
        // 如果有缓存且未过期，抛出特殊错误以便在响应拦截器中处理
        return Promise.reject({ __cached: true, data: cachedItem.data })
      }
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 缓存GET请求的响应
    if (response.config.method === 'get') {
      const cacheKey = response.config.url + JSON.stringify(response.config.params)
      cache[cacheKey] = {
        data: response.data,
        timestamp: Date.now()
      }
    }

    return response
  },
  (error) => {
    // 处理缓存数据
    if (error.__cached) {
      return Promise.resolve({ data: error.data })
    }

    // 处理错误
    if (error.response) {
      // 401错误处理
      if (error.response.status === 401) {
        // 清除本地存储的token
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        // 跳转到登录页
        window.location.href = '/login'
      }

      // 其他错误处理
      return Promise.reject(error.response.data)
    }

    // 网络错误
    return Promise.reject({ error: '网络错误，请检查网络连接' })
  }
)

// 导出request实例
export default request
