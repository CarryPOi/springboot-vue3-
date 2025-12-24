import request from '@/utils/request'

// 用户注册接口
export function userRegister(data) {
  return request({
    url: '/api/user/register',
    method: 'post',
    data
  })
}

// 用户登录接口
export function userLogin(params) {
  return request({
    url: '/api/user/login',
    method: 'post',
    params
  })
}