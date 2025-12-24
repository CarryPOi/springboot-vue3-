import request from '@/utils/request'

// 获取饮食推荐列表
export const getDietRecommendationList = () => {
  return request.get('/api/diet-recommendations/list')
}

// 发布饮食推荐
export const addDietRecommendation = (data) => {
  return request.post('/api/diet-recommendations/add', data)
}

// 更新饮食推荐
export const updateDietRecommendation = (data) => {
  return request.put('/api/diet-recommendations/update', data)
}

// 删除饮食推荐
export const deleteDietRecommendation = (id) => {
  return request.delete(`/api/diet-recommendations/${id}`)
}
