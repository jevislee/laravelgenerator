<?php
namespace App\Http\Controllers;

use Validator;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use App\Models\XXX;

class XXXController extends Controller
{
    public function add(Request $request)
    {
        //json_decode()第二个参数为TRUE时将返回数组，FALSE时返回对象
        $data = json_decode($request->getContent(), true);

        $validator = Validator::make($data, [
           @@@fillrule
        ]);

        if ($validator->fails()) {
            return $this->validateError($validator->errors());
        }

        $vo = new XXX($data);
        if ($vo->save()) {
            $ret = new XXX;
            $ret->id = $vo->id;
            return $this->successWithData($ret);
        } else {
            return $this->error();
        }
    }

    public function update(Request $request, $id)
    {
        $data = json_decode($request->getContent(), true);

        $validator = Validator::make($data, [
           @@@fillrule
        ]);

        if ($validator->fails()) {
            return $this->validateError($validator->errors());
        }

        $vo = XXX::where('id', '=', $id)->update($data);
        if($vo > 0) {
            return $this->success();
        } else {
            return $this->error();
        }
    }

    public function delete(Request $request, $id)
    {
        $vo = XXX::destroy($id);
        if($vo > 0) {
            return $this->success();
        } else {
            return $this->error();
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

        $validator = Validator::make($data, [
           @@@fillrule
        ]);

        if ($validator->fails()) {
            return $this->validateError($validator->errors());
        }

        $vo = XXX::
        @@@fillwhere
        paginate(isset($data['page_size']) && is_numeric($data['page_size']) && intval($data['page_size']) > 0 && intval($data['page_size']) < 100 ? $data['page_size'] : 10);
        return $this->successWithData($vo);
    }
}