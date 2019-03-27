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
                AdminUser::where('id', '=', $ids[$i])->update(['字段' => $data['字段']]);
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
        $vo = XXX::find($id);
        return $this->successWithData($vo);
    }

    //page参数指定当前页,laravel可以自动获取json里和url参数里的page字段值
    public function queryList(Request $request)
    {
        $data = $request->only(['page_size',@@@fillable]);

        $validator = Validator::make($data, $this->rules);

        if ($validator->fails()) {
            return $this->validateError($validator->errors());
        }

        //select的字段可以通过as重命名来对应接口文档里的字段名,paginate返回的对象可以取出数据重新组织来对应接口文档里的字段名
        $vo = XXX::
        select(['id',@@@fillable,'created_at', 'updated_at'])->
        @@@fillwhere
        orderBy('id', 'desc')->
        paginate(isset($data['page_size']) && is_numeric($data['page_size']) && intval($data['page_size']) > 0 && intval($data['page_size']) < 100 ? $data['page_size'] : 10);
        return $this->successWithPage($vo);
    }
}