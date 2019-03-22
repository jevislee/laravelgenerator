<?php
namespace App\Models;
use Illuminate\Database\Eloquent\SoftDeletes;

class XXX extends Model {
    //与模型关联的数据表
    protected $table = '@@@table';

    //Eloquent会假定每一个表都会有 id 作为主键,你可以定义一个受保护的 $primaryKey 属性来重写此约定
    protected $primaryKey = 'id';

    //可以被赋值的字段
    protected $fillable = [@@@fillable];

    //默认情况下, Eloquent 会假定你的表中存在 created_at 和 updated_at 字段. 如果你不想让 Eloquent 自动管理这俩个列, 可以在你的模型中将 $timestamps 属性设置为 false
    public $timestamps = true;
    
    //解注释后删除操作使用软删除
    use SoftDeletes;
    protected $dates = ['deleted_at'];
}