<?php

namespace App\Utils;

trait Tree
{
    static public function findChild(&$data, $pid = 0, $col_pid = 'parent_id')
    {
        $rootList = array();
        foreach ($data as $key => $val) {
            if ($val[$col_pid] == $pid) {
                $rootList[] = $val;
                unset($data[$key]);
            }
        }
        return $rootList;
    }

    /**
     * 无限分级
     * @access  public
     * @param   array     &$data 数据库里取得的结果集 地址引用
     * @param   integer $pid 父级id的值
     * @param   string $col_id 自增id字段名（对应&$data里的字段名）
     * @param   string $col_pid 父级字段名（对应&$data里的字段名）
     * @param   string $level 当前处于第几层）
     * @param   boolean $need_level 是否需要构建level字段
     * @return  array     children     返回整理好的数组
     */
    static public function getTree(&$data, $pid = 0, $col_id = 'id', $col_pid = 'parent_id', $level = 1,$need_level=false)
    {
        $childs = self::findChild($data, $pid, $col_pid);
        if (empty($childs)) {
            return null;
        }
        foreach ($childs as $key => $val) {
            if ($need_level){
                $childs[$key]['level'] = $level;
            }

            $treeList = self::getTree($data, $val[$col_id], $col_id, $col_pid, $level+1);
            if ($treeList !== null) {

                $childs[$key]['children'] = $treeList;
            }
        }
        return $childs;
    }
}
