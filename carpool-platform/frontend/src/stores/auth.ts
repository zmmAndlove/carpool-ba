import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

interface User {
  id: number
  username: string
  phone?: string
  realName: string
  avatar: string
  rating: number
  tripCount: number
  completedTrips: number
}

interface LoginData {
  username: string
  password: string
}

interface RegisterData extends LoginData {
  phone?: string
  realName?: string
  avatar?: string
  gender?: string
  age?: number
  hometownProvince?: string
  hometownCity?: string
  currentProvince?: string
  currentCity?: string
  university?: string
  interests?: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(null)
  const isAuthenticated = ref(!!token.value)

  const setAuth = (newToken: string, userData: User) => {
    token.value = newToken
    user.value = userData
    isAuthenticated.value = true
    localStorage.setItem('token', newToken)
  }

  const clearAuth = () => {
    token.value = null
    user.value = null
    isAuthenticated.value = false
    localStorage.removeItem('token')
  }

  const login = async (data: LoginData) => {
    try {
      const response = await request.post('/auth/login', data)
      const { token: newToken, user: userData } = response.data
      setAuth(newToken, userData)
      return response.data
    } catch (error) {
      clearAuth()
      throw error
    }
  }

  const register = async (data: RegisterData) => {
    try {
      const response = await request.post('/auth/register', data)
      const { token: newToken, user: userData } = response.data
      setAuth(newToken, userData)
      return response.data
    } catch (error) {
      clearAuth()
      throw error
    }
  }

  const logout = () => {
    clearAuth()
  }

  const fetchCurrentUser = async () => {
    if (!token.value) return null
    
    try {
      const response = await request.get('/auth/me')
      user.value = response.data.user
      return user.value
    } catch (error) {
      clearAuth()
      throw error
    }
  }

  const updateProfile = async (data: Partial<User>) => {
    try {
      const response = await request.put('/users/profile', data)
      user.value = { ...user.value, ...response.data.user }
      return response.data
    } catch (error) {
      throw error
    }
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    register,
    logout,
    fetchCurrentUser,
    updateProfile,
    clearAuth
  }
})