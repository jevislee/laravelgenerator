<?php
namespace App\Http\Controllers\Backend;

use Validator;
use Exception;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\Http\Controllers\Controller;
use App\Models\XXX;

class XXXController extends Controller
{
    //nullable可以改为required
    //integer类型的type和status可以在末尾追加|in:0,1,2进一步限制数值范围
    private $rules = [
       @@@fillrule
    ];

    public function add(Request $request)
    {
        //json_decode()第二个参数为TRUE时将返回数组，FALSE时返回对象
        $data = json_decode($request->getContent(), true);

        $validator = Validator::make($data, $this->rules);

        if ($validator->fails()) {
            return $this->validateError($validator->errors());
        }

        $vo = new XXX($data);
        if ($vo->save()) {
            $ret = new XXX;
            $ret->id = $vo->id;
            return $this->successWithData($ret);
        } else {
            return $this->errorWithInfo('XXX save error');
        }
    }

    public function update(Request $request, $id)
    {
        $data = json_decode($request->getContent(), true);

        $validator = Validator::make($data, $this->rules);

        if ($validator->fails()) {
            return $this->validateError($validator->errors());
        }

        $vo = XXX::where('id', '=', $id)->update($data);
        if($vo > 0) {
            return $this->success();
        } else {
            return $this->errorWithInfo('XXX update error');
        }
    }

    public function batchUpdate(Request $request)
    {
        $data = json_decode($request->getContent(), true);

        $ids = $data['ids'];
        $size = count($ids);

        DB::beginTransaction();
        try{
            for($i = 0; $i < $size; $i++){
                XXX::where('id', '=', $ids[$i])->update(['字段' => $data['字段']]);
            }
            DB::commit();
            return $this->success();
        } catch (Exception $e) {
            DB::rollBack();
            return $this->errorWithInfo($e->getMessage());
        }
    }

    public function delete(Request $request, $id)
    {
        $vo = XXX::destroy($id);
        if($vo > 0) {
            return $this->success();
        } else {
            return $this->errorWithInfo('XXX delete error');
        }
    }

    public function queryById(Request $request, $id)
    {
        //同$vo = XXX::where('id', '=', $id)->get();
        $vo = XXX::find($id);
        return $this->successWithData($vo);
    }

    //page参数指定当前页,laravel可以自动获取json里和url参数里的page字段值
    public function queryList(Request $request)
    {
        //获取url参数
        $data = $request->only(['page_size',@@@fillable]);

        $validator = Validator::make($data, $this->rules);

        if ($validator->fails()) {
            return $this->validateError($validator->errors());
        }

        //select的字段可以通过as重命名来对应接口文档里的字段名
        //paginate返回的对象可以取出数据重新组织来对应接口文档里的字段名
        //不需要分页时把paginate()改为get()即可
        //多表关联查询可以套用DB::select('select * from users where gender = ? and city = ?', ['male', 'bj']);
        $vo = XXX::
        select(['id',@@@fillable,'created_at', 'updated_at'])->
        @@@fillwhere
        orderBy('id', 'desc')->
        paginate(isset($data['page_size']) && is_numeric($data['page_size']) && intval($data['page_size']) > 0 && intval($data['page_size']) < 100 ? intval($data['page_size']) : 10);
        return $this->successWithPage($vo);
    }
}