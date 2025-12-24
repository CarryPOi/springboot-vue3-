import request from '@/utils/request'

// 获取评论列表
export const getCommentList = () => {
  return request.get('/api/comments/list')
}

// 发布评论
export const addComment = (data) => {
  return request.post('/api/comments/add', data)
}

// 删除评论
export const deleteComment = (id, userId) => {
  return request.delete(`/api/comments/${id}`, {
    params: { userId }
  })
}

// 点赞评论
export const likeComment = (id) => {
  return request.post(`/api/comments/${id}/like`)
}
