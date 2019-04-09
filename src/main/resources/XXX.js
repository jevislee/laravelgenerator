import request from '@/utils/request'

//新增
export function add(data) {
  return request({
    url: '/@@@table',
    method: 'post',
    data
  })
}

//编辑
export function update(id, data) {
  return request({
    url: `/@@@table/${id}`,
    method: 'put',
    data
  })
}

//批量处理
export function batchUpdate(ids, status) {
    return request({
        url: '/@@@tables',
        method: 'put',
        data: {
            ids,
            status
        }
    })
}

//删除
export function delete(id) {
    return request({
        url: `/@@@table/${id}`,
        method: 'delete'
    })
}

//获取详情
export function queryById(id) {
  return request({
    url: `/@@@table/${id}`,
    method: 'get'
  })
}

//获取列表
export function queryList(query) {
    return request({
        url: '/@@@table',
        method: 'get',
        params: query
    })
}
